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

package org.amanzi.awe.statistics.functions;

import org.amanzi.awe.statistics.engine.IAggregationFunction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public enum AggregationFunctions {
    MAX("max") {

        @Override
        public IAggregationFunction newFunction() {
            return new Max();
        }

    },
    MIN("min") {
        
        @Override
        public IAggregationFunction newFunction() {
            return new Min();
        }
        
    },
    SUM("sum") {
        
        @Override
        public IAggregationFunction newFunction() {
            return new Sum();
        }
        
    },
    COUNT("count"){

        @Override
        public IAggregationFunction newFunction() {
            return new Count();
        }

    },
    AVERAGE("average") {

        @Override
        public IAggregationFunction newFunction() {
            return new Average();
        }

    };
    private String fName;
    
    /**
     * @param name
     */
    private AggregationFunctions(String name) {
        fName = name;
    }

    public abstract IAggregationFunction newFunction();
    
    /**
     * @return Returns the fName.
     */
    public String getFunctionName() {
        return fName;
    }

    public static AggregationFunctions getFunctionByName(String name){
        for (AggregationFunctions func:values()){
            if (func.getFunctionName().equalsIgnoreCase(name)){
                return func;
            }
        }
        throw new EnumConstantNotPresentException(AggregationFunctions.class,name);
    }
}
