/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class CommonTestDataType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class CommonTestDataType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _probeIDNumberMapList.
     */
    private java.util.Vector _probeIDNumberMapList;

    /**
     * Field _servingDataList.
     */
    private java.util.Vector _servingDataList;

    /**
     * Field _neighborDataList.
     */
    private java.util.Vector _neighborDataList;

    /**
     * Field _ntpqList.
     */
    private java.util.Vector _ntpqList;

    /**
     * Field _mptSyncList.
     */
    private java.util.Vector _mptSyncList;


      //----------------/
     //- Constructors -/
    //----------------/

    public CommonTestDataType() {
        super();
        this._probeIDNumberMapList = new java.util.Vector();
        this._servingDataList = new java.util.Vector();
        this._neighborDataList = new java.util.Vector();
        this._ntpqList = new java.util.Vector();
        this._mptSyncList = new java.util.Vector();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vMptSync
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addMptSync(
            final org.amanzi.neo.loader.model.ams01.MptSync vMptSync)
    throws java.lang.IndexOutOfBoundsException {
        this._mptSyncList.addElement(vMptSync);
    }

    /**
     * 
     * 
     * @param index
     * @param vMptSync
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addMptSync(
            final int index,
            final org.amanzi.neo.loader.model.ams01.MptSync vMptSync)
    throws java.lang.IndexOutOfBoundsException {
        this._mptSyncList.add(index, vMptSync);
    }

    /**
     * 
     * 
     * @param vNeighborData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addNeighborData(
            final org.amanzi.neo.loader.model.ams01.NeighborData vNeighborData)
    throws java.lang.IndexOutOfBoundsException {
        this._neighborDataList.addElement(vNeighborData);
    }

    /**
     * 
     * 
     * @param index
     * @param vNeighborData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addNeighborData(
            final int index,
            final org.amanzi.neo.loader.model.ams01.NeighborData vNeighborData)
    throws java.lang.IndexOutOfBoundsException {
        this._neighborDataList.add(index, vNeighborData);
    }

    /**
     * 
     * 
     * @param vNtpq
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addNtpq(
            final org.amanzi.neo.loader.model.ams01.Ntpq vNtpq)
    throws java.lang.IndexOutOfBoundsException {
        this._ntpqList.addElement(vNtpq);
    }

    /**
     * 
     * 
     * @param index
     * @param vNtpq
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addNtpq(
            final int index,
            final org.amanzi.neo.loader.model.ams01.Ntpq vNtpq)
    throws java.lang.IndexOutOfBoundsException {
        this._ntpqList.add(index, vNtpq);
    }

    /**
     * 
     * 
     * @param vProbeIDNumberMap
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addProbeIDNumberMap(
            final org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap vProbeIDNumberMap)
    throws java.lang.IndexOutOfBoundsException {
        this._probeIDNumberMapList.addElement(vProbeIDNumberMap);
    }

    /**
     * 
     * 
     * @param index
     * @param vProbeIDNumberMap
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addProbeIDNumberMap(
            final int index,
            final org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap vProbeIDNumberMap)
    throws java.lang.IndexOutOfBoundsException {
        this._probeIDNumberMapList.add(index, vProbeIDNumberMap);
    }

    /**
     * 
     * 
     * @param vServingData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addServingData(
            final org.amanzi.neo.loader.model.ams01.ServingData vServingData)
    throws java.lang.IndexOutOfBoundsException {
        this._servingDataList.addElement(vServingData);
    }

    /**
     * 
     * 
     * @param index
     * @param vServingData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addServingData(
            final int index,
            final org.amanzi.neo.loader.model.ams01.ServingData vServingData)
    throws java.lang.IndexOutOfBoundsException {
        this._servingDataList.add(index, vServingData);
    }

    /**
     * Method enumerateMptSync.
     * 
     * @return an Enumeration over all
     * org.amanzi.neo.loader.model.ams01.MptSync elements
     */
    public java.util.Enumeration enumerateMptSync(
    ) {
        return this._mptSyncList.elements();
    }

    /**
     * Method enumerateNeighborData.
     * 
     * @return an Enumeration over all
     * org.amanzi.neo.loader.model.ams01.NeighborData elements
     */
    public java.util.Enumeration enumerateNeighborData(
    ) {
        return this._neighborDataList.elements();
    }

    /**
     * Method enumerateNtpq.
     * 
     * @return an Enumeration over all
     * org.amanzi.neo.loader.model.ams01.Ntpq elements
     */
    public java.util.Enumeration enumerateNtpq(
    ) {
        return this._ntpqList.elements();
    }

    /**
     * Method enumerateProbeIDNumberMap.
     * 
     * @return an Enumeration over all
     * org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap elements
     */
    public java.util.Enumeration enumerateProbeIDNumberMap(
    ) {
        return this._probeIDNumberMapList.elements();
    }

    /**
     * Method enumerateServingData.
     * 
     * @return an Enumeration over all
     * org.amanzi.neo.loader.model.ams01.ServingData elements
     */
    public java.util.Enumeration enumerateServingData(
    ) {
        return this._servingDataList.elements();
    }

    /**
     * Method getMptSync.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * org.amanzi.neo.loader.model.ams01.MptSync at the given index
     */
    public org.amanzi.neo.loader.model.ams01.MptSync getMptSync(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._mptSyncList.size()) {
            throw new IndexOutOfBoundsException("getMptSync: Index value '" + index + "' not in range [0.." + (this._mptSyncList.size() - 1) + "]");
        }
        
        return (org.amanzi.neo.loader.model.ams01.MptSync) _mptSyncList.get(index);
    }

    /**
     * Method getMptSync.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public org.amanzi.neo.loader.model.ams01.MptSync[] getMptSync(
    ) {
        org.amanzi.neo.loader.model.ams01.MptSync[] array = new org.amanzi.neo.loader.model.ams01.MptSync[0];
        return (org.amanzi.neo.loader.model.ams01.MptSync[]) this._mptSyncList.toArray(array);
    }

    /**
     * Method getMptSyncCount.
     * 
     * @return the size of this collection
     */
    public int getMptSyncCount(
    ) {
        return this._mptSyncList.size();
    }

    /**
     * Method getNeighborData.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * org.amanzi.neo.loader.model.ams01.NeighborData at the given
     * index
     */
    public org.amanzi.neo.loader.model.ams01.NeighborData getNeighborData(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._neighborDataList.size()) {
            throw new IndexOutOfBoundsException("getNeighborData: Index value '" + index + "' not in range [0.." + (this._neighborDataList.size() - 1) + "]");
        }
        
        return (org.amanzi.neo.loader.model.ams01.NeighborData) _neighborDataList.get(index);
    }

    /**
     * Method getNeighborData.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public org.amanzi.neo.loader.model.ams01.NeighborData[] getNeighborData(
    ) {
        org.amanzi.neo.loader.model.ams01.NeighborData[] array = new org.amanzi.neo.loader.model.ams01.NeighborData[0];
        return (org.amanzi.neo.loader.model.ams01.NeighborData[]) this._neighborDataList.toArray(array);
    }

    /**
     * Method getNeighborDataCount.
     * 
     * @return the size of this collection
     */
    public int getNeighborDataCount(
    ) {
        return this._neighborDataList.size();
    }

    /**
     * Method getNtpq.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * org.amanzi.neo.loader.model.ams01.Ntpq at the given index
     */
    public org.amanzi.neo.loader.model.ams01.Ntpq getNtpq(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ntpqList.size()) {
            throw new IndexOutOfBoundsException("getNtpq: Index value '" + index + "' not in range [0.." + (this._ntpqList.size() - 1) + "]");
        }
        
        return (org.amanzi.neo.loader.model.ams01.Ntpq) _ntpqList.get(index);
    }

    /**
     * Method getNtpq.Returns the contents of the collection in an
     * Array.  <p>Note:  Just in case the collection contents are
     * changing in another thread, we pass a 0-length Array of the
     * correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public org.amanzi.neo.loader.model.ams01.Ntpq[] getNtpq(
    ) {
        org.amanzi.neo.loader.model.ams01.Ntpq[] array = new org.amanzi.neo.loader.model.ams01.Ntpq[0];
        return (org.amanzi.neo.loader.model.ams01.Ntpq[]) this._ntpqList.toArray(array);
    }

    /**
     * Method getNtpqCount.
     * 
     * @return the size of this collection
     */
    public int getNtpqCount(
    ) {
        return this._ntpqList.size();
    }

    /**
     * Method getProbeIDNumberMap.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap at the
     * given index
     */
    public org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap getProbeIDNumberMap(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._probeIDNumberMapList.size()) {
            throw new IndexOutOfBoundsException("getProbeIDNumberMap: Index value '" + index + "' not in range [0.." + (this._probeIDNumberMapList.size() - 1) + "]");
        }
        
        return (org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap) _probeIDNumberMapList.get(index);
    }

    /**
     * Method getProbeIDNumberMap.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap[] getProbeIDNumberMap(
    ) {
        org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap[] array = new org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap[0];
        return (org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap[]) this._probeIDNumberMapList.toArray(array);
    }

    /**
     * Method getProbeIDNumberMapCount.
     * 
     * @return the size of this collection
     */
    public int getProbeIDNumberMapCount(
    ) {
        return this._probeIDNumberMapList.size();
    }

    /**
     * Method getServingData.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * org.amanzi.neo.loader.model.ams01.ServingData at the given
     * index
     */
    public org.amanzi.neo.loader.model.ams01.ServingData getServingData(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._servingDataList.size()) {
            throw new IndexOutOfBoundsException("getServingData: Index value '" + index + "' not in range [0.." + (this._servingDataList.size() - 1) + "]");
        }
        
        return (org.amanzi.neo.loader.model.ams01.ServingData) _servingDataList.get(index);
    }

    /**
     * Method getServingData.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public org.amanzi.neo.loader.model.ams01.ServingData[] getServingData(
    ) {
        org.amanzi.neo.loader.model.ams01.ServingData[] array = new org.amanzi.neo.loader.model.ams01.ServingData[0];
        return (org.amanzi.neo.loader.model.ams01.ServingData[]) this._servingDataList.toArray(array);
    }

    /**
     * Method getServingDataCount.
     * 
     * @return the size of this collection
     */
    public int getServingDataCount(
    ) {
        return this._servingDataList.size();
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
    public void removeAllMptSync(
    ) {
        this._mptSyncList.clear();
    }

    /**
     */
    public void removeAllNeighborData(
    ) {
        this._neighborDataList.clear();
    }

    /**
     */
    public void removeAllNtpq(
    ) {
        this._ntpqList.clear();
    }

    /**
     */
    public void removeAllProbeIDNumberMap(
    ) {
        this._probeIDNumberMapList.clear();
    }

    /**
     */
    public void removeAllServingData(
    ) {
        this._servingDataList.clear();
    }

    /**
     * Method removeMptSync.
     * 
     * @param vMptSync
     * @return true if the object was removed from the collection.
     */
    public boolean removeMptSync(
            final org.amanzi.neo.loader.model.ams01.MptSync vMptSync) {
        boolean removed = _mptSyncList.remove(vMptSync);
        return removed;
    }

    /**
     * Method removeMptSyncAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.amanzi.neo.loader.model.ams01.MptSync removeMptSyncAt(
            final int index) {
        java.lang.Object obj = this._mptSyncList.remove(index);
        return (org.amanzi.neo.loader.model.ams01.MptSync) obj;
    }

    /**
     * Method removeNeighborData.
     * 
     * @param vNeighborData
     * @return true if the object was removed from the collection.
     */
    public boolean removeNeighborData(
            final org.amanzi.neo.loader.model.ams01.NeighborData vNeighborData) {
        boolean removed = _neighborDataList.remove(vNeighborData);
        return removed;
    }

    /**
     * Method removeNeighborDataAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.amanzi.neo.loader.model.ams01.NeighborData removeNeighborDataAt(
            final int index) {
        java.lang.Object obj = this._neighborDataList.remove(index);
        return (org.amanzi.neo.loader.model.ams01.NeighborData) obj;
    }

    /**
     * Method removeNtpq.
     * 
     * @param vNtpq
     * @return true if the object was removed from the collection.
     */
    public boolean removeNtpq(
            final org.amanzi.neo.loader.model.ams01.Ntpq vNtpq) {
        boolean removed = _ntpqList.remove(vNtpq);
        return removed;
    }

    /**
     * Method removeNtpqAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.amanzi.neo.loader.model.ams01.Ntpq removeNtpqAt(
            final int index) {
        java.lang.Object obj = this._ntpqList.remove(index);
        return (org.amanzi.neo.loader.model.ams01.Ntpq) obj;
    }

    /**
     * Method removeProbeIDNumberMap.
     * 
     * @param vProbeIDNumberMap
     * @return true if the object was removed from the collection.
     */
    public boolean removeProbeIDNumberMap(
            final org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap vProbeIDNumberMap) {
        boolean removed = _probeIDNumberMapList.remove(vProbeIDNumberMap);
        return removed;
    }

    /**
     * Method removeProbeIDNumberMapAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap removeProbeIDNumberMapAt(
            final int index) {
        java.lang.Object obj = this._probeIDNumberMapList.remove(index);
        return (org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap) obj;
    }

    /**
     * Method removeServingData.
     * 
     * @param vServingData
     * @return true if the object was removed from the collection.
     */
    public boolean removeServingData(
            final org.amanzi.neo.loader.model.ams01.ServingData vServingData) {
        boolean removed = _servingDataList.remove(vServingData);
        return removed;
    }

    /**
     * Method removeServingDataAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.amanzi.neo.loader.model.ams01.ServingData removeServingDataAt(
            final int index) {
        java.lang.Object obj = this._servingDataList.remove(index);
        return (org.amanzi.neo.loader.model.ams01.ServingData) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vMptSync
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setMptSync(
            final int index,
            final org.amanzi.neo.loader.model.ams01.MptSync vMptSync)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._mptSyncList.size()) {
            throw new IndexOutOfBoundsException("setMptSync: Index value '" + index + "' not in range [0.." + (this._mptSyncList.size() - 1) + "]");
        }
        
        this._mptSyncList.set(index, vMptSync);
    }

    /**
     * 
     * 
     * @param vMptSyncArray
     */
    public void setMptSync(
            final org.amanzi.neo.loader.model.ams01.MptSync[] vMptSyncArray) {
        //-- copy array
        _mptSyncList.clear();
        
        for (int i = 0; i < vMptSyncArray.length; i++) {
                this._mptSyncList.add(vMptSyncArray[i]);
        }
    }

    /**
     * 
     * 
     * @param index
     * @param vNeighborData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setNeighborData(
            final int index,
            final org.amanzi.neo.loader.model.ams01.NeighborData vNeighborData)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._neighborDataList.size()) {
            throw new IndexOutOfBoundsException("setNeighborData: Index value '" + index + "' not in range [0.." + (this._neighborDataList.size() - 1) + "]");
        }
        
        this._neighborDataList.set(index, vNeighborData);
    }

    /**
     * 
     * 
     * @param vNeighborDataArray
     */
    public void setNeighborData(
            final org.amanzi.neo.loader.model.ams01.NeighborData[] vNeighborDataArray) {
        //-- copy array
        _neighborDataList.clear();
        
        for (int i = 0; i < vNeighborDataArray.length; i++) {
                this._neighborDataList.add(vNeighborDataArray[i]);
        }
    }

    /**
     * 
     * 
     * @param index
     * @param vNtpq
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setNtpq(
            final int index,
            final org.amanzi.neo.loader.model.ams01.Ntpq vNtpq)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ntpqList.size()) {
            throw new IndexOutOfBoundsException("setNtpq: Index value '" + index + "' not in range [0.." + (this._ntpqList.size() - 1) + "]");
        }
        
        this._ntpqList.set(index, vNtpq);
    }

    /**
     * 
     * 
     * @param vNtpqArray
     */
    public void setNtpq(
            final org.amanzi.neo.loader.model.ams01.Ntpq[] vNtpqArray) {
        //-- copy array
        _ntpqList.clear();
        
        for (int i = 0; i < vNtpqArray.length; i++) {
                this._ntpqList.add(vNtpqArray[i]);
        }
    }

    /**
     * 
     * 
     * @param index
     * @param vProbeIDNumberMap
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setProbeIDNumberMap(
            final int index,
            final org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap vProbeIDNumberMap)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._probeIDNumberMapList.size()) {
            throw new IndexOutOfBoundsException("setProbeIDNumberMap: Index value '" + index + "' not in range [0.." + (this._probeIDNumberMapList.size() - 1) + "]");
        }
        
        this._probeIDNumberMapList.set(index, vProbeIDNumberMap);
    }

    /**
     * 
     * 
     * @param vProbeIDNumberMapArray
     */
    public void setProbeIDNumberMap(
            final org.amanzi.neo.loader.model.ams01.ProbeIDNumberMap[] vProbeIDNumberMapArray) {
        //-- copy array
        _probeIDNumberMapList.clear();
        
        for (int i = 0; i < vProbeIDNumberMapArray.length; i++) {
                this._probeIDNumberMapList.add(vProbeIDNumberMapArray[i]);
        }
    }

    /**
     * 
     * 
     * @param index
     * @param vServingData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setServingData(
            final int index,
            final org.amanzi.neo.loader.model.ams01.ServingData vServingData)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._servingDataList.size()) {
            throw new IndexOutOfBoundsException("setServingData: Index value '" + index + "' not in range [0.." + (this._servingDataList.size() - 1) + "]");
        }
        
        this._servingDataList.set(index, vServingData);
    }

    /**
     * 
     * 
     * @param vServingDataArray
     */
    public void setServingData(
            final org.amanzi.neo.loader.model.ams01.ServingData[] vServingDataArray) {
        //-- copy array
        _servingDataList.clear();
        
        for (int i = 0; i < vServingDataArray.length; i++) {
                this._servingDataList.add(vServingDataArray[i]);
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
