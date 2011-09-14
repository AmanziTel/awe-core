/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.awe.cassidian.structure;

import org.amanzi.awe.cassidian.constants.ChildTypes;

/**
 * <p>
 * describe eventsElement tag
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class EventsElement implements IXmlTag {
    private AbstractTOCTTC tocttc;
    private GroupAttach groupAttach;
    private AbstactMsg sendRecieveMsg;
    private ItsiAttach itsiAttach;
    private Handover handover;
    private CellReselection cellReselection;

    public AbstractTOCTTC getTocttc() {
        return tocttc;
    }

    public void setTocttc(AbstractTOCTTC tocttc) {
        if (tocttc instanceof TTCElement) {
            this.tocttc = (TTCElement)tocttc;
        } else if (tocttc instanceof TOCElement) {
            this.tocttc = (TOCElement)tocttc;
        }
    }

    public GroupAttach getGroupAttach() {
        return groupAttach;
    }

    public void setGroupAttach(GroupAttach groupAttach) {
        this.groupAttach = groupAttach;
    }

    public Handover getHandover() {
        return handover;
    }

    public void setHandover(Handover handover) {
        this.handover = handover;
    }

    public ItsiAttach getItsiAttach() {
        return itsiAttach;
    }

    public void setItsiAttach(ItsiAttach itsiAttach) {
        this.itsiAttach = itsiAttach;
    }

    public String getType() {

        return ChildTypes.EVENTS.getId();
    }

    public EventsElement() {
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (value instanceof TOCElement && tagName.equals(ChildTypes.TOC.getId())) {
            setTocttc((TOCElement)value);
        } else if (value instanceof TTCElement && tagName.equals(ChildTypes.TTC.getId())) {
            setTocttc((TTCElement)value);
        } else if (value instanceof GroupAttach) {
            groupAttach = (GroupAttach)value;
        } else if (value instanceof SendMsg) {
            sendRecieveMsg = (SendMsg)value;
        } else if (value instanceof RecieveMsg) {
            sendRecieveMsg = (RecieveMsg)value;
        } else if (value instanceof ItsiAttach) {
            itsiAttach = (ItsiAttach)value;
        } else if (value instanceof Handover) {
            handover = (Handover)value;
        } else if (value instanceof CellReselection) {
            cellReselection = (CellReselection)value;
        }
        // TODO Auto-generated method stub

    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (tagName.equals(ChildTypes.TOC.getId()) && tocttc instanceof TOCElement) {
            return (TOCElement)tocttc;
        } else if (tagName.equals(ChildTypes.TTC.getId()) && tocttc instanceof TTCElement) {
            return (TTCElement)tocttc;
        } else if (tagName.equals(ChildTypes.GROUP_ATTACH.getId())) {
            return groupAttach;
        } else if (tagName.equals(ChildTypes.SEND_MSG.getId()) && getSendRecieveMsg() instanceof SendMsg) {
            return getSendRecieveMsg();
        } else if (tagName.equals(ChildTypes.RECIEVE_MSG.getId()) && getSendRecieveMsg() instanceof RecieveMsg) {
            return getSendRecieveMsg();
        } else if (tagName.equals(ChildTypes.ITSI_ATTACH.getId())) {
            return getItsiAttach();
        } else if (tagName.equals(ChildTypes.HANDOVER.getId())) {
            return getHandover();
        }
        return null;
    }

    public AbstactMsg getSendRecieveMsg() {
        return sendRecieveMsg;
    }

    public void setSendRecieveMsg(AbstactMsg sendRecieveMsg) {
        this.sendRecieveMsg = sendRecieveMsg;
    }

    public CellReselection getCellReselection() {
        return cellReselection;
    }

    public void setCellReselection(CellReselection cellReselection) {
        this.cellReselection = cellReselection;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cellReselection == null) ? 0 : cellReselection.hashCode());
        result = prime * result + ((groupAttach == null) ? 0 : groupAttach.hashCode());
        result = prime * result + ((handover == null) ? 0 : handover.hashCode());
        result = prime * result + ((itsiAttach == null) ? 0 : itsiAttach.hashCode());
        result = prime * result + ((sendRecieveMsg == null) ? 0 : sendRecieveMsg.hashCode());
        result = prime * result + ((tocttc == null) ? 0 : tocttc.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EventsElement)) {
            return false;
        }
        EventsElement other = (EventsElement)obj;
        if (cellReselection == null) {
            if (other.cellReselection != null) {
                return false;
            }
        } else if (!cellReselection.equals(other.cellReselection)) {
            return false;
        }
        if (groupAttach == null) {
            if (other.groupAttach != null) {
                return false;
            }
        } else if (!groupAttach.equals(other.groupAttach)) {
            return false;
        }
        if (handover == null) {
            if (other.handover != null) {
                return false;
            }
        } else if (!handover.equals(other.handover)) {
            return false;
        }
        if (itsiAttach == null) {
            if (other.itsiAttach != null) {
                return false;
            }
        } else if (!itsiAttach.equals(other.itsiAttach)) {
            return false;
        }
        if (sendRecieveMsg == null) {
            if (other.sendRecieveMsg != null) {
                return false;
            }
        } else if (!sendRecieveMsg.equals(other.sendRecieveMsg)) {
            return false;
        }
        if (tocttc == null) {
            if (other.tocttc != null) {
                return false;
            }
        } else if (!tocttc.equals(other.tocttc)) {
            return false;
        }
        return true;
    }

}
