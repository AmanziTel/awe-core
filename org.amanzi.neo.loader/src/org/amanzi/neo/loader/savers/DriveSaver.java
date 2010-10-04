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

package org.amanzi.neo.loader.savers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.ActionUtil.RunnableWithResult;
import org.amanzi.neo.loader.core.parser.HeaderTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.IStructuredSaver;
import org.amanzi.neo.loader.dialogs.DateTimeDialogWithToggle;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.ui.PlatformUI;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Abstract saver for drive data
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public abstract class DriveSaver<T extends HeaderTransferData> extends AbstractHeaderSaver<T> implements IStructuredSaver<T> {
    protected boolean newElem;
    protected Calendar workDate;
    protected boolean applyToAll;
    protected Node parent;
    protected Node lastMNode;
    @Override
    public void init(T element) {
        super.init(element);
        initBeforeStartTX();
        startMainTx(4000);
        initializeIndexes();
        if (cleanHeaders()) {
            element.put("cleanHeaders", "true");
        }
    }

    public void save(T element) {
        if (newElem) {
            handleFirstRow(element);
            newElem = false;
        }

    };
    /**
     * Ask time.
     * 
     * @param element the element
     * @return the calendar
     */
    protected Calendar askTime(final T element) {
        Calendar result = ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult<Calendar>() {
            Calendar result;
            private DateTimeDialogWithToggle dialog;

            @Override
            public void run() {
                Calendar prefDate = Calendar.getInstance();
                String time = element.get("timestamp");
                long millis = time == null ? System.currentTimeMillis() : Long.parseLong(time);
                prefDate.setTimeInMillis(millis);
                dialog = new DateTimeDialogWithToggle(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Date of file", null, String.format(
                        "File '%s' has no date information.", element.getFileName()), "Please specify the date on which this data was collected:",
                        MessageDialogWithToggle.QUESTION, new String[] {IDialogConstants.CANCEL_LABEL, IDialogConstants.OK_LABEL}, 0, "apply this date to all files in this load ",
                        applyToAll, prefDate.get(Calendar.YEAR), prefDate.get(Calendar.MONTH), prefDate.get(Calendar.DAY_OF_MONTH));
                dialog.open();
                if (dialog.getReturnCode() == IDialogConstants.OK_ID) {
                    result = dialog.getCallendar();
                    applyToAll = dialog.getToggleState();
                } else {
                    result = null;
                }
            }

            @Override
            public Calendar getValue() {
                return result;
            }
        });
        return result;
    }
    /**
     * Gets the longitude.
     * 
     * @param stringValue the string value
     * @return the longitude
     */
    protected Double getLongitude(String stringValue) {
        if (stringValue == null) {
            return null;
        }
        try {
            return Double.valueOf(stringValue);
        } catch (NumberFormatException e) {
            Pattern p = Pattern.compile("^([+-]{0,1}\\d+(\\.\\d+)*)([NESW]{0,1})$");
            Matcher m = p.matcher(stringValue);
            if (m.matches()) {
                try {
                    return Double.valueOf(m.group(1));
                } catch (NumberFormatException e2) {
                    error(String.format("Can't get Longitude from: %s", stringValue));
                }
            }
        }
        return null;
    }

    /**
     * Gets the latitude.
     * 
     * @param stringValue the string value
     * @return the latitude
     */
    protected Double getLatitude(String stringValue) {
        if (stringValue == null) {
            return null;
        }
        try {
            return Double.valueOf(stringValue);
        } catch (NumberFormatException e) {
            Pattern p = Pattern.compile("^([+-]{0,1}\\d+(\\.\\d+)*)([NESW]{0,1})$");
            Matcher m = p.matcher(stringValue);
            if (m.matches()) {
                try {
                    return Double.valueOf(m.group(1));
                } catch (NumberFormatException e2) {
                    error(String.format("Can't get Latitude from: %s", stringValue));
                }
            }
        }
        return null;
    }   
    
    public boolean beforeSaveNewElement(T element) {
        newElem = true;

        workDate = applyToAll?workDate:getWorkDate(element);
        boolean result = workDate == null;
        parent = null;
        if (!result) {
            parent = service.getFileNode(rootNode, element.getFileName());
            lastMNode = null;
        }
        return result;
    };
    /**
     * @param key -key of value from preference store
     * @return array of possible headers
     */
    protected String[] getPossibleHeaders(String key) {
        String text = NeoLoaderPlugin.getDefault().getPreferenceStore().getString(key);
        String[] array = text.split(",");
        List<String> result = new ArrayList<String>();
        for (String string : array) {
            String value = string.trim();
            if (!value.isEmpty()) {
                result.add(value);
            }
        }
        return result.toArray(new String[0]);
    }
    protected abstract Calendar getWorkDate(T element);

    /**
     * Handle first row.
     * 
     * @param element the element
     */
    protected void handleFirstRow(T element) {
        propertyMap.clear();
        definePropertyMap(element);
    }

    protected abstract void definePropertyMap(T element);

    /**
     * Clean headers.
     * 
     * @return true, if successful
     */
    protected boolean cleanHeaders() {
        return true;
    }

    /**
     * Inits the before start tx.
     */
    protected void initBeforeStartTX() {
        newElem = true;
        workDate = null;
        applyToAll = false;
        addDriveIndexes();
    }

    protected abstract void addDriveIndexes();
}
