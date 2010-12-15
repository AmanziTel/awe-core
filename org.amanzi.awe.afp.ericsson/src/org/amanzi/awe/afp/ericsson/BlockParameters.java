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
 * class to represent block of parameters
 */
public class BlockParameters implements IParameters {
	
	// count of parameters in block
	private final int count;
	// parameters in block
	private IParameters[] parameters;
	
	// constructor to block parameters
	public BlockParameters(int count, IParameters...parameters) {
		this.setParameters(parameters);
		this.count = count;
	}
	
	@Override
	public int getBytesLen() {
		return 0;
	}

	@Override
	public boolean isBlock() {
		return true;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(IParameters[] parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the parameters
	 */
	public IParameters[] getParameters() {
		return parameters;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

}
