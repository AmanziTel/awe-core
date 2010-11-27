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

package org.amanzi.awe.afp.loaders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.afp.files.ControlFile;
import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.parser.LineParser;
import org.amanzi.neo.loader.core.parser.LineTransferData;

/**
 * <p>
 * Parser for AFP data
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class AfpParser extends LineParser {
    @Override
    protected LineTransferData getStartupElement(FileElement element) {
        LineTransferData result=super.getStartupElement(element);
        result.put("afpType", ((AfpFileElement)element).getFileType().name());
        return result;
    }
    @Override
    protected List<FileElement> getElementList() {
        List<FileElement> result = new ArrayList<FileElement>();
        CommonConfigData prop = getProperties();
        try {
            ControlFile file = new ControlFile(prop.getRoot());
            File cellFile = file.getCellFile();
            if (cellFile == null) {
                error("Not found Cite file");
                return result;
            }
            AfpFileElement cellFileElement = new AfpFileElement(cellFile, getDescriptionFormat(),AfpFileTypes.CELL);
            result.add(cellFileElement);
            File ff = file.getForbiddenFile();
            if (ff!=null){
                result.add(new AfpFileElement(ff, getDescriptionFormat(), AfpFileTypes.FORBIDDEN)); 
            }
            File fNeigh = file.getForbiddenFile();
            if (fNeigh!=null){
                result.add(new AfpFileElement(fNeigh, getDescriptionFormat(), AfpFileTypes.NEIGHBOUR)); 
            }
            File fExc = file.getForbiddenFile();
            if (fExc!=null){
                result.add(new AfpFileElement(fExc, getDescriptionFormat(), AfpFileTypes.EXCEPTION)); 
            }
            File fInt = file.getForbiddenFile();
            if (fInt!=null){
                result.add(new AfpFileElement(fInt, getDescriptionFormat(), AfpFileTypes.INTERFERENCE)); 
            }
        } catch (IOException e) {
            exception(e);
            return new ArrayList<FileElement>();
        }

        return result;
    }

    public static class AfpFileElement extends FileElement {
        protected AfpFileTypes fileType;

        public AfpFileElement(File file, String descriptionFormat, AfpFileTypes type) {
            super(file, descriptionFormat);
            fileType = type;
        }

        public AfpFileTypes getFileType() {
            return fileType;
        }

    }
}
