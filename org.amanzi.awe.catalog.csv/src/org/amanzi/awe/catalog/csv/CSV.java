package org.amanzi.awe.catalog.csv;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import com.csvreader.CsvReader;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * This class is a utility class for reading from CSV files, using the CSVReader
 * library. This class needs to be resolved by the CSVGeoResource class that
 * uDIG uses when placing the data into the map. Currently this class assumes
 * the data contains "x" and "y" headings, and that the projection matches the
 * one returned by the CSVGeoResourceInfo metadata.
 * @author craig
 */
public class CSV {
	private URL url;
    public CSV(URL url){
        this.url = url;
    }
	public CsvReader reader() throws IOException{
		Reader reader = new InputStreamReader(url.openStream());
		return new CsvReader(reader);
	}
	/**
	 * This is the main method called by the application to produce
	 * the data for display on the map. Should be called repeatedly
	 * until it returns null.
	 * @param reader
	 * @return instance of com.vividsolutions.jts.geom.Point
	 * @throws IOException
	 */
	public static Point getPoint(CsvReader reader) throws IOException{
		if(reader == null) return null;
		GeometryFactory geometryFactory = new GeometryFactory();
		double x = Double.valueOf(reader.get("x"));
		double y = Double.valueOf(reader.get("y"));
		Coordinate coordinate = new Coordinate(x,y);
		return geometryFactory.createPoint(coordinate);
	}
}
