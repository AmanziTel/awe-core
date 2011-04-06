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

package org.amanzi.awe.afp.testing.engine;

import org.amanzi.awe.afp.ericsson.parser.BarRirParser;
import org.amanzi.awe.afp.ericsson.parser.NetworkConfigurationParser;
import org.amanzi.awe.afp.ericsson.parser.NetworkConfigurationTransferData;
import org.amanzi.awe.afp.ericsson.parser.RecordTransferData;
import org.amanzi.awe.afp.ericsson.saver.BarRirSaver;
import org.amanzi.awe.afp.ericsson.saver.NetworkConfigurationSaver;
import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.parser.CSVParser;
import org.amanzi.neo.loader.core.saver.impl.NeighbourSaver;
import org.amanzi.neo.loader.core.saver.impl.NetworkSaver;
import org.amanzi.neo.loader.core.saver.network.InterferenceMatrixSaver;
import org.amanzi.neo.loader.ui.loaders.Loader;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public class FakeLoaderFactory {
    
    public static ILoader<?, CommonConfigData> getNetworkLoader() {
        Loader<BaseTransferData, CommonConfigData> loader = new Loader<BaseTransferData, CommonConfigData>();
        
        loader.setParser(new CSVParser());
        loader.setSaver(new NetworkSaver());
        
        return loader;
    }
    
    public static ILoader<?, CommonConfigData> getNeighbourLoader() {
        Loader<BaseTransferData, CommonConfigData> loader = new Loader<BaseTransferData, CommonConfigData>();
        
        loader.setParser(new CSVParser());
        loader.setSaver(new NeighbourSaver());
        
        return loader;
    }
    
    public static ILoader<?, CommonConfigData> getIMLoader() {
        Loader<BaseTransferData, CommonConfigData> loader = new Loader<BaseTransferData, CommonConfigData>();
        
        loader.setParser(new CSVParser());
        loader.setSaver(new InterferenceMatrixSaver());
        
        return loader;
    }
    
    public static ILoader<?, CommonConfigData> getNetworkConfigLoader() {
        Loader<NetworkConfigurationTransferData, CommonConfigData> loader = new Loader<NetworkConfigurationTransferData, CommonConfigData>();
        
        loader.setParser(new NetworkConfigurationParser());
        loader.setSaver(new NetworkConfigurationSaver());
        
        return loader;
    }
    
    public static ILoader<?, CommonConfigData> getNetworkMeasurementsLoader() {
        Loader<RecordTransferData, CommonConfigData> loader = new Loader<RecordTransferData, CommonConfigData>();
        
        loader.setParser(new BarRirParser());
        loader.setSaver(new BarRirSaver());
        
        return loader;
    }
    
}
