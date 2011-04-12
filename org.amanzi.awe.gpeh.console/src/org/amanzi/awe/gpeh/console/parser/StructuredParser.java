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

package org.amanzi.awe.gpeh.console.parser;

import java.util.List;

import org.amanzi.awe.gpeh.console.interfaces.IConfigurationData;
import org.amanzi.awe.gpeh.console.interfaces.IDataElement;
import org.amanzi.awe.gpeh.console.interfaces.IProgressEvent;
import org.amanzi.awe.gpeh.console.interfaces.ISaver;
import org.amanzi.awe.gpeh.console.interfaces.IStructuredElement;
import org.amanzi.awe.gpeh.console.interfaces.IStructuredSaver;
import org.amanzi.awe.gpeh.console.internal.ProgressEventImpl;

/**
 * <p>
 * Parser for structured data
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public abstract class StructuredParser<S extends IStructuredElement, T extends IDataElement, C extends IConfigurationData> extends AbstractParser<T, C> {
    protected double percentage;
    protected double percentageParser;
    
    protected long totalLen;
public void init(C properties, ISaver<T> saver) {
    super.init(properties, saver);
    percentageParser=formsParserPercent(properties);
    
};
    /**
 *
 * @param properties
 * @return
 */
protected double formsParserPercent(C properties) {
    return 1d;
}
    @SuppressWarnings({})
    @Override
    public void parce() {
        percentage = 0;
        List<S> elementList = getElementList();
        totalLen = getTotalLength(elementList);
        T initData = getInitData(getProperties());
        getSaver().init(initData);
        try {
            for (S element : elementList) {
                ProgressEventImpl event = new ProgressEventImpl(element.getDescription(), percentage);
                if (fireProgressEvent(event)) {
                    return;
                }
                if (getSaver() instanceof IStructuredSaver) {
                    if (((IStructuredSaver< T >)getSaver()).beforeSaveNewElement(getStartupElement(element))){
                        continue;
                    }
                    try{
                        if (parseElement(element)) {
                            return;
                        }
                    }finally{
                        ((IStructuredSaver< T >)getSaver()).finishSaveNewElement(getFinishElement(element));
                       
                    }
                } else {
                    if (parseElement(element)) {
                        return;
                    }
                }
                percentage += (double)element.getSize() / totalLen*percentageParser;
                ProgressEventImpl event2 = new ProgressEventImpl(element.getDescription(), percentage);
                if (fireProgressEvent(event2)) {
                    return;
                }
            }
        } finally {
            getSaver().finishUp(getFinishData());
        }
    }

    protected abstract T getStartupElement(S element);

    protected abstract T getFinishElement(S element);

    /**
     * Fire sub progress event.
     * 
     * @param element the element
     * @param event the event
     */
    protected boolean fireSubProgressEvent(S element, final IProgressEvent event) {
        return fireProgressEvent(new ProgressEventImpl(event.getProcessName(), percentage + (double)event.getPercentage() * element.getSize() / totalLen*percentageParser));
    }

    /**
     * Gets the percentage.
     * 
     * @return the percentage
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * Gets the total length.
     * 
     * @param elementList the element list
     * @return the total length
     */
    protected long getTotalLength(List<S> elementList) {
        long totalLen = 0;
        for (S element : elementList) {
            totalLen += element.getSize();
        }
        return totalLen;
    }

    /**
     * Gets the finish data.
     * 
     * @return the finish data
     */
    protected abstract T getFinishData();

    /**
     * Parses the element.
     * 
     * @param element the element
     */
    protected abstract boolean parseElement(S element);

    /**
     * Gets the inits the data.
     * 
     * @param properties the properties
     * @return the inits the data
     */
    protected abstract T getInitData(C properties);

    /**
     * Gets the element list.
     * 
     * @return the element list
     */
    protected abstract List<S> getElementList();
}
