package org.amanzi.awe.catalog.csv;

import net.refractions.udig.catalog.IServiceInfo;

public class CSVServiceInfo extends IServiceInfo {
    CSVService handle;
    public CSVServiceInfo( CSVService service ) {
        this.handle = service;
        this.title = handle.getIdentifier().toString();
        this.description = "Comma Separated Value File Service (" + this.title + ")";
        this.keywords = new String[]{"CSV", "File"};
    }
}
