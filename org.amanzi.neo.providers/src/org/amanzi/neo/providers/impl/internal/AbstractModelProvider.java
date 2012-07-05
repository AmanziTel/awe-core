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

package org.amanzi.neo.providers.impl.internal;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.db.manager.events.DatabaseEvent;
import org.amanzi.neo.db.manager.events.IDatabaseEventListener;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.models.impl.internal.util.AbstractLoggable;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractModelProvider<T extends AbstractModel, T1 extends IModel> extends AbstractLoggable
        implements
            IDatabaseEventListener {

    protected interface IKey {

    }

    protected static class NodeKey implements IKey {

        private final Node node;

        public NodeKey(final Node node) {
            this.node = node;
        }

        @Override
        public boolean equals(final Object o) {
            if (o instanceof NodeKey) {
                NodeKey anotherKey = (NodeKey)o;

                return node.equals(anotherKey.node);
            }

            return false;
        }

        @Override
        public int hashCode() {
            return node.hashCode();
        }

    }

    protected static class NameKey implements IKey {

        private final String name;

        public NameKey(final String name) {
            this.name = name;
        }

        @Override
        public boolean equals(final Object o) {
            if (o instanceof NameKey) {
                NameKey anotherKey = (NameKey)o;

                return name.equals(anotherKey.name);
            }

            return false;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

    }

    private final Map<IKey, T> modelCache = new HashMap<IKey, T>();

    protected AbstractModelProvider() {
        DatabaseManagerFactory.getDatabaseManager().addDatabaseEventListener(this);
    }

    protected T initializeFromNode(final Node node) throws ModelException {
        T model = createInstance();
        model.initialize(node);

        return model;
    }

    protected abstract T createInstance();

    protected T getFromCache(final IKey key) {
        return modelCache.get(key);
    }

    protected void addToCache(final T model, final IKey key) {
        modelCache.put(key, model);
    }

    @Override
    public void onDatabaseEvent(final DatabaseEvent event) {
        switch (event.getEventType()) {
        case BEFORE_SHUTDOWN:
            modelCache.clear();
            break;
        default:
            // do nothing
            break;
        }
    }

}
