/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.2</a>, using an XML
 * Schema.
 * $Id$
 */

package org.amanzi.neo.loader.model.ams01;

/**
 * Class EventsType.
 * 
 * @version $Revision$ $Date$
 */
public abstract class EventsType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Internal choice value storage
     */
    private java.lang.Object _choiceValue;

    /**
     * Field _itsiAttach.
     */
    private org.amanzi.neo.loader.model.ams01.ItsiAttach _itsiAttach;

    /**
     * Field _cellResel.
     */
    private org.amanzi.neo.loader.model.ams01.CellResel _cellResel;

    /**
     * Field _handover.
     */
    private org.amanzi.neo.loader.model.ams01.Handover _handover;

    /**
     * Field _groupAttach.
     */
    private org.amanzi.neo.loader.model.ams01.GroupAttach _groupAttach;

    /**
     * Field _toc.
     */
    private org.amanzi.neo.loader.model.ams01.Toc _toc;

    /**
     * Field _ttc.
     */
    private org.amanzi.neo.loader.model.ams01.Ttc _ttc;

    /**
     * Field _tpc.
     */
    private org.amanzi.neo.loader.model.ams01.Tpc _tpc;

    /**
     * Field _sendMsg.
     */
    private org.amanzi.neo.loader.model.ams01.SendMsg _sendMsg;

    /**
     * Field _receiveMsg.
     */
    private org.amanzi.neo.loader.model.ams01.ReceiveMsg _receiveMsg;


      //----------------/
     //- Constructors -/
    //----------------/

    public EventsType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'cellResel'.
     * 
     * @return the value of field 'CellResel'.
     */
    public org.amanzi.neo.loader.model.ams01.CellResel getCellResel(
    ) {
        return this._cellResel;
    }

    /**
     * Returns the value of field 'choiceValue'. The field
     * 'choiceValue' has the following description: Internal choice
     * value storage
     * 
     * @return the value of field 'ChoiceValue'.
     */
    public java.lang.Object getChoiceValue(
    ) {
        return this._choiceValue;
    }

    /**
     * Returns the value of field 'groupAttach'.
     * 
     * @return the value of field 'GroupAttach'.
     */
    public org.amanzi.neo.loader.model.ams01.GroupAttach getGroupAttach(
    ) {
        return this._groupAttach;
    }

    /**
     * Returns the value of field 'handover'.
     * 
     * @return the value of field 'Handover'.
     */
    public org.amanzi.neo.loader.model.ams01.Handover getHandover(
    ) {
        return this._handover;
    }

    /**
     * Returns the value of field 'itsiAttach'.
     * 
     * @return the value of field 'ItsiAttach'.
     */
    public org.amanzi.neo.loader.model.ams01.ItsiAttach getItsiAttach(
    ) {
        return this._itsiAttach;
    }

    /**
     * Returns the value of field 'receiveMsg'.
     * 
     * @return the value of field 'ReceiveMsg'.
     */
    public org.amanzi.neo.loader.model.ams01.ReceiveMsg getReceiveMsg(
    ) {
        return this._receiveMsg;
    }

    /**
     * Returns the value of field 'sendMsg'.
     * 
     * @return the value of field 'SendMsg'.
     */
    public org.amanzi.neo.loader.model.ams01.SendMsg getSendMsg(
    ) {
        return this._sendMsg;
    }

    /**
     * Returns the value of field 'toc'.
     * 
     * @return the value of field 'Toc'.
     */
    public org.amanzi.neo.loader.model.ams01.Toc getToc(
    ) {
        return this._toc;
    }

    /**
     * Returns the value of field 'tpc'.
     * 
     * @return the value of field 'Tpc'.
     */
    public org.amanzi.neo.loader.model.ams01.Tpc getTpc(
    ) {
        return this._tpc;
    }

    /**
     * Returns the value of field 'ttc'.
     * 
     * @return the value of field 'Ttc'.
     */
    public org.amanzi.neo.loader.model.ams01.Ttc getTtc(
    ) {
        return this._ttc;
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
     * Sets the value of field 'cellResel'.
     * 
     * @param cellResel the value of field 'cellResel'.
     */
    public void setCellResel(
            final org.amanzi.neo.loader.model.ams01.CellResel cellResel) {
        this._cellResel = cellResel;
        this._choiceValue = cellResel;
    }

    /**
     * Sets the value of field 'groupAttach'.
     * 
     * @param groupAttach the value of field 'groupAttach'.
     */
    public void setGroupAttach(
            final org.amanzi.neo.loader.model.ams01.GroupAttach groupAttach) {
        this._groupAttach = groupAttach;
        this._choiceValue = groupAttach;
    }

    /**
     * Sets the value of field 'handover'.
     * 
     * @param handover the value of field 'handover'.
     */
    public void setHandover(
            final org.amanzi.neo.loader.model.ams01.Handover handover) {
        this._handover = handover;
        this._choiceValue = handover;
    }

    /**
     * Sets the value of field 'itsiAttach'.
     * 
     * @param itsiAttach the value of field 'itsiAttach'.
     */
    public void setItsiAttach(
            final org.amanzi.neo.loader.model.ams01.ItsiAttach itsiAttach) {
        this._itsiAttach = itsiAttach;
        this._choiceValue = itsiAttach;
    }

    /**
     * Sets the value of field 'receiveMsg'.
     * 
     * @param receiveMsg the value of field 'receiveMsg'.
     */
    public void setReceiveMsg(
            final org.amanzi.neo.loader.model.ams01.ReceiveMsg receiveMsg) {
        this._receiveMsg = receiveMsg;
        this._choiceValue = receiveMsg;
    }

    /**
     * Sets the value of field 'sendMsg'.
     * 
     * @param sendMsg the value of field 'sendMsg'.
     */
    public void setSendMsg(
            final org.amanzi.neo.loader.model.ams01.SendMsg sendMsg) {
        this._sendMsg = sendMsg;
        this._choiceValue = sendMsg;
    }

    /**
     * Sets the value of field 'toc'.
     * 
     * @param toc the value of field 'toc'.
     */
    public void setToc(
            final org.amanzi.neo.loader.model.ams01.Toc toc) {
        this._toc = toc;
        this._choiceValue = toc;
    }

    /**
     * Sets the value of field 'tpc'.
     * 
     * @param tpc the value of field 'tpc'.
     */
    public void setTpc(
            final org.amanzi.neo.loader.model.ams01.Tpc tpc) {
        this._tpc = tpc;
        this._choiceValue = tpc;
    }

    /**
     * Sets the value of field 'ttc'.
     * 
     * @param ttc the value of field 'ttc'.
     */
    public void setTtc(
            final org.amanzi.neo.loader.model.ams01.Ttc ttc) {
        this._ttc = ttc;
        this._choiceValue = ttc;
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
