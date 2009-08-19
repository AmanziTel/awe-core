/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.amanzi.neo.core.enums;

/**
 * TODO Purpose of
 * <p>
 * GIS Types of GIS node
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public enum GisTypes {
    /** GisTypes Network */
    Network("network"),
    /** GisTypes TEMS */
    Tems("tems");

    private String header;

    private GisTypes(String header) {
        this.header = header;
    }

    /**
     * gets header
     * 
     * @return header
     */
    public String getHeader() {
        return header;
    }

    /**
     * Find enum by header
     * 
     * @param header
     * @return GisTypes
     */
    public static GisTypes findGisTypeByHeader(String header) {
        for (GisTypes gisType : GisTypes.values()) {
            if (gisType.getHeader().equals(header)) {
                return gisType;
            }
        }
        return null;
    }
}
