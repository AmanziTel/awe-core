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
package org.amanzi.awe.script;

import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;

import net.refractions.udig.ui.operations.IOp;

public class FeatureCollectionInfoOpJava implements IOp {

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        FeatureSource data = (FeatureSource) target;
        FeatureCollection features = data.getFeatures();

        // Iterate with normal iterator
        Iterator featureIterator = features.iterator();
        int count = 0;
        try{
            monitor.beginTask("Java iteration", features.size());
            System.out.println("Java iterating over "+features.size()+" features:");
            while(featureIterator.hasNext()){
                Feature feature = (Feature)featureIterator.next();
                if(count<10){
                    System.out.println("    "+feature.getID());
                }
                count++;
                monitor.worked(1);
                if(monitor.isCanceled()) break;
                if(count%100 == 0) Thread.sleep(100);   // makes the process slow enough to test cancellation command
            }
            if(count>=10){
                System.out.println("... and "+(count-10)+" more features suppressed");
            }
        }catch(Exception e){
            System.err.println("Error iterating over feature collection: "+e);
            e.printStackTrace(System.err);
        }finally{
            features.close(featureIterator);
            monitor.done();
        }
    }

}
