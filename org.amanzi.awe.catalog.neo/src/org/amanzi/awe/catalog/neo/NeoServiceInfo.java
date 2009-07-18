package org.amanzi.awe.catalog.neo;

import net.refractions.udig.catalog.IServiceInfo;

public class NeoServiceInfo extends IServiceInfo {
    NeoService handle;
    public NeoServiceInfo( NeoService service ) {
        this.handle = service;
        this.title = handle.getIdentifier().toString();
        this.description = "Neo4J Database (" + this.title + ")";
        this.keywords = new String[]{"Neo4j", "Database", "File"};
    }
}
