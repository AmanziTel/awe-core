/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class TTCType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class TTCType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _probeID.
     */
    private java.lang.String _probeID;

    /**
     * Field _callingNumber.
     */
    private java.lang.String _callingNumber;

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
     * Field _indicationTime.
     */
    private java.lang.String _indicationTime;

    /**
     * Field _answerTime.
     */
    private java.lang.String _answerTime;

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
     * Field _TTCPesqResultList.
     */
    private java.util.Vector _TTCPesqResultList;

    /**
     * Field _TTCIsInclusive.
     */
    private org.amanzi.neo.loader.model.ams01.TTCIsInclusive _TTCIsInclusive;


      //----------------/
     //- Constructors -/
    //----------------/

    public TTCType() {
        super();
        this._TTCPesqResultList = new java.util.Vector();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vTTCPesqResult
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addTTCPesqResult(
            final org.amanzi.neo.loader.model.ams01.TTCPesqResult vTTCPesqResult)
    throws java.lang.IndexOutOfBoundsException {
        this._TTCPesqResultList.addElement(vTTCPesqResult);
    }

    /**
     * 
     * 
     * @param index
     * @param vTTCPesqResult
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addTTCPesqResult(
            final int index,
            final org.amanzi.neo.loader.model.ams01.TTCPesqResult vTTCPesqResult)
    throws java.lang.IndexOutOfBoundsException {
        this._TTCPesqResultList.add(index, vTTCPesqResult);
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
    public void deleteSimplex(
    ) {
        this._has_simplex= false;
    }

    /**
     * Method enumerateTTCPesqResult.
     * 
     * @return an Enumeration over all
     * org.amanzi.neo.loader.model.ams01.TTCPesqResult elements
     */
    public java.util.Enumeration enumerateTTCPesqResult(
    ) {
        return this._TTCPesqResultList.elements();
    }

    /**
     * Returns the value of field 'answerTime'.
     * 
     * @return the value of field 'AnswerTime'.
     */
    public java.lang.String getAnswerTime(
    ) {
        return this._answerTime;
    }

    /**
     * Returns the value of field 'callingNumber'.
     * 
     * @return the value of field 'CallingNumber'.
     */
    public java.lang.String getCallingNumber(
    ) {
        return this._callingNumber;
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
     * Returns the value of field 'indicationTime'.
     * 
     * @return the value of field 'IndicationTime'.
     */
    public java.lang.String getIndicationTime(
    ) {
        return this._indicationTime;
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
     * Returns the value of field 'simplex'.
     * 
     * @return the value of field 'Simplex'.
     */
    public int getSimplex(
    ) {
        return this._simplex;
    }

    /**
     * Returns the value of field 'TTCIsInclusive'.
     * 
     * @return the value of field 'TTCIsInclusive'.
     */
    public org.amanzi.neo.loader.model.ams01.TTCIsInclusive getTTCIsInclusive(
    ) {
        return this._TTCIsInclusive;
    }

    /**
     * Method getTTCPesqResult.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * org.amanzi.neo.loader.model.ams01.TTCPesqResult at the given
     * index
     */
    public org.amanzi.neo.loader.model.ams01.TTCPesqResult getTTCPesqResult(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._TTCPesqResultList.size()) {
            throw new IndexOutOfBoundsException("getTTCPesqResult: Index value '" + index + "' not in range [0.." + (this._TTCPesqResultList.size() - 1) + "]");
        }
        
        return (org.amanzi.neo.loader.model.ams01.TTCPesqResult) _TTCPesqResultList.get(index);
    }

    /**
     * Method getTTCPesqResult.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public org.amanzi.neo.loader.model.ams01.TTCPesqResult[] getTTCPesqResult(
    ) {
        org.amanzi.neo.loader.model.ams01.TTCPesqResult[] array = new org.amanzi.neo.loader.model.ams01.TTCPesqResult[0];
        return (org.amanzi.neo.loader.model.ams01.TTCPesqResult[]) this._TTCPesqResultList.toArray(array);
    }

    /**
     * Method getTTCPesqResultCount.
     * 
     * @return the size of this collection
     */
    public int getTTCPesqResultCount(
    ) {
        return this._TTCPesqResultList.size();
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
    public void removeAllTTCPesqResult(
    ) {
        this._TTCPesqResultList.clear();
    }

    /**
     * Method removeTTCPesqResult.
     * 
     * @param vTTCPesqResult
     * @return true if the object was removed from the collection.
     */
    public boolean removeTTCPesqResult(
            final org.amanzi.neo.loader.model.ams01.TTCPesqResult vTTCPesqResult) {
        boolean removed = _TTCPesqResultList.remove(vTTCPesqResult);
        return removed;
    }

    /**
     * Method removeTTCPesqResultAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.amanzi.neo.loader.model.ams01.TTCPesqResult removeTTCPesqResultAt(
            final int index) {
        java.lang.Object obj = this._TTCPesqResultList.remove(index);
        return (org.amanzi.neo.loader.model.ams01.TTCPesqResult) obj;
    }

    /**
     * Sets the value of field 'answerTime'.
     * 
     * @param answerTime the value of field 'answerTime'.
     */
    public void setAnswerTime(
            final java.lang.String answerTime) {
        this._answerTime = answerTime;
    }

    /**
     * Sets the value of field 'callingNumber'.
     * 
     * @param callingNumber the value of field 'callingNumber'.
     */
    public void setCallingNumber(
            final java.lang.String callingNumber) {
        this._callingNumber = callingNumber;
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
     * Sets the value of field 'indicationTime'.
     * 
     * @param indicationTime the value of field 'indicationTime'.
     */
    public void setIndicationTime(
            final java.lang.String indicationTime) {
        this._indicationTime = indicationTime;
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
     * Sets the value of field 'TTCIsInclusive'.
     * 
     * @param TTCIsInclusive the value of field 'TTCIsInclusive'.
     */
    public void setTTCIsInclusive(
            final org.amanzi.neo.loader.model.ams01.TTCIsInclusive TTCIsInclusive) {
        this._TTCIsInclusive = TTCIsInclusive;
    }

    /**
     * 
     * 
     * @param index
     * @param vTTCPesqResult
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setTTCPesqResult(
            final int index,
            final org.amanzi.neo.loader.model.ams01.TTCPesqResult vTTCPesqResult)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._TTCPesqResultList.size()) {
            throw new IndexOutOfBoundsException("setTTCPesqResult: Index value '" + index + "' not in range [0.." + (this._TTCPesqResultList.size() - 1) + "]");
        }
        
        this._TTCPesqResultList.set(index, vTTCPesqResult);
    }

    /**
     * 
     * 
     * @param vTTCPesqResultArray
     */
    public void setTTCPesqResult(
            final org.amanzi.neo.loader.model.ams01.TTCPesqResult[] vTTCPesqResultArray) {
        //-- copy array
        _TTCPesqResultList.clear();
        
        for (int i = 0; i < vTTCPesqResultArray.length; i++) {
                this._TTCPesqResultList.add(vTTCPesqResultArray[i]);
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
