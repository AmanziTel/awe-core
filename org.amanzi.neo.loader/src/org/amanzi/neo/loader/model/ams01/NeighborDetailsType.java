/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class NeighborDetailsType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class NeighborDetailsType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _frequency.
     */
    private java.lang.String _frequency;

    /**
     * Field _rssi.
     */
    private int _rssi;

    /**
     * keeps track of state for field: _rssi
     */
    private boolean _has_rssi;

    /**
     * Field _c2.
     */
    private int _c2;

    /**
     * keeps track of state for field: _c2
     */
    private boolean _has_c2;


      //----------------/
     //- Constructors -/
    //----------------/

    public NeighborDetailsType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteC2(
    ) {
        this._has_c2= false;
    }

    /**
     */
    public void deleteRssi(
    ) {
        this._has_rssi= false;
    }

    /**
     * Returns the value of field 'c2'.
     * 
     * @return the value of field 'C2'.
     */
    public int getC2(
    ) {
        return this._c2;
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
     * Returns the value of field 'rssi'.
     * 
     * @return the value of field 'Rssi'.
     */
    public int getRssi(
    ) {
        return this._rssi;
    }

    /**
     * Method hasC2.
     * 
     * @return true if at least one C2 has been added
     */
    public boolean hasC2(
    ) {
        return this._has_c2;
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
     * Sets the value of field 'c2'.
     * 
     * @param c2 the value of field 'c2'.
     */
    public void setC2(
            final int c2) {
        this._c2 = c2;
        this._has_c2 = true;
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
