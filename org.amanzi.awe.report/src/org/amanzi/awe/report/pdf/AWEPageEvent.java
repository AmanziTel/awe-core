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

package org.amanzi.awe.report.pdf;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.amanzi.awe.report.ReportPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class AWEPageEvent extends PdfPageEventHelper {
    private Image logo;

    public AWEPageEvent() {
         final URL entry = Platform.getBundle(ReportPlugin.PLUGIN_ID).getEntry("icons/amanzi_tel_logo.png");
         String file;
        try {
            file = FileLocator.resolve(entry).getFile();
            logo=Image.getInstance(file);
            logo.setAbsolutePosition(0, 0);
        } catch (IOException e1) {
            // TODO Handle IOException
            throw (RuntimeException) new RuntimeException( ).initCause( e1 );
        } catch (BadElementException e) {
            // TODO Handle BadElementException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
           
       
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte contentUnder = writer.getDirectContentUnder();
        try {
            contentUnder.addImage(logo);
//            contentUnder.addImage(logo, logo.getWidth(), 0, 0, logo.getHeight(), 100, 100);
        } catch (BadElementException e) {
            // TODO Handle BadElementException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (DocumentException e) {
            // TODO Handle DocumentException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

}
