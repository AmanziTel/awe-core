/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class ServingDataType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class ServingDataType implements java.io.Serializable {


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
     * Field _rssi.
     */
    private int _rssi;

    /**
     * keeps track of state for field: _rssi
     */
    private boolean _has_rssi;

    /**
     * Field _locationArea.
     */
    private int _locationArea;

    /**
     * keeps track of state for field: _locationArea
     */
    private boolean _has_locationArea;

    /**
     * Field _frequency.
     */
    private java.lang.String _frequency;

    /**
     * Field _c1.
     */
    private int _c1;

    /**
     * keeps track of state for field: _c1
     */
    private boolean _has_c1;


      //----------------/
     //- Constructors -/
    //----------------/

    public ServingDataType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteC1(
    ) {
        this._has_c1= false;
    }

    /**
     */
    public void deleteLocationArea(
    ) {
        this._has_locationArea= false;
    }

    /**
     */
    public void deleteRssi(
    ) {
        this._has_rssi= false;
    }

    /**
     * Returns the value of field 'c1'.
     * 
     * @return the value of field 'C1'.
     */
    public int getC1(
    ) {
        return this._c1;
    }

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
     * Returns the value of field 'frequency'.
     * 
     * @return the value of field 'Frequency'.
     */
    public java.lang.String getFrequency(
    ) {
        return this._frequency;
    }

    /**
     * Returns the value of field 'locationArea'.
     * 
     * @return the value of field 'LocationArea'.
     */
    public int getLocationArea(
    ) {
        return this._locationArea;
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
     * Returns the value of field 'rssi'.
     * 
     * @return the value of field 'Rssi'.
     */
    public int getRssi(
    ) {
        return this._rssi;
    }

    /**
     * Method hasC1.
     * 
     * @return true if at least one C1 has been added
     */
    public boolean hasC1(
    ) {
        return this._has_c1;
    }

    /**
     * Method hasLocationArea.
     * 
     * @return true if at least one LocationArea has been added
     */
    public boolean hasLocationArea(
    ) {
        return this._has_locationArea;
    }

    /**
     * Method hasRssi.
     * 
     * @return true if at least one Rssi has been added
     */
    public boolean hasRssi(
    ) {
        return this._has_rssi;
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
     * Sets the value of field 'c1'.
     * 
     * @param c1 the value of field 'c1'.
     */
    public void setC1(
            final int c1) {
        this._c1 = c1;
        this._has_c1 = true;
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
     * Sets the value of field 'frequency'.
     * 
     * @param frequency the value of field 'frequency'.
     */
    public void setFrequency(
            final java.lang.String frequency) {
        this._frequency = frequency;
    }

    /**
     * Sets the value of field 'locationArea'.
     * 
     * @param locationArea the value of field 'locationArea'.
     */
    public void setLocationArea(
            final int locationArea) {
        this._locationArea = locationArea;
        this._has_locationArea = true;
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
     * Sets the value of field 'rssi'.
     * 
     * @param rssi the value of field 'rssi'.
     */
    public void setRssi(
            final int rssi) {
        this._rssi = rssi;
        this._has_rssi = true;
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
