/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class MptSyncType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class MptSyncType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _probeID.
     */
    private java.lang.String _probeID;

    /**
     * Field _mptSyncTime.
     */
    private java.lang.String _mptSyncTime;

    /**
     * Field _probeList.
     */
    private java.lang.String _probeList;

    /**
     * Field _syncID.
     */
    private int _syncID;

    /**
     * keeps track of state for field: _syncID
     */
    private boolean _has_syncID;

    /**
     * Field _timeOut.
     */
    private int _timeOut;

    /**
     * keeps track of state for field: _timeOut
     */
    private boolean _has_timeOut;


      //----------------/
     //- Constructors -/
    //----------------/

    public MptSyncType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteSyncID(
    ) {
        this._has_syncID= false;
    }

    /**
     */
    public void deleteTimeOut(
    ) {
        this._has_timeOut= false;
    }

    /**
     * Returns the value of field 'mptSyncTime'.
     * 
     * @return the value of field 'MptSyncTime'.
     */
    public java.lang.String getMptSyncTime(
    ) {
        return this._mptSyncTime;
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
     * Returns the value of field 'probeList'.
     * 
     * @return the value of field 'ProbeList'.
     */
    public java.lang.String getProbeList(
    ) {
        return this._probeList;
    }

    /**
     * Returns the value of field 'syncID'.
     * 
     * @return the value of field 'SyncID'.
     */
    public int getSyncID(
    ) {
        return this._syncID;
    }

    /**
     * Returns the value of field 'timeOut'.
     * 
     * @return the value of field 'TimeOut'.
     */
    public int getTimeOut(
    ) {
        return this._timeOut;
    }

    /**
     * Method hasSyncID.
     * 
     * @return true if at least one SyncID has been added
     */
    public boolean hasSyncID(
    ) {
        return this._has_syncID;
    }

    /**
     * Method hasTimeOut.
     * 
     * @return true if at least one TimeOut has been added
     */
    public boolean hasTimeOut(
    ) {
        return this._has_timeOut;
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
     * Sets the value of field 'mptSyncTime'.
     * 
     * @param mptSyncTime the value of field 'mptSyncTime'.
     */
    public void setMptSyncTime(
            final java.lang.String mptSyncTime) {
        this._mptSyncTime = mptSyncTime;
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
     * Sets the value of field 'probeList'.
     * 
     * @param probeList the value of field 'probeList'.
     */
    public void setProbeList(
            final java.lang.String probeList) {
        this._probeList = probeList;
    }

    /**
     * Sets the value of field 'syncID'.
     * 
     * @param syncID the value of field 'syncID'.
     */
    public void setSyncID(
            final int syncID) {
        this._syncID = syncID;
        this._has_syncID = true;
    }

    /**
     * Sets the value of field 'timeOut'.
     * 
     * @param timeOut the value of field 'timeOut'.
     */
    public void setTimeOut(
            final int timeOut) {
        this._timeOut = timeOut;
        this._has_timeOut = true;
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
