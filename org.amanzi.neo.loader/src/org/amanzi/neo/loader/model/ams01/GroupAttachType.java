/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class GroupAttachType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class GroupAttachType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _probeID.
     */
    private java.lang.String _probeID;

    /**
     * Field _groupAttachTime.
     */
    private java.lang.String _groupAttachTime;

    /**
     * Field _errorCode.
     */
    private int _errorCode;

    /**
     * keeps track of state for field: _errorCode
     */
    private boolean _has_errorCode;

    /**
     * Field _attachmentList.
     */
    private java.util.Vector _attachmentList;


      //----------------/
     //- Constructors -/
    //----------------/

    public GroupAttachType() {
        super();
        this._attachmentList = new java.util.Vector();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vAttachment
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAttachment(
            final org.amanzi.neo.loader.model.ams01.Attachment vAttachment)
    throws java.lang.IndexOutOfBoundsException {
        // check for the maximum size
        if (this._attachmentList.size() >= 26) {
            throw new IndexOutOfBoundsException("addAttachment has a maximum of 26");
        }
        
        this._attachmentList.addElement(vAttachment);
    }

    /**
     * 
     * 
     * @param index
     * @param vAttachment
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAttachment(
            final int index,
            final org.amanzi.neo.loader.model.ams01.Attachment vAttachment)
    throws java.lang.IndexOutOfBoundsException {
        // check for the maximum size
        if (this._attachmentList.size() >= 26) {
            throw new IndexOutOfBoundsException("addAttachment has a maximum of 26");
        }
        
        this._attachmentList.add(index, vAttachment);
    }

    /**
     */
    public void deleteErrorCode(
    ) {
        this._has_errorCode= false;
    }

    /**
     * Method enumerateAttachment.
     * 
     * @return an Enumeration over all
     * org.amanzi.neo.loader.model.ams01.Attachment elements
     */
    public java.util.Enumeration enumerateAttachment(
    ) {
        return this._attachmentList.elements();
    }

    /**
     * Method getAttachment.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * org.amanzi.neo.loader.model.ams01.Attachment at the given
     * index
     */
    public org.amanzi.neo.loader.model.ams01.Attachment getAttachment(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._attachmentList.size()) {
            throw new IndexOutOfBoundsException("getAttachment: Index value '" + index + "' not in range [0.." + (this._attachmentList.size() - 1) + "]");
        }
        
        return (org.amanzi.neo.loader.model.ams01.Attachment) _attachmentList.get(index);
    }

    /**
     * Method getAttachment.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public org.amanzi.neo.loader.model.ams01.Attachment[] getAttachment(
    ) {
        org.amanzi.neo.loader.model.ams01.Attachment[] array = new org.amanzi.neo.loader.model.ams01.Attachment[0];
        return (org.amanzi.neo.loader.model.ams01.Attachment[]) this._attachmentList.toArray(array);
    }

    /**
     * Method getAttachmentCount.
     * 
     * @return the size of this collection
     */
    public int getAttachmentCount(
    ) {
        return this._attachmentList.size();
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
     * Returns the value of field 'groupAttachTime'.
     * 
     * @return the value of field 'GroupAttachTime'.
     */
    public java.lang.String getGroupAttachTime(
    ) {
        return this._groupAttachTime;
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
     */
    public void removeAllAttachment(
    ) {
        this._attachmentList.clear();
    }

    /**
     * Method removeAttachment.
     * 
     * @param vAttachment
     * @return true if the object was removed from the collection.
     */
    public boolean removeAttachment(
            final org.amanzi.neo.loader.model.ams01.Attachment vAttachment) {
        boolean removed = _attachmentList.remove(vAttachment);
        return removed;
    }

    /**
     * Method removeAttachmentAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.amanzi.neo.loader.model.ams01.Attachment removeAttachmentAt(
            final int index) {
        java.lang.Object obj = this._attachmentList.remove(index);
        return (org.amanzi.neo.loader.model.ams01.Attachment) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vAttachment
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setAttachment(
            final int index,
            final org.amanzi.neo.loader.model.ams01.Attachment vAttachment)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._attachmentList.size()) {
            throw new IndexOutOfBoundsException("setAttachment: Index value '" + index + "' not in range [0.." + (this._attachmentList.size() - 1) + "]");
        }
        
        this._attachmentList.set(index, vAttachment);
    }

    /**
     * 
     * 
     * @param vAttachmentArray
     */
    public void setAttachment(
            final org.amanzi.neo.loader.model.ams01.Attachment[] vAttachmentArray) {
        //-- copy array
        _attachmentList.clear();
        
        for (int i = 0; i < vAttachmentArray.length; i++) {
                this._attachmentList.add(vAttachmentArray[i]);
        }
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
     * Sets the value of field 'groupAttachTime'.
     * 
     * @param groupAttachTime the value of field 'groupAttachTime'.
     */
    public void setGroupAttachTime(
            final java.lang.String groupAttachTime) {
        this._groupAttachTime = groupAttachTime;
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
