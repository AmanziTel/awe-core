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
package org.amanzi.splash.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISpreadsheet;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyElementDelta;
import org.rubypeople.rdt.internal.core.RubyModel;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.RubyModelOperation;
import org.rubypeople.rdt.internal.core.RubyModelStatus;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.core.SourceFolder;
import org.rubypeople.rdt.internal.core.Spreadsheet;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;

/**
 * RubyModelOperation for creation of Spreadsheet
 * 
 * @author Lagutko_N
 */
public class CreateSpreadsheetOperation extends RubyModelOperation {
    
    /*
     * RubyProject
     */
    private RubyProject rubyProject;
    
    /*
     * Spreadsheet name 
     */
    private String spreadsheetName;
    
    /*
     * RubyModel
     */
    private RubyModel rubyModel;

    /**
     * Constructor
     * 
     * @param rubyProject Ruby Project of Spreadsheet
     * @param spreadsheetName name of Spreadsheet
     */
    public CreateSpreadsheetOperation(IProject rubyProject, String spreadsheetName) {
        this.spreadsheetName = spreadsheetName;
        
        rubyModel = RubyModelManager.getRubyModelManager().getRubyModel();
        this.rubyProject = (RubyProject)rubyModel.getRubyProject(rubyProject);
    }

    @Override
    protected void executeOperation() throws RubyModelException {
        try {
            beginTask(Messages.operation_createUnitProgress, 2); 
            RubyElementDelta delta = newRubyElementDelta();
            
            ISourceFolder folder = RubyModelUtil.getSourceFolder(rubyProject);
            
            ISpreadsheet unit = new Spreadsheet((SourceFolder)folder, spreadsheetName);
            worked(1);            
            resultElements = new IRubyElement[] {unit};
            for (int i = 0; i < resultElements.length; i++) {
                delta.added(resultElements[i], IRubyElementDelta.ADDED);
            }
            addDelta(delta);
            
            worked(1);
        } finally {
            done();
        }
    }

    @Override
    public IRubyModelStatus verify() {        
        return RubyModelStatus.VERIFIED_OK;
    }
    
    @Override
    public IRubyModel getRubyModel() {
        return rubyModel;
    }
}
