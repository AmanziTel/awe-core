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

package org.amanzi.neo.data_generator.utils.call;

import java.io.IOException;
import java.util.List;

import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.data.calls.CallXmlData;
import org.amanzi.neo.data_generator.utils.xml_data.XMLFileBuilder;

/**
 * <p>
 * File builder for xml call data.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CallXmlFileBuilder extends XMLFileBuilder{
    
    private static String FILE_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    private static String FILE_NAME_PREFIX = "Cluster 001__";
    private static String FILE_NAME_POSTFIX = ".xml";
    
    private String path;
    
    public CallXmlFileBuilder(String aPath) {
        path = aPath;
    }
    
    public void saveData(String typeKey, List<CallGroup> aData)throws IOException{
        for(CallGroup group : aData){
            String fileNamePrefix = buildFileNamePrefix(typeKey, group);
            for(CallData call : group.getData()){
                String fileName = fileNamePrefix+call.getStartTime()+FILE_NAME_POSTFIX;
                saveFile(path, fileName, ((CallXmlData)call).getRoot());
            }
        }
    }
    
    private String buildFileNamePrefix(String typeKey, CallGroup group){
        StringBuilder result = new StringBuilder(FILE_NAME_PREFIX)
                                                .append(typeKey).append("_")
                                                .append(group.getSourceProbe());
        for(int receiver : group.getReceiverProbes()){
            result.append("-").append(receiver);
        }
        result.append("#");
        return result.toString();
    }

    @Override
    protected String getPrefix() {
        return FILE_PREFIX;
    }

}
