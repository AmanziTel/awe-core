/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class SendReportType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class SendReportType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _reportTime.
     */
    private java.lang.String _reportTime;

    /**
     * Field _status.
     */
    private org.amanzi.neo.loader.model.ams01.types.StatusType _status;


      //----------------/
     //- Constructors -/
    //----------------/

    public SendReportType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'reportTime'.
     * 
     * @return the value of field 'ReportTime'.
     */
    public java.lang.String getReportTime(
    ) {
        return this._reportTime;
    }

    /**
     * Returns the value of field 'status'.
     * 
     * @return the value of field 'Status'.
     */
    public org.amanzi.neo.loader.model.ams01.types.StatusType getStatus(
    ) {
        return this._status;
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
     * Sets the value of field 'reportTime'.
     * 
     * @param reportTime the value of field 'reportTime'.
     */
    public void setReportTime(
            final java.lang.String reportTime) {
        this._reportTime = reportTime;
    }

    /**
     * Sets the value of field 'status'.
     * 
     * @param status the value of field 'status'.
     */
    public void setStatus(
            final org.amanzi.neo.loader.model.ams01.types.StatusType status) {
        this._status = status;
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
