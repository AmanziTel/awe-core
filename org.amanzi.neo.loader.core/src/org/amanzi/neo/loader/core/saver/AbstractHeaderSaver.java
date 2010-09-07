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

import java.io.PrintStream;
import java.util.LinkedHashMap;

import org.amanzi.neo.core.utils.GisProperties;
import org.amanzi.neo.loader.core.parser.HeaderTransferData;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.StatisticHandler;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public abstract  class AbstractHeaderSaver<T extends HeaderTransferData> implements ISaver<T> {

    protected DatasetService service;
    protected Node rootNode;
    protected StatisticHandler statistic;
    protected PrintStream outputStream;
    protected LinkedHashMap<Node, GisProperties> gisNodes=new LinkedHashMap<Node, GisProperties>();
    protected T element;

    @Override
    public void init(T element) {
        this.element = element;
        String rootNodeType=getRootNodeType();
        String projectName = element.getProjectName();
        String rootname = element.getProjectName();
        service=NeoServiceFactory.getInstance().getDatasetService();
         rootNode=service.findRoot(projectName,rootname);
        if (rootNode==null){
            rootNode= service.createRootNode(projectName,rootname,rootNodeType);
            fillRootNode(rootNode,element);
        }
        statistic=new StatisticHandler();
        statistic.loadStatistic(rootNode);
        
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
            //TODO implement
            Node gis=service.findGisNode(rootNode,true);
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


    @Override
    public void finishUp(T element) {
        statistic.saveStatistic(rootNode);
    }

    @Override
    public PrintStream getPrintStream() {
        if (outputStream==null){
            return System.out;
        }
        return outputStream;
    }

    @Override
    public void setPrintStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }


}
