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

package org.amanzi.neo.wizards;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * <p>
 * Wizard to import dataset from url
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class DatasetImportUrlWizard extends Wizard implements IImportWizard {
    /** String PAGE_TITLE field */
    private static final String PAGE_TITLE = NeoLoaderPluginMessages.TemsImportWizard_PAGE_TITLE;
    /** String PAGE_DESCR field */
    private static final String PAGE_DESCR = NeoLoaderPluginMessages.TemsImportWizard_PAGE_DESCR;
    private DatasetImportUrlWizardPage mainPage;

    @Override
    public boolean performFinish() {
        System.out.println("Started");
        try {

            URL url = new URL(mainPage.getUrl());

            String line;
            StringBuilder file = new StringBuilder("");
            long statr = 0L;
            BufferedReader in = null;

            statr = System.currentTimeMillis();

            InputStream inputStream = url.openStream();

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            in = new BufferedReader(inputStreamReader);

            file = new StringBuilder("");
            while ((line = in.readLine()) != null) {
                file.append(line);

            }
            in.close();
            System.out.println(file.toString());
            System.out.println("finished in:" + (System.currentTimeMillis() - statr));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Finished");
        return false;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        mainPage = new DatasetImportUrlWizardPage(PAGE_TITLE, PAGE_DESCR);
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }
}
