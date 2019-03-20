/**
 * Copyright (c) 2019 Netflix, Inc.  All rights reserved.
 */
package org.apache.cassandra.sidecar.mocks;

import org.apache.cassandra.sidecar.common.CassRpcInteraction;

/**
 * @author vchella
 */
public class MockCassRpcInteraction implements CassRpcInteraction
{
    public String getLocalHostId()
    {
        return "MOCK_LOCAL_HOST_ID";
    }

    public String getLocalLoadString()
    {
        return null;
    }

    public String getClusterName()
    {
        return null;
    }

    public void triggerCompaction(String keyspace, String... tables)
    {

    }

    public void triggerCleanup(int jobs, String keyspace, String... tables)
    {

    }

    public boolean isConnectionAlive(boolean useCache)
    {
        return false;
    }

    public void connectAsync()
    {

    }

    public boolean connectSync()
    {
        return false;
    }

    public void triggerFlush(String keyspace, String... tables)
    {

    }
}
