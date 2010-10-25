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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Map;

import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.CountingFileInputStream;
import org.amanzi.neo.loader.core.ProgressEventImpl;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * <p>
 * Line parser
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class LineParser extends CommonFilesParser<LineTransferData, CommonConfigData> {

    private LineTransferData initdata;
    private String characterSet;

    /**
     * Gets the character set.
     *
     * @return the character set
     */
    public String getCharacterSet() {
        if (characterSet==null){
            return Charset.defaultCharset().name();
        }
        return characterSet;
    }

    /**
     * Sets the character set.
     *
     * @param characterSet the new character set
     */
    public void setCharacterSet(String characterSet) {
        this.characterSet = characterSet;
    }

    @Override
    protected LineTransferData getFinishData() {
        return null;
    }

    @Override
    protected boolean parseElement(FileElement element) {
        BufferedReader reader=null;
        try {
            CountingFileInputStream is = new CountingFileInputStream(element.getFile());
            reader= new BufferedReader(new InputStreamReader(is, getCharacterSet()));
            int persentageOld = 0;
            long line = 0;
            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                try {
                    line++;

                    LineTransferData data = new LineTransferData();
                    data.setLine(line);
                    data.setFileName(element.getFile().getName());
                    data.setStringLine(nextLine);
                    getSaver().save(data);
                } finally {
                    int persentage = is.percentage();
                    if (persentage - persentageOld > PERCENTAGE_FIRE) {
                        persentageOld = persentage;
                        if (fireSubProgressEvent(element, new ProgressEventImpl(String.format(getDescriptionFormat(), element.getFile().getName()), persentage/100d))) {
                            return true;
                        }
                    }
                }
            }

        } catch (IOException e) {
            exception(e);
        } finally {
            closeStream(reader);
        }
        return false;

    }

    @Override
    protected LineTransferData getInitData(CommonConfigData properties) {
        initdata = new LineTransferData();
        initdata.setProjectName(properties.getProjectName());
        initdata.setRootName(properties.getDbRootName());
        for (Map.Entry<String, Object> entry:properties.getAdditionalProperties().entrySet()){
            if ("workdate".equals(entry.getKey())){
                initdata.setWorkDate((Calendar)entry.getValue());
            }else if (entry.getValue()!=null){
                initdata.put(entry.getKey(), entry.getValue().toString());
            }
        }
        setCrs(initdata, properties.getCrs());
        return initdata;
    }
    /**
     * Sets the crs.
     *TODO move no utility
     * @param data the data
     * @param crs the crs
     */
    protected void setCrs(BaseTransferData data, CoordinateReferenceSystem crs) {
        if (crs!=null){
            data.put("CRS", crs.toWKT());
        }else{
            data.remove("CRS");
        }
    }
    @Override
    protected LineTransferData getStartupElement(FileElement element) {
        LineTransferData result = new LineTransferData();
        result.setFileName(element.getFile().getName());
        result.put("timestamp", String.valueOf(element.getFile().lastModified()));
        return result;
    }

}
