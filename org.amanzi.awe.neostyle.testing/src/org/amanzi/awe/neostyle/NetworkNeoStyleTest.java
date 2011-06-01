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

package org.amanzi.awe.neostyle;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.amanzi.neo.services.IDatasetService;
import org.amanzi.neo.services.enums.NodeTypes;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * <p>
 *Test Network style
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class NetworkNeoStyleTest {
    private Mockery context=new JUnit4Mockery();
    @Test
    public  final void  getSectorLabelTextTypeSectorOnNullPrperty(){
        NetworkNeoStyle style=createDefaultNetworkStyle();
        style.setSectorLabelTypeId(NodeTypes.SECTOR.getId());
        style.setSectorLabelProperty("property1");
        final Node sector=context.mock(Node.class);
        final IDatasetService service=context.mock(IDatasetService.class);
        context.checking(new Expectations(){{
            oneOf(service).getPropertyListOfSectorRoot(sector,NodeTypes.SECTOR.getId(),"property1");will(returnValue(new ArrayList<String>()));
        }});
        String label=style.getSectorLabel(sector,service);
        assertEquals("",label);
    }
    @Test
    public  final void  getSectorLabelTextTypeSectorOnFillProperty(){
        NetworkNeoStyle style=createDefaultNetworkStyle();
        style.setSectorLabelTypeId(NodeTypes.SECTOR.getId());
        style.setSectorLabelProperty("property1");
        final Node sector=context.mock(Node.class);
        final IDatasetService service=context.mock(IDatasetService.class);
        final ArrayList<String> expectedResult = new ArrayList<String>();
        expectedResult.add("12.7");
        context.checking(new Expectations(){{
            oneOf(service).getPropertyListOfSectorRoot(sector,NodeTypes.SECTOR.getId(),"property1");
            will(returnValue(expectedResult));
        }});
        String label=style.getSectorLabel(sector,service);
        assertEquals(String.valueOf(12.7),label);
    }
    @Test
    public  final void  getSectorLabelTextTypeTRXOnFillProperty(){
        NetworkNeoStyle style=createDefaultNetworkStyle();
        style.setSectorLabelTypeId(NodeTypes.TRX.getId());
        style.setSectorLabelProperty("property2");
        final Node sector=context.mock(Node.class);
        final IDatasetService service=context.mock(IDatasetService.class);
        final ArrayList<String> expectedResult = new ArrayList<String>();
        expectedResult.add("12.7");
        expectedResult.add("ddd");
        context.checking(new Expectations(){{
            oneOf(service).getPropertyListOfSectorRoot(sector,NodeTypes.TRX.getId(),"property2");
            will(returnValue(expectedResult));
        }});
        String label=style.getSectorLabel(sector,service);
        assertEquals("12.7, ddd",label);
    }

    /**
     *
     * @return
     */
    private  NetworkNeoStyle createDefaultNetworkStyle() {
            NetworkNeoStyle result = new NetworkNeoStyle(NetworkNeoStyleContent.DEF_COLOR_LINE, NetworkNeoStyleContent.DEF_COLOR_FILL, NetworkNeoStyleContent.DEF_COLOR_LABEL);
            result.setSmallestSymb(NetworkNeoStyleContent.DEF_SMALLEST_SYMB);
            result.setSmallSymb(NetworkNeoStyleContent.DEF_SMALL_SYMB);
            result.setLabeling(NetworkNeoStyleContent.DEF_LABELING);
            result.setFixSymbolSize(NetworkNeoStyleContent.DEF_FIX_SYMB_SIZE);
            result.setSymbolSize(NetworkNeoStyleContent.DEF_SYMB_SIZE);
            result.setSymbolTransparency(NetworkNeoStyleContent.DEF_TRANSPARENCY);
            result.setSiteFill(NetworkNeoStyleContent.DEF_COLOR_SITE);
            result.setMaximumSymbolSize(NetworkNeoStyleContent.DEF_MAXIMUM_SYMBOL_SIZE);
            result.setDefaultBeamwidth(NetworkNeoStyleContent.DEF_DEF_BEAMWIDTH);
            result.setFontSize(NetworkNeoStyleContent.DEF_FONT_SIZE);
            result.setSectorFontSize(NetworkNeoStyleContent.DEF_FONT_SIZE_SECTOR);
            result.setMainProperty(NetworkNeoStyleContent.DEF_MAIN_PROPERTY);
            result.setSectorLabelProperty(NetworkNeoStyleContent.DEF_SECONDARY_PROPERTY);
            result.setSectorLabelTypeId(NetworkNeoStyleContent.DEF_SECTOR_LABEL_TYPE_ID);
            result.setIgnoreTransparency(NetworkNeoStyleContent.IGNORE_TRANSPARENCY);
            return result;
        }
}
