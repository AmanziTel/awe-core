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

package org.amanzi.awe;

import org.eclipse.ui.IPerspectiveFactory;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractPerspectiveFactory implements IPerspectiveFactory {

    /** float BOTTOM_LEFT_SIZE field */
    private static final float BOTTOM_LEFT_SIZE = 0.25f;
    /** float BOTTOM_SIZE field */
    private static final float BOTTOM_SIZE = 0.65f;
    /** float TOP_LEFT_SIZE field */
    private static final float TOP_LEFT_SIZE = 0.25f;

    protected float getBottomLeft() {
        return BOTTOM_LEFT_SIZE;
    }

    protected float getBottom() {
        return BOTTOM_SIZE;
    }

    protected float getTopLeft() {
        return TOP_LEFT_SIZE;
    }

}
