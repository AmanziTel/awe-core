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

package org.amanzi.neo.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Nemo events ver. 2.1
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum NemoEvents {

    AG("#AG") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "AG";
                Float value = getFloatValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    BF("#BF") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "BTS_file";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    CInf("#CI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Converter name";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
                key = "Converter version";
                value = getStringValue(parameters, 1);
                parsedParameters.put(key, value);
                key = "Converter file";
                value = getStringValue(parameters, 2);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    CL("#CL") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "CL";
                Float value = getFloatValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    DL("#DL") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Device label";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    DN("#DN") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Device name";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    DS("#DS") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Number of supported systems";
                Integer value = getIntegerValue(parameters, 0);
                parsedParameters.put(key, value);
                key = "Supported systems";
                value = getIntegerValue(parameters, 1);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    DT("#DT") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Device type";
                Integer value = getIntegerValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    FF("#FF") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "File format version";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    EI("#EI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Device identity";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    HV("#HV") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Handler version";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    HW("#HW") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Hardware version";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    ID("#ID") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Measurement ID";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    MF("#MF") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Map file";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    ML("#ML") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Measurement label";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    NN("#NN") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Network name";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    PC("#PC") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Packet capture state";
                Integer value = getIntegerValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    PRODUCT("#PRODUCT") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Product name";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
                key = "Product version";
                value = getStringValue(parameters, 1);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    SI("#SI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Subscriber identity";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    SP("#SP") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Subscriber phone number";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    SW("#SW") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Device software version";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    TS("#TS") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Test script file";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    UT("#UT") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Gap to UTC";
                Integer value = getIntegerValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    VQ("#VQ") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "VQ type";
                Integer value = getIntegerValue(parameters, 0);
                parsedParameters.put(key, value);
                key = "VQ version";
                String valueStr = getStringValue(parameters, 1);
                parsedParameters.put(key, valueStr);
            }
            return parsedParameters;
        }
    },
    START("#START") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Date";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    STOP("#STOP") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Date";
                String value = getStringValue(parameters, 0);
                parsedParameters.put(key, value);
            }
            return parsedParameters;
        }
    },
    CAA("CAA") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Call context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Call type";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Direction";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "Number";
                parsedParameters.put(key, getStringValue(parameters, 4));

            } else if ("1.86".equals(version)) {
                String key = "Call type";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "#MOC";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Number";
                parsedParameters.put(key, getStringValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    CAI("CAI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "Call type";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "#MTC";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Number";
                parsedParameters.put(key, getStringValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    CAC("CAC") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Call context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Call type";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Call status";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "Parameters";
                parsedParameters.put(key, getStringValue(parameters, 4));
                // TODO parse parameters if necessary

            } else if ("1.86".equals(version)) {
                String key = "Call status";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Call att. time";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "TN";
                parsedParameters.put(key, getStringValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    CAF("CAF") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Call context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Call type";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "CS fail. status";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                // TODO parse parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "CS fail. status";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "CS fail. time";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "CS fail. cause";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    CAD("CAD") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Call context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Call type";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "CS disc. status";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                // TODO parse parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "CS disc. status";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "CS call dur.";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "CS disc. cause";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    VCHI("VCHI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters, 0));
            // TODO parse parameters if necessary
            return parsedParameters;
        }
    },
    DAA("DAA") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Data connection context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "Packet session context ID";
                parsedParameters.put(key, getStringValue(parameters, 1));
                key = "Call context ID";
                parsedParameters.put(key, getStringValue(parameters, 2));
                key = "Application protocol";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "Host address";
                parsedParameters.put(key, getStringValue(parameters, 4));
                key = "Host port";
                parsedParameters.put(key, getIntegerValue(parameters, 5));

            } else if ("1.86".equals(version)) {
                String key = "Data protocol";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Host address";
                parsedParameters.put(key, getStringValue(parameters, 1));
                key = "Host port";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    DAC("DAC") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Data connection context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "Application protocol";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
            } else if ("1.86".equals(version)) {
                String key = "Conn. time";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Conn. rate DL";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Conn. rate UL";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    DAF("DAF") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Data connection context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "Application protocol";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Data fail. status";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "Data fail. status";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Data fail. time";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Data fail. cause";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    DAD("DAD") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Data connection context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "Application protocol";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Data disc. status";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "Data disc. status";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Conn. duration";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Disc. cause";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    DREQ("DREQ") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Data transfer context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "Data connection context ID";
                parsedParameters.put(key, getStringValue(parameters, 1));
                key = "Application protocol";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Transf. dir.";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {

                String key = "Data protocol";
                final Integer dataProt = getIntegerValue(parameters, 0);
                parsedParameters.put(key, dataProt);
                key = "Transf. dir.";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                if (dataProt == 11) {
                    key = "File name";
                    parsedParameters.put(key, getStringValue(parameters, 2));
                } else {
                    key = "File size";
                    parsedParameters.put(key, getIntegerValue(parameters, 2));
                    if (dataProt < 3) {
                        key = "Packet size";
                        parsedParameters.put(key, getIntegerValue(parameters, 3));
                        key = "Rate limit";
                        parsedParameters.put(key, getIntegerValue(parameters, 4));
                        key = "Ping size";
                        parsedParameters.put(key, getIntegerValue(parameters, 5));
                        key = "Ping rate";
                        parsedParameters.put(key, getIntegerValue(parameters, 6));
                        key = "Ping timeout";
                        parsedParameters.put(key, getIntegerValue(parameters, 7));
                    } else {
                        key = "File name";
                        parsedParameters.put(key, getStringValue(parameters, 3));
                        if (dataProt == 3 || dataProt == 4) {
                            key = "Transf. att. #";
                            parsedParameters.put(key, getIntegerValue(parameters, 4));
                        }
                    }
                }
            }
            return parsedParameters;
        }
    },
    DCOMP("DCOMP") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Data transfer context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "Application protocol";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Transf. status";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "Data protocol";
                final Integer dataProt = getIntegerValue(parameters, 0);
                parsedParameters.put(key, dataProt);
                key = "Transf. status";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Fail. cause";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "IP service access time";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "IP termination time";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
            }
            return parsedParameters;
        }
    },
    DAS("DAS") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "TPut status";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "App. rate UL";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "App. rate DL";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Sent bytes";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "Receiv. bytes DL";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
            }
            return parsedParameters;
        }
    },
    DRATE("DRATE") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Data transfer context ID";
            parsedParameters.put(key, getStringValue(parameters, 0));
            key = "Application protocol";
            parsedParameters.put(key, getIntegerValue(parameters, 1));
            key = "App. rate UL";
            parsedParameters.put(key, getIntegerValue(parameters, 2));
            key = "App. rate DL";
            parsedParameters.put(key, getIntegerValue(parameters, 3));
            key = "Bytes UL";
            parsedParameters.put(key, getIntegerValue(parameters, 4));
            key = "Bytes DL";
            parsedParameters.put(key, getIntegerValue(parameters, 5));
            return parsedParameters;
        }
    },
    PER("PER") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Data transfer context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "Application protocol";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "PER UL";
                parsedParameters.put(key, getFloatValue(parameters, 2));
                key = "PER DL";
                parsedParameters.put(key, getFloatValue(parameters, 3));
            } else if ("1.86".equals(version)) {
                String key = "PER UL";
                parsedParameters.put(key, getFloatValue(parameters, 0));
                key = "PER DL";
                parsedParameters.put(key, getFloatValue(parameters, 1));
            }
            return parsedParameters;
        }
    },
    PREQ("PREQ") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "Ping protocol";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Ping host";
                parsedParameters.put(key, getStringValue(parameters, 1));
                key = "Ping size";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Ping rate";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "Ping timeout";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
            }
            return parsedParameters;
        }
    },
    PCOMP("PCOMP") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "Ping protocol";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Ping status";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Ping fail. cause";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    PING("PING") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "Ping size";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Ping RTT";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
            }
            return parsedParameters;
        }
    },
    RTT("RTT") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Data transfer context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "Application protocol";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    JITTER("JITTER") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Data transfer context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "Application protocol";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    DSS("DSS") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Application protocol";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "Data protocol";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Stream state";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Stream bandwith";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    RXL("RXL") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "System";
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put(key, system);
                if (system == 1 || system == 2 || system == 3 || system == 22) {
                    key = "ARFCN";
                    parsedParameters.put(key, getIntegerValue(parameters, 1));
                    key = "BSIC";
                    parsedParameters.put(key, getIntegerValue(parameters, 2));
                    key = "RxLev full";
                    parsedParameters.put(key, getIntegerValue(parameters, 3));
                    key = "RxLev sub";
                    parsedParameters.put(key, getIntegerValue(parameters, 4));
                    key = "C1";
                    parsedParameters.put(key, getIntegerValue(parameters, 5));
                    key = "C2";
                    parsedParameters.put(key, getIntegerValue(parameters, 6));
                    key = "nARFCN";
                    parsedParameters.put(key, getIntegerValue(parameters, 7));
                    key = "nBSIC";
                    parsedParameters.put(key, getIntegerValue(parameters, 8));
                    key = "nRxLev";
                    parsedParameters.put(key, getIntegerValue(parameters, 9));
                    key = "nC1";
                    parsedParameters.put(key, getIntegerValue(parameters, 10));
                    key = "nC2";
                    parsedParameters.put(key, getIntegerValue(parameters, 11));
                } else {
                    key = "Channel";
                    parsedParameters.put(key, getIntegerValue(parameters, 1));
                    key = "DCC";
                    parsedParameters.put(key, getIntegerValue(parameters, 2));
                    key = "RSSI";
                    parsedParameters.put(key, getIntegerValue(parameters, 3));
                    key = "nSystem";
                    parsedParameters.put(key, getIntegerValue(parameters, 4));
                    key = "nCh";
                    parsedParameters.put(key, getIntegerValue(parameters, 5));
                    key = "nDCC";
                    parsedParameters.put(key, getIntegerValue(parameters, 6));
                    key = "nRXL";
                    parsedParameters.put(key, getIntegerValue(parameters, 7));
                }

            }
            return parsedParameters;
        }
    },
    ERXL("ERXL") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "System";
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put(key, system);
                if (system == 1 || system == 2 || system == 3 || system == 22) {
                    key = "#Chs";
                    parsedParameters.put(key, getIntegerValue(parameters, 1));
                    key = "Params/ch";
                    parsedParameters.put(key, getIntegerValue(parameters, 2));
                    key = "ARFCN";
                    parsedParameters.put(key, getIntegerValue(parameters, 3));
                    key = "BSIC";
                    parsedParameters.put(key, getIntegerValue(parameters, 4));
                    key = "RxLev full";
                    parsedParameters.put(key, getIntegerValue(parameters, 5));
                    key = "RxLev sub";
                    parsedParameters.put(key, getIntegerValue(parameters, 6));
                    key = "C1";
                    parsedParameters.put(key, getIntegerValue(parameters, 7));
                    key = "C2";
                    parsedParameters.put(key, getIntegerValue(parameters, 8));
                    key = "C31";
                    parsedParameters.put(key, getIntegerValue(parameters, 9));
                    key = "C32";
                    parsedParameters.put(key, getIntegerValue(parameters, 10));
                    key = "HCS priority";
                    parsedParameters.put(key, getIntegerValue(parameters, 11));
                    key = "HCS thr.";
                    parsedParameters.put(key, getIntegerValue(parameters, 12));
                    key = "CI";
                    parsedParameters.put(key, getIntegerValue(parameters, 13));
                    key = "LAC";
                    parsedParameters.put(key, getIntegerValue(parameters, 14));
                    key = "RAC";
                    parsedParameters.put(key, getIntegerValue(parameters, 15));
                    key = "Srxlev";
                    parsedParameters.put(key, getIntegerValue(parameters, 16));
                    key = "Hrxlev";
                    parsedParameters.put(key, getIntegerValue(parameters, 17));
                    key = "Rrxlev";
                    parsedParameters.put(key, getIntegerValue(parameters, 18));
                } else if (system == 11) {
                    key = "#Chs";
                    parsedParameters.put(key, getIntegerValue(parameters, 1));
                    key = "Params/ch";
                    parsedParameters.put(key, getIntegerValue(parameters, 2));
                    key = "ARFCN";
                    parsedParameters.put(key, getIntegerValue(parameters, 3));
                    key = "BAND";
                    parsedParameters.put(key, getIntegerValue(parameters, 4));
                    key = "LAC";
                    parsedParameters.put(key, getIntegerValue(parameters, 5));
                    key = "RSSI";
                    parsedParameters.put(key, getIntegerValue(parameters, 6));
                    key = "C1";
                    parsedParameters.put(key, getIntegerValue(parameters, 7));
                    key = "C2";
                    parsedParameters.put(key, getIntegerValue(parameters, 8));
                    key = "CC";
                } else if (system == 32 || system == 35) {
                    key = "#Chs";
                    parsedParameters.put(key, getIntegerValue(parameters, 1));
                    key = "Params/ch";
                    parsedParameters.put(key, getIntegerValue(parameters, 2));
                    key = "Quality";
                    parsedParameters.put(key, getFloatValue(parameters, 3));
                    key = "Channel number";
                    parsedParameters.put(key, getIntegerValue(parameters, 4));
                    key = "WLAN RSSI";
                    parsedParameters.put(key, getFloatValue(parameters, 5));
                    key = "WLAN SSID";
                    parsedParameters.put(key, getStringValue(parameters, 6));
                    key = "WLAN MAC addr.";
                    parsedParameters.put(key, getStringValue(parameters, 7));
                }

            }
            return parsedParameters;
        }
    },
    ECI0("ECI0") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "System";
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put(key, system);
                if (system == 7 || system == 8 || system == 14 || system == 15 || system == 26 || system == 30) {
                    key = "#Chs";
                    parsedParameters.put(key, getIntegerValue(parameters, 1));
                    key = "Carrier";
                    parsedParameters.put(key, getIntegerValue(parameters, 2));
                    key = "Rx power";
                    parsedParameters.put(key, getIntegerValue(parameters, 3));
                    key = "Params/pilot";
                    parsedParameters.put(key, getIntegerValue(parameters, 4));
                    key = "Act. set size";
                    parsedParameters.put(key, getIntegerValue(parameters, 5));
                    key = "PN";
                    parsedParameters.put(key, getIntegerValue(parameters, 6));
                    key = "Set";
                    parsedParameters.put(key, getIntegerValue(parameters, 7));
                    key = "Ec/I0";
                    parsedParameters.put(key, getFloatValue(parameters, 8));
                    key = "Walsh";
                    parsedParameters.put(key, getIntegerValue(parameters, 9));
                    key = "Cand. set size";
                    parsedParameters.put(key, getIntegerValue(parameters, 10));
                    key = "PN_2";
                    parsedParameters.put(key, getIntegerValue(parameters, 11));
                    key = "Ec/I0_2";
                    parsedParameters.put(key, getFloatValue(parameters, 12));
                    key = "Walsh_2";
                    parsedParameters.put(key, getIntegerValue(parameters, 13));
                    key = "Neigh. set size";
                    parsedParameters.put(key, getIntegerValue(parameters, 14));
                    key = "PN_3";
                    parsedParameters.put(key, getIntegerValue(parameters, 15));
                    key = "Ec/I0_3";
                    parsedParameters.put(key, getFloatValue(parameters, 16));
                    key = "Walsh_3";
                    parsedParameters.put(key, getIntegerValue(parameters, 17));
                    key = "Rem. set size";
                    parsedParameters.put(key, getIntegerValue(parameters, 18));
                    key = "PN_4";
                    parsedParameters.put(key, getIntegerValue(parameters, 19));
                    key = "Ec/I0_4";
                    parsedParameters.put(key, getFloatValue(parameters, 20));
                    key = "Walsh_4";
                    parsedParameters.put(key, getIntegerValue(parameters, 21));
                } else if (system == 27 || system == 28 || system == 29) {
                    key = "#Chs";
                    parsedParameters.put(key, getIntegerValue(parameters, 1));
                    key = "Packet carrier";
                    parsedParameters.put(key, getIntegerValue(parameters, 2));
                    key = "Rx power";
                    parsedParameters.put(key, getIntegerValue(parameters, 3));
                    key = "Params/pilot";
                    parsedParameters.put(key, getIntegerValue(parameters, 4));
                    key = "Act. set size";
                    parsedParameters.put(key, getIntegerValue(parameters, 5));
                    key = "PN";
                    parsedParameters.put(key, getIntegerValue(parameters, 6));
                    key = "Set";
                    parsedParameters.put(key, getIntegerValue(parameters, 7));
                    key = "Ec/I0";
                    parsedParameters.put(key, getFloatValue(parameters, 8));
                    key = "Walsh";
                    parsedParameters.put(key, getIntegerValue(parameters, 9));
                    key = "Cand. set size";
                    parsedParameters.put(key, getIntegerValue(parameters, 10));
                    key = "PN_2";
                    parsedParameters.put(key, getIntegerValue(parameters, 11));
                    key = "Ec/I0_2";
                    parsedParameters.put(key, getFloatValue(parameters, 12));
                    key = "Walsh_2";
                    parsedParameters.put(key, getIntegerValue(parameters, 13));
                    key = "Neigh. set size";
                    parsedParameters.put(key, getIntegerValue(parameters, 14));
                    key = "PN_3";
                    parsedParameters.put(key, getIntegerValue(parameters, 15));
                    key = "Ec/I0_3";
                    parsedParameters.put(key, getFloatValue(parameters, 16));
                    key = "Walsh_3";
                    parsedParameters.put(key, getIntegerValue(parameters, 17));
                    key = "Rem. set size";
                    parsedParameters.put(key, getIntegerValue(parameters, 18));
                    key = "PN_4";
                    parsedParameters.put(key, getIntegerValue(parameters, 19));
                    key = "Ec/I0_4";
                    parsedParameters.put(key, getFloatValue(parameters, 20));
                    key = "Walsh_4";
                    parsedParameters.put(key, getIntegerValue(parameters, 21));
                }
            }
            return parsedParameters;
        }
    },
    DCONTENT("DCONTENT") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Application protocol";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    CELLMEAS("CELLMEAS") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    ADJMEAS("ADJMEAS") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    RXQ("RXQ") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                // TODO how check GSM/DAMPS????
                String key = "RxQual full/BER class";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "RxQual sub/Reserved";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
            }
            return parsedParameters;
        }
    },
    PRXQ("PRXQ") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                // TODO how check GSM/DAMPS????
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "RxQual";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "C value";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "SIGN_VAR";
                parsedParameters.put(key, getFloatValue(parameters, 3));
                key = "#TSL results";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
                key = "TSL interf.";
                parsedParameters.put(key, getFloatValue(parameters, 5));
            }
            return parsedParameters;
        }
    },
    FER("FER") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "FER full";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "FER sub";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "FER TCH";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    EFER("EFER") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "System";
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put(key, system);
                key = "FER (dec)";
                parsedParameters.put(key, getFloatValue(parameters, 1));
                if (system == 7 || system == 8 || system == 14 || system == 15 || system == 26 || system == 30) {
                    key = "FER F-FCH";
                    parsedParameters.put(key, getFloatValue(parameters, 2));
                    key = "FER F-SCH";
                    parsedParameters.put(key, getFloatValue(parameters, 3));
                    key = "FER target F-FCH";
                    parsedParameters.put(key, getFloatValue(parameters, 4));
                    key = "FER target F-SCH";
                    parsedParameters.put(key, getFloatValue(parameters, 5));
                }

            }
            return parsedParameters;
        }
    },
    MSP("MSP") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "MSP";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
            }
            return parsedParameters;
        }
    },
    RLT("RLT") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "RLT";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
            }
            return parsedParameters;
        }
    },
    TAD("TAD") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "TA";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
            }
            return parsedParameters;
        }
    },
    TAL("TAL") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "TAL";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
            }
            return parsedParameters;
        }
    },
    DSC("DSC") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "DSC current";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "DSC max";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
            }
            return parsedParameters;
        }
    },
    BEP("BEP") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "GMSK MEAN_BEP %";
                parsedParameters.put(key, getFloatValue(parameters, 1));
                key = "GMSK CV_BEP %";
                parsedParameters.put(key, getFloatValue(parameters, 2));
                key = "8-PSK MEAN_BEP %";
                parsedParameters.put(key, getFloatValue(parameters, 3));
                key = "8-PSK CV_BEP %";
                parsedParameters.put(key, getFloatValue(parameters, 4));
                key = "GMSK MEAN_BEP";
                parsedParameters.put(key, getIntegerValue(parameters, 5));
                key = "GMSK CV_BEP";
                parsedParameters.put(key, getIntegerValue(parameters, 6));
                key = "8-PSK MEAN_BEP";
                parsedParameters.put(key, getIntegerValue(parameters, 7));
                key = "8-PSK CV_BEP";
                parsedParameters.put(key, getIntegerValue(parameters, 8));
            }
            return parsedParameters;
        }
    },
    CI("CI") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Ch C/I";
                parsedParameters.put(key, getFloatValue(parameters, 1));
                key = "#TSL C/I values";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "TSL C/I";
                parsedParameters.put(key, getFloatValue(parameters, 3));
            }
            return parsedParameters;
        }
    },
    TXPC("TXPC") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "System";
                Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put(key, system);
                if (system == 12 || system == 13 || system == 20 || system == 21 || system == 31 || system == 33 || system == 34) {
                    key = "TX power";// maybe this type Float?
                    parsedParameters.put(key, getIntegerValue(parameters, 1));
                    key = "Pwr ctrl alg.";
                    parsedParameters.put(key, getIntegerValue(parameters, 2));
                    key = "Pwr ctrl step";
                    parsedParameters.put(key, getIntegerValue(parameters, 3));
                    key = "Compr. mode";
                    parsedParameters.put(key, getIntegerValue(parameters, 4));
                    key = "#UL pwr up";
                    parsedParameters.put(key, getIntegerValue(parameters, 5));
                    key = "#UL pwr down";
                    parsedParameters.put(key, getIntegerValue(parameters, 6));
                    key = "UL pwr up %";
                    parsedParameters.put(key, getFloatValue(parameters, 7));
                } else if (system == 7 || system == 8 || system == 14 || system == 15 || system == 26 || system == 27
                        || system == 28 || system == 29 || system == 30) {
                    key = "TX power_f";// because the type of this property is different from TX
                    // power
                    parsedParameters.put(key, getFloatValue(parameters, 1));
                    key = "Pwr ctrl step";
                    parsedParameters.put(key, getIntegerValue(parameters, 2));
                    key = "Pwr ctrl step";
                    parsedParameters.put(key, getIntegerValue(parameters, 3));
                    key = "#UL pwr up";
                    parsedParameters.put(key, getIntegerValue(parameters, 4));
                    key = "#UL pwr down";
                    parsedParameters.put(key, getIntegerValue(parameters, 5));
                    key = "UL pwr up %";
                    parsedParameters.put(key, getFloatValue(parameters, 6));
                    key = "TX adjust";
                    parsedParameters.put(key, getFloatValue(parameters, 7));
                    key = "TX pwr limit";
                    parsedParameters.put(key, getFloatValue(parameters, 8));
                } else if (system == 11) {
                    key = "TX power_f";// because the type of this property is different from TX
                    // power
                    parsedParameters.put(key, getFloatValue(parameters, 1));
                    key = "Pwr ctrl alg.";
                    parsedParameters.put(key, getIntegerValue(parameters, 2));
                    key = "TX pwr. change";
                    parsedParameters.put(key, getFloatValue(parameters, 3));
                }
            }
            return parsedParameters;
        }
    },
    RXPC("RXPC") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "System";
                Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put(key, system);
                if (system == 12 || system == 13 || system == 20 || system == 21 || system == 31 || system == 33 || system == 34) {
                    // reserved 2 fields
                    key = "SIR target";
                    parsedParameters.put(key, getFloatValue(parameters, 3));
                    key = "SIR";
                    parsedParameters.put(key, getFloatValue(parameters, 4));
                    key = "BS div. state";
                    parsedParameters.put(key, getIntegerValue(parameters, 5));
                    key = "#DL pwr up";
                    parsedParameters.put(key, getIntegerValue(parameters, 6));
                    key = "#DL pwr down";
                    parsedParameters.put(key, getIntegerValue(parameters, 7));
                    key = "DL pwr up %";
                    parsedParameters.put(key, getFloatValue(parameters, 8));
                    key = "DPC mode";
                    parsedParameters.put(key, getIntegerValue(parameters, 9));

                } else if (system == 7 || system == 8 || system == 14 || system == 15 || system == 26 || system == 30) {
                    key = "FPC mode";
                    parsedParameters.put(key, getIntegerValue(parameters, 1));
                    key = "FPC subch. ind.";
                    parsedParameters.put(key, getIntegerValue(parameters, 2));
                    key = "FPC subch. gain.";
                    parsedParameters.put(key, getFloatValue(parameters, 3));
                    key = "#DL pwr up";
                    parsedParameters.put(key, getIntegerValue(parameters, 4));
                    key = "#DL pwr down";
                    parsedParameters.put(key, getIntegerValue(parameters, 5));
                    key = "DL pwr up %";
                    parsedParameters.put(key, getFloatValue(parameters, 6));
                } else if (system == 27 || system == 28 || system == 29) {
                    key = "#Header params";// because the type of this property is different from TX
                    // power
                    parsedParameters.put(key, getIntegerValue(parameters, 1));
                    key = "DRC index";
                    parsedParameters.put(key, getIntegerValue(parameters, 2));
                    key = "#Act set PNs";
                    parsedParameters.put(key, getIntegerValue(parameters, 3));
                    key = "Params/pilot";
                    parsedParameters.put(key, getIntegerValue(parameters, 4));
                    key = "Pn";
                    parsedParameters.put(key, getIntegerValue(parameters, 5));
                    key = "SINR";
                    parsedParameters.put(key, getFloatValue(parameters, 6));
                }
            }
            return parsedParameters;
        }
    },

    BER("BER") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "System";
                Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put(key, system);
                if (system == 12 || system == 13 || system == 20 || system == 21 || system == 31 || system == 33 || system == 34) {
                    key = "Pilot BER";
                    parsedParameters.put(key, getFloatValue(parameters, 1));
                    key = "TFCI BER";
                    parsedParameters.put(key, getFloatValue(parameters, 2));
                } else if (system == 11) {
                    key = "BER";
                    parsedParameters.put(key, getFloatValue(parameters, 1));
                }
            }
            return parsedParameters;
        }
    },
    ECNO("ECNO") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "System";
                Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put(key, system);
                key = "#Chs";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Channel";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Carrier RSSI";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "#params/cell";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
                key = "#Active cells";
                parsedParameters.put(key, getIntegerValue(parameters, 5));
                key = "Channel_1";
                parsedParameters.put(key, getIntegerValue(parameters, 6));
                key = "Scrambling code";
                parsedParameters.put(key, getIntegerValue(parameters, 7));
                key = "Ec/No";
                parsedParameters.put(key, getIntegerValue(parameters, 8));
                key = "STTD";
                parsedParameters.put(key, getIntegerValue(parameters, 9));
                key = "RSSP";
                parsedParameters.put(key, getIntegerValue(parameters, 10));
                key = "Secondary scr.";
                parsedParameters.put(key, getIntegerValue(parameters, 11));
                key = "Squal";
                parsedParameters.put(key, getIntegerValue(parameters, 12));
                key = "Srxlev";
                parsedParameters.put(key, getIntegerValue(parameters, 13));
                key = "Hqual";
                parsedParameters.put(key, getIntegerValue(parameters, 14));
                key = "Hrxlev";
                parsedParameters.put(key, getIntegerValue(parameters, 15));
                key = "Rqual";
                parsedParameters.put(key, getIntegerValue(parameters, 16));
                key = "Rrxlev";
                parsedParameters.put(key, getIntegerValue(parameters, 17));
                key = "OFF";
                parsedParameters.put(key, getIntegerValue(parameters, 18));
                key = "TM";
                parsedParameters.put(key, getFloatValue(parameters, 19));
                key = "#Monit. cells";
                parsedParameters.put(key, getIntegerValue(parameters, 20));
                key = "Channel_2";
                parsedParameters.put(key, getIntegerValue(parameters, 21));
                key = "Scrambling code_2";
                parsedParameters.put(key, getIntegerValue(parameters, 22));
                key = "Ec/No_2";
                parsedParameters.put(key, getIntegerValue(parameters, 23));
                key = "STTD_2";
                parsedParameters.put(key, getIntegerValue(parameters, 24));
                key = "RSSP_2";
                parsedParameters.put(key, getIntegerValue(parameters, 25));
                key = "Secondary scr._2";
                parsedParameters.put(key, getIntegerValue(parameters, 26));
                key = "Squal_2";
                parsedParameters.put(key, getIntegerValue(parameters, 27));
                key = "Srxlev_2";
                parsedParameters.put(key, getIntegerValue(parameters, 28));
                key = "Hqual_2";
                parsedParameters.put(key, getIntegerValue(parameters, 29));
                key = "Hrxlev_2";
                parsedParameters.put(key, getIntegerValue(parameters, 30));
                key = "Rqual_2";
                parsedParameters.put(key, getIntegerValue(parameters, 31));
                key = "Rrxlev_2";
                parsedParameters.put(key, getIntegerValue(parameters, 32));
                key = "OFF_2";
                parsedParameters.put(key, getIntegerValue(parameters, 33));
                key = "TM_2";
                parsedParameters.put(key, getFloatValue(parameters, 34));
                key = "#Detect. cells";
                parsedParameters.put(key, getIntegerValue(parameters, 35));
                key = "Channel_3";
                parsedParameters.put(key, getIntegerValue(parameters, 36));
                key = "Scrambling code_3";
                parsedParameters.put(key, getIntegerValue(parameters, 37));
                key = "Ec/No_3";
                parsedParameters.put(key, getIntegerValue(parameters, 38));
                key = "STTD_3";
                parsedParameters.put(key, getIntegerValue(parameters, 39));
                key = "RSSP_3";
                parsedParameters.put(key, getIntegerValue(parameters, 40));
                key = "Secondary scr._3";
                parsedParameters.put(key, getIntegerValue(parameters, 41));
                key = "Squal_3";
                parsedParameters.put(key, getIntegerValue(parameters, 42));
                key = "Srxlev_3";
                parsedParameters.put(key, getIntegerValue(parameters, 43));
                key = "Hqual_3";
                parsedParameters.put(key, getIntegerValue(parameters, 44));
                key = "Hrxlev_3";
                parsedParameters.put(key, getIntegerValue(parameters, 45));
                key = "Rqual_3";
                parsedParameters.put(key, getIntegerValue(parameters, 46));
                key = "Rrxlev_3";
                parsedParameters.put(key, getIntegerValue(parameters, 47));
                key = "OFF_3";
                parsedParameters.put(key, getIntegerValue(parameters, 48));
                key = "TM_3";
                parsedParameters.put(key, getFloatValue(parameters, 49));
                key = "#Undet. cells";
                parsedParameters.put(key, getIntegerValue(parameters, 50));
                key = "Channel_4";
                parsedParameters.put(key, getIntegerValue(parameters, 51));
                key = "Scrambling code_4";
                parsedParameters.put(key, getIntegerValue(parameters, 52));
                key = "Ec/No_4";
                parsedParameters.put(key, getIntegerValue(parameters, 53));
                key = "STTD_4";
                parsedParameters.put(key, getIntegerValue(parameters, 54));
                key = "RSSP_4";
                parsedParameters.put(key, getIntegerValue(parameters, 55));
                key = "Secondary scr._4";
                parsedParameters.put(key, getIntegerValue(parameters, 56));
                key = "Squal_4";
                parsedParameters.put(key, getIntegerValue(parameters, 57));
                key = "Srxlev_4";
                parsedParameters.put(key, getIntegerValue(parameters, 58));
                key = "Hqual_4";
                parsedParameters.put(key, getIntegerValue(parameters, 59));
                key = "Hrxlev_4";
                parsedParameters.put(key, getIntegerValue(parameters, 60));
                key = "Rqual_4";
                parsedParameters.put(key, getIntegerValue(parameters, 61));
                key = "Rrxlev_4";
                parsedParameters.put(key, getIntegerValue(parameters, 62));
                key = "OFF_4";
                parsedParameters.put(key, getIntegerValue(parameters, 63));
                key = "TM_4";
                parsedParameters.put(key, getFloatValue(parameters, 64));

            }
            return parsedParameters;
        }
    },
    PHDAS("PHDAS") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "System";
                Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put(key, system);
                key = "DPDCH rate UL";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
            }
            return parsedParameters;
        }
    },
    PPPDAS("PPPDAS") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "PPP rate UL";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "PPP rate DL";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Sent PPP bytes";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Recv. PPP bytes";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
            }
            return parsedParameters;
        }
    },
    WLANDAS("WLANDAS") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "System";
                Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put(key, system);
                if (system == 32) {
                    key = "WLAN rate UL";
                    parsedParameters.put(key, getIntegerValue(parameters, 1));
                    key = "WLAN rate DL";
                    parsedParameters.put(key, getIntegerValue(parameters, 2));
                }
            }
            return parsedParameters;
        }
    },
    RLPDAS("RLPDAS") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "System";
                Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put(key, system);
                key = "RLP R-rate";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "RLP F-rate";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "RLP R-retr";
                parsedParameters.put(key, getFloatValue(parameters, 1));
                key = "RLP F-retr";
                parsedParameters.put(key, getFloatValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    PHRATE("PHRATE") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    WLANRATE("WLANRATE") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "WLAN rate UL";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "WLAN rate DL";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    PPPRATE("PPPRATE") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "PPP rate UL";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "PPP rate DL";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Sent PPP bytes";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Recv. PPP bytes";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
            }
            return parsedParameters;
        }
    },
    RLPRATE("RLPRATE") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    RLPSTATISTICS("RLPSTATISTICS") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    MEI("MEI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Event_m";// because "event" property maybe exist
                parsedParameters.put(key, getIntegerValue(parameters, 1));
            }
            return parsedParameters;
        }
    },
    CQI("CQI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "#CQI header params";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Sample dur.";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Ph. req. TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "CQI repetitions";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
                key = "#CQI cycle";
                parsedParameters.put(key, getIntegerValue(parameters, 5));
                key = "#CQI values";
                final Integer cycle = getIntegerValue(parameters, 6);
                parsedParameters.put(key, cycle);
                key = "#Params/CQI";
                final Integer cycleLen = getIntegerValue(parameters, 7);
                parsedParameters.put(key, cycleLen);
                if (cycle != null || cycleLen != null) {
                    for (int i = 0; i < cycle; i++) {
                        List<String> param = new ArrayList<String>();
                        for (int j = 0; j < cycleLen; j++) {
                            param.add(getStringValue(parameters, 8 + i * cycleLen + j));
                        }
                        parsedParameters.put("CQI_" + i, param.toArray(new String[0]));
                    }
                }

            }
            return parsedParameters;
        }
    },
    HARQI("HARQI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "#Header params.";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "#HARQ processes";
                final Integer cycle = getIntegerValue(parameters, 2);
                parsedParameters.put(key, cycle);
                key = "#Params/HARQ";
                final Integer cycleLen = getIntegerValue(parameters, 3);
                parsedParameters.put(key, cycleLen);
                if (cycle != null || cycleLen != null) {
                    for (int i = 0; i < cycle; i++) {
                        List<String> param = new ArrayList<String>();
                        for (int j = 0; j < cycleLen; j++) {
                            param.add(getStringValue(parameters, 4 + i * cycleLen + j));
                        }
                        parsedParameters.put("HARQ_" + i, param.toArray(new String[0]));
                    }
                }

            }
            return parsedParameters;
        }
    },
    HSSCCHI("HSSCCHI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "#Header params.";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "#HS-SCCH channels";
                final Integer cycle = getIntegerValue(parameters, 2);
                parsedParameters.put(key, cycle);
                key = "#Params/HS-SCCH";
                final Integer cycleLen = getIntegerValue(parameters, 3);
                parsedParameters.put(key, cycleLen);
                if (cycle != null || cycleLen != null) {
                    for (int i = 0; i < cycle; i++) {
                        List<String> param = new ArrayList<String>();
                        for (int j = 0; j < cycleLen; j++) {
                            param.add(getStringValue(parameters, 4 + i * cycleLen + j));
                        }
                        parsedParameters.put("HSSCCHI_" + i, param.toArray(new String[0]));
                    }
                }

            }
            return parsedParameters;
        }
    },
    PLAI("PLAI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "Packet technology";
                final Integer pt = getIntegerValue(parameters, 0);
                parsedParameters.put(key, pt);
                if (5 == pt) {
                    key = "#PLA header parameters";
                    parsedParameters.put(key, getIntegerValue(parameters, 1));
                    key = "Sample dur.";
                    parsedParameters.put(key, getIntegerValue(parameters, 2));
                    key = "#PLA sets";
                    final Integer cycle = getIntegerValue(parameters, 3);
                    parsedParameters.put(key, cycle);
                    key = "#params/PLA set";
                    final Integer cycleLen = getIntegerValue(parameters, 4);
                    parsedParameters.put(key, cycleLen);
                    if (cycle != null || cycleLen != null) {
                        for (int i = 0; i < cycle; i++) {
                            List<String> param = new ArrayList<String>();
                            for (int j = 0; j < cycleLen; j++) {
                                param.add(getStringValue(parameters, 5 + i * cycleLen + j));
                            }
                            parsedParameters.put("PLAI_" + i, param.toArray(new String[0]));
                        }
                    }
                }
            }
            return parsedParameters;
        }
    },
    PLAID("PLAID") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    PLAIU("PLAIU") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    HBI("HBI") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    MACERATE("MACERATE") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    AGRANT("AGRANT") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    SGRANT("SGRANT") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    EDCHI("EDCHI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    HSUPASI("HSUPASI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    DRCI("DRCI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version) || "1.86".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "#Header params";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Sample duration";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "#DRC sets";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "#params/DRC set";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
                key = "Percentage";
                parsedParameters.put(key, getFloatValue(parameters, 5));
                key = "Requested rate";
                parsedParameters.put(key, getIntegerValue(parameters, 6));
                key = "Packet length";
                parsedParameters.put(key, getIntegerValue(parameters, 7));
            }
            return parsedParameters;

        }
    },
    RDRC("RDRC") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    FDRC("FDRC") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    PHFER("PHFER") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    MARKOVMUX("MARKOVMUX") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    MARKOVSTATS("MARKOVSTATS") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "#Header params";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "M FER";
                parsedParameters.put(key, getFloatValue(parameters, 2));
                key = "#expected values";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "#Params";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
                key = "M expected";
                parsedParameters.put(key, getFloatValue(parameters, 5));
                key = "M 1/1";
                parsedParameters.put(key, getIntegerValue(parameters, 6));
                key = "M 1/2";
                parsedParameters.put(key, getIntegerValue(parameters, 7));
                key = "M 1/4";
                parsedParameters.put(key, getIntegerValue(parameters, 8));
                key = "M 1/8";
                parsedParameters.put(key, getIntegerValue(parameters, 9));
                key = "M erasures";
                parsedParameters.put(key, getIntegerValue(parameters, 10));
            }
            return parsedParameters;

        }
    },
    MER("MER") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));

                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "MER";
                parsedParameters.put(key, getFloatValue(parameters, 1));

            }
            return parsedParameters;
        }
    },
    DVBI("DVBI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                Integer dvbType = getIntegerValue(parameters, 0);
                parsedParameters.put("DVB type", dvbType);
                if (dvbType == 1) {
                    parsedParameters.put("Service state", getIntegerValue(parameters, 1));
                    parsedParameters.put("Frequency", getFloatValue(parameters, 2));
                    parsedParameters.put("Bandwidth", getFloatValue(parameters, 3));
                    parsedParameters.put("Cell identifier", getIntegerValue(parameters, 4));
                    parsedParameters.put("Transmission mode", getIntegerValue(parameters, 5));
                    parsedParameters.put("Modulation", getIntegerValue(parameters, 6));
                    parsedParameters.put("Code rate LP", getIntegerValue(parameters, 7));
                    parsedParameters.put("Code rate HP", getIntegerValue(parameters, 8));
                    parsedParameters.put("Guard time", getIntegerValue(parameters, 9));
                    parsedParameters.put("MPE-FEC code rate LP", getIntegerValue(parameters, 10));
                    parsedParameters.put("MPE-FEC code rate HP", getIntegerValue(parameters, 11));
                    parsedParameters.put("Hierarchy", getIntegerValue(parameters, 12));
                }

            }
            return parsedParameters;
        }
    },
    DVBFER("DVBFER") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                Integer dvbType = getIntegerValue(parameters, 0);
                parsedParameters.put("DVB type", dvbType);
                if (dvbType == 1) {
                    parsedParameters.put("FER", getFloatValue(parameters, 1));
                    parsedParameters.put("MFER", getFloatValue(parameters, 2));
                    parsedParameters.put("Frame count", getIntegerValue(parameters, 3));
                }

            }
            return parsedParameters;
        }
    },
    DVBBER("DVBBER") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                Integer dvbType = getIntegerValue(parameters, 0);
                parsedParameters.put("DVB type", dvbType);
                if (dvbType == 1) {
                    parsedParameters.put("BER", getFloatValue(parameters, 1));
                    parsedParameters.put("VBER", getFloatValue(parameters, 2));
                }

            }
            return parsedParameters;
        }
    },
    DVBRXL("DVBRXL") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                Integer dvbType = getIntegerValue(parameters, 0);
                parsedParameters.put("DVB type", dvbType);
                if (dvbType == 1) {
                    parsedParameters.put("Num. header params", getIntegerValue(parameters, 1));
                    final Integer cycle = getIntegerValue(parameters, 2);
                    parsedParameters.put("#Channel", cycle);
                    final Integer cycleLen = getIntegerValue(parameters, 3);
                    parsedParameters.put("#Params per ch.", cycleLen);
                    if (cycle != null || cycleLen != null) {
                        for (int i = 0; i < cycle; i++) {
                            List<String> param = new ArrayList<String>();
                            for (int j = 0; j < cycleLen; j++) {
                                param.add(getStringValue(parameters, 4 + i * cycleLen + j));
                            }
                            parsedParameters.put("DVBRXL_" + i, param.toArray(new String[0]));
                        }
                    }
                }

            }
            return parsedParameters;
        }
    },
    SCAN("SCAN") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                if (system == 1 || system == 2 || system == 3 || system == 22) {
                    parsedParameters.put("ARFCN", getIntegerValue(parameters, 1));
                    parsedParameters.put("BSIC", getIntegerValue(parameters, 2));
                    parsedParameters.put("RxLev", getFloatValue(parameters, 3));
                } else if (system == 4 || system == 5 || system == 6 || system == 9 || system == 10 || system == 23 || system == 24
                        || system == 25) {
                    parsedParameters.put("Channel", getIntegerValue(parameters, 1));
                    parsedParameters.put("DCC", getIntegerValue(parameters, 2));
                    parsedParameters.put("RXL", getFloatValue(parameters, 3));
                } else if (system == 7 || system == 8 || system == 26) {
                    final Integer carrier = getIntegerValue(parameters, 1);
                    if (carrier != 0) {
                        parsedParameters.put("Carrier", carrier);
                        parsedParameters.put("PN", getIntegerValue(parameters, 2));
                        parsedParameters.put("Ec/Io", getFloatValue(parameters, 3));
                    } else {
                        // reserved 1 field
                        parsedParameters.put("Carrier", getIntegerValue(parameters, 2));
                        parsedParameters.put("RSSI", getFloatValue(parameters, 3));
                    }
                } else if (system == 12 || system == 13 || system == 20 || system == 21 || system == 31 || system == 33
                        || system == 34) {
                    final Integer channel = getIntegerValue(parameters, 1);
                    if (channel != 0) {
                        parsedParameters.put("Channel", channel);
                        parsedParameters.put("RSSI", getFloatValue(parameters, 2));
                        parsedParameters.put("Ch type", getIntegerValue(parameters, 3));
                        parsedParameters.put("Scrambling code", getIntegerValue(parameters, 4));
                        parsedParameters.put("Ec/No", getFloatValue(parameters, 5));
                    } else {
                        parsedParameters.put("Channel number", channel);
                        // 2 field not used
                        parsedParameters.put("Channel", getIntegerValue(parameters, 4));
                        parsedParameters.put("RSSI", getFloatValue(parameters, 5));
                    }
                }
            }
            return parsedParameters;
        }
    },
    TSCAN("TSCAN") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                if (system == 12 || system == 13 || system == 20 || system == 21 || system == 31 || system == 33 || system == 34) {
                    parsedParameters.put("Channel", getIntegerValue(parameters, 1));
                    parsedParameters.put("RSSI", getFloatValue(parameters, 2));
                    parsedParameters.put("Ch type", getIntegerValue(parameters, 3));
                    parsedParameters.put("Chip", getIntegerValue(parameters, 4));
                    parsedParameters.put("Ec/No", getFloatValue(parameters, 5));
                }
            }
            return parsedParameters;
        }
    },
    DSCAN("DSCAN") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                if (system == 12 || system == 13 || system == 20 || system == 21 || system == 31 || system == 33 || system == 34) {
                    parsedParameters.put("Channel", getIntegerValue(parameters, 1));
                    parsedParameters.put("RSSI", getFloatValue(parameters, 2));
                    parsedParameters.put("Ch type", getIntegerValue(parameters, 3));
                    parsedParameters.put("Scrambling code", getIntegerValue(parameters, 4));
                    parsedParameters.put("Delay spread", getFloatValue(parameters, 5));
                }
            }
            return parsedParameters;
        }
    },
    DELAY("DELAY") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                if (system == 7 || system == 8 || system == 26) {
                    parsedParameters.put("Carrier", getIntegerValue(parameters, 1));
                    parsedParameters.put("PN", getIntegerValue(parameters, 2));
                    parsedParameters.put("Delay", getFloatValue(parameters, 3));

                } else if (system == 12 || system == 13 || system == 20 || system == 21 || system == 31 || system == 33
                        || system == 34) {
                    parsedParameters.put("Channel", getIntegerValue(parameters, 1));
                    parsedParameters.put("RSSI", getFloatValue(parameters, 2));
                    parsedParameters.put("Ch type", getIntegerValue(parameters, 3));
                    parsedParameters.put("Scrambling code", getIntegerValue(parameters, 4));
                    parsedParameters.put("Delay spread", getFloatValue(parameters, 5));
                }
            }
            return parsedParameters;
        }
    },
    RSCP("RSCP") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                parsedParameters.put("Channel", getIntegerValue(parameters, 1));
                parsedParameters.put("RSSI", getFloatValue(parameters, 2));
                parsedParameters.put("Ch type", getIntegerValue(parameters, 3));
                parsedParameters.put("Scrambling code", getIntegerValue(parameters, 4));
                parsedParameters.put("RSCP", getFloatValue(parameters, 5));

            }
            return parsedParameters;
        }
    },
    SSCAN("SSCAN") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                parsedParameters.put("Channel", getIntegerValue(parameters, 1));
                parsedParameters.put("RSSI", getFloatValue(parameters, 2));
                parsedParameters.put("Ch type", getIntegerValue(parameters, 3));
                parsedParameters.put("Scrambling code", getIntegerValue(parameters, 4));
                parsedParameters.put("CPICH SIR", getFloatValue(parameters, 5));

            }
            return parsedParameters;
        }
    },
    DPROF("DPROF") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                parsedParameters.put("Channel", getIntegerValue(parameters, 1));
                parsedParameters.put("Scrambling code", getIntegerValue(parameters, 2));
                parsedParameters.put("Ch type", getIntegerValue(parameters, 3));
                parsedParameters.put("#Samples", getFloatValue(parameters, 4));
                parsedParameters.put("Sample offset", getFloatValue(parameters, 5));
                parsedParameters.put("Sample", getFloatValue(parameters, 6));

            }
            return parsedParameters;
        }
    },

    DVBRATE("DVBRATE") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    FREQSCAN("FREQSCAN") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    SPECTRUMSCAN("SPECTRUMSCAN") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Scanning mode";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    PILOTSCAN("PILOTSCAN") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    OFDMSCAN("OFDMSCAN") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    TPROFSCAN("PPPRATE") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    DPROFSCAN("DPROFSCAN") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    FINGER("FINGER") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                final Integer cycle = getIntegerValue(parameters, 1);
                parsedParameters.put("#Fingers", cycle);
                final Integer cycleLen = getIntegerValue(parameters, 2);
                parsedParameters.put("#Params/finger", cycleLen);
                if (cycle != null || cycleLen != null) {
                    for (int i = 0; i < cycle; i++) {
                        List<String> param = new ArrayList<String>();
                        for (int j = 0; j < cycleLen; j++) {
                            param.add(getStringValue(parameters, 8 + i * cycleLen + j));
                        }
                        parsedParameters.put("Fingers_" + i, param.toArray(new String[0]));
                    }
                }

            }
            return parsedParameters;
        }
    },
    CISCAN("CISCAN") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                if (system == 1 || system == 2 || system == 3 || system == 22) {
                    parsedParameters.put("ARFCN", getIntegerValue(parameters, 1));
                    parsedParameters.put("BSIC", getIntegerValue(parameters, 2));
                    parsedParameters.put("C/I", getFloatValue(parameters, 3));
                }
            }
            return parsedParameters;
        }
    },
    UISCAN("UISCAN") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary

            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                if (system == 12 || system == 13 || system == 20 || system == 21 || system == 31 || system == 33 || system == 34) {
                    parsedParameters.put("#params/cell", getIntegerValue(parameters, 1));
                    parsedParameters.put("#cells", getIntegerValue(parameters, 2));
                    parsedParameters.put("ARFCN", getIntegerValue(parameters, 3));
                    parsedParameters.put("SC", getIntegerValue(parameters, 4));
                    parsedParameters.put("UL interf.", getFloatValue(parameters, 5));
                }
            }
            return parsedParameters;
        }
    },
    CELLSCAN("CELLSCAN") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                parsedParameters.put("#Header params", getIntegerValue(parameters, 1));
                final Integer cycle = getIntegerValue(parameters, 2);
                parsedParameters.put("#Cells", cycle);
                final Integer cycleLen = getIntegerValue(parameters, 3);
                parsedParameters.put("Params/Cell", cycleLen);
                if (cycle != null || cycleLen != null) {
                    for (int i = 0; i < cycle; i++) {
                        List<String> param = new ArrayList<String>();
                        for (int j = 0; j < cycleLen; j++) {
                            param.add(getStringValue(parameters, 4 + i * cycleLen + j));
                        }
                        parsedParameters.put("CELLSCAN_" + i, param.toArray(new String[0]));
                    }
                }
            }
            return parsedParameters;
        }
    },
    HOA("HOA") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Handover context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "#Header params";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "HOA type";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("HOA type", getIntegerValue(parameters, 0));
                parsedParameters.put("Channel number", getIntegerValue(parameters, 1));
                parsedParameters.put("TSL or SC", getIntegerValue(parameters, 2));
                parsedParameters.put("Current system", getIntegerValue(parameters, 3));
                parsedParameters.put("Att. ch", getIntegerValue(parameters, 4));
                parsedParameters.put("Att. TSL or Att. SC", getIntegerValue(parameters, 5));
                parsedParameters.put("Att. system", getIntegerValue(parameters, 6));
            }
            return parsedParameters;
        }
    },
    HOS("HOS") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Handover context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
            }
            return parsedParameters;
        }
    },
    HOF("HOF") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Handover context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("HOF cause", getIntegerValue(parameters, 0));
            }

            return parsedParameters;
        }
    },
    CREL("CREL") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Handover context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("Old system", getIntegerValue(parameters, 0));
                parsedParameters.put("Old LAC", getIntegerValue(parameters, 1));
                parsedParameters.put("Old CI", getIntegerValue(parameters, 2));
                parsedParameters.put("New system", getIntegerValue(parameters, 3));
                parsedParameters.put("New LAC", getIntegerValue(parameters, 4));
                parsedParameters.put("New CI", getIntegerValue(parameters, 5));
            }
            return parsedParameters;
        }
    },
    SHOI("SHOI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                if (system == 12 || system == 13 || system == 20 || system == 21 || system == 31 || system == 33 || system == 34) {
                    parsedParameters.put("SHO event", getIntegerValue(parameters, 1));
                }
                // else if (system==7||system==8||system==14||system==15||system==26||system==30){
                // }
            }
            return parsedParameters;
        }
    },
    SHO("SHO") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                if (system == 12 || system == 13 || system == 20 || system == 21 || system == 31 || system == 33 || system == 34) {
                    parsedParameters.put("SHO status", getIntegerValue(parameters, 1));
                    parsedParameters.put("RRC cause", getIntegerValue(parameters, 2));
                    parsedParameters.put("#SCs added", getIntegerValue(parameters, 3));
                    parsedParameters.put("#SCs removed", getIntegerValue(parameters, 4));
                    parsedParameters.put("Added SC", getIntegerValue(parameters, 5));
                    parsedParameters.put("Removed SC", getIntegerValue(parameters, 6));
                } else if (system == 7 || system == 8 || system == 14 || system == 15 || system == 26 || system == 30) {
                    parsedParameters.put("#Header params", getIntegerValue(parameters, 1));
                    parsedParameters.put("#Pilots added", getIntegerValue(parameters, 2));
                    parsedParameters.put("#Pilots removed", getIntegerValue(parameters, 3));
                    parsedParameters.put("Added pilot", getIntegerValue(parameters, 4));
                    parsedParameters.put("Removed pilot", getIntegerValue(parameters, 5));
                }

            }
            return parsedParameters;
        }
    },
    SIPU("SIPU") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("#Header params", getIntegerValue(parameters, 0));
                parsedParameters.put("SIP msg. name", getStringValue(parameters, 1));
                parsedParameters.put("SIP msg. length", getIntegerValue(parameters, 2));
                // TODO check type
                parsedParameters.put("SIP msg.", getStringValue(parameters, 3));

            }
            return parsedParameters;
        }
    },
    SIPD("SIPD") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("#Header params", getIntegerValue(parameters, 0));
                parsedParameters.put("SIP msg. name", getStringValue(parameters, 1));
                parsedParameters.put("SIP msg. length", getIntegerValue(parameters, 2));
                // TODO check type
                parsedParameters.put("SIP msg.", getStringValue(parameters, 3));

            }
            return parsedParameters;
        }
    },
    RTPU("RTPU") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("#Header params", getIntegerValue(parameters, 0));
                parsedParameters.put("RTP msg. name", getStringValue(parameters, 1));
                parsedParameters.put("RTP msg. seq.#", getIntegerValue(parameters, 2));
                parsedParameters.put("RTP msg. length", getIntegerValue(parameters, 3));
                // TODO check type
                parsedParameters.put("RTP msg.", getStringValue(parameters, 4));

            }
            return parsedParameters;
        }
    },
    RTPD("RTPD") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("#Header params", getIntegerValue(parameters, 0));
                parsedParameters.put("RTP msg. name", getStringValue(parameters, 1));
                parsedParameters.put("RTP msg. seq.#", getIntegerValue(parameters, 2));
                parsedParameters.put("RTP msg. length", getIntegerValue(parameters, 3));
                // TODO check type
                parsedParameters.put("RTP msg.", getStringValue(parameters, 4));

            }
            return parsedParameters;
        }
    },

    LUA("LUA") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Location area update context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "LAU type";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
            } else if ("1.86".equals(version)) {
                parsedParameters.put("LAU type", getIntegerValue(parameters, 0));
            }
            return parsedParameters;
        }
    },
    LUS("LUS") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Location area update context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Old LAC";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "LAC";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "MCC";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
                key = "MNC";
                parsedParameters.put(key, getIntegerValue(parameters, 5));
            } else if ("1.86".equals(version)) {
                String key = "Old LAC";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "New LAC";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "MCC";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "MNC";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
            }
            return parsedParameters;
        }
    },
    LUF("LUF") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Location area update context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "LUF status";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Old LAC";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "LUF status";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Old LAC";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "MM cause";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    CHI("CHI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                if (system == 1 || system == 2 || system == 3 || system == 22) {
                    parsedParameters.put("Ch type", getIntegerValue(parameters, 1));
                    parsedParameters.put("ARFCN", getIntegerValue(parameters, 2));
                    parsedParameters.put("CI", getIntegerValue(parameters, 3));
                    parsedParameters.put("LAC", getIntegerValue(parameters, 4));
                    // reserved 1 field
                    parsedParameters.put("DTX UL", getIntegerValue(parameters, 6));
                    parsedParameters.put("RTL max", getIntegerValue(parameters, 7));
                    parsedParameters.put("Ext. ch type", getIntegerValue(parameters, 8));
                    parsedParameters.put("TN", getIntegerValue(parameters, 9));
                    parsedParameters.put("BCCH ARFCN", getIntegerValue(parameters, 10));
                } else if (system == 4 || system == 9) {
                    parsedParameters.put("Ch type", getIntegerValue(parameters, 1));
                    parsedParameters.put("Channel", getIntegerValue(parameters, 2));
                } else if (system == 5 || system == 6) {
                    parsedParameters.put("Ch type", getIntegerValue(parameters, 1));
                    parsedParameters.put("Channel", getIntegerValue(parameters, 2));
                    parsedParameters.put("NW type", getIntegerValue(parameters, 3));
                    parsedParameters.put("PSID1", getIntegerValue(parameters, 4));
                    parsedParameters.put("PSID2", getIntegerValue(parameters, 5));
                    parsedParameters.put("PSID3", getIntegerValue(parameters, 6));
                    parsedParameters.put("PSID4", getIntegerValue(parameters, 7));
                    parsedParameters.put("LAREG", getIntegerValue(parameters, 8));
                    parsedParameters.put("RNUM", getIntegerValue(parameters, 9));
                    parsedParameters.put("REG PERIOD", getIntegerValue(parameters, 10));
                } else if (system == 7 || system == 8 || system == 14 || system == 15 || system == 26 || system == 30) {
                    parsedParameters.put("Ch type", getIntegerValue(parameters, 1));
                    parsedParameters.put("Carrier", getIntegerValue(parameters, 2));
                    parsedParameters.put("MCC", getIntegerValue(parameters, 3));
                    parsedParameters.put("SID", getIntegerValue(parameters, 4));
                    parsedParameters.put("NID", getIntegerValue(parameters, 5));
                    parsedParameters.put("Slotted mode", getIntegerValue(parameters, 6));
                    parsedParameters.put("SEARCH_WIN_A", getIntegerValue(parameters, 7));
                    parsedParameters.put("SEARCH_WIN_N", getIntegerValue(parameters, 8));
                    parsedParameters.put("SEARCH_WIN_R", getIntegerValue(parameters, 9));
                    parsedParameters.put("T_ADD", getIntegerValue(parameters, 10));
                    parsedParameters.put("T_DROP", getIntegerValue(parameters, 11));
                    parsedParameters.put("T_TDROP", getIntegerValue(parameters, 12));
                    parsedParameters.put("T_COMP", getIntegerValue(parameters, 13));
                    parsedParameters.put("P_REV", getIntegerValue(parameters, 14));
                    parsedParameters.put("MIN_P_REV", getIntegerValue(parameters, 15));
                } else if (system == 12 || system == 13 || system == 20 || system == 21 || system == 31 || system == 33
                        || system == 34) {
                    parsedParameters.put("RRC state", getIntegerValue(parameters, 1));
                    parsedParameters.put("Channel", getIntegerValue(parameters, 2));
                    parsedParameters.put("CI", getIntegerValue(parameters, 3));
                    parsedParameters.put("LAC", getIntegerValue(parameters, 4));
                    parsedParameters.put("Addition window", getFloatValue(parameters, 5));
                    parsedParameters.put("1A time to tr.", getIntegerValue(parameters, 6));
                    parsedParameters.put("Drop window", getFloatValue(parameters, 7));
                    parsedParameters.put("1B time to tr.", getIntegerValue(parameters, 8));
                    parsedParameters.put("Repl. window", getFloatValue(parameters, 9));
                    parsedParameters.put("1C time to tr.", getIntegerValue(parameters, 10));
                    parsedParameters.put("DL SF", getIntegerValue(parameters, 11));
                    parsedParameters.put("Min UL SF", getIntegerValue(parameters, 12));
                    parsedParameters.put("DRX cycle", getIntegerValue(parameters, 13));
                    parsedParameters.put("Max TX power", getIntegerValue(parameters, 14));
                    parsedParameters.put("Treselection", getIntegerValue(parameters, 15));
                } else if (system == 11) {
                    parsedParameters.put("Ch type", getIntegerValue(parameters, 1));
                    parsedParameters.put("Channel", getIntegerValue(parameters, 2));
                    parsedParameters.put("Band", getIntegerValue(parameters, 3));
                    parsedParameters.put("LAC", getIntegerValue(parameters, 4));
                    parsedParameters.put("Ext. ch type", getIntegerValue(parameters, 5));
                    parsedParameters.put("Encryption", getIntegerValue(parameters, 6));
                    parsedParameters.put("Slot number", getIntegerValue(parameters, 7));
                } else if (system == 32) {
                    parsedParameters.put("CI", getIntegerValue(parameters, 1));
                    parsedParameters.put("LAC", getIntegerValue(parameters, 2));
                }

            }
            return parsedParameters;
        }
    },
    GANCHI("GANCHI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                if (system == 32) {
                    parsedParameters.put("#Header params", getIntegerValue(parameters, 1));
                    parsedParameters.put("GAN state", getIntegerValue(parameters, 2));
                    parsedParameters.put("GAN channel", getIntegerValue(parameters, 3));
                    parsedParameters.put("GAN BSIC", getIntegerValue(parameters, 4));
                    parsedParameters.put("GAN CI", getIntegerValue(parameters, 5));
                    parsedParameters.put("GAN LAC", getIntegerValue(parameters, 6));
                    parsedParameters.put("GAN IP", getStringValue(parameters, 7));
                    parsedParameters.put("SEGW IP", getStringValue(parameters, 8));
                }
            }
            return parsedParameters;
        }
    },
    SEI("SEI") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Service status";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("Service status", getIntegerValue(parameters, 0));
            }
            return parsedParameters;
        }
    },
    ROAM("ROAM") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Roaming status";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
            } else if ("1.86".equals(version)) {
                parsedParameters.put("Service status", getIntegerValue(parameters, 0));
            }
            return parsedParameters;
        }
    },
    DCHR("DCHR") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                if (system == 1 || system == 2 || system == 3 || system == 22) {
                    parsedParameters.put("Initiator", getIntegerValue(parameters, 1));
                    parsedParameters.put("Coding", getIntegerValue(parameters, 2));
                    parsedParameters.put("Data mode", getIntegerValue(parameters, 3));
                    parsedParameters.put("#CS TSL UL", getIntegerValue(parameters, 4));
                    parsedParameters.put("#CS TSL DL", getIntegerValue(parameters, 5));
                    parsedParameters.put("Modem type", getIntegerValue(parameters, 6));
                    parsedParameters.put("Compression", getStringValue(parameters, 7));
                } else if (system == 12 || system == 13 || system == 20 || system == 21 || system == 31 || system == 33
                        || system == 34) {
                    parsedParameters.put("Initiator", getIntegerValue(parameters, 1));
                    parsedParameters.put("Req. CS rate", getIntegerValue(parameters, 2));
                    parsedParameters.put("Data mode", getIntegerValue(parameters, 3));
                    parsedParameters.put("Modem type", getIntegerValue(parameters, 4));
                    parsedParameters.put("Compression", getStringValue(parameters, 5));
                }
            }
            return parsedParameters;
        }
    },
    DCHI("DCHI") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                parsedParameters.put("Coding", getIntegerValue(parameters, 1));
                parsedParameters.put("Data mode", getIntegerValue(parameters, 2));
                parsedParameters.put("#CS TSL UL", getIntegerValue(parameters, 3));
                parsedParameters.put("#CS TSL DL", getIntegerValue(parameters, 4));
                parsedParameters.put("CS TNs UL", getIntegerValue(parameters, 5));
                parsedParameters.put("CS TNs DL", getStringValue(parameters, 6));

            }
            return parsedParameters;
        }
    },
    HOP("HOP") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                final Integer hopping = getIntegerValue(parameters, 1);
                parsedParameters.put("Hopping", hopping);
                if (hopping == 1) {
                    parsedParameters.put("HSN", getIntegerValue(parameters, 2));
                    parsedParameters.put("MAIO", getIntegerValue(parameters, 3));
                    parsedParameters.put("Hopping ch", getIntegerValue(parameters, 4));
                } else {
                    parsedParameters.put("Traffic ARFCN", getIntegerValue(parameters, 2));
                }

            }
            return parsedParameters;
        }
    },
    NMISS("NMISS") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "#Header params";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Source system";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("#Header params", getIntegerValue(parameters, 0));
                parsedParameters.put("#nChs", getIntegerValue(parameters, 1));
                parsedParameters.put("#Params", getIntegerValue(parameters, 2));
                final Integer system = getIntegerValue(parameters, 3);
                parsedParameters.put("System", system);
                if (system == 1 || system == 2 || system == 3 || system == 22) {
                    parsedParameters.put("Channel", getIntegerValue(parameters, 4));
                    parsedParameters.put("BSIC", getIntegerValue(parameters, 5));
                    parsedParameters.put("RX level", getIntegerValue(parameters, 6));
                } else if (system == 12 || system == 13 || system == 20 || system == 21 || system == 31 || system == 33
                        || system == 34) {
                    parsedParameters.put("Channel", getIntegerValue(parameters, 4));
                    parsedParameters.put("Scrambling code", getIntegerValue(parameters, 5));
                    parsedParameters.put("Ec/N0", getIntegerValue(parameters, 6));
                    parsedParameters.put("RSCP", getIntegerValue(parameters, 7));
                    parsedParameters.put("Diff. tp st", getIntegerValue(parameters, 8));
                }
            }
            return parsedParameters;
        }
    },
    NLIST("NLIST") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "#Header params";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Source system";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("#Header params", getIntegerValue(parameters, 0));
                parsedParameters.put("Nlist status", getIntegerValue(parameters, 1));
                parsedParameters.put("#nChs", getIntegerValue(parameters, 2));
                parsedParameters.put("#Params", getIntegerValue(parameters, 3));
                final Integer system = getIntegerValue(parameters, 4);
                parsedParameters.put("System", system);
                if (system == 1 || system == 2 || system == 3 || system == 22) {
                    parsedParameters.put("ARFCN", getIntegerValue(parameters, 5));
                    parsedParameters.put("BSIC", getIntegerValue(parameters, 6));
                } else if (system == 12 || system == 13 || system == 20 || system == 21 || system == 31 || system == 33
                        || system == 34) {
                    parsedParameters.put("Channel", getIntegerValue(parameters, 5));
                    parsedParameters.put("Scrambling code", getIntegerValue(parameters, 6));
                }
            }
            return parsedParameters;
        }
    },
    SEPR("SEPR") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                if (system == 7 || system == 8 || system == 14 || system == 15 || system == 26 || system == 30) {
                    parsedParameters.put("Service option", getIntegerValue(parameters, 1));
                    parsedParameters.put("Req. forw. RC", getIntegerValue(parameters, 2));
                    parsedParameters.put("Req. rev. RC", getIntegerValue(parameters, 3));
                    parsedParameters.put("Req. F-FCH MUX", getIntegerValue(parameters, 4));
                    parsedParameters.put("Req. R-FCH MUX", getIntegerValue(parameters, 5));

                }
            }
            return parsedParameters;
        }
    },
    SEPN("SEPN") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                if (system == 7 || system == 8 || system == 14 || system == 15 || system == 26 || system == 30) {
                    parsedParameters.put("Neg. SO", getIntegerValue(parameters, 1));
                    parsedParameters.put("Neg. forw. RC", getIntegerValue(parameters, 2));
                    parsedParameters.put("Neg. rev. RC", getIntegerValue(parameters, 3));
                    parsedParameters.put("Neg. F-FCH MUX", getIntegerValue(parameters, 4));
                    parsedParameters.put("Neg. R-FCH MUX", getIntegerValue(parameters, 5));

                }
            }
            return parsedParameters;
        }
    },
    SERVCONF("SERVCONF") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    RACHI("RACHI") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                if (system == 12 || system == 13 || system == 20 || system == 21 || system == 31 || system == 33 || system == 34) {
                    parsedParameters.put("Init. TX pwr.", getIntegerValue(parameters, 1));
                    parsedParameters.put("Preamble step", getIntegerValue(parameters, 2));
                    parsedParameters.put("Preamble count", getIntegerValue(parameters, 3));
                    parsedParameters.put("RACX TX power", getIntegerValue(parameters, 4));
                    parsedParameters.put("Max preamble", getIntegerValue(parameters, 5));
                    parsedParameters.put("UL interf.", getIntegerValue(parameters, 6));
                    parsedParameters.put("AICH status", getIntegerValue(parameters, 7));
                    parsedParameters.put("Data gain", getIntegerValue(parameters, 8));
                    parsedParameters.put("Ctrl gain", getIntegerValue(parameters, 9));
                    parsedParameters.put("Power offset", getIntegerValue(parameters, 10));
                    parsedParameters.put("Message length", getIntegerValue(parameters, 11));
                    parsedParameters.put("Preamble cycles", getIntegerValue(parameters, 12));
                } else if (system == 7 || system == 8 || system == 14 || system == 15 || system == 26 || system == 30) {
                    parsedParameters.put("NOM_PWR", getIntegerValue(parameters, 1));
                    parsedParameters.put("INIT_PWR", getIntegerValue(parameters, 2));
                    parsedParameters.put("PWR_STEP", getIntegerValue(parameters, 3));
                    parsedParameters.put("NUM_STEP", getIntegerValue(parameters, 4));
                    parsedParameters.put("TX level", getFloatValue(parameters, 5));
                    parsedParameters.put("Access probe count max", getIntegerValue(parameters, 6));
                    parsedParameters.put("Access probe seq. max", getIntegerValue(parameters, 7));
                    parsedParameters.put("Result", getIntegerValue(parameters, 8));
                    parsedParameters.put("Access ch", getIntegerValue(parameters, 9));
                    parsedParameters.put("Random delay", getIntegerValue(parameters, 10));
                } else if (system == 27 || system == 28 || system == 29) {
                    parsedParameters.put("MAX #Probes", getIntegerValue(parameters, 1));
                    parsedParameters.put("MAX #Probe seqs", getIntegerValue(parameters, 2));
                    parsedParameters.put("Result", getIntegerValue(parameters, 3));
                    parsedParameters.put("#Probes", getIntegerValue(parameters, 4));
                    parsedParameters.put("#Probe seqs", getIntegerValue(parameters, 5));
                }
            }

            return parsedParameters;
        }
    },
    VOCS("VOCS") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version) || "1.86".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Voc. rate For.";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Voc. rate Rev.";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
            }
            return parsedParameters;

        }
    },
    PHCHI("PHCHI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                parsedParameters.put("#Header params", getIntegerValue(parameters, 1));
                parsedParameters.put("#Physical channels", getIntegerValue(parameters, 2));
                parsedParameters.put("#Params/channel", getIntegerValue(parameters, 3));
                parsedParameters.put("Type", getIntegerValue(parameters, 4));
                parsedParameters.put("Direction", getIntegerValue(parameters, 5));
                parsedParameters.put("Pilot PN", getIntegerValue(parameters, 6));
                parsedParameters.put("Walsh code", getIntegerValue(parameters, 7));
                parsedParameters.put("Rate", getIntegerValue(parameters, 8));
            }
            return parsedParameters;
        }
    },
    QPCHI("QPCHI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                parsedParameters.put("QPCH rate", getIntegerValue(parameters, 1));
                parsedParameters.put("Slot number", getIntegerValue(parameters, 2));
                parsedParameters.put("QPCH pilot#", getIntegerValue(parameters, 3));
                parsedParameters.put("PI Walsh", getIntegerValue(parameters, 4));
                parsedParameters.put("PI power offset", getFloatValue(parameters, 5));
                parsedParameters.put("THB", getIntegerValue(parameters, 6));
                parsedParameters.put("THI", getIntegerValue(parameters, 7));
            }
            return parsedParameters;
        }
    },
    FCHPACKETS("FCHPACKETS") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    CONNECTIONC("CONNECTIONC") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    CONNECTIOND("CONNECTIOND") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    SESSIONC("SESSIONC") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    RBI("RBI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "#Header params";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "#params/RB";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "#RBs";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "#Header params";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "#params/RB";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "#RBs";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                parsedParameters.put("RB ID", getIntegerValue(parameters, 4));
                parsedParameters.put("RLC ID", getIntegerValue(parameters, 5));
                parsedParameters.put("TrCh ID", getIntegerValue(parameters, 6));
                parsedParameters.put("Direction", getIntegerValue(parameters, 7));
                parsedParameters.put("Logical Ch", getIntegerValue(parameters, 8));
                parsedParameters.put("RLC mode", getIntegerValue(parameters, 9));
                parsedParameters.put("Chiphering", getIntegerValue(parameters, 10));
                parsedParameters.put("TrCh type", getIntegerValue(parameters, 11));
            }
            return parsedParameters;
        }
    },
    TRCHI("TRCHI") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "#Header params";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "#params/TRCH";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "#TrChs";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "#Header params";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "#params/TRCH";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "#TrChs";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                parsedParameters.put("TrChs ID", getIntegerValue(parameters, 4));
                parsedParameters.put("CCTrChs ID", getIntegerValue(parameters, 5));
                parsedParameters.put("Direction", getIntegerValue(parameters, 6));
                parsedParameters.put("TrChs type", getIntegerValue(parameters, 7));
                parsedParameters.put("TrChs coding", getIntegerValue(parameters, 8));
                parsedParameters.put("CRC length", getIntegerValue(parameters, 9));
                parsedParameters.put("TTI", getIntegerValue(parameters, 10));
                parsedParameters.put("Rate-m. attr.", getIntegerValue(parameters, 11));

            }
            return parsedParameters;
        }
    },
    RRA("RRA") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "RRC context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                parsedParameters.put("RRC est. cause", getIntegerValue(parameters, 1));
            }
            return parsedParameters;
        }
    },
    RRC("RRC") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "RRC context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                parsedParameters.put("#RRC att.", getIntegerValue(parameters, 1));
                parsedParameters.put("RRC est. time", getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    RRF("RRF") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "RRC context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                parsedParameters.put("#RRC att.", getIntegerValue(parameters, 1));
                parsedParameters.put("RRC fail. time", getIntegerValue(parameters, 2));
                parsedParameters.put("RRC rej. status", getIntegerValue(parameters, 3));
                parsedParameters.put("RRC rej. cause", getIntegerValue(parameters, 4));
            }
            return parsedParameters;
        }
    },
    RRD("RRD") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "RRC context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                parsedParameters.put("RRC rel. status", getIntegerValue(parameters, 1));
                parsedParameters.put("RRC rel. cause", getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },

    CIPI("CIPI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                parsedParameters.put("Ciph. type", getIntegerValue(parameters, 1));
                parsedParameters.put("KSG", getIntegerValue(parameters, 2));
                parsedParameters.put("SCK", getIntegerValue(parameters, 3));
            }
            return parsedParameters;
        }
    },
    L3U("L3U") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("Subchannel/Channel type", getStringValue(parameters, 0));
                parsedParameters.put("L3 msg", getStringValue(parameters, 1));
                // TODO check types
                parsedParameters.put("L3 data", getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    L3D("L3D") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("Subchannel/Channel type", getStringValue(parameters, 0));
                parsedParameters.put("L3 msg", getStringValue(parameters, 1));
                // TODO check types
                parsedParameters.put("L3 data", getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    L2U("L2U") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("Subchannel", getStringValue(parameters, 0));
                parsedParameters.put("L2 msg", getStringValue(parameters, 1));
                // TODO check types
                parsedParameters.put("L2 data", getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    L2D("L2D") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("Subchannel", getStringValue(parameters, 0));
                parsedParameters.put("L2 msg", getStringValue(parameters, 1));
                // TODO check types
                parsedParameters.put("L2 data", getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    MACU("MACU") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("Subchannel", getStringValue(parameters, 0));
                parsedParameters.put("RLC/MAC msg", getStringValue(parameters, 1));
                // TODO check types
                parsedParameters.put("RLC/MAC data", getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    MACD("MACD") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("Subchannel", getStringValue(parameters, 0));
                parsedParameters.put("RLC/MAC msg", getStringValue(parameters, 1));
                // TODO check types
                parsedParameters.put("RLC/MAC data", getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    LLCU("LLCU") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("Subchannel", getStringValue(parameters, 0));
                parsedParameters.put("LLC msg", getStringValue(parameters, 1));
                // TODO check types
                parsedParameters.put("LLC data", getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    LLCD("LLCD") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("Subchannel", getStringValue(parameters, 0));
                parsedParameters.put("LLC msg", getStringValue(parameters, 1));
                // TODO check types
                parsedParameters.put("LLC data", getStringValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    RRLPU("RRLPU") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("Subchannel", getStringValue(parameters, 0));
                parsedParameters.put("RRLP msg", getStringValue(parameters, 1));
                // TODO check types
                parsedParameters.put("RRLP data", getStringValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    RRLPD("RRLPD") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("Subchannel", getStringValue(parameters, 0));
                parsedParameters.put("RRLP msg", getStringValue(parameters, 1));
                // TODO check types
                parsedParameters.put("RRLP data", getStringValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    RRCU("RRCU") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("Subchannel", getStringValue(parameters, 0));
                parsedParameters.put("RRC msg", getStringValue(parameters, 1));
                // TODO check types
                parsedParameters.put("RRC data", getStringValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    RRCD("RRCD") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("Subchannel", getStringValue(parameters, 0));
                parsedParameters.put("RRC msg", getStringValue(parameters, 1));
                // TODO check types
                parsedParameters.put("RRC data", getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    RLCU("RLCU") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("Subchannel", getStringValue(parameters, 0));
                parsedParameters.put("RLC msg", getStringValue(parameters, 1));
                // TODO check types
                parsedParameters.put("RLC data", getStringValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    RLCD("RLCD") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("Subchannel", getStringValue(parameters, 0));
                parsedParameters.put("RLC msg", getStringValue(parameters, 1));
                // TODO check types
                parsedParameters.put("RLC data", getStringValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    SNPU("SNPU") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "#Header params";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Ch type";
                parsedParameters.put(key, getStringValue(parameters, 1));
                key = "SNP layer";
                parsedParameters.put(key, getStringValue(parameters, 2));
                key = "SNP msg. name";
                parsedParameters.put(key, getStringValue(parameters, 3));
                key = "Protocol subtype";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
                key = "SNP msg. length";
                parsedParameters.put(key, getIntegerValue(parameters, 5));
                // TODO check type
                key = "SNP msg.";
                parsedParameters.put(key, getStringValue(parameters, 6));
            }
            return parsedParameters;
        }
    },
    SNPD("SNPD") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "#Header params";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Ch type";
                parsedParameters.put(key, getStringValue(parameters, 1));
                key = "SNP layer";
                parsedParameters.put(key, getStringValue(parameters, 2));
                key = "SNP msg. name";
                parsedParameters.put(key, getStringValue(parameters, 3));
                key = "Protocol subtype";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
                key = "SNP msg. length";
                parsedParameters.put(key, getIntegerValue(parameters, 5));
                // TODO check type
                key = "SNP msg.";
                parsedParameters.put(key, getStringValue(parameters, 6));
            }
            return parsedParameters;
        }
    },
    GANSU("GANSU") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "#Header params";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "GAN msg.";
                parsedParameters.put(key, getStringValue(parameters, 1));
                key = "GUN subch.";
                parsedParameters.put(key, getStringValue(parameters, 2));
                key = "GAN msg. length";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                // TODO check type
                key = "GAN msg. data";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
            }
            return parsedParameters;
        }
    },
    GANSD("GANSD") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "#Header params";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "GAN msg.";
                parsedParameters.put(key, getStringValue(parameters, 1));
                key = "GUN subch.";
                parsedParameters.put(key, getStringValue(parameters, 2));
                key = "GAN msg. length";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                // TODO check type
                key = "GAN msg. data";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
            }
            return parsedParameters;
        }
    },
    L3SM("L3SM") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    L2SM("L2SM") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    RRCSM("RRCSM") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    RLCSM("RLCSM") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    MACSM("MACSM") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    LLCSM("LLCSM") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    SNPSM("SNPSM") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    RRLPSM("RRLPSM") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    GANSM("GANSM") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    SIPSM("SIPSM") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },

    RTPSM("RTPSM") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },

    PAA("PAA") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Packet session context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("Initiator", getIntegerValue(parameters, 0));
                parsedParameters.put("Protocol type", getIntegerValue(parameters, 1));
                parsedParameters.put("APN", getIntegerValue(parameters, 2));
                parsedParameters.put("IP", getStringValue(parameters, 3));
                parsedParameters.put("Header compr.", getIntegerValue(parameters, 4));
                parsedParameters.put("Data compr.", getIntegerValue(parameters, 5));
            }
            return parsedParameters;
        }
    },
    PAF("PAF") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Packet session context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Fail status";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Deact. cause";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("Fail status", getIntegerValue(parameters, 0));
                parsedParameters.put("Fail time", getIntegerValue(parameters, 1));
                parsedParameters.put("Fail cause", getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    PAC("PAC") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Packet session context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Packet act. state";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "IP";
                parsedParameters.put(key, getStringValue(parameters, 3));
            } else if ("1.86".equals(version)) {
                parsedParameters.put("PDP act. state", getIntegerValue(parameters, 0));
                parsedParameters.put("PDP act. time", getIntegerValue(parameters, 1));
                parsedParameters.put("IP", getStringValue(parameters, 2));
            }
            return parsedParameters;
        }
    },

    PAD("PAD") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Packet session context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Deact. status";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Deact. cause";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "Deact. time";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
            } else if ("1.86".equals(version)) {
                parsedParameters.put("Deact. status", getIntegerValue(parameters, 0));
                parsedParameters.put("Duration", getIntegerValue(parameters, 1));
                parsedParameters.put("Deact. cause", getStringValue(parameters, 2));
                parsedParameters.put("Deact. time", getStringValue(parameters, 3));
            }

            return parsedParameters;
        }
    },

    QSPR("QSPR") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {

                String key = "Packet session context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Avg. TPut class";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Peak TPut class";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "Delay class";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
                key = "Priority class";
                parsedParameters.put(key, getIntegerValue(parameters, 5));
                key = "Reliab. class";
                parsedParameters.put(key, getIntegerValue(parameters, 6));
                key = "Min avg. TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 7));
                key = "Min peak TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 8));
                key = "Min delay";
                parsedParameters.put(key, getIntegerValue(parameters, 9));
                key = "Min priority class";
                parsedParameters.put(key, getIntegerValue(parameters, 10));
                key = "Min reliability";
                parsedParameters.put(key, getIntegerValue(parameters, 11));
                key = "Req. traffic class";
                parsedParameters.put(key, getIntegerValue(parameters, 12));
                key = "Req. max UL TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 13));
                key = "Req. max DL TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 14));
                key = "Req. gr. UL TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 15));
                key = "Req. gr. DL TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 16));
                key = "Req. deliv. order";
                parsedParameters.put(key, getIntegerValue(parameters, 17));
                key = "Req. max SDU size";
                parsedParameters.put(key, getIntegerValue(parameters, 18));
                key = "Req. SDU err. ratio";
                parsedParameters.put(key, getStringValue(parameters, 19));
                key = "Req. resid. BER";
                parsedParameters.put(key, getStringValue(parameters, 20));
                key = "Req. deliv. err. SDU";
                parsedParameters.put(key, getIntegerValue(parameters, 21));
                key = "Req. transfer delay";
                parsedParameters.put(key, getIntegerValue(parameters, 22));
                key = "Req. THP";
                parsedParameters.put(key, getIntegerValue(parameters, 23));
                key = "Min traffic class";
                parsedParameters.put(key, getIntegerValue(parameters, 24));
                key = "Min max UL TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 25));
                key = "Min max DL TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 26));
                key = "Min gr. UL TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 27));
                key = "Min gr. DL TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 28));
                key = "Min deliv. order";
                parsedParameters.put(key, getIntegerValue(parameters, 29));
                key = "Min max SDU size";
                parsedParameters.put(key, getIntegerValue(parameters, 30));
                key = "Min SDU err.";
                parsedParameters.put(key, getStringValue(parameters, 31));
                key = "Min resid. BER";
                parsedParameters.put(key, getStringValue(parameters, 32));
                key = "Min del. err. SDU";
                parsedParameters.put(key, getIntegerValue(parameters, 33));
                key = "Min tranfer delay";
                parsedParameters.put(key, getIntegerValue(parameters, 34));
                key = "Min THP";
                parsedParameters.put(key, getIntegerValue(parameters, 35));
            } else if ("1.86".equals(version)) {
                parsedParameters.put("Packet Tech.", getIntegerValue(parameters, 0));
                parsedParameters.put("Avg. TPut class", getIntegerValue(parameters, 1));
                parsedParameters.put("Peak TPut class", getIntegerValue(parameters, 2));
                parsedParameters.put("Delay class", getIntegerValue(parameters, 3));
                parsedParameters.put("Priority class", getIntegerValue(parameters, 4));
                parsedParameters.put("Reliab. class", getIntegerValue(parameters, 5));
                parsedParameters.put("Min avg. TPut", getIntegerValue(parameters, 6));
                parsedParameters.put("Min peak TPut", getIntegerValue(parameters, 7));
                parsedParameters.put("Min delay", getIntegerValue(parameters, 8));
                parsedParameters.put("Min priority class", getIntegerValue(parameters, 9));
                parsedParameters.put("Min reliability", getIntegerValue(parameters, 10));
                parsedParameters.put("Traffic class", getIntegerValue(parameters, 11));
                parsedParameters.put("Max UL TPut", getIntegerValue(parameters, 12));
                parsedParameters.put("Max DL TPut", getIntegerValue(parameters, 13));
                parsedParameters.put("Gr. UL TPut", getIntegerValue(parameters, 14));
                parsedParameters.put("Gr. DL TPut", getIntegerValue(parameters, 15));
                parsedParameters.put("Deliv. order", getIntegerValue(parameters, 16));
                parsedParameters.put("Max SDU size", getIntegerValue(parameters, 17));
                parsedParameters.put("SDU err. ratio", getStringValue(parameters, 18));
                parsedParameters.put("Residual BER", getStringValue(parameters, 19));
                parsedParameters.put("Deliv. err. PDU", getIntegerValue(parameters, 20));
                parsedParameters.put("Transfer delay", getIntegerValue(parameters, 21));
                parsedParameters.put("Traffic prior.", getIntegerValue(parameters, 22));
                parsedParameters.put("Min tr. class", getIntegerValue(parameters, 23));
                parsedParameters.put("Min max UL TPut", getIntegerValue(parameters, 24));
                parsedParameters.put("Min max DL TPut", getIntegerValue(parameters, 25));
                parsedParameters.put("Min gr. UL TPut", getIntegerValue(parameters, 26));
                parsedParameters.put("Min gr. DL TPut", getIntegerValue(parameters, 27));
                parsedParameters.put("Min deliv. ord.", getIntegerValue(parameters, 28));
                parsedParameters.put("Min max SDU size", getIntegerValue(parameters, 29));
                parsedParameters.put("Min SDU err.", getStringValue(parameters, 30));
                parsedParameters.put("Min resid. BER", getStringValue(parameters, 31));
                parsedParameters.put("Min del err PDU", getIntegerValue(parameters, 32));
                parsedParameters.put("Min train. delay", getIntegerValue(parameters, 33));
                parsedParameters.put("Min tr. priority", getIntegerValue(parameters, 34));
            }
            return parsedParameters;
        }
    },
    QSPN("QSPN") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Packet session context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Avg. TPut class";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Peak TPut class";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "Delay class";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
                key = "Priority class";
                parsedParameters.put(key, getIntegerValue(parameters, 5));
                key = "Reliab. class";
                parsedParameters.put(key, getIntegerValue(parameters, 6));
                key = "Traffic class";
                parsedParameters.put(key, getIntegerValue(parameters, 7));
                key = "Max UL TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 8));
                key = "Max DL TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 9));
                key = "Gr. UL TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 10));
                key = "Gr. DL TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 11));
                key = "Deliv. order";
                parsedParameters.put(key, getIntegerValue(parameters, 12));
                key = "Max SDU size";
                parsedParameters.put(key, getIntegerValue(parameters, 13));
                key = "SDU err. ratio";
                parsedParameters.put(key, getStringValue(parameters, 14));
                key = "Resid. BER";
                parsedParameters.put(key, getStringValue(parameters, 15));
                key = "Deliv. err. SDU";
                parsedParameters.put(key, getIntegerValue(parameters, 16));
                key = "Transf. delay";
                parsedParameters.put(key, getIntegerValue(parameters, 17));
                key = "THP";
                parsedParameters.put(key, getIntegerValue(parameters, 18));
            } else if ("1.86".equals(version)) {
                String key = "Packet tech";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Avg. TPut class";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Peak TPut class";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Delay class";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "Priority class";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
                key = "Reliab. class";
                parsedParameters.put(key, getIntegerValue(parameters, 5));
                key = "Traffic class";
                parsedParameters.put(key, getIntegerValue(parameters, 6));
                key = "Max UL TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 7));
                key = "Max DL TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 8));
                key = "Gr. UL TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 9));
                key = "Gr. DL TPut";
                parsedParameters.put(key, getIntegerValue(parameters, 10));
                key = "Deliv. order";
                parsedParameters.put(key, getIntegerValue(parameters, 11));
                key = "Max SDU size";
                parsedParameters.put(key, getIntegerValue(parameters, 12));
                key = "SDU err. ratio";
                parsedParameters.put(key, getStringValue(parameters, 13));
                key = "Resid. BER";
                parsedParameters.put(key, getStringValue(parameters, 14));
                key = "Deliv. err. SDU";
                parsedParameters.put(key, getIntegerValue(parameters, 15));
                key = "Transf. delay";
                parsedParameters.put(key, getIntegerValue(parameters, 16));
                key = "THP";
                parsedParameters.put(key, getIntegerValue(parameters, 17));
            }
            return parsedParameters;

        }
    },

    GAA("GAA") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            // if ("1.86".equals(version)) {
            // }
            return parsedParameters;
        }
    },
    PCHI("PCHI") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Attach context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
            } else if ("1.86".equals(version)) {
                String key = "Packet tech";
                final Integer packTech = getIntegerValue(parameters, 0);
                parsedParameters.put(key, packTech);
                if (packTech == 1 || packTech == 2) {
                    parsedParameters.put("Packet state", getIntegerValue(parameters, 1));
                    parsedParameters.put("Rac", getIntegerValue(parameters, 2));
                    parsedParameters.put("Radio priority", getIntegerValue(parameters, 3));
                    parsedParameters.put("Priority acc. th.", getIntegerValue(parameters, 4));
                    parsedParameters.put("Split PG cycle", getIntegerValue(parameters, 5));
                    parsedParameters.put("PS coding UL", getIntegerValue(parameters, 6));
                    parsedParameters.put("PS coding DL", getIntegerValue(parameters, 7));
                    parsedParameters.put("#PS TSL UL", getIntegerValue(parameters, 8));
                    parsedParameters.put("#PS TSL DL", getIntegerValue(parameters, 9));
                    parsedParameters.put("PS TNs UL", getIntegerValue(parameters, 10));
                    parsedParameters.put("PS TNs DL", getIntegerValue(parameters, 11));
                    parsedParameters.put("NW operation", getIntegerValue(parameters, 12));
                    parsedParameters.put("Network crtl. order", getIntegerValue(parameters, 13));
                    parsedParameters.put("IR status UL", getIntegerValue(parameters, 14));
                    parsedParameters.put("PBCCH", getIntegerValue(parameters, 15));
                    parsedParameters.put("CLRS hyst.", getIntegerValue(parameters, 16));
                    parsedParameters.put("CLRS time", getIntegerValue(parameters, 17));
                } else if (packTech == 3 || packTech == 5) {
                    parsedParameters.put("Packet state", getIntegerValue(parameters, 1));
                    parsedParameters.put("Rac", getIntegerValue(parameters, 2));
                    parsedParameters.put("NW mode", getIntegerValue(parameters, 3));
                    parsedParameters.put("HSDPA UE categ.", getIntegerValue(parameters, 4));
                    parsedParameters.put("HS-DSCH SC", getIntegerValue(parameters, 5));
                    parsedParameters.put("#HS-SCCH", getIntegerValue(parameters, 6));
                    parsedParameters.put("Power offset", getFloatValue(parameters, 7));
                    parsedParameters.put("ACK/NACK repetitions", getIntegerValue(parameters, 8));
                    parsedParameters.put("H-RNTI", getIntegerValue(parameters, 9));
                } else if (packTech == 4) {
                    parsedParameters.put("Packet state", getIntegerValue(parameters, 1));
                } else if (packTech == 6) {
                    parsedParameters.put("Packet state", getIntegerValue(parameters, 1));
                    parsedParameters.put("Packet ch type", getIntegerValue(parameters, 2));
                    parsedParameters.put("Packet carrier", getIntegerValue(parameters, 3));
                } else if (packTech == 8) {
                    parsedParameters.put("Packet state", getIntegerValue(parameters, 1));
                    parsedParameters.put("RAC", getIntegerValue(parameters, 2));
                }
            }
            return parsedParameters;
        }
    },
    GAF("GAF") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Attach context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Attach fail";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Att. fail. cause";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
            } else if ("1.86".equals(version)) {
                parsedParameters.put("Attach fail", getIntegerValue(parameters, 0));
                parsedParameters.put("Att. fail time", getIntegerValue(parameters, 1));
                parsedParameters.put("Att. fail cause", getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    GAC("GAC") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Attach context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
            } else if ("1.86".equals(version)) {
                parsedParameters.put("Attach time", getIntegerValue(parameters, 0));
            }
            return parsedParameters;
        }
    },

    GAD("GAD") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Attach context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Detach status";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Detach cause";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "Detach time";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
            } else if ("1.86".equals(version)) {
                parsedParameters.put("Detach status", getIntegerValue(parameters, 0));
                parsedParameters.put("Att. duration", getIntegerValue(parameters, 1));
                parsedParameters.put("Detach cause", getIntegerValue(parameters, 2));
                parsedParameters.put("Detach time", getIntegerValue(parameters, 3));
            }
            return parsedParameters;
        }
    },
    BLER("BLER") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("BLER DL", getFloatValue(parameters, 0));
                parsedParameters.put("System", getIntegerValue(parameters, 1));
                parsedParameters.put("TrCh #blocks", getIntegerValue(parameters, 2));
                parsedParameters.put("TrCh #errors", getIntegerValue(parameters, 3));
                parsedParameters.put("#TrChs", getIntegerValue(parameters, 4));
                parsedParameters.put("Params/TrCh", getIntegerValue(parameters, 5));
                parsedParameters.put("TrCh ID", getIntegerValue(parameters, 6));
                parsedParameters.put("BLER", getFloatValue(parameters, 7));
                parsedParameters.put("#blocks", getIntegerValue(parameters, 8));
                parsedParameters.put("#errors", getIntegerValue(parameters, 9));
            }
            return parsedParameters;
        }
    },
    RDAS("RDAS") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("RLC rate UL", getIntegerValue(parameters, 0));
                parsedParameters.put("RLC rate DL", getIntegerValue(parameters, 1));
                parsedParameters.put("RLC retr. UL", getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    LDAS("LDAS") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("LLC rate UL", getIntegerValue(parameters, 0));
                parsedParameters.put("LLC rate DL", getIntegerValue(parameters, 1));
                parsedParameters.put("LLC retrans. UL", getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },

    RLCBLER("RLCBLER") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },

    RLCRATE("RLCRATE") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },

    LLCRATE("LLCRATE") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },

    RUA("RUA") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Routing area update context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "RAU type";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
            } else if ("1.86".equals(version)) {
                parsedParameters.put("RAU type", getIntegerValue(parameters, 0));
            }
            return parsedParameters;
        }
    },

    RUS("RUS") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Routing area update context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Old RAC";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Old LAC";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "RAC";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
                key = "LAC";
                parsedParameters.put(key, getIntegerValue(parameters, 5));
            } else if ("1.86".equals(version)) {
                parsedParameters.put("Old RAC", getIntegerValue(parameters, 0));
                parsedParameters.put("Old LAC", getIntegerValue(parameters, 1));
                parsedParameters.put("RAC", getIntegerValue(parameters, 2));
                parsedParameters.put("LAC", getIntegerValue(parameters, 3));
                parsedParameters.put("RAU time", getIntegerValue(parameters, 4));
            }
            return parsedParameters;
        }
    },

    RUF("RUF") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Routing area update context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Att. RAC";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Att. LAC";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "RAU fail cause";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
            } else if ("1.86".equals(version)) {
                parsedParameters.put("Att. RAC", getIntegerValue(parameters, 0));
                parsedParameters.put("Att. LAC", getIntegerValue(parameters, 1));
                parsedParameters.put("RAU fail time", getIntegerValue(parameters, 2));
                parsedParameters.put("RAU fail cause", getIntegerValue(parameters, 3));
            }
            return parsedParameters;
        }
    },

    TBFI("TBFI") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version) || "1.86".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "#Header params";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "TLLI";
                parsedParameters.put(key, getStringValue(parameters, 2));
                key = "#params/TBF";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "#UL TBFs";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
                key = "TFI";
                parsedParameters.put(key, getIntegerValue(parameters, 5));
                key = "RLC win.";
                parsedParameters.put(key, getIntegerValue(parameters, 6));
                key = "#DL TBFs";
                parsedParameters.put(key, getIntegerValue(parameters, 7));
                key = "TFI";
                parsedParameters.put(key, getIntegerValue(parameters, 8));
                key = "RLC win.";
                parsedParameters.put(key, getIntegerValue(parameters, 9));
            }
            return parsedParameters;

        }
    },

    TBFULE("TBFULE") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version) || "1.86".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "UL TBF est. cause";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "UL TBF est. type";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "UL TBF est. status";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "#UL TBF est. req";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
            }
            return parsedParameters;
        }
    },
    MACDAS("MACDAS") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("System", getIntegerValue(parameters, 0));
                parsedParameters.put("#MAC header params", getIntegerValue(parameters, 1));
                parsedParameters.put("#TRCH", getIntegerValue(parameters, 2));
                parsedParameters.put("#params/TRCH", getIntegerValue(parameters, 3));
                parsedParameters.put("TrCh ID", getIntegerValue(parameters, 4));
                parsedParameters.put("TRCH type", getIntegerValue(parameters, 5));
                parsedParameters.put("MAC bit rate DL", getIntegerValue(parameters, 6));
                parsedParameters.put("MAC block rate DL", getIntegerValue(parameters, 7));
                parsedParameters.put("HS-DSCH 1st", getFloatValue(parameters, 8));
                parsedParameters.put("HS-DSCH 2nd", getFloatValue(parameters, 9));
                parsedParameters.put("HS-DSCH 3rd", getFloatValue(parameters, 10));
            }
            return parsedParameters;

        }
    },
    MACRATE("MACRATE") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },

    MACBLER("MACBLER") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("System", getIntegerValue(parameters, 0));
                parsedParameters.put("#MAC header params", getIntegerValue(parameters, 1));
                parsedParameters.put("#TRCH", getIntegerValue(parameters, 2));
                parsedParameters.put("#params/TRCH", getIntegerValue(parameters, 3));
                parsedParameters.put("TrCh ID", getIntegerValue(parameters, 4));
                parsedParameters.put("TRCH type", getIntegerValue(parameters, 5));
                parsedParameters.put("#ACK/NACK", getIntegerValue(parameters, 6));
                parsedParameters.put("BLER", getFloatValue(parameters, 7));
            }
            return parsedParameters;
        }
    },

    AMRI("AMRI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                if (system == 1 || system == 2 || system == 3 || system == 22) {
                    parsedParameters.put("AMR init. mode", getIntegerValue(parameters, 1));
                    parsedParameters.put("AMR ICMI", getIntegerValue(parameters, 2));
                    parsedParameters.put("AMR TH1", getFloatValue(parameters, 3));
                    parsedParameters.put("AMR HYS1", getFloatValue(parameters, 4));
                    parsedParameters.put("AMR TH2", getFloatValue(parameters, 5));
                    parsedParameters.put("AMR HYS2", getFloatValue(parameters, 6));
                    parsedParameters.put("AMR TH3", getFloatValue(parameters, 7));
                    parsedParameters.put("AMR HYS3", getFloatValue(parameters, 8));
                    parsedParameters.put("#Active codecs", getIntegerValue(parameters, 9));
                    parsedParameters.put("AMR codecs", getIntegerValue(parameters, 10));
                } else if (system == 32) {
                    parsedParameters.put("AMR init. mode", getIntegerValue(parameters, 1));
                    parsedParameters.put("AMR ICMI", getIntegerValue(parameters, 2));
                    parsedParameters.put("AMR TH1", getFloatValue(parameters, 3));
                    parsedParameters.put("AMR HYS1", getFloatValue(parameters, 4));
                    parsedParameters.put("AMR TH2", getFloatValue(parameters, 5));
                    parsedParameters.put("AMR HYS2", getFloatValue(parameters, 6));
                    parsedParameters.put("AMR TH3", getFloatValue(parameters, 7));
                    parsedParameters.put("AMR HYS3", getFloatValue(parameters, 8));
                    parsedParameters.put("#Active codecs", getIntegerValue(parameters, 9));
                    parsedParameters.put("AMR codecs", getIntegerValue(parameters, 10));
                }
            }
            return parsedParameters;
        }
    },

    AMRS("AMRS") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer system = getIntegerValue(parameters, 0);
                parsedParameters.put("System", system);
                if (system == 1 || system == 2 || system == 3 || system == 22) {
                    parsedParameters.put("AMR mode UL", getIntegerValue(parameters, 1));
                    parsedParameters.put("AMR mode DL", getIntegerValue(parameters, 2));
                    parsedParameters.put("AMR mode cmd.", getIntegerValue(parameters, 3));
                    parsedParameters.put("AMR mode req.", getIntegerValue(parameters, 4));
                } else if (system == 32) {
                    parsedParameters.put("AMR init. mode", getIntegerValue(parameters, 1));
                    parsedParameters.put("AMR ICMI", getIntegerValue(parameters, 2));
                    parsedParameters.put("AMR TH1", getFloatValue(parameters, 3));
                    parsedParameters.put("AMR HYS1", getFloatValue(parameters, 4));
                    parsedParameters.put("AMR TH2", getFloatValue(parameters, 5));
                    parsedParameters.put("AMR HYS2", getFloatValue(parameters, 6));
                    parsedParameters.put("AMR TH3", getFloatValue(parameters, 7));
                    parsedParameters.put("AMR HYS3", getFloatValue(parameters, 8));
                    parsedParameters.put("#Active codecs", getIntegerValue(parameters, 9));
                    parsedParameters.put("AMR codecs", getIntegerValue(parameters, 10));
                }
                // TODO add for UMTS - dont understand how check
            }
            return parsedParameters;
        }
    },
    AMRQ("AMRQ") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version) || "1.86".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "AMR qual. est.";
                parsedParameters.put(key, getFloatValue(parameters, 0));
            }
            return parsedParameters;
        }
    },
    AQUL("AQUL") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "AQ type UL";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "AQ MOS";
                parsedParameters.put(key, getFloatValue(parameters, 1));
                key = "AQ sample file";
                parsedParameters.put(key, getStringValue(parameters, 2));
                key = "AQ ref. file";
                parsedParameters.put(key, getStringValue(parameters, 3));
                key = "AQ timestamp";
                parsedParameters.put(key, getStringValue(parameters, 4));
                key = "AQ sample duration UL";
                parsedParameters.put(key, getIntegerValue(parameters, 5));
                key = "AQ activity";
                parsedParameters.put(key, getFloatValue(parameters, 6));
                key = "AQ delay";
                parsedParameters.put(key, getFloatValue(parameters, 7));
                key = "AQ min delay";
                parsedParameters.put(key, getFloatValue(parameters, 8));
                key = "AQ max delay";
                parsedParameters.put(key, getFloatValue(parameters, 9));
                key = "AQ stdev delay";
                parsedParameters.put(key, getFloatValue(parameters, 10));
                key = "AQ SNR";
                parsedParameters.put(key, getFloatValue(parameters, 11));
                key = "AQ insertion gain";
                parsedParameters.put(key, getFloatValue(parameters, 12));
                key = "AQ noise gain";
                parsedParameters.put(key, getFloatValue(parameters, 13));
            } else if ("1.86".equals(version)) {
                parsedParameters.put("MOS type", getIntegerValue(parameters, 0));
                parsedParameters.put("AQ mean", getFloatValue(parameters, 1));
                parsedParameters.put("AQ file", getStringValue(parameters, 2));
                parsedParameters.put("AQ ref. file", getStringValue(parameters, 3));
                // timestamp already exist?
                parsedParameters.put("Timestamp_p", getStringValue(parameters, 4));
                parsedParameters.put("AQ duration", getIntegerValue(parameters, 5));
            }
            return parsedParameters;
        }
    },
    AQDL("AQDL") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "AQ type DL";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer aqType = getIntegerValue(parameters, 0);
                parsedParameters.put("AQ type", aqType);
                parsedParameters.put("AQ MOS", getFloatValue(parameters, 1));
                if (aqType == 1 || aqType == 2 || aqType == 3) {
                    parsedParameters.put("AQ sample", getStringValue(parameters, 2));
                    parsedParameters.put("AQ reference", getStringValue(parameters, 3));
                    parsedParameters.put("AQ timestamp", getStringValue(parameters, 4));
                    parsedParameters.put("AQ duration", getIntegerValue(parameters, 5));
                }
            }
            return parsedParameters;
        }
    },
    AQI("AQI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "AQ type DL";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "AQ type";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "AQ activity";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "AQ synch.";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
            } else if ("1.86".equals(version)) {
                parsedParameters.put("Test type", getIntegerValue(parameters, 0));
                parsedParameters.put("Activity status", getIntegerValue(parameters, 1));
                parsedParameters.put("AQ synch.", getIntegerValue(parameters, 2));
            }
            return parsedParameters;
        }
    },
    VQDL("VQDL") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Data transfer context ID";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                final Integer vqType = getIntegerValue(parameters, 0);
                parsedParameters.put("VQ type", vqType);
                parsedParameters.put("VQ MOS", getFloatValue(parameters, 1));
                if (vqType == 1) {
                    parsedParameters.put("VQ blockiness", getFloatValue(parameters, 2));
                    parsedParameters.put("VQ blurriness", getFloatValue(parameters, 3));
                    parsedParameters.put("VQ jerkiness", getFloatValue(parameters, 4));
                } else if (vqType == 2) {
                    parsedParameters.put("VQ jitter", getIntegerValue(parameters, 2));
                    parsedParameters.put("VQ PER", getFloatValue(parameters, 3));
                }
            }
            return parsedParameters;
        }
    },
    VDAS("VDAS") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                parsedParameters.put("System", getIntegerValue(parameters, 0));
                parsedParameters.put("Video protocol", getIntegerValue(parameters, 1));
                parsedParameters.put("Video rate UL", getIntegerValue(parameters, 2));
                parsedParameters.put("Video rate DL", getIntegerValue(parameters, 3));
                parsedParameters.put("Video frame rate UL", getIntegerValue(parameters, 4));
                parsedParameters.put("Video frame rate DL", getIntegerValue(parameters, 5));
                parsedParameters.put("Video FER", getFloatValue(parameters, 6));
            }
            return parsedParameters;
        }
    },
    VRATE("VRATE") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            }
            return parsedParameters;
        }
    },
    MSGA("MSGA") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Message type";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("System", getIntegerValue(parameters, 0));
                final Integer mesType = getIntegerValue(parameters, 1);
                parsedParameters.put("Message type", mesType);
                if (mesType == 1) {
                    parsedParameters.put("#Header params", getIntegerValue(parameters, 2));
                    parsedParameters.put("Msg. direction", getIntegerValue(parameters, 3));
                    parsedParameters.put("SMS msg. type", getIntegerValue(parameters, 4));
                    parsedParameters.put("SMS number", getStringValue(parameters, 5));
                    parsedParameters.put("SMS ser. center", getStringValue(parameters, 6));
                    parsedParameters.put("SMS coding sch.", getIntegerValue(parameters, 7));
                    parsedParameters.put("SMS seq number", getIntegerValue(parameters, 8));
                    parsedParameters.put("SMS msg. type", getIntegerValue(parameters, 9));
                    // TODO check type
                    parsedParameters.put("SMS msg. data", getStringValue(parameters, 10));
                } else {
                    parsedParameters.put("#Header params", getIntegerValue(parameters, 2));
                    parsedParameters.put("Msg. direction", getIntegerValue(parameters, 3));
                    parsedParameters.put("MMS msg. type", getIntegerValue(parameters, 4));
                    parsedParameters.put("MMS ser. center", getStringValue(parameters, 5));
                    parsedParameters.put("MMS tr. protocol", getIntegerValue(parameters, 6));
                    parsedParameters.put("MMS seq. number", getIntegerValue(parameters, 7));
                    parsedParameters.put("#MMS files", getIntegerValue(parameters, 8));
                    // TODO check type
                    parsedParameters.put("MMS filename", getStringValue(parameters, 9));
                }
            }
            return parsedParameters;
        }
    },
    MSGS("MSGS") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Message type";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("System", getIntegerValue(parameters, 0));
                final Integer mesType = getIntegerValue(parameters, 1);
                parsedParameters.put("Message type", mesType);
                if (mesType == 1) {
                    parsedParameters.put("Msg. direction", getIntegerValue(parameters, 2));
                    parsedParameters.put("Ref. number", getIntegerValue(parameters, 3));
                    parsedParameters.put("SMS seq number", getIntegerValue(parameters, 4));
                    parsedParameters.put("SMS msg. type", getIntegerValue(parameters, 5));
                } else {
                    parsedParameters.put("Msg. direction", getIntegerValue(parameters, 2));
                    parsedParameters.put("MMS msg. ID", getStringValue(parameters, 3));
                    parsedParameters.put("MMS seq number", getIntegerValue(parameters, 4));
                    parsedParameters.put("MMS msg. type", getIntegerValue(parameters, 5));
                }
            }
            return parsedParameters;
        }
    },
    MSGF("MSGF") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Message type";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("System", getIntegerValue(parameters, 0));
                final Integer mesType = getIntegerValue(parameters, 1);
                parsedParameters.put("Message type", mesType);
                if (mesType == 1) {
                    parsedParameters.put("Msg. direction", getIntegerValue(parameters, 2));
                    parsedParameters.put("SMS cause", getIntegerValue(parameters, 3));
                    parsedParameters.put("SMS seq number", getIntegerValue(parameters, 4));
                    parsedParameters.put("SMS msg. type", getIntegerValue(parameters, 5));
                } else {
                    parsedParameters.put("Msg. direction", getIntegerValue(parameters, 2));
                    parsedParameters.put("MMS cause", getIntegerValue(parameters, 3));
                    parsedParameters.put("MMS seq number", getIntegerValue(parameters, 4));
                    parsedParameters.put("MMS msg. type", getIntegerValue(parameters, 5));
                }
            }
            return parsedParameters;
        }
    },
    PTTA("PTTA") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "PTT tech.";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("PTT type", getStringValue(parameters, 0));
                parsedParameters.put("POC server", getStringValue(parameters, 1));
            }
            return parsedParameters;
        }
    },
    PTTF("PTTF") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "PTT tech.";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("PTT type", getStringValue(parameters, 0));
                parsedParameters.put("Fail status", getIntegerValue(parameters, 1));
                parsedParameters.put("Fail time", getIntegerValue(parameters, 2));
                parsedParameters.put("Fail cause", getIntegerValue(parameters, 3));
            }

            return parsedParameters;
        }
    },
    PTTC("PTTC") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "PTT tech.";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("PTT type", getStringValue(parameters, 0));
                parsedParameters.put("Act. time", getIntegerValue(parameters, 1));
                parsedParameters.put("Login time", getIntegerValue(parameters, 2));
                parsedParameters.put("Group attach time", getIntegerValue(parameters, 3));
                parsedParameters.put("POC server", getStringValue(parameters, 4));
            }
            return parsedParameters;
        }
    },
    PTTD("PTTD") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "PTT tech.";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("PTT type", getStringValue(parameters, 0));
                parsedParameters.put("Deact. status", getIntegerValue(parameters, 1));
                parsedParameters.put("Duration", getIntegerValue(parameters, 2));
                parsedParameters.put("Deact. cause", getIntegerValue(parameters, 3));
                parsedParameters.put("Deact. time", getIntegerValue(parameters, 4));
            }
            return parsedParameters;
        }
    },

    PTTI("PTTI") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "System";

                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "PTT tech.";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("System", getIntegerValue(parameters, 0));
                final Integer pTech = getIntegerValue(parameters, 1);
                parsedParameters.put("PTT tech.", pTech);
                if (pTech == 1) {
                    parsedParameters.put("PTT state", getIntegerValue(parameters, 2));
                    parsedParameters.put("PTT user identify", getStringValue(parameters, 3));
                    parsedParameters.put("PTT status", getIntegerValue(parameters, 4));
                } else {
                    parsedParameters.put("PTT state", getIntegerValue(parameters, 2));
                    parsedParameters.put("PTT comm. type", getIntegerValue(parameters, 3));
                    parsedParameters.put("PTT user identity", getStringValue(parameters, 4));
                }
            }
            return parsedParameters;

        }
    },
    RTPJITTER("RTPJITTER") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "RTP jitter type";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "RTP jitter DL";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "RTP jitter UL";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "RTP interarr. DL";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "RTP interarr. UL";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
                // TODO add parsing parameters if necessary
            } else if ("1.86".equals(version)) {
                parsedParameters.put("RTP jitter DL", getIntegerValue(parameters, 0));
                parsedParameters.put("RTP jitter UL", getIntegerValue(parameters, 1));
                parsedParameters.put("RTP IAT DL", getIntegerValue(parameters, 2));
                parsedParameters.put("RTP IAT UL", getIntegerValue(parameters, 3));
            }
            return parsedParameters;
        }
    },
    GPS("GPS") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Lon.";
                parsedParameters.put(key, getFloatValue(parameters, 0));
                key = "Lat.";
                parsedParameters.put(key, getFloatValue(parameters, 1));
                key = "Height";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                key = "Distance";
                parsedParameters.put(key, getIntegerValue(parameters, 3));
                key = "GPS fix";
                parsedParameters.put(key, getIntegerValue(parameters, 4));
                key = "Satellites";
                parsedParameters.put(key, getIntegerValue(parameters, 5));
                key = "Velocity";
                parsedParameters.put(key, getIntegerValue(parameters, 6));
            }
            return parsedParameters;
        }
    },
    TNOTE("TNOTE") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version) || "1.86".equals(version)) {
                String key = "TNote";
                parsedParameters.put(key, getStringValue(parameters, 0));
            }
            return parsedParameters;
        }
    },
    VNOTE("VNOTE") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("1.86".equals(version)) {
                String key = "VNote";
                parsedParameters.put(key, getStringValue(parameters, 0));
            }
            return parsedParameters;
        }
    },
    QNOTE("QNOTE") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "ID";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Parent ID";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Question";
                parsedParameters.put(key, getStringValue(parameters, 2));
                key = "Answer";
                parsedParameters.put(key, getStringValue(parameters, 3));
                key = "Description";
                parsedParameters.put(key, getStringValue(parameters, 4));
            }
            return parsedParameters;

        }
    },
    QTRIGGER("QTRIGGER") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "Description";
                parsedParameters.put(key, getStringValue(parameters, 0));
            }
            return parsedParameters;
        }
    },
    MARK("MARK") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version) || "1.86".equals(version)) {
                String key = "Marker seq.#";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Marker#";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
            }
            return parsedParameters;
        }
    },
    ERR("ERR") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version) || "1.86".equals(version)) {
                String key = "Error";
                parsedParameters.put(key, getStringValue(parameters, 0));
            }
            return parsedParameters;
        }
    },
    DATE("DATE") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version) || "1.86".equals(version)) {
                String key = "Date";
                parsedParameters.put(key, getStringValue(parameters, 0));
            }
            return parsedParameters;
        }
    },
    PAUSE("PAUSE") {

        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            return parsedParameters;
        }
    },
    APP("APP") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version) || "1.86".equals(version)) {
                String key = "Ext. app. state";
                parsedParameters.put(key, getStringValue(parameters, 0));
                key = "#Ext. app. launch";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "Ext. app. name";
                parsedParameters.put(key, getStringValue(parameters, 2));
                key = "Ext. app. params";
                parsedParameters.put(key, getStringValue(parameters, 3));
            }
            return parsedParameters;
        }
    },
    LOCK("LOCK") {
        @Override
        public Map<String, Object> fill(String version, List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            if ("2.01".equals(version)) {
                String key = "#Forcings";
                parsedParameters.put(key, getIntegerValue(parameters, 0));
                key = "Lock type";
                parsedParameters.put(key, getIntegerValue(parameters, 1));
                key = "#Params";
                parsedParameters.put(key, getIntegerValue(parameters, 2));
                // TODO add parse parameters
            } else if ("1.86".equals(version)) {
                parsedParameters.put("#Locks", getIntegerValue(parameters, 0));
                final Integer lockType = getIntegerValue(parameters, 1);
                parsedParameters.put("Lock type", lockType);
                if (lockType == 1) {
                    parsedParameters.put("#parameters", getIntegerValue(parameters, 2));
                    parsedParameters.put("Ch. lock channel", getIntegerValue(parameters, 3));
                    parsedParameters.put("Ch. lock system", getIntegerValue(parameters, 4));
                } else if (lockType == 2) {
                    parsedParameters.put("#parameters", getIntegerValue(parameters, 2));
                    parsedParameters.put("Sector lock SCR", getIntegerValue(parameters, 3));
                    parsedParameters.put("Sector lock ch.", getIntegerValue(parameters, 4));
                } else if (lockType == 3) {
                    parsedParameters.put("#parameters", getIntegerValue(parameters, 2));
                    parsedParameters.put("System lock system", getIntegerValue(parameters, 3));
                } else if (lockType == 4) {
                    parsedParameters.put("#parameters", getIntegerValue(parameters, 2));
                    parsedParameters.put("Band lock band", getIntegerValue(parameters, 3));
                } else if (lockType == 5) {
                    parsedParameters.put("#parameters", getIntegerValue(parameters, 2));
                    parsedParameters.put("Cell barring state", getIntegerValue(parameters, 3));
                } else if (lockType == 6) {
                    parsedParameters.put("#parameters", getIntegerValue(parameters, 2));
                }
            }
            return parsedParameters;
        }
    };
    private static HashMap<String, NemoEvents> eventsList = new HashMap<String, NemoEvents>();
    static {
        for (NemoEvents event : NemoEvents.values()) {
            eventsList.put(event.eventId, event);
        }
    }
    private final String eventId;

    // protected Set<String> statisticKeys;

    /**
     * 
     */
    private NemoEvents(String eventId) {
        this.eventId = eventId;
    }

    public static NemoEvents getEventById(String id) {
        return eventsList.get(id);
    }

    // public abstract Map<String, Object> fill(List<String> parameters);

    public abstract Map<String, Object> fill(String version, List<String> parameters);

    // {
    // return fill(parameters);
    // }
    /**
     * @param parameters2
     * @param i
     * @return
     */
    protected static Integer getIntegerValue(List<String> parameters, int i) {
        if (parameters == null || parameters.size() <= i || i < 0) {
            return null;
        }
        String value = parameters.get(i);
        if (value.isEmpty()) {
            return null;
        }

        return Integer.parseInt(value);
    }

    /**
     * @param parameters2
     * @param i
     * @return
     */
    protected static String getStringValue(List<String> parameters, int i) {
        if (parameters == null || parameters.size() <= i || i < 0) {
            return null;
        }
        return parameters.get(i);
    }

    /**
     * @param parameters2
     * @param i
     * @return
     */
    protected static Float getFloatValue(List<String> parameters, int i) {
        if (parameters == null || parameters.size() <= i || i < 0) {
            return null;
        }
        String value = parameters.get(i);
        if (value.isEmpty()) {
            return null;
        }

        return Float.parseFloat(value);
    }
}
