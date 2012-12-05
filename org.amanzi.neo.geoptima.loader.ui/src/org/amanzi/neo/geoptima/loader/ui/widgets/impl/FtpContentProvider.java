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

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class FtpContentProvider implements ITreeContentProvider {

    private static final Logger LOGGER = Logger.getLogger(FtpContentProvider.class);

    private final FTPClient client;

    private FTPFile parentFile;

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    public FtpContentProvider(final FTPClient client) {
        this.client = client;
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object[] getElements(final Object inputElement) {
        return (FTPFile[])inputElement;
    }

    @Override
    public Object[] getChildren(final Object parentElement) {
        parentFile = (FTPFile)parentElement;
        try {
            return client.listFiles(parentFile.getLink());
        } catch (IOException e) {
            LOGGER.error("can't get list of file for " + parentFile.getName(), e);
        };
        return null;
    }

    @Override
    public Object getParent(final Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(final Object element) {
        FTPFile file = (FTPFile)element;
        file.setLink((parentFile == null ? StringUtils.EMPTY : parentFile.getLink()) + "/" + file.getName());
        if (file.isDirectory()) {
            return true;

        }
        return false;
    }

}
