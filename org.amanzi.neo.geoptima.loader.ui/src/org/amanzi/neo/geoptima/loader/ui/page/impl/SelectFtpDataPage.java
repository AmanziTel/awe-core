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

package org.amanzi.neo.geoptima.loader.ui.page.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.ui.view.widgets.AWEWidgetFactory;
import org.amanzi.awe.ui.view.widgets.TextWidget;
import org.amanzi.neo.geoptima.loader.ui.internal.Messages;
import org.amanzi.neo.geoptima.loader.ui.widgets.impl.FtpTreeViewer;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class SelectFtpDataPage extends SelectRemoteDataPage implements SelectionListener {

    private static final Logger LOGGER = Logger.getLogger(SelectFtpDataPage.class);

    private Button bConnect;

    private FtpTreeViewer viewer;

    private TextWidget userNameWidget;

    private TextWidget passwordWidget;

    /**
     * @param name
     */
    public SelectFtpDataPage() {
        super(Messages.selectFtpSource_PageName);
    }

    @Override
    public void createControl(final Composite parent) {
        super.createControl(parent);
        userNameWidget = AWEWidgetFactory.getFactory().addTextWidget(this, SWT.BORDER, Messages.userName_Label, getMainComposite(),
                getMinimalLabelWidth());

        passwordWidget = AWEWidgetFactory.getFactory().addTextWidget(this, SWT.BORDER | SWT.PASSWORD, Messages.password_Label,
                getMainComposite(), getMinimalLabelWidth());

        userNameWidget.setDefault(getDefaulUserName());
        passwordWidget.setDefault(getDefaulPassword());
        viewer = new FtpTreeViewer(getMainComposite());
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.heightHint = 200;
        viewer.getTree().setLayoutData(data);

        bConnect = new Button(getMainComposite(), SWT.PUSH);
        bConnect.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
        bConnect.addSelectionListener(this);
        bConnect.setText(Messages.connectButton_Label);
        viewer.getTree().addSelectionListener(this);
        update();
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        if (e.getSource().equals(bConnect)) {
            connectToFtp();
        } else {
            setSelectedItems(viewer.getTree().getSelection());
        }
        update();
    }

    /**
     *
     */
    private void update() {
        if (getConfiguration().getFiles() != null && getConfiguration().getFiles().length > 0) {
            setErrorMessage(null);
            setPageComplete(true);
            return;
        }
        setErrorMessage(Messages.selectFilesToUploadMessage);
        setPageComplete(false);
    }

    /**
     * @param selection
     */
    private void setSelectedItems(final TreeItem[] selection) {
        List<FTPFile> files = new ArrayList<FTPFile>();
        for (TreeItem item : selection) {
            files.add((FTPFile)item.getData());
        }
        getConfiguration().setRemoteFiles(files.toArray(new FTPFile[files.size()]));
    }

    /**
     *
     */
    private void connectToFtp() {
        try {
            if (getConfiguration().getFtpClient() != null) {
                getConfiguration().getFtpClient().disconnect();
            }
            FTPClient client = new FTPClient();
            FTPClientConfig config = new FTPClientConfig();
            client.configure(config);

            client.connect(getUrl());
            client.login(userNameWidget.getText(), passwordWidget.getText());
            viewer.initialize(client);
            getConfiguration().setFtpClient(client);
        } catch (IOException e) {
            LOGGER.error("can't connect to server", e);
        }
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        // TODO Auto-generated method stub

    }

    // TODO KV: move all default credentials to preference store
    @Override
    protected String getDefaultHost() {
        return "ftp.amanzitel.com";
    }

    protected String getDefaulUserName() {
        return "amanzitel";
    }

    protected String getDefaulPassword() {
        return "J3sT?dr4";
    }

    @Override
    public void onTextChanged(final String text) {
        if (userNameWidget != null && passwordWidget != null) {
            getConfiguration().setCredentials(getUrl(), userNameWidget.getText(), passwordWidget.getText());
        }

    }
}
