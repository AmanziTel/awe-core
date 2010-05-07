/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class NtpqType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class NtpqType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _probeID.
     */
    private java.lang.String _probeID;

    /**
     * Field _ntpqTime.
     */
    private java.lang.String _ntpqTime;

    /**
     * Field _offset.
     */
    private double _offset;

    /**
     * keeps track of state for field: _offset
     */
    private boolean _has_offset;

    /**
     * Field _jitter.
     */
    private double _jitter;

    /**
     * keeps track of state for field: _jitter
     */
    private boolean _has_jitter;


      //----------------/
     //- Constructors -/
    //----------------/

    public NtpqType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteJitter(
    ) {
        this._has_jitter= false;
    }

    /**
     */
    public void deleteOffset(
    ) {
        this._has_offset= false;
    }

    /**
     * Returns the value of field 'jitter'.
     * 
     * @return the value of field 'Jitter'.
     */
    public double getJitter(
    ) {
        return this._jitter;
    }

    /**
     * Returns the value of field 'ntpqTime'.
     * 
     * @return the value of field 'NtpqTime'.
     */
    public java.lang.String getNtpqTime(
    ) {
        return this._ntpqTime;
    }

    /**
     * Returns the value of field 'offset'.
     * 
     * @return the value of field 'Offset'.
     */
    public double getOffset(
    ) {
        return this._offset;
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
     * Method hasJitter.
     * 
     * @return true if at least one Jitter has been added
     */
    public boolean hasJitter(
    ) {
        return this._has_jitter;
    }

    /**
     * Method hasOffset.
     * 
     * @return true if at least one Offset has been added
     */
    public boolean hasOffset(
    ) {
        return this._has_offset;
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
     * Sets the value of field 'jitter'.
     * 
     * @param jitter the value of field 'jitter'.
     */
    public void setJitter(
            final double jitter) {
        this._jitter = jitter;
        this._has_jitter = true;
    }

    /**
     * Sets the value of field 'ntpqTime'.
     * 
     * @param ntpqTime the value of field 'ntpqTime'.
     */
    public void setNtpqTime(
            final java.lang.String ntpqTime) {
        this._ntpqTime = ntpqTime;
    }

    /**
     * Sets the value of field 'offset'.
     * 
     * @param offset the value of field 'offset'.
     */
    public void setOffset(
            final double offset) {
        this._offset = offset;
        this._has_offset = true;
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
