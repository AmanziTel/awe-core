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

package org.amanzi.awe.afp.models.parameters;

public enum OptimizationType implements IOptimizationParameterEnum {
    FREQUENCIES("Frequencies"),
    BSIC("BSIC"),
    HSN("HSN"),
    MAIO("MAIO");
    
    private String text;
    
    private OptimizationType(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
}