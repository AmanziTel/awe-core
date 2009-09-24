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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.neo4j.api.core.Node;

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
    private Map<Node, Map<Node, Point>> vault = new HashMap<Node, Map<Node, Point>>();

    private StarDataVault() {

    }

    public static StarDataVault getInstance() {
        return instance;
    }

    public void setMap(Node gisNode, Map<Node, Point> nodeMap) {
        writeLock.lock();
        try {
            vault.put(gisNode, nodeMap);
        } finally {
            writeLock.unlock();
        }
    }

    public Map<Node, Point> getCopyOfAllMap() {
        readLock.lock();
        try {
            Map<Node, Point> result = new HashMap<Node, Point>();
            for (Map<Node, Point> map : vault.values()) {
                result.putAll(map);
            }
            return result;
        } finally {
            readLock.unlock();
        }
    }

    public Map<Node, Point> getCopyOfMap(Node gisNode) {
        readLock.lock();
        try {
            Map<Node, Point> result = new HashMap<Node, Point>();
            Map<Node, Point> map = vault.get(gisNode);
            if (map != null) {
                result.putAll(map);
            }
            return result;
        } finally {
            readLock.unlock();
        }
    }
}
