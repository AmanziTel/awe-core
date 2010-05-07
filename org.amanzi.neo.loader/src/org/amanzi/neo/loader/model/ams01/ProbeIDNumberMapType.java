/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class ProbeIDNumberMapType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class ProbeIDNumberMapType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _probeID.
     */
    private java.lang.String _probeID;

    /**
     * Field _phoneNumber.
     */
    private java.lang.String _phoneNumber;

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


      //----------------/
     //- Constructors -/
    //----------------/

    public ProbeIDNumberMapType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteLocationArea(
    ) {
        this._has_locationArea= false;
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
     * Returns the value of field 'phoneNumber'.
     * 
     * @return the value of field 'PhoneNumber'.
     */
    public java.lang.String getPhoneNumber(
    ) {
        return this._phoneNumber;
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
     * Method hasLocationArea.
     * 
     * @return true if at least one LocationArea has been added
     */
    public boolean hasLocationArea(
    ) {
        return this._has_locationArea;
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
     * Sets the value of field 'phoneNumber'.
     * 
     * @param phoneNumber the value of field 'phoneNumber'.
     */
    public void setPhoneNumber(
            final java.lang.String phoneNumber) {
        this._phoneNumber = phoneNumber;
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
