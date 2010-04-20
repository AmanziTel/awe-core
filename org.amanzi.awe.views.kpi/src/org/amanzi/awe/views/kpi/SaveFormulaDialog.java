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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class SaveFormulaDialog extends Dialog {
    private static final Logger LOGGER = Logger.getLogger(SaveFormulaDialog.class);
    protected static final String DEFAULT_EXTENSION = ".rb";
    private String text;
    private String formulaText;
    private String fileName;
    private String formulaName;
    private String parameters;
    protected String status;
    private Text txtFileName;
    private Text txtFormulaName;

    protected SaveFormulaDialog(Shell parentShell, String text) {
        super(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        this.text = text;
        // getShell().setText(title);

    }

    public String open() {
        Shell parentShell = getParent();
        Shell shell = new Shell(parentShell, getStyle());
        shell.setText(text);

        createContents(shell);
        shell.pack();

        // calculate location
        Point size = parentShell.getSize();
        int dlgWidth = shell.getSize().x;
        int dlgHeight = shell.getSize().y;
        shell.setLocation((size.x - dlgWidth) / 2, (size.y - dlgHeight) / 2);
        shell.open();

        // wait
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return status;
    }

    private Control createContents(final Shell shell) {
        shell.setLayout(new GridLayout(2, true));
        // Formula text
        final Label lblFormulaText = new Label(shell, SWT.NONE);
        lblFormulaText.setText(formulaText);
        GridData gdLblFormulaText = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gdLblFormulaText.minimumWidth = 50;
        gdLblFormulaText.horizontalSpan = 2;
        gdLblFormulaText.horizontalAlignment = GridData.CENTER;
        lblFormulaText.setLayoutData(gdLblFormulaText);

       // Formula name
        Label lblFormulaName = new Label(shell, SWT.NONE);
        lblFormulaName.setText("Formula name:");
        GridData gdLblFormulaName = new GridData(GridData.FILL);
        gdLblFormulaName.minimumWidth = 50;
        lblFormulaName.setLayoutData(gdLblFormulaName);

        txtFormulaName = new Text(shell, SWT.SINGLE);
        GridData gdTxtFormulaName = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
        gdTxtFormulaName.widthHint = 200;
        txtFormulaName.setLayoutData(gdTxtFormulaName);
        txtFormulaName.setText(getFormulaName());
        
        
       
        // Formula file
        Label lblFileName = new Label(shell, SWT.NONE);
        lblFileName.setText("Formula file:");
        GridData gdLblFileName = new GridData(GridData.FILL);
        gdLblFileName.minimumWidth = 50;
        lblFileName.setLayoutData(gdLblFileName);

        txtFileName = new Text(shell, SWT.SINGLE);
        GridData gdTxtFileName = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
        gdLblFileName.widthHint = 200;
        txtFileName.setLayoutData(gdTxtFileName);
        txtFileName.setText(getFileName().replaceAll("\\.\\w+", ""));

        //use formula name as default value for file name
        txtFormulaName.addModifyListener(new ModifyListener(){

            @Override
            public void modifyText(ModifyEvent e) {
               txtFileName.setText(txtFormulaName.getText());
            }
            
        });
        // Parameters
        final Label lblParameters = new Label(shell, SWT.NONE);
        lblParameters.setText("Parameters:");
        GridData gdLblParameters = new GridData(GridData.FILL);
        gdLblParameters.minimumWidth = 50;
        lblParameters.setLayoutData(gdLblParameters);

        final Text txtParameters = new Text(shell, SWT.SINGLE);
        GridData gdTxtParameters = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
        gdTxtParameters.widthHint = 200;
        txtParameters.setLayoutData(gdTxtParameters);
        txtParameters.setText(getParameters());

        // Error message
        final Label lblError = new Label(shell, SWT.NONE);
        lblError.setText("");
        GridData gdLblError = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        // gdLblError.minimumWidth=50;
        gdLblError.horizontalSpan = 2;
        gdLblError.horizontalAlignment = GridData.CENTER;
        lblError.setLayoutData(gdLblError);

        // Save button
        Button btnSave = new Button(shell, SWT.PUSH);
        btnSave.setText("Save");
        GridData gdBtnSave = new GridData();
        gdBtnSave.horizontalAlignment = GridData.CENTER;
        btnSave.setLayoutData(gdBtnSave);
        btnSave.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean hasErrors = false;
                StringBuffer errors = new StringBuffer();
                String fileName = txtFileName.getText();
                if (fileName != null && fileName.length() != 0) {
                    // validate file name
                    String SCRIPT_FILE_REGEX = "([\\w|_|\\d]+)(.\\w+)?";
                    Matcher matcher = Pattern.compile(SCRIPT_FILE_REGEX).matcher(fileName);
                    if (fileName.matches(SCRIPT_FILE_REGEX)) {
                        LOGGER.debug("file name matches: " + fileName.matches(SCRIPT_FILE_REGEX));
                        if (matcher.find()) {
                            String name = matcher.group(1);
                            String extension = matcher.group(2);
                            if (extension == null) {
                                setFileName(name + DEFAULT_EXTENSION);
                            } else {
                                if (extension.equalsIgnoreCase(DEFAULT_EXTENSION)) {
                                    setFileName(fileName);
                                } else {
                                    errors.append("Only .rb extension is allowed!").append("\n");
                                    hasErrors = true;
                                }
                            }
                        }
                    } else {
                        errors.append("Incorrect file name!").append("\n");
                        hasErrors = true;
                    }
                } else {
                    errors.append("File name can't be empty!").append("\n");
                    hasErrors = true;
                }

                // validate formula name
                String formulaName = txtFormulaName.getText();
                if (formulaName == null || formulaName.length() == 0) {
                    errors.append("Formula name can't be empty!").append("\n");
                    hasErrors = true;
                } else if (!formulaName.matches("[\\w|_|\\d]+")) {
                    errors.append("Incorrect formula name!").append("\n");
                    hasErrors = true;
                } else {
                    setFormulaName(formulaName);
                }
                // validate parameters
                String parameters = txtParameters.getText();
                if (parameters != null && parameters.length() != 0) {
                    String PARAMETERS_REGEX = "(([\\w|_|\\d]+),\\s*)*([\\w|_|\\d]+){1}";
                    if (!parameters.matches(PARAMETERS_REGEX)) {
                        errors.append("Incorrect parameters!").append("\n");
                        hasErrors = true;
                    } else {
                        // TODO validate if formula contains every parameter
                        LOGGER.debug("Parameters:");
                        String[] params = parameters.split(",");
                        for (String param : params) {
                            LOGGER.debug("-> " + param);
                        }
                        setParameters(parameters);
                    }
                } else {
                    setParameters("");
                }
                if (!hasErrors) {
                    status = "SAVE";
                    shell.close();
                } else {
                    lblError.setText(errors.toString());
                    shell.pack();
                }
            }

        });

        // Cancel button
        Button btnCancel = new Button(shell, SWT.PUSH);
        btnCancel.setText("Cancel");
        GridData gdBtnCancel = new GridData();
        gdBtnCancel.horizontalAlignment = GridData.CENTER;
        btnCancel.setLayoutData(gdBtnCancel);
        btnCancel.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.close();
            }

        });

        return shell;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the text
     */
    @Override
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    @Override
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the formulaText
     */
    public String getFormulaText() {
        return formulaText;
    }

    /**
     * @param formulaText the formulaText to set
     */
    public void setFormulaText(String formulaText) {
        this.formulaText = formulaText;
    }

    /**
     * @return the formulaName
     */
    public String getFormulaName() {
        return formulaName;
    }

    /**
     * @param formulaName the formulaName to set
     */
    public void setFormulaName(String formulaName) {
        this.formulaName = formulaName;
    }

    /**
     * @return the parameters
     */
    public String getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

}
