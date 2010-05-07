/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class ValidGpsDataListType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class ValidGpsDataListType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _validGpsDataList.
     */
    private java.util.Vector _validGpsDataList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ValidGpsDataListType() {
        super();
        this._validGpsDataList = new java.util.Vector();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vValidGpsData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addValidGpsData(
            final org.amanzi.neo.loader.model.ams01.ValidGpsData vValidGpsData)
    throws java.lang.IndexOutOfBoundsException {
        this._validGpsDataList.addElement(vValidGpsData);
    }

    /**
     * 
     * 
     * @param index
     * @param vValidGpsData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addValidGpsData(
            final int index,
            final org.amanzi.neo.loader.model.ams01.ValidGpsData vValidGpsData)
    throws java.lang.IndexOutOfBoundsException {
        this._validGpsDataList.add(index, vValidGpsData);
    }

    /**
     * Method enumerateValidGpsData.
     * 
     * @return an Enumeration over all
     * org.amanzi.neo.loader.model.ams01.ValidGpsData elements
     */
    public java.util.Enumeration enumerateValidGpsData(
    ) {
        return this._validGpsDataList.elements();
    }

    /**
     * Method getValidGpsData.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * org.amanzi.neo.loader.model.ams01.ValidGpsData at the given
     * index
     */
    public org.amanzi.neo.loader.model.ams01.ValidGpsData getValidGpsData(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._validGpsDataList.size()) {
            throw new IndexOutOfBoundsException("getValidGpsData: Index value '" + index + "' not in range [0.." + (this._validGpsDataList.size() - 1) + "]");
        }
        
        return (org.amanzi.neo.loader.model.ams01.ValidGpsData) _validGpsDataList.get(index);
    }

    /**
     * Method getValidGpsData.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public org.amanzi.neo.loader.model.ams01.ValidGpsData[] getValidGpsData(
    ) {
        org.amanzi.neo.loader.model.ams01.ValidGpsData[] array = new org.amanzi.neo.loader.model.ams01.ValidGpsData[0];
        return (org.amanzi.neo.loader.model.ams01.ValidGpsData[]) this._validGpsDataList.toArray(array);
    }

    /**
     * Method getValidGpsDataCount.
     * 
     * @return the size of this collection
     */
    public int getValidGpsDataCount(
    ) {
        return this._validGpsDataList.size();
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
    public void removeAllValidGpsData(
    ) {
        this._validGpsDataList.clear();
    }

    /**
     * Method removeValidGpsData.
     * 
     * @param vValidGpsData
     * @return true if the object was removed from the collection.
     */
    public boolean removeValidGpsData(
            final org.amanzi.neo.loader.model.ams01.ValidGpsData vValidGpsData) {
        boolean removed = _validGpsDataList.remove(vValidGpsData);
        return removed;
    }

    /**
     * Method removeValidGpsDataAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.amanzi.neo.loader.model.ams01.ValidGpsData removeValidGpsDataAt(
            final int index) {
        java.lang.Object obj = this._validGpsDataList.remove(index);
        return (org.amanzi.neo.loader.model.ams01.ValidGpsData) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vValidGpsData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setValidGpsData(
            final int index,
            final org.amanzi.neo.loader.model.ams01.ValidGpsData vValidGpsData)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._validGpsDataList.size()) {
            throw new IndexOutOfBoundsException("setValidGpsData: Index value '" + index + "' not in range [0.." + (this._validGpsDataList.size() - 1) + "]");
        }
        
        this._validGpsDataList.set(index, vValidGpsData);
    }

    /**
     * 
     * 
     * @param vValidGpsDataArray
     */
    public void setValidGpsData(
            final org.amanzi.neo.loader.model.ams01.ValidGpsData[] vValidGpsDataArray) {
        //-- copy array
        _validGpsDataList.clear();
        
        for (int i = 0; i < vValidGpsDataArray.length; i++) {
                this._validGpsDataList.add(vValidGpsDataArray[i]);
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
