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

package org.amanzi.neo.geoptima.loader.core;

import java.util.Calendar;

import org.amanzi.neo.loader.core.IMultiFileConfiguration;
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
public interface IRemoteSupportConfiguration extends IMultiFileConfiguration {

    boolean isConnectable();

    void setCredentials(String url, String user, String password);

    void setRemoteFiles(FTPFile[] files);

    /**
     * @return
     */
    FTPFile[] getFiles();

    void setFtpClient(FTPClient client);

    /**
     * @return
     */
    FTPClient getFtpClient();

    /**
     * @param calendar
     */
    void setStartTime(Calendar calendar);

    /**
     * @param calendar
     */
    void setEndTime(Calendar calendar);

    /**
     * @return
     */
    Calendar getEndTime();

    /**
     * @return
     */
    Calendar getStartTime();

    /**
     * @param imsi
     */
    void setImsi(String imsi);

    /**
     * @return
     */
    String getImsi();

    /**
     * @param imei
     */
    void setImei(String imei);

    /**
     * @return
     */
    String getImei();

    /**
     * @return
     */
    String getUrl();

    /**
     * @param removeData
     */
    void setRemoveData(boolean removeData);

    /**
     * @return
     */
    boolean isRemoveData();

}
