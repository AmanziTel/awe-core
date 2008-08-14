package org.amanzi.awe.catalog.json;

import net.refractions.udig.catalog.IServiceInfo;

public class JSONServiceInfo extends IServiceInfo {
    JSONService handle;
    public JSONServiceInfo( JSONService service ) {
        this.handle = service;
        this.title = handle.getIdentifier().toString();
        this.description = "Javascript Object Notaction file server (" + this.title + ")";
        this.keywords = new String[]{"JSON", "File", "HTTP"};
    }
}
