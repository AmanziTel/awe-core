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

import org.apache.commons.net.ftp.FTPClient;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class FtpTreeViewer extends TreeViewer {

    /**
     * @param parent
     */
    public FtpTreeViewer(final Composite parent) {
        super(parent);
    }

    public void initialize(final FTPClient client) {
        setContentProvider(new FtpContentProvider(client));
        setLabelProvider(new FtpLabelProvider());
        try {
            setInput(client.listFiles());
        } catch (IOException e) {
            // TODO KV: handle exception
            e.printStackTrace();
        }
    }
}
