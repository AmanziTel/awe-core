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

package org.amanzi.awe.report.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.awe.filters.experimental.GroupFilter;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.geotools.data.FeatureSource;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.IProjectElement;
import net.refractions.udig.project.internal.Blackboard;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.impl.LayerImpl;
import net.refractions.udig.project.internal.impl.MapImpl;
import net.refractions.udig.project.ui.ApplicationGIS;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class GIS {
    public static final String FILTER = "FILTER";

    public static IGeoResource findGeoNeo(ILayer layer) {
        return layer.findGeoResource(GeoNeo.class);
    }

    public static GeoNeo resolveGeoNeo(ILayer layer) throws IOException {
        final long start = System.currentTimeMillis();
        final IGeoResource resource = layer.findGeoResource(GeoNeo.class);
        final GeoNeo geoNeo = resource.resolve(GeoNeo.class, null);
        System.out.println("[DEBUG] time: " + (System.currentTimeMillis() - start));
        return geoNeo;
    }

    public static List<MapImpl> getAllMaps() {
        List<MapImpl> maps = new ArrayList<MapImpl>();
        final List<IProjectElement> elements = ProjectPlugin.getPlugin().getProjectRegistry().getCurrentProject().getElements();
        for (IProjectElement element : elements) {
            if (element instanceof MapImpl) {
                MapImpl map = (MapImpl)element;
                if (!map.eIsProxy()) {
                    maps.add(map);
                }
            }
        }
        return maps;
    }

    public static void printFeatures(IMap map) {
        for (ILayer layer : map.getMapLayers()) {
            try {
                FeatureSource source = layer.getResource(FeatureSource.class, new NullProgressMonitor());
                if (source != null) {
                    FeatureCollection collection = source.getFeatures(layer.getFilter());

                    if (collection != null) {
                        FeatureIterator features = collection.features();

                        try {
                            while (features.hasNext()) {
                                final Feature feature = features.next();
                                System.out.println("[DEBUG] feature " + (feature == null ? null : feature));
                                final Geometry defaultGeometry = feature.getDefaultGeometry();
                                if (defaultGeometry!=null && defaultGeometry instanceof MultiPolygon){
                                    System.out.println("[DEBUG] Geometry type=" + defaultGeometry.getGeometryType()+" "+(defaultGeometry == null ? null : defaultGeometry));
                                    final Envelope envelope = defaultGeometry.getEnvelopeInternal();
                                    System.out.println("[DEBUG] envelope " + envelope);
                                    System.out.println("[DEBUG] coordinates:");
                                    for (Coordinate c:defaultGeometry.getCoordinates()){
                                        System.out.println(c.x+"\t"+c.y);
                                    }
                                    
                                }
                                
                            }
                        } finally {
                            features.close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static  List<Feature> getSelectedFeatures(IMap map) {
        List<Feature> features=new ArrayList<Feature>();
        for (ILayer layer : map.getMapLayers()) {
            try {
                FeatureSource source = layer.getResource(FeatureSource.class, new NullProgressMonitor());
                if (source != null) {
                    FeatureCollection collection = source.getFeatures(layer.getFilter());
                    
                    if (collection != null) {
                        FeatureIterator featuresIterator = collection.features();
                        
                        try {
                            while (featuresIterator.hasNext()) {
                                features.add(featuresIterator.next());
                            }
                        } finally {
                            featuresIterator.close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return features;
    }
}
