package org.amanzi.neo.services;

import junit.framework.Assert;

import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
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
