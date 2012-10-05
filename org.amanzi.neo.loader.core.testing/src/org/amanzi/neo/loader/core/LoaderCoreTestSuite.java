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

package org.amanzi.neo.loader.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
@RunWith(Suite.class)
@SuiteClasses({org.amanzi.neo.loader.core.LoaderTest.class, org.amanzi.neo.loader.core.integration.NetworkLoaderTest.class,
        org.amanzi.neo.loader.core.parser.impl.CSVParserTest.class,
        org.amanzi.neo.loader.core.parser.impl.internal.AbstractParserTest.class,
        org.amanzi.neo.loader.core.parser.impl.internal.AbstractStreamParserTest.class,
        org.amanzi.neo.loader.core.saver.impl.NetworkSaverTest.class,
        org.amanzi.neo.loader.core.saver.impl.internal.AbstractSaverTest.class,
        org.amanzi.neo.loader.core.saver.impl.internal.AbstractSynonymsSaverTest.class,
        org.amanzi.neo.loader.core.synonyms.SynonymsManagerTest.class})
public class LoaderCoreTestSuite {

}
