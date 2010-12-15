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

import org.amanzi.awe.afp.ericsson.DataType;
import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.parser.IDataElement;

/**
 * @author Kasnitskij_V
 * class to represent of recordConfigData
 */
public class RecordConfigData extends CommonConfigData implements IDataElement {
	
	// type of data(RIR of BAR)
	private DataType dataType;

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the dataType
	 */
	public DataType getDataType() {
		return dataType;
	}
	
	
}
