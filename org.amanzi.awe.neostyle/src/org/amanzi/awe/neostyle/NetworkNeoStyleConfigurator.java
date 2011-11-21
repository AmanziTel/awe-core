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

import org.amanzi.neo.services.model.IRenderableModel;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

/**
 * <p>
 * Configurer of network style
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkNeoStyleConfigurator extends IStyleConfigurator {
    private NetworkNeoStyle curStyle;
    private NetworkStyleDefiner defaultStyle = new NetworkStyleDefiner();
    private CheckboxTableViewer viewer;
    
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

        // // Set up a gradient background for the selected tab
        // tabFolder.setSelectionBackground(new Color[] {
        // display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW),
        // display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW),
        // display.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW)}, new int[] { 50,
        // 100});

        CTabItem item = new CTabItem(tabFolder, SWT.NONE);
        item.setText("Style");
        tabFolder.setSelection(item);
        ScrolledComposite scroll = new ScrolledComposite(tabFolder, SWT.V_SCROLL | SWT.H_SCROLL);
        scroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scroll.setExpandVertical(true);
        scroll.setExpandHorizontal(true);
        item.setControl(scroll);
        Composite style = new Composite(scroll, SWT.FILL);
        style.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        defaultStyle.createPartControl(style);
        scroll.setContent(style);
        scroll.setMinSize(style.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        item = new CTabItem(tabFolder, SWT.NONE);
        item.setText("Filters");
        scroll = new ScrolledComposite(tabFolder, SWT.V_SCROLL | SWT.H_SCROLL);
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
        filterMain.setLayout(new GridLayout(2, true));
        Label lb = new Label(filterMain, SWT.LEFT);
        lb.setText("Applyed filters:");
        viewer = CheckboxTableViewer.newCheckList(filterMain, SWT.FULL_SELECTION | SWT.BORDER);
        viewer.setColumnProperties(new String[] {"Filter"});
        
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 10);
        viewer.getControl().setLayoutData(layoutData);
        viewer.getTable().addControlListener(new ControlListener() {

            @Override
            public void controlResized(ControlEvent e) {
                Table table = (Table)e.widget;
                int width = table.getClientArea().width;
                table.getColumn(0).setWidth(width - 2);
            }

            @Override
            public void controlMoved(ControlEvent e) {
            }
        });
    }

    @Override
    protected void refresh() {
        curStyle = (NetworkNeoStyle)getStyleBlackboard().get(ID);
        defaultStyle.setCurStyle(curStyle);
        IRenderableModel resource;
        try {
            resource = getLayer().findGeoResource(IRenderableModel.class).resolve(IRenderableModel.class, null);
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        defaultStyle.setRenderableResource(resource);
        defaultStyle.refresh();
        viewer.setInput(curStyle);

    }

    @Override
    public void preApply() {
        super.preApply();
        defaultStyle.preApply();
        getStyleBlackboard().put(ID, defaultStyle.getCurStyle());
    }        
}
