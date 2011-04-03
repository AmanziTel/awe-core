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

package org.amanzi.awe.views.reuse.range;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.swt.graphics.RGB;
import org.geotools.util.NumberRange;

/**
 * <p>
 * BAR information
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class Bar {

    /** The range. */
    private NumberRange range;

    /** The name. */
    private String name;

    /** The color. */
    private RGB color;

    /** The is changed. */
    private boolean isChanged = false;

    /**
     * @param range
     * @param name
     * @param color
     */
    public Bar(NumberRange range, String name, RGB color) {
        super();
        this.range = range;
        this.name = name;
        this.color = color;
    }

    /**
     * Gets the range.
     * 
     * @return the range
     */
    public NumberRange getRange() {
        return range;
    }

    /**
     * Sets the range.
     * 
     * @param range the new range
     */
    public void setRange(NumberRange range) {
        setRange(range, true);
    }

    public void setRange(NumberRange range, boolean change) {
        if (change) {
            setChanged(isChanged() || !ObjectUtils.equals(range, this.range));
        }
        this.range = range;
    }
    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setName(String name) {
        setName(name, true);
    }

    public void setName(String name, boolean change) {
        if (change) {
            setChanged(isChanged() || !ObjectUtils.equals(name, this.name));
        }
        this.name = name;
    }

    /**
     * Gets the color.
     * 
     * @return the color
     */
    public RGB getColor() {
        return color;
    }


    /**
     * Sets the color.
     * 
     * @param color the new color
     */
    public void setColor(RGB color) {
        setColor(color, true);
    }

    public void setColor(RGB color, boolean change) {
        if (change) {
            setChanged(isChanged() || !ObjectUtils.equals(color, this.color));
        }
        this.color = color;
    }


    /**
     * Checks if is changed.
     * 
     * @return true, if is changed
     */
    public boolean isChanged() {
        return isChanged;
    }

    /**
     * Sets the changed.
     * 
     * @param isChanged the new changed
     */
    public void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public String getRangeAsStr(String defIfNull) {
        return range != null ? String.valueOf(range) : defIfNull;
    }

    /**
     * @return
     */
    public RGB getDefaultRGB() {
        return color != null ? color : new RGB(255, 255, 255);
    }

    /**
     * @return
     */
    public String getDefaultName() {
        return name != null ? name : getRangeAsStr("");
    }

    /**
     * @return
     */
    public boolean isValid() {
        return range != null;
    }

}
