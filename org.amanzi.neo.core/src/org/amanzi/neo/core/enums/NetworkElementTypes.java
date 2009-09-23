package org.amanzi.neo.core.enums;

/**
 * Element Types for Network
 * 
 * @author Lagutko_N
 * @since 1.1.0
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
