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
package org.amanzi.awe.cassidian.structure;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * TODO Purpose of
 * <p>
 * common tag interface
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public interface IXmlTag {
    public final static SimpleDateFormat dateFormatWithTimeZone = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,SSSZ");
//    2010-05-16T02:39:59,509+00:00
    public final static SimpleDateFormat dateFormatWithoutTimeZone = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss,SSS");
   

    /**
     * return type of mainElement;
     * 
     * @return
     */
    public String getType();

    /**
     * set value of tag fields by name;
     * 
     * @param tagName
     * @param value
     */
    public void setValueByTagType(String tagName, Object value);

    /**
     * get value of tag fields by name
     * 
     * @param tagName
     * @return
     */
    public Object getValueByTagType(String tagName);

}
