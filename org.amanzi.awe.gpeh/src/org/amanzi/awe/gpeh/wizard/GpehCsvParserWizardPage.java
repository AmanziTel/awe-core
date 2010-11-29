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

package org.amanzi.awe.gpeh.wizard;

import java.io.File;
import java.util.HashSet;

import org.amanzi.awe.gpeh.GpehTransferData;
import org.amanzi.awe.gpeh.parser.Events;
import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.ui.wizards.DirectoryEditor;
import org.amanzi.neo.loader.ui.wizards.LoaderPage;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class GpehCsvParserWizardPage extends LoaderPage<CommonConfigData> {
    private String inputDirectory, outputDirectory;
    
    protected String datasetName;
    private DirectoryFieldEditor inputDir, outputDir;
    
    public  static final String SELECTED_EVENT = "GPEH SELECTED_EVENTS";
    private static final String WIZARD_PAGE_TITLE = "Save GPEH-data to CSV files";
    
    protected GpehCsvParserWizardPage() {
        super(WIZARD_PAGE_TITLE);
    }

    @Override
    public void createControl(Composite parent) {
        final Composite main = new Composite(parent, SWT.NULL);
        main.setLayout(new GridLayout(3, false));
        inputDir = new DirectoryEditor("editor", "Input directory: ", main); // NON-NLS-1
        inputDir.setEmptyStringAllowed(true);
        inputDir.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                inputDirectory = inputDir.getStringValue();
                update();
            }
        });
        
        outputDir = new DirectoryEditor("editor", "Output directory: ", main);
        outputDir.setEmptyStringAllowed(true);
        outputDir.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                outputDirectory = outputDir.getStringValue();
                update();
            }
        });
        
        setAccessType(false);
        setControl(main);
        
        getLoadersDescriptions();
        selectLoader(0);
        update();
    }

    @Override
    protected boolean validateConfigData(CommonConfigData configurationData) {
        try {
            if (StringUtils.isEmpty(inputDirectory) ||
                    StringUtils.isEmpty(outputDirectory)) {
                return false;
            }
            File fileInput = new File(inputDirectory);
            File fileOutput = new File(outputDirectory);
            if ((!(fileInput.isAbsolute() && fileInput.exists())) || 
                    (!(fileOutput.isAbsolute() && fileOutput.exists()))) {
                return false;
            }
            
            configurationData.setRoot(fileInput);
            
            HashSet<Events> selectedEvents = new HashSet<Events>();
            
            Events event = null;
            int count = 0;
            for (int i = 384; i < 396; i++) {
                event = Events.findById(i);
                selectedEvents.add(event);
                count++;
            }
            
            for (int i = 397; i < 409; i++) {
                event = Events.findById(i);
                selectedEvents.add(event);
                count++;
            }
            selectedEvents.add(Events.findById(410));
            
            for (int i = 413; i < 424; i++) {
                event = Events.findById(i);
                selectedEvents.add(event);
                count++;
            }
            
            for (int i = 425; i < 457; i++) {
                event = Events.findById(i);
                selectedEvents.add(event);
                count++;
            }
            configurationData.getAdditionalProperties().put(SELECTED_EVENT, new HashSet<Events>(selectedEvents));
            configurationData.getAdditionalProperties().put(GpehTransferData.OUTPUT_DIRECTORY, outputDir.getStringValue());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
