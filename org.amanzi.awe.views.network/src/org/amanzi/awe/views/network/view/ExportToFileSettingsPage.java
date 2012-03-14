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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.Lists;

public class ExportToFileSettingsPage extends WizardPage {

    // label text
    private final static String LABEL_EXTENSION = "File expansion:";
    private final static String LABEL_SEPARATOR = "File separator:";

    // directory name
    private final static String EXPORT_FILES = "export_files";

    // default extensions
    private final static String[] DEFAULT_EXTENSION = {".csv", ".txt"};

    // default separators
    private final static String[] DEFAULT_SEPARATOR = {"\"\\" + "t\"", "\",\"", "\";\""};
    private final static String OTHER_SEPARATOR = "other:";
    private final static String[] SEPARATORS = {"\t", ",", ";"};

    // home property
    protected static final String USER_HOME = "user.home";

    // message title
    private static final String TILTE = "Export to file";

    // message text
    private static final String MESSAGE = "Export to file finished";

    // row separator
    private static final String ROW_SEPARATOR = "\n";

    // error title
    private static final String ERROR_TITLE = "Error";

    // container for groups
    private Composite container;

    // directory
    private DirectoryFieldEditor directoryEditor;

    // text for other separator
    private Text text;

    // all exist sector properties
    private List<String> properties;

    // selected network
    private INetworkModel network;

    // values
    private String extensionValue;
    private String separatorValue;

    protected ExportToFileSettingsPage(INetworkModel network) {
        super("Export to file settings");
        setDescription("Choose settings");
        this.network = network;
    }

    @Override
    public void createControl(Composite parent) {

        container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 1;

        Group groupD = new Group(container, SWT.NONE);
        GridLayout layoutGroupD = new GridLayout(1, true);
        GridData dataD = new GridData(SWT.FILL, SWT.FILL, true, true);
        dataD.widthHint = 200;
        groupD.setLayoutData(dataD);
        groupD.setLayout(layoutGroupD);

        directoryEditor = new DirectoryFieldEditor("Directory", "Choose directory:", groupD);

        Group group = new Group(container, SWT.NONE);
        GridLayout layoutGroup = new GridLayout(2, true);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.widthHint = 200;
        group.setLayoutData(data);
        group.setLayout(layoutGroup);

        Group group1 = new Group(group, SWT.FILL);

        GridLayout layoutGroup1 = new GridLayout(1, true);
        group1.setLayout(layoutGroup1);

        createRadioExtensionGroup(group1);

        Group group2 = new Group(group, SWT.FILL);

        GridLayout layoutGroup2 = new GridLayout(1, true);
        group2.setLayout(layoutGroup2);

        createRadioseparatorGroup(group2);

        // Required to avoid an error in the system
        setControl(container);

        setPageComplete(true);

    }

    /**
     * Create radio extension group
     * 
     * @param group
     */
    private void createRadioExtensionGroup(Group group) {

        boolean first = true;

        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.heightHint = 100;
        data.widthHint = 150;
        group.setLayoutData(data);

        Label label = new Label(group, SWT.NONE);
        label.setText(LABEL_EXTENSION);

        for (String ext : DEFAULT_EXTENSION) {
            final Button radio = new Button(group, SWT.RADIO);
            radio.setText(ext);
            if (first) {
                radio.setSelection(true);
                extensionValue = radio.getText();
                first = false;
            }
            radio.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    extensionValue = radio.getText();
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });
        }
    }

    /**
     * Create radio separator group
     * 
     * @param group
     */
    private void createRadioseparatorGroup(Group group) {

        boolean first = true;

        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.heightHint = 130;
        data.widthHint = 150;
        group.setLayoutData(data);

        Label label = new Label(group, SWT.NONE);
        label.setText(LABEL_SEPARATOR);

        for (String ext : DEFAULT_SEPARATOR) {
            final Button radio = new Button(group, SWT.RADIO);
            radio.setText(ext);
            if (first) {
                radio.setSelection(true);
                separatorValue = radio.getText();
                first = false;
            }
            radio.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    text.setEnabled(false);
                    separatorValue = radio.getText();
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });
        }

        final Button radio = new Button(group, SWT.RADIO);
        radio.setText(OTHER_SEPARATOR);

        radio.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setPageComplete(false);
                text.setEnabled(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        text = new Text(group, SWT.BORDER);
        text.setEnabled(false);
    }

    /**
     * export to file
     */
    protected void export() {
        // create csv file
        File csvFile = createCSVFile();

        // get all sectors
        List<IDataElement> sectors = Lists.newArrayList(network.getAllElementsByType(NetworkElementNodeType.SECTOR));

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
            }

            writer.flush();
            writer.close();

            // message dialog
            MessageDialog.openInformation(null, TILTE, MESSAGE);

        } catch (IOException e) {
            MessageDialog.openError(null, ERROR_TITLE, e.getMessage());
        }
    }

    /**
     * Create directory and file
     * 
     * @return file CSV file
     */
    private File createCSVFile() {
        File dir = new File(System.getProperty(USER_HOME) + File.separatorChar + EXPORT_FILES);
        dir.mkdir();
        File nemoFile = new File(dir, network.getName() + extensionValue);
        try {
            nemoFile.createNewFile();
        } catch (IOException e) {
            MessageDialog.openError(null, ERROR_TITLE, e.getMessage());
        }
        return nemoFile;
    }

    /**
     * write row values in CSV files
     * 
     * @param writer file writer
     * @throws IOException
     */
    private void writeRow(FileWriter writer, List<String> values) throws IOException {

        int i = 1;

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
