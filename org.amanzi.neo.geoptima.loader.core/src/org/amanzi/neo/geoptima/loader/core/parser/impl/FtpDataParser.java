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
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.scripting.JRubyRuntimeWrapper;
import org.amanzi.awe.scripting.exceptions.ScriptingException;
import org.amanzi.neo.geoptima.loader.core.IRemoteSupportConfiguration;
import org.amanzi.neo.geoptima.loader.core.internal.GeoptimaLoaderCorePlugin;
import org.amanzi.neo.geoptima.loader.impl.core.RemoteSupportConfiguration;
import org.amanzi.neo.loader.core.IMappedStringData;
import org.amanzi.neo.loader.core.ISingleFileConfiguration;
import org.amanzi.neo.loader.core.impl.SingleFileConfiguration;
import org.amanzi.neo.loader.core.parser.impl.CSVParser;
import org.amanzi.neo.loader.core.parser.impl.internal.MultiStreamParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
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

    private static final String PATH_TO_PARSED_DATA = "PATH_TO_PARSED_DATA";
    private static final String SOURCE_DATA_PATH = "SOURCE_DATA_PATH";

    private static final String FOLDER_FOR_PARSED_DATA = "parsed_data";
    private static final String PARSED_DATA_FILE_NAME = "parsed_data.csv";
    private static final FTPFileFilter FTP_FILE_FILTER = new FTPFileFilter() {
        @Override
        public boolean accept(final FTPFile file) {
            return file.getName().endsWith(".csv") || file.getName().endsWith(".json") || file.isDirectory();
        }
    };
    private static final FileFilter DIRECTORY_FILE_FILTER = FileFilterUtils.directoryFileFilter();
    private static final FileFilter CSV_FILE_FILTER = FileFilterUtils.suffixFileFilter(".csv");
    private static final String SOURCE_DATA_PATH_FORMAT = "%s" + File.separator + "**" + File.separator + "*.json";

    private final List<File> files = new ArrayList<File>();

    private File sourceFolder;

    @Override
    protected CSVParser createParserInstance() {
        return new CSVParser();
    }

    @Override
    public void init(final IRemoteSupportConfiguration configuration) {
        Calendar calendar = Calendar.getInstance();
        sourceFolder = createFolder(TEMPORARY_DIRECTORY_PATH + File.separator + calendar.getTimeInMillis());
        File parsedDataFolder = createFolder(sourceFolder.getAbsolutePath() + File.separator + FOLDER_FOR_PARSED_DATA);
        try {
            LOGGER.info("< Start data Donwloading from ftp:" + configuration.getUrl() + " >");
            uploadData(configuration.getFtpClient(), configuration.getFiles(), sourceFolder, null);
            LOGGER.info("< Data uploading successefull compleat >");
        } catch (IOException e) {
            LOGGER.error("can't upload necessarry data", e);
        } finally {
            try {
                configuration.getFtpClient().disconnect();
            } catch (IOException e) {
                LOGGER.error("Error occured while trying to close connection ", e);
            }
        }
        try {
            JRubyRuntimeWrapper wrapper = GeoptimaLoaderCorePlugin.getDefault().getRuntimeWrapper();
            Map<String, String> replacedParameters = new HashMap<String, String>();
            replacedParameters
                    .put(PATH_TO_PARSED_DATA, parsedDataFolder.getAbsolutePath() + File.separator + PARSED_DATA_FILE_NAME);
            replacedParameters.put(SOURCE_DATA_PATH, String.format(SOURCE_DATA_PATH_FORMAT, sourceFolder.getAbsolutePath()));
            LOGGER.info("<Parsing process begin on folder: "
                    + String.format(SOURCE_DATA_PATH_FORMAT, sourceFolder.getAbsolutePath())
                    + " destination forlder for parsed data: " + parsedDataFolder.getAbsolutePath() + File.separator
                    + PARSED_DATA_FILE_NAME + ">");
            wrapper.executeScript(GeoptimaLoaderCorePlugin.getDefault().getScriptsForProject("geoptima-loader").get(0),
                    replacedParameters);
            LOGGER.info("<Parsing process end >");
            initCsvFiles(sourceFolder);
            ((RemoteSupportConfiguration)configuration).addFiles(files);
        } catch (ScriptingException e) {
            LOGGER.error("can't process convering", e);
        }
        super.init(configuration);
    }

    /**
     * @param folder
     * @param configuration
     */
    private void initCsvFiles(final File folder) {
        files.addAll(Arrays.asList(folder.listFiles(CSV_FILE_FILTER)));
        for (File file : folder.listFiles(DIRECTORY_FILE_FILTER)) {
            initCsvFiles(file);
        }
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
     * @throws IOException
     */
    private void uploadData(final FTPClient ftpClient, final FTPFile[] files, final File folder, final FTPFile source)
            throws IOException {
        try {
            for (FTPFile file : files) {
                if (file.isDirectory()) {
                    File newFolder = createFolder(folder.getAbsolutePath() + File.separator + file.getName());
                    uploadData(ftpClient, getFiles(ftpClient, file), newFolder, file);
                } else {
                    FileOutputStream fos = new FileOutputStream(folder.getAbsolutePath() + File.separator + file.getName());
                    LOGGER.info("<Downloading file " + file.getName() + " to directory " + folder.getAbsolutePath() + ">");
                    ftpClient.retrieveFile(file.getLink(), fos);
                    fos.close();
                }
            }
        } catch (IOException e) {
            LOGGER.error("can't upload file ", e);
            throw e;
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

    @Override
    public void finishUp() {
        super.finishUp();
        if (getConfiguration().isRemoveData()) {
            try {
                LOGGER.info("Remove folder " + sourceFolder);
                FileUtils.forceDelete(sourceFolder);
            } catch (IOException e) {
                LOGGER.error("Cann't remove folder" + sourceFolder, e);
            }
        }
    }
}
