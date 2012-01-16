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

package org.amanzi.neo.services.model.impl;

/**
 * TODO Purpose of
 * <p>
 * Antenna values for saver
 * </p>
 * 
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class AntennaPattern {

    // angle must be from 0 to 360 degrees
    private double angle;

    // loss values must be positive
    private double loss;

    // type of radiation pattern
    private TypeRadiationPattern type;
    
    public AntennaPattern(double angle,double loss,TypeRadiationPattern type){
        this.angle = angle;
        this.loss = loss;
        this.type = type;
    }

    /*
     * getters and setters
     */
    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getLoss() {
        return loss;
    }

    public void setLoss(double loss) {
        this.loss = loss;
    }

    public TypeRadiationPattern getType() {
        return type;
    }

    public void setType(TypeRadiationPattern type) {
        this.type = type;
    }

    /*
     * radiation pattern type
     */
    public enum TypeRadiationPattern {
        HORISONTAL, VERTICAL
    }

}
