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
import org.amanzi.neo.services.ui.utils.AbstractDialog;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.google.common.collect.Lists;

/**
 * <p>
 * Dialog for export to CSV settings
 * </p>
 * 
 * @author ladornaya_a
 * @since 1.0.0
 */
public class ExportToFileSettings extends AbstractDialog<Integer> {

    // label text
    private final static String LABEL_EXTENSION = "File expansion:";
    private final static String LABEL_SEPARATOR = "File separator:";

    // directory name
    private final static String EXPORT_FILES = "export_files";

    // default extensions
    private final static String[] DEFAULT_EXTENSION = {".csv", ".txt"};

    // default separators
    private final static String[] DEFAULT_SEPARATOR = {"\"\\" + "t\"", "\",\"", "\";\""};
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

    /** The b ok. */
    private Button bOk;

    /** The b cancel. */
    private Button bCancel;

    /** The shell. */
    private Shell shell;

    // all exist sector properties
    private List<String> properties;

    // selected network
    private INetworkModel network;

    // values
    private String extensionValue;
    private String separatorValue;

    public ExportToFileSettings(Shell parent, INetworkModel network, String title, int style) {
        super(parent, title, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        status = SWT.CANCEL;
        this.network = network;
    }

    @Override
    protected void createContents(Shell shell) {

        this.shell = shell;

        shell.setLayout(new GridLayout(1, true));

        container = new Composite(shell, SWT.FILL);
        GridLayout layout = new GridLayout(1, true);
        container.setLayout(layout);

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

        bOk = new Button(group, SWT.PUSH);
        bOk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        bOk.setText("OK");
        bCancel = new Button(group, SWT.PUSH);
        bCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        bCancel.setText("Cancel");
        addListeners();

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
        data.heightHint = 100;
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
                    separatorValue = radio.getText();
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });
        }
    }

    /**
     * Adds the listeners.
     */
    private void addListeners() {
        bOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.OK;
                int j = 0;
                for (String sep : DEFAULT_SEPARATOR) {
                    if (sep.equals(separatorValue)) {
                        separatorValue = SEPARATORS[j];
                    }
                    j++;
                }
                export();
                shell.close();
            }
        });
        bCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.CANCEL;
                shell.close();
            }
        });
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
