/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class CellReselType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class CellReselType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _probeID.
     */
    private java.lang.String _probeID;

    /**
     * Field _cellReselReq.
     */
    private java.lang.String _cellReselReq;

    /**
     * Field _cellReselAccept.
     */
    private java.lang.String _cellReselAccept;

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
     * Field _cellReselIsInclusive.
     */
    private org.amanzi.neo.loader.model.ams01.CellReselIsInclusive _cellReselIsInclusive;


      //----------------/
     //- Constructors -/
    //----------------/

    public CellReselType() {
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
     * Returns the value of field 'cellReselAccept'.
     * 
     * @return the value of field 'CellReselAccept'.
     */
    public java.lang.String getCellReselAccept(
    ) {
        return this._cellReselAccept;
    }

    /**
     * Returns the value of field 'cellReselIsInclusive'.
     * 
     * @return the value of field 'CellReselIsInclusive'.
     */
    public org.amanzi.neo.loader.model.ams01.CellReselIsInclusive getCellReselIsInclusive(
    ) {
        return this._cellReselIsInclusive;
    }

    /**
     * Returns the value of field 'cellReselReq'.
     * 
     * @return the value of field 'CellReselReq'.
     */
    public java.lang.String getCellReselReq(
    ) {
        return this._cellReselReq;
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
     * Sets the value of field 'cellReselAccept'.
     * 
     * @param cellReselAccept the value of field 'cellReselAccept'.
     */
    public void setCellReselAccept(
            final java.lang.String cellReselAccept) {
        this._cellReselAccept = cellReselAccept;
    }

    /**
     * Sets the value of field 'cellReselIsInclusive'.
     * 
     * @param cellReselIsInclusive the value of field
     * 'cellReselIsInclusive'.
     */
    public void setCellReselIsInclusive(
            final org.amanzi.neo.loader.model.ams01.CellReselIsInclusive cellReselIsInclusive) {
        this._cellReselIsInclusive = cellReselIsInclusive;
    }

    /**
     * Sets the value of field 'cellReselReq'.
     * 
     * @param cellReselReq the value of field 'cellReselReq'.
     */
    public void setCellReselReq(
            final java.lang.String cellReselReq) {
        this._cellReselReq = cellReselReq;
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
