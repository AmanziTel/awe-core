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

package org.amanzi.neo.loader.core.saver;

import java.util.LinkedHashMap;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.INodeType;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.GisProperties;
import org.amanzi.neo.loader.core.parser.HeaderTransferData;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.statistic.StatisticManager;
import org.neo4j.graphdb.Node;

/**
 * <p>
 *Abstract Saver based on  HeaderTransferData
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public abstract  class AbstractHeaderSaver<T extends HeaderTransferData> extends AbstractSaver<T> {

    protected DatasetService service;

    
    protected LinkedHashMap<Node, GisProperties> gisNodes=new LinkedHashMap<Node, GisProperties>();


    protected String rootname;


    @Override
    public void init(T element) {
        super.init(element);
        String rootNodeType=getRootNodeType();
        String projectName = element.getProjectName();
        rootname = element.getRootName();
        service=NeoServiceFactory.getInstance().getDatasetService();
        rootNode=service.findRoot(projectName,rootname);
        if (rootNode==null){
            rootNode= service.createRootNode(projectName,rootname,rootNodeType);
            fillRootNode(rootNode,element);
        }
        statistic=StatisticManager.getStatistic(rootNode);
        
    }
    /**
     * get gisProperties by gis name
     * 
     * @param name gis name
     * @return GisProperties
     */
    protected GisProperties getGisProperties(Node rootNode) {
        GisProperties property = gisNodes.get(rootNode);
        if (property==null){
            Node gis=service.findGisNode(rootNode,true);
            property=new GisProperties(gis);
            gisNodes.put(rootNode, property);
        }
        return property;
    }

    protected boolean setPropertyToNode(String key,String nodeType,Node node,String propertyName,Object propertyValue){
        if (propertyValue==null){
            return false;
        }
        node.setProperty(propertyName, propertyValue);
        return statistic.indexValue(key, nodeType, propertyName, propertyValue);
    }
    protected abstract void fillRootNode(Node rootNode, T element);

    protected abstract String getRootNodeType();

    protected Node addSimpleChild(Node parent, INodeType type, String name) {
        statistic.increaseTypeCount(rootname,type.getId(),1);
        Node child = service.addSimpleChild(parent, type, name);
        service.indexByProperty(rootNode.getId(),child, INeoConstants.PROPERTY_NAME_NAME);
        return child;
    }

    @Override
    public void finishUp(T element) {
        statistic.save();
        for(GisProperties gis:gisNodes.values()){
            gis.setSavedData(statistic.getTotalCount(rootname,NodeTypes.SECTOR.getId()));
            gis.save();
        }
        finishUpIndexes();
        commit(false);
    }




}
