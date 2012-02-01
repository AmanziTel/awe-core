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

package org.amanzi.neo.loader.core.saver.neighbor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.services.utils.Pair;

/**
 * <p>
 * Contains conflicted N2N related models (target or serving doesn't exist) T - key to identify
 * model
 * </p>
 * 
 * @author harchevnikov_m
 * @since 1.0.0
 */
public class ConflictNeighboursModel<T extends Comparable<T>> {
    /*
     * Map: serving -->targets
     */
    private Map<T, Set<T>> neighbors;

    /**
     * @param neighbors
     */
    public ConflictNeighboursModel() {
        super();
        this.neighbors = new HashMap<T, Set<T>>();
    }

    /**
     * @param neighbors
     */
    public ConflictNeighboursModel(Map<T, Set<T>> neighbors) {
        super();
        this.neighbors = new HashMap<T, Set<T>>();
    }

    public Set<T> getTargetModels(T servingId) {
        return neighbors.get(servingId);
    }

    public void addRelation(T serving, T target) {
        Set<T> targets = neighbors.get(serving);
        if (targets == null) {
            targets = new HashSet<T>();
        }

        targets.add(target);
    }

    /**
     * Remove Pair form model
     *
     * @param serving Serving
     * @param target Target
     * @return removed pair or null if it doesn't exist
     */
    public boolean removeRelation(T serving, T target) {
        Set<T> targets = neighbors.get(serving);
        if (targets != null) {
            boolean removed = targets.remove(target);
            return removed;
        }

        return false;
    }
}