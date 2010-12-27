package org.amanzi.awe.afp.executors;

import java.sql.Date;

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
