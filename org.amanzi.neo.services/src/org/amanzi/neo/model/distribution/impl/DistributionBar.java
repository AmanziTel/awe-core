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

import org.amanzi.neo.model.distribution.IDistributionBar;
import org.amanzi.neo.model.distribution.IDistributionModel;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.neo4j.graphdb.Node;

/**
 * Implementation of DistributionBar
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class DistributionBar implements IDistributionBar {
    
    static final Color DEFUALT_COLOR = new Color(0.75f, 0.7f, 0.4f);
    
    private Color color = DEFUALT_COLOR;
    
    private int count = 0;
    
    private String name;
    
    private IDataElement rootElement;
    
    private IDistributionModel distribution;
    
    public DistributionBar(IDataElement rootElement, IDistributionModel distribution) {
        this.rootElement = rootElement;
        this.distribution = distribution;
    }
    
    public DistributionBar(IDistributionModel distribution) {
        this.distribution = distribution;
    }
    
    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        if (color == null) {
            color = DEFUALT_COLOR;
        }
        this.color = color;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IDataElement getRootElement() {
        return rootElement;
    }
    
    /**
     * Sets new Count for Bar
     *
     * @param count
     */
    public void setCount(int count) {
        this.count = count;
    }
    
    /**
     * Sets name of Bar
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Sets Root Element
     *
     * @param rootElement
     */
    public void setRootElement(IDataElement rootElement) {
        this.rootElement = rootElement;
    }

    @Override
    public boolean equals(Object o) { 
        if (o instanceof IDistributionBar){ 
            IDistributionBar otherBar = (IDistributionBar)o;
            
            return (otherBar.getName().equals(getName()) && 
                   (otherBar.getRootElement().equals(getRootElement())));
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(IDistributionBar arg0) {
        Node thisRoot = ((DataElement)getRootElement()).getNode();
        Node anotherRoot = ((DataElement)arg0.getRootElement()).getNode();
        
        return new Long(thisRoot.getId()).compareTo(new Long(anotherRoot.getId()));
    }

    @Override
    public IDistributionModel getDistribution() {
        return distribution;
    }    
}
