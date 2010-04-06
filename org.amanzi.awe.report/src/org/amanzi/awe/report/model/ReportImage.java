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

package org.amanzi.awe.report.model;


/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Pechko_E
 * @since 1.0.0
 */
public class ReportImage implements IReportPart {
    private String imageFileName;
    private int index;
    private int width=600;
    private int height=400;

    /**
     * @param imageFileName
     */
    public ReportImage(String imageFileName) {
        super();
        this.imageFileName = imageFileName;
    }

    /**
     * @return Returns the imageFileName.
     */
    public String getImageFileName() {
        return imageFileName;
    }

    /**
     * @param imageFileName The imageFileName to set.
     */
    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    

    @Override
    public String getScript() {
        return new StringBuffer("image '").append(imageFileName).append("'").toString();
    }

    /**
     * @return Returns the type.
     */
    public ReportPartType getType() {
        return ReportPartType.IMAGE;
    }

    

    /**
     * @return Returns the index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index The index to set.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((imageFileName == null) ? 0 : imageFileName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ReportImage other = (ReportImage)obj;
        if (imageFileName == null) {
            if (other.imageFileName != null)
                return false;
        } else if (!imageFileName.equals(other.imageFileName))
            return false;
        return true;
    }

    /**
     * @return Returns the width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width The width to set.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return Returns the height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height The height to set.
     */
    public void setHeight(int height) {
        this.height = height;
    }

}
