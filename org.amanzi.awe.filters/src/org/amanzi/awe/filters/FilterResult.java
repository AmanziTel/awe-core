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

package org.amanzi.awe.filters;

import org.neo4j.graphdb.Node;

/**
 * <p>
 * Result of filtering
 * </p>
 * 
 * @author Tsinkel_A
 * @since 1.0.0
 */
public class FilterResult {
    private final boolean isValid;
    private final boolean isGroupResult;
    private final int subFilterNum;
    private final int countSubFilters;
    private final Node filteredNode;

    /**Constructor
     * @param isValid - is valid node
     * @param isGroupResult - is group result
     * @param subFilterNum - number of subfilter (-1 if filter is not group)
     * @param countSubFilters - count of subfilter (-1 if filter is not group)
     * @param filteredNode - filtered node
     */
    public FilterResult(boolean isValid, boolean isGroupResult, int subFilterNum, int countSubFilters, Node filteredNode) {
        super();
        this.isValid = isValid;
        this.isGroupResult = isGroupResult;
        this.subFilterNum = subFilterNum;
        this.countSubFilters = countSubFilters;
        this.filteredNode = filteredNode;
    }

    /**
     * @return Returns the isValid.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * @return Returns the isGroupResult.
     */
    public boolean isGroupResult() {
        return isGroupResult;
    }

    /**
     * @return Returns the subFilterNum.
     */
    public int getSubFilterNum() {
        return subFilterNum;
    }

    /**
     * @return Returns the countDubFilters.
     */
    public int getCountSubFilters() {
        return countSubFilters;
    }

    /**
     * @return Returns the filteredNode.
     */
    public Node getFilteredNode() {
        return filteredNode;
    }

}
