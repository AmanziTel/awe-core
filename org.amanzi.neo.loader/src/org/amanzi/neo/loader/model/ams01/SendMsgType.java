/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class SendMsgType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class SendMsgType implements java.io.Serializable {


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
     * Field _msgType.
     */
    private org.amanzi.neo.loader.model.ams01.types.MsgTypeType _msgType;

    /**
     * Field _dataLength.
     */
    private int _dataLength;

    /**
     * keeps track of state for field: _dataLength
     */
    private boolean _has_dataLength;

    /**
     * Field _dataTxt.
     */
    private byte[] _dataTxt;

    /**
     * Field _sendTime.
     */
    private java.lang.String _sendTime;

    /**
     * Field _msgRef.
     */
    private int _msgRef;

    /**
     * keeps track of state for field: _msgRef
     */
    private boolean _has_msgRef;

    /**
     * Field _errorCode.
     */
    private int _errorCode;

    /**
     * keeps track of state for field: _errorCode
     */
    private boolean _has_errorCode;

    /**
     * Field _sendReportList.
     */
    private java.util.Vector _sendReportList;

    /**
     * Field _sendMsgIsInclusive.
     */
    private org.amanzi.neo.loader.model.ams01.SendMsgIsInclusive _sendMsgIsInclusive;


      //----------------/
     //- Constructors -/
    //----------------/

    public SendMsgType() {
        super();
        this._sendReportList = new java.util.Vector();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vSendReport
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addSendReport(
            final org.amanzi.neo.loader.model.ams01.SendReport vSendReport)
    throws java.lang.IndexOutOfBoundsException {
        this._sendReportList.addElement(vSendReport);
    }

    /**
     * 
     * 
     * @param index
     * @param vSendReport
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addSendReport(
            final int index,
            final org.amanzi.neo.loader.model.ams01.SendReport vSendReport)
    throws java.lang.IndexOutOfBoundsException {
        this._sendReportList.add(index, vSendReport);
    }

    /**
     */
    public void deleteDataLength(
    ) {
        this._has_dataLength= false;
    }

    /**
     */
    public void deleteErrorCode(
    ) {
        this._has_errorCode= false;
    }

    /**
     */
    public void deleteMsgRef(
    ) {
        this._has_msgRef= false;
    }

    /**
     * Method enumerateSendReport.
     * 
     * @return an Enumeration over all
     * org.amanzi.neo.loader.model.ams01.SendReport elements
     */
    public java.util.Enumeration enumerateSendReport(
    ) {
        return this._sendReportList.elements();
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
     * Returns the value of field 'dataLength'.
     * 
     * @return the value of field 'DataLength'.
     */
    public int getDataLength(
    ) {
        return this._dataLength;
    }

    /**
     * Returns the value of field 'dataTxt'.
     * 
     * @return the value of field 'DataTxt'.
     */
    public byte[] getDataTxt(
    ) {
        return this._dataTxt;
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
     * Returns the value of field 'msgRef'.
     * 
     * @return the value of field 'MsgRef'.
     */
    public int getMsgRef(
    ) {
        return this._msgRef;
    }

    /**
     * Returns the value of field 'msgType'.
     * 
     * @return the value of field 'MsgType'.
     */
    public org.amanzi.neo.loader.model.ams01.types.MsgTypeType getMsgType(
    ) {
        return this._msgType;
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
     * Returns the value of field 'sendMsgIsInclusive'.
     * 
     * @return the value of field 'SendMsgIsInclusive'.
     */
    public org.amanzi.neo.loader.model.ams01.SendMsgIsInclusive getSendMsgIsInclusive(
    ) {
        return this._sendMsgIsInclusive;
    }

    /**
     * Method getSendReport.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * org.amanzi.neo.loader.model.ams01.SendReport at the given
     * index
     */
    public org.amanzi.neo.loader.model.ams01.SendReport getSendReport(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._sendReportList.size()) {
            throw new IndexOutOfBoundsException("getSendReport: Index value '" + index + "' not in range [0.." + (this._sendReportList.size() - 1) + "]");
        }
        
        return (org.amanzi.neo.loader.model.ams01.SendReport) _sendReportList.get(index);
    }

    /**
     * Method getSendReport.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public org.amanzi.neo.loader.model.ams01.SendReport[] getSendReport(
    ) {
        org.amanzi.neo.loader.model.ams01.SendReport[] array = new org.amanzi.neo.loader.model.ams01.SendReport[0];
        return (org.amanzi.neo.loader.model.ams01.SendReport[]) this._sendReportList.toArray(array);
    }

    /**
     * Method getSendReportCount.
     * 
     * @return the size of this collection
     */
    public int getSendReportCount(
    ) {
        return this._sendReportList.size();
    }

    /**
     * Returns the value of field 'sendTime'.
     * 
     * @return the value of field 'SendTime'.
     */
    public java.lang.String getSendTime(
    ) {
        return this._sendTime;
    }

    /**
     * Method hasDataLength.
     * 
     * @return true if at least one DataLength has been added
     */
    public boolean hasDataLength(
    ) {
        return this._has_dataLength;
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
     * Method hasMsgRef.
     * 
     * @return true if at least one MsgRef has been added
     */
    public boolean hasMsgRef(
    ) {
        return this._has_msgRef;
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
    public void removeAllSendReport(
    ) {
        this._sendReportList.clear();
    }

    /**
     * Method removeSendReport.
     * 
     * @param vSendReport
     * @return true if the object was removed from the collection.
     */
    public boolean removeSendReport(
            final org.amanzi.neo.loader.model.ams01.SendReport vSendReport) {
        boolean removed = _sendReportList.remove(vSendReport);
        return removed;
    }

    /**
     * Method removeSendReportAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public org.amanzi.neo.loader.model.ams01.SendReport removeSendReportAt(
            final int index) {
        java.lang.Object obj = this._sendReportList.remove(index);
        return (org.amanzi.neo.loader.model.ams01.SendReport) obj;
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
     * Sets the value of field 'dataLength'.
     * 
     * @param dataLength the value of field 'dataLength'.
     */
    public void setDataLength(
            final int dataLength) {
        this._dataLength = dataLength;
        this._has_dataLength = true;
    }

    /**
     * Sets the value of field 'dataTxt'.
     * 
     * @param dataTxt the value of field 'dataTxt'.
     */
    public void setDataTxt(
            final byte[] dataTxt) {
        this._dataTxt = dataTxt;
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
     * Sets the value of field 'msgRef'.
     * 
     * @param msgRef the value of field 'msgRef'.
     */
    public void setMsgRef(
            final int msgRef) {
        this._msgRef = msgRef;
        this._has_msgRef = true;
    }

    /**
     * Sets the value of field 'msgType'.
     * 
     * @param msgType the value of field 'msgType'.
     */
    public void setMsgType(
            final org.amanzi.neo.loader.model.ams01.types.MsgTypeType msgType) {
        this._msgType = msgType;
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
     * Sets the value of field 'sendMsgIsInclusive'.
     * 
     * @param sendMsgIsInclusive the value of field
     * 'sendMsgIsInclusive'.
     */
    public void setSendMsgIsInclusive(
            final org.amanzi.neo.loader.model.ams01.SendMsgIsInclusive sendMsgIsInclusive) {
        this._sendMsgIsInclusive = sendMsgIsInclusive;
    }

    /**
     * 
     * 
     * @param index
     * @param vSendReport
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setSendReport(
            final int index,
            final org.amanzi.neo.loader.model.ams01.SendReport vSendReport)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._sendReportList.size()) {
            throw new IndexOutOfBoundsException("setSendReport: Index value '" + index + "' not in range [0.." + (this._sendReportList.size() - 1) + "]");
        }
        
        this._sendReportList.set(index, vSendReport);
    }

    /**
     * 
     * 
     * @param vSendReportArray
     */
    public void setSendReport(
            final org.amanzi.neo.loader.model.ams01.SendReport[] vSendReportArray) {
        //-- copy array
        _sendReportList.clear();
        
        for (int i = 0; i < vSendReportArray.length; i++) {
                this._sendReportList.add(vSendReportArray[i]);
        }
    }

    /**
     * Sets the value of field 'sendTime'.
     * 
     * @param sendTime the value of field 'sendTime'.
     */
    public void setSendTime(
            final java.lang.String sendTime) {
        this._sendTime = sendTime;
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
