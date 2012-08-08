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

package org.amanzi.neo.loader.ui.page.widgets.impl.internal;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.loader.ui.internal.Messages;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectDriveResourcesWidget;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectDriveResourcesWidget.ISelectDriveResourceListener;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractPageWidget;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DriveDataFileSelector extends AbstractPageWidget<Composite, SelectDriveResourcesWidget.ISelectDriveResourceListener>
        implements
            SelectionListener {

    protected static final GridLayout FIXED_ONE_ROW_LAYOUT = new GridLayout(1, false);

    private List availableFilesList;

    private List selectedFilesList;

    private Button addAllButton;

    private Button addSelectedButton;

    private Button removeAllButton;

    private Button removeSelectedButton;

    private final Map<String, File> availableFiles = new HashMap<String, File>();

    private final Map<String, File> selectedFiles = new HashMap<String, File>();

    /**
     * @param isEnabled
     * @param parent
     * @param listener
     * @param projectModelProvider
     */
    public DriveDataFileSelector(final Composite parent, final ISelectDriveResourceListener listener) {
        super(true, parent, listener, null);
    }

    @Override
    protected Composite createWidget(final Composite parent, final int style) {
        availableFilesList = createListComposite(parent, Messages.DriveDataFileSelector_DirectoryFilesLabel);

        createActionComposite(parent);

        selectedFilesList = createListComposite(parent, Messages.DriveDataFileSelector_SelectedFilesLabel);

        return parent;
    }

    private List createListComposite(final Composite parent, final String labelText) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(FIXED_ONE_ROW_LAYOUT);
        panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label listLabel = new Label(panel, SWT.NONE);
        listLabel.setText(labelText);
        listLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        List list = new List(panel, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.minimumWidth = 200;
        gridData.minimumHeight = 300;
        list.setLayoutData(gridData);

        return list;
    }

    private void createActionComposite(final Composite parent) {
        Composite actionPanel = new Composite(parent, SWT.NONE);
        actionPanel.setLayout(FIXED_ONE_ROW_LAYOUT);
        actionPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

        addAllButton = createChooseButton(actionPanel, Messages.DriveDataFileSelector_AddAllButton);
        addSelectedButton = createChooseButton(actionPanel, Messages.DriveDataFileSelector_AddButton);
        removeSelectedButton = createChooseButton(actionPanel, Messages.DriveDataFileSelector_RemoveButton);
        removeAllButton = createChooseButton(actionPanel, Messages.DriveDataFileSelector_RemoveAllButton);
    }

    private Button createChooseButton(final Composite parent, final String label) {
        Button button = new Button(parent, SWT.NONE);
        button.setText(label);
        button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

        button.addSelectionListener(this);

        return button;
    }

    private void transferFiles(final String[] fileNames, final Map<String, File> from, final Map<String, File> to) {
        for (String fileName : fileNames) {
            File file = from.remove(fileName);

            to.put(fileName, file);
        }

        updateLists();
    }

    public void setFiles(final File directory) {
        assert directory.isDirectory();

        IOFileFilter csvFilter = FileFilterUtils.suffixFileFilter(".csv");
        IOFileFilter fmtFilter = FileFilterUtils.suffixFileFilter("*.fmt");
        FileFilter filter = FileFilterUtils.orFileFilter(csvFilter, fmtFilter);

        for (File file : directory.listFiles(filter)) {
            if (file.isFile()) {
                availableFiles.put(file.getName(), file);
            }
        }

        updateLists();
    }

    private void updateLists() {
        updateList(availableFilesList, availableFiles);
        updateList(selectedFilesList, selectedFiles);
    }

    private void updateList(final List list, final Map<String, File> values) {
        list.removeAll();

        for (String fileName : values.keySet()) {
            list.add(fileName);
        }
    }

    @Override
    protected int getStyle() {
        return SWT.FILL;
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        Map<String, File> from = null;
        Map<String, File> to = null;
        String[] items = null;

        if (e.getSource().equals(addAllButton)) {
            items = availableFilesList.getItems();
            from = availableFiles;
            to = selectedFiles;
        } else if (e.getSource().equals(addSelectedButton)) {
            items = availableFilesList.getSelection();
            from = availableFiles;
            to = selectedFiles;
        } else if (e.getSource().equals(removeAllButton)) {
            items = selectedFilesList.getItems();
            from = selectedFiles;
            to = availableFiles;
        } else if (e.getSource().equals(removeSelectedButton)) {
            items = selectedFilesList.getSelection();
            from = selectedFiles;
            to = availableFiles;
        }

        if ((from != null) && (to != null) && (items != null)) {
            transferFiles(items, from, to);

            fireFilesChangedEvent();
        }
    }

    private void fireFilesChangedEvent() {
        for (ISelectDriveResourceListener listener : getListeners()) {
            listener.onResourcesSelected(selectedFiles.values());
        }
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        widgetSelected(e);
    }

}
