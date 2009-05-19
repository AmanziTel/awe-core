package org.amanzi.awe.catalog.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveChangeListener;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.amanzi.awe.catalog.json.beans.ExtJSON;
import org.amanzi.awe.catalog.json.beans.GisGeo;
import org.amanzi.awe.views.network.utils.TreeViewContentProvider;
import org.amanzi.awe.views.network.utils.ViewLabelProvider;
import org.amanzi.awe.views.network.views.NetworkTreeView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class JSONReader {
    private URL url; // URL to start reading (might be only a header URL)
    private URL dataURL; // optional data URL in case it was automatically determined from header
    // URL (or vice versa)
    private JSONObject data;
    private CoordinateReferenceSystem crs;
    private ReferencedEnvelope bounds;
    private String name;
    private Boolean networkGeoJSON;
    private final CharSequence csSite = "site";

    private ExtJSON extJSON;
    private GisGeo gisGeo;

    public JSONReader() {
        super();
        CatalogPlugin.addListener(new MyResolveChangeReporter());
    }

    public JSONReader( final JSONService service ) {
        this();
        this.url = service.getValidURL();
        this.dataURL = service.getURL();
    }

    /**
     * @param baseUrl
     * @param href
     * @return
     * @throws MalformedURLException
     */
    public static URL createRelativeURL( final URL baseUrl, final String href )
            throws MalformedURLException {
        return new URL(baseUrl, new File(baseUrl.toString()).getParent() + href);
    }
    /**
     * Reads ExtJSON data structure into object and makes a cache in field of this class.
     * 
     * @return {@link ExtJSON}object
     * @throws IOException file not found
     */
    public final ExtJSON getExtJSON() throws IOException {
        if (extJSON == null) {
            extJSON = new ExtJSON(JSONObject.fromObject(readURL(url)));
        }
        return extJSON;
    }
    /**
     * Reads GisGeo data structure into object and makes a cache in field of this class.
     * 
     * @return {@link GisGeo}object
     * @throws IOException file not found
     */
    public final GisGeo getGisGeo() throws IOException {
        if (gisGeo == null) {
            final String href = getExtJSON().getExtGis().getHref();
            gisGeo = new GisGeo(JSONObject.fromObject(readURL(createRelativeURL(url, href))));
        }
        return gisGeo;
    }

    /**
     * This method is used internally by the other data methods, and caches its results for future
     * queries. It is available here for code that wishes to investigate the JSON more specifically.
     * 
     * @return the entire dataset as a JSONObject instance
     * @throws IOException file not found
     */
    public final JSONObject jsonObject() throws IOException {
        if (data == null) {
            final String href = getGisGeo().getFeatureSource().getHref();
            data = JSONObject.fromObject(readURL(createRelativeURL(url, href)));
        }
        return data;
    }

    /**
     * Read a file from given URL.
     * 
     * @param url {@link URL}object
     * @return file as string
     */
    public static String readURL( final URL url ) {
        final StringBuilder sb = new StringBuilder();
        Reader reader = null;
        try {
            reader = new InputStreamReader(url.openStream(), "UTF8");
            final char[] buffer = new char[1024];
            int bytesRead = 0;

            while( (bytesRead = reader.read(buffer)) >= 0 ) {
                if (bytesRead > 0) {
                    sb.append(buffer);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to get features from url '" + url + "': " + e);
            e.printStackTrace(System.err);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return sb.toString();
    }

    /**
     * Find the Coordinate Reference System in the JSON, or default to WGS84 if none found.
     * 
     * @return CoordinateReferenceSystem
     */
    public final CoordinateReferenceSystem getCRS() {
        return getCRS(DefaultGeographicCRS.WGS84);
    }

    /**
     * Find the Coordinate Reference System in the JSON, or default to the specified default if no
     * CRS is found in the JSON.
     * 
     * @param defaultCRS {@link CoordinateReferenceSystem} object
     * @return {@link CoordinateReferenceSystem} object
     */
    public final CoordinateReferenceSystem getCRS( final CoordinateReferenceSystem defaultCRS ) {
        if (crs == null) {
            crs = defaultCRS; // default if crs cannot be found below
            try {
                JSONObject jsonCRS = jsonObject().getJSONObject("crs");
                System.out.println("Determining CRS from JSON: " + jsonCRS.toString());
                JSONObject jsonCRSProperties = jsonCRS.getJSONObject("properties");
                if (jsonCRS.getString("type").equals("name")) {
                    // The simple approach is to name the CRS, eg. EPSG:4326 (GeoJSON spec prefers a
                    // new naming standard, but I'm not sure geotools knows it)
                    crs = CRS.decode(jsonCRSProperties.getString("name"));
                } else if (jsonCRS.getString("type").equals("link")) {
                    // TODO: This type is specified in GeoJSON spec, but what the HREF means is not,
                    // so we assume it is a live URL that will feed a CRS specification directly
                    URL crsURL = new URL(jsonCRSProperties.getString("href"));
                    crs = CRS.decode(crsURL.getContent().toString());
                }
            } catch (Exception crs_e) {
                System.err.println("Failed to interpret CRS: " + crs_e.getMessage());
                crs_e.printStackTrace(System.err);
            }
        }
        return crs;
    }

    /**
     * Find the bounding box for the data set as a ReferenceEnvelope. It uses the getCRS method to
     * find the reference system then looks for explicit "bbox" elements, and finally, if no bbox
     * was found, scans all feature geometries for coordinates and builds the bounds on those. The
     * result is cached for future calls.
     * 
     * @return ReferencedEnvelope for bounding box
     */
    public final ReferencedEnvelope getBounds() {
        if (bounds == null) {
            // Create Null envelope
            this.bounds = new ReferencedEnvelope(getCRS());
            // First try to find the BBOX definition in the JSON directly
            try {
                JSONArray jsonBBox = jsonObject().getJSONArray("bbox");
                if (jsonBBox != null) {
                    System.out.println("Interpreting GeoJSON BBox: " + jsonBBox.toString());
                    double minX = jsonBBox.getDouble(0);
                    double minY = jsonBBox.getDouble(1);
                    double maxX = jsonBBox.getDouble(2);
                    double maxY = jsonBBox.getDouble(3);
                    this.bounds = new ReferencedEnvelope(minX, maxX, minY, maxY, crs);
                } else {
                    System.err.println("No BBox defined in the GeoJSON object");
                }
            } catch (Exception e) {
                System.err.println("Failed to interpret BBOX: " + e.getMessage());
                e.printStackTrace(System.err);
            }
            // Secondly, if bounds is still empty, try find all feature geometries and calculate
            // bounds
            // This should only work if the JSON actually contains the data, which should not be the
            // case for a header-only JSON stream.
            try {
                if (this.bounds.isNull()) {
                    // Try to create envelope from any data contained in the GeoJSON object (fails
                    // for header, but then that should have a bbox)
                    JSONArray features = jsonObject().getJSONArray("features");
                    if (features != null && features.size() > 0) {
                        for( int i = 0; i < features.size(); i++ ) {
                            JSONObject feature = features.getJSONObject(i);
                            JSONObject geometry = feature.getJSONObject("geometry");
                            if (geometry != null) {
                                String geometryType = geometry.getString("type");
                                JSONArray coordinates = geometry.getJSONArray("coordinates");
                                if (geometryType.equals("Point")) {
                                    this.bounds.expandToInclude(coordinates.getDouble(0),
                                            coordinates.getDouble(1));
                                } else {
                                    for( int x = 0; x < coordinates.size(); x++ ) {
                                        JSONArray coords = coordinates.getJSONArray(x);
                                        this.bounds.expandToInclude(coords.getDouble(0), coords
                                                .getDouble(1));
                                    }
                                }
                            } else {
                                System.err.println("Failed to find geometry in feature: "
                                        + feature.toString());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to interpret BBOX: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
        return bounds;
    }

    /**
     * Return the name of the dataset as specified in the JSON, or default to the URL.getFile().
     * 
     * @return dataset name
     */
    public final String getName() {
        if (name == null) {
            try {
                if (jsonObject().has("name")) {
                    name = jsonObject().getString("name");
                } else {
                    name = url.getFile();
                }
            } catch (IOException e) {
                System.err.println("Failed to find name element: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
        return name;
    }

    /**
     * Checks does this FeatureCollection contains name field and does name contains site word in
     * it.
     * 
     * @return Network GeoJSON indicator, false if it is not, true if it is
     */
    public final boolean isNetworkGeoJSON() {
        if (networkGeoJSON == null) {
            try {
                boolean containsKeyName = jsonObject().containsKey("name");
                if (containsKeyName) {
                    final String strName = jsonObject().getString("name");
                    if (strName.toLowerCase().contains(csSite)) {
                        networkGeoJSON = true;
                    } else {
                        // it contains name, but name does not contain 'site'
                        // string, its not network GeoJSON
                        networkGeoJSON = false;
                    }
                } else {
                    // does not contain name, its not network GeoJSON
                    networkGeoJSON = false;
                }
            } catch (IOException e) {
                networkGeoJSON = false;
                // this can never occur, since we execution can end up if
                // jsonObject().getString("name") throws exception, but if there
                // is no 'name' we will not try to fetch it
            }
        }
        return networkGeoJSON;
    }

    /**
     * Return a descriptive string of this dataset. This is based on the name, crs and bounding box.
     * 
     * @return descriptive string
     */
    public final String toString() {
        return "JSON[" + getName() + "]: CRS:" + getCRS() + " Bounds:" + getBounds();
    }

    public final FeatureIterator getFeatures() {
        JSONArray features = null;
        JSONObject featureSource = null;
        try {
            if (jsonObject().has("features")) {
                features = jsonObject().getJSONArray("features");
            }
            if (jsonObject().has("feature_source")) {
                featureSource = jsonObject().getJSONObject("feature_source");
            }
        } catch (Exception e) {
            System.err.println("Failed to find features collection: " + e);
        }
        if (features != null) {
            // We have a feature collection, so let's use that directly as the data
            return new JSONFeatureReader(features);
        } else if (featureSource != null) {
            // We have a reference to an alternative data source, use that
            try {
                String dataType = featureSource.getString("type");
                URL feature_url = new URL(url, featureSource.getString("href"));
                if (dataType != null && dataType.toLowerCase().equals("csv")) {
                    return new CSVURLFeatureReader(feature_url); // Connect to a URL and
                    // interpret as CSV stream
                } else if (dataType != null && dataType.toLowerCase().endsWith("json")) {
                    // support .json and .geo_json
                    return new JSONURLFeatureReader(feature_url); // Connect to a URL and
                    // interpret as JSON stream
                } else if (this.dataURL != null) {
                    return new JSONURLFeatureReader(dataURL); // Connect to data URL if specified,
                    // and assume it's JSON (we get here
                    // if the data URL is a simple
                    // modification of the header URL)
                } else {
                    System.err
                            .println("JSON URL contained no features, nor a reference to an alternative feature source: "
                                    + this.url);
                    return null;
                }
            } catch (MalformedURLException e) {
                System.err.println("Failed to determine feature source URL: " + e);
                e.printStackTrace(System.err);
                return null;
            }
        } else {
            // TODO: Support geometries collection also (like features but without bbox and
            // properties)
            System.err
                    .println("JSON contains no features collection, or a featues_source reference");
            return null;
        }
    }

    public class MyResolveChangeReporter implements IResolveChangeListener {
        public final void changed( final IResolveChangeEvent event ) {

            switch( event.getType() ) {
            case POST_CHANGE:
                System.out.println("Resources have changed.");
                try {
                    updateNetworkTreeView(JSONReader.this.getExtJSON().getExtTree().getHref(),
                            JSONReader.this.getUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            // unused yet
            case PRE_CLOSE:
                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                IWorkbenchPart part = window.getActivePage().findView(
                        NetworkTreeView.NETWORK_VIEW_ID);
                window.getActivePage().hideView((IViewPart) part);
                break;
            case PRE_DELETE:
            default:
                throw new IllegalStateException("Unexpected state occured!");
            }
        }
        /**
         * Below Code is added by Sachin P After loading the map, Network tree view should be shown.
         * Below code creates view in same UI thread and same renders a view which is populated with
         * geo_JSON data in tree format.
         */
        private void updateNetworkTreeView( final String treeHref, final URL baseUrl ) {
            final Display display = PlatformUI.getWorkbench().getDisplay();
            display.syncExec(new Runnable(){

                public void run() {

                    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

                    try {
                        // Finding if the view is opened.
                        IWorkbenchPart part = window.getActivePage().findView(
                                NetworkTreeView.NETWORK_VIEW_ID);

                        if (part != null) {
                            window.getActivePage().hideView((IViewPart) part);
                        }

                        NetworkTreeView viewPart = (NetworkTreeView) window.getActivePage()
                                .showView(NetworkTreeView.NETWORK_VIEW_ID, null,
                                        IWorkbenchPage.VIEW_ACTIVATE);

                        viewPart.getViewer().setContentProvider(
                                new TreeViewContentProvider(treeHref, baseUrl));
                        viewPart.getViewer().setLabelProvider(new ViewLabelProvider());
                        viewPart.getViewer().setInput(viewPart.getViewSite());
                        viewPart.makeActions();
                        viewPart.hookDoubleClickAction();
                        viewPart.setFocus();
                        window.getActivePage().activate(viewPart);
                    } catch (PartInitException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    public final URL getUrl() {
        return url;
    }

}
