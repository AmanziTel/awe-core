/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class TOCType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class TOCType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _probeID.
     */
    private java.lang.String _probeID;

    /**
     * Field _calledNumber.
     */
    private java.lang.String _calledNumber;

    /**
     * Field _hook.
     */
    private int _hook;

    /**
     * keeps track of state for field: _hook
     */
    private boolean _has_hook;

    /**
     * Field _simplex.
     */
    private int _simplex;

    /**
     * keeps track of state for field: _simplex
     */
    private boolean _has_simplex;

    /**
     * Field _priority.
     */
    private int _priority;

    /**
     * keeps track of state for field: _priority
     */
    private boolean _has_priority;

    /**
     * Field _configTime.
     */
    private java.lang.String _configTime;

    /**
     * Field _setupTime.
     */
    private java.lang.String _setupTime;

    /**
     * Field _connectTime.
     */
    private java.lang.String _connectTime;

    /**
     * Field _disconnectTime.
     */
    private java.lang.String _disconnectTime;

    /**
     * Field _releaseTime.
     */
    private java.lang.String _releaseTime;

    /**
     * Field _causeForTermination.
     */
    private int _causeForTermination;

    /**
     * keeps track of state for field: _causeForTermination
     */
    private boolean _has_causeForTermination;

    /**
     * Field _errorCode.
     */
    private int _errorCode;

    /**
     * keeps track of state for field: _errorCode
     */
    private boolean _has_errorCode;

    /**
     * Field _pesqResultList.
     */
    private java.util.Vector _pesqResultList;

    /**
     * Field _TOCIsInclusive.
     */
    private org.amanzi.neo.loader.model.ams01.TOCIsInclusive _TOCIsInclusive;


      //----------------/
     //- Constructors -/
    //----------------/

    public TOCType() {
        super();
        this._pesqResultList = new java.util.Vector();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vPesqResult
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPesqResult(
            final org.amanzi.neo.loader.model.ams01.PesqResult vPesqResult)
    throws java.lang.IndexOutOfBoundsException {
        this._pesqResultList.addElement(vPesqResult);
    }

    /**
     * 
     * 
     * @param index
     * @param vPesqResult
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPesqResult(
            final int index,
            final org.amanzi.neo.loader.model.ams01.PesqResult vPesqResult)
    throws java.lang.IndexOutOfBoundsException {
        this._pesqResultList.add(index, vPesqResult);
    }

    /**
     */
    public void deleteCauseForTermination(
    ) {
        this._has_causeForTermination= false;
    }

    /**
     */
    public void deleteErrorCode(
    ) {
        this._has_errorCode= false;
    }

    /**
     */
    public void deleteHook(
    ) {
        this._has_hook= false;
    }

    /**
     */
    public void deletePriority(
    ) {
        this._has_priority= false;
    }

    /**
     */
    public void deleteSimplex(
    ) {
        this._has_simplex= false;
    }

    /**
     * Method enumeratePesqResult.
     * 
     * @return an Enumeration over all
     * org.amanzi.neo.loader.model.ams01.PesqResult elements
     */
    public java.util.Enumeration enumeratePesqResult(
    ) {
        return this._pesqResultList.elements();
    }

    /**
     * Returns the value of field 'calledNumber'.
     * 
     * @return the value of field 'CalledNumber'.
     */
    public java.lang.String getCalledNumber(
    ) {
        return this._calledNumber;
    }

    /**
     * Returns the value of field 'causeForTermination'.
     * 
     * @return the value of field 'CauseForTermination'.
     */
    public int getCauseForTermination(
    ) {
        return this._causeForTermination;
    }

    /**
     * Returns the value of field 'configTime'.
     * 
     * @return the value of field 'ConfigTime'.
     */
    public java.lang.String getConfigTime(
    ) {
        return this._configTime;
    }

    /**
     * Returns the value of field 'connectTime'.
     * 
     * @return the value of field 'ConnectTime'.
     */
    public java.lang.String getConnectTime(
    ) {
        return this._connectTime;
    }

    /**
     * Returns the value of field 'disconnectTime'.
     * 
     * @return the value of field 'DisconnectTime'.
     */
    public java.lang.String getDisconnectTime(
    ) {
        return this._disconnectTime;
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
     * Returns the value of field 'hook'.
     * 
     * @return the value of field 'Hook'.
     */
    public int getHook(
    ) {
        return this._hook;
    }

    /**
     * Method getPesqResult.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * org.amanzi.neo.loader.model.ams01.PesqResult at the given
     * index
     */
    public org.amanzi.neo.loader.model.ams01.PesqResult getPesqResult(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._pesqResultList.size()) {
            throw new IndexOutOfBoundsException("getPesqResult: Index value '" + index + "' not in range [0.." + (this._pesqResultList.size() - 1) + "]");
        }
        
        return (org.amanzi.neo.loader.model.ams01.PesqResult) _pesqResultList.get(index);
    }

    /**
     * Method getPesqResult.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public org.amanzi.neo.loader.model.ams01.PesqResult[] getPesqResult(
    ) {
        org.amanzi.neo.loader.model.ams01.PesqResult[] array = new org.amanzi.neo.loader.model.ams01.PesqResult[0];
        return (org.amanzi.neo.loader.model.ams01.PesqResult[]) this._pesqResultList.toArray(array);
    }

    /**
     * Method getPesqResultCount.
     * 
     * @return the size of this collection
     */
    public int getPesqResultCount(
    ) {
        return this._pesqResultList.size();
    }

    /**
     * Returns the value of field 'priority'.
     * 
     * @return the value of field 'Priority'.
     */
    public int getPriority(
    ) {
        return this._priority;
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
     * Returns the value of field 'releaseTime'.
     * 
     * @return the value of field 'ReleaseTime'.
     */
    public java.lang.String getReleaseTime(
    ) {
        return this._releaseTime;
    }

    /**
     * Returns the value of field 'setupTime'.
     * 
     * @return the value of field 'SetupTime'.
     */
    public java.lang.String getSetupTime(
    ) {
        return this._setupTime;
    }

    /**
     * Returns the value of field 'simplex'.
     * 
     * @return the value of field 'Simplex'.
     */
    public int getSimplex(
    ) {
        return this._simplex;
    }

    /**
     * Returns the value of field 'TOCIsInclusive'.
     * 
     * @return the value of field 'TOCIsInclusive'.
     */
    public org.amanzi.neo.loader.model.ams01.TOCIsInclusive getTOCIsInclusive(
    ) {
        return this._TOCIsInclusive;
    }

    /**
     * Method hasCauseForTermination.
     * 
     * @return true if at least one CauseForTermination has been
     * added
     */
    public boolean hasCauseForTermination(
    ) {
        return this._has_causeForTermination;
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
     * Method hasHook.
     * 
     * @return true if at least one Hook has been added
     */
    public boolean hasHook(
    ) {
        return this._has_hook;
    }

    /**
     * Method hasPriority.
     * 
     * @return true if at least one Priority has been added
     */
    public boolean hasPriority(
    ) {
        return this._has_priority;
    }

    /**
     * Method hasSimplex.
     * 
     * @return true if at least one Simplex has been added
     */
    public boolean hasSimplex(
    ) {
        return this._has_simplex;
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
    public void removeAllPesqResult(
    ) {
        this._pesqResultList.clear();
    }

    /**
     * Method removePesqResult.
     * 
     * @param vPesqResult
     * @return true if the object was removed from the collection.
     */
    public boolean removePesqResult(
            final org.amanzi.neo.loader.model.ams01.PesqResult vPesqResult) {
        boolean removed = _pesqResultList.remove(vPesqResult);
        return removed;
    }

    /**
     * Method removePesqResultAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.amanzi.neo.loader.model.ams01.PesqResult removePesqResultAt(
            final int index) {
        java.lang.Object obj = this._pesqResultList.remove(index);
        return (org.amanzi.neo.loader.model.ams01.PesqResult) obj;
    }

    /**
     * Sets the value of field 'calledNumber'.
     * 
     * @param calledNumber the value of field 'calledNumber'.
     */
    public void setCalledNumber(
            final java.lang.String calledNumber) {
        this._calledNumber = calledNumber;
    }

    /**
     * Sets the value of field 'causeForTermination'.
     * 
     * @param causeForTermination the value of field
     * 'causeForTermination'.
     */
    public void setCauseForTermination(
            final int causeForTermination) {
        this._causeForTermination = causeForTermination;
        this._has_causeForTermination = true;
    }

    /**
     * Sets the value of field 'configTime'.
     * 
     * @param configTime the value of field 'configTime'.
     */
    public void setConfigTime(
            final java.lang.String configTime) {
        this._configTime = configTime;
    }

    /**
     * Sets the value of field 'connectTime'.
     * 
     * @param connectTime the value of field 'connectTime'.
     */
    public void setConnectTime(
            final java.lang.String connectTime) {
        this._connectTime = connectTime;
    }

    /**
     * Sets the value of field 'disconnectTime'.
     * 
     * @param disconnectTime the value of field 'disconnectTime'.
     */
    public void setDisconnectTime(
            final java.lang.String disconnectTime) {
        this._disconnectTime = disconnectTime;
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
     * Sets the value of field 'hook'.
     * 
     * @param hook the value of field 'hook'.
     */
    public void setHook(
            final int hook) {
        this._hook = hook;
        this._has_hook = true;
    }

    /**
     * 
     * 
     * @param index
     * @param vPesqResult
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPesqResult(
            final int index,
            final org.amanzi.neo.loader.model.ams01.PesqResult vPesqResult)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._pesqResultList.size()) {
            throw new IndexOutOfBoundsException("setPesqResult: Index value '" + index + "' not in range [0.." + (this._pesqResultList.size() - 1) + "]");
        }
        
        this._pesqResultList.set(index, vPesqResult);
    }

    /**
     * 
     * 
     * @param vPesqResultArray
     */
    public void setPesqResult(
            final org.amanzi.neo.loader.model.ams01.PesqResult[] vPesqResultArray) {
        //-- copy array
        _pesqResultList.clear();
        
        for (int i = 0; i < vPesqResultArray.length; i++) {
                this._pesqResultList.add(vPesqResultArray[i]);
        }
    }

    /**
     * Sets the value of field 'priority'.
     * 
     * @param priority the value of field 'priority'.
     */
    public void setPriority(
            final int priority) {
        this._priority = priority;
        this._has_priority = true;
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
     * Sets the value of field 'releaseTime'.
     * 
     * @param releaseTime the value of field 'releaseTime'.
     */
    public void setReleaseTime(
            final java.lang.String releaseTime) {
        this._releaseTime = releaseTime;
    }

    /**
     * Sets the value of field 'setupTime'.
     * 
     * @param setupTime the value of field 'setupTime'.
     */
    public void setSetupTime(
            final java.lang.String setupTime) {
        this._setupTime = setupTime;
    }

    /**
     * Sets the value of field 'simplex'.
     * 
     * @param simplex the value of field 'simplex'.
     */
    public void setSimplex(
            final int simplex) {
        this._simplex = simplex;
        this._has_simplex = true;
    }

    /**
     * Sets the value of field 'TOCIsInclusive'.
     * 
     * @param TOCIsInclusive the value of field 'TOCIsInclusive'.
     */
    public void setTOCIsInclusive(
            final org.amanzi.neo.loader.model.ams01.TOCIsInclusive TOCIsInclusive) {
        this._TOCIsInclusive = TOCIsInclusive;
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
