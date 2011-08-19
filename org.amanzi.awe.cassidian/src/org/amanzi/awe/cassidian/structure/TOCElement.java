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

package org.amanzi.awe.cassidian.structure;

import java.util.Calendar;

import org.amanzi.awe.cassidian.constants.ChildTypes;

public class TOCElement extends AbstractTOCTTC {
	private static final String TYPE=ChildTypes.TOC.getId();
	public TOCElement(String probeID, String calledNumber, Integer hook,
			Integer simplex, Integer priority, Calendar configTime, Calendar setupTime,
			Calendar connectTime, Calendar disconnectTime, Calendar releaseTime,
			Integer causeForTermination, PESQResultElement pesqResult) {
		super(probeID, calledNumber, hook, simplex, priority, configTime, setupTime,
				connectTime, disconnectTime, releaseTime, causeForTermination,pesqResult);
		// TODO Auto-generated constructor stub
	}
	public TOCElement(){
		super();
	}

    @Override
    public String getType() {
        return TYPE;
    }

}
