/**
 * 
 */
package org.amanzi.awe.neostyle.drive;

import java.io.IOException;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.style.IStyleConfigurator;

import org.amanzi.neo.models.drive.IDriveModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Drive Style Configurator
 * 
 * @author Bondoronok_P
 */
public class DriveStyleConfigurator extends IStyleConfigurator {

    private static final String ID = "org.amanzi.awe.neostyle.style.drive";

    private static final String STYLE_TAB_HEADER = "Style";

    private DriveStyle driveStyle;
    private final DriveStyleDefiner defaultStyle = new DriveStyleDefiner();

    /**
     * The Constructor
     */
    public DriveStyleConfigurator() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.style.IStyleConfigurator#canStyle(net.refractions
     * .udig.project.internal.Layer)
     */
    @Override
    public boolean canStyle(final Layer aLayer) {
        return aLayer.getBlackboard().get(ID) != null;
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.style.IStyleConfigurator#refresh()
     */
    @Override
    protected void refresh() {
        driveStyle = (DriveStyle)getStyleBlackboard().get(ID);
        defaultStyle.setCurrentStyle(driveStyle);
        defaultStyle.setRenderableModel(findRenderableModel());
        defaultStyle.refresh();
    }

    @Override
    public void preApply() {
        defaultStyle.preApply();
        DriveStyle clone = (DriveStyle)defaultStyle.getCurrentStyle().clone();
        getStyleBlackboard().put(ID, clone);
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.style.IStyleConfigurator#createControl(org.eclipse
     * .swt.widgets.Composite)
     */
    @Override
    public void createControl(final Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        CTabFolder tabFolder = new CTabFolder(parent, SWT.TOP);
        tabFolder.setBorderVisible(true);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

        CTabItem item = new CTabItem(tabFolder, SWT.NONE);
        item.setText(STYLE_TAB_HEADER);
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
    }

    /**
     * Find renderable model for current layer
     * 
     * @return IRenderableModel or null
     */
    private IDriveModel findRenderableModel() {
        IDriveModel model;
        try {
            model = getLayer().findGeoResource(IDriveModel.class).resolve(IDriveModel.class, null);
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        return model;
    }
}
