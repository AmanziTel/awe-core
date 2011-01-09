package org.amanzi.awe.afp.loaders;

import java.io.File;
import java.io.IOException;

import org.amanzi.awe.afp.files.ControlFile;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public class AfpOutputFileLoader extends AfpLoader {
	
	String outputFileName;

	public AfpOutputFileLoader(Node networkRoot, String outputFileName, Node afpDataset) {
        super("", null, networkRoot.getGraphDatabase());
        this.afpRoot = networkRoot;
        this.afpDataset = afpDataset;
        this.outputFileName = outputFileName;
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
        runAfpLoader(monitor,null);
    }
    
    public void runAfpLoader(IProgressMonitor monitor, String projectName) {

    	mainTx = neo.beginTx();
        NeoUtils.addTransactionLog(mainTx, Thread.currentThread(), "AfpLoader");
        try {
        	File outputFile = new File(this.outputFileName);
        	
            //defineRoot(projectName);
            if (outputFile != null) {
                loadCellFile(outputFile);
            }
            commit(true);
           
//            saveProperties();
        } finally {
            commit(false);
        }
    }
}
