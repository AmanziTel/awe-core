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
package org.amanzi.awe.ui.operation.shape_export;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.filter.AttributeExpression;
import org.geotools.filter.BBoxExpression;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;
import org.geotools.filter.FilterType;
import org.geotools.filter.GeometryFilter;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Envelope;

public class ShapeExportOp implements IOp {
    /**
     * We have coded this class to be called on both FeatureSource and ILayer
     * targets, so that it can be used in both layer views and catalog views.
     * For the former, make the target class org.geotools.data.FeatureSource.
     * For the latter use instead net.refractions.udig.project.ILayer.
     */
    public void op( final Display display, Object target, IProgressMonitor monitor )
            throws Exception {
        ILayer layer = null;
        FeatureSource source = null;
        if(target instanceof ILayer){
            layer = (ILayer) target;
            source = layer.getResource(FeatureSource.class, monitor);
        }else{
            source = (FeatureSource) target;
        }
        FeatureType featureType = source.getSchema();
        GeometryAttributeType geometryType = featureType.getDefaultGeometry();
        CoordinateReferenceSystem crs = geometryType.getCoordinateSystem();
        String typeName = featureType.getTypeName();
        String filename = typeName.replace(':', '_');
        URL directory = FileLocator.toFileURL(Platform.getInstanceLocation().getURL());
        final URL shpURL = new URL(directory.toExternalForm() + filename + ".shp");
        final List<String> files = new ArrayList<String>();
        display.syncExec(new Runnable(){
            public void run() {
                FileDialog fileDialog = new FileDialog(display.getActiveShell(), SWT.SAVE);
                try {
                    fileDialog.setFileName((new File(shpURL.toURI())).getPath());
                } catch (URISyntaxException e) {
                }
                files.add(fileDialog.open());
            }
        });
        if ((filename = files.get(0)) == null) {
            return;
        }
        final File file = new File(filename);
        promptOverwrite(display, file);
        if (file.exists()) {
            return;
        }
        // create and write the new shapefile
        ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
        Map<String, URL> params = new HashMap<String, URL>();
        params.put("url", file.toURI().toURL());
        ShapefileDataStore dataStore = (ShapefileDataStore) factory.createNewDataStore(params);
        dataStore.createSchema(featureType);
        FeatureStore store = (FeatureStore) dataStore.getFeatureSource();
        FeatureCollection features = null;

        if(layer!=null){
            // We were called from a real layer
            // get the viewport bounds and only export the visible view
            FilterFactory ff = FilterFactoryFinder.createFilterFactory();
            Envelope bounds = layer.getMap().getViewportModel().getBounds();
            CoordinateReferenceSystem worldCrs = layer.getMap().getViewportModel().getCRS();
            MathTransform transform = CRS.findMathTransform(worldCrs, crs, false);
            Envelope bounds_transformed = JTS.transform(bounds, transform);
            // We need the bounding box filter to use the same CRS on both left and
            // right
            BBoxExpression bboxExpression = ff.createBBoxExpression(bounds_transformed);
            GeometryFilter geometryFilter = ff.createGeometryFilter(FilterType.GEOMETRY_BBOX);
            geometryFilter.addRightGeometry(bboxExpression);
            AttributeExpression featureExpression = ff
                    .createAttributeExpression(geometryType.getName());
            geometryFilter.addLeftGeometry(featureExpression);
            features = source.getFeatures(geometryFilter);
        }else{
            // called from catalog, export everything
            features = source.getFeatures();
        }

        store.addFeatures(features);
        dataStore.forceSchemaCRS(crs);
    }

    private void promptOverwrite( final Display display, final File file ) {
        if (!file.exists())
            return;
        display.syncExec(new Runnable(){
            public void run() {
                boolean overwrite = MessageDialog.openConfirm(display.getActiveShell(), "Warning",
                        "File exists - do you wish to overwrite?");
                if (overwrite) {
                    file.delete();
                }
            }
        });
    }
}
