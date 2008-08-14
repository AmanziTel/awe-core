package org.amanzi.awe.catalog.csv;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResourceInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.csvreader.CsvReader;
import com.vividsolutions.jts.geom.Point;

/**
 * This class produces information about the CSV data stream, including
 * the bounds of the data. Currently this is done by reading the stream
 * and expanding an envelope to include all points. A better approach would
 * be to provide meta-data with the stream.
 */
public class CSVGeoResourceInfo extends IGeoResourceInfo {
    CSVGeoResource handle;
    public CSVGeoResourceInfo( CSVGeoResource resource, IProgressMonitor monitor )
            throws IOException {
        this.handle = resource;
        this.title = handle.getIdentifier().toString();
        CSV csv = handle.getCSV(monitor);
        CsvReader reader = csv.reader();
        try {
            reader.readHeaders();
            this.description = "Information:";
            for( String header : reader.getHeaders() ) {
                this.description += " " + header;
            }
            this.bounds = new ReferencedEnvelope(DefaultGeographicCRS.WGS84);
            this.bounds = new ReferencedEnvelope(CRS.decode("EPSG:3021"));  // hard coded to RT90 for current CSV data - TODO: fix this to determine CRS from data
            while( reader.readRecord() ) {
                Point point = CSV.getPoint(reader);
                if (point == null)
                    continue;
                this.bounds.expandToInclude(point.getCoordinate());
            }
        } catch (NoSuchAuthorityCodeException e) {
            System.err.println("Failed to interpret CRS: "+e.getMessage());
            e.printStackTrace(System.err);
        } finally {
            reader.close();
        }
    }
}
