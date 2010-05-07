/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class PESQ_Result.
 * 
 * @version $Revision$ $Date$
 */
public abstract class PESQ_Result implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _sendSampleStart.
     */
    private java.lang.String _sendSampleStart;

    /**
     * Field _pesq.
     */
    private double _pesq;

    /**
     * keeps track of state for field: _pesq
     */
    private boolean _has_pesq;

    /**
     * Field _delay.
     */
    private int _delay;

    /**
     * keeps track of state for field: _delay
     */
    private boolean _has_delay;


      //----------------/
     //- Constructors -/
    //----------------/

    public PESQ_Result() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteDelay(
    ) {
        this._has_delay= false;
    }

    /**
     */
    public void deletePesq(
    ) {
        this._has_pesq= false;
    }

    /**
     * Returns the value of field 'delay'.
     * 
     * @return the value of field 'Delay'.
     */
    public int getDelay(
    ) {
        return this._delay;
    }

    /**
     * Returns the value of field 'pesq'.
     * 
     * @return the value of field 'Pesq'.
     */
    public double getPesq(
    ) {
        return this._pesq;
    }

    /**
     * Returns the value of field 'sendSampleStart'.
     * 
     * @return the value of field 'SendSampleStart'.
     */
    public java.lang.String getSendSampleStart(
    ) {
        return this._sendSampleStart;
    }

    /**
     * Method hasDelay.
     * 
     * @return true if at least one Delay has been added
     */
    public boolean hasDelay(
    ) {
        return this._has_delay;
    }

    /**
     * Method hasPesq.
     * 
     * @return true if at least one Pesq has been added
     */
    public boolean hasPesq(
    ) {
        return this._has_pesq;
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
     * Sets the value of field 'delay'.
     * 
     * @param delay the value of field 'delay'.
     */
    public void setDelay(
            final int delay) {
        this._delay = delay;
        this._has_delay = true;
    }

    /**
     * Sets the value of field 'pesq'.
     * 
     * @param pesq the value of field 'pesq'.
     */
    public void setPesq(
            final double pesq) {
        this._pesq = pesq;
        this._has_pesq = true;
    }

    /**
     * Sets the value of field 'sendSampleStart'.
     * 
     * @param sendSampleStart the value of field 'sendSampleStart'.
     */
    public void setSendSampleStart(
            final java.lang.String sendSampleStart) {
        this._sendSampleStart = sendSampleStart;
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
