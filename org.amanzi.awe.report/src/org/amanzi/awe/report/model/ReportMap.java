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

package org.amanzi.awe.report.model;

import net.refractions.udig.project.IMap;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class ReportMap implements IReportPart {
    private int index;
    private IMap map;
    private int height;
    private int width;
    private String title;

    /**
     * @param map
     * @param title
     */
    public ReportMap(String title, IMap map) {
        this.map = map;
        this.title = title;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getScript() {
        return null;
    }

    @Override
    public ReportPartType getType() {
        return ReportPartType.MAP;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return Returns the map.
     */
    public IMap getMap() {
        return map;
    }

    /**
     * @param map The map to set.
     */
    public void setMap(IMap map) {
        this.map = map;
    }

    /**
     * @return Returns the height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height The height to set.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return Returns the width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width The width to set.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }

}
