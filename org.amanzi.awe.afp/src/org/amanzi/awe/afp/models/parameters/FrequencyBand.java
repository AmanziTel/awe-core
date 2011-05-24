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

package org.amanzi.awe.afp.models.parameters;

import java.util.Arrays;
import java.util.Comparator;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public enum FrequencyBand implements IOptimizationParameterEnum {
    BAND_850("850", ".*850.*", 2) {

        private String[] supportedFrequencies = null;

        @Override
        public String[] getSupportedFrequencies() {
            if (supportedFrequencies == null) {
                supportedFrequencies = new String[251 - 128 + 1];
                for (int i = 0; i < supportedFrequencies.length; i++) {
                    supportedFrequencies[i] = Integer.toString(128 + i);
                }
            }

            return supportedFrequencies;
        }
    },
    BAND_900("900", ".*900.*", 0) {

        private String[] supportedFrequencies = null;

        @Override
        public String[] getSupportedFrequencies() {
            if (supportedFrequencies == null) {
                supportedFrequencies = new String[(124 - 0 + 1) + (1023 - 955 + 1)];
                for (int i = 0; i < supportedFrequencies.length; i++) {
                    if (i <= 124) {
                        supportedFrequencies[i] = Integer.toString(i);
                    } else {
                        supportedFrequencies[i] = Integer.toString(i + 955 - (124 + 1));
                    }
                }
            }

            return supportedFrequencies;
        }
    },
    BAND_1800("1800", ".*1800.*", 1) {

        private String[] supportedFrequencies = null;

        @Override
        public String[] getSupportedFrequencies() {
            if (supportedFrequencies == null) {
                supportedFrequencies = new String[885 - 512 + 1];
                for (int i = 0; i < supportedFrequencies.length; i++) {
                    supportedFrequencies[i] = Integer.toString(512 + i);
                }
            }

            return supportedFrequencies;
        }
    },
    BAND_1900("1900", ".*1900.*", 3) {
        
        private String[] supportedFrequencies = null;
        
        @Override
        public String[] getSupportedFrequencies() {
            if (supportedFrequencies == null) {
                supportedFrequencies = new String[810 - 512 + 1];
                for (int i = 0; i < supportedFrequencies.length; i++) {
                    supportedFrequencies[i] = Integer.toString(512 + i);
                }
            }
            
            return supportedFrequencies;
        }
    };

    private String text;

    private String regExp;

    private Integer index;

    private FrequencyBand(String text, String regExp, int index) {
        this.text = text;
        this.regExp = regExp;
        this.index = index;
    }

    @Override
    public String getText() {
        return text;
    }

    public String getRegExp() {
        return regExp;
    }

    public static FrequencyBand findByText(String textToSearch) {
        for (FrequencyBand band : FrequencyBand.values()) {
            if (band.getText().equals(textToSearch)) {
                return band;
            }
        }

        return null;
    }

    public static FrequencyBand[] valuesSorted() {
        FrequencyBand[] result = Arrays.copyOf(values(), values().length);
        Arrays.sort(result, new Comparator<FrequencyBand>() {

            @Override
            public int compare(FrequencyBand o1, FrequencyBand o2) {
                return o1.index.compareTo(o2.index);
            }
        });

        return result;
    }

    public abstract String[] getSupportedFrequencies();

}
