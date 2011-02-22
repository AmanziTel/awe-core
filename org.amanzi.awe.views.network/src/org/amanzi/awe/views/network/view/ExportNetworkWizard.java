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

package org.amanzi.awe.views.network.view;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.views.network.NetworkTreePlugin;
import org.amanzi.neo.loader.core.LoaderUtils;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.ui.NeoLoaderPlugin;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NodeTypes;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.helpers.Predicate;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * <p>
 * Wizard for export network to csv file
 * </p>
 * .
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class ExportNetworkWizard extends Wizard implements IExportWizard {

    /** The selection page. */
    ExportNetworkWizardSelectionPage selectionPage = null;

    /** The column config page. */
    ExportNetworkWizardColumnsConfigPage columnConfigPage = null;

    /** The file property page. */
    ExportNetworkWizardFilePropertyPage filePropertyPage = null;

    /** The saving data selection page. */
    ExportNetworkWizardSavingDataSelectionPage savingDataSelectionPage = null;
    
    private IStructuredSelection selection;

    @Override
    public boolean performFinish() {
        final String fileSelected = selectionPage.getFileName();
        final String separator = getSeparator();
        final String quoteChar = getQuoteChar();
        final String charSet = filePropertyPage.getCharsetValue();
        final Node rootNode = selectionPage.getSelectedNode();
        final Map<String, Map<String, String>> propertyMap = columnConfigPage.getPropertyMap();
        final HashMap<String, Boolean> checkBoxStates = savingDataSelectionPage.getCheckBoxesState();
        final String fileWithPrefix = savingDataSelectionPage.getFileWithPrefixName();
        
        Job exportJob = new Job("Network export") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    runExport(fileSelected, propertyMap, rootNode, checkBoxStates, fileWithPrefix, separator, quoteChar, charSet);
                    return Status.OK_STATUS;
                } catch (IOException e) {
                    return new Status(Status.ERROR, NetworkTreePlugin.PLUGIN_ID, e.getLocalizedMessage(), e);
                } finally {
                    // ActionUtil.getInstance().runTask(new Runnable() {
                    //
                    // @Override
                    // public void run() {
                    // // button.setEnabled(true);
                    // // tableControl.setEnabled(true);
                    // }
                    // }, true);
                }
            }
        };
        exportJob.schedule();
        return true;
    }

    /**
     * Gets the quote char.
     * 
     * @return the quote char
     */
    private String getQuoteChar() {
        return filePropertyPage.getTextDelValue();
    }

    /**
     * Gets the separator.
     * 
     * @return the separator
     */
    private String getSeparator() {
        return filePropertyPage.getFieldDelValue();
    }

    @Override
    public void addPages() {
        super.addPages();
        if (selectionPage == null) {
            selectionPage = new ExportNetworkWizardSelectionPage("mainPage", selection);
        }
        if (columnConfigPage == null) {
            columnConfigPage = new ExportNetworkWizardColumnsConfigPage("columnConfigPage");
        }
        if (filePropertyPage == null) {
            // NeoLoaderPlugin.getDefault().getPreferenceStore().getString(DataLoadPreferences.DEFAULT_CHARSET);
            filePropertyPage = new ExportNetworkWizardFilePropertyPage("propertyCSV", "windows-1251", "\t", "\"");
        }
        if (savingDataSelectionPage == null) {
            savingDataSelectionPage = new ExportNetworkWizardSavingDataSelectionPage("savingDataSelectionPage");
        }
        addPage(selectionPage);
        addPage(columnConfigPage);
        addPage(savingDataSelectionPage);
        addPage(filePropertyPage);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setNeedsProgressMonitor(false);
        this.selection = selection;
        setWindowTitle("Export Network");
    }

    /**
     * Displays error message instead of throwing an exception.
     * 
     * @param e exception thrown
     */
    private void displayErrorMessage(final Exception e) {
        final Display display = PlatformUI.getWorkbench().getDisplay();
        display.asyncExec(new Runnable() {

            @Override
            public void run() {
                MessageDialog.openError(display.getActiveShell(), "Export problem", e.getMessage());
            }

        });
    }

    /**
     * Run export.
     * 
     * @param fileSelected the file selected
     * @param propertyMap the property map
     * @param rootNode the root node
     * @param separator the separator
     * @param quoteChar the quote char
     * @param charSet the char set
     * @throws IOException
     */
    private void runExport(final String fileSelected, final Map<String, Map<String, String>> propertyMap, Node rootNode, HashMap<String, Boolean> checkboxStates, String fileWithPrefix,
            String separator, String quoteChar, String charSet) throws IOException {

        DatasetService datasetService = NeoServiceFactory.getInstance().getDatasetService();
        String[] strtypes = datasetService.getSructureTypesId(rootNode);
        List<String> headers = new ArrayList<String>();
        List<String> usingHeadersFromStructure = new ArrayList<String>();
        
        for (int i = 1; i < strtypes.length; i++) {
            headers.add(strtypes[i]);
        }

        TraversalDescription descr = Traversal.description().depthFirst().uniqueness(Uniqueness.NONE).relationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)
                .filter(Traversal.returnAllButStartNode());
        descr = descr.filter(new Predicate<Path>() {

            @Override
            public boolean accept(Path paramT) {
                return !paramT.endNode().hasRelationship(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING);
            }
        });
        Iterator<Path> iter = descr.traverse(rootNode).iterator();
        List<String> fields = new ArrayList<String>();
        if (fileSelected != null) {

            String ext = "";

            String extention = LoaderUtils.getFileExtension(fileSelected);
            if (extention.equals("")) {
                ext = ".csv";
            } else if (extention.equals(".")) {
                ext = "csv";
            }

            char separatorChar = separator.charAt(0);
            char quote = quoteChar.isEmpty() ? CSVWriter.NO_QUOTE_CHARACTER : quoteChar.charAt(0);
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(fileSelected + ext), charSet), separatorChar, quote);

            try {
                for (String headerType : headers) {
                    Map<String, String> propertyCol = propertyMap.get(headerType);
                    if(propertyCol == null) {
                        continue;
                    }
                    else {
                        // save using headers from newtwork structure
                        usingHeadersFromStructure.add(headerType);
                    }
                    for (String col : propertyCol.values()) {
                        fields.add(col);
                    }
                }
                
                // choose which types we write
                int k = 1;
                strtypes = new String[usingHeadersFromStructure.size() + 1];
                strtypes[0] = "network";
                for (String headerType : usingHeadersFromStructure) {
                    strtypes[k] = headerType;
                    k++;
                }

                writer.writeNext(fields.toArray(new String[0]));
                while (iter.hasNext()) {
                    fields.clear();
                    Path path = iter.next();
                    int currentIndex = 1;
                    for (Node node : path.nodes()) {
                        INodeType nodeType = datasetService.getNodeType(node);
                        if (nodeType == NodeTypes.NETWORK) {
                            continue;
                        }
                        Map<String, String> propertyCol = propertyMap.get(nodeType.getId());
                        // if property exist, but not need write
                        if (propertyCol == null) {
                            continue;
                        }
                        int i = 0, index = 0;
                        while (!nodeType.getId().equals(strtypes[i])) {
                            if (propertyMap.get(strtypes[i]) == null)
                                index++;
                            else
                                index += propertyMap.get(strtypes[i]).keySet().size();
                            i++;
                        }
                        
                        if (currentIndex != index && propertyCol != null) {
                            for (int j = 0; j < index - currentIndex; j++) {
                                fields.add("");
                            }
                            currentIndex += (index - currentIndex);
                        }
                        else if (propertyCol == null) {
                            if (index == currentIndex) {
                                currentIndex++;
                            }
                            else {
                                currentIndex += (index - currentIndex);
                            }
                        }
                        if (propertyCol != null) {
                            for (String propertyName : propertyCol.keySet()) {
                                fields.add(String.valueOf(node.getProperty(propertyName, "")));
                                currentIndex++;
                            }   
                        }
                    }
                    if (fields.size() != 0)
                        writer.writeNext(fields.toArray(new String[0]));
                }
            } finally {
                writer.close();
            }

        }
    }
    
    /**
     * Kasnitskij_V:
     * 
     * Get synonyms to header
     * 
     * @param header -header of value from preference store
     * @return array of possible headers
     */
    protected static String[] getPossibleHeaders(String header) {
        if (header == null) {
            return new String[0];
        }
        String text = NeoLoaderPlugin.getDefault().getPreferenceStore().getString(header);
        if (text == null) {
            return new String[0];
        }
        String[] array = text.split(",");
        List<String> result = new ArrayList<String>();
        for (String string : array) {
            String value = string.trim();
            if (!value.isEmpty()) {
                result.add(value);
            }
        }
        return result.toArray(new String[0]);
    }
    
    /**
     * Kasnitskij_V:
     *
     * @return return map in which key = propertyName and value = name of header
     */
    protected static HashMap<String, String> getMapPropertyNameHeader() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("name_site", DataLoadPreferences.NH_SITE);
        map.put("name_sector", DataLoadPreferences.NH_SECTOR);
        map.put("lat", DataLoadPreferences.NH_LATITUDE);
        map.put("lon", DataLoadPreferences.NH_LONGITUDE);
        map.put("ci", DataLoadPreferences.NH_SECTOR_CI);
        map.put("lac", DataLoadPreferences.NH_SECTOR_LAC);
        map.put("beamwidth", DataLoadPreferences.NH_BEAMWIDTH);
        map.put("azimuth", DataLoadPreferences.NH_AZIMUTH);
        map.put("city", DataLoadPreferences.NH_CITY);
        map.put("msc", DataLoadPreferences.NH_MSC);
        map.put("bsc", DataLoadPreferences.NH_BSC);
        
        return (HashMap<String, String>)map;
    }
}
