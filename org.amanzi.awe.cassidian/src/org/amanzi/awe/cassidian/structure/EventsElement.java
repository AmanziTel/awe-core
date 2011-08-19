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

import java.util.LinkedList;
import java.util.List;

import org.amanzi.awe.cassidian.constants.ChildTypes;

public class EventsElement  implements IXmlTag  {
    private List<AbstractTOCTTC> toc = new LinkedList<AbstractTOCTTC>();
    private List<AbstractTOCTTC> ttc = new LinkedList<AbstractTOCTTC>();

    public String getType() {
        return ChildTypes.EVENTS.getId();
    }

    public EventsElement() {
    }

    public EventsElement(AbstractTOCTTC toc, AbstractTOCTTC ttc) {
        super();
        if (toc instanceof TOCElement) {
            this.toc.add(toc);
            this.ttc.add(ttc);
        } else {
            this.ttc.add(toc);
            this.toc.add(ttc);
        }

    }

    public EventsElement(AbstractTOCTTC element) {
        super();
        if (element instanceof TOCElement) {
            this.toc.add(element);

        } else {
            this.ttc.add(element);

        }

    }

    /**
     * @return the toc
     */
    public List<AbstractTOCTTC> getTOCList() {
        return toc;
    }

    /**
     * @param toc
     */
    public void setTOCList(List<AbstractTOCTTC> toc) {
        this.toc = toc;
    }

    /**
     * @return the ttc
     */
    public List<AbstractTOCTTC> getTTCList() {
        return ttc;
    }

    /**
     * @param ttc
     */
    public void setTTCList(List<AbstractTOCTTC> ttc) {

        this.ttc = ttc;

    }

    public void addATTCTOC(AbstractTOCTTC member) {
        if (member instanceof TTCElement) {
            ttc.add((TTCElement)member);
        } else {
            toc.add((TOCElement)member);
        }
    }

	@Override
	public void setValueByTagType(String tagName, Object value)
			 {
		if(value instanceof TOCElement){
			toc.add((TOCElement)value);
		}else if(value instanceof TTCElement){
			 ttc.add((TTCElement)value);
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getValueByTagType(String tagName) {
		if(tagName.equals(ChildTypes.TOC.getId())){
			return toc;
		}else{
			return ttc;
		}
		
	}

}
