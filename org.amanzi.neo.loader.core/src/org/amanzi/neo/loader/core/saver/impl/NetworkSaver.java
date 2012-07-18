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

package org.amanzi.neo.loader.core.saver.impl;

import org.amanzi.neo.loader.core.IMappedStringData;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.saver.impl.internal.AbstractSynonymsSaver;
import org.amanzi.neo.models.network.INetworkModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NetworkSaver extends AbstractSynonymsSaver<IConfiguration> {

    private static final String SYNONYMS_TYPE = "network";

    @Override
    public void save(final IMappedStringData dataElement) {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getSynonymsType() {
        return SYNONYMS_TYPE;
    }

    protected INetworkModel createNetworkModel(final String networkName) {
        return null;
    }

}
