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
package org.amanzi.awe.views.kpi;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.core.utils.PropertyHeader;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.jruby.Ruby;
import org.jruby.parser.EvalStaticScope;
import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.scope.ManyVarsDynamicScope;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.Transaction;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.internal.ui.wizards.NewRubyElementCreationWizard;



/**
 * <p>
 * Kpi builder view
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class KpiView extends ViewPart {

    /** String TEST_TITLE field */
    private static final String TEST_TITLE = "Parsing script";

    /** String TITLE field */
    private static final String TITLE = "Execution Result";

    /**
     * The ID of the view as specified by the extension.
     */
	public static final String ID = "org.amanzi.awe.views.kpi.KpiView";

    private static final String B_RUN = "Run";

    private static final String B_TEST = "Test";

    private static final String TEST_ERROR = "Script has errors:\n%s";

    private static final String TEST_NO_ERR = "Script parsing has passed successfully";

    private static final String JRUBY_SCRIPT = "formula.rb";

    private static final String LB_NETWORK = "Network:";

    private static final String LB_DRIVE = "Drive:";

    private static final String B_SAVE = "Save";

    private static final String B_INIT = "Init";

    private static final String DIALOG_TITLE = "Open Ruby Script file";

    private static final String[] RUBY_FILE_NAMES = new String[] {"Ruby files (*.rb"};
    private static final String[] RUBY_FILE_EXT = new String[] {"*.rb"};

    private Text editor;

    private Button bRun;

    private Button bTest;

    private List formulaList;

    private Combo networkNode;

    private Combo driveNode;

    private List propertyList;

    private LinkedHashMap<String, Node> networks = new LinkedHashMap<String, Node>();

    private LinkedHashMap<String, Node> drives = new LinkedHashMap<String, Node>();

    private Button bSave;

    private Button bInit;

	private String INIT_SCRIPT="init.rb";
	
	private String fileName="";
	
	private String formulaName="";
	
	private String parameters="";

	/**
	 * The constructor.
	 */
	public KpiView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
        Composite frame = new Composite(parent, SWT.NONE);
        FormLayout mainLayout = new FormLayout();
        frame.setLayout(mainLayout);
        Composite top = new Composite(frame, SWT.NONE);

        Composite bottom = new Composite(frame, SWT.NONE);
        Composite right = new Composite(frame, SWT.NONE);
        editor = new Text(frame, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        bRun = new Button(bottom, SWT.PUSH);
        bRun.setText(B_RUN);
        bRun.setImage(KPIPlugin.getImageDescriptor("icons/run.gif").createImage());
        bTest = new Button(bottom, SWT.PUSH);
        bTest.setText(B_TEST);
        bTest.setImage(KPIPlugin.getImageDescriptor("icons/test.gif").createImage());
        bSave = new Button(bottom, SWT.PUSH);
        bSave.setText(B_SAVE);
        bSave.setImage(KPIPlugin.getImageDescriptor("icons/save.png").createImage());
        bInit = new Button(bottom, SWT.PUSH);
        bInit.setText(B_INIT);
        bInit.setImage(KPIPlugin.getImageDescriptor("icons/init.png").createImage());
        Label labelNetwork = new Label(top, SWT.LEFT);
        labelNetwork.setText(LB_NETWORK);
        networkNode = new Combo(top, SWT.DROP_DOWN | SWT.READ_ONLY);
        Label labelDrive = new Label(top, SWT.LEFT);
        labelDrive.setText(LB_DRIVE);
        driveNode = new Combo(top, SWT.DROP_DOWN | SWT.READ_ONLY);
        // top
        FormData layoutData = new FormData();
        layoutData.top = new FormAttachment(0, 2);
        layoutData.left = new FormAttachment(0, 2);
        layoutData.right = new FormAttachment(right, -2);
        // layoutData.bottom = new FormAttachment(networkNode, );
        top.setLayoutData(layoutData);
        top.setLayout(new FormLayout());

        layoutData = new FormData();
        layoutData.top = new FormAttachment(networkNode, 5, SWT.CENTER);
        layoutData.left = new FormAttachment(0, 2);
        labelNetwork.setLayoutData(layoutData);

        layoutData = new FormData();
        layoutData.top = new FormAttachment(0, 2);
        layoutData.left = new FormAttachment(labelNetwork, 5);
        layoutData.right = new FormAttachment(50, -5);
        networkNode.setLayoutData(layoutData);

        layoutData = new FormData();
        layoutData.top = new FormAttachment(driveNode, 5, SWT.CENTER);
        layoutData.left = new FormAttachment(networkNode, 10);
        labelDrive.setLayoutData(layoutData);

        layoutData = new FormData();
        layoutData.top = new FormAttachment(0, 2);
        layoutData.left = new FormAttachment(labelDrive, 5);
        layoutData.right = new FormAttachment(100, -5);
        driveNode.setLayoutData(layoutData);

        // bottom
        layoutData = new FormData();
        layoutData.bottom = new FormAttachment(100, -2);
        layoutData.left = new FormAttachment(0, 2);
        layoutData.right = new FormAttachment(right, -2);
        // layoutData.top = new FormAttachment(100, -50);
        bottom.setLayoutData(layoutData);

        // right
        layoutData = new FormData();
        layoutData.top = new FormAttachment(0, 2);
        layoutData.bottom = new FormAttachment(100, -2);
        layoutData.left = new FormAttachment(60, 2);
        layoutData.right = new FormAttachment(100, -2);
        layoutData.right = new FormAttachment(100, -2);
        right.setLayoutData(layoutData);

        // editor
        layoutData = new FormData();
        layoutData.left = new FormAttachment(0, 2);
        layoutData.top = new FormAttachment(top, 2);
        layoutData.right = new FormAttachment(right, -2);
        layoutData.bottom = new FormAttachment(bottom, -2);
        editor.setLayoutData(layoutData);

        //buttons
        bottom.setLayout(new GridLayout(4, true));

        // bRun.setLayoutData(layoutData);
        //
        // layoutData = new FormData();
        // layoutData.left = new FormAttachment(60);
        // layoutData.right = new FormAttachment(90);
        // layoutData.bottom = new FormAttachment(100, -2);
        // bTest.setLayoutData(layoutData);

        right.setLayout(new GridLayout(2, true));
        // right
        Label label = new Label(right, SWT.CENTER);
        label.setText("Formulas:");
        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
        label = new Label(right, SWT.CENTER);
        label.setText("Properties:");
        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

        formulaList = new List(right, SWT.BORDER | SWT.V_SCROLL);

        GridData layoutDataPr = new GridData(GridData.FILL_VERTICAL | GridData.GRAB_VERTICAL | GridData.FILL_HORIZONTAL);
        layoutDataPr.horizontalSpan = 1;
        formulaList.setLayoutData(layoutDataPr);
        layoutDataPr = new GridData(GridData.FILL_VERTICAL | GridData.GRAB_VERTICAL | GridData.FILL_HORIZONTAL);
        layoutDataPr.horizontalSpan = 1;
        propertyList = new List(right, SWT.BORDER | SWT.V_SCROLL);
        propertyList.setLayoutData(layoutDataPr);
        // TODO implement save function
        bSave.setVisible(true);
        fillList();
        addListeners();

    }

    /**
     *
     */
    private void fillList() {
        fillFormulas();
        networkNode.setItems(getAllNetworks());
        driveNode.setItems(getAllDrive());

    }

    /**
     *
     */
    private void fillFormulas() {
        formulaList.setItems(getAllFormulas());
    }

    /**
     * @return
     */
    private String[] getAllDrive() {
        Transaction tx = NeoUtils.beginTransaction();
        try {
            NeoService service = NeoServiceProvider.getProvider().getService();
            Node refNode = service.getReferenceNode();
            drives = new LinkedHashMap<String, Node>();
            for (Relationship relationship : refNode.getRelationships(Direction.OUTGOING)) {
                Node node = relationship.getEndNode();
                Object type = node.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, "");
                if (node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME)
                        && node.hasProperty(INeoConstants.PROPERTY_NAME_NAME)
                        && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).toString().equalsIgnoreCase(
                                INeoConstants.GIS_TYPE_NAME) && GisTypes.DRIVE.getHeader().equals(type)) {
                    String id = node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
                    drives.put(id, node);
                }
            }
            return drives.keySet().toArray(new String[] {});
        } finally {
            tx.finish();
        }
    }

    /**
     * @return
     */
    private String[] getAllNetworks() {
        Transaction tx = NeoUtils.beginTransaction();
        try {
            NeoService service = NeoServiceProvider.getProvider().getService();
            Node refNode = service.getReferenceNode();
            networks = new LinkedHashMap<String, Node>();
            for (Relationship relationship : refNode.getRelationships(Direction.OUTGOING)) {
                Node node = relationship.getEndNode();
                Object type = node.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, "");
                if (node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME)
                        && node.hasProperty(INeoConstants.PROPERTY_NAME_NAME)
                        && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).toString().equalsIgnoreCase(
                                INeoConstants.GIS_TYPE_NAME) && GisTypes.NETWORK.getHeader().equals(type)) {
                    String id = node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
                    networks.put(id, node);
                }
            }
            return networks.keySet().toArray(new String[] {});
        } finally {
            tx.finish();
        }
    }

    /**
     * get List of all formula
     * 
     * @return String[]
     */
    private String[] getAllFormulas() {
        try {
            ArrayList<String> result = new ArrayList<String>();
            Ruby rubyRuntime = KPIPlugin.getDefault().getRubyRuntime();
            IRubyObject formula = rubyRuntime.evalScriptlet(getFormulaScript());
            Object[] array = formula.convertToArray().toArray();
            for (Object met : array) {
                result.add(met.toString());
            }
            return result.toArray(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initRubyScript() {
        final FileDialog dlg = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        dlg.setText(DIALOG_TITLE);
        dlg.setFilterNames(RUBY_FILE_NAMES);
        dlg.setFilterExtensions(RUBY_FILE_EXT);
        dlg.setFilterPath(KPIPlugin.getDefault().getDirectory());
        final String filename = dlg.open();
        if (filename != null) {
            String script = NeoSplashUtil.getScriptContent(filename);
            testInitScriptAndRun(script);
        }
    }

	private void testInitScriptAndRun(String script) {
		try {
			Pair<Boolean, Exception> parseResult = testScript(script);
			if (parseResult.getLeft()) {
			    KPIPlugin.getDefault().getRubyRuntime().evalScriptlet(script);
			    fillFormulas();
			    System.out.println("fillFormulas");
			} else {
			    testError(parseResult.getRight().getLocalizedMessage());
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


    /**
     * get script content for "formula.rb"
     * 
     * @return script content
     */
    private String getFormulaScript() {
        return "allFormula";
//        URL scriptURL;
//        try {
//            scriptURL = FileLocator.toFileURL(KPIPlugin.getDefault().getBundle().getEntry(JRUBY_SCRIPT));
//        } catch (IOException e) {
//            return null;
//        }
//        return NeoSplashUtil.getScriptContent(scriptURL.getPath());
    }

    /**
     *add listener
     */
    private void addListeners() {
        bRun.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                runScript();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bTest.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                testScript();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        formulaList.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
            }

            @Override
            public void mouseDown(MouseEvent e) {
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                insertFormula();
            }
        });
        propertyList.addMouseListener(new MouseListener() {
            
            @Override
            public void mouseUp(MouseEvent e) {
            }
            
            @Override
            public void mouseDown(MouseEvent e) {
            }
            
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                insertProperty();
            }
        });
        SelectionListener listener = new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                formPropertyList();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        };
        networkNode.addSelectionListener(listener);
        driveNode.addSelectionListener(listener);
        bInit.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                initRubyScript();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        bSave.addSelectionListener(new SelectionAdapter(){

			private static final String KPI_FOLDER = "kpi";

			@Override
			public void widgetSelected(SelectionEvent e) {
                try {
                    String scriptText = getScriptText();
                    if (scriptText != null && scriptText.length() != 0) {
                        Shell shell = getSite().getWorkbenchWindow().getShell();

                        SaveFormulaDialog dialog = new SaveFormulaDialog(shell, "Save formula");
                        dialog.setFormulaText(scriptText);
                        dialog.setFileName(fileName);
                        dialog.setFormulaName(formulaName);
                        dialog.setParameters(parameters);
                        if (dialog.open() != null) {
                            fileName = dialog.getFileName();
                            formulaName = dialog.getFormulaName();
                            parameters = dialog.getParameters();
                            System.out.println(fileName);
                            System.out.println(formulaName);
                            System.out.println(parameters);

                            String aweProjectName = AWEProjectManager.getActiveProjectName();
                            IRubyProject rubyProject = NewRubyElementCreationWizard.configureRubyProject(null, aweProjectName);

                            IPath rubyProjectPath = rubyProject.getProject().getFullPath();
                            String location = rubyProject.getResource().getLocation().toOSString();
                            System.out.println("[DEBUG] rubyProjectPath " + rubyProjectPath);
                            System.out.println("[DEBUG] location " + location);

                            IFolder folder = rubyProject.getProject().getFolder(new Path(KPI_FOLDER));
                            System.out.println("[DEBUG] folder.exists()? " + folder.exists());

                            if (!folder.exists()) {
                                folder.create(false, true, null);
                            }
                            // update or insert formula to file specified
                            String methodText = KPIUtils.generateRubyMethod(formulaName, parameters, scriptText, "  ");
                            IFile formulaScript = folder.getFile(fileName);
                            StringBuffer sb = new StringBuffer();
                            if (!formulaScript.exists()) {
                                sb.append("module KpiBuilder\nend");
                                ByteArrayInputStream is = createOrUpdateFormulaScript(sb, methodText);
                                formulaScript.create(is, true, null);
                                is.close();
                                System.out.println("Formula script was created");
                            } else {
                                KPIUtils.readContentToStringBuffer(formulaScript.getContents(), sb);
                                ByteArrayInputStream is = createOrUpdateFormulaScript(sb, methodText);
                                formulaScript.setContents(is, IFile.FORCE, null);
                            }
                            //update init script
                            IFile initScript = folder.getFile(INIT_SCRIPT);
                            sb = new StringBuffer();
                            if (!initScript.exists()) {
                                ByteArrayInputStream is = createOrUpdateInitScript(formulaScript.getLocation().toOSString(), sb);
                                initScript.create(is, true, null);
                                is.close();
                                testInitScriptAndRun(sb.toString());
                                System.out.println("Init script was created");
                            } else {
                                KPIUtils.readContentToStringBuffer(initScript.getContents(), sb);
                                ByteArrayInputStream is = createOrUpdateInitScript(formulaScript.getLocation().toOSString(), sb);
                                initScript.setContents(is, IFile.FORCE, null);
                                testInitScriptAndRun(sb.toString());

                            }
                        } else {
                            System.out.println("saving was cancelled");
                        }
                    }
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }

			private ByteArrayInputStream createOrUpdateFormulaScript(
					StringBuffer sb, String methodText) {
				String newScript = KPIUtils.insertOrUpdateRubyMethod(sb.toString(), methodText);
				ByteArrayInputStream is = new ByteArrayInputStream(newScript.getBytes());
				return is;
			}

			private ByteArrayInputStream createOrUpdateInitScript(String fileName,
					StringBuffer sb) {
				appendLoadDirective(fileName, sb);
				ByteArrayInputStream is = new ByteArrayInputStream(sb.toString().getBytes());
				return is;
			}

			private void appendLoadDirective(String fileName, StringBuffer sb) {
				String loadLine = new StringBuffer("load '").append(fileName).append("'\n").toString();
				if (sb.indexOf(loadLine)<0){
					sb.append(loadLine);
				}
			}
        	
        });
    }

    /**
     *
     */
    protected void insertProperty() {
        String[] selection = propertyList.getSelection();
        if (selection.length != 1) {
            return;
        }
        String formula = selection[0];
        StringBuilder builder = new StringBuilder(editor.getText());
        int position = editor.getCaretPosition();
        builder.insert(position, formula);
        editor.setText(builder.toString());
        editor.setSelection(position + formula.length() + 1);
        editor.setFocus();
    }

    /**
     *
     */
    protected void formPropertyList() {
        Node netNode = networks.get(networkNode.getText());
        Long networkId = netNode == null ? null : netNode.getId();
        KPIPlugin.getDefault().setNetworkId(networkId);
        Node drivNode = drives.get(driveNode.getText());
        Long driveId = drivNode == null ? null : drivNode.getId();
        KPIPlugin.getDefault().setDriveId(driveId);
        runScript("init");
        fillPropertyListList();
    }

    /**
     *
     */
    private void fillPropertyListList() {
        Transaction tx = NeoUtils.beginTransaction();
        try {
            ArrayList<String> result = new ArrayList<String>();
            // TODO get possible properties from ruby?
            Node netNode = networks.get(networkNode.getText());
            if (netNode != null) {
                result.add("sites");
                result.add("sectors");
                String[] fields = new PropertyHeader(netNode).getNumericFields();
                for (String string : fields) {
                    result.add("sectors." + string);
                }
            }
            Node drivNode = drives.get(driveNode.getText());
            if (drivNode != null) {
                result.add("messages");
                String[] fields = new PropertyHeader(drivNode).getNumericFields();
                for (String string : fields) {
                    result.add("messages." + string);
                }
                result.add("events");
                fields = new PropertyHeader(drivNode).getNumericFields();
                for (String string : fields) {
                    result.add("events." + string);
                }
            }

            propertyList.setItems(result.toArray(new String[0]));
        } finally {
            tx.finish();
        }
    }

    /**
     *insert formula into editor
     */
    protected void insertFormula() {
        String[] selection = formulaList.getSelection();
        if (selection.length != 1) {
            return;
        }
        String formula = selection[0] + "()";
        StringBuilder builder = new StringBuilder(editor.getText());
        int position = editor.getCaretPosition();
        builder.insert(position, formula);
        editor.setText(builder.toString());
        editor.setSelection(position + formula.length() - 1);
        editor.setFocus();
    }

    /**
     * parsing script in editor
     */
    protected void testScript() {
        Pair<Boolean, Exception> parceResult = testScript(getScriptText());
        if (parceResult.getLeft()) {
            testOk();
        } else {
            testError(parceResult.getRight().getLocalizedMessage());
        }
    }

    /**
     * parsing script
     * 
     * @param aTestScript -parsing script content
     * @return Pair<true-no errors,null or exception if error present>
     */
    protected Pair<Boolean, Exception> testScript(String aTestScript) {
        Ruby rubyRuntime = KPIPlugin.getDefault().getRubyRuntime();
        try {
            ThreadContext context = rubyRuntime.getCurrentContext();
            DynamicScope currentScope = context.getCurrentScope();
            ManyVarsDynamicScope newScope = new ManyVarsDynamicScope(new EvalStaticScope(currentScope.getStaticScope()),
                    currentScope);
            rubyRuntime.parseEval(aTestScript, "<script>", newScope, 0);
            return new Pair<Boolean, Exception>(true, null);
        } catch (Exception e) {
            return new Pair<Boolean, Exception>(false, e);
        }
    }

    /**
     * show message that test failing
     * 
     * @param message - error message
     */
    private void testError(String message) {
        MessageBox msg = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK);
        msg.setText(TEST_TITLE);
        String resultStr = String.format(TEST_ERROR, message);
        msg.setMessage(resultStr);
        msg.open();
    }

    /**
     *show message that parsing ok
     */
    private void testOk() {
        MessageBox msg = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK);
        msg.setText(TEST_TITLE);
        msg.setMessage(TEST_NO_ERR);
        msg.open();
    }

    /**
     *run script
     */
    protected void runScript() {

        IRubyObject result;
        try {
            result = KPIPlugin.getDefault().getRubyRuntime().evalScriptlet(getScriptText());
            outputResult(result);
        } catch (Exception e) {
            e.printStackTrace();
            outputError(e);
        }

    }

    /**
     *run script
     */
    protected IRubyObject runScript(String script) {

        IRubyObject result;
        try {
            result = KPIPlugin.getDefault().getRubyRuntime().evalScriptlet(script);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    /**
     * shows message if runs script throw exception
     * 
     * @param e - exception
     */
    private void outputError(Exception e) {
        MessageBox msg = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK);
        msg.setText(TITLE);
        String resultStr = "Script error:/n" + e.getLocalizedMessage();
        msg.setMessage(resultStr);
        msg.open();
    }

    /**
     * Output result of script execution
     * 
     * @param result - result of script execution
     */
    private void outputResult(IRubyObject result) {
        MessageBox msg = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OK);
        msg.setText(TITLE);
        String resultStr = result == null ? "ERROR" : result.toString();
        msg.setMessage(resultStr);
        msg.open();
    }

    /**
     * get script from editor
     * 
     * @return script content
     */
    protected String getScriptText() {
        return editor.getText();
    }


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
        editor.setFocus();
	}
}