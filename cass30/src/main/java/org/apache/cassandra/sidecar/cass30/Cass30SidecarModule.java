/**
 * Copyright (c) 2019 Netflix, Inc.  All rights reserved.
 */
package org.apache.cassandra.sidecar.cass30;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.apache.cassandra.sidecar.common.CassRpcInteraction;
import org.apache.cassandra.sidecar.common.Configuration;

public final class Cass30SidecarModule extends AbstractModule
{
    @Override
    protected void configure()
    {

    }

    @Provides
    @Singleton
    public CassRpcInteraction getCass30RpcInteraction(Configuration config) throws Exception
    {
        CassRpcInteraction cassRpcInteraction = new Cass30RpcInteraction(config);
        return cassRpcInteraction;
    }
}
