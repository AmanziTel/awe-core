/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Davids <sdavids@gmx.de> - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.testunit.ui;

import java.util.List;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

/**
 * Default preference value initialization for the
 * <code>org.rubypeople.rdt.testunit</code> plug-in.
 */
public class TestUnitPreferenceInitializer extends AbstractPreferenceInitializer {

	/** {@inheritDoc} */
	public void initializeDefaultPreferences() {
		Preferences prefs= TestunitPlugin.getDefault().getPluginPreferences();
		prefs.setDefault(TestUnitPreferencesConstants.DO_FILTER_STACK, true);
		prefs.setDefault(TestUnitPreferencesConstants.SHOW_ON_ERROR_ONLY, false);
//		prefs.setDefault(TestUnitPreferencesConstants.ENABLE_ASSERTIONS, false);

		List defaults= TestUnitPreferencesConstants.createDefaultStackFiltersList();
		String[] filters= (String[]) defaults.toArray(new String[defaults.size()]);
		String active= TestUnitPreferencesConstants.serializeList(filters);
		prefs.setDefault(TestUnitPreferencesConstants.PREF_ACTIVE_FILTERS_LIST, active);
		prefs.setDefault(TestUnitPreferencesConstants.PREF_INACTIVE_FILTERS_LIST, ""); //$NON-NLS-1$
//		prefs.setDefault(TestUnitPreferencesConstants.MAX_TEST_RUNS, 10);
	}
}
