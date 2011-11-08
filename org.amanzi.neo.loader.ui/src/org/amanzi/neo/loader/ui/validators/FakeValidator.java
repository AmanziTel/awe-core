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

package org.amanzi.neo.loader.ui.validators;

import java.io.File;
import java.util.List;

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.IValidator;

/**
 * @author Vladislav_Kondratenko
 */
public class FakeValidator implements IValidator {

    private Result result = Result.FAIL;
    private String message = "";

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public String getMessages() {
        return message;
    }

    @Override
    public Result isAppropriate(List<File> fileToLoad) {
        message = "unknow data. you shoul select required loader";
        return Result.UNKNOWN;
    }

    @Override
    public Result isValid(IConfiguration config) {
        message = "unknow data. you shoul select required loader";
        return Result.UNKNOWN;
    }

}
