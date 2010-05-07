/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class CompleteGpsDataType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class CompleteGpsDataType implements java.io.Serializable {


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
     * Field _gpsSentence.
     */
    private java.lang.String _gpsSentence;


      //----------------/
     //- Constructors -/
    //----------------/

    public CompleteGpsDataType() {
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
     * Returns the value of field 'gpsSentence'.
     * 
     * @return the value of field 'GpsSentence'.
     */
    public java.lang.String getGpsSentence(
    ) {
        return this._gpsSentence;
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
     * Sets the value of field 'gpsSentence'.
     * 
     * @param gpsSentence the value of field 'gpsSentence'.
     */
    public void setGpsSentence(
            final java.lang.String gpsSentence) {
        this._gpsSentence = gpsSentence;
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
