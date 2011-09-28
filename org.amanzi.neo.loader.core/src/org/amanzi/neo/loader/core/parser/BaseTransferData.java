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

package org.amanzi.neo.loader.core.parser;


/**
 * <p>
 * TransferData contains information about headers (key - header(column name), value - value from table)
 * </p>.
 *
 * @author TsAr
 * @since 1.0.0
 */
public class BaseTransferData extends MapBasedTransferData<String, String> {

    /** long serialVersionUID field. */
    private static final long serialVersionUID = -5686953917241479358L;
    private IParser parser;
    private double currentpersentage;
    
    
    public double getCurrentpersentage() {
        return currentpersentage;
    }

    public void setCurrentpersentage(double currentpersentage) {
        this.currentpersentage = currentpersentage;
    }

    public IParser getParser() {
        return parser;
    }

    public void setParser(IParser parser) {
        this.parser = parser;
    }   
}
