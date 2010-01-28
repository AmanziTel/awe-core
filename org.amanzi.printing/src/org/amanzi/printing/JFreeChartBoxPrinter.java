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

package org.amanzi.printing;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import net.refractions.udig.printing.model.AbstractBoxPrinter;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Dimension;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * BoxPrinter that prints JFreeChart
 * @author Pechko_E
 * @since 1.0.0
 */
public class JFreeChartBoxPrinter extends AbstractBoxPrinter implements IAdaptable {
    private JFreeChart chart;

    @Override
    public void draw(Graphics2D graphics, IProgressMonitor monitor) {
        Dimension size=this.getBox().getSize();
        if (chart!=null){
        chart.draw(graphics, new Rectangle(size.width, size.height));
        }else{
            chart=createChart();//TODO perhaps we should call wizard here
        }
    }
    
    private JFreeChart createChart() {
        DefaultCategoryDataset ds=new DefaultCategoryDataset();
        ds.addValue(10, "series1", "jan");
        ds.addValue(20, "series1", "feb");
        ds.addValue(15, "series1", "mar");
     // create the chart...
        return ChartFactory.createBarChart("Test chart", // chart title
                "values", // domain axis label
                "range", // range axis label
                ds, // data
                PlotOrientation.HORIZONTAL, // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
                );
    }

    @Override
    public String getExtensionPointID() {
        return "org.amanzi.splash.printing.chartBoxPrinter";//TODO
    }

    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }
    public void setChart(JFreeChart chart){
        this.chart=chart;
        setDirty(true);
        
    }

}
