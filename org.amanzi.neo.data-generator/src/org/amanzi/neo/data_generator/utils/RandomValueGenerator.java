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

package org.amanzi.neo.data_generator.utils;

import java.util.Random;

/**
 * Class for generate different random values.  
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class RandomValueGenerator {
    
    /**
     * Instance of generator.
     */
    private static RandomValueGenerator generator = new RandomValueGenerator();
    
    /**
     * Returns instance.
     *
     * @return RandomValueGenerator.
     */
    public static RandomValueGenerator getGenerator(){
        return generator;
    }

    /**
     * Returns random value from interval.
     *
     * @param start Long
     * @param end Long
     * @return Long
     */
    public Long getLongValue(Long start, Long end){
        Number checkResult = checkParameters(start, end);
        if(checkResult!=null){
            return checkResult.longValue();
        }
        return new Double(start+(end-start)*getRandomValue()).longValue();
    }
    
    /**
     * Returns random value from interval.
     *
     * @param start Double
     * @param end Double
     * @return Double
     */
    public Double getDoubleValue(Double start, Double end){
        Number checkResult = checkParameters(start, end);
        if(checkResult!=null){
            return checkResult.doubleValue();
        }
        return start+(end-start)*getRandomValue();
    }
    
    /**
     * Returns random value from interval.
     *
     * @param start Double
     * @param end Double
     * @return Double
     */
    public Float getFloatValue(final Float start, final Float end)
    {
        Number checkResult = checkParameters(start, end);
        if(checkResult!=null){
            return checkResult.floatValue();
        }
        return start + (end - start) * ( (float) getRandomValue() );
    }
    
    /**
     * Returns random boolean value.
     *
     * @return boolean.
     */
    public boolean getBooleanValue(){
        return new Random().nextBoolean();
    }
    
    /**
     * Returns random value from interval.
     *
     * @param start Integer
     * @param end Integer
     * @return Integer
     */
    public Integer getIntegerValue(Integer start, Integer end){
        Number checkResult = checkParameters(start, end);
        if(checkResult!=null){
            return checkResult.intValue();
        }
        int count = end - start;
        double step = 1.0/count;
        double value = getRandomValue();
        for(int i = 1; i<count;i++){
            if(((i-1)*step<=value)&&(value<(i*step))){
                return start+i-1;
            }
        }
        return end;
    }
    
    /**
     * Check if interval borders is correct.
     *
     * @param start Number
     * @param end Number
     */
    private Number checkParameters(Number start, Number end) {
        if(start.equals(end)){
            return start;
        }
        if(end.doubleValue()<start.doubleValue()){
            throw new IllegalArgumentException("Incorrect parameters: end <"+end
                    +"> less or equals with start <"+start+">.");
        }
        return null;
    }
    
    /**
     * Returns ordinary random value.
     *
     * @return double.
     */
    private double getRandomValue(){
        return Math.random();
    }
}
