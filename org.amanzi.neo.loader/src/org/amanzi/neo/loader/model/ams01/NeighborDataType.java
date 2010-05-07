/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class NeighborDataType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class NeighborDataType implements java.io.Serializable {


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
     * Field _neighborDetailsList.
     */
    private java.util.Vector _neighborDetailsList;


      //----------------/
     //- Constructors -/
    //----------------/

    public NeighborDataType() {
        super();
        this._neighborDetailsList = new java.util.Vector();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vNeighborDetails
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addNeighborDetails(
            final org.amanzi.neo.loader.model.ams01.NeighborDetails vNeighborDetails)
    throws java.lang.IndexOutOfBoundsException {
        // check for the maximum size
        if (this._neighborDetailsList.size() >= 31) {
            throw new IndexOutOfBoundsException("addNeighborDetails has a maximum of 31");
        }
        
        this._neighborDetailsList.addElement(vNeighborDetails);
    }

    /**
     * 
     * 
     * @param index
     * @param vNeighborDetails
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addNeighborDetails(
            final int index,
            final org.amanzi.neo.loader.model.ams01.NeighborDetails vNeighborDetails)
    throws java.lang.IndexOutOfBoundsException {
        // check for the maximum size
        if (this._neighborDetailsList.size() >= 31) {
            throw new IndexOutOfBoundsException("addNeighborDetails has a maximum of 31");
        }
        
        this._neighborDetailsList.add(index, vNeighborDetails);
    }

    /**
     * Method enumerateNeighborDetails.
     * 
     * @return an Enumeration over all
     * org.amanzi.neo.loader.model.ams01.NeighborDetails elements
     */
    public java.util.Enumeration enumerateNeighborDetails(
    ) {
        return this._neighborDetailsList.elements();
    }

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
     * Method getNeighborDetails.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * org.amanzi.neo.loader.model.ams01.NeighborDetails at the
     * given index
     */
    public org.amanzi.neo.loader.model.ams01.NeighborDetails getNeighborDetails(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._neighborDetailsList.size()) {
            throw new IndexOutOfBoundsException("getNeighborDetails: Index value '" + index + "' not in range [0.." + (this._neighborDetailsList.size() - 1) + "]");
        }
        
        return (org.amanzi.neo.loader.model.ams01.NeighborDetails) _neighborDetailsList.get(index);
    }

    /**
     * Method getNeighborDetails.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public org.amanzi.neo.loader.model.ams01.NeighborDetails[] getNeighborDetails(
    ) {
        org.amanzi.neo.loader.model.ams01.NeighborDetails[] array = new org.amanzi.neo.loader.model.ams01.NeighborDetails[0];
        return (org.amanzi.neo.loader.model.ams01.NeighborDetails[]) this._neighborDetailsList.toArray(array);
    }

    /**
     * Method getNeighborDetailsCount.
     * 
     * @return the size of this collection
     */
    public int getNeighborDetailsCount(
    ) {
        return this._neighborDetailsList.size();
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
     */
    public void removeAllNeighborDetails(
    ) {
        this._neighborDetailsList.clear();
    }

    /**
     * Method removeNeighborDetails.
     * 
     * @param vNeighborDetails
     * @return true if the object was removed from the collection.
     */
    public boolean removeNeighborDetails(
            final org.amanzi.neo.loader.model.ams01.NeighborDetails vNeighborDetails) {
        boolean removed = _neighborDetailsList.remove(vNeighborDetails);
        return removed;
    }

    /**
     * Method removeNeighborDetailsAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.amanzi.neo.loader.model.ams01.NeighborDetails removeNeighborDetailsAt(
            final int index) {
        java.lang.Object obj = this._neighborDetailsList.remove(index);
        return (org.amanzi.neo.loader.model.ams01.NeighborDetails) obj;
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
     * 
     * 
     * @param index
     * @param vNeighborDetails
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setNeighborDetails(
            final int index,
            final org.amanzi.neo.loader.model.ams01.NeighborDetails vNeighborDetails)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._neighborDetailsList.size()) {
            throw new IndexOutOfBoundsException("setNeighborDetails: Index value '" + index + "' not in range [0.." + (this._neighborDetailsList.size() - 1) + "]");
        }
        
        this._neighborDetailsList.set(index, vNeighborDetails);
    }

    /**
     * 
     * 
     * @param vNeighborDetailsArray
     */
    public void setNeighborDetails(
            final org.amanzi.neo.loader.model.ams01.NeighborDetails[] vNeighborDetailsArray) {
        //-- copy array
        _neighborDetailsList.clear();
        
        for (int i = 0; i < vNeighborDetailsArray.length; i++) {
                this._neighborDetailsList.add(vNeighborDetailsArray[i]);
        }
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
