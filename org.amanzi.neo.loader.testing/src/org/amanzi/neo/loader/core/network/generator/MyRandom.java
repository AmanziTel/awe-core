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

package org.amanzi.neo.loader.core.network.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Class to generate different random data
 * 
 * @author Kasnitskij_V
 * @since 1.0.0
 */

public class MyRandom {
    private static Random random = new Random();
    
    MyRandom(){
        
    }
    
    public static String randomChannelType() {
        String result = null;
        long a = randomLong(0, 100);
        byte modulo = (byte)((byte)a % 3);
        
        switch (modulo) {
        case 0:
            result = "BCCH";
            break;
        case 1:
            result = "TCH";
            break;
        case 2:
            result = "All";
            break;
        }
        return result;
    }
    
    public static String randomExtended() {
        String result = null;
        int a = (int)randomLong(1, 4);
        
        switch (a) {
        case 1: 
            result = "P";
            break;
        case 2:
            result = "E";
            break;
        case 3: 
            result = "R";
            break;
        case 4: 
            result = "N/A";
            break;
        }
        
        return result;
    }
    
    public static String randomIntOrStar(int min, int max) {
        Integer n = max - min + 1;
        Integer i = random.nextInt() % n;
        if (i < 0)
            i = -i;
        
        if ((i > ((max - min) / 2 + min) - ((max - min) / 8)) && 
                (i <= ((max - min) / 2 + min) + ((max - min) / 8))) {
                    return "*";
                }
                else {
                    return (new Integer(min + i)).toString();
                }
    }
    
    public static Integer randomIntOrNULL(int min, int max) {
        long rand = randomLong(min, max);
        
        if (rand == min || rand == max) {
            return null;
        }
        else {
            return (int)rand;
        }
    }
    
    public static String randomCurrentIntOrStar(String currentInt) {
        long randomInt = randomLong(0, 1000);
        if (randomInt > 500 && randomInt < 600) {
            return "*";
        }
        else {
            return currentInt;
        }
    }
    
    public static long randomLong(long min, long max){
        long n = max - min + 1;
        long i = random.nextInt() % n;
        if (i < 0)
            i = -i;
        
        return min + i;
    }
    
    public static boolean randomBoolean(){
        long a = randomLong(0, 100);
        if (a % 2 == 0){
            return true;
        }
        else{
            return false;
        }
    }
    
    public static byte randomBooleanInteger() {
        long a = randomLong(0, 100);
        if (a % 2 == 0){
            return 0;
        }
        else{
            return 1;
        }
    }
    
    public static byte randomByte(){
        return (byte)randomLong(-128, 127);
    }
    
    public static char randomChar(){
        return (char)randomLong('a', 'z');
    }
    
    public static double randomDouble(long min, long max, int countAfterComa){
        long randLong = randomLong(min, max);
        Random random = new Random();
        double d = random.nextDouble();
        BigDecimal bd = new BigDecimal(d);
        return randLong + bd.setScale(countAfterComa, RoundingMode.HALF_UP).doubleValue();
    }
    
    @SuppressWarnings("deprecation")
    public static String randomString(long min, long max){
        long n = randomLong(min, max);
        byte b[] = new byte[(int) n];
        for (int i = 0; i < n; i++){
            b[i] = (byte)randomLong('a', 'z');
        }
        
        return new String(b, 0);
    }
}

