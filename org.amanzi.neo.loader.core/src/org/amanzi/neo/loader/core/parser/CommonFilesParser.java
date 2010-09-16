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

package org.amanzi.neo.loader.core.parser;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.parser.CommonFilesParser.FileElement;

/**
 * <p>
 * Provide common logic for parsing list of files
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public abstract class CommonFilesParser<T extends IDataElement, C extends CommonConfigData> extends StructuredParser<FileElement, T, C> {

    @Override
    protected List<FileElement> getElementList() {
        C prop = getProperties();
        String descr = getDescriptionFormat();
        if (prop.getFileToLoad() == null) {
            List<File> fileToLoad = getAllFiles(prop.getRoot());
            prop.setFileToLoad(fileToLoad);
        }
        List<FileElement> result = new LinkedList<FileElement>();
        for (File file : prop.getFileToLoad()) {
            result.add(new FileElement(file, descr));
        }
        return result;
    }

    /**
     * Close stream.
     * 
     * @param inputStream the input stream
     */
    protected void closeStream(Closeable inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace(getPrintStream());
            }
        }
    }

    /**
     * Gets the all files.
     * 
     * @param root the root
     * @return the all files
     */
    protected List<File> getAllFiles(File root) {
        if (root.isDirectory()) {
            return getAllFiles(root, getProperties().getFilter());
        } else {
            List<File> result = new ArrayList<File>();
            if (getProperties().getFilter().accept(root)) {
                result.add(root);
            }
            return result;
        }
    }

    /**
     * Calculates list of files
     * 
     * @param directory - directory to import
     * @param filter - filter (if filter teturn true for directory this directory will be handled
     *        also )
     * @return list of files to import
     */
    // TODO union with org.amanzi.neo.loader.LoaderUtils.getAllFiles(File, FileFilter)
    public static List<File> getAllFiles(File directory, FileFilter filter) {
        LinkedList<File> result = new LinkedList<File>();
        for (File childFile : directory.listFiles(filter)) {
            if (childFile.isDirectory()) {
                result.addAll(getAllFiles(childFile, filter));
            } else {
                result.add(childFile);
            }
        }
        return result;
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    protected String getDescriptionFormat() {
        return "load file '%s'";
    }

    /**
     * <p>
     * Wrapper for single file;
     * </p>
     * 
     * @author TsAr
     * @since 1.0.0
     */
    public static class FileElement implements IStructuredElement {
        private File file;
        private String descriptionFormat;

        /**
         * Instantiates a new file element.
         * 
         * @param file the file
         * @param descriptionFormat the description format
         */
        public FileElement(File file, String descriptionFormat) {
            super();
            this.file = file;
            this.descriptionFormat = descriptionFormat;
        }

        @Override
        public long getSize() {
            return file.length();
        }

        @Override
        public String getDescription() {
            return String.format(descriptionFormat, file.getName());
        }

        /**
         * Gets the file.
         * 
         * @return the file
         */
        public File getFile() {
            return file;
        }

    }

    @Override
    protected T getStartupElement(FileElement element) {
        return null;
    }

    @Override
    protected T getFinishElement(FileElement element) {
        return null;
    }
}
