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

package org.amanzi.awe.views.network.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.INodeType;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.EditPropertiesPage;
import org.amanzi.neo.core.utils.GisProperties;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.services.statistic.IPropertyHeader;
import org.amanzi.neo.services.statistic.PropertyHeader;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.neo4j.graphdb.Node;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * <p>
 * Page for creating new nodes
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class CreateNewNodeWizardPage extends EditPropertiesPage {

    /** int VIEVER_HEIGHT_HINT field */
    private static final int VIEVER_HEIGHT_HINT = 150;
    private final Node sourceNode;

    /**
     * @param pageName
     * @param title
     * @param nodeType
     */
    public CreateNewNodeWizardPage(String pageName, String title, INodeType nodeType, Node sourceNode) {
        super(pageName, title, nodeType);
        this.sourceNode = sourceNode;
    }

    @Override
    public void initProperty() {
        // super.initProperty();
        // propertyList.add(new PropertyWrapper("lat", Double.class, "", false));

        NodeTypes type = NodeTypes.getEnumById(nodeType.getId());
        // propertyList.add(new NewNodePropertyWrapper("name", String.class, "new " +
        // nodeType.getId(), false));
        if (type != null) {
            IPropertyHeader ph = PropertyHeader.getPropertyStatistic(NeoUtils.getParentNode(sourceNode, NodeTypes.NETWORK.getId()));
            Map<String, Object> statisticProperties = ph.getStatisticParams(type);
            for (String key : statisticProperties.keySet()) {
                propertyList.add(new NewNodePropertyWrapper(key, statisticProperties.get(key).getClass(), statisticProperties.get(key).toString(), false));
            }
        }

        // NewNodePropertyWrapper name = new NewNodePropertyWrapper("name", String.class, "new " +
        // nodeType.getId(), false);
        NewNodePropertyWrapper name = new NewNodePropertyWrapper("name", String.class, "", false);
        if (!propertyList.contains(name))
            propertyList.add(name);

        if (type == NodeTypes.SECTOR) {
            NewNodePropertyWrapper ci = new NewNodePropertyWrapper(INeoConstants.PROPERTY_SECTOR_CI, Integer.class, "", false);
            if (!propertyList.contains(ci))
                propertyList.add(ci);
            NewNodePropertyWrapper lac = new NewNodePropertyWrapper(INeoConstants.PROPERTY_SECTOR_LAC, Integer.class, "", false);
            if (!propertyList.contains(lac))
                propertyList.add(lac);
            NewNodePropertyWrapper beamwidth = new NewNodePropertyWrapper("beamwidth", Double.class, "0", false);
            if (!propertyList.contains(beamwidth))
                propertyList.add(beamwidth);
            NewNodePropertyWrapper azimuth = new NewNodePropertyWrapper("azimuth", Integer.class, "0", false);
            if (!propertyList.contains(azimuth))
                propertyList.add(azimuth);
        } else if (type == NodeTypes.SITE) {
            Node networkNode = NeoUtils.getParentNode(sourceNode, NodeTypes.NETWORK.getId());
            Node gis = NeoUtils.getGisNodeByDataset(networkNode);
            GisProperties prop = new GisProperties(gis);
            double[] bb = prop.getBbox();
            String latVal = "0.0",lonVal = "0.0";
            if(bb != null){
                latVal = String.valueOf((bb[2] + bb[3]) / 2D);
                lonVal = String.valueOf((bb[0] + bb[1]) / 2D);
            }
            NewNodePropertyWrapper lat = new NewNodePropertyWrapper(INeoConstants.PROPERTY_LAT_NAME, Double.class, latVal, false);
            if (!propertyList.contains(lat))
                propertyList.add(lat);
            NewNodePropertyWrapper lon = new NewNodePropertyWrapper(INeoConstants.PROPERTY_LON_NAME, Double.class, lonVal, false);
            if (!propertyList.contains(lon))
                propertyList.add(lon);
        }

        Collections.sort(propertyList, new Comparator<PropertyWrapper>() {

            @Override
            public int compare(PropertyWrapper o1, PropertyWrapper o2) {
                if ("name".equalsIgnoreCase(o1.getName())) {
                    return -1;
                }
                if ("name".equalsIgnoreCase(o2.getName())) {
                    return 1;
                }
                if ("ci".equalsIgnoreCase(o1.getName())) {
                    return -1;
                }
                if ("ci".equalsIgnoreCase(o2.getName())) {
                    return 1;
                }
                if ("lac".equalsIgnoreCase(o1.getName())) {
                    return -1;
                }
                if ("lac".equalsIgnoreCase(o2.getName())) {
                    return 1;
                }
                return o1.getName().compareTo(o2.getName());
            }

        });

    }

    /**
     * validate properties
     */
    @Override
    protected void validate() {
        super.validate();
        Node network = NeoUtils.getParentNode(sourceNode, NodeTypes.NETWORK.getId());
        if (!isPageComplete()) {
            return;
        }
        for (int i = 0; i < propertyList.size(); i++) {
            PropertyWrapper wr = propertyList.get(i);
            if (wr.getName().equals(INeoConstants.PROPERTY_NAME_NAME)) {
                LuceneIndexService luceneInd = NeoServiceProvider.getProvider().getIndexService();
                String key = NeoUtils.getLuceneIndexKeyByProperty(network, INeoConstants.PROPERTY_NAME_NAME, nodeType);
                if (luceneInd.getNodes(key, wr.getParsedValue()).size() > 0) {
                    setDescription(String.format("Node with the name '%s' is alredy exist", wr.getDefValue()));
                    setPageComplete(false);
                    return;
                }
            }
            if (wr.getName().equals("type")) {
                setDescription("Property name \"type\" is not allowed.");
                setPageComplete(false);
                return;
            }
        }

        if (nodeType == NodeTypes.SECTOR) {
            PropertyWrapper ciWr = null;
            PropertyWrapper lacWr = null;
            for (int i = 0; i < propertyList.size(); i++) {
                if (propertyList.get(i).getName().equals(INeoConstants.PROPERTY_SECTOR_CI))
                    ciWr = propertyList.get(i);
                if (propertyList.get(i).getName().equals(INeoConstants.PROPERTY_SECTOR_LAC))
                    lacWr = propertyList.get(i);
            }
            try {
                Node sector = NeoUtils.findSector(network, Integer.valueOf(ciWr.getDefValue()), Integer.valueOf(lacWr.getDefValue()), null, true, NeoServiceProvider
                        .getProvider().getIndexService(), NeoServiceProvider.getProvider().getService());
                if (sector != null) {
                    setDescription(String.format("Sector node with CI = '%s' and LAC = '%s' is alredy exist", ciWr.getDefValue(), lacWr.getDefValue()));
                    setPageComplete(false);
                    return;
                }
            } catch (Exception e) {
                setDescription(e.getMessage());
                e.printStackTrace();
                setPageComplete(false);
                return;
            }
        }

        setDescription(getNormalDescription());
        setPageComplete(true);
        return;
    }

    /**
     * The Class NewNodePropertyWrapper.
     */
    protected class NewNodePropertyWrapper extends PropertyWrapper {

        /**
         * Instantiates a new property wrapper.
         * 
         * @param name the name
         * @param type the type
         * @param defValue the def value
         * @param editable the editable
         */
        public NewNodePropertyWrapper(String name, Class< ? > type, String defValue, boolean editable) {
            super(name, type, defValue, editable);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean isValid() {
            if (super.isValid()) {
                return StringUtils.isNotEmpty(getDefValue());
            }
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            NewNodePropertyWrapper other = (NewNodePropertyWrapper)obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        private CreateNewNodeWizardPage getOuterType() {
            return CreateNewNodeWizardPage.this;
        }

    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        ((GridData)viewer.getTable().getLayoutData()).heightHint = VIEVER_HEIGHT_HINT;
    }

    private class NewNodeTableEditableSupport extends TableEditableSupport {

        /**
         * @param viewer
         * @param id
         */
        public NewNodeTableEditableSupport(TableViewer viewer, int id) {
            super(viewer, id);
        }

        @Override
        protected boolean canEdit(Object element) {
            if (id != 2)
                return super.canEdit(element);
            else {
                return true;
            }
        }

    }

    @Override
    protected EditingSupport getEditingSupport(TableViewer viewer, int id) {
        return new NewNodeTableEditableSupport(viewer, id);
    }

    private class NewNodeColLabelProvider extends ColLabelProvider {

        /**
         * @param columnIndex
         */
        public NewNodeColLabelProvider(int columnIndex) {
            super(columnIndex);
        }

        @Override
        public Color getBackground(Object element) {
            if (columnIndex != 2)
                return super.getBackground(element);
            else
                return null;
        }
    }

    @Override
    protected ColLabelProvider getColumnLabelProvider(int id) {
        return new NewNodeColLabelProvider(id);
    }

}
