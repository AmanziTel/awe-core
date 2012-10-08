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

package org.amanzi.awe.ui.view.widgets;

import java.util.Arrays;
import java.util.Collection;

import net.refractions.udig.ui.PlatformGIS;

import org.amanzi.awe.ui.view.widgets.PaletteComboWidget.IPaletteChanged;
import org.amanzi.awe.ui.view.widgets.internal.AbstractComboWidget;
import org.eclipse.swt.widgets.Composite;
import org.geotools.brewer.color.BrewerPalette;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class PaletteComboWidget extends AbstractComboWidget<BrewerPalette, IPaletteChanged> {

    public interface IPaletteChanged extends AbstractComboWidget.IComboSelectionListener {

    }

    /**
     * @param parent
     * @param listener
     * @param label
     */
    protected PaletteComboWidget(final Composite parent, final IPaletteChanged listener, final String label) {
        super(parent, listener, label);
    }

    @Override
    protected Collection<BrewerPalette> getItems() {
        return Arrays.asList(PlatformGIS.getColorBrewer().getPalettes());
    }

    @Override
    protected String getItemName(final BrewerPalette item) {
        return item.getName();
    }

    @Override
    protected void fireListener(final IPaletteChanged listener, final BrewerPalette selectedItem) {
        // TODO Auto-generated method stub
    }

    public BrewerPalette getCurrentPalette() {
        return getSelectedItem();
    }

}
