package org.amanzi.awe.afp.models;

import java.util.Vector;

import org.amanzi.awe.afp.filters.AfpFilter;
import org.amanzi.neo.services.INeoConstants;
import org.neo4j.graphdb.Node;

public class AfpFrequencyDomainModel extends AfpDomainModel {

	String band;
	String[] frequencies;
	
	public static AfpFrequencyDomainModel getModel(Node n) {

		try {
			AfpFrequencyDomainModel model = new AfpFrequencyDomainModel();
			
			AfpDomainModel.getModel(model, n);

			String band = (String) n.getProperty(INeoConstants.AFP_PROPERTY_FREQUENCY_BAND_NAME);
			String[] frequencies = (String[]) n.getProperty(INeoConstants.AFP_PROPERTY_FREQUENCIES_NAME);
			
			model.setBand(band);
			model.setFrequencies(frequencies);
		
			return model;
		} catch (Exception e) {
			return null;
		}
	}
	
	public AfpFrequencyDomainModel() {
	}
	public AfpFrequencyDomainModel(AfpFrequencyDomainModel c) {
		super(c);
		this.setBand(c.getBand());
		this.setFrequencies(c.getFrequencies());
	}
	
	
	public int getCount() {
		// calculate not free count
		String r[] = AfpModel.rangeArraytoArray(frequencies);
		if(r != null) {
			return r.length;
		}
		return 0;
	}
	/**
	 * @return the band
	 */
	public String getBand() {
		return band;
	}
	/**
	 * @param band the band to set
	 */
	public void setBand(String band) {
		this.band = band;
		// also add a filter for the same
	}
	
	@Override
	public String getFilters() {
		/*String f=super.getFilters();
		AfpFilter af = AfpFilter.getFilter(f);

		for(String b: AfpModel.BAND_NAMES) {
			af.removeFilter(AfpFilter.FILTER_LIKE, "band", ".*"+band+".*");
		}
		af.addFilter(AfpFilter.FILTER_LIKE, "band", ".*"+band+".*");

		return af.toString();*/
		return super.getFilters();
	}
	/**
	 * @return the frequencies
	 */
	public String[] getFrequencies() {
		return frequencies;
	}
	public String getFrequenciesAsString() {
		String s = new String();
		boolean flg = false;
		if(frequencies != null) {
			for(String s2: frequencies) {
				if(flg) {
					s+=",";
				}
				s+=s2;
				flg = true;
			}
		}
		return s;
	}
	/**
	 * @param frequencies the frequencies to set
	 */
	public void setFrequencies(String[] frequencies) {
		if(frequencies != null) {
			Vector<String> v = new Vector<String>();
			// validate and split if required.
			for(String f: frequencies) {
				String[] tokens = f.split(",");
				for(String t:tokens) {
					v.add(t);
				}
			}
			this.frequencies = new String[v.size()];
			v.copyInto(this.frequencies);
		} else
			this.frequencies = null;
	}


	
}
