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
package org.amanzi.neo.core.enums;

/**
 * Element Types for Network
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */

public enum NetworkElementTypes {

    NETWORK(new String[]{"Network"}),
    CITY(new String[]{"City","Town","Ort"}),
    MSC(new String[]{"MSC","MSC_NAME","MSC Name"}),
    BSC(new String[]{"BSC","BSC_NAME","RNC","BSC Name"}),
    SITE(new String[]{"Site","Name","IDX","Site Name"}),
    SECTOR(new String[]{"Sector","Cell","BTS_Name","CELL_NAME","GSM Sector ID"});

    private String[] headers = null;
    
    private NetworkElementTypes(String[] headers){
        this.headers = headers;
    }
    
    public boolean matches(String header){
        for(String valid_header:headers) {
            if(header.equalsIgnoreCase(valid_header)) return true;
        }
        return false;
    }
    
    public String toString(){
        return super.toString().toLowerCase();
    }    
    
}
