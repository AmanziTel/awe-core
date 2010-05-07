/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class CompleteGpsDataListType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class CompleteGpsDataListType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _completeGpsDataList.
     */
    private java.util.Vector _completeGpsDataList;


      //----------------/
     //- Constructors -/
    //----------------/

    public CompleteGpsDataListType() {
        super();
        this._completeGpsDataList = new java.util.Vector();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vCompleteGpsData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addCompleteGpsData(
            final org.amanzi.neo.loader.model.ams01.CompleteGpsData vCompleteGpsData)
    throws java.lang.IndexOutOfBoundsException {
        this._completeGpsDataList.addElement(vCompleteGpsData);
    }

    /**
     * 
     * 
     * @param index
     * @param vCompleteGpsData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addCompleteGpsData(
            final int index,
            final org.amanzi.neo.loader.model.ams01.CompleteGpsData vCompleteGpsData)
    throws java.lang.IndexOutOfBoundsException {
        this._completeGpsDataList.add(index, vCompleteGpsData);
    }

    /**
     * Method enumerateCompleteGpsData.
     * 
     * @return an Enumeration over all
     * org.amanzi.neo.loader.model.ams01.CompleteGpsData elements
     */
    public java.util.Enumeration enumerateCompleteGpsData(
    ) {
        return this._completeGpsDataList.elements();
    }

    /**
     * Method getCompleteGpsData.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * org.amanzi.neo.loader.model.ams01.CompleteGpsData at the
     * given index
     */
    public org.amanzi.neo.loader.model.ams01.CompleteGpsData getCompleteGpsData(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._completeGpsDataList.size()) {
            throw new IndexOutOfBoundsException("getCompleteGpsData: Index value '" + index + "' not in range [0.." + (this._completeGpsDataList.size() - 1) + "]");
        }
        
        return (org.amanzi.neo.loader.model.ams01.CompleteGpsData) _completeGpsDataList.get(index);
    }

    /**
     * Method getCompleteGpsData.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public org.amanzi.neo.loader.model.ams01.CompleteGpsData[] getCompleteGpsData(
    ) {
        org.amanzi.neo.loader.model.ams01.CompleteGpsData[] array = new org.amanzi.neo.loader.model.ams01.CompleteGpsData[0];
        return (org.amanzi.neo.loader.model.ams01.CompleteGpsData[]) this._completeGpsDataList.toArray(array);
    }

    /**
     * Method getCompleteGpsDataCount.
     * 
     * @return the size of this collection
     */
    public int getCompleteGpsDataCount(
    ) {
        return this._completeGpsDataList.size();
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
    public void removeAllCompleteGpsData(
    ) {
        this._completeGpsDataList.clear();
    }

    /**
     * Method removeCompleteGpsData.
     * 
     * @param vCompleteGpsData
     * @return true if the object was removed from the collection.
     */
    public boolean removeCompleteGpsData(
            final org.amanzi.neo.loader.model.ams01.CompleteGpsData vCompleteGpsData) {
        boolean removed = _completeGpsDataList.remove(vCompleteGpsData);
        return removed;
    }

    /**
     * Method removeCompleteGpsDataAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.amanzi.neo.loader.model.ams01.CompleteGpsData removeCompleteGpsDataAt(
            final int index) {
        java.lang.Object obj = this._completeGpsDataList.remove(index);
        return (org.amanzi.neo.loader.model.ams01.CompleteGpsData) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vCompleteGpsData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setCompleteGpsData(
            final int index,
            final org.amanzi.neo.loader.model.ams01.CompleteGpsData vCompleteGpsData)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._completeGpsDataList.size()) {
            throw new IndexOutOfBoundsException("setCompleteGpsData: Index value '" + index + "' not in range [0.." + (this._completeGpsDataList.size() - 1) + "]");
        }
        
        this._completeGpsDataList.set(index, vCompleteGpsData);
    }

    /**
     * 
     * 
     * @param vCompleteGpsDataArray
     */
    public void setCompleteGpsData(
            final org.amanzi.neo.loader.model.ams01.CompleteGpsData[] vCompleteGpsDataArray) {
        //-- copy array
        _completeGpsDataList.clear();
        
        for (int i = 0; i < vCompleteGpsDataArray.length; i++) {
                this._completeGpsDataList.add(vCompleteGpsDataArray[i]);
        }
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
