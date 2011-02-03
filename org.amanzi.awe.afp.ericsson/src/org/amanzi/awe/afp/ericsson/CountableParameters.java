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
package org.amanzi.awe.afp.ericsson;

/**
 * @author Kasnitskij_V
 * class to parameters with identical main part and different index
 */
public class CountableParameters implements IParameters {
	// some main parameter
	IParameters mainParameter;
	// index of parameter
	int index;
	
	public CountableParameters(IParameters mainParameter, int index) {
		this.mainParameter = mainParameter;
		this.index = index;
	}
	
	@Override
	public int getBytesLen() {
		return 0;
	}

	@Override
	public boolean isBlock() {
		return false;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        result = prime * result + ((mainParameter == null) ? 0 : mainParameter.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CountableParameters)) {
            return false;
        }
        CountableParameters other = (CountableParameters)obj;
        if (index != other.index) {
            return false;
        }
        if (mainParameter == null) {
            if (other.mainParameter != null) {
                return false;
            }
        } else if (!mainParameter.equals(other.mainParameter)) {
            return false;
        }
        return true;
    }

}
