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

import java.util.List;

import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.parser.CSVParser;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkConfigurationParser extends CSVParser{
    NetworkConfigurationParser(){
        super();
        delimeters=' ';
    }
    @Override
    protected List<org.amanzi.neo.loader.core.parser.CommonFilesParser.FileElement> getElementList() {
        return super.getElementList();
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
    protected BaseTransferData getStartupElement(org.amanzi.neo.loader.core.parser.CommonFilesParser.FileElement element) {
        BaseTransferData result = super.getStartupElement(element);
        result.put("fileType", isCNAFile(element)?NetworkConfigurationFileTypes.CNA.name():NetworkConfigurationFileTypes.BSM.name());
        return result;
    }
    /**
     *
     * @param element
     * @return
     */
    private boolean isCNAFile(org.amanzi.neo.loader.core.parser.CommonFilesParser.FileElement element) {
        return false;
    }
}
