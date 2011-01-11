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

/**
 * <p>
 * Rir parcer
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class RirParser extends BarRirParser {

    /**
     * Instantiates a new rir parser.
     */
    public RirParser() {
        dataType = DataType.RIR_DATA;
    }
}
