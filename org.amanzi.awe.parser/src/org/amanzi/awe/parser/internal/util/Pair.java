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

package org.amanzi.awe.parser.internal.util;

/**
 * A simple class for wrapping a couple of objects. Often for return types.
 * 
 * @since 1.0.0
 */
public class Pair<T, V> {
    private T left;
    private V right;

    /**
     * Create a new insance
     * 
     * @param left
     * @param right
     */
    public Pair(T left, V right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Factory method
     * 
     * @return return a new instance
     */
    public <L, R> Pair<L, R> create(L left, R right) {
        return new Pair<L, R>(left, right);
    }

    public T l() {
        return getLeft();
    }

    public V r() {
        return getRight();
    }

    public T left() {
        return getLeft();
    }

    public V right() {
        return getRight();
    }

    /**
     * @return Returns the left.
     */
    public T getLeft() {
        return left;
    }

    /**
     * @return Returns the right.
     */
    public V getRight() {
        return right;
    }

    /**
     * @param left The left to set.
     */
    public void setLeft(T left) {
        this.left = left;
    }

    /**
     * @param right The right to set.
     */
    public void setRight(V right) {
        this.right = right;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Pair other = (Pair)obj;
        if (left == null) {
            if (other.left != null)
                return false;
        } else if (!left.equals(other.left))
            return false;
        if (right == null) {
            if (other.right != null)
                return false;
        } else if (!right.equals(other.right))
            return false;
        return true;
    }

}
