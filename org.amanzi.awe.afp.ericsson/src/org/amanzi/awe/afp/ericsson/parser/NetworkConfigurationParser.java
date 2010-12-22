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

package org.amanzi.awe.afp.ericsson.parser;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.parser.AbstractCSVParser;
import org.amanzi.neo.loader.core.saver.ISaver;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkConfigurationParser extends AbstractCSVParser<NetworkConfigurationTransferData> {
    private Collection<File> bsmFiles=new LinkedHashSet<File>();


    private NetworkConfigurationTransferData initdata;
    public  NetworkConfigurationParser(){
        super();
        delimeters=' ';
    }
    @Override
    public void init(CommonConfigData properties, ISaver<NetworkConfigurationTransferData> saver) {
        super.init(properties, saver);
        Collection<File> bsmList = (Collection<File>)properties.getAdditionalProperties().get("BSM_FILES");
        bsmFiles.clear();
        if (bsmList!=null){
            bsmFiles.addAll(bsmList); 
        }
    }
    @Override
    protected List<org.amanzi.neo.loader.core.parser.CommonFilesParser.FileElement> getElementList() {
        List<FileElement> result = super.getElementList();
        String descr = getDescriptionFormat();
        for (File file : bsmFiles) {
            result.add(new FileElement(file, descr));
        }
        return result;
    }
    @Override
    protected NetworkConfigurationTransferData createTransferData(FileElement element, String[] header, String[] nextLine, long line) {
        NetworkConfigurationTransferData data =createEmptyTransferData();
        data.setLine(line);
        data.setFileName(element.getFile().getName());
        for (int i = 0; i < header.length; i++) {
            
            String value = nextLine[i];
            if ("NULL".equalsIgnoreCase(value)){
                continue;
            }
            data.put(header[i], value);
        }     
        data.setHeaders(header);
        data.setValuesData(nextLine);
        return data;
    }
    @Override
    protected boolean parseElement(org.amanzi.neo.loader.core.parser.CommonFilesParser.FileElement element) {
        if (isCNAFile(element)){
            return super.parseElement(element);
        }else{
            //TODO implement
            return false;  
        }
    }
    @Override
    protected NetworkConfigurationTransferData getStartupElement(FileElement element) {
        NetworkConfigurationTransferData result=super.getStartupElement(element);
        result.setType(isCNAFile(element)?NetworkConfigurationFileTypes.CNA:NetworkConfigurationFileTypes.BSM);
        return result;
    }

    private boolean isCNAFile(org.amanzi.neo.loader.core.parser.CommonFilesParser.FileElement element) {
        return !bsmFiles.contains(element.getFile());
    }

    @Override
    protected NetworkConfigurationTransferData createEmptyTransferData() {
        return new NetworkConfigurationTransferData();
    }
public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
   Object l = Class.forName("org.amanzi.awe.afp.ericsson.parser.NetworkConfigurationParser").newInstance();
   System.out.println(l);
}
}
