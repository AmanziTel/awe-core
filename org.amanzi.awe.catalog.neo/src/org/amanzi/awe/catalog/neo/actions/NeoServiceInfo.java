package org.amanzi.awe.catalog.neo.actions;

import net.refractions.udig.catalog.IServiceInfo;

public class NeoServiceInfo extends IServiceInfo {
    NeoService handle;
    public NeoServiceInfo( NeoService service ) {
        this.handle = service;
        this.title = handle.getIdentifier().toString();
        this.description = "Neoclipse support integrated to uDIG platform (" + this.title + ")";
        this.keywords = new String[]{"Neoclipse"};
    }
}
