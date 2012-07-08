package org.amanzi.neo.loader.core;

import org.amanzi.neo.loader.core.saver.AutoParseTesting;
import org.amanzi.neo.loader.core.saver.InterferenceSaverTesting;
import org.amanzi.neo.loader.core.saver.NeighbourSaverTesting;
import org.amanzi.neo.loader.core.saver.NetworkSaverTesting;
import org.amanzi.neo.loader.core.saver.SaversSuite;
import org.amanzi.neo.loader.core.saver.SeparationConstraintsSaverTesting;
import org.amanzi.neo.loader.core.saver.TRXSaverTesting;
import org.amanzi.neo.loader.core.saver.TrafficSaverTesting;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AutoParseTesting.class, InterferenceSaverTesting.class,
		NeighbourSaverTesting.class, NetworkSaverTesting.class,
		SaversSuite.class, SeparationConstraintsSaverTesting.class,
		TrafficSaverTesting.class, TRXSaverTesting.class })
public class LoaderTests {

}
