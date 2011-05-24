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

package org.amanzi.awe.afp.testing.model;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.afp.wizards.good.FrequenciesListUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public class FrequenciesListUtilsTest {
    
    @Test
    public void checkCompress1() {
        ArrayList<String> frequencies = new ArrayList<String>();
        frequencies.add("1");
        frequencies.add("2");
        frequencies.add("3");
        frequencies.add("4");
        frequencies.add("7");
        frequencies.add("8");
        frequencies.add("9");
        frequencies.add("10");
        
        String result = FrequenciesListUtils.compressList(frequencies);
        
        Assert.assertEquals("Incorrect compression of Frequencies", "1-4,7-10", result);
    }
    
    @Test
    public void checkCompress2() {
        ArrayList<String> frequencies = new ArrayList<String>();
        frequencies.add("1");
        frequencies.add("3");
        frequencies.add("5");
        frequencies.add("7");
        
        String result = FrequenciesListUtils.compressList(frequencies);
        
        Assert.assertEquals("Incorrect compression of Frequencies", "1,3,5,7", result);
    }
    
    @Test
    public void checkCompress3() {
        ArrayList<String> frequencies = new ArrayList<String>();
        frequencies.add("1");
        frequencies.add("2");
        frequencies.add("3");
        frequencies.add("4");
        frequencies.add("7");
        frequencies.add("9");
        frequencies.add("10");
        frequencies.add("12");
        frequencies.add("13");
        
        String result = FrequenciesListUtils.compressList(frequencies);
        
        Assert.assertEquals("Incorrect compression of Frequencies", "1-4,7,9-10,12-13", result);
    }
    
    @Test
    public void checkCompressUnsortable() {
        ArrayList<String> frequencies = new ArrayList<String>();
        frequencies.add("7");
        frequencies.add("5");
        frequencies.add("3");
        frequencies.add("1");
        
        String result = FrequenciesListUtils.compressList(frequencies);
        
        Assert.assertEquals("Incorrect compression of Frequencies", "1,3,5,7", result);
    }
    
    @Test
    public void checkUncompress1() {
        String input = "1-4, 7-10";
        
        List<String> result = FrequenciesListUtils.decompressString(input);
        
        ArrayList<String> frequencies = new ArrayList<String>();
        frequencies.add("1");
        frequencies.add("2");
        frequencies.add("3");
        frequencies.add("4");
        frequencies.add("7");
        frequencies.add("8");
        frequencies.add("9");
        frequencies.add("10");
        
        Assert.assertTrue("Incorrect decompression of Frequencies", frequencies.equals(result));
    }
    
    
}
