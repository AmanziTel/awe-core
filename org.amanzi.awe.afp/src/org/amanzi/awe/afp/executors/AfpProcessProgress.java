package org.amanzi.awe.afp.executors;


public interface AfpProcessProgress {

	public void onProgressUpdate(int result, 
			long time, // time in millisecs
			long remaingtotal, 
			long sectorSeperations,
			long siteSeperation,
			long freqConstraints,
			long interference,
			long neighbor,
			long tringulation,
			long shadowing);
}
