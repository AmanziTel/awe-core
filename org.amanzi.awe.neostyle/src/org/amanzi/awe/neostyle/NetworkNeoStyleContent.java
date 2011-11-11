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

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.StyleContent;

import org.amanzi.awe.catalog.neo.NeoGeoResource;
import org.amanzi.neo.services.enums.GisTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

/**
 * <p>
 *Network style config
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkNeoStyleContent extends StyleContent{

        public static final String ID = "org.amanzi.awe.neostyle.style.network";

        /*
         * Default values for most fields
         */
        public static final int DEF_TRANSPARENCY = 40;
        public static final int DEF_SYMB_SIZE = 15;
        public static final int DEF_SYMB_SIZE_DRIVE = 7;
        public static final boolean DEF_FIX_SYMB_SIZE = false;
        public static final boolean IGNORE_TRANSPARENCY = true;
        public static final boolean DRAW_CORRELATIONS = true;
        public static final int DEF_LABELING = 50;
        public static final int DEF_SMALL_SYMB = 100;
        public static final int DEF_SMALLEST_SYMB = 1000;
        public static final Color DEF_COLOR_LINE = Color.DARK_GRAY;
        public static final Color DEF_COLOR_LABEL = Color.DARK_GRAY;
        public static final Color DEF_COLOR_FILL = new Color(255, 255, 128);
        public static final String DEF_NONE = "";
        public static final String DEF_SECONDARY_PROPERTY = DEF_NONE;
        public static final String DEF_SECTOR_LABEL_TYPE_ID = NodeTypes.SECTOR.getId();
        public static final String DEF_MAIN_PROPERTY = "name";
        /** Network site font size */
        public static final Integer DEF_FONT_SIZE = 10;
        /** Network sector font size */
        public static final Integer DEF_FONT_SIZE_SECTOR = 8;
        public static final String FILTERS = "FILTERS";
        public static final Integer DEF_MAXIMUM_SYMBOL_SIZE = 40;
        public static final Integer DEF_DEF_BEAMWIDTH = 40;
        public static final Integer DEF_ICON_OFFSET = 0;
        public static final Color DEF_COLOR_SITE = Color.DARK_GRAY;
        private static final String LINE_PRFX = "NET_LINE_";
        private static final String FILL_PRFX = "NET_FILL_";
        private static final String LABEL_PRFX = "NET_LABEL_";
        private static final String SITE_PRFX = "NET_SITE_";
        private static final String COLOR_RGB = "NET_RGB";
        private static final String SMALLEST_SYMB = "NET_SECTOR_SMALLEST_SYMB";
        private static final String SMALL_SYMB = "NET_SECTOR_SMALL_SYMB";
        private static final String LABELING = "NET_SECTOR_LABELING";
        private static final String FIX_SYMBOL = "NET_FIX_SYMBOL";
        private static final String SYMBOL_SIZE = "NET_SYMBOL_SIZE";
        private static final String SECTOR_TRANSPARENCY = "NET_SECTOR_TRANSPARENCY";
        private static final String MAX_SYMB_SIZE = "NET_MAXIMUM_SYMBOL_SIZE";
        private static final String DEF_BEAMWIDTH = "NET_DEF_BEAMWIDTH";
        private static final String FONT_SIZE = "NET_FONT_SIZE";
        private static final String FONT_SIZE_SECTOR = "NET_FONT_SIZE_SECTOR";
        private static final String SITE_NAME = "NET_SITE_NAME";
        private static final String SECTOR_NAME = "NET_SECTOR_NAME";
        private static final String SECTOR_LABEL_TYPE = "NET_SECTOR_LABEL_TYPE";


        public NetworkNeoStyleContent() {
            super(ID);
        }

        @Override
        public Object createDefaultStyle(IGeoResource resource, Color colour, IProgressMonitor monitor) throws IOException {
            if (resource.canResolve(NeoGeoResource.class)) {
                NeoGeoResource res = resource.resolve(NeoGeoResource.class, monitor);
//                if (res.getGeoNeo(monitor).getGisType() == GisTypes.NETWORK) {
//                    return createDefaultNetworkStyle();
//                } 
            }
            return null;
        }

        /**
         *
         * @return
         */
        public NetworkNeoStyle createDefaultNetworkStyle() {
            NetworkNeoStyle result = new NetworkNeoStyle(DEF_COLOR_LINE, DEF_COLOR_FILL, DEF_COLOR_LABEL);
            result.setSmallestSymb(DEF_SMALLEST_SYMB);
            result.setSmallSymb(DEF_SMALL_SYMB);
            result.setLabeling(DEF_LABELING);
            result.setFixSymbolSize(DEF_FIX_SYMB_SIZE);
            result.setSymbolSize(DEF_SYMB_SIZE);
            result.setSymbolTransparency(DEF_TRANSPARENCY);
            result.setSiteFill(DEF_COLOR_SITE);
            result.setMaximumSymbolSize(DEF_MAXIMUM_SYMBOL_SIZE);
            result.setDefaultBeamwidth(DEF_DEF_BEAMWIDTH);
            result.setFontSize(DEF_FONT_SIZE);
            result.setSectorFontSize(DEF_FONT_SIZE_SECTOR);
            result.setMainProperty(DEF_MAIN_PROPERTY);
            result.setSectorLabelProperty(DEF_SECONDARY_PROPERTY);
            result.setSectorLabelTypeId(DEF_SECTOR_LABEL_TYPE_ID);
            result.setIgnoreTransparency(IGNORE_TRANSPARENCY);
            return result;
        }

        @Override
        public Class<NetworkNeoStyle> getStyleClass() {
            return NetworkNeoStyle.class;
        }

        @Override
        public Object load(IMemento memento) {
            Color line = loadColor(memento, LINE_PRFX, Color.BLACK);
            Color fill = loadColor(memento, FILL_PRFX, Color.BLACK);
            Color label = loadColor(memento, LABEL_PRFX, Color.BLACK);
            Color site = loadColor(memento, SITE_PRFX, Color.BLACK);
            NetworkNeoStyle result = new NetworkNeoStyle(line, fill, label);
            result.setSiteFill(site);
            result.setSmallestSymb(memento.getInteger(SMALLEST_SYMB));
            result.setSmallSymb(memento.getInteger(SMALL_SYMB));
            result.setLabeling(memento.getInteger(LABELING));
            result.setFixSymbolSize(Boolean.parseBoolean(memento.getString(FIX_SYMBOL)));
            result.setSymbolSize(memento.getInteger(SYMBOL_SIZE));
            result.setSymbolTransparency(memento.getInteger(SECTOR_TRANSPARENCY));
            result.setMaximumSymbolSize(memento.getInteger(MAX_SYMB_SIZE));
            result.setDefaultBeamwidth(memento.getInteger(DEF_BEAMWIDTH));
            result.setFontSize(memento.getInteger(FONT_SIZE));
            result.setSectorFontSize(memento.getInteger(FONT_SIZE_SECTOR));
            result.setMainProperty((memento.getString(SITE_NAME)));
            result.setSectorLabelProperty((memento.getString(SECTOR_NAME)));
            result.setSectorLabelTypeId((memento.getString(SECTOR_LABEL_TYPE)));
            
            String filters = memento.getString(FILTERS);
            if (StringUtils.isNotEmpty(filters)){
                result.setFilterMap(Arrays.asList(filters.split("\n")));
                
            }
            // result.setNetwork(getBoolean(memento, IS_NETWORK_STYLE, true));
            return result;
        }

        private Color loadColor(IMemento memento, String prfx, Color defColor) {
            Integer rgb = memento.getInteger(prfx + COLOR_RGB);
            return rgb == null ? defColor : new Color(rgb);
        }

        @Override
        public Object load(URL url, IProgressMonitor monitor) throws IOException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void save(IMemento memento, Object value) {
            NetworkNeoStyle style = (NetworkNeoStyle)value;
            saveColor(memento, LINE_PRFX, style.getLine());
            saveColor(memento, FILL_PRFX, style.getFill());
            saveColor(memento, LABEL_PRFX, style.getLabel());
            saveColor(memento, SITE_PRFX, style.getSiteFill());
            memento.putInteger(SMALLEST_SYMB, style.getSmallestSymb());
            memento.putInteger(SMALL_SYMB, style.getSmallSymb());
            memento.putInteger(LABELING, style.getLabeling());
            memento.putString(FIX_SYMBOL, String.valueOf(style.isFixSymbolSize()));
            memento.putInteger(SYMBOL_SIZE, style.getSymbolSize());
            memento.putInteger(SECTOR_TRANSPARENCY, style.getSymbolTransparency());
            memento.putInteger(MAX_SYMB_SIZE, style.getMaximumSymbolSize());
            memento.putInteger(DEF_BEAMWIDTH, style.getDefaultBeamwidth());
            memento.putInteger(FONT_SIZE, style.getFontSize());
            memento.putInteger(FONT_SIZE_SECTOR, style.getSecondaryFontSize());
            memento.putString(SITE_NAME, style.getMainProperty());
            memento.putString(SECTOR_NAME, style.getSectorLabelProperty());
            memento.putString(SECTOR_LABEL_TYPE, style.getSectorLabelTypeId());
            StringBuilder sb=new StringBuilder();
            String del="\n";
            for (String name:style.getFilterNames()){
                sb.append(name).append(del);
            }
            memento.putString(FILTERS,sb.toString() );
        }

        /**
         * save color in IMemento
         * 
         * @param memento IMemento
         * @param prfx value prefix
         * @param colorTosave color to save
         */
        private void saveColor(IMemento memento, String prfx, Color colorTosave) {
            memento.putInteger(prfx + COLOR_RGB, colorTosave.getRGB());
        }

    }

