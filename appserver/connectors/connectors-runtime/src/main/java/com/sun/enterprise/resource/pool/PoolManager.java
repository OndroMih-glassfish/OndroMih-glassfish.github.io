/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.sun.enterprise.resource.pool;

import com.sun.appserv.connectors.internal.api.ConnectorConstants.PoolType;
import com.sun.appserv.connectors.internal.api.PoolingException;
import com.sun.appserv.connectors.internal.api.TransactedPoolManager;
import com.sun.enterprise.connectors.ConnectorConnectionPool;
import com.sun.enterprise.deployment.ResourceReferenceDescriptor;
import com.sun.enterprise.resource.ClientSecurityInfo;
import com.sun.enterprise.resource.ResourceHandle;
import com.sun.enterprise.resource.ResourceSpec;
import com.sun.enterprise.resource.allocator.ResourceAllocator;
import com.sun.enterprise.resource.listener.PoolLifeCycle;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.RetryableUnavailableException;
import jakarta.transaction.Transaction;

import java.util.Hashtable;

import org.glassfish.api.naming.SimpleJndiName;
import org.glassfish.resourcebase.resources.api.PoolInfo;
import org.jvnet.hk2.annotations.Contract;

/**
 * PoolManager manages jdbc and connector connection pool
 */
@Contract
public interface PoolManager extends TransactedPoolManager {

    // transaction support levels
    int NO_TRANSACTION = 0;
    int LOCAL_TRANSACTION = 1;
    int XA_TRANSACTION = 2;

    // Authentication mechanism levels
    int BASIC_PASSWORD = 0;
    int KERBV5 = 1;

    // Credential Interest levels
    String PASSWORD_CREDENTIAL = "jakarta.resource.spi.security.PasswordCredential";
    String GENERIC_CREDENTIAL = "jakarta.resource.spi.security.GenericCredential";

    /**
     * Flush Connection pool by reinitializing the connections established in the pool.
     *
     * @param poolInfo
     * @throws com.sun.appserv.connectors.internal.api.PoolingException
     */
    boolean flushConnectionPool(PoolInfo poolInfo) throws PoolingException;

    // Get status of pool
    PoolStatus getPoolStatus(PoolInfo poolInfo);

    ResourceHandle getResourceFromPool(ResourceSpec spec, ResourceAllocator alloc, ClientSecurityInfo info, Transaction transaction) throws PoolingException, RetryableUnavailableException;

    void createEmptyConnectionPool(PoolInfo poolInfo, PoolType pt, Hashtable env) throws PoolingException;

    void putbackResourceToPool(ResourceHandle resourceHandle, boolean errorOccurred);

    void putbackBadResourceToPool(ResourceHandle resourceHandle);

    void putbackDirectToPool(ResourceHandle resourceHandle, PoolInfo poolInfo);

    void resourceClosed(ResourceHandle resourceHandle);

    void badResourceClosed(ResourceHandle resourceHandle);

    void resourceErrorOccurred(ResourceHandle resourceHandle);

    void resourceAbortOccurred(ResourceHandle resourceHandle);

    void transactionCompleted(Transaction transaction, int status);

    void emptyResourcePool(ResourceSpec spec);

    void killPool(PoolInfo poolInfo);

    void reconfigPoolProperties(ConnectorConnectionPool ccp) throws PoolingException;

    boolean switchOnMatching(PoolInfo poolInfo);

    /**
     * Obtain a transactional resource such as JDBC connection
     *
     * @param spec Specification for the resource
     * @param alloc Allocator for the resource
     * @param info Client security for this request
     * @return An object that represents a connection to the resource
     * @throws PoolingException Thrown if some error occurs while obtaining the resource
     */
    Object getResource(ResourceSpec spec, ResourceAllocator alloc, ClientSecurityInfo info) throws PoolingException, RetryableUnavailableException;

    ResourceReferenceDescriptor getResourceReference(SimpleJndiName jndiName, SimpleJndiName logicalName);

    void killAllPools();

    void killFreeConnectionsInPools();

    ResourcePool getPool(PoolInfo poolInfo);

    void setSelfManaged(PoolInfo poolInfo, boolean flag);

    void lazyEnlist(ManagedConnection mc) throws ResourceException;

    void registerPoolLifeCycleListener(PoolLifeCycle poolListener);

    void unregisterPoolLifeCycleListener();
}
