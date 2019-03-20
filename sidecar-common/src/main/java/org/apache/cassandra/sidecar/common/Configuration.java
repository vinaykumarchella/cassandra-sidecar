/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.cassandra.sidecar.common;

/**
 * Sidecar configuration
 */
public class Configuration
{
    /* Cassandra Host */
    private final String cassandraHost;

    /* Cassandra Port */
    private final Integer cassandraPort;

    /* Sidecar's HTTP REST API port */
    private final Integer port;
    /* Healthcheck frequency in miilis */
    private final Integer healthCheckFrequencyMillis;
    /* SSL related settings */
    private final String keyStorePath;
    private final String keyStorePassword;
    private final String trustStorePath;
    private final String trustStorePassword;
    private final boolean isSslEnabled;
    /* Sidecar's listen address */
    private String host;
    private String cassandraJmxAddress = "127.0.0.1";
    private int cassandraJmxPort = 7100;
    private int jmxConnectionMonitorPeriodInMs = 60000;
    private int jmxCacheTtl = 10000;
    private String jmxUsername = null;
    private String jmxPassword = null;


    public Configuration(String cassandraHost, Integer cassandraPort, String host, Integer port,
                         Integer healthCheckFrequencyMillis, boolean isSslEnabled,
                         String keyStorePath, String keyStorePassword, String trustStorePath,
                         String trustStorePassword, String cassandraJmxAddress, int cassandraJmxPort,
                         int jmxConnectionMonitorPeriodInMs, int jmxCacheTtl, String jmxUsername,
                         String jmxPassword)
    {
        this.cassandraHost = cassandraHost;
        this.cassandraPort = cassandraPort;
        this.host = host;
        this.port = port;
        this.healthCheckFrequencyMillis = healthCheckFrequencyMillis;

        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
        this.trustStorePath = trustStorePath;
        this.trustStorePassword = trustStorePassword;
        this.isSslEnabled = isSslEnabled;

        this.cassandraJmxAddress = cassandraJmxAddress;
        this.cassandraJmxPort = cassandraJmxPort;
        this.jmxConnectionMonitorPeriodInMs = jmxConnectionMonitorPeriodInMs;
        this.jmxCacheTtl = jmxCacheTtl;
        this.jmxUsername = jmxUsername;
        this.jmxPassword = jmxPassword;
    }

    /**
     * Get the Cassandra host
     *
     * @return
     */
    public String getCassandraHost()
    {
        return cassandraHost;
    }

    /**
     * Get the Cassandra port
     *
     * @return
     */
    public Integer getCassandraPort()
    {
        return cassandraPort;
    }

    /**
     * Sidecar's listen address
     *
     * @return
     */
    public String getHost()
    {
        return host;
    }

    /**
     * Get the Sidecar's REST HTTP API port
     *
     * @return
     */
    public Integer getPort()
    {
        return port;
    }

    /**
     * Get the health check frequency in millis
     *
     * @return
     */
    public Integer getHealthCheckFrequencyMillis()
    {
        return healthCheckFrequencyMillis;
    }

    /**
     * Get the SSL status
     *
     * @return
     */
    public boolean isSslEnabled()
    {
        return isSslEnabled;
    }

    /**
     * Get the Keystore Path
     *
     * @return
     */
    public String getKeyStorePath()
    {
        return keyStorePath;
    }

    /**
     * Get the Keystore password
     *
     * @return
     */
    public String getKeystorePassword()
    {
        return keyStorePassword;
    }

    /**
     * Get the Truststore Path
     *
     * @return
     */
    public String getTrustStorePath()
    {
        return trustStorePath;
    }

    /**
     * Get the Truststore password
     *
     * @return
     */
    public String getTruststorePassword()
    {
        return trustStorePassword;
    }

    public String getCassandraJmxAddress()
    {
        return cassandraJmxAddress;
    }

    public int getCassandraJmxPort()
    {
        return cassandraJmxPort;
    }

    public int getJmxConnectionMonitorPeriodInMs()
    {
        return jmxConnectionMonitorPeriodInMs;
    }

    public int getJmxCacheTtl()
    {
        return jmxCacheTtl;
    }

    public String getJmxUsername()
    {
        return jmxUsername;
    }

    public String getJmxPassword()
    {
        return jmxPassword;
    }


    /**
     * Configuration Builder
     */
    public static class Builder
    {
        private String cassandraHost;
        private Integer cassandraPort;
        private String host;
        private Integer port;
        private Integer healthCheckFrequencyMillis;
        private String keyStorePath;
        private String keyStorePassword;
        private String trustStorePath;
        private String trustStorePassword;
        private boolean isSslEnabled;
        private String cassandraJmxAddress;
        private int cassandraJmxPort;
        private int jmxConnectionMonitorPeriodInMs = 60000;
        private int jmxCacheTtl = 10000;
        private String jmxUsername = null;
        private String jmxPassword = null;

        public Builder setCassandraHost(String host)
        {
            this.cassandraHost = host;
            return this;
        }

        public Builder setCassandraPort(Integer port)
        {
            this.cassandraPort = port;
            return this;
        }

        public Builder setHost(String host)
        {
            this.host = host;
            return this;
        }

        public Builder setPort(Integer port)
        {
            this.port = port;
            return this;
        }

        public Builder setHealthCheckFrequency(Integer freqMillis)
        {
            this.healthCheckFrequencyMillis = freqMillis;
            return this;
        }

        public Builder setKeyStorePath(String path)
        {
            this.keyStorePath = path;
            return this;
        }

        public Builder setKeyStorePassword(String password)
        {
            this.keyStorePassword = password;
            return this;
        }

        public Builder setTrustStorePath(String path)
        {
            this.trustStorePath = path;
            return this;
        }

        public Builder setTrustStorePassword(String password)
        {
            this.trustStorePassword = password;
            return this;
        }

        public Builder setSslEnabled(boolean enabled)
        {
            this.isSslEnabled = enabled;
            return this;
        }

        public Builder setCassandraJmxAddress(String cassandraJmxAddress)
        {
            this.cassandraJmxAddress = cassandraJmxAddress;
            return this;
        }

        public Builder setCassandraJmxPort(int cassandraJmxPort)
        {
            this.cassandraJmxPort = cassandraJmxPort;
            return this;
        }

        public Builder setJmxConnectionMonitorPeriodInMs(int jmxConnectionMonitorPeriodInMs)
        {
            this.jmxConnectionMonitorPeriodInMs = jmxConnectionMonitorPeriodInMs;
            return this;
        }

        public Builder setJmxCacheTtl(int jmxCacheTtl)
        {
            this.jmxCacheTtl = jmxCacheTtl;
            return this;
        }

        public Builder setJmxUsername(String jmxUsername)
        {
            this.jmxUsername = jmxUsername;
            return this;
        }

        public Builder setJmxPassword(String jmxPassword)
        {
            this.jmxPassword = jmxPassword;
            return this;
        }

        public Configuration build()
        {
            return new Configuration(cassandraHost, cassandraPort, host, port, healthCheckFrequencyMillis, isSslEnabled,
                                     keyStorePath, keyStorePassword, trustStorePath, trustStorePassword, cassandraJmxAddress,
                                     cassandraJmxPort, jmxConnectionMonitorPeriodInMs, jmxCacheTtl, jmxUsername, jmxPassword);
        }
    }
}
