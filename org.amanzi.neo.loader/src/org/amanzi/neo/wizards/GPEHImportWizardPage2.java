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

package org.amanzi.neo.wizards;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.amanzi.neo.core.enums.gpeh.Events;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * <p>
 * GPEH loader second page
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class GPEHImportWizardPage2 extends WizardPage {
    private static final Logger LOGGER = Logger.getLogger(GPEHImportWizardPage2.class);

    private TableViewer optionsViewer;
    private TableViewer eventsViewer;
    // TODO hardcoding!!!
    private final String[] loadOptions = new String[] {"Locations", "Measurement Reports"};
    private List<String> events = new ArrayList<String>();

    private HashSet<Integer> selectedEvents = new HashSet<Integer>();

    /**
     * @param pageName
     */
    protected GPEHImportWizardPage2(String pageName) {
        super(pageName);
        setTitle(NeoLoaderPluginMessages.GpehOptionsTitle);
        setDescription(NeoLoaderPluginMessages.GpehOptionsDescr);

    }

    @Override
    public void createControl(Composite parent) {
        final Composite main = new Composite(parent, SWT.FILL);

        Group mainFrame = new Group(main, SWT.FILL);
        mainFrame.setText("GPEH loading options");
        GridLayout mainLayout = new GridLayout(2, false);
        mainFrame.setLayout(mainLayout);

        optionsViewer = new TableViewer(mainFrame, SWT.BORDER | SWT.FULL_SELECTION);
        createTable(optionsViewer, "Options");
        optionsViewer.setContentProvider(new OptionsTableContentProvider());
        optionsViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 10));

        optionsViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                optionChangeged();
            }
        });

        eventsViewer = new TableViewer(mainFrame, SWT.BORDER | SWT.FULL_SELECTION);
        createTable(eventsViewer, "Events");
        eventsViewer.setContentProvider(new EventsTableContentProvider());
        eventsViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 10));

        eventsViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                eventChanged();
            }
        });

        main.setLayout(new GridLayout(1, false));

        optionsViewer.setInput("");
        eventsViewer.setInput("");

        setControl(main);
        setDefaults();
        // validateFinish();
    }

    /**
     *
     */
    protected void eventChanged() {
        StructuredSelection sel = (StructuredSelection)eventsViewer.getSelection();
        Events e = Events.valueOf((String)sel.getFirstElement());
        selectedEvents.clear();
        selectedEvents.add(e.getId());
        validateFinish();
    }

    /**
     *
     */
    private void setDefaults() {
        optionsViewer.getTable().select(0);
        optionChangeged();
        eventsViewer.getTable().select(0);
        eventChanged();
    }

    private void optionChangeged() {
        events.clear();
        // TODO hardcoding!!!
        if (optionsViewer.getSelection().isEmpty())
            return;
        StructuredSelection sel = (StructuredSelection)optionsViewer.getSelection();
        if (sel.getFirstElement().equals(loadOptions[0])) {
            events.add(Events.findById(426).name());
        } else {
            events.add(Events.RRC_MEASUREMENT_REPORT.name());
            events.add(Events.NBAP_DEDICATED_MEASUREMENT_REPORT.name());
            events.add(Events.NBAP_COMMON_MEASUREMENT_REPORT.name());
            events.add(Events.RANAP_LOCATION_REPORT.name());
            events.add(Events.RNSAP_DEDICATED_MEASUREMENT_REPORT.name());
        }

        eventsViewer.refresh();
    }

    /**
     * Create table
     * 
     * @param tableView table
     * @param columnName name of column
     */
    private void createTable(TableViewer tableView, String columnName) {
        Table table = tableView.getTable();
        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setWidth(200);
        column.setText(columnName);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
    }

    /**
     *check correct input
     */
    private void validateFinish() {
        // setPageComplete(false);
        setPageComplete(isValidPage());
    }

    /**
     * validate page
     * 
     * @return
     */
    protected boolean isValidPage() {
        return !eventsViewer.getSelection().isEmpty();
    }

    /*
     * The content provider class is responsible for providing objects to the view. It can wrap
     * existing objects in adapters or simply return objects as-is. These objects may be sensitive
     * to the current input of the view, or ignore it and always show the same content (like Taskc
     * List, for example).
     */

    private class OptionsTableContentProvider implements IStructuredContentProvider {

        @Override
        public Object[] getElements(Object inputElement) {
            return loadOptions;
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    /*
     * The content provider class is responsible for providing objects to the view. It can wrap
     * existing objects in adapters or simply return objects as-is. These objects may be sensitive
     * to the current input of the view, or ignore it and always show the same content (like Taskc
     * List, for example).
     */

    private class EventsTableContentProvider implements IStructuredContentProvider {

        @Override
        public Object[] getElements(Object inputElement) {
            return events.toArray(new String[0]);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    /**
     * @return
     */
    public Set<Integer> getSelectedEvents() {
        return selectedEvents;
    }

}
