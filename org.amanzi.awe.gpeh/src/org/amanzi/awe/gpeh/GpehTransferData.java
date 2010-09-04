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

package org.amanzi.awe.gpeh;

import java.util.LinkedHashMap;

import org.amanzi.neo.loader.core.IMapBasedDataElement;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class GpehTransferData extends LinkedHashMap<String,Object> implements IMapBasedDataElement<String,Object> {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 6034771565805664352L;
    public static final String PROJECT = "project";
    public static final String DATASET = "dataset";
    public static final String EVENT = "event";
    public static final String TIMESTAMP = "timestamp";
    
}
