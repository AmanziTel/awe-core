/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class PD_Result.
 * 
 * @version $Revision$ $Date$
 */
public abstract class PD_Result implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _direction.
     */
    private org.amanzi.neo.loader.model.ams01.types.DirectionType _direction;

    /**
     * Field _size.
     */
    private int _size;

    /**
     * keeps track of state for field: _size
     */
    private boolean _has_size;

    /**
     * Field _transmitStart.
     */
    private java.lang.String _transmitStart;

    /**
     * Field _transmitEnd.
     */
    private java.lang.String _transmitEnd;


      //----------------/
     //- Constructors -/
    //----------------/

    public PD_Result() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteSize(
    ) {
        this._has_size= false;
    }

    /**
     * Returns the value of field 'direction'.
     * 
     * @return the value of field 'Direction'.
     */
    public org.amanzi.neo.loader.model.ams01.types.DirectionType getDirection(
    ) {
        return this._direction;
    }

    /**
     * Returns the value of field 'size'.
     * 
     * @return the value of field 'Size'.
     */
    public int getSize(
    ) {
        return this._size;
    }

    /**
     * Returns the value of field 'transmitEnd'.
     * 
     * @return the value of field 'TransmitEnd'.
     */
    public java.lang.String getTransmitEnd(
    ) {
        return this._transmitEnd;
    }

    /**
     * Returns the value of field 'transmitStart'.
     * 
     * @return the value of field 'TransmitStart'.
     */
    public java.lang.String getTransmitStart(
    ) {
        return this._transmitStart;
    }

    /**
     * Method hasSize.
     * 
     * @return true if at least one Size has been added
     */
    public boolean hasSize(
    ) {
        return this._has_size;
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
     * Sets the value of field 'direction'.
     * 
     * @param direction the value of field 'direction'.
     */
    public void setDirection(
            final org.amanzi.neo.loader.model.ams01.types.DirectionType direction) {
        this._direction = direction;
    }

    /**
     * Sets the value of field 'size'.
     * 
     * @param size the value of field 'size'.
     */
    public void setSize(
            final int size) {
        this._size = size;
        this._has_size = true;
    }

    /**
     * Sets the value of field 'transmitEnd'.
     * 
     * @param transmitEnd the value of field 'transmitEnd'.
     */
    public void setTransmitEnd(
            final java.lang.String transmitEnd) {
        this._transmitEnd = transmitEnd;
    }

    /**
     * Sets the value of field 'transmitStart'.
     * 
     * @param transmitStart the value of field 'transmitStart'.
     */
    public void setTransmitStart(
            final java.lang.String transmitStart) {
        this._transmitStart = transmitStart;
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
