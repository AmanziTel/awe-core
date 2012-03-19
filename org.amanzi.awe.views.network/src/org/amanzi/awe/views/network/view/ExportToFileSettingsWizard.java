package org.amanzi.awe.views.network.view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.synonyms.ExportSynonymsManager;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import com.google.common.collect.Lists;

/**
 * <p>
 * Wizard for network export to file
 * </p>
 * 
 * @author ladornaya_a
 * @since 1.0.0
 */
public class ExportToFileSettingsWizard extends Wizard {

    // error title
    private static final String ERROR_TITLE = "Error";

    // row separator
    private static final String ROW_SEPARATOR = "\n";

    // selected network
    private INetworkModel network;

    // first wizard page
    protected ExportToFileSettingsPage page;

    // all exist sector properties
    private List<String> properties;

    public ExportToFileSettingsWizard(INetworkModel network) {
        super();
        setNeedsProgressMonitor(true);
        setWindowTitle("Export to file");
        this.network = network;
    }

    @Override
    public void addPages() {
        page = new ExportToFileSettingsPage();
        addPage(page);
    }

    @Override
    public boolean performFinish() {

        Job exportJob = new Job(getWindowTitle()) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                export(monitor);
                return Status.OK_STATUS;
            }
        };
        exportJob.schedule();

        return true;
    }

    /**
     * export to file
     */
    protected void export(IProgressMonitor monitor) {

        // create csv file
        File csvFile = createFile();

        // get all sectors
        List<IDataElement> sectors = Lists.newArrayList(network.getAllElementsByType(NetworkElementNodeType.SECTOR));

        monitor.beginTask(getWindowTitle(), sectors.size() + 1);

        // separator for sorting of elements by name
        Comparator<IDataElement> comp = new Comparator<IDataElement>() {

            public int compare(IDataElement arg0, IDataElement arg1) {
                return (arg0.get(AbstractService.NAME).toString()).compareTo(arg1.get(AbstractService.NAME).toString());
            };
        };

        Collections.sort(sectors, comp);

        // export synonyms manager for file headers
        ExportSynonymsManager esm = ExportSynonymsManager.getManager();

        /*
         * fill properties list
         */
        properties = new ArrayList<String>();

        // list for all properties
        List<String> allProperties = new ArrayList<String>();

        for (IDataElement sector : sectors) {
            Set<String> sectorProperties = sector.keySet();
            for (String sectorProperty : sectorProperties) {
                if (!allProperties.contains(sectorProperty)) {
                    String s;
                    try {
                        s = esm.getExportHeader(network, NetworkElementNodeType.SECTOR, sectorProperty);
                    } catch (DatabaseException e) {
                        MessageDialog.openError(null, ERROR_TITLE, e.getMessage());
                        throw (RuntimeException)new RuntimeException().initCause(e);
                    }
                    if (s != null) {
                        properties.add(s);
                    } else {
                        properties.add(sectorProperty);
                    }
                    allProperties.add(sectorProperty);
                }
            }
        }

        try {
            FileWriter writer = new FileWriter(csvFile);

            // write headers
            writeRow(writer, properties);

            monitor.worked(1);

            // write values
            for (IDataElement sector : sectors) {
                List<String> values = new ArrayList<String>();
                for (String property : allProperties) {
                    Object v = sector.get(property);
                    if (v != null) {
                        values.add(v.toString());
                    } else {
                        values.add(StringUtils.EMPTY);
                    }
                }
                writeRow(writer, values);
                monitor.worked(1);
            }

            writer.flush();
            writer.close();

            monitor.done();

        } catch (IOException e) {
            MessageDialog.openError(null, ERROR_TITLE, e.getMessage());
        }
    }

    /**
     * Create directory and file
     * 
     * @return file export file
     */
    private File createFile() {

        String directory = page.getDirectoryValue();
        File dir = new File(directory);
        dir.mkdir();

        String extensionValue = page.getExtensionValue();
        File nemoFile = new File(dir, network.getName() + extensionValue);
        try {
            nemoFile.createNewFile();
        } catch (IOException e) {
            MessageDialog.openError(null, ERROR_TITLE, e.getMessage());
        }
        return nemoFile;
    }

    /**
     * write row values in file
     * 
     * @param writer file writer
     * @throws IOException
     */
    private void writeRow(FileWriter writer, List<String> values) throws IOException {

        int i = 1;

        String separatorValue = page.getSeparatorValue();

        for (String property : values) {
            writer.append(property);
            if (i != values.size()) {
                writer.append(separatorValue);
            }
            i++;
        }
        writer.append(ROW_SEPARATOR);
    }

}
