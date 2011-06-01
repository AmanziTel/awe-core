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

import org.amanzi.neo.services.enums.NodeTypes;
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
    public  final void  ckeckSectorLabelTextOnEmptyOrNullPrperty(){
        NetworkNeoStyle style=createDefaultNetworkStyle();
        style.setSectorLabelTypeId(NodeTypes.SECTOR.getId());
        style.setSectorLabelProperty("property1");
        Node sector=context.mock(Node.class);
        
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
