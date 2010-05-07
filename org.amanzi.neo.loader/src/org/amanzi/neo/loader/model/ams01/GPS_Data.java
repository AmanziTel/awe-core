/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class GPS_Data.
 * 
 * @version $Revision$ $Date$
 */
public abstract class GPS_Data implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Internal choice value storage
     */
    private java.lang.Object _choiceValue;

    /**
     * Field _validGpsDataList.
     */
    private org.amanzi.neo.loader.model.ams01.ValidGpsDataList _validGpsDataList;

    /**
     * Field _completeGpsDataList.
     */
    private org.amanzi.neo.loader.model.ams01.CompleteGpsDataList _completeGpsDataList;


      //----------------/
     //- Constructors -/
    //----------------/

    public GPS_Data() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'choiceValue'. The field
     * 'choiceValue' has the following description: Internal choice
     * value storage
     * 
     * @return the value of field 'ChoiceValue'.
     */
    public java.lang.Object getChoiceValue(
    ) {
        return this._choiceValue;
    }

    /**
     * Returns the value of field 'completeGpsDataList'.
     * 
     * @return the value of field 'CompleteGpsDataList'.
     */
    public org.amanzi.neo.loader.model.ams01.CompleteGpsDataList getCompleteGpsDataList(
    ) {
        return this._completeGpsDataList;
    }

    /**
     * Returns the value of field 'validGpsDataList'.
     * 
     * @return the value of field 'ValidGpsDataList'.
     */
    public org.amanzi.neo.loader.model.ams01.ValidGpsDataList getValidGpsDataList(
    ) {
        return this._validGpsDataList;
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
     * Sets the value of field 'completeGpsDataList'.
     * 
     * @param completeGpsDataList the value of field
     * 'completeGpsDataList'.
     */
    public void setCompleteGpsDataList(
            final org.amanzi.neo.loader.model.ams01.CompleteGpsDataList completeGpsDataList) {
        this._completeGpsDataList = completeGpsDataList;
        this._choiceValue = completeGpsDataList;
    }

    /**
     * Sets the value of field 'validGpsDataList'.
     * 
     * @param validGpsDataList the value of field 'validGpsDataList'
     */
    public void setValidGpsDataList(
            final org.amanzi.neo.loader.model.ams01.ValidGpsDataList validGpsDataList) {
        this._validGpsDataList = validGpsDataList;
        this._choiceValue = validGpsDataList;
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
