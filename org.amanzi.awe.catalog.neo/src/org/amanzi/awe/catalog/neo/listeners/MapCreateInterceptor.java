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

package org.amanzi.awe.catalog.neo.listeners;

import java.io.IOException;

import net.refractions.udig.project.interceptor.MapInterceptor;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectPackage;

import org.amanzi.awe.catalog.neo.NeoGeoResource;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class MapCreateInterceptor implements MapInterceptor {

    @Override
    public void run(Map map) {
        Adapter superListener = new AdapterImpl() {
            @Override
            public void notifyChanged(final Notification msg) {
                System.out.println(msg.getNotifier().getClass());
                if (msg.getNotifier() instanceof Layer) {
                    Layer layer = (Layer)msg.getNotifier();
                    if (layer.getGeoResource().canResolve(NeoGeoResource.class)) {
                        System.out.println(msg.getFeatureID(Layer.class));
                        if (msg.getFeatureID(Layer.class) == ProjectPackage.LAYER__CRS) {
                            switch (msg.getEventType()) {
                            case Notification.ADD:
                            case Notification.ADD_MANY: {
                                // ...
                                break;
                            }
                            case Notification.SET: {
                                CoordinateReferenceSystem crs = (CoordinateReferenceSystem)msg.getNewValue();
                                storeCrs(layer,crs);
                                break;
                            }
                            }
                        }
                    }
                }
            }
            
            private void storeCrs(Layer layer, final CoordinateReferenceSystem crs) {

                try {
                    final NeoGeoResource resource=layer.getGeoResource().resolve(NeoGeoResource.class, null);
                    Job job=new Job("StoreCrs"){

                        @Override
                        protected IStatus run(IProgressMonitor monitor) {
                            GraphDatabaseService service = NeoServiceProviderUi.getProvider().getService();
                            Transaction tx = service.beginTx();
                            try{
                                NeoUtils.setCRS(resource.getGeoNeo(monitor).getMainGisNode(),crs,service);
                                resource.getGeoNeo(monitor).getMainGisNode().setProperty(INeoConstants.PROPERTY_WKT_CRS, crs.toWKT());
                                tx.success();
                                return Status.OK_STATUS;
                            } catch (Exception e) {
                                // TODO Handle IOException
                                throw (RuntimeException) new RuntimeException( ).initCause( e );
                            }finally{
                               tx.finish(); 
                            }
                        }
                        
                    };
                    job.schedule();
                    try {
                        job.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    resource.updateCRS();
                    layer.refresh(null);
                } catch (IOException e) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
                
            }
        };

        map.addDeepAdapter(superListener);
    }

}
