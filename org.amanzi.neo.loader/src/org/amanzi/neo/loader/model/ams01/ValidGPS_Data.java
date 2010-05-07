/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class ValidGPS_Data.
 * 
 * @version $Revision$ $Date$
 */
public abstract class ValidGPS_Data implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _probeID.
     */
    private java.lang.String _probeID;

    /**
     * Field _deliveryTime.
     */
    private java.lang.String _deliveryTime;

    /**
     * Field _gpsTime.
     */
    private java.lang.String _gpsTime;

    /**
     * Field _latitude.
     */
    private java.lang.String _latitude;

    /**
     * Field _directionLat.
     */
    private org.amanzi.neo.loader.model.ams01.types.DirectionLatType _directionLat;

    /**
     * Field _longitude.
     */
    private java.lang.String _longitude;

    /**
     * Field _directionLon.
     */
    private org.amanzi.neo.loader.model.ams01.types.DirectionLonType _directionLon;

    /**
     * Field _speed.
     */
    private java.lang.String _speed;


      //----------------/
     //- Constructors -/
    //----------------/

    public ValidGPS_Data() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'deliveryTime'.
     * 
     * @return the value of field 'DeliveryTime'.
     */
    public java.lang.String getDeliveryTime(
    ) {
        return this._deliveryTime;
    }

    /**
     * Returns the value of field 'directionLat'.
     * 
     * @return the value of field 'DirectionLat'.
     */
    public org.amanzi.neo.loader.model.ams01.types.DirectionLatType getDirectionLat(
    ) {
        return this._directionLat;
    }

    /**
     * Returns the value of field 'directionLon'.
     * 
     * @return the value of field 'DirectionLon'.
     */
    public org.amanzi.neo.loader.model.ams01.types.DirectionLonType getDirectionLon(
    ) {
        return this._directionLon;
    }

    /**
     * Returns the value of field 'gpsTime'.
     * 
     * @return the value of field 'GpsTime'.
     */
    public java.lang.String getGpsTime(
    ) {
        return this._gpsTime;
    }

    /**
     * Returns the value of field 'latitude'.
     * 
     * @return the value of field 'Latitude'.
     */
    public java.lang.String getLatitude(
    ) {
        return this._latitude;
    }

    /**
     * Returns the value of field 'longitude'.
     * 
     * @return the value of field 'Longitude'.
     */
    public java.lang.String getLongitude(
    ) {
        return this._longitude;
    }

    /**
     * Returns the value of field 'probeID'.
     * 
     * @return the value of field 'ProbeID'.
     */
    public java.lang.String getProbeID(
    ) {
        return this._probeID;
    }

    /**
     * Returns the value of field 'speed'.
     * 
     * @return the value of field 'Speed'.
     */
    public java.lang.String getSpeed(
    ) {
        return this._speed;
    }

    /**
     * Method isValid.
     * 
     * @return true if this object is valid according to the schema
     */
    public boolean isValid(
    ) {
        try {
            validate();
        } catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    }

    /**
     * Sets the value of field 'deliveryTime'.
     * 
     * @param deliveryTime the value of field 'deliveryTime'.
     */
    public void setDeliveryTime(
            final java.lang.String deliveryTime) {
        this._deliveryTime = deliveryTime;
    }

    /**
     * Sets the value of field 'directionLat'.
     * 
     * @param directionLat the value of field 'directionLat'.
     */
    public void setDirectionLat(
            final org.amanzi.neo.loader.model.ams01.types.DirectionLatType directionLat) {
        this._directionLat = directionLat;
    }

    /**
     * Sets the value of field 'directionLon'.
     * 
     * @param directionLon the value of field 'directionLon'.
     */
    public void setDirectionLon(
            final org.amanzi.neo.loader.model.ams01.types.DirectionLonType directionLon) {
        this._directionLon = directionLon;
    }

    /**
     * Sets the value of field 'gpsTime'.
     * 
     * @param gpsTime the value of field 'gpsTime'.
     */
    public void setGpsTime(
            final java.lang.String gpsTime) {
        this._gpsTime = gpsTime;
    }

    /**
     * Sets the value of field 'latitude'.
     * 
     * @param latitude the value of field 'latitude'.
     */
    public void setLatitude(
            final java.lang.String latitude) {
        this._latitude = latitude;
    }

    /**
     * Sets the value of field 'longitude'.
     * 
     * @param longitude the value of field 'longitude'.
     */
    public void setLongitude(
            final java.lang.String longitude) {
        this._longitude = longitude;
    }

    /**
     * Sets the value of field 'probeID'.
     * 
     * @param probeID the value of field 'probeID'.
     */
    public void setProbeID(
            final java.lang.String probeID) {
        this._probeID = probeID;
    }

    /**
     * Sets the value of field 'speed'.
     * 
     * @param speed the value of field 'speed'.
     */
    public void setSpeed(
            final java.lang.String speed) {
        this._speed = speed;
    }

    /**
     * 
     * 
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void validate(
    )
    throws org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    }

}
