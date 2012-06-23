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
public abstract class AbstractModelProvider<T extends AbstractModel, T1 extends IModel> extends AbstractLoggable implements IDatabaseEventListener {

    protected interface IKey {

    }

    protected static class NameKey implements IKey {

        private String name;

        public NameKey(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
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

    private Map<IKey, T> modelCache = new HashMap<IKey, T>();

    protected AbstractModelProvider() {
        DatabaseManagerFactory.getDatabaseManager().addDatabaseEventListener(this);
    }

    protected T initializeFromNode(Node node) throws ModelException {
        T model = createInstance();
        model.initialize(node);

        return model;
    }

    protected abstract T createInstance();

    protected T getFromCache(IKey key) {
        return modelCache.get(key);
    }

    protected void addToCache(T model, IKey key) {
        modelCache.put(key, model);
    }

    @Override
    public void onDatabaseEvent(DatabaseEvent event) {
        switch (event.getEventType()) {
        case BEFORE_SHUTDOWN:
            modelCache.clear();
            break;
        default:
            // do nothing
        }
    }

    @SuppressWarnings("unchecked")
    protected T convert(T1 model) {
        return (T)model;
    }

}
