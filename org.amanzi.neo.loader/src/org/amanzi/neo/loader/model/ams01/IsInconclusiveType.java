/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class IsInconclusiveType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class IsInconclusiveType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _errCode.
     */
    private int _errCode;

    /**
     * keeps track of state for field: _errCode
     */
    private boolean _has_errCode;

    /**
     * Field _reason.
     */
    private java.lang.String _reason;


      //----------------/
     //- Constructors -/
    //----------------/

    public IsInconclusiveType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteErrCode(
    ) {
        this._has_errCode= false;
    }

    /**
     * Returns the value of field 'errCode'.
     * 
     * @return the value of field 'ErrCode'.
     */
    public int getErrCode(
    ) {
        return this._errCode;
    }

    /**
     * Returns the value of field 'reason'.
     * 
     * @return the value of field 'Reason'.
     */
    public java.lang.String getReason(
    ) {
        return this._reason;
    }

    /**
     * Method hasErrCode.
     * 
     * @return true if at least one ErrCode has been added
     */
    public boolean hasErrCode(
    ) {
        return this._has_errCode;
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
     * Sets the value of field 'errCode'.
     * 
     * @param errCode the value of field 'errCode'.
     */
    public void setErrCode(
            final int errCode) {
        this._errCode = errCode;
        this._has_errCode = true;
    }

    /**
     * Sets the value of field 'reason'.
     * 
     * @param reason the value of field 'reason'.
     */
    public void setReason(
            final java.lang.String reason) {
        this._reason = reason;
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
