/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class TPCType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class TPCType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _probeID.
     */
    private java.lang.String _probeID;

    /**
     * Field _setupTime.
     */
    private java.lang.String _setupTime;

    /**
     * Field _connectTime.
     */
    private java.lang.String _connectTime;

    /**
     * Field _pdpRequest.
     */
    private java.lang.String _pdpRequest;

    /**
     * Field _pdpAccept.
     */
    private java.lang.String _pdpAccept;

    /**
     * Field _ftpConnReq.
     */
    private java.lang.String _ftpConnReq;

    /**
     * Field _ftpConnAccept.
     */
    private java.lang.String _ftpConnAccept;

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
     * Field _pdResultList.
     */
    private java.util.Vector _pdResultList;

    /**
     * Field _TPCIsInclusive.
     */
    private org.amanzi.neo.loader.model.ams01.TPCIsInclusive _TPCIsInclusive;


      //----------------/
     //- Constructors -/
    //----------------/

    public TPCType() {
        super();
        this._pdResultList = new java.util.Vector();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vPdResult
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPdResult(
            final org.amanzi.neo.loader.model.ams01.PdResult vPdResult)
    throws java.lang.IndexOutOfBoundsException {
        this._pdResultList.addElement(vPdResult);
    }

    /**
     * 
     * 
     * @param index
     * @param vPdResult
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPdResult(
            final int index,
            final org.amanzi.neo.loader.model.ams01.PdResult vPdResult)
    throws java.lang.IndexOutOfBoundsException {
        this._pdResultList.add(index, vPdResult);
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
     * Method enumeratePdResult.
     * 
     * @return an Enumeration over all
     * org.amanzi.neo.loader.model.ams01.PdResult elements
     */
    public java.util.Enumeration enumeratePdResult(
    ) {
        return this._pdResultList.elements();
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
     * Returns the value of field 'errorCode'.
     * 
     * @return the value of field 'ErrorCode'.
     */
    public int getErrorCode(
    ) {
        return this._errorCode;
    }

    /**
     * Returns the value of field 'ftpConnAccept'.
     * 
     * @return the value of field 'FtpConnAccept'.
     */
    public java.lang.String getFtpConnAccept(
    ) {
        return this._ftpConnAccept;
    }

    /**
     * Returns the value of field 'ftpConnReq'.
     * 
     * @return the value of field 'FtpConnReq'.
     */
    public java.lang.String getFtpConnReq(
    ) {
        return this._ftpConnReq;
    }

    /**
     * Method getPdResult.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * org.amanzi.neo.loader.model.ams01.PdResult at the given index
     */
    public org.amanzi.neo.loader.model.ams01.PdResult getPdResult(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._pdResultList.size()) {
            throw new IndexOutOfBoundsException("getPdResult: Index value '" + index + "' not in range [0.." + (this._pdResultList.size() - 1) + "]");
        }
        
        return (org.amanzi.neo.loader.model.ams01.PdResult) _pdResultList.get(index);
    }

    /**
     * Method getPdResult.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public org.amanzi.neo.loader.model.ams01.PdResult[] getPdResult(
    ) {
        org.amanzi.neo.loader.model.ams01.PdResult[] array = new org.amanzi.neo.loader.model.ams01.PdResult[0];
        return (org.amanzi.neo.loader.model.ams01.PdResult[]) this._pdResultList.toArray(array);
    }

    /**
     * Method getPdResultCount.
     * 
     * @return the size of this collection
     */
    public int getPdResultCount(
    ) {
        return this._pdResultList.size();
    }

    /**
     * Returns the value of field 'pdpAccept'.
     * 
     * @return the value of field 'PdpAccept'.
     */
    public java.lang.String getPdpAccept(
    ) {
        return this._pdpAccept;
    }

    /**
     * Returns the value of field 'pdpRequest'.
     * 
     * @return the value of field 'PdpRequest'.
     */
    public java.lang.String getPdpRequest(
    ) {
        return this._pdpRequest;
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
     * Returns the value of field 'TPCIsInclusive'.
     * 
     * @return the value of field 'TPCIsInclusive'.
     */
    public org.amanzi.neo.loader.model.ams01.TPCIsInclusive getTPCIsInclusive(
    ) {
        return this._TPCIsInclusive;
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
    public void removeAllPdResult(
    ) {
        this._pdResultList.clear();
    }

    /**
     * Method removePdResult.
     * 
     * @param vPdResult
     * @return true if the object was removed from the collection.
     */
    public boolean removePdResult(
            final org.amanzi.neo.loader.model.ams01.PdResult vPdResult) {
        boolean removed = _pdResultList.remove(vPdResult);
        return removed;
    }

    /**
     * Method removePdResultAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.amanzi.neo.loader.model.ams01.PdResult removePdResultAt(
            final int index) {
        java.lang.Object obj = this._pdResultList.remove(index);
        return (org.amanzi.neo.loader.model.ams01.PdResult) obj;
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
     * Sets the value of field 'ftpConnAccept'.
     * 
     * @param ftpConnAccept the value of field 'ftpConnAccept'.
     */
    public void setFtpConnAccept(
            final java.lang.String ftpConnAccept) {
        this._ftpConnAccept = ftpConnAccept;
    }

    /**
     * Sets the value of field 'ftpConnReq'.
     * 
     * @param ftpConnReq the value of field 'ftpConnReq'.
     */
    public void setFtpConnReq(
            final java.lang.String ftpConnReq) {
        this._ftpConnReq = ftpConnReq;
    }

    /**
     * 
     * 
     * @param index
     * @param vPdResult
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPdResult(
            final int index,
            final org.amanzi.neo.loader.model.ams01.PdResult vPdResult)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._pdResultList.size()) {
            throw new IndexOutOfBoundsException("setPdResult: Index value '" + index + "' not in range [0.." + (this._pdResultList.size() - 1) + "]");
        }
        
        this._pdResultList.set(index, vPdResult);
    }

    /**
     * 
     * 
     * @param vPdResultArray
     */
    public void setPdResult(
            final org.amanzi.neo.loader.model.ams01.PdResult[] vPdResultArray) {
        //-- copy array
        _pdResultList.clear();
        
        for (int i = 0; i < vPdResultArray.length; i++) {
                this._pdResultList.add(vPdResultArray[i]);
        }
    }

    /**
     * Sets the value of field 'pdpAccept'.
     * 
     * @param pdpAccept the value of field 'pdpAccept'.
     */
    public void setPdpAccept(
            final java.lang.String pdpAccept) {
        this._pdpAccept = pdpAccept;
    }

    /**
     * Sets the value of field 'pdpRequest'.
     * 
     * @param pdpRequest the value of field 'pdpRequest'.
     */
    public void setPdpRequest(
            final java.lang.String pdpRequest) {
        this._pdpRequest = pdpRequest;
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
     * Sets the value of field 'TPCIsInclusive'.
     * 
     * @param TPCIsInclusive the value of field 'TPCIsInclusive'.
     */
    public void setTPCIsInclusive(
            final org.amanzi.neo.loader.model.ams01.TPCIsInclusive TPCIsInclusive) {
        this._TPCIsInclusive = TPCIsInclusive;
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
