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

package org.amanzi.awe.ui.view.widgets;

import java.util.Calendar;
import java.util.Date;

import org.amanzi.awe.ui.view.widgets.DateTimeWidget.ITimeChangedListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.amanzi.awe.ui.view.widgets.internal.AbstractLabeledWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DateTimeWidget extends AbstractLabeledWidget<Composite, ITimeChangedListener> implements SelectionListener {

    private static final Calendar CALENDAR = Calendar.getInstance();

    public interface ITimeChangedListener extends AbstractAWEWidget.IAWEWidgetListener {

        void onTimeChanged(Date newTime, DateTimeWidget source);

    }

    private Date defaultDate;

    private DateTime date;

    private DateTime time;

    private Button skipButton;

    private Date currentDate;

    /**
     * @param parent
     * @param listener
     * @param label
     */
    protected DateTimeWidget(final Composite parent, final ITimeChangedListener listener, final String label,
            final int minimalLabelWidth) {
        super(parent, listener, label, minimalLabelWidth);
    }

    @Override
    protected Composite createControl(final Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));

        date = addDateTime(composite, SWT.BORDER | SWT.DATE);
        time = addDateTime(composite, SWT.BORDER | SWT.TIME);

        date.addSelectionListener(this);
        time.addSelectionListener(this);

        skipButton = new Button(composite, SWT.NONE);
        skipButton.setText("X");
        skipButton.addSelectionListener(this);
        skipButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        return composite;
    }

    private DateTime addDateTime(final Composite parent, final int style) {
        DateTime result = new DateTime(parent, style);

        result.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        result.setVisible(true);

        return result;
    }

    private void updateDate(final Date newDate) {
        CALENDAR.setTime(newDate);

        date.setDate(CALENDAR.get(Calendar.YEAR), CALENDAR.get(Calendar.MONTH), CALENDAR.get(Calendar.DAY_OF_MONTH));
        time.setHours(CALENDAR.get(Calendar.HOUR_OF_DAY));
        time.setMinutes(CALENDAR.get(Calendar.MINUTE));
        time.setSeconds(CALENDAR.get(Calendar.SECOND));
    }

    public void setDefaultDate(final Date newDate) {
        if ((defaultDate == null) || !newDate.equals(defaultDate)) {
            this.currentDate = newDate;
            this.defaultDate = newDate;
        }

        updateDate(currentDate);
    }

    public Date getDate() {
        return currentDate;
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        if (e.getSource().equals(skipButton)) {
            currentDate = defaultDate;
        } else {
            CALENDAR.set(Calendar.YEAR, date.getYear());
            CALENDAR.set(Calendar.DATE, date.getDay());
            CALENDAR.set(Calendar.MONTH, date.getMonth());
            CALENDAR.set(Calendar.SECOND, time.getSeconds());
            CALENDAR.set(Calendar.MINUTE, time.getMinutes());
            CALENDAR.set(Calendar.HOUR_OF_DAY, time.getHours());
            currentDate = CALENDAR.getTime();
        }

        updateDate(currentDate);
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        widgetSelected(e);
    }

}
