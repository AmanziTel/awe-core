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

package org.amanzi.neo.geoptima.loader.ui.widgets.impl;

import org.apache.commons.net.ftp.FTPFile;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class FtpLabelProvider implements ILabelProvider {

    @Override
    public void addListener(final ILabelProviderListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isLabelProperty(final Object element, final String property) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void removeListener(final ILabelProviderListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public Image getImage(final Object element) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getText(final Object element) {
        FTPFile file = (FTPFile)element;
        return file.getName();
    }

}
