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
package org.amanzi.neo.loader.ui.preferences;

import org.amanzi.neo.loader.core.saver.impl.NetworkSaver;
import org.amanzi.neo.loader.ui.internal.Messages;
import org.amanzi.neo.loader.ui.preferences.internal.AbstractDatasetSynonymsPreferencePage;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NetworkSynonymsPreferencePage extends AbstractDatasetSynonymsPreferencePage {

    public NetworkSynonymsPreferencePage() {
        super(Messages.NetworkSynonymsPreferencePage_NetworkSynonymsPage_Description);
    }

    @Override
    protected String getDatasetType() {
        return NetworkSaver.SYNONYMS_TYPE;
    }

}
