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

package org.amanzi.neo.geoptima.loader.impl.core;

import org.amanzi.neo.geoptima.loader.core.IRemoteSupportConfiguration;
import org.amanzi.neo.loader.core.impl.MultiFileConfiguration;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class RemoteSupportConfiguration extends MultiFileConfiguration implements IRemoteSupportConfiguration {

    private String url;

    private String user;

    private String password;

    private FTPFile[] files;
    private FTPClient ftpClient;

    @Override
    public boolean isConnectable() {
        return false;
    }

    @Override
    public void setCredentials(final String url, final String user, final String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * @return Returns the url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return Returns the user.
     */
    public String getUser() {
        return user;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }

    @Override
    public void setRemoteFiles(final FTPFile[] files) {
        this.files = files;
    }

    @Override
    public FTPFile[] getFiles() {
        return files;
    }

    @Override
    public void setFtpClient(final FTPClient client) {
        this.ftpClient = client;

    }

    @Override
    public FTPClient getFtpClient() {
        return ftpClient;
    }
}
