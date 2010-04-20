/*
 * Licensed to "Neo Technology," Network Engine for Objects in Lund AB
 * (http://neotechnology.com) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. Neo Technology licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at (http://www.apache.org/licenses/LICENSE-2.0). Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.neo4j.neoclipse.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.neo4j.neoclipse.Activator;
import org.neo4j.neoclipse.graphdb.GraphDbServiceMode;

/**
 * Initializes neo4j preferences with their default values.
 * 
 * @author Peter H&auml;nsgen
 * @author Anders Nawroth
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
    /**
     * Initializes the neo4j preferences.
     */
    @Override
    public void initializeDefaultPreferences()
    {
        IPreferenceStore pref = Activator.getDefault().getPreferenceStore();
        pref.setDefault( Preferences.DATABASE_LOCATION, "" );
        pref.setDefault( Preferences.DATABASE_RESOURCE_URI, "" );
        pref.setDefault( Preferences.HELP_ON_START, true );
        pref.setDefault( Preferences.CONNECTION_MODE,
                GraphDbServiceMode.READ_WRITE_EMBEDDED.name() );
        pref.setDefault( Preferences.NEOSTORE_NODES, 25);
        pref.setDefault( Preferences.NEOSTORE_RELATIONSHIPS, 50);
        pref.setDefault( Preferences.NEOSTORE_PROPERTIES, 90);
        pref.setDefault( Preferences.NEOSTORE_PROPERTIES_INDEX, 1);
        pref.setDefault( Preferences.NEOSTORE_PROPERTIES_KEYS, 1);
        pref.setDefault( Preferences.NEOSTORE_PROPERTIES_STRING, 130);
        pref.setDefault( Preferences.NEOSTORE_PROPERTIES_ARRAYS, 130);
    }
}
