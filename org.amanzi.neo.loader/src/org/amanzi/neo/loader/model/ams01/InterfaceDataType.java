/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class InterfaceDataType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class InterfaceDataType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _commonTestData.
     */
    private org.amanzi.neo.loader.model.ams01.CommonTestData _commonTestData;

    /**
     * Field _eventsList.
     */
    private java.util.Vector _eventsList;

    /**
     * Field _gpsData.
     */
    private org.amanzi.neo.loader.model.ams01.GpsData _gpsData;


      //----------------/
     //- Constructors -/
    //----------------/

    public InterfaceDataType() {
        super();
        this._eventsList = new java.util.Vector();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vEvents
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addEvents(
            final org.amanzi.neo.loader.model.ams01.Events vEvents)
    throws java.lang.IndexOutOfBoundsException {
        this._eventsList.addElement(vEvents);
    }

    /**
     * 
     * 
     * @param index
     * @param vEvents
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addEvents(
            final int index,
            final org.amanzi.neo.loader.model.ams01.Events vEvents)
    throws java.lang.IndexOutOfBoundsException {
        this._eventsList.add(index, vEvents);
    }

    /**
     * Method enumerateEvents.
     * 
     * @return an Enumeration over all
     * org.amanzi.neo.loader.model.ams01.Events elements
     */
    public java.util.Enumeration enumerateEvents(
    ) {
        return this._eventsList.elements();
    }

    /**
     * Returns the value of field 'commonTestData'.
     * 
     * @return the value of field 'CommonTestData'.
     */
    public org.amanzi.neo.loader.model.ams01.CommonTestData getCommonTestData(
    ) {
        return this._commonTestData;
    }

    /**
     * Method getEvents.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * org.amanzi.neo.loader.model.ams01.Events at the given index
     */
    public org.amanzi.neo.loader.model.ams01.Events getEvents(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._eventsList.size()) {
            throw new IndexOutOfBoundsException("getEvents: Index value '" + index + "' not in range [0.." + (this._eventsList.size() - 1) + "]");
        }
        
        return (org.amanzi.neo.loader.model.ams01.Events) _eventsList.get(index);
    }

    /**
     * Method getEvents.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public org.amanzi.neo.loader.model.ams01.Events[] getEvents(
    ) {
        org.amanzi.neo.loader.model.ams01.Events[] array = new org.amanzi.neo.loader.model.ams01.Events[0];
        return (org.amanzi.neo.loader.model.ams01.Events[]) this._eventsList.toArray(array);
    }

    /**
     * Method getEventsCount.
     * 
     * @return the size of this collection
     */
    public int getEventsCount(
    ) {
        return this._eventsList.size();
    }

    /**
     * Returns the value of field 'gpsData'.
     * 
     * @return the value of field 'GpsData'.
     */
    public org.amanzi.neo.loader.model.ams01.GpsData getGpsData(
    ) {
        return this._gpsData;
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
    public void removeAllEvents(
    ) {
        this._eventsList.clear();
    }

    /**
     * Method removeEvents.
     * 
     * @param vEvents
     * @return true if the object was removed from the collection.
     */
    public boolean removeEvents(
            final org.amanzi.neo.loader.model.ams01.Events vEvents) {
        boolean removed = _eventsList.remove(vEvents);
        return removed;
    }

    /**
     * Method removeEventsAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.amanzi.neo.loader.model.ams01.Events removeEventsAt(
            final int index) {
        java.lang.Object obj = this._eventsList.remove(index);
        return (org.amanzi.neo.loader.model.ams01.Events) obj;
    }

    /**
     * Sets the value of field 'commonTestData'.
     * 
     * @param commonTestData the value of field 'commonTestData'.
     */
    public void setCommonTestData(
            final org.amanzi.neo.loader.model.ams01.CommonTestData commonTestData) {
        this._commonTestData = commonTestData;
    }

    /**
     * 
     * 
     * @param index
     * @param vEvents
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setEvents(
            final int index,
            final org.amanzi.neo.loader.model.ams01.Events vEvents)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._eventsList.size()) {
            throw new IndexOutOfBoundsException("setEvents: Index value '" + index + "' not in range [0.." + (this._eventsList.size() - 1) + "]");
        }
        
        this._eventsList.set(index, vEvents);
    }

    /**
     * 
     * 
     * @param vEventsArray
     */
    public void setEvents(
            final org.amanzi.neo.loader.model.ams01.Events[] vEventsArray) {
        //-- copy array
        _eventsList.clear();
        
        for (int i = 0; i < vEventsArray.length; i++) {
                this._eventsList.add(vEventsArray[i]);
        }
    }

    /**
     * Sets the value of field 'gpsData'.
     * 
     * @param gpsData the value of field 'gpsData'.
     */
    public void setGpsData(
            final org.amanzi.neo.loader.model.ams01.GpsData gpsData) {
        this._gpsData = gpsData;
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
