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

package org.amanzi.neo.model.distribution.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistributionBar;
import org.amanzi.neo.model.distribution.IDistributionModel;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.model.distribution.IRange;
import org.amanzi.neo.services.DistributionService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.impl.AbstractModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.neo4j.graphdb.Node;

/**
 * Model for Distribution 
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class DistributionModel extends AbstractModel implements IDistributionModel {
    
    private static final Logger LOGGER = Logger.getLogger(DistributionModel.class);
    
    /*
     * Color property of Distribution Bar node 
     */
    static final String BAR_COLOR = "color";
    
    /*
     * Count property of Distribution Bar node
     */
    static final String COUNT = "count"; 
    
    /*
     * Distribution Service
     */
    static DistributionService distributionService = NeoServiceFactory.getInstance().getDistributionService();
    
    private IDistributionalModel analyzedModel;
    
    private IDistribution distributionType;
    
    private boolean isExist = false;
    
    private int count;
    
    /**
     * Returns 
     * @param parentNode
     * @param distributionName
     * @throws DatabaseException
     */
    public DistributionModel(IDistributionalModel analyzedModel, IDistribution distributionType) throws DatabaseException {
        LOGGER.debug("start new DistributionModel()");
        
        //validate input
        if (analyzedModel == null) {
            LOGGER.error("Analyzed Model cannot be null");
            throw new IllegalArgumentException("Analyzed Model cannot be null");
        }
        if (distributionType == null) {
            LOGGER.error("Distribution Type cannot be null");
            throw new IllegalArgumentException("Distribution Type cannot be null");
        }
        
        //try to find in DB
        rootNode = distributionService.findRootAggregationNode(analyzedModel.getRootNode(), distributionType.getName());
        if (rootNode == null) {
            LOGGER.info("Creating new Distribution Structure for <" + analyzedModel + ", " + distributionType + ">");
            
            rootNode = distributionService.createRootAggregationNode(analyzedModel.getRootNode(), distributionType.getName());
        } else {
            isExist = true;
            LOGGER.info("Distribution Found for <" + analyzedModel + ", " + distributionType + ">");
        }
        
        //initialize other fields
        this.analyzedModel = analyzedModel;
        this.distributionType = distributionType;
        this.name = distributionType.getName();
        this.nodeType = NodeTypeManager.getType(DistributionService.getNodeType(rootNode));
        this.count = (Integer)rootNode.getProperty(COUNT, 0);
        
        LOGGER.debug("finish new DistributionModel()");
    }
    
    @Override
    public IDistribution getDistributionType() {
        return distributionType;
    }

    @Override
    public List<IDistributionBar> getDistributionBars() {
        return getDistributionBars(new NullProgressMonitor());
    }
    
    /**
     * Creates Distirubtion Bar from Node
     *
     * @param barNode
     * @return
     */
    private DistributionBar createDistributionBar(Node barNode) {
        //create root element
        DataElement rootElement = new DataElement(barNode);
        //create distribution bar
        DistributionBar distributionBar = new DistributionBar(rootElement);
        
        //load properties
        Integer count = (Integer)rootElement.get(COUNT);
        if (count != null) {
            distributionBar.setCount(count);
        }
        Color color = getColor(rootElement);
        if (color != null) {
            distributionBar.setColor(color);
        }
        String name = (String)rootElement.get(NewAbstractService.NAME);
        distributionBar.setName(name);
        
        return distributionBar;
    }
    
    /**
     * Converts Color property from Node to Color Object
     *
     * @param rootElement bar element
     * @return
     */
    private Color getColor(DataElement rootElement) {
        int[] colorArray = (int[])rootElement.get(BAR_COLOR);
        
        if (colorArray != null) {
            return new Color(colorArray[0], colorArray[1], colorArray[2]);
        } else {
            return null;
        }
    }
    
    /**
     * Creates Distribution Database structure
     */
    private List<IDistributionBar> createDistribution(IProgressMonitor monitor) {
        monitor.beginTask("Creating Distribution <" + getName() + "> in Database", distributionType.getCount());
        
        List<IDistributionBar> result = createDistributionBars();
        
        monitor.done();
        
        return result;
    }
    
    /**
     * Creates list of Distribution Bars from Ranges
     *
     * @return
     */
    private List<IDistributionBar> createDistributionBars() {
        ArrayList<IDistributionBar> result = new ArrayList<IDistributionBar>();
        
        Node previousBar = null;
        
        for (IRange range : distributionType.getRanges()) {
            IDistributionBar bar = createDistributionBar(previousBar, range);
            
            previousBar = ((DataElement)bar.getRootElement()).getNode();
        }
        
        return result;
    }
    
    /**
     * Creates single Distribution 
     *
     * @param range
     * @return
     */
    private IDistributionBar createDistributionBar(Node previousBar, IRange range) {
        DistributionBar distributionBar = new DistributionBar();
        
        return distributionBar;
    }
    
    /**
     * Loads Distribution from Database
     *
     * @return
     */
    private List<IDistributionBar> loadDistribution(IProgressMonitor monitor) {
        List<IDistributionBar> distributionBars = new ArrayList<IDistributionBar>();
        
        monitor.beginTask("Loading Distribution <" + getName() + "> from Database", count);
        
        for (Node distributionBarNode : distributionService.findAggregationBars(getRootNode())) {
            distributionBars.add(createDistributionBar(distributionBarNode));
            monitor.worked(1);
        }
        
        monitor.done();
        
        LOGGER.info("Loaded <" + distributionBars.size() + "> distribution bars from Database");
        
        return distributionBars;
    }

    @Override
    public void updateBar(IDistributionBar bar) {
    }

    @Override
    public List<IDistributionBar> getDistributionBars(IProgressMonitor monitor) {
        LOGGER.debug("start getDistributionBars()");
        
        List<IDistributionBar> result = null;
        
        if (!isExist) {
            LOGGER.info("No Distribution <" + getName() + ">. Create new one");
            result = createDistribution(monitor);
            isExist = true;
        } else {
            LOGGER.info("Load Distribution <" + getName() + "> from Database");
            result = loadDistribution(monitor);
        }
    
        LOGGER.debug("finish getDistributionBars()");
        
        return result;
    }

}
