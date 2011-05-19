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

import java.io.IOException;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.style.IStyleConfigurator;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkNeoStyleConfigurator extends IStyleConfigurator {
    private NetworkNeoStyle curStyle;
    private NetworkStyleDefiner defaultStyle=new NetworkStyleDefiner();
    /** NetworkNeoStyleConfigurator ID field */
    public static final String ID = "org.amanzi.awe.neostyle.style.network"; //$NON-NLS-1$


    //private static final String[] ICON_SIZES = new String[] {"6", "8", "12", "16", "32", "48", "64"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

    public NetworkNeoStyleConfigurator() {
        super();
        
    }

    @Override
    public boolean canStyle(Layer aLayer) {
        return aLayer.getStyleBlackboard().get(ID) != null;
        // try {
        // if (aLayer.getStyleBlackboard().get(ID) != null) {
        // GeoNeo geoNeo;
        // geoNeo = aLayer.findGeoResource(NeoGeoResource.class).resolve(GeoNeo.class, null);
        // return geoNeo.getGisType() == GisTypes.NETWORK;
        // }
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // return false;
    }


    @Override
    public void createControl(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        CTabFolder tabFolder = new CTabFolder(parent, SWT.TOP);
        tabFolder.setBorderVisible(true);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        
//        // Set up a gradient background for the selected tab
//        tabFolder.setSelectionBackground(new Color[] {
//            display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW),
//            display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW),
//            display.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW)}, new int[] { 50,
//            100});

        CTabItem item = new CTabItem (tabFolder, SWT.NONE);
        item.setText ("Style");
        tabFolder.setSelection(item);
        Composite style=new Composite(tabFolder,SWT.NONE);
        style.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        defaultStyle.createPartControl(style);
        item.setControl(style);
        item = new CTabItem (tabFolder, SWT.NONE);
        item.setText ("Filters");
        ScrolledComposite scroll = new ScrolledComposite(tabFolder, SWT.V_SCROLL | SWT.H_SCROLL);
        scroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scroll.setExpandVertical(true);
        scroll.setExpandHorizontal(true);
        item.setControl(scroll);
        Composite filterMain = new Composite(scroll, SWT.NONE);
        filterMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createFilterPage(filterMain);    
        scroll.setContent(filterMain);
    }


    private void createFilterPage(Composite filterMain) {
        filterMain.setLayout(new GridLayout(1, true));
        
    }


    @Override
    protected void refresh() {
        curStyle = (NetworkNeoStyle)getStyleBlackboard().get(ID);
        defaultStyle.setCurStyle(curStyle);
        GeoNeo resource;
        try {
            resource = getLayer().findGeoResource(GeoNeo.class).resolve(GeoNeo.class, null);
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
        defaultStyle.setGeoNeo(resource);
        defaultStyle.refresh();
 
    }

    @Override
    public void preApply() {
        super.preApply();
        defaultStyle.preApply();
        getStyleBlackboard().put(ID, curStyle);
    }

 
}
