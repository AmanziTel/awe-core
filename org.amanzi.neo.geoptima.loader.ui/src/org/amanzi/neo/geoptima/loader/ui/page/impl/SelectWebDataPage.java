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

package org.amanzi.neo.geoptima.loader.ui.page.impl;

import java.util.Calendar;
import java.util.Date;

import org.amanzi.awe.ui.view.widgets.AWEWidgetFactory;
import org.amanzi.awe.ui.view.widgets.DateTimeWidget;
import org.amanzi.awe.ui.view.widgets.DateTimeWidget.ITimeChangedListener;
import org.amanzi.awe.ui.view.widgets.TextWidget;
import org.amanzi.neo.geoptima.loader.ui.internal.Messages;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class SelectWebDataPage extends SelectRemoteDataPage implements ITimeChangedListener {

    private TextWidget imsiWidget;
    private TextWidget imeiWidget;
    private DateTimeWidget startDateWidget;
    private DateTimeWidget endDateWidget;

    /**
     * @param name
     */
    public SelectWebDataPage() {
        super(Messages.selectWebSource_PageName);
    }

    @Override
    public void createControl(final Composite parent) {
        super.createControl(parent);
        imeiWidget = AWEWidgetFactory.getFactory().addTextWidget(this, SWT.BORDER, Messages.imei_Label, getMainComposite(),
                getMinimalLabelWidth());
        imeiWidget.setDefault("240016008967428");
        imsiWidget = AWEWidgetFactory.getFactory().addTextWidget(this, SWT.BORDER, Messages.imsi_Label, getMainComposite(),
                getMinimalLabelWidth());
        imsiWidget.setDefault("358506046830281");
        startDateWidget = AWEWidgetFactory.getFactory().addPeriodWidget(this, Messages.startDate_label, getMainComposite(),
                getMinimalLabelWidth());
        // IMSI:240016008967428, IMEI: 358506046830281
        startDateWidget.setDefaultDate(Calendar.getInstance().getTime());
        endDateWidget = AWEWidgetFactory.getFactory().addPeriodWidget(this, Messages.endDate_label, getMainComposite(),
                getMinimalLabelWidth());
        endDateWidget.setDefaultDate(Calendar.getInstance().getTime());
        onTimeChanged(startDateWidget.getDate(), startDateWidget);
        onTimeChanged(endDateWidget.getDate(), endDateWidget);
        update();
    }

    @Override
    protected String getDefaultHost() {
        return "http://explorer.amanzitel.com/geoptima";
    }

    @Override
    public void onTextChanged(final String text) {
        if (imeiWidget != null && imsiWidget != null) {
            getConfiguration().setImei(imeiWidget.getText());
            getConfiguration().setImsi(imsiWidget.getText());
            getConfiguration().setCredentials(getUrl(), null, null);
            update();
        }
    }

    @Override
    public void onTimeChanged(final Date newTime, final DateTimeWidget source) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newTime);
        if (source.equals(startDateWidget)) {
            getConfiguration().setStartTime(calendar);
        } else {
            getConfiguration().setEndTime(calendar);
        }
        update();
    }

    protected void update() {
        if (StringUtils.isEmpty(getUrl()) || StringUtils.isEmpty(imeiWidget.getText()) || StringUtils.isEmpty(imsiWidget.getText())) {
            setErrorMessage(Messages.enterRequiredCredentials_msg);
            setPageComplete(false);
            return;
        }
        if (getConfiguration().getStartTime() != null && getConfiguration().getEndTime() != null
                && getConfiguration().getStartTime().getTimeInMillis() > getConfiguration().getEndTime().getTimeInMillis()) {
            setErrorMessage(Messages.incorrectDateValue_msg);
            setPageComplete(false);
            return;
        }
        setErrorMessage(null);
        setPageComplete(true);

    }
}
