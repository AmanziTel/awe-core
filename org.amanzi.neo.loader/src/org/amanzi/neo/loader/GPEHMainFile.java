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

package org.amanzi.neo.loader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.amanzi.neo.core.utils.Pair;

/**
 * <p>
 * GPEH MainFile
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class GPEHMainFile implements IGPEHBlock {
    protected Header header;
    protected GPEHEnd endRecord;
    protected ArrayList<Record> records;
    protected ArrayList<Protocol> protocols;
    protected ArrayList<Link> links;

    /**
     * 
     */
    public GPEHMainFile() {
        header = new Header();
        records = new ArrayList<Record>();
        protocols = new ArrayList<Protocol>();
        links = new ArrayList<Link>();
        endRecord = null;

    }

    public void setEndRecord(GPEHEnd end) {
        endRecord = end;
    }

    public void addRecord(Record record) {
        records.add(record);
    }

    /**
     * @param protocol
     */
    public void addProtocol(Protocol protocol) {
        protocols.add(protocol);
    }

    /**
     * add link
     * 
     * @param link - link record
     */
    public void addLink(Link link) {
        links.add(link);
    }

    /**
     * <p>
     * Header record
     * </p>
     * 
     * @author TsAr
     * @since 1.0.0
     */
    public static class Header {
        protected String fileVer;
        protected int month;
        protected int year;
        protected int day;
        protected int hour;
        protected int minute;
        protected int second;
        protected String neUserLabel;
        protected String neLogicalName;

    }

    /**
     * <p>
     * Scaners record
     * </p>
     * 
     * @author TsAr
     * @since 1.0.0
     */
    public static class Record {
        // 1) 0 - PM recording type UETR, 1 - PM recording type CTR, 2 - PM recording type GPEH
        protected int filterType;
        ArrayList<Pair<String, String>> filters = new ArrayList<Pair<String, String>>();

        /**
         * @param buffer
         */
        public void saveFilters(byte[] buffer) {
            try {
                filters.clear();
                ByteArrayInputStream input = new ByteArrayInputStream(buffer);
                while (input.available() > 0) {
                    String key = GPEHParser.readStringToDelim(input, 0);
                    String value = GPEHParser.readStringToDelim(input, 0);
                    filters.add(new Pair<String, String>(key, value));
                }
            } catch (IOException e) {
                // it's not possible for array
                e.printStackTrace();
            }
        }
    }

    /**
     * <p>
     * Protocols record
     * </p>
     * 
     * @author TsAr
     * @since 1.0.0
     */
    public static class Protocol {
        // 0=RRC "3GPP TS 25.331 V6.9.0", 1=NBAP "3GPP TS 25.433 V6.9.0 ", 2=RANAP
        // "3GPP TS 25.413 V6.6.0 ", or 4=RNSAP "3GPP TS 25.423 V4.8.0 "
        protected int id;
        protected String name;
        protected String objectIdentifier;

    }

    /**
     * <p>
     * Link record
     * </p>
     * 
     * @author TsAr
     * @since 1.0.0
     */
    public static class Link {

        protected String filePath;

    }

}
