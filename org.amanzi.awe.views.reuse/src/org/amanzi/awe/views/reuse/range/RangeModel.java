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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.services.utils.Pair;
import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.runtime.Assert;
import org.geotools.util.NumberRange;

/**
 * <p>
 * Range model
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class RangeModel {
    private LinkedList<Bar> bars = new LinkedList<Bar>();
    private String name;
    private boolean isChanged = false;

    public RangeModel() {
    }
    public RangeModel(String name) {
        this.name = name;

    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        if (!ObjectUtils.equals(name, this.name)) {
            setChanged(true);
            this.name = name;
        }
    }

    public void setSize(int size) {
        Assert.isTrue(size >= 0);
        boolean remove = bars.size() > size;
        while (bars.size() != size) {
            if (remove) {
                bars.removeLast();
            } else {
                addNew();
            }
        }

    }

    /**
     *
     */
    private void addNew() {
        bars.add(new Bar(null, null, null));
    }

    /**
     * @return
     */
    public int getSize() {
        return bars.size();
    }

    /**
     * @return
     */
    public Bar[] getBars() {
        return bars.toArray(new Bar[0]);
    }

    /**
     * @param isChanged The isChanged to set.
     */
    public void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    /**
     * @return
     */
    public boolean isChanged() {
        if (isChanged) {
            return true;
        }
        for (Bar bar : bars) {
            if (bar.isChanged()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return
     */
    public Pair<Boolean, String> validate() {
        Bar[] bars = getBars();
        List<Bar> list = Arrays.asList(bars);
        Collections.sort(list, new Comparator<Bar>() {

            @Override
            public int compare(Bar o1, Bar o2) {
                NumberRange r1 = o1.getRange();
                NumberRange r2 = o2.getRange();
                if (r1 == r2) {
                    return 0;
                }
                if (r1 == null) {
                    return -1;
                }
                if (r2 == null) {
                    return 1;
                }
                int cmp = new Double(r1.getMinimum()).compareTo(r2.getMinimum());
                if (cmp == 0) {
                    return new Double(r1.getMaximum()).compareTo(r2.getMaximum());
                }
                return cmp;

            }

        });
        for (Bar bar : bars) {
            if (!bar.isValid()) {
                return new Pair<Boolean, String>(false, "Model have incorrect bars");
            }
        }
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i - 1).getRange().intersects(list.get(i).getRange())) {
                return new Pair<Boolean, String>(false, String.format("Have intersected ranges: %s and %s", list.get(i - 1)
                        .getRange(), list.get(i).getRange()));
            }
        }
        return new Pair<Boolean, String>(true, "");
    }
}
