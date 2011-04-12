package org.amanzi.awe.gpeh.console.parser;

import java.io.File;

import org.amanzi.awe.gpeh.console.interfaces.IStructuredElement;

/**
 * <p>
 * Wrapper for single file;
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class FileElement implements IStructuredElement {
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