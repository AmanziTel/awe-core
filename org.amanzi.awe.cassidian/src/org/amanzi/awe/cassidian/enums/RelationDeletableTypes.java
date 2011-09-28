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

package org.amanzi.awe.cassidian.enums;

/**
 * Types of relation deletion.
 * 
 * @author Shcharbatsevich_A
 */
public enum RelationDeletableTypes {

    /**
     * Node, that has relation with this type, can not be delete in any case.
     */
    FIXED,
    /**
     * Node on the other end should be linked to previous (for example: node M with NEXT relation).
     */
    RELINK,
    /**
     * Delete only link.
     */
    DELETE_ONLY_LINK,
    /**
     * Delete link and check node on the other end (it can be delete only it is need, for example
     * node M with LOCATION relation).
     */
    DEETE_WITH_CHECK_LINKED,
    /**
     * Delete link with node on the other end ('cascade', for example: CHILD link).
     */
    DELETE_WITH_LINKED;
}
