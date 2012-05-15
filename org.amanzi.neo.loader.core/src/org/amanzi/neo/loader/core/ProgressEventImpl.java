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

package org.amanzi.neo.loader.core;

/**
 * <p>
 * Implementation of persentage
 * </p>
 *
 * @author TsAr
 * @since 1.0.0
 */
public class ProgressEventImpl implements IProgressEvent {

    /** The name. */
    private String name;
    
    /** The percentage. */
    private double percentage;

    private boolean cancel;
    


    /**
     * Instantiates a new progress event.
     *
     * @param name the name
     * @param persentage the persentage
     */
    public ProgressEventImpl(String name, double persentage) {
        super();
        this.name = name;
        this.percentage = persentage;
        cancel=false;
    }

    /**
     * Gets the process name.
     *
     * @return the process name
     */
    @Override
    public String getProcessName() {
        return name;
    }

    /**
     * Gets the percentage.
     *
     * @return the percentage
     */
    @Override
    public double getPercentage() {
        return percentage;
    }

    @Override
    public void cancelProcess() {
        cancel=true;
    }

    @Override
    public boolean isCanseled() {
        return cancel;
    }

}
