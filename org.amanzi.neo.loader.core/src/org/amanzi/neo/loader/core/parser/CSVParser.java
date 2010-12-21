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

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Map;

import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.CountingFileInputStream;
import org.amanzi.neo.loader.core.LoaderUtils;
import org.amanzi.neo.loader.core.ProgressEventImpl;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import au.com.bytecode.opencsv.CSVReader;

/**
 * <p>
 * Csv parser
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class CSVParser extends CommonFilesParser<BaseTransferData, CommonConfigData> {
    
    protected String[] possibleFieldSepRegexes = new String[] {"\t", ",", ";"};
    protected Character delimeters;
    private BaseTransferData initdata;
    private int minSize=2;

    @Override
    protected BaseTransferData getFinishData() {
        return null;
    }

    @Override
    protected boolean parseElement(FileElement element) {
        CSVReader reader = null;
        char delim = getDelimiters(element.getFile());
        try {
            CountingFileInputStream is = new CountingFileInputStream(element.getFile());
            reader = new CSVReader(new InputStreamReader(is), delim);
            String[] nextLine;
            String[] header = null;
            int persentageOld = 0;
            long line = 0;
            while ((nextLine = reader.readNext()) != null) {
                try {
                    line++;
                    if (header == null) {
                        if (nextLine.length<minSize){
                           continue; 
                        }
                        header = nextLine;
                        if ("true".equals(initdata.get("cleanHeaders"))){
                            cleanHeader(header);
                        }
                        continue;
                    }

                    if (header.length != nextLine.length) {
                        error(String.format("File %s, line %s:incorrect data: Header length=%s,data length=%s. Data was skipped", element.getFile().getName(), line, header.length, nextLine.length));
                        continue;
                    }
                    BaseTransferData data = new BaseTransferData();
                    data.setLine(line);
                    data.setFileName(element.getFile().getName());
                    for (int i = 0; i < header.length; i++) {
                        data.put(header[i], nextLine[i]);
                    }
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



    private void cleanHeader(String[] header) {
        if (header!=null){
            for (int i = 0; i < header.length; i++) {
                header[i]=cleanHeader(header[i]); 
            }
        }
    }
    private final static String cleanHeader(String header) {
        return header.replaceAll("[\\s\\-\\[\\]\\(\\)\\/\\.\\\\\\:\\#]+", "_").replaceAll("[^\\w]+", "_").replaceAll("_+", "_").replaceAll("\\_$", "").toLowerCase();
    }
    /**
     * Gets the delimiters.
     *
     * @param file the file
     * @return the delimiters
     */
    private char getDelimiters(File file) {
        if (delimeters == null) {
            
            String fieldSepRegex = LoaderUtils.defineDelimeters(file,minSize,possibleFieldSepRegexes);
            delimeters = fieldSepRegex.charAt(0);
        }
        return delimeters;
    }



    @Override
    protected BaseTransferData getInitData(CommonConfigData properties) {
        initdata = new BaseTransferData();
        initdata.setProjectName(properties.getProjectName());
        initdata.setRootName(properties.getDbRootName());
        for (Map.Entry<String, Object> entry:properties.getAdditionalProperties().entrySet()){
            if ("workdate".equals(entry.getKey())){
                initdata.setWorkDate((Calendar)entry.getValue());
            }else if (entry.getValue()!=null){
                initdata.put(entry.getKey(), entry.getValue().toString());
            }
        }
        CoordinateReferenceSystem crs = properties.getCrs();
        setCrs(initdata,crs);
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
    protected BaseTransferData getStartupElement(FileElement element) {
        BaseTransferData result=new BaseTransferData();
        result.setFileName(element.getFile().getName());
        Calendar cl=Calendar.getInstance();
        cl.setTimeInMillis(element.getFile().lastModified());
//        result.put("timestamp", String.valueOf(element.getFile().lastModified()));
        result.setWorkDate(cl);
         return result;
    }

    @Override
    protected BaseTransferData getFinishElement(FileElement element) {
        return null;
    }

}
