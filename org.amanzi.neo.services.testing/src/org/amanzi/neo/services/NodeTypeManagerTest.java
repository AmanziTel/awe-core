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
package org.amanzi.neo.services;

import junit.framework.Assert;

import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.junit.BeforeClass;
import org.junit.Test;

public class NodeTypeManagerTest {
    
    @BeforeClass
    public static void beforeTest() throws Exception {
        //LN: we need to have loaded DriveModel before test started
        DriveModel.DriveNodeTypes.values();
    }

	@Test
	public void testGetType() {
		for (DriveNodeTypes nodeType : DriveNodeTypes.values()) {
			INodeType t = NodeTypeManager.getType(nodeType.getId());
			Assert.assertEquals(nodeType, t);
		}
	}

	@Test
	public void testGetTypeMultipleTypes() {
		NodeTypeManager.registerNodeType(DatasetTypes.class);
		for (DriveNodeTypes nodeType : DriveNodeTypes.values()) {
			INodeType t = NodeTypeManager.getType(nodeType.getId());
			Assert.assertEquals(nodeType, t);
		}
	}

	@Test
	public void testRegisterNodeTypeTwice() {
		NodeTypeManager.registerNodeType(DatasetTypes.class);
		NodeTypeManager.registerNodeType(DatasetTypes.class);
	}

}
