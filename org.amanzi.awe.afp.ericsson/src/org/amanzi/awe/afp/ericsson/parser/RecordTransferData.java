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
import org.amanzi.neo.loader.core.parser.BaseTransferData;

/**
 * <p>
 *Transfer data between BAR/RIR parser and saver
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class RecordTransferData extends BaseTransferData{
    /** long serialVersionUID field */
    private static final long serialVersionUID = -7272835324152268894L;
    private DataType type;
    private MainRecord record;
    public DataType getType() {
        return type;
    }
    public void setType(DataType type) {
        this.type = type;
    }
    public MainRecord getRecord() {
        return record;
    }
    public void setRecord(MainRecord record) {
        this.record = record;
    }

}
