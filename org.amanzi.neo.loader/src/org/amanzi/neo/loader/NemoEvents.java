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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Nemo events ver. 2.1
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum NemoEvents {

    AG("#AG"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "AG";
            Float value = getFloatValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    BF("#BF") {
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "BTS_file";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    CInf ("#CI"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Converter name";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            key = "Converter version";
            value = getStringValue(parameters, 1);
            parsedParameters.put(key, value);
            key = "Converter file";
            value = getStringValue(parameters, 2);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    CL ("#CL"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "CL";
            Float value = getFloatValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    DL ("#DL"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Device label";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    DN("#DN"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Device name";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    DS ("#DS"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Number of supported systems";
            Integer value = getIntegerValue(parameters, 0);
            parsedParameters.put(key, value);
            key = "Supported systems";
            value = getIntegerValue(parameters, 1);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    DT ("#DT"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Device type";
            Integer value = getIntegerValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    FF ("#FF"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "File format version";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    EI ("#EI"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Device identity";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    HV ("#HV"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Handler version";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    HW ("#HW"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Hardware version";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    ID ("#ID"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Measurement ID";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    MF ("#MF"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Map file";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    ML ("#ML"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Measurement label";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    NN ("#NN"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Network name";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    PC ("#PC"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Packet capture state";
            Integer value = getIntegerValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
   PRODUCT ("#PRODUCT"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Product name";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            key = "Product version";
            value = getStringValue(parameters, 1);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    SI ("#SI"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Subscriber identity";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    SP ("#SP"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Subscriber phone number";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    SW ("#SW"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Device software version";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    TS ("#TS"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Test script file";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    UT ("#UT"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Gap to UTC";
            Integer value = getIntegerValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    VQ ("#VQ"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "VQ type";
            Integer value = getIntegerValue(parameters, 0);
            parsedParameters.put(key, value);
            key = "VQ version";
            String valueStr = getStringValue(parameters,1);
            parsedParameters.put(key, valueStr);
            return parsedParameters;
        }
    },
    START ("#START"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Date";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    STOP ("#STOP"){
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Date";
            String value = getStringValue(parameters, 0);
            parsedParameters.put(key, value);
            return parsedParameters;
        }
    },
    CAA ("CAA"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Call context ID");
            statisticKeys.add("System");
            statisticKeys.add("Call type");
            statisticKeys.add("Direction");
            statisticKeys.add("Number");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Call context ID";
            parsedParameters.put(key, getStringValue(parameters, 0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Call type";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "Direction";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "Number";
            parsedParameters.put(key, getStringValue(parameters,4));
            return parsedParameters;
        }
    },
    CAC ("CAC"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Call context ID");
            statisticKeys.add("System");
            statisticKeys.add("Call type");
            statisticKeys.add("Call status");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Call context ID";
            parsedParameters.put(key, getStringValue(parameters, 0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Call type";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "Call status";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "Parameters";
            parsedParameters.put(key, getStringValue(parameters,4));
            //TODO parse parameters if necessary
            return parsedParameters;
        }
    },
    CAF ("CAF"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Call context ID");
            statisticKeys.add("System");
            statisticKeys.add("Call type");
            statisticKeys.add("CS fail. status");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Call context ID";
            parsedParameters.put(key, getStringValue(parameters, 0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Call type";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "CS fail. status";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            //TODO parse parameters if necessary
            return parsedParameters;
        }
    },
    CAD ("CAD"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Call context ID");
            statisticKeys.add("System");
            statisticKeys.add("Call type");
            statisticKeys.add("CS disc. status");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Call context ID";
            parsedParameters.put(key, getStringValue(parameters, 0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Call type";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "CS disc. status";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            //TODO parse parameters if necessary
            return parsedParameters;
        }
    },
    VCHI ("VCHI"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO parse parameters if necessary
            return parsedParameters;
        }
    },
    DAA ("DAA"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Data connection context ID");
            statisticKeys.add("Packet session context ID");
            statisticKeys.add("Call context ID");
            statisticKeys.add("Application protocol");
            statisticKeys.add("Host address");
            statisticKeys.add("Host port");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Data connection context ID";
            parsedParameters.put(key, getStringValue(parameters, 0));
            key = "Packet session context ID";
            parsedParameters.put(key, getStringValue(parameters, 1));
            key = "Call context ID";
            parsedParameters.put(key, getStringValue(parameters, 2));
            key = "Application protocol";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "Host address";
            parsedParameters.put(key, getStringValue(parameters,4));
            key = "Host port";
            parsedParameters.put(key, getIntegerValue(parameters,5));
            return parsedParameters;
        }
    },
    DAC ("DAC"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Data connection context ID");
            statisticKeys.add("Application protocol");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Data connection context ID";
            parsedParameters.put(key, getStringValue(parameters, 0));
            key = "Application protocol";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            return parsedParameters;
        }
    },
    DAF ("DAF"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Data connection context ID");
            statisticKeys.add("Application protocol");
            statisticKeys.add("Data fail. status");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Data connection context ID";
            parsedParameters.put(key, getStringValue(parameters, 0));
            key = "Application protocol";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Data fail. status";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    DAD ("DAD"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Data connection context ID");
            statisticKeys.add("Application protocol");
            statisticKeys.add("Data disc. status");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Data connection context ID";
            parsedParameters.put(key, getStringValue(parameters, 0));
            key = "Application protocol";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Data disc. status";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    DREQ ("DREQ"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Data transfer context ID");
            statisticKeys.add("Data connection context ID");
            statisticKeys.add("Application protocol");
            statisticKeys.add("Transf. dir.");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Data transfer context ID";
            parsedParameters.put(key, getStringValue(parameters, 0));
            key = "Data connection context ID";
            parsedParameters.put(key, getStringValue(parameters, 1));
            key = "Application protocol";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "Transf. dir.";
            parsedParameters.put(key, getIntegerValue(parameters,3));
          //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    DCOMP ("DCOMP"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Data transfer context ID");
            statisticKeys.add("Application protocol");
            statisticKeys.add("Transf. status");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Data transfer context ID";
            parsedParameters.put(key, getStringValue(parameters, 0));
            key = "Application protocol";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Transf. status";
            parsedParameters.put(key, getIntegerValue(parameters,2));
          //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    DRATE ("DRATE"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Data transfer context ID");
            statisticKeys.add("Application protocol");
            statisticKeys.add("App. rate UL");
            statisticKeys.add("App. rate DL");
            statisticKeys.add("Bytes UL");
            statisticKeys.add("Bytes DL");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Data transfer context ID";
            parsedParameters.put(key, getStringValue(parameters, 0));
            key = "Application protocol";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "App. rate UL";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "App. rate DL";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "Bytes UL";
            parsedParameters.put(key, getIntegerValue(parameters,4));
            key = "Bytes DL";
            parsedParameters.put(key, getIntegerValue(parameters,5));
            return parsedParameters;
        }
    },
    PER ("PER"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Data transfer context ID");
            statisticKeys.add("Application protocol");
            statisticKeys.add("PER UL");
            statisticKeys.add("PER DL");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Data transfer context ID";
            parsedParameters.put(key, getStringValue(parameters, 0));
            key = "Application protocol";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "PER UL";
            parsedParameters.put(key, getFloatValue(parameters, 2));
            key = "PER DL";
            parsedParameters.put(key, getFloatValue(parameters, 3));
            return parsedParameters;
        }
    },
    RTT ("RTT"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Data transfer context ID");
            statisticKeys.add("Application protocol");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Data transfer context ID";
            parsedParameters.put(key, getStringValue(parameters, 0));
            key = "Application protocol";
            parsedParameters.put(key, getIntegerValue(parameters,1));
          //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    JITTER ("JITTER"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Data transfer context ID");
            statisticKeys.add("Application protocol");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Data transfer context ID";
            parsedParameters.put(key, getStringValue(parameters, 0));
            key = "Application protocol";
            parsedParameters.put(key, getIntegerValue(parameters,1));
          //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    DSS ("DSS"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Application protocol");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Application protocol";
            parsedParameters.put(key, getIntegerValue(parameters,0));
          //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    DCONTENT ("DCONTENT"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Application protocol");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Application protocol";
            parsedParameters.put(key, getIntegerValue(parameters,0));
          //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    CELLMEAS ("CELLMEAS"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    ADJMEAS ("ADJMEAS"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    RXQ ("RXQ"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    PRXQ ("PRXQ"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    FER ("FER"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    MSP ("MSP"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    RLT ("RLT"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    TAD ("TAD"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    DSC ("DSC"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    BEP ("BEP"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    CI ("CI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    TXPC ("TXPC"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    RXPC ("RXPC"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    BER ("BER"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    PHRATE ("PHRATE"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    WLANRATE ("WLANRATE"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            statisticKeys.add("WLAN rate UL");
            statisticKeys.add("WLAN rate DL");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "WLAN rate UL";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "WLAN rate DL";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            return parsedParameters;
        }
    },
    PPPRATE ("PPPRATE"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("PPP rate UL");
            statisticKeys.add("PPP rate DL");
            statisticKeys.add("Sent PPP bytes");
            statisticKeys.add("Recv. PPP bytes");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "PPP rate UL";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "PPP rate DL";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Sent PPP bytes";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "Recv. PPP bytes";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            return parsedParameters;
        }
    },
    RLPRATE ("RLPRATE"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    RLPSTATISTICS ("RLPSTATISTICS"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    MEI ("MEI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    CQI ("CQI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    HARQI ("HARQI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    HSSCCHI ("HSSCCHI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    PLAID ("PLAID"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },
    PLAIU ("PLAIU"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    HBI ("HBI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },    
    MACERATE ("MACERATE"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    }, 
    AGRANT ("AGRANT"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    }, 
    SGRANT ("SGRANT"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    EDCHI ("EDCHI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },    
    HSUPASI ("HSUPASI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    DRCI ("DRCI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            statisticKeys.add("#Header params");
            statisticKeys.add("Sample duration");
            statisticKeys.add("#DRC sets");
            statisticKeys.add("#params/DRC set");
            statisticKeys.add("Percentage");
            statisticKeys.add("Requested rate");
            statisticKeys.add("Packet length");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "#Header params";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Sample duration";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "#DRC sets";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "#params/DRC set";
            parsedParameters.put(key, getIntegerValue(parameters,4));
            key = "Percentage";
            parsedParameters.put(key, getFloatValue(parameters, 5));
            key = "Requested rate";
            parsedParameters.put(key, getIntegerValue(parameters,6));
            key = "Packet length";
            parsedParameters.put(key, getIntegerValue(parameters,7));
            return parsedParameters;

        }
    },   
    RDRC ("RDRC"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    FDRC ("FDRC"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    PHFER ("PHFER"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    MARKOVMUX ("MARKOVMUX"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    MARKOVSTATS ("MARKOVSTATS"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            statisticKeys.add("#Header params");
            statisticKeys.add("M FER");
            statisticKeys.add("#expected values");
            statisticKeys.add("#Params");
            statisticKeys.add("M expected");
            statisticKeys.add("M 1/1");
            statisticKeys.add("M 1/2");
            statisticKeys.add("M 1/4");
            statisticKeys.add("M 1/8");
            statisticKeys.add("M erasures");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "#Header params";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "M FER";
            parsedParameters.put(key, getFloatValue(parameters, 2));
            key = "#expected values";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "#Params";
            parsedParameters.put(key, getIntegerValue(parameters,4));
            key = "M expected";
            parsedParameters.put(key, getFloatValue(parameters, 5));
            key = "M 1/1";
            parsedParameters.put(key, getIntegerValue(parameters,6));
            key = "M 1/2";
            parsedParameters.put(key, getIntegerValue(parameters,7));
            key = "M 1/4";
            parsedParameters.put(key, getIntegerValue(parameters,8));
            key = "M 1/8";
            parsedParameters.put(key, getIntegerValue(parameters,9));
            key = "M erasures";
            parsedParameters.put(key, getIntegerValue(parameters,10));
            return parsedParameters;

        }
    },   
    MER ("MER"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    DVBI ("DVBI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    DVBFER ("DVBFER"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    DVBBER ("DVBBER"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    DVBRXL ("DVBRXL"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    }, 
    DVBRATE ("DVBRATE"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    }, 
    FREQSCAN ("FREQSCAN"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },    
    SPECTRUMSCAN ("SPECTRUMSCAN"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Scanning mode");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Scanning mode";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },    
    PILOTSCAN ("PILOTSCAN"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    OFDMSCAN ("OFDMSCAN"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    TPROFSCAN ("PPPRATE"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    DPROFSCAN ("DPROFSCAN"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    FINGER ("FINGER"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },    
    UISCAN ("UISCAN"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    CELLSCAN ("CELLSCAN"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    HOA ("HOA"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Handover context ID");
            statisticKeys.add("#Header params");
            statisticKeys.add("HOA type");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Handover context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "#Header params";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "HOA type";
            parsedParameters.put(key, getIntegerValue(parameters,2));
          //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },    
    HOS ("HOS"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Handover context ID");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Handover context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            return parsedParameters;
        }
    },    
    HOF ("HOF"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Handover context ID");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Handover context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
          //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    CREL ("CREL"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Handover context ID");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Handover context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
          //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    }, 
    SHO ("SHO"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    LUA ("LUA"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Location area update context ID");
            statisticKeys.add("System");
            statisticKeys.add("LAU type");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Location area update context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "LAU type";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            return parsedParameters;
        }
    },  
    LUS ("LUS"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Location area update context ID");
            statisticKeys.add("System");
            statisticKeys.add("Old LAC");
            statisticKeys.add("LAC");
            statisticKeys.add("MCC");
            statisticKeys.add("MNC");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Location area update context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Old LAC";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "LAC";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "MCC";
            parsedParameters.put(key, getIntegerValue(parameters,4));
            key = "MNC";
            parsedParameters.put(key, getIntegerValue(parameters,5));
            return parsedParameters;
        }
    },    
    LUF ("LUF"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Location area update context ID");
            statisticKeys.add("System");
            statisticKeys.add("LUF status");
            statisticKeys.add("Old LAC");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Location area update context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "LUF status";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "Old LAC";
            parsedParameters.put(key, getIntegerValue(parameters,3));
          //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    CHI ("CHI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    GANCHI ("GANCHI"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    SEI ("SEI"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            statisticKeys.add("Service status");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "Service status";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    }, 
    ROAM ("ROAM"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            statisticKeys.add("Roaming status");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "Roaming status";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            return parsedParameters;
        }
    },   
    DCHR ("DCHR"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    DCHI ("DCHI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    HOP ("HOP"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    NMISS ("NMISS"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("#Header params");
            statisticKeys.add("Source system");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "#Header params";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "Source system";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },    
    NLIST ("NLIST"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("#Header params");
            statisticKeys.add("Source system");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "#Header params";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "Source system";
            parsedParameters.put(key, getIntegerValue(parameters,1));
          //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },    
    SERVCONF ("SERVCONF"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    RACHI ("RACHI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    VOCS ("VOCS"){
        
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            statisticKeys.add("Voc. rate For.");
            statisticKeys.add("Voc. rate Rev.");
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "Voc. rate For.";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Voc. rate Rev.";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            return parsedParameters;

        }
    },   
    PHCHI ("PHCHI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    QPCHI ("QPCHI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    FCHPACKETS ("FCHPACKETS"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    }, 
    CONNECTIONC ("CONNECTIONC"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    }, 
    CONNECTIOND ("CONNECTIOND"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    }, 
    SESSIONC ("SESSIONC"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    }, 
    RBI ("RBI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            statisticKeys.add("#Header params");
            statisticKeys.add("#params/RB");
            statisticKeys.add("#RBs");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "#Header params";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "#params/RB";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "#RBs";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    TRCHI ("TRCHI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            statisticKeys.add("#Header params");
            statisticKeys.add("#params/TRCH");
            statisticKeys.add("#TrChs");

        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "#Header params";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "#params/TRCH";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "#TrChs";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    RRA ("RRA"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("RRC context ID");
            statisticKeys.add("System");
            
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "RRC context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    RRC ("RRC"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("RRC context ID");
            statisticKeys.add("System");
            
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "RRC context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    }, 
    RRF ("RRF"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("RRC context ID");
            statisticKeys.add("System");
            
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "RRC context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    }, 
    RRD ("RRD"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("RRC context ID");
            statisticKeys.add("System");


        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "RRC context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    }, 
    
    CIPI ("CIPI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    L3SM ("L3SM"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    L2SM ("L2SM"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    RRCSM ("RRCSM"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    RLCSM ("RLCSM"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    MACSM ("MACSM"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    LLCSM ("LLCSM"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    SNPSM ("SNPSM"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    RRLPSM ("RRLPSM"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    GANSM ("GANSM"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    SIPSM ("SIPSM"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    
    RTPSM ("RTPSM"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    
    PAA ("PAA"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Packet session context ID");
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Packet session context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    PAF ("PAF"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Packet session context ID");
            statisticKeys.add("System");
            statisticKeys.add("Fail status");
            statisticKeys.add("Deact. cause");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Packet session context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Fail status";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "Deact. cause";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    PAC ("PAC"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Packet session context ID");
            statisticKeys.add("System");
            statisticKeys.add("Packet act. state");
            statisticKeys.add("IP");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Packet session context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Packet act. state";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "IP";
            parsedParameters.put(key, getStringValue(parameters,3));
            return parsedParameters;
        }
    },   
    
    PAD ("PAD"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Packet session context ID");
            statisticKeys.add("System");
            statisticKeys.add("Deact. status");
            statisticKeys.add("Deact. cause");
            statisticKeys.add("Deact. time");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Packet session context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Deact. status";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "Deact. cause";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "Deact. time";
            parsedParameters.put(key, getIntegerValue(parameters,4));
            return parsedParameters;
        }
    },   
    
    QSPR ("QSPR"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Packet session context ID");
            statisticKeys.add("System");
            statisticKeys.add("Avg. TPut class");
            statisticKeys.add("Peak TPut class");
            statisticKeys.add("Delay class");
            statisticKeys.add("Priority class");
            statisticKeys.add("Reliab. class");
            statisticKeys.add("Min avg. TPut");
            statisticKeys.add("Min peak TPut");
            statisticKeys.add("Min delay");
            statisticKeys.add("Min priority class");
            statisticKeys.add("Min reliability");
            statisticKeys.add("Req. traffic class");
            statisticKeys.add("Req. max UL TPut");
            statisticKeys.add("Req. max DL TPut");
            statisticKeys.add("Req. gr. UL TPut");
            statisticKeys.add("Req. gr. DL TPut");
            statisticKeys.add("Req. deliv. order");
            statisticKeys.add("Req. max SDU size");
            statisticKeys.add("Req. SDU err. ratio");
            statisticKeys.add("Req. resid. BER");
            statisticKeys.add("Req. deliv. err. SDU");
            statisticKeys.add("Req. transfer delay");
            statisticKeys.add("Req. THP");
            statisticKeys.add("Min traffic class");
            statisticKeys.add("Min max UL TPut");
            statisticKeys.add("Min max DL TPut");
            statisticKeys.add("Min gr. UL TPut");
            statisticKeys.add("Min gr. DL TPut");
            statisticKeys.add("Min deliv. order");
            statisticKeys.add("Min max SDU size");
            statisticKeys.add("Min SDU err.");
            statisticKeys.add("Min resid. BER");
            statisticKeys.add("Min del. err. SDU");
            statisticKeys.add("Min tranfer delay");
            statisticKeys.add("Min THP");
            
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Packet session context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Avg. TPut class";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "Peak TPut class";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "Delay class";
            parsedParameters.put(key, getIntegerValue(parameters,4));
            key = "Priority class";
            parsedParameters.put(key, getIntegerValue(parameters,5));
            key = "Reliab. class";
            parsedParameters.put(key, getIntegerValue(parameters,6));
            key = "Min avg. TPut";
            parsedParameters.put(key, getIntegerValue(parameters,7));
            key = "Min peak TPut";
            parsedParameters.put(key, getIntegerValue(parameters,8));
            key = "Min delay";
            parsedParameters.put(key, getIntegerValue(parameters,9));
            key = "Min priority class";
            parsedParameters.put(key, getIntegerValue(parameters,10));
            key = "Min reliability";
            parsedParameters.put(key, getIntegerValue(parameters,11));
            key = "Req. traffic class";
            parsedParameters.put(key, getIntegerValue(parameters,12));
            key = "Req. max UL TPut";
            parsedParameters.put(key, getIntegerValue(parameters,13));
            key = "Req. max DL TPut";
            parsedParameters.put(key, getIntegerValue(parameters,14));
            key = "Req. gr. UL TPut";
            parsedParameters.put(key, getIntegerValue(parameters,15));
            key = "Req. gr. DL TPut";
            parsedParameters.put(key, getIntegerValue(parameters,16));
            key = "Req. deliv. order";
            parsedParameters.put(key, getIntegerValue(parameters,17));
            key = "Req. max SDU size";
            parsedParameters.put(key, getIntegerValue(parameters,18));
            key = "Req. SDU err. ratio";
            parsedParameters.put(key, getStringValue(parameters,19));
            key = "Req. resid. BER";
            parsedParameters.put(key, getStringValue(parameters,20));
            key = "Req. deliv. err. SDU";
            parsedParameters.put(key, getIntegerValue(parameters,21));
            key = "Req. transfer delay";
            parsedParameters.put(key, getIntegerValue(parameters,22));
            key = "Req. THP";
            parsedParameters.put(key, getIntegerValue(parameters,23));
            key = "Min traffic class";
            parsedParameters.put(key, getIntegerValue(parameters,24));
            key = "Min max UL TPut";
            parsedParameters.put(key, getIntegerValue(parameters,25));
            key = "Min max DL TPut";
            parsedParameters.put(key, getIntegerValue(parameters,26));
            key = "Min gr. UL TPut";
            parsedParameters.put(key, getIntegerValue(parameters,27));
            key = "Min gr. DL TPut";
            parsedParameters.put(key, getIntegerValue(parameters,28));
            key = "Min deliv. order";
            parsedParameters.put(key, getIntegerValue(parameters,29));
            key = "Min max SDU size";
            parsedParameters.put(key, getIntegerValue(parameters,30));
            key = "Min SDU err.";
            parsedParameters.put(key, getStringValue(parameters,31));
            key = "Min resid. BER";
            parsedParameters.put(key, getStringValue(parameters,32));
            key = "Min del. err. SDU";
            parsedParameters.put(key, getIntegerValue(parameters,33));
            key = "Min tranfer delay";
            parsedParameters.put(key, getIntegerValue(parameters,34));
            key = "Min THP";
            parsedParameters.put(key, getIntegerValue(parameters,35));
            return parsedParameters;
        }
    },   
    QSPN ("QSPN"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Packet session context ID");
            statisticKeys.add("System");
            statisticKeys.add("Avg. TPut class");
            statisticKeys.add("Peak TPut class");
            statisticKeys.add("Delay class");
            statisticKeys.add("Priority class");
            statisticKeys.add("Reliab. class");
            statisticKeys.add("Traffic class");
            statisticKeys.add("Max UL TPut");
            statisticKeys.add("Max DL TPut");
            statisticKeys.add("Gr. UL TPut");
            statisticKeys.add("Gr. DL TPut");
            statisticKeys.add("Deliv. order");
            statisticKeys.add("Max SDU size");
            statisticKeys.add("SDU err. ratio");
            statisticKeys.add("Resid. BER");
            statisticKeys.add("Deliv. err. SDU");
            statisticKeys.add("Transf. delay");
            statisticKeys.add("THP");
  
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Packet session context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Avg. TPut class";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "Peak TPut class";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "Delay class";
            parsedParameters.put(key, getIntegerValue(parameters,4));
            key = "Priority class";
            parsedParameters.put(key, getIntegerValue(parameters,5));
            key = "Reliab. class";
            parsedParameters.put(key, getIntegerValue(parameters,6));
            key = "Traffic class";
            parsedParameters.put(key, getIntegerValue(parameters,7));
            key = "Max UL TPut";
            parsedParameters.put(key, getIntegerValue(parameters,8));
            key = "Max DL TPut";
            parsedParameters.put(key, getIntegerValue(parameters,9));
            key = "Gr. UL TPut";
            parsedParameters.put(key, getIntegerValue(parameters,10));
            key = "Gr. DL TPut";
            parsedParameters.put(key, getIntegerValue(parameters,11));
            key = "Deliv. order";
            parsedParameters.put(key, getIntegerValue(parameters,12));
            key = "Max SDU size";
            parsedParameters.put(key, getIntegerValue(parameters,13));
            key = "SDU err. ratio";
            parsedParameters.put(key, getStringValue(parameters,14));
            key = "Resid. BER";
            parsedParameters.put(key, getStringValue(parameters,15));
            key = "Deliv. err. SDU";
            parsedParameters.put(key, getIntegerValue(parameters,16));
            key = "Transf. delay";
            parsedParameters.put(key, getIntegerValue(parameters,17));
            key = "THP";
            parsedParameters.put(key, getIntegerValue(parameters,18));
            return parsedParameters;

  
        }
    },   
    
    PCHI ("PCHI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Attach context ID");
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Attach context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            return parsedParameters;
        }
    },   
    
    GAF ("GAF"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Attach context ID");
            statisticKeys.add("System");
            statisticKeys.add("Attach fail");
            statisticKeys.add("Att. fail. cause");           
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Attach context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Attach fail";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "Att. fail. cause";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            return parsedParameters;
        }
    },   
    
    GAC ("GAC"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Attach context ID");
            statisticKeys.add("System");                  
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Attach context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            return parsedParameters;
        }
    },   
    
    GAD ("GAD"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Attach context ID");
            statisticKeys.add("System");          
            statisticKeys.add("Detach status");          
            statisticKeys.add("Detach cause");          
            statisticKeys.add("Detach time");          
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Attach context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Detach status";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "Detach cause";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "Detach time";
            parsedParameters.put(key, getIntegerValue(parameters,4));
            return parsedParameters;
        }
    },   
    
    RLCBLER ("RLCBLER"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    
    RLCRATE ("RLCRATE"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    
    LLCRATE ("LLCRATE"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    
    RUA ("RUA"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Routing area update context ID");
            statisticKeys.add("System");
            statisticKeys.add("RAU type");

            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Routing area update context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "RAU type";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            return parsedParameters;
        }
    },   
    
    RUS ("RUS"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Routing area update context ID");
            statisticKeys.add("System");
            statisticKeys.add("Old RAC");
            statisticKeys.add("Old LAC");
            statisticKeys.add("RAC");
            statisticKeys.add("LAC");

            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Routing area update context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Old RAC";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "Old LAC";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "RAC";
            parsedParameters.put(key, getIntegerValue(parameters,4));
            key = "LAC";
            parsedParameters.put(key, getIntegerValue(parameters,5));
            return parsedParameters;
        }
    },   
    
    RUF ("RUF"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Routing area update context ID");
            statisticKeys.add("System");
            statisticKeys.add("Att. RAC");
            statisticKeys.add("Att. LAC");
            statisticKeys.add("RAU fail cause");

            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Routing area update context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Att. RAC";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "Att. LAC";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "RAU fail cause";
            parsedParameters.put(key, getIntegerValue(parameters,4));
            return parsedParameters;
        }
    },   
    
    TBFI ("TBFI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            statisticKeys.add("#Header params");
            statisticKeys.add("TLLI");
            statisticKeys.add("#params/TBF");
            statisticKeys.add("#UL TBFs");
            statisticKeys.add("TFI");
            statisticKeys.add("RLC win.");
            statisticKeys.add("#DL TBFs");
            statisticKeys.add("TFI");
            statisticKeys.add("RLC win.");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "#Header params";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "TLLI";
            parsedParameters.put(key, getStringValue(parameters,2));
            key = "#params/TBF";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "#UL TBFs";
            parsedParameters.put(key, getIntegerValue(parameters,4));
            key = "TFI";
            parsedParameters.put(key, getIntegerValue(parameters,5));
            key = "RLC win.";
            parsedParameters.put(key, getIntegerValue(parameters,6));
            key = "#DL TBFs";
            parsedParameters.put(key, getIntegerValue(parameters,7));
            key = "TFI";
            parsedParameters.put(key, getIntegerValue(parameters,8));
            key = "RLC win.";
            parsedParameters.put(key, getIntegerValue(parameters,9));
            return parsedParameters;

        }
    },   
    
    TBFULE ("TBFULE"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            statisticKeys.add("UL TBF est. cause");
            statisticKeys.add("UL TBF est. type");
            statisticKeys.add("UL TBF est. status");
            statisticKeys.add("#UL TBF est. req");

            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "UL TBF est. cause";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "UL TBF est. type";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "UL TBF est. status";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "#UL TBF est. req";
            parsedParameters.put(key, getIntegerValue(parameters,4));
            return parsedParameters;
        }
    },   
    
    MACRATE ("MACRATE"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    
    MACBLER ("MACBLER"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    
    AMRI ("AMRI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },   
    
    AMRS ("AMRS"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    AMRQ ("AMRQ"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            statisticKeys.add("AMR qual. est.");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "AMR qual. est.";
            parsedParameters.put(key, getFloatValue(parameters, 0));
            return parsedParameters;
        }
    },  
    AQUL ("AQUL"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("AQ type UL");
            statisticKeys.add("AQ MOS");
            statisticKeys.add("AQ sample file");
            statisticKeys.add("AQ ref. file");
            statisticKeys.add("AQ timestamp");
            statisticKeys.add("AQ sample duration UL");
            statisticKeys.add("AQ activity");
            statisticKeys.add("AQ delay");
            statisticKeys.add("AQ min delay");
            statisticKeys.add("AQ max delay");
            statisticKeys.add("AQ stdev delay");
            statisticKeys.add("AQ SNR");
            statisticKeys.add("AQ insertion gain");
            statisticKeys.add("AQ noise gain");

            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "AQ type UL";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "AQ MOS";
            parsedParameters.put(key, getFloatValue(parameters, 1));
            key = "AQ sample file";
            parsedParameters.put(key, getStringValue(parameters,2));
            key = "AQ ref. file";
            parsedParameters.put(key, getStringValue(parameters,3));
            key = "AQ timestamp";
            parsedParameters.put(key, getStringValue(parameters,4));
            key = "AQ sample duration UL";
            parsedParameters.put(key, getIntegerValue(parameters,5));
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
            return parsedParameters;
        }
    },  
    AQDL ("AQDL"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("AQ type DL");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "AQ type DL";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    AQI ("AQI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("AQ type DL");
            statisticKeys.add("AQ type");
            statisticKeys.add("AQ activity");
            statisticKeys.add("AQ synch.");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "AQ type DL";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "AQ type";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "AQ activity";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "AQ synch.";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            return parsedParameters;
        }
    },  
    VQDL ("VQDL"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Data transfer context ID");
            statisticKeys.add("VQ type");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Data transfer context ID";
            parsedParameters.put(key, getStringValue(parameters,0));
            key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    VRATE ("VRATE"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    MSGA ("MSGA"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            statisticKeys.add("Message type");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "Message type";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    MSGS ("MSGS"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            statisticKeys.add("Message type");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "Message type";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    MSGF ("MSGF"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            statisticKeys.add("Message type");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "Message type";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    PTTA ("PTTA"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("PTT tech.");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "PTT tech.";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    PTTF ("PTTF"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("PTT tech.");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "PTT tech.";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    PTTC ("PTTC"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("PTT tech.");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "PTT tech.";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    PTTD ("PTTD"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("PTT tech.");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "PTT tech.";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
  
    PTTI ("PTTI"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("System");
            statisticKeys.add("PTT tech.");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "System";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "PTT tech.";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            //TODO add parsing parameters if necessary
            return parsedParameters;

        }
    },  
    RTPJITTER ("RTPJITTER"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("RTP jitter type");
            statisticKeys.add("RTP jitter DL");
            statisticKeys.add("RTP jitter UL");
            statisticKeys.add("RTP interarr. DL");
            statisticKeys.add("RTP interarr. UL");           
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "RTP jitter type";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "RTP jitter DL";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "RTP jitter UL";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "RTP interarr. DL";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "RTP interarr. UL";
            parsedParameters.put(key, getIntegerValue(parameters,4));
            //TODO add parsing parameters if necessary
            return parsedParameters;
        }
    },  
    GPS ("GPS"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Lon.");
            statisticKeys.add("Lat.");
            statisticKeys.add("Height");
            statisticKeys.add("Distance");
            statisticKeys.add("GPS fix");
            statisticKeys.add("Satellites");
            statisticKeys.add("Velocity");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Lon.";
            parsedParameters.put(key, getFloatValue(parameters, 0));
            key = "Lat.";
            parsedParameters.put(key, getFloatValue(parameters, 1));
            key = "Height";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            key = "Distance";
            parsedParameters.put(key, getIntegerValue(parameters,3));
            key = "GPS fix";
            parsedParameters.put(key, getIntegerValue(parameters,4));
            key = "Satellites";
            parsedParameters.put(key, getIntegerValue(parameters,5));
            key = "Velocity";
            parsedParameters.put(key, getIntegerValue(parameters,6));
            return parsedParameters;
        }
    },  
    TNOTE ("TNOTE"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("TNote");
            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "TNote";
            parsedParameters.put(key, getFloatValue(parameters, 0));
            return parsedParameters;
        }
    },  
    QNOTE ("QNOTE"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("ID");
            statisticKeys.add("Parent ID");
            statisticKeys.add("Question");
            statisticKeys.add("Answer");
            statisticKeys.add("Description");

            
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "ID";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "Parent ID";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Question";
            parsedParameters.put(key, getStringValue(parameters,2));
            key = "Answer";
            parsedParameters.put(key, getStringValue(parameters,3));
            key = "Description";
            parsedParameters.put(key, getStringValue(parameters,4));
            return parsedParameters;

        }
    },  
    QTRIGGER ("QTRIGGER"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Description");           
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Description";
            parsedParameters.put(key, getStringValue(parameters,0));
            return parsedParameters;
        }
    },  
    MARK ("MARK"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Marker seq.#");           
            statisticKeys.add("Marker#");           
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Marker seq.#";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "Marker#";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            return parsedParameters;
        }
    },  
    ERR ("ERR"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Error");           
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Error";
            parsedParameters.put(key, getStringValue(parameters,0));
            return parsedParameters;
        }
    },  
    DATE ("DATE"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Date");           
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Date";
            parsedParameters.put(key, getStringValue(parameters,0));
            return parsedParameters;
        }
    },  
    PAUSE ("PAUSE"){

        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            return parsedParameters;
        }
    },  
    APP ("APP"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("Ext. app. state");           
            statisticKeys.add("#Ext. app. launch");           
            statisticKeys.add("Ext. app. name");           
            statisticKeys.add("Ext. app. params");                     
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "Ext. app. state";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "#Ext. app. launch";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "Ext. app. name";
            parsedParameters.put(key, getStringValue(parameters,2));
            key = "Ext. app. params";
            parsedParameters.put(key, getStringValue(parameters,3));
            return parsedParameters;
        }
    },  
    LOCK ("LOCK"){
        @Override
        protected void init() {
            super.init();
            statisticKeys.add("#Forcings");           
            statisticKeys.add("Lock type");           
            statisticKeys.add("#Params");           
         
        }
        @Override
        public Map<String, Object> fill(List<String> parameters) {
            Map<String, Object> parsedParameters = new LinkedHashMap<String, Object>();
            String key = "#Forcings";
            parsedParameters.put(key, getIntegerValue(parameters,0));
            key = "Lock type";
            parsedParameters.put(key, getIntegerValue(parameters,1));
            key = "#Params";
            parsedParameters.put(key, getIntegerValue(parameters,2));
            //TODO add parse parameters
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
    protected Set<String> statisticKeys;
    /**
     * 
     */
    private NemoEvents(String eventId) {
        statisticKeys=new HashSet<String>();
        init();
        this.eventId = eventId;
    }
    /**
     *
     */
    protected void init() {
    }

    public static NemoEvents getEventById(String id) {
        return eventsList.get(id);
    }
    public Collection<String> getStatisticKey(){
       
        return Collections.unmodifiableCollection(statisticKeys);
    }

    public abstract Map<String, Object> fill(List<String> parameters);

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
