package org.amanzi.awe.catalog.json;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import com.csvreader.CsvReader;

class CSVURLFeatureReader extends FeatureIterator {
    private URL feature_url;
    private CsvReader reader;
    private int x_col = -1;
    private int y_col = -1;
    private int name_col = -1;

    public CSVURLFeatureReader( final URL feature_url ) {
        this.feature_url = feature_url;
    }
    private void setupFeatures() {
        try {
            if (reader != null) {
                reader.close();
            }
            reader = new CsvReader(new InputStreamReader(feature_url.openStream()));
            reader.readHeaders(); // Assume all CSV files have a header line
            HashMap<String, Integer> headers = new HashMap<String, Integer>();
            for( String header : reader.getHeaders() )
                headers.put(header.toLowerCase(), headers.size());
            for( String head : new String[]{"long", "longitude", "x"} ) {
                if (headers.containsKey(head)) {
                    x_col = headers.get(head);
                }
            }
            for( String head : new String[]{"lat", "latitude", "y"} ) {
                if (headers.containsKey(head)) {
                    y_col = headers.get(head);
                }
            }
            for( String head : new String[]{"description", "name"} ) {
                if (headers.containsKey(head)) {
                    name_col = headers.get(head);
                }
            }
            // test for invalid x and y columns
            if (x_col < 0 || x_col >= reader.getHeaderCount()) {
                throw new Exception("Invalid easting column: " + x_col);
            }
            if (y_col < 0 || y_col >= reader.getHeaderCount()) {
                throw new Exception("Invalid northing column: " + y_col);
            }
            // fix invalid name_col
            int loops = 0;
            while( loops < 2 && invalidNameCol() ) {
                name_col++;
                if (name_col >= reader.getHeaderCount()) {
                    name_col = 0;
                    loops++;
                }
            }
            if (invalidNameCol()) {
                name_col = -1; // deal with this later
            }
        } catch (Exception e) {
            System.err.println("Failed to get features from url '" + feature_url + "': " + e);
            e.printStackTrace(System.err);
        }
    }
    private Feature getFeature() {
        if (reader == null) {
            setupFeatures();
        }
        try {
            double x = Double.valueOf(reader.get(x_col));
            double y = Double.valueOf(reader.get(y_col));
            String name = (name_col < 0) ? "Point[" + x + ":" + y + "]" : reader.get(name_col);
            HashMap<String, Object> properties = new HashMap<String, Object>();
            properties.put("name", name);
            for( int i = 0; i < reader.getColumnCount(); i++ ) {
                if (i != x_col && i != y_col && i != name_col) {
                    properties.put(reader.getHeader(i), reader.get(i));
                }
            }
            return new SimplePointFeature(x, y, properties);
        } catch (Exception e) {
            System.err.println("Failed to get features from url '" + feature_url + "': " + e);
            e.printStackTrace(System.err);
            return null;
        }
    }
    private boolean invalidNameCol() {
        return (name_col == x_col || name_col == y_col || name_col >= reader.getHeaderCount() || name_col < 0);
    }
    public Iterator<Feature> iterator() {
        setupFeatures();
        return new Iterator<Feature>(){
            private Feature next = null;
            public boolean hasNext() {
                return (next = getFeature()) != null;
            }
            public Feature next() {
                if (next == null) {
                    next = getFeature();
                }
                return next;
            }
            public void remove() {
            }
        };
    }

}