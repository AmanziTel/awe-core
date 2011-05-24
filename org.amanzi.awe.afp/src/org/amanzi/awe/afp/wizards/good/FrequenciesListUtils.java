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

package org.amanzi.awe.afp.wizards.good;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class FrequenciesListUtils {

    private static final String FREQUENCY_DELIMITER = ",";

    private static final String COMPRESSOR_DELIMITER = "-";

    public static final Comparator<String> FREQUENCIES_COMPARATOR = new Comparator<String>() {

        @Override
        public int compare(String o1, String o2) {
            return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
        }
    };

    public static List<String> decompressString(String frequenciesString) throws NumberFormatException {
        ArrayList<String> result = new ArrayList<String>();

        for (String compressed : frequenciesString.split(FREQUENCY_DELIMITER)) {
            String frequency = compressed.trim();

            if (frequency.contains(COMPRESSOR_DELIMITER)) {
                String[] range = frequency.split(COMPRESSOR_DELIMITER);
                if ((range.length > 2) || (range.length < 1)) {
                    throw new IllegalArgumentException("Unexpected compressed Frequency <" + compressed + ">");
                }
                int min = Integer.parseInt(range[0]);
                int max = Integer.parseInt(range[1]);

                for (int i = min; i <= max; i++) {
                    result.add(Integer.toString(i));
                }
            } else {
                Integer.parseInt(frequency);// will throw NumberFormatException if it's not a number
                result.add(frequency);
            }
        }

        return result;
    }

    public static String compressList(List<String> frequenciesList) {
        Collections.sort(frequenciesList, FREQUENCIES_COMPARATOR);

        StringBuilder result = new StringBuilder();

        int prevFrequency = -2;
        Integer min = null;

        for (String singleFrequency : frequenciesList) {
            int frequency = Integer.parseInt(singleFrequency);

            if ((frequency - 1) != prevFrequency) {
                result = addRange(min, prevFrequency, result);
                min = frequency;
            }

            prevFrequency = frequency;
        }

        String output = addRange(min, prevFrequency, result).toString();

        if ((output.length() > 0) && (output.charAt(output.length() - 1) == ',')) {
            output = output.substring(0, output.length() - 1);
        }

        return output;
    }

    private static StringBuilder addRange(Integer min, int max, StringBuilder frequencyString) {
        if (min == null) {
            return frequencyString;
        }
        if (min < max) {
            return frequencyString.append(min).append(COMPRESSOR_DELIMITER).append(max).append(FREQUENCY_DELIMITER);
        } else {
            return frequencyString.append(min).append(FREQUENCY_DELIMITER);
        }
    }

}
