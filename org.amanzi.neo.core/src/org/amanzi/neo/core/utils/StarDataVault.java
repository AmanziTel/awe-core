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

package org.amanzi.neo.core.utils;

import java.awt.Point;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class StarDataVault {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private static StarDataVault instance = new StarDataVault();
    private Map<URL, Map<Long, Point>> vault = new HashMap<URL, Map<Long, Point>>();

    private StarDataVault() {
    }

    public static StarDataVault getInstance() {
        return instance;
    }

    public void setMap(URL identifier, Map<Long, Point> nodeMap) {
        writeLock.lock();
        try {
            vault.put(identifier, nodeMap);
        } finally {
            writeLock.unlock();
        }
    }

    public Map<Long, Point> getCopyOfMap(URL identifier) {
        readLock.lock();
        try {
            Map<Long, Point> result = new HashMap<Long, Point>();
            Map<Long, Point> map = vault.get(identifier);
            if (map != null) {
                result.putAll(map);
            }
            return result;
        } finally {
            readLock.unlock();
        }
    }
}
