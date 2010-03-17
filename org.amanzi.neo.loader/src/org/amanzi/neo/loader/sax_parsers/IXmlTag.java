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

package org.amanzi.neo.loader.sax_parsers;

import org.xml.sax.Attributes;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public interface IXmlTag {
    String getName();

    /**
     * @param localName
     * @param attributes
     * @return
     */
    IXmlTag startElement(String localName, Attributes attributes);

    /**
     * @param localName
     * @param chars
     * @return
     */
    IXmlTag endElement(String localName, StringBuilder chars);

}
