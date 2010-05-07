/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class AttachmentType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class AttachmentType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _groupType.
     */
    private java.lang.String _groupType;

    /**
     * Field _gssi.
     */
    private java.lang.String _gssi;


      //----------------/
     //- Constructors -/
    //----------------/

    public AttachmentType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'groupType'.
     * 
     * @return the value of field 'GroupType'.
     */
    public java.lang.String getGroupType(
    ) {
        return this._groupType;
    }

    /**
     * Returns the value of field 'gssi'.
     * 
     * @return the value of field 'Gssi'.
     */
    public java.lang.String getGssi(
    ) {
        return this._gssi;
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
     * Sets the value of field 'groupType'.
     * 
     * @param groupType the value of field 'groupType'.
     */
    public void setGroupType(
            final java.lang.String groupType) {
        this._groupType = groupType;
    }

    /**
     * Sets the value of field 'gssi'.
     * 
     * @param gssi the value of field 'gssi'.
     */
    public void setGssi(
            final java.lang.String gssi) {
        this._gssi = gssi;
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
