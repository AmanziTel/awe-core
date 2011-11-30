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

package org.amanzi.neo.model.distribution.xml.schema;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author kostyukovich_n
 * @since 1.0.0
 */
public class TestSchema {
    
    private static void createXmlFromObject() throws JAXBException, FileNotFoundException {
        Distribution distr = new Distribution();
        Data data = new Data();
        data.setDataType("NETWORK");
        data.setNodeType("SECTOR");
        distr.setData(data);
        Bars bars = new Bars();
        Bar bar = new Bar();
        bar.setName("bar number one");
        bar.setColor(new Color(100, 200, 50));
        Filter filter = new Filter();
        filter.setExpressionType("OR");
        filter.setFilterType("EQUALS");
        filter.setPropertyName("azimut");
        filter.setValue("13.44");
        Filter underFilter = new Filter();
        underFilter.setFilterType("MORE");
        underFilter.setPropertyName("azimut2");
        underFilter.setValue("20");
        filter.setUnderlyingFilter(underFilter);
        bar.setFilter(filter);
        bars.getBar().add(bar);
        distr.setBars(bars);
        
        JAXBContext jc = JAXBContext.newInstance( "org.amanzi.neo.model.distribution.xml.schema" );
        Marshaller m = jc.createMarshaller();
        OutputStream os = new FileOutputStream( "d:/nosferatu.xml" );
        m.marshal( distr, os );        
    }

    /**
     *
     * @param args
     * @throws JAXBException 
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException, JAXBException {
        createXmlFromObject();
    }

}
