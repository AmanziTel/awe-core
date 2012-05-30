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

package org.amanzi.neo.loader.ui.utils.dialogs;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * <p>
 * Input dialog with toggle
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class DateTimeDialogWithToggle extends MessageDialogWithToggle {
    private int selYear;
    private int selMonth;
    private int selDay;
    private DateTime dateTime;
    private final String dataLabelMessage;

    /**
     * Creates a message dialog with a toggle. See the superclass constructor for info on the other
     * parameters.
     * 
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title, or <code>null</code> if none
     * @param image the dialog title image, or <code>null</code> if none
     * @param message the dialog message
     * @param dataLabelMessage the label message for data field
     * @param dialogImageType one of the following values:
     *        <ul>
     *        <li><code>MessageDialog.NONE</code> for a dialog with no image</li>
     *        <li><code>MessageDialog.ERROR</code> for a dialog with an error image</li>
     *        <li><code>MessageDialog.INFORMATION</code> for a dialog with an information image</li>
     *        <li><code>MessageDialog.QUESTION </code> for a dialog with a question image</li>
     *        <li><code>MessageDialog.WARNING</code> for a dialog with a warning image</li>
     *        </ul>
     * @param dialogButtonLabels an array of labels for the buttons in the button bar
     * @param defaultIndex the index in the button label array of the default button
     * @param toggleMessage the message for the toggle control, or <code>null</code> for the default
     *        message
     * @param toggleState the initial state for the toggle
     * @param selYear initial year
     * @param selMonth initial month
     * @param selDay initial day
     */
    public DateTimeDialogWithToggle(Shell parentShell, String dialogTitle, Image image, String message, String dataLabelMessage, int dialogImageType,
            String[] dialogButtonLabels, int defaultIndex, String toggleMessage, boolean toggleState, int selYear, int selMonth, int selDay) {
        super(parentShell, dialogTitle, image, message, dialogImageType, dialogButtonLabels, defaultIndex, toggleMessage, toggleState);
        this.dataLabelMessage = dataLabelMessage == null ? "" : dataLabelMessage;
        this.selDay = selDay;
        this.selMonth = selMonth;
        this.selYear = selYear;
    }

    @Override
    protected Control createCustomArea(Composite parent) {
        Composite row = new Composite(parent, SWT.FILL);
        row.setLayout(new GridLayout(2, false));
        Label lb = new Label(row, SWT.NONE);
        lb.setText(dataLabelMessage);
        dateTime = new DateTime(row, SWT.DATE | SWT.CENTER);
        dateTime.setYear(selYear);
        dateTime.setMonth(selMonth);
        dateTime.setDay(selDay);
        dateTime.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selYear = dateTime.getYear();
                selMonth = dateTime.getMonth();
                selDay = dateTime.getDay();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        return dateTime;
        }

    /**
     * @return Returns the selYear.
     */
    public int getSelYear() {
        return selYear;
    }

    /**
     * @return Returns the selMonth.
     */
    public int getSelMonth() {
        return selMonth;
    }

    /**
     * @return Returns the selDay.
     */
    public int getSelDay() {
        return selDay;
    }

    /**
     * returns gregorianCallendar which containce information about selected time
     * 
     * @return
     */
    public GregorianCalendar getCallendar() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, getSelYear());
        calendar.set(Calendar.MONTH, getSelMonth());
        calendar.set(Calendar.DAY_OF_MONTH, getSelDay());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;

    }
    }


