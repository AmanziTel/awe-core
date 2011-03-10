package org.amanzi.awe.afp.wizards;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.awe.afp.models.AfpModel.ScalingFactors;
import org.amanzi.neo.services.node2node.NodeToNodeTypes;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class AfpScalingRulesPage extends AfpWizardPage implements Listener {

    private static final int textWidth = 25;
    private static final int sepTextWidth = 35;
    // Indentation between cells in Interference Matrix
    private static final int imIndent = 10;

    private Map<NodeToNodeTypes, Map<String, Text[][]>> intTexts = new HashMap<NodeToNodeTypes, Map<String, Text[][]>>();
    private Text[][] sepTexts = new Text[9][2];
    private ScrolledComposite c1;

    public AfpScalingRulesPage(String pageName, AfpModel model, String desc) {
        super(pageName, model);
        setTitle(AfpImportWizard.title);
        setDescription(desc);
        setPageComplete(false);

    }

    @Override
    public void createControl(Composite parent) {

        Composite thisParent = new Composite(parent, SWT.NONE);
        thisParent.setLayout(new GridLayout(2, false));

        Group stepsGroup = AfpWizardUtils.getStepsGroup(thisParent, 6);

		TabFolder tabFolder = new TabFolder(thisParent, SWT.NONE | SWT.BORDER);
        tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        TabItem item1 = new TabItem(tabFolder, SWT.NONE);
        item1.setText("Separations");

        Group separationsGroup = new Group(tabFolder, SWT.NONE);
        separationsGroup.setLayout(new GridLayout(4, false));
        separationsGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));

        Label servingLabel = new Label(separationsGroup, SWT.LEFT);
        servingLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
        servingLabel.setText("Serving");
        AfpWizardUtils.makeFontBold(servingLabel);

        Label interferingLabel = new Label(separationsGroup, SWT.LEFT);
        interferingLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
        interferingLabel.setText("Interfering");
        AfpWizardUtils.makeFontBold(interferingLabel);

        Label sectorLabel = new Label(separationsGroup, SWT.LEFT);
        sectorLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
        sectorLabel.setText("Sector");
        AfpWizardUtils.makeFontBold(sectorLabel);

        Label siteLabel = new Label(separationsGroup, SWT.LEFT);
        siteLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
        siteLabel.setText("Site");
        AfpWizardUtils.makeFontBold(siteLabel);

        float[][] separationRules = new float[][] {model.getSectorSeparation(), model.getSiteSeparation()};

        for (int i = 0; i < sepTexts.length; i++) {
            new Label(separationsGroup, SWT.LEFT).setText(AfpModel.SCALING_PAGE_ROW_HEADERS[i][0]);
            new Label(separationsGroup, SWT.LEFT).setText(AfpModel.SCALING_PAGE_ROW_HEADERS[i][1]);
            for (int j = 0; j < sepTexts[i].length; j++) {
                GridData gridData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
                gridData.widthHint = sepTextWidth;
                sepTexts[i][j] = new Text(separationsGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
                sepTexts[i][j].setLayoutData(gridData);
                sepTexts[i][j].setText(Float.toString(separationRules[j][i]));
                sepTexts[i][j].addListener(SWT.Modify, this);
            }

        }

        item1.setControl(separationsGroup);

        TabItem item2 = new TabItem(tabFolder, SWT.NONE);
        item2.setText("Interference Matrices");

        Group interferenceGroup = new Group(tabFolder, SWT.NONE);
        interferenceGroup.setLayout(new GridLayout(10, false));
        interferenceGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));

        String headers[] = {""};
        for (String item : headers) {
            Label headerLabel = new Label(interferenceGroup, SWT.LEFT);
            headerLabel.setLayoutData(new GridData(GridData.FILL, GridData.END, false, false, 2, 1));
            headerLabel.setText(item);
            AfpWizardUtils.makeFontBold(headerLabel);
        }
        c1 = new ScrolledComposite(interferenceGroup, SWT.H_SCROLL);
        c1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 8, 13));
        for (String item : headers) {
            Label headerLabel = new Label(interferenceGroup, SWT.LEFT);
            headerLabel.setLayoutData(new GridData(GridData.FILL, GridData.END, false, false, 2, 1));
            headerLabel.setText(item);
            AfpWizardUtils.makeFontBold(headerLabel);
        }
        String headers2[] = {"Serving", "Interfering"};// , "Co", "Adj", "Co", "Adj", "Co", "Adj",
                                                       // "Co", "Adj"};
        for (String item : headers2) {
            GridData gridData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1);
            Label headerLabel = new Label(interferenceGroup, SWT.CENTER);
            if (item.equals("Serving")) {
                gridData.horizontalAlignment = GridData.BEGINNING | GridData.FILL;
            }
            headerLabel.setLayoutData(gridData);
            headerLabel.setText(item);
            AfpWizardUtils.makeFontBold(headerLabel);
        }
        // TODO
        float[][] scalingRules = new float[10][10];
        // {
        // model.getCoInterference(),
        // model.getAdjInterference(),
        // model.getCoNeighbor(),
        // model.getAdjNeighbor(),
        // model.getCoTriangulation(),
        // model.getAdjTriangulation(),
        // model.getCoShadowing(),
        // model.getAdjShadowing()
        // };

        for (int i = 0; i < 9; i++) {
            Label label = new Label(interferenceGroup, SWT.LEFT);
            label.setText(AfpModel.SCALING_PAGE_ROW_HEADERS[i][0]);
            GridData layoutData = new GridData();
            layoutData.heightHint = 20;
            label.setLayoutData(layoutData);
            label = new Label(interferenceGroup, SWT.LEFT);
            layoutData = new GridData();
            layoutData.heightHint = 20;
            label.setLayoutData(layoutData);
            label.setText(AfpModel.SCALING_PAGE_ROW_HEADERS[i][1]);
            // for (int j = 0; j < intTexts[i].length; j++){
            // GridData gridData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
            // gridData.widthHint = textWidth;
            // if ((j != 0) && ((j % 2) == 0))
            // gridData.horizontalIndent = imIndent;
            // intTexts[i][j] = new Text (interferenceGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
            // intTexts[i][j].setLayoutData(gridData);
            // intTexts[i][j].setText(Float.toString(scalingRules[j][i]));
            // intTexts[i][j].addListener(SWT.Modify, this);
            // }

        }

        item2.setControl(interferenceGroup);

        setPageComplete(true);
        setControl(thisParent);
    }

    /**
     * @param type
     * @return
     */
    private String getTypeDescr(NodeToNodeTypes type) {
        switch (type) {
        case NEIGHBOURS:
            return "Neighbor";
        case INTERFERENCE_MATRIX:
            return "Interference";
        case TRIANGULATION:
            return "Triangulation";
        case SHADOWING:
            return "Shadowing";
        default:
            return type.name();
        }
    }

    @Override
    public void handleEvent(Event event) {
        setErrorMessage(null);
        setPageComplete(true);
        float[] siteSeparation = new float[sepTexts.length];
        float[] sectorSeparation = new float[sepTexts.length];
        // Construct an array of all interference matrix arrays

        try {
            for (int i = 0; i < sepTexts.length; i++) {
                float val = Float.parseFloat(sepTexts[i][0].getText());
                if (val < 0 || val > 100)
                    showError();
                sectorSeparation[i] = val;
            }

            for (int i = 0; i < sepTexts.length; i++) {
                float val = Float.parseFloat(sepTexts[i][1].getText());
                if (val < 0 || val > 100)
                    showError();
                siteSeparation[i] = val;
            }
            for (Entry<NodeToNodeTypes, Map<String, Text[][]>> entry : intTexts.entrySet()) {
                for (Entry<String, Text[][]> entry2 : entry.getValue().entrySet()) {
                    float[] co = new float[entry2.getValue()[0].length];
                    float[] adj = new float[entry2.getValue()[0].length];
                    for (int i = 0; i < co.length; i++) {
                        float val = Float.parseFloat(entry2.getValue()[0][i].getText());
                        if (val < 0 || val > 100) {
                            showError();
                            return;
                        }
                        co[i] = val;
                        val = Float.parseFloat(entry2.getValue()[1][i].getText());
                        if (val < 0 || val > 100) {
                            showError();
                            return;
                        }
                        adj[i] = val;
                    }
                    ScalingFactors fs = model.findScalingFactor(entry.getKey(), entry2.getKey());
                    fs.setCo(co);
                    fs.setAdj(adj);
                }
            }

        } catch (NumberFormatException e) {
            setErrorMessage("Only numeric values between 1 and 100 with step size of 0.1 are allowed");
        }

        model.setSiteSeparation(siteSeparation);
        model.setSectorSeparation(sectorSeparation);
        // TODO
        // model.setInterferenceMatrixArrays(interferenceArray);
    }

    public void showError() {
        setErrorMessage("Only numeric values between 1 and 100 with step size of 0.1 are allowed");
        setPageComplete(false);
    }

    @Override
    public void refreshPage() {
        if (c1.getContent() != null) {
            return;
        }
        Composite tbl = new Composite(c1, SWT.NONE);
        final GridLayout tblLayout = new GridLayout();
        tblLayout.makeColumnsEqualWidth = false;
        tbl.setLayout(tblLayout);
        int clCount = 0;
        for (NodeToNodeTypes type : new NodeToNodeTypes[] {NodeToNodeTypes.INTERFERENCE_MATRIX, NodeToNodeTypes.NEIGHBOURS, NodeToNodeTypes.TRIANGULATION,
                NodeToNodeTypes.SHADOWING}) {
            final Set<String> lists = model.getLists(type);
            for (String listName : lists) {

                clCount += 2;
                GridData gridData = new GridData(GridData.FILL, GridData.END, false, false, 2, 1);
                Label headerLabel = new Label(tbl, SWT.CENTER);
                headerLabel.setLayoutData(gridData);
                headerLabel.setText(getTypeDescr(type));
                AfpWizardUtils.makeFontBold(headerLabel);
            }
        }
        for (NodeToNodeTypes type : new NodeToNodeTypes[] {NodeToNodeTypes.INTERFERENCE_MATRIX, NodeToNodeTypes.NEIGHBOURS, NodeToNodeTypes.TRIANGULATION,
                NodeToNodeTypes.SHADOWING}) {
            final Set<String> lists = model.getLists(type);
            for (String listName : lists) {
                GridData gridData = new GridData(GridData.FILL, GridData.END, false, false, 2, 1);
                Label headerLabel = new Label(tbl, SWT.CENTER);
                headerLabel.setLayoutData(gridData);
                String name = listName.length() < 14 ? listName : listName.substring(0, 11) + "...";
                headerLabel.setText(name);
                headerLabel.setToolTipText(listName);
            }
        }
        for (NodeToNodeTypes type : new NodeToNodeTypes[] {NodeToNodeTypes.INTERFERENCE_MATRIX, NodeToNodeTypes.NEIGHBOURS, NodeToNodeTypes.TRIANGULATION,
                NodeToNodeTypes.SHADOWING}) {
            final Set<String> lists = model.getLists(type);
            for (String listName : lists) {
                GridData gridData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1);
                Label headerLabel = new Label(tbl, SWT.CENTER);
                headerLabel.setLayoutData(gridData);
                headerLabel.setText("Co");
                AfpWizardUtils.makeFontBold(headerLabel);
                gridData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1);
                headerLabel = new Label(tbl, SWT.CENTER);
                headerLabel.setLayoutData(gridData);
                headerLabel.setText("Adj");
                AfpWizardUtils.makeFontBold(headerLabel);
            }
        }
        for (int i = 0; i < 9; i++) {
            int j = 0;
            for (NodeToNodeTypes type : new NodeToNodeTypes[] {NodeToNodeTypes.INTERFERENCE_MATRIX, NodeToNodeTypes.NEIGHBOURS, NodeToNodeTypes.TRIANGULATION,
                    NodeToNodeTypes.SHADOWING}) {
                Map<String, Text[][]> map = intTexts.get(type);
                if (map == null) {
                    map = new HashMap<String, Text[][]>();
                    intTexts.put(type, map);
                }
                final Set<String> lists = model.getLists(type);
                for (String listName : lists) {
                    Text[][] arr = map.get(listName);
                    if (arr == null) {
                        arr = new Text[2][9];
                        map.put(listName, arr);
                    }
                    ScalingFactors factor = model.findScalingFactor(type, listName);

                    GridData gridData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
                    gridData.widthHint = textWidth;
                    if (j != 0) {
                        gridData.horizontalIndent = imIndent;
                    }
                    arr[0][i] = new Text(tbl, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
                    arr[0][i].setLayoutData(gridData);
                    arr[0][i].setText(Float.toString(factor.getCo()[i]));
                    arr[0][i].addListener(SWT.Modify, this);
                    gridData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
                    gridData.widthHint = textWidth;

                    arr[1][i] = new Text(tbl, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
                    arr[1][i].setLayoutData(gridData);
                    arr[1][i].setText(Float.toString(factor.getCo()[i]));
                    arr[1][i].addListener(SWT.Modify, this);
                    j = 1;
                }
            }
        }
        tblLayout.numColumns = clCount;
        tbl.pack();
        c1.setContent(tbl);
    }
}
