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
 * The layer of abstraction on top of Cassandra's RPC APIs. As the RPC(JMX) APIs change frequently
 * between versions this interface serves to allow us to plug in support for different versions without significant
 * coupling to those RPC(JMX/management) APIs.
 */
public interface CassRpcInteraction
{
    /**
     * Returns the local Cassandra node's Host ID (e.g. 43262282-a8a9-486a-9b90-1f83e7605129). This is used to
     * uniquely identify this host.
     *
     * @return A String representation of the local Cassandra node's Host ID.
     */
    String getLocalHostId();

    /**
     * Returns a String representation of the local Cassandra load (aka disk usage).
     *
     * @return A String representation of local Cassandra load (disk usage).
     */
    String getLocalLoadString();

    /**
     * Returns the name of the cluster that the local Cassandra node belongs to.
     *
     * @return The name of the cluster that the local Cassandra node belongs to.
     */
    String getClusterName();

    /**
     * Initiates local compaction for specified keyspace and table(s)
     *
     * @param keyspace
     * @param tables
     */
    void triggerCompaction(String keyspace, String... tables);

    /**
     * Initiates local cleanup of the provided keyspace and tables
     *
     * @param jobs
     * @param keyspace
     * @param tables
     */
    void triggerCleanup(int jobs, String keyspace, String... tables);

    /**
     * Determine if the interaction connection to the local node is active. Internally there is caching on this
     * call since it is called so much so if you want the most up to date information, you must explicitly tell
     * it not to use a cache.
     *
     * @param useCache If true, allow stale data (up to ~30s), otherwise check healthy immediately.
     * @return False if the local connection to Cassandra is dead and cannot work. True indicates that the
     * connection is most likely alive, although it can be stale.
     */
    boolean isConnectionAlive(boolean useCache);

    /**
     * Initiate async background connection to Cassandra and connection monitoring
     */
    void connectAsync();

    /**
     * Synchronously connect to the local Cassandra node.
     *
     * @return True if the connection succeeded to the local Cassandra node, false otherwise.
     */
    boolean connectSync();

    /**
     * Initiates local node's flush on specified keyspace and table
     */
    void triggerFlush(String keyspace, String... tables);
}