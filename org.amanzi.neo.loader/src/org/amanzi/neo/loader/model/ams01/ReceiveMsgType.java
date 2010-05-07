/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class ReceiveMsgType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class ReceiveMsgType implements java.io.Serializable {


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
     * Field _receiveMsgMsgType.
     */
    private org.amanzi.neo.loader.model.ams01.types.MsgTypeType _receiveMsgMsgType;

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
     * Field _receiveTime.
     */
    private java.lang.String _receiveTime;

    /**
     * Field _msgRef.
     */
    private int _msgRef;

    /**
     * keeps track of state for field: _msgRef
     */
    private boolean _has_msgRef;

    /**
     * Field _receiveMsgIsInclusive.
     */
    private org.amanzi.neo.loader.model.ams01.ReceiveMsgIsInclusive _receiveMsgIsInclusive;


      //----------------/
     //- Constructors -/
    //----------------/

    public ReceiveMsgType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteDataLength(
    ) {
        this._has_dataLength= false;
    }

    /**
     */
    public void deleteMsgRef(
    ) {
        this._has_msgRef= false;
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
     * Returns the value of field 'msgRef'.
     * 
     * @return the value of field 'MsgRef'.
     */
    public int getMsgRef(
    ) {
        return this._msgRef;
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
     * Returns the value of field 'receiveMsgIsInclusive'.
     * 
     * @return the value of field 'ReceiveMsgIsInclusive'.
     */
    public org.amanzi.neo.loader.model.ams01.ReceiveMsgIsInclusive getReceiveMsgIsInclusive(
    ) {
        return this._receiveMsgIsInclusive;
    }

    /**
     * Returns the value of field 'receiveMsgMsgType'.
     * 
     * @return the value of field 'ReceiveMsgMsgType'.
     */
    public org.amanzi.neo.loader.model.ams01.types.MsgTypeType getReceiveMsgMsgType(
    ) {
        return this._receiveMsgMsgType;
    }

    /**
     * Returns the value of field 'receiveTime'.
     * 
     * @return the value of field 'ReceiveTime'.
     */
    public java.lang.String getReceiveTime(
    ) {
        return this._receiveTime;
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
     * Sets the value of field 'callingNumber'.
     * 
     * @param callingNumber the value of field 'callingNumber'.
     */
    public void setCallingNumber(
            final java.lang.String callingNumber) {
        this._callingNumber = callingNumber;
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
     * Sets the value of field 'probeID'.
     * 
     * @param probeID the value of field 'probeID'.
     */
    public void setProbeID(
            final java.lang.String probeID) {
        this._probeID = probeID;
    }

    /**
     * Sets the value of field 'receiveMsgIsInclusive'.
     * 
     * @param receiveMsgIsInclusive the value of field
     * 'receiveMsgIsInclusive'.
     */
    public void setReceiveMsgIsInclusive(
            final org.amanzi.neo.loader.model.ams01.ReceiveMsgIsInclusive receiveMsgIsInclusive) {
        this._receiveMsgIsInclusive = receiveMsgIsInclusive;
    }

    /**
     * Sets the value of field 'receiveMsgMsgType'.
     * 
     * @param receiveMsgMsgType the value of field
     * 'receiveMsgMsgType'.
     */
    public void setReceiveMsgMsgType(
            final org.amanzi.neo.loader.model.ams01.types.MsgTypeType receiveMsgMsgType) {
        this._receiveMsgMsgType = receiveMsgMsgType;
    }

    /**
     * Sets the value of field 'receiveTime'.
     * 
     * @param receiveTime the value of field 'receiveTime'.
     */
    public void setReceiveTime(
            final java.lang.String receiveTime) {
        this._receiveTime = receiveTime;
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
