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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.INodeType;
import org.amanzi.neo.core.utils.GisProperties;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.core.parser.HeaderTransferData;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.statistic.StatisticManager;
import org.neo4j.graphdb.Node;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Abstract Saver based on  HeaderTransferData
 * </p>.
 *
 * @param <T> the generic type
 * @author TsAr
 * @since 1.0.0
 */
public abstract  class AbstractHeaderSaver<T extends HeaderTransferData> extends AbstractSaver<T> {

    /** The service. */
    protected DatasetService service;
    
    /** The property map. */
    protected Map<String, String> propertyMap = new HashMap<String, String>();
    
    /** The gis nodes. */
    protected LinkedHashMap<Node, GisProperties> gisNodes=new LinkedHashMap<Node, GisProperties>();


    /** The rootname. */
    protected String rootname;


    /**
     * Inits the.
     *
     * @param element the element
     */
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
     * get gisProperties by gis name.
     *
     * @param rootNode the root node
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

    /**
     * Sets the property to node.
     *
     * @param key the key
     * @param nodeType the node type
     * @param node the node
     * @param propertyName the property name
     * @param propertyValue the property value
     * @return true, if successful
     */
    protected boolean setPropertyToNode(String key,String nodeType,Node node,String propertyName,Object propertyValue){
        if (propertyValue==null){
            return false;
        }
        node.setProperty(propertyName, propertyValue);
        return statistic.indexValue(key, nodeType, propertyName, propertyValue);
    }
    
    /**
     * Fill root node.
     *
     * @param rootNode the root node
     * @param element the element
     */
    protected abstract void fillRootNode(Node rootNode, T element);

    /**
     * Gets the root node type.
     *
     * @return the root node type
     */
    protected abstract String getRootNodeType();

    /**
     * Adds the simple child.
     *
     * @param parent the parent
     * @param type the type
     * @param name the name
     * @return the node
     */
    protected Node addSimpleChild(Node parent, INodeType type, String name) {
        statistic.increaseTypeCount(rootname,type.getId(),1);
        Node child = service.addSimpleChild(parent, type, name);
        service.indexByProperty(rootNode.getId(),child, INeoConstants.PROPERTY_NAME_NAME);
        return child;
    }

    /**
     * Finish up.
     *
     * @param element the element
     */
    @Override
    public void finishUp(T element) {
        statistic.save();
        for(GisProperties gis:gisNodes.values()){
            gis.setSavedData(statistic.getTotalCount(rootname,getTypeIdForGisCount(gis)));
            gis.save();
        }
        finishUpIndexes();
        commit(false);
    }
    
    protected abstract String getTypeIdForGisCount(GisProperties gis);

    /**
     * Define header.
     *
     * @param headers the headers
     * @param newName the new name
     * @param possibleHeaders the possible headers
     */
    protected void defineHeader(Set<String> headers, String newName, String[] possibleHeaders) {
        if (possibleHeaders == null) {
            return;
        }
        for (String header : headers) {
            if (propertyMap.values().contains(header)) {
                continue;
            }
            for (String headerRegExp : possibleHeaders) {
                Pattern pat=Pattern.compile(headerRegExp,Pattern.CASE_INSENSITIVE);
                Matcher match = pat.matcher(header);
                if (match.matches()) {
                    propertyMap.put(newName, header);
                    return;
                }
            }
        }
    }
    
    /**
     * Gets the string value.
     *
     * @param key the key
     * @param element the element
     * @return the string value
     */
    protected String getStringValue(String key, HeaderTransferData element) {
        String header = propertyMap.get(key);
        if (header == null) {
            header = key;
        }
        return element.get(header);
    }

    /**
     * Gets the number value.
     * 
     * @param <T> the generic type
     * @param klass the klass
     * @param key the key
     * @param element the element
     * @return the number value
     */

    protected <T extends Number> T getNumberValue(Class<T> klass, String key, HeaderTransferData element) {
        String value = getStringValue(key, element);
        return     getNumberValue(klass,value);
        
    }
    
    /**
     * Gets the number value.
     *
     * @param <T> the generic type
     * @param klass the klass
     * @param value the value
     * @return the number value
     */
    protected <T extends Number> T getNumberValue(Class<T> klass, String value) {
        try {
            return NeoUtils.getNumberValue(klass, value);
        } catch (SecurityException e) {
            exception(e);
        } catch (IllegalArgumentException e) {
            exception(e);
        } catch (NoSuchMethodException e) {
            exception(e);
        } catch (IllegalAccessException e) {
            exception(e);
        } catch (InvocationTargetException e) {
            exception(e);
        }
        return null;

    }

    /**
     * Gets the not handled data.
     *
     * @param element the element
     * @param keyId the key id
     * @param typeId the type id
     * @return the not handled data
     */
    protected Map<String, Object> getNotHandledData(HeaderTransferData element,String keyId,String typeId) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (String key : element.keySet()) {
            if (!propertyMap.values().contains(key)) {
                Object value=statistic.parseValue(keyId,typeId,key,element.get(key));
                if(value!=null){
                    result.put(key, value);
                }
                continue;
            }
        }
        return result;
    }
    protected boolean  setUnparsedProperty(Node node, String keyId, String typeId, String propertyName, String stringValue) {
        Object value=statistic.parseValue(keyId,typeId,propertyName,stringValue);
        if (value!=null){
            return setProperty(keyId,typeId,node,propertyName,value);
        }
        return false;
    }
    
    
}
