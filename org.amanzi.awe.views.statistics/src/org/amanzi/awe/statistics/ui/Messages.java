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

package org.amanzi.awe.statistics.ui;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Messages for views.
 * <p>
 * </p>
 * 
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public final class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getName();

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);

    /*
     * labels
     */
    public static String statisticsViewLabelDataset;
    public static String statisticsViewLabelBuild;
    public static String statisticsViewLabelAggregation;
    public static String statisticsViewLabelTemplate;
    public static String statisticsViewLabelStartTime;
    public static String statisticsViewLabelPeriod;
    public static String statisticsViewLabelResetButton;
    public static String statisticsViewLabelEndTime;
    public static String statisticsViewLabelReport;
    public static String statisticsViewLabelExport;
    public static String statisticsViewLabelChartView;
    public static String sortingDialogLabelOk;
    public static String sortingDialogLableClose;
    public static String sortingDialogLableClear;
    public static String sortingDialogLabelTextFilters;
    public static String sortingDialogLabelClearFilter;
    public static String sortingDialogLabelSortFromAtoZ;
    public static String sortingDialogLabelSortFromZtoA;
    public static String sortingDialogLabelSelectAll;
    public static String pathToRefreshButtonImg;
    public static String pathToEmptyFilterImg;

    private Messages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
