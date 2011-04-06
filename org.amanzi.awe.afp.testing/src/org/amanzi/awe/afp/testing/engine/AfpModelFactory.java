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

package org.amanzi.awe.afp.testing.engine;

import java.util.LinkedList;
import java.util.Queue;

import org.amanzi.awe.afp.filters.AfpColumnFilter;
import org.amanzi.awe.afp.filters.AfpRowFilter;
import org.amanzi.awe.afp.models.AfpFrequencyDomainModel;
import org.amanzi.awe.afp.models.AfpModel;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public class AfpModelFactory {
    
    public enum AfpScenario {
        QUAD_SPLIT {
            @Override
            public boolean[] getAvailableBands() {
                return new boolean[] {true, true, false, false};
            }

            @Override
            public String[] getAvailableFreq() {
                return new String[] {"1-61", "512-572", "", ""};
            }
        },
        BCCH_TCH_SPLIT_900 {
            @Override
            public boolean[] getAvailableBands() {
                return new boolean[] {true, false, false, false};
            }

            @Override
            public String[] getAvailableFreq() {
                return new String[] {"1-61", "", "", ""};
            }
        },
        BCCH_TCH_SPLIT_1800 {
            @Override
            public boolean[] getAvailableBands() {
                return new boolean[] {false, true, false, false};
            }

            @Override
            public String[] getAvailableFreq() {
                return new String[] {"", "512-572", "", ""};
            }
        },
        DANIEL {
            @Override
            public boolean[] getAvailableBands() {
                return new boolean[] {true, true, false, false};
            }

            @Override
            public String[] getAvailableFreq() {
                return new String[] {"1-90", "512-600", "", ""};
            }
        };
        
        public abstract boolean[] getAvailableBands();
        
        public abstract String[] getAvailableFreq();
    }
    
    public static AfpModel getAfpModel(IDataset loader, AfpScenario scenario) {
        AfpModel model = createEmptyModel(loader);
        
        model.setFrequencyBands(scenario.getAvailableBands());
        for (int i = 0; i < 4; i++) {
            model.setAvailableFreq(i, scenario.getAvailableFreq()[i]);
        }
        
        
        Queue<AfpFrequencyDomainModel> domains = createDomains(scenario, loader.getName() + "_" + scenario.name().toLowerCase());
        while (!domains.isEmpty()) {
            model.setTotalTRX(1);
            
            AfpFrequencyDomainModel domain = domains.poll();
            model.addFrequencyDomainToQueue(domain);
            model.addFreqDomain(domain);
        }
        
        return model;
    }
    
    private static AfpModel createEmptyModel(IDataset loader) {
        AfpModel model = new AfpModel();
        
        model.setTotalTRX(1);
        model.setDatasetNode(loader.getRootNode());
        
        model.loadAfpDataSet();
        
        return model;
    }
    
    private static LinkedList<AfpFrequencyDomainModel> createDomains(AfpScenario scenario, String prefix) {
        switch (scenario) {
        case QUAD_SPLIT:
            return createQuadSplitDomains(prefix);
        case BCCH_TCH_SPLIT_1800:
            return createBcchTchDomains(prefix, "1800");
        case BCCH_TCH_SPLIT_900:
            return createBcchTchDomains(prefix, "900");
        case DANIEL:
            return createQuadSplitDanielDomains(prefix);
        }
        
        return null;
    }
    
    private static LinkedList<AfpFrequencyDomainModel> createQuadSplitDomains(String prefix) {
        LinkedList<AfpFrequencyDomainModel> models = new LinkedList<AfpFrequencyDomainModel>();
        
        AfpColumnFilter bcchFilter = new AfpColumnFilter("bcch", "carrier");
        bcchFilter.addValue("true");
        
        AfpColumnFilter filter900 = new AfpColumnFilter("band", "carrier");
        filter900.addValue("GSM900");
        filter900.addValue("GSM 900");
        
        //bcch900
        AfpFrequencyDomainModel bcch900 = new AfpFrequencyDomainModel();
        bcch900.setName(prefix + "_bcch900");
        bcch900.setFrequencies(AfpModel.rangeArraytoArray(new String[] {"1-30"}));
        AfpRowFilter bcch900filter = new AfpRowFilter();
        bcch900filter.addColumn(bcchFilter);
        bcch900filter.addColumn(filter900);
        bcch900.setFilters(bcch900filter.toString());
        bcch900.setBand("GSM900");
        
        models.add(bcch900);
        
        //tch900
        AfpFrequencyDomainModel tch900 = new AfpFrequencyDomainModel();
        tch900.setName(prefix + "_tch900");
        tch900.setFrequencies(AfpModel.rangeArraytoArray(new String[] {"31-61"}));
        AfpRowFilter tch900filter = new AfpRowFilter();
        tch900filter.addColumn(filter900);
        tch900.setFilters(tch900filter.toString());
        tch900.setBand("GSM900");
        
        models.add(tch900);
        
        //bcch1800
        AfpFrequencyDomainModel bcch1800 = new AfpFrequencyDomainModel();
        bcch1800.setName(prefix + "_bcch1800");
        bcch1800.setFrequencies(AfpModel.rangeArraytoArray(new String[] {"512-541"}));
        AfpRowFilter bcch1800filter = new AfpRowFilter();
        bcch1800filter.addColumn(bcchFilter);
        bcch1800.setFilters(bcch1800filter.toString());
        bcch1800.setBand("GSM1800");
        
        models.add(bcch1800);
        
        //tch1800
        AfpFrequencyDomainModel tch1800 = new AfpFrequencyDomainModel();
        tch1800.setName(prefix + "_tch1800");
        tch1800.setFrequencies(AfpModel.rangeArraytoArray(new String[] {"542-562"}));
        tch1800.setFilters(new AfpRowFilter().toString());
        tch1800.setBand("GSM1800");
        
        models.add(tch1800);
        
        return models;
    }
    
    private static LinkedList<AfpFrequencyDomainModel> createBcchTchDomains(String prefix, String band) {
        LinkedList<AfpFrequencyDomainModel> models = new LinkedList<AfpFrequencyDomainModel>();
        
        String bcchFreq = "1-30";
        if (band.equals("1800")) {
            bcchFreq = "512-541";
        }
        
        String tchFreq = "31-61";
        if (band.equals("1800")) {
            tchFreq = "542-562";
        }
        
        AfpColumnFilter bcchFilter = new AfpColumnFilter("bcch", "carrier");
        bcchFilter.addValue("true");
        
        AfpColumnFilter bandFilter = new AfpColumnFilter("band", "carrier");
        bandFilter.addValue("GSM" + band);
        bandFilter.addValue("GSM " + band);
        
        //bcch
        AfpFrequencyDomainModel bcch = new AfpFrequencyDomainModel();
        bcch.setName(prefix + "_bcch");
        bcch.setFrequencies(AfpModel.rangeArraytoArray(new String[] {bcchFreq}));
        AfpRowFilter bcchRowfilter = new AfpRowFilter();
        bcchRowfilter.addColumn(bcchFilter);
        bcchRowfilter.addColumn(bandFilter);
        bcch.setFilters(bcchRowfilter.toString());
        bcch.setBand("GSM" + band);
        
        models.add(bcch);
        
        //tch
        AfpFrequencyDomainModel tch = new AfpFrequencyDomainModel();
        tch.setName(prefix + "_tch");
        tch.setFrequencies(AfpModel.rangeArraytoArray(new String[] {tchFreq}));
        AfpRowFilter tchRowfilter = new AfpRowFilter();
        tchRowfilter.addColumn(bandFilter);
        tch.setFilters(tchRowfilter.toString());
        tch.setBand("GSM" + band);
        
        models.add(tch);
        
        return models;
    }
    
    private static LinkedList<AfpFrequencyDomainModel> createQuadSplitDanielDomains(String prefix) {
        LinkedList<AfpFrequencyDomainModel> models = new LinkedList<AfpFrequencyDomainModel>();
        
        AfpColumnFilter bcchFilter = new AfpColumnFilter("bcch", "carrier");
        bcchFilter.addValue("true");
        
        AfpColumnFilter filter900 = new AfpColumnFilter("band", "carrier");
        filter900.addValue("GSM900");
        filter900.addValue("GSM 900");
        
        //bcch900
        AfpFrequencyDomainModel bcch900 = new AfpFrequencyDomainModel();
        bcch900.setName(prefix + "_bcch900");
        bcch900.setFrequencies(AfpModel.rangeArraytoArray(new String[] {"1-29"}));
        AfpRowFilter bcch900filter = new AfpRowFilter();
        bcch900filter.addColumn(bcchFilter);
        bcch900filter.addColumn(filter900);
        bcch900.setFilters(bcch900filter.toString());
        bcch900.setBand("GSM900");
        
        models.add(bcch900);
        
        //tch900
        AfpFrequencyDomainModel tch900 = new AfpFrequencyDomainModel();
        tch900.setName(prefix + "_tch900");
        tch900.setFrequencies(AfpModel.rangeArraytoArray(new String[] {"30-90"}));
        AfpRowFilter tch900filter = new AfpRowFilter();
        tch900filter.addColumn(filter900);
        tch900.setFilters(tch900filter.toString());
        tch900.setBand("GSM900");
        
        models.add(tch900);
        
        //bcch1800
        AfpFrequencyDomainModel bcch1800 = new AfpFrequencyDomainModel();
        bcch1800.setName(prefix + "_bcch1800");
        bcch1800.setFrequencies(AfpModel.rangeArraytoArray(new String[] {"512-540"}));
        AfpRowFilter bcch1800filter = new AfpRowFilter();
        bcch1800filter.addColumn(bcchFilter);
        bcch1800.setFilters(bcch1800filter.toString());
        bcch1800.setBand("GSM1800");
        
        models.add(bcch1800);
        
        //tch1800
        AfpFrequencyDomainModel tch1800 = new AfpFrequencyDomainModel();
        tch1800.setName(prefix + "_tch1800");
        tch1800.setFrequencies(AfpModel.rangeArraytoArray(new String[] {"541-600"}));
        tch1800.setFilters(new AfpRowFilter().toString());
        tch1800.setBand("GSM1800");
        
        models.add(tch1800);
        
        return models;
    }

}
