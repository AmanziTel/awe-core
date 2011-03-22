package org.amanzi.awe.afp.loaders;

import java.io.File;
import java.io.IOException;

import org.amanzi.awe.afp.exporters.AfpExporter;
import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.graphdb.Node;

public class AfpOutputFileLoader extends AfpLoader{
	
	private AfpExporter exporter;
	
	public AfpOutputFileLoader(Node networkRoot, Node afpDataset, AfpExporter exporter) {
		super("", null, networkRoot.getGraphDatabase());
        this.afpRoot = networkRoot;
        this.afpDataset = afpDataset;
        this.exporter = exporter;
	}
	
    /**
     * Run.
     * 
     * @param monitor the monitor
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void run(IProgressMonitor monitor) throws IOException {
        //monitor.beginTask("Load AFP data", 7);
        runAfpLoader(monitor);
    }
    
    public void runAfpLoader(IProgressMonitor monitor) {

    	mainTx = neo.beginTx();
        NeoUtils.addTransactionLog(mainTx, Thread.currentThread(), "AfpLoader");
        try {
        	monitor.beginTask("Load generated output", exporter.domainDirPaths.length);
        	for (String dirPath : exporter.domainDirPaths){
        		String outputFileName = dirPath + exporter.outputFileName;
        		File outputFile = new File(outputFileName);
        		if (outputFile != null) {
                    if (outputFile.exists()) {
                        defineRoot();
                		loadCellFile(outputFile, true);
                        statistic.save();
                    } else {
                		AweConsolePlugin.error("Error:: No output File generated: " + outputFile);
                	}
                }
        		monitor.worked(1);
        		commit(true);
        	}
        	
        } finally {
            commit(false);
        }
    }
}
