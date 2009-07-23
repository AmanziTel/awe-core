package org.amanzi.neo.core.enums;

/**
 * Element Types for Network
 * 
 * @author Lagutko_N
 * @since 1.1.0
 */

public enum NetworkElementTypes {

    NETWORK("Network"),
    BSC("BSC"),
    SITE("Name"),
    SECTOR("Cell");
    
    private String header = null;
    
    private NetworkElementTypes(String header){
        this.header = header;
    }
    
    public String getHeader(){
        return header;
    }
    
    public String toString(){
        return super.toString().toLowerCase();
    }    
    
}
