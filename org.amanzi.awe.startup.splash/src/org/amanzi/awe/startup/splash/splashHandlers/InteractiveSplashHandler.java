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
package org.amanzi.awe.startup.splash.splashHandlers;

import org.amanzi.awe.startup.splash.Activator;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.branding.IProductConstants;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.splash.BasicSplashHandler;

/**
 * @since 1.0.0
 */
@SuppressWarnings("restriction")
public class InteractiveSplashHandler extends BasicSplashHandler {
    public void init(Shell splash) {
        super.init(splash);
        String progressRectString = null;
        String messageRectString = null;
        String foregroundColorString = null;
        IProduct product = Platform.getProduct();
        if (product != null) {
            progressRectString = product.getProperty(IProductConstants.STARTUP_PROGRESS_RECT);
            messageRectString = product.getProperty(IProductConstants.STARTUP_MESSAGE_RECT);
            foregroundColorString = product.getProperty(IProductConstants.STARTUP_FOREGROUND_COLOR);
        }
        Rectangle progressRect = StringConverter.asRectangle(progressRectString, new Rectangle(10, 10, 300, 15));
        setProgressRect(progressRect);

        Rectangle messageRect = StringConverter.asRectangle(messageRectString, new Rectangle(10, 35, 300, 15));
        setMessageRect(messageRect);

        int foregroundColorInteger;
        try {
            foregroundColorInteger = Integer.parseInt(foregroundColorString, 16);
        } catch (Exception ex) {
            foregroundColorInteger = 0xD2D7FF; // off white
        }

        setForeground(new RGB((foregroundColorInteger & 0xFF0000) >> 16, (foregroundColorInteger & 0xFF00) >> 8,
                foregroundColorInteger & 0xFF));
        // the following code will be removed for release time
        if (PrefUtil.getInternalPreferenceStore().getBoolean("SHOW_BUILDID_ON_STARTUP")) { //$NON-NLS-1$
            final String buildId = System.getProperty("eclipse.buildId", "Unknown Build"); //$NON-NLS-1$ //$NON-NLS-2$
            // find the specified location. Not currently API
            // hardcoded to be sensible with our current Europa Graphic
            String buildIdLocString = product.getProperty("buildIdLocation"); //$NON-NLS-1$
            final Point buildIdPoint = StringConverter.asPoint(buildIdLocString, new Point(322, 190));
            getContent().addPaintListener(new PaintListener() {

                public void paintControl(PaintEvent e) {
                    e.gc.setForeground(getForeground());
                    e.gc.drawText(buildId, buildIdPoint.x, buildIdPoint.y, true);
                }
            });
        } else {
            getContent(); // ensure creation of the progress
        }
        Activator.getDefault().setSplashMonitor(getBundleProgressMonitor());
    }

}
