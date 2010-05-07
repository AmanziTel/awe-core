/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class HandoverType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class HandoverType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _probeID.
     */
    private java.lang.String _probeID;

    /**
     * Field _ho_Req.
     */
    private java.lang.String _ho_Req;

    /**
     * Field _ho_Accept.
     */
    private java.lang.String _ho_Accept;

    /**
     * Field _locationAreaBefore.
     */
    private int _locationAreaBefore;

    /**
     * keeps track of state for field: _locationAreaBefore
     */
    private boolean _has_locationAreaBefore;

    /**
     * Field _locationAreaAfter.
     */
    private int _locationAreaAfter;

    /**
     * keeps track of state for field: _locationAreaAfter
     */
    private boolean _has_locationAreaAfter;

    /**
     * Field _errorCode.
     */
    private int _errorCode;

    /**
     * keeps track of state for field: _errorCode
     */
    private boolean _has_errorCode;

    /**
     * Field _handoverIsInclusive.
     */
    private org.amanzi.neo.loader.model.ams01.HandoverIsInclusive _handoverIsInclusive;


      //----------------/
     //- Constructors -/
    //----------------/

    public HandoverType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteErrorCode(
    ) {
        this._has_errorCode= false;
    }

    /**
     */
    public void deleteLocationAreaAfter(
    ) {
        this._has_locationAreaAfter= false;
    }

    /**
     */
    public void deleteLocationAreaBefore(
    ) {
        this._has_locationAreaBefore= false;
    }

    /**
     * Returns the value of field 'errorCode'.
     * 
     * @return the value of field 'ErrorCode'.
     */
    public int getErrorCode(
    ) {
        return this._errorCode;
    }

    /**
     * Returns the value of field 'handoverIsInclusive'.
     * 
     * @return the value of field 'HandoverIsInclusive'.
     */
    public org.amanzi.neo.loader.model.ams01.HandoverIsInclusive getHandoverIsInclusive(
    ) {
        return this._handoverIsInclusive;
    }

    /**
     * Returns the value of field 'ho_Accept'.
     * 
     * @return the value of field 'Ho_Accept'.
     */
    public java.lang.String getHo_Accept(
    ) {
        return this._ho_Accept;
    }

    /**
     * Returns the value of field 'ho_Req'.
     * 
     * @return the value of field 'Ho_Req'.
     */
    public java.lang.String getHo_Req(
    ) {
        return this._ho_Req;
    }

    /**
     * Returns the value of field 'locationAreaAfter'.
     * 
     * @return the value of field 'LocationAreaAfter'.
     */
    public int getLocationAreaAfter(
    ) {
        return this._locationAreaAfter;
    }

    /**
     * Returns the value of field 'locationAreaBefore'.
     * 
     * @return the value of field 'LocationAreaBefore'.
     */
    public int getLocationAreaBefore(
    ) {
        return this._locationAreaBefore;
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
     * Method hasErrorCode.
     * 
     * @return true if at least one ErrorCode has been added
     */
    public boolean hasErrorCode(
    ) {
        return this._has_errorCode;
    }

    /**
     * Method hasLocationAreaAfter.
     * 
     * @return true if at least one LocationAreaAfter has been added
     */
    public boolean hasLocationAreaAfter(
    ) {
        return this._has_locationAreaAfter;
    }

    /**
     * Method hasLocationAreaBefore.
     * 
     * @return true if at least one LocationAreaBefore has been adde
     */
    public boolean hasLocationAreaBefore(
    ) {
        return this._has_locationAreaBefore;
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
     * Sets the value of field 'errorCode'.
     * 
     * @param errorCode the value of field 'errorCode'.
     */
    public void setErrorCode(
            final int errorCode) {
        this._errorCode = errorCode;
        this._has_errorCode = true;
    }

    /**
     * Sets the value of field 'handoverIsInclusive'.
     * 
     * @param handoverIsInclusive the value of field
     * 'handoverIsInclusive'.
     */
    public void setHandoverIsInclusive(
            final org.amanzi.neo.loader.model.ams01.HandoverIsInclusive handoverIsInclusive) {
        this._handoverIsInclusive = handoverIsInclusive;
    }

    /**
     * Sets the value of field 'ho_Accept'.
     * 
     * @param ho_Accept the value of field 'ho_Accept'.
     */
    public void setHo_Accept(
            final java.lang.String ho_Accept) {
        this._ho_Accept = ho_Accept;
    }

    /**
     * Sets the value of field 'ho_Req'.
     * 
     * @param ho_Req the value of field 'ho_Req'.
     */
    public void setHo_Req(
            final java.lang.String ho_Req) {
        this._ho_Req = ho_Req;
    }

    /**
     * Sets the value of field 'locationAreaAfter'.
     * 
     * @param locationAreaAfter the value of field
     * 'locationAreaAfter'.
     */
    public void setLocationAreaAfter(
            final int locationAreaAfter) {
        this._locationAreaAfter = locationAreaAfter;
        this._has_locationAreaAfter = true;
    }

    /**
     * Sets the value of field 'locationAreaBefore'.
     * 
     * @param locationAreaBefore the value of field
     * 'locationAreaBefore'.
     */
    public void setLocationAreaBefore(
            final int locationAreaBefore) {
        this._locationAreaBefore = locationAreaBefore;
        this._has_locationAreaBefore = true;
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
