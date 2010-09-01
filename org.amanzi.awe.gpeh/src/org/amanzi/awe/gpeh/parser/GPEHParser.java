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

package org.amanzi.awe.gpeh.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.amanzi.awe.gpeh.parser.internal.GPEHEvent;
import org.amanzi.awe.gpeh.parser.internal.GPEHEvent.Event;
import org.amanzi.awe.parser.core.IParserOldVersion;
import org.kc7bfi.jflac.io.BitInputStream;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public class GPEHParser implements IParserOldVersion<GPEHDataElement> {
    
    private InputStream inputStream;
    
    private BitInputStream bitStream;
    
    private boolean hasNext = true;
    
    private Set<Integer> possibleIds = new TreeSet<Integer>();
    
    private long timestamp;
    
    private SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd");
    
    private long fileSize;
    
    private long processedSize;

    private GPEHTimeWrapper timeWrapper;
    
    @Override
    public void init(File file) {
        timeWrapper=new GPEHTimeWrapper(file.getName());
        if (!timeWrapper.isValid()) {
            System.err.println(String.format("Can't parese file %s. incorrect name format", file.getName()));
            hasNext = false;
            return;
        }
        Calendar cl=Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cl.clear();
        cl.set(Calendar.YEAR, timeWrapper.getYear());
        cl.set(Calendar.MONTH, timeWrapper.getMonth());
        cl.set(Calendar.DAY_OF_MONTH, timeWrapper.getDay());
        timestamp = cl.getTimeInMillis();
        //TODO use pattern for main
        if (file.getName().contains("Mp0")) {
            hasNext = false;
        }
        else {
            try {
                inputStream = new FileInputStream(file);
                fileSize = file.length();
                if (file.getName().endsWith("gz")) {
                    inputStream = new GZIPInputStream(inputStream);
                    fileSize = getFileSize(file);
                }
                
                bitStream = new BitInputStream(inputStream);
                
            }
            catch (IOException e) {
                //TODO: handle
                e.printStackTrace();
                hasNext = false;
            }
        }
    }
    
    private long getFileSize(File file) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        raf.seek(raf.length() - 4);
        int b4 = raf.read();
        int b3 = raf.read();
        int b2 = raf.read();
        int b1 = raf.read();
        int val = (b1 << 24) | (b2 << 16) + (b3 << 8) + b4;
        raf.close();
        
        return val;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }
    
    private GPEHDataElement convertIterator(GPEHEvent event) {
        if ((event == null) || (event.getEvent() == null)) {
            return null;
        }
        GPEHDataElement element = new GPEHDataElement();
        Event gpehEvent = event.getEvent();
        element.setTimestamp(gpehEvent.getFullTime(timestamp));
        element.setName(gpehEvent.getType().name());
        element.setType(gpehEvent.getType());
        element.setSize(event.getSize());
            
        for (Entry<Parameters, Object> parameter : gpehEvent.getProperties().entrySet()) {
            element.put(parameter.getKey().name(), parameter.getValue());
        }
        return element;
    }
    //TODO tsinkel a - refactored hasNext/next mechanism - if hasNext=true we should always take GPEHDataElement, not NULL. 
    @Override
    public GPEHDataElement next() {
        return convertIterator(getNewEvent());
    }
    
    private GPEHEvent getNewEvent() {
        try {
            int recordLen = bitStream.readRawUInt(16) - 3;
            int recordType = bitStream.readRawUInt(8);
            if (recordType == 4) {
                GPEHEvent result = new GPEHEvent();
                
                if (recordType == 4) {
                    org.amanzi.awe.gpeh.parser.internal.GPEHParser.parseEvent(bitStream, result, recordLen, possibleIds, timeWrapper);
                } else if (recordType == 7) {
                    org.amanzi.awe.gpeh.parser.internal.GPEHParser.pareseFooter(bitStream, result);
                } else if (recordType == 6) {
                    org.amanzi.awe.gpeh.parser.internal.GPEHParser.pareseError(bitStream, result);
                } else {
                    // wrong file format!
                    throw new IllegalArgumentException();
                }
                processedSize = bitStream.getTotalBytesRead();
                return result;
            }
        }
        catch (IOException e){ 
            hasNext = false;
            return null;
        }
        return null;
            
    }
    
    public void setPossibleIds(Set<Events> possibleIds) {
        this.possibleIds.clear();
        
        for (Events event : possibleIds) {
            this.possibleIds.add(event.getId());
        }
    }

    @Override
    public void remove() {
    }

    public long getFileSize() {
        return fileSize;
    }

    @Override
    public long getProccessedSize() {
        return processedSize;
    }
}
