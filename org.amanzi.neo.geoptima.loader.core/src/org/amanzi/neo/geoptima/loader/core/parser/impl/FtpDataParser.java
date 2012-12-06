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

package org.amanzi.neo.geoptima.loader.core.parser.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.amanzi.awe.scripting.JRubyRuntimeWrapper;
import org.amanzi.awe.scripting.exceptions.ScriptingException;
import org.amanzi.neo.geoptima.loader.core.IRemoteSupportConfiguration;
import org.amanzi.neo.geoptima.loader.core.internal.GeoptimaLoaderCorePlugin;
import org.amanzi.neo.loader.core.IMappedStringData;
import org.amanzi.neo.loader.core.ISingleFileConfiguration;
import org.amanzi.neo.loader.core.impl.SingleFileConfiguration;
import org.amanzi.neo.loader.core.parser.impl.CSVParser;
import org.amanzi.neo.loader.core.parser.impl.internal.MultiStreamParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class FtpDataParser
        extends
            MultiStreamParser<ISingleFileConfiguration, CSVParser, IRemoteSupportConfiguration, IMappedStringData> {

    private static final Logger LOGGER = Logger.getLogger(FtpDataParser.class);
    private static final String TEMPORARY_DIRECTORY_PATH = System.getProperty("user.home") + File.separator + ".amanzi"
            + File.separator + "temp";
    private static final FTPFileFilter FTP_FILE_FILTER = new FTPFileFilter() {
        @Override
        public boolean accept(final FTPFile file) {
            return file.getName().endsWith(".csv") || file.getName().endsWith(".json");
        }
    };

    @Override
    protected CSVParser createParserInstance() {
        return new CSVParser();
    }

    @Override
    public void init(final IRemoteSupportConfiguration configuration) {
        File folder = createFolder(TEMPORARY_DIRECTORY_PATH);
        uploadData(configuration.getFtpClient(), configuration.getFiles(), folder, null);
        try {
            JRubyRuntimeWrapper wrapper = GeoptimaLoaderCorePlugin.getDefault().getRuntimeWrapper();
            wrapper.executeScript("-x -l -a ~/.amanzi/temp/2012-08-06/*json", GeoptimaLoaderCorePlugin.getDefault()
                    .getScriptsForProject("geoptima-loader").get(0));
        } catch (ScriptingException e) {
            LOGGER.error("can't process convering", e);
        }
        super.init(configuration);
    }

    /**
     * @param string
     */
    private File createFolder(final String string) {
        File file = new File(string);
        try {
            FileUtils.forceMkdir(file);
        } catch (IOException e) {
            LOGGER.error("can't create folder " + string, e);
        }
        return file;

    }

    /**
     * @param ftpClient
     * @param files
     * @param folder
     */
    private void uploadData(final FTPClient ftpClient, final FTPFile[] files, final File folder, final FTPFile source) {
        try {
            for (FTPFile file : files) {
                if (file.isDirectory()) {
                    File newFolder = createFolder(folder.getAbsolutePath() + File.separator + file.getName());
                    uploadData(ftpClient, getFiles(ftpClient, file), newFolder, file);
                } else {
                    FileOutputStream fos = new FileOutputStream(folder.getAbsolutePath() + File.separator + file.getName());
                    ftpClient.retrieveFile(file.getLink(), fos);
                    fos.close();
                }
            }
        } catch (IOException e) {
            LOGGER.error("can't upload file ", e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                LOGGER.error("Error occured while trying to close connection ", e);
            }
        }
    }

    /**
     * @param ftpClient
     * @param file
     * @return
     * @throws IOException
     */
    private FTPFile[] getFiles(final FTPClient ftpClient, final FTPFile file) throws IOException {
        FTPFile[] files = ftpClient.listFiles(file.getLink(), FTP_FILE_FILTER);
        for (FTPFile innerFile : files) {
            innerFile.setLink(file.getLink() + "/" + innerFile.getName());
        }
        return files;
    }

    @Override
    protected ISingleFileConfiguration createSingleFileConfiguration(final File file,
            final IRemoteSupportConfiguration configuration) {
        SingleFileConfiguration singleFileConfiguration = new SingleFileConfiguration();
        singleFileConfiguration.setDatasetName(configuration.getDatasetName());
        singleFileConfiguration.setFile(file);
        return singleFileConfiguration;
    }

    @Override
    public File getLastParsedFile() {
        return getCurrentParser().getLastParsedFile();
    }

    @Override
    public int getLastParsedLineNumber() {
        return getCurrentParser().getLastParsedLineNumber();
    }

}
