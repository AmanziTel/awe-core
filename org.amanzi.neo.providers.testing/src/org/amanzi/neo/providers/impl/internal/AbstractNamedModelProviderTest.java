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

package org.amanzi.neo.providers.impl.internal;

import java.util.Arrays;
import java.util.List;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.DuplicatedModelException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.exceptions.ParameterInconsistencyException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.models.impl.internal.AbstractNamedModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeUtils;
import org.amanzi.neo.providers.impl.internal.AbstractModelProvider.IKey;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.impl.NodeService.NodeServiceRelationshipType;
import org.amanzi.testing.AbstractMockitoTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AbstractNamedModelProviderTest extends AbstractMockitoTest {

    private static final String MODEL_NAME = "name";

    private static final IKey CACHE_KEY = new AbstractModelProvider.NameKey(MODEL_NAME);

    public static class TestModel extends AbstractNamedModel {

        /**
         * @param nodeService
         * @param generalNodeProperties
         */
        public TestModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties) {
            super(nodeService, generalNodeProperties);
        }

        @Override
        public void finishUp() throws ModelException {

        }

        @Override
        protected void initialize(final Node parentNode, final String name, final INodeType nodeType) throws ModelException {

        }

        @Override
        protected INodeType getModelType() {
            return null;
        }

        @Override
        public Iterable<IDataElement> getChildren(final IDataElement parentElement) throws ModelException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Iterable<IDataElement> getAllElementsByType(final INodeType nodeType) throws ModelException {
            // TODO Auto-generated method stub
            return null;
        }

    }

    public static class TestDataModelProvider extends AbstractNamedModelProvider<IModel, IModel, TestModel> {

        /**
         * @param nodeService
         * @param generalNodeProperties
         */
        protected TestDataModelProvider(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties) {
            super(nodeService, generalNodeProperties);
        }

        @Override
        protected TestModel createInstance() {
            return null;
        }

        @Override
        protected INodeType getModelType() {
            return null;
        }

        @Override
        protected Class< ? extends IModel> getModelClass() {
            return null;
        }

    }

    private static enum TestNodeTypes implements INodeType {
        TEST1;

        @Override
        public String getId() {
            return NodeTypeUtils.getTypeId(this);
        }
    }

    private INodeService nodeService;

    private AbstractModel parent;

    private AbstractNamedModelProvider<IModel, IModel, TestModel> provider;

    private TestModel model;

    private Node parentNode;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        nodeService = mock(INodeService.class);

        parentNode = mock(Node.class);

        parent = mock(AbstractModel.class);
        when(parent.getRootNode()).thenReturn(parentNode);

        model = mock(TestModel.class);

        provider = spy(new TestDataModelProvider(nodeService, new GeneralNodeProperties()));
        doReturn(TestNodeTypes.TEST1).when(provider).getModelType();

        doReturn(model).when(provider).createInstance();
    }

    @Test
    public void testCheckActivityOnCreateWhenNothingFound() throws Exception {
        doReturn(null).when(provider).findByName(parent, MODEL_NAME);

        provider.create(parent, MODEL_NAME);

        verify(parent).getRootNode();
        verify(provider).findByName(parent, MODEL_NAME);
        verify(provider).createInstance();
        verify(model).initialize(parentNode, MODEL_NAME);
        verify(provider).postInitialize(model);
    }

    @Test(expected = DuplicatedModelException.class)
    public void testCheckExceptionOnCreateModelWithExistingName() throws Exception {
        doReturn(parent).when(provider).findByName(parent, MODEL_NAME);

        provider.create(parent, MODEL_NAME);
    }

    @Test
    public void testCheckResultOnFindAllMethod() throws Exception {
        List<Node> nodes = getNodeList();

        doReturn(nodes.iterator()).when(provider).getNodeIterator(parentNode, TestNodeTypes.TEST1);

        List<IModel> result = provider.findAll(parent);

        assertNotNull("list of models should not be null", result);
        assertEquals("unexpected size of list", nodes.size(), result.size());
    }

    @Test
    public void testCheckActivityOnFindAllMethod() throws Exception {
        List<Node> nodes = getNodeList();

        doReturn(nodes.iterator()).when(provider).getNodeIterator(parentNode, TestNodeTypes.TEST1);

        provider.findAll(parent);

        verify(parent).getRootNode();
        verify(provider).getNodeIterator(parentNode, TestNodeTypes.TEST1);
        verify(provider, times(nodes.size())).createInstance();
        for (Node node : nodes) {
            verify(model).initialize(node);
        }
        verify(provider, times(nodes.size())).postInitialize(model);
    }

    @Test
    public void testCheckActivityOnFindByExistingName() throws Exception {
        Node node = getNodeMock();

        doReturn(node).when(provider).getNodeByName(parentNode, MODEL_NAME, TestNodeTypes.TEST1);

        provider.findByName(parent, MODEL_NAME);

        verify(parent).getRootNode();
        verify(provider).getNodeByName(parentNode, MODEL_NAME, TestNodeTypes.TEST1);
        verify(provider).createInstance();
        verify(model).initialize(node);
        verify(provider).postInitialize(model);
    }

    @Test
    public void testCheckActivityOnFindByNotExistingName() throws Exception {
        Node node = getNodeMock();

        doReturn(null).when(provider).getNodeByName(parentNode, MODEL_NAME, TestNodeTypes.TEST1);

        provider.findByName(parent, MODEL_NAME);

        verify(parent).getRootNode();
        verify(provider).getNodeByName(parentNode, MODEL_NAME, TestNodeTypes.TEST1);
        verify(provider, never()).createInstance();
        verify(model, never()).initialize(node);
    }

    @Test
    public void testCheckResultOnFindByExistingName() throws Exception {
        Node node = getNodeMock();

        doReturn(node).when(provider).getNodeByName(parentNode, MODEL_NAME, TestNodeTypes.TEST1);

        IModel result = provider.findByName(parent, MODEL_NAME);

        assertNotNull("Result cannot be null", result);
    }

    @Test
    public void testCheckActiviyOnFindByNotExistingName() throws Exception {
        when(nodeService.getChildByName(parentNode, MODEL_NAME, TestNodeTypes.TEST1, NodeServiceRelationshipType.CHILD))
                .thenReturn(null);

        IModel result = provider.findByName(parent, MODEL_NAME);

        assertNull("result should be null", result);
    }

    @Test(expected = ParameterInconsistencyException.class)
    public void testCheckCreationWithEmptyName() throws Exception {
        provider.create(parent, StringUtils.EMPTY);
    }

    @Test(expected = ParameterInconsistencyException.class)
    public void testCheckCreationWithoutName() throws Exception {
        provider.create(parent, null);
    }

    @Test(expected = ParameterInconsistencyException.class)
    public void testCheckFindWithEmptyName() throws Exception {
        provider.findByName(parent, StringUtils.EMPTY);
    }

    @Test(expected = ParameterInconsistencyException.class)
    public void testCheckFindWithoutName() throws Exception {
        provider.findByName(parent, null);
    }

    @Test
    public void testCacheEnabling() throws Exception {
        Node node = getNodeMock();

        doReturn(node).when(provider).getNodeByName(parentNode, MODEL_NAME, TestNodeTypes.TEST1);
        when(provider.getFromCache(CACHE_KEY)).thenReturn(null);

        provider.findByName(parent, MODEL_NAME);

        verify(provider).getFromCache(CACHE_KEY);
        verify(provider).addToCache(model, CACHE_KEY);
    }

    @Test
    public void testCacheUsing() throws Exception {
        when(provider.getFromCache(CACHE_KEY)).thenReturn(model);

        provider.findByName(parent, MODEL_NAME);

        verify(provider).getFromCache(CACHE_KEY);
        verify(provider, never()).addToCache(model, CACHE_KEY);
        verifyNoMoreInteractions(nodeService);
    }

    private List<Node> getNodeList() {
        return Arrays.asList(getNodeMock(), getNodeMock(), getNodeMock());
    }

}
