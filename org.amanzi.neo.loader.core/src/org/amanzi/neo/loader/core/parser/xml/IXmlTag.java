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

package org.amanzi.neo.loader.core.parser.xml;

import org.xml.sax.Attributes;

/**
 * Basic interface for SAX XML Parsers
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public interface IXmlTag {
    
    /**
     * get name of tag
     *
     * @return
     */
    String getName();

    /**
     * Receive notification of the start of an element.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param attributes The attributes attached to the element.  If
     *        there are no attributes, it shall be an empty
     *        Attributes object.
     * @return handler of next tag
     */
    IXmlTag startElement(String localName, Attributes attributes);

    /**
     * Receive notification of the end of an element.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param chars contains string between tags <></>
     * @return handler of next tag
     */
    IXmlTag endElement(String localName, StringBuilder chars);

}
