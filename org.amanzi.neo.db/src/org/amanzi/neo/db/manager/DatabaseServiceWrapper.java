/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.neo.db.manager;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.KernelEventHandler;
import org.neo4j.graphdb.event.TransactionEventHandler;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class DatabaseServiceWrapper implements GraphDatabaseService {
    private GraphDatabaseService realService;
    private final ReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    DatabaseServiceWrapper(GraphDatabaseService realService) {
        super();
        this.realService = realService;
    }

    void setRealService(GraphDatabaseService realService) {
        w.lock();
        try {
            this.realService = realService;
        } finally {
            w.unlock();
        }
    }

    @Override
    public Node createNode() {
        r.lock();
        try {
            return realService.createNode();
        } finally {
            r.unlock();
        }
    }

    @Override
    public Node getNodeById(long paramLong) {
        r.lock();
        try {
            return realService.getNodeById(paramLong);
        } finally {
            r.unlock();
        }
    }

    @Override
    public Relationship getRelationshipById(long paramLong) {
        r.lock();
        try {
            return realService.getRelationshipById(paramLong);
        } finally {
            r.unlock();
        }
    }

    @Override
    public Node getReferenceNode() {
        r.lock();
        try {
            return realService.getReferenceNode();
        } finally {
            r.unlock();
        }
    }

    @Override
    public Iterable<Node> getAllNodes() {
        r.lock();
        try {
            return realService.getAllNodes();
        } finally {
            r.unlock();
        }
    }

    @Override
    public Iterable<RelationshipType> getRelationshipTypes() {
        r.lock();
        try {
            return realService.getRelationshipTypes();
        } finally {
            r.unlock();
        }
    }

    @Override
    public void shutdown() {
        r.lock();
        try {
            realService.shutdown();
        } finally {
            r.unlock();
        }
    }

    @Override
    public boolean enableRemoteShell() {
        r.lock();
        try {
            return realService.enableRemoteShell();
        } finally {
            r.unlock();
        }
    }

    @Override
    public boolean enableRemoteShell(Map<String, Serializable> paramMap) {
        r.lock();
        try {
            return realService.enableRemoteShell(paramMap);
        } finally {
            r.unlock();
        }
    }

    @Override
    public Transaction beginTx() {
        r.lock();
        try {
            return realService.beginTx();
        } finally {
            r.unlock();
        }
    }

    @Override
    public <T> TransactionEventHandler<T> registerTransactionEventHandler(TransactionEventHandler<T> paramTransactionEventHandler) {
        r.lock();
        try {
            return realService.registerTransactionEventHandler(paramTransactionEventHandler);
        } finally {
            r.unlock();
        }
    }

    @Override
    public <T> TransactionEventHandler<T> unregisterTransactionEventHandler(TransactionEventHandler<T> paramTransactionEventHandler) {
        r.lock();
        try {
            return realService.unregisterTransactionEventHandler(paramTransactionEventHandler);
        } finally {
            r.unlock();
        }
    }

    @Override
    public KernelEventHandler registerKernelEventHandler(KernelEventHandler paramKernelEventHandler) {
        r.lock();
        try {
            return realService.registerKernelEventHandler(paramKernelEventHandler);
        } finally {
            r.unlock();
        }
    }

    @Override
    public KernelEventHandler unregisterKernelEventHandler(KernelEventHandler paramKernelEventHandler) {
        r.lock();
        try {
            return realService.unregisterKernelEventHandler(paramKernelEventHandler);
        } finally {
            r.unlock();
        }
    }

    void lockWrite() {
        w.lock();
    }

    void writeUnlock() {
        w.unlock();
    }

}
