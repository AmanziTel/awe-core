package org.amanzi.awe.afp.executors;

import java.sql.Date;

public interface AfpProcessProgress {

	public void onProgressUpdate(int result, 
			long time, // time in millisecs
			int remaingtotal, 
			int sectorSeperations,
			int siteSeperation,
			int freqConstraints,
			int interference,
			int neighbor,
			int tringulation,
			int shadowing);
}
