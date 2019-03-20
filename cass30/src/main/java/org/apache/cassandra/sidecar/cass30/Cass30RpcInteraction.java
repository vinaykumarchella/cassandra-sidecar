/**
 * Copyright (c) 2019 Netflix, Inc.  All rights reserved.
 */

package org.apache.cassandra.sidecar.cass30;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.google.inject.Inject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.cassandra.db.compaction.CompactionManager;
import org.apache.cassandra.db.compaction.CompactionManagerMBean;
import org.apache.cassandra.gms.FailureDetector;
import org.apache.cassandra.gms.FailureDetectorMBean;
import org.apache.cassandra.service.StorageServiceMBean;
import org.apache.cassandra.sidecar.common.CassRpcInteraction;
import org.apache.cassandra.sidecar.common.Configuration;

public class Cass30RpcInteraction implements CassRpcInteraction
{
    protected static final Logger logger = LoggerFactory.getLogger(Cass30RpcInteraction.class);
    protected final Configuration config;
    // Mbean Management
    protected final String JMX_URL = "service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi";
    protected MBeanServerConnection mbeanServer;
    protected CompactionManagerMBean cmProxy;
    protected FailureDetectorMBean fdProxy;
    protected StorageServiceMBean ssProxy;
    protected JMXConnector jmxConnector = null;
    protected ObjectName ssMbeanName;
    protected JMXServiceURL jmxUrl;
    protected volatile Timer timer = new Timer();

    // Caches for very frequently called metadata
    protected boolean isConnected = false;
    protected boolean reconnectionScheduled = false;
    protected long lastConnectionLookup = 0;
    protected String cachedConnectionId = null;
    protected String cachedHostId = null;

    /**
     * Constructor that does absolutely no connecting or talking to Cassandra at all. Any interaction must be
     * deferred to the first usage of the API methods themselves. This is important for testing and reliability.
     * Do not change this behavior.
     *
     * @param config The configuration to use when setting up connections.
     */
    @Inject
    public Cass30RpcInteraction(Configuration config)
    {
        this.config = config;
    }

    /**
     * Check the state of expression and throw exception as needed
     *
     * @param expression   Expression to check for
     * @param errorMessage Error message to be used in throwing exception
     */
    public static void checkState(boolean expression, Object errorMessage)
    {
        if (!expression)
        {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }

    @Override
    public String getLocalHostId()
    {
        checkState(tryGetConnection(), "JMXConnection is broken or not able to connect");
        // This is a really frequently called method and it doesn't change
        if (cachedHostId == null || System.currentTimeMillis() > (lastConnectionLookup + config.getJmxCacheTtl()))
            cachedHostId = ssProxy.getLocalHostId();
        return cachedHostId;
    }

    @Override
    public String getLocalLoadString()
    {
        return null;
    }

    @Override
    public String getClusterName()
    {
        return null;
    }

    @Override
    public void triggerCompaction(String keyspace, String... tables)
    {

    }

    @Override
    public void triggerCleanup(int jobs, String keyspace, String... tables)
    {

    }

    @Override
    public void triggerFlush(String keyspace, String... tables)
    {

    }

    /**
     * JMX Connection management
     */

    public Optional<String> getJMXConnectionId(boolean useCache)
    {
        // We call this method a _lot_ to check the health of the connection, and if we don't have
        // some sort of cache the caller of it can't make the JMX calls fast enough
        if (jmxConnector != null && (!useCache ||
                                     (System.currentTimeMillis() > (lastConnectionLookup + config.getJmxCacheTtl()))))
        {
            try
            {
                cachedConnectionId = jmxConnector.getConnectionId();
                lastConnectionLookup = System.currentTimeMillis();
            }
            catch (IOException | NullPointerException ignored)
            {
                logger.error("Error connecting", ignored);
            }
        }
        String connectionId = cachedConnectionId;
        if (connectionId != null)
            return Optional.of(cachedConnectionId);
        return Optional.empty();
    }

    @Override
    public boolean isConnectionAlive(boolean useCache)
    {
        return getJMXConnectionId(useCache).filter(s -> s.length() > 0).isPresent();
    }

    @Override
    public void connectAsync()
    {
        scheduleReconnector();
    }

    @Override
    public boolean connectSync()
    {
        try
        {
            doConnect();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
        finally
        {
            connectAsync();
        }
    }

    // Synchronized because of the timer manipulation
    protected synchronized void scheduleReconnector()
    {
        if (reconnectionScheduled)
            return;

        int delay = 100;   // delay for 100ms before trying to connect
        int period = config.getJmxConnectionMonitorPeriodInMs();  // repeat every 60 sec.
        timer.cancel();
        timer.purge();
        timer = new Timer();
        logger.info("Scheduling JMX connection monitor with initial delay of {} ms and interval of {} ms.", delay, period);
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                tryGetConnection(false);
            }
        }, delay, period);
        reconnectionScheduled = true;
    }

    protected boolean tryGetConnection(boolean useCache)
    {
        boolean connectionAlive = isConnectionAlive(useCache);

        connectionAlive = connectionAlive || connectSync();
        return isConnected && connectionAlive;
        //return connectionAlive;
    }

    protected boolean tryGetConnection()
    {
        return tryGetConnection(true);
    }

    // Synchronized because mixing beans is a bad plan
    protected synchronized void doConnect()
    {
        ObjectName cmMbeanName, fdMbeanName;
        try
        {
            isConnected = false;
            jmxUrl = new JMXServiceURL(String.format(JMX_URL, config.getCassandraJmxAddress(), config.getCassandraJmxPort()));
            ssMbeanName = new ObjectName("org.apache.cassandra.db:type=StorageService");
            cmMbeanName = new ObjectName(CompactionManager.MBEAN_OBJECT_NAME);
            fdMbeanName = new ObjectName(FailureDetector.MBEAN_NAME);
        }
        catch (MalformedURLException | MalformedObjectNameException e)
        {
            logger.error(String.format("Failed to prepare JMX connection to %s", jmxUrl), e);
            return;
        }
        try
        {
            jmxConnector = JMXConnectorFactory.connect(jmxUrl);
            mbeanServer = jmxConnector.getMBeanServerConnection();
            ssProxy = JMX.newMBeanProxy(mbeanServer, ssMbeanName, StorageServiceMBean.class);
            cmProxy = JMX.newMBeanProxy(mbeanServer, cmMbeanName, CompactionManagerMBean.class);
            fdProxy = JMX.newMBeanProxy(mbeanServer, fdMbeanName, FailureDetectorMBean.class);

            isConnected = true;
            logger.info(String.format("JMX connection to %s properly connected", jmxUrl));
        }
        catch (Exception e)
        {
            String msg = String.format("Failed to establish JMX connection to %s", jmxUrl);
            logger.error(msg, e);
        }
    }

    /**
     * Cleanly shut down by un-registering the listeners and closing the JMX connection.
     */
    public void close()
    {
        logger.debug(String.format("Closing JMX connection to %s", jmxUrl));
        try
        {
            jmxConnector.close();
        }
        catch (IOException e)
        {
            logger.warn("Failed to close JMX connection.", e);
        }
    }
}
