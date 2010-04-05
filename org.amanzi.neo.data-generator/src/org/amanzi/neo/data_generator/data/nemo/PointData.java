package org.amanzi.neo.data_generator.data.nemo;

/**
 * Store gps event from Nemo protocol 
 * @author zhuhrou_a
 */
public class PointData {
    
    /** measure longitude */
    private Float longitude;
    
    /** measure latitude */
    private Float latitude;
    
    /** measure time */
    private String time;
    
    /** 
     * 
     * @param aTime
     * @param aLongitude
     * @param aLatitude
     */
    public PointData(final String aTime, final Float aLongitude, final Float aLatitude)
    {
        this.time = aTime;
        this.longitude = aLongitude;
        this.latitude = aLatitude;
    }
    
    /**
     * Get measure longitude
     * @return measure longitude
     */
    public Float getLongitude() {
        return longitude;
    }
    
    /**
     * Get measure latitude
     * @return measure latitude
     */
    public Float getLatitude() {
        return latitude;
    }
    
    /**
     * Get measure time
     * @return measure time
     */
    public String getTime() {
        return time;
    }
    
    /** 
     * String representation in nemo format 
     * @return String representation in nemo format
     * */
    @Override
    public String toString()
    {
        StringBuilder line = new StringBuilder();
        line.append( "GPS," );
        line.append(time);
        line.append(",,");
        line.append(longitude);
        line.append(',');
        line.append(latitude);
        line.append(",439,0,1,8,0");
        return line.toString();
    }
    
}