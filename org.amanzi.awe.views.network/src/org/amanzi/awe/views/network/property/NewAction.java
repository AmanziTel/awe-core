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
package org.amanzi.awe.views.network.property;

import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.neoclipse.property.PropertyTransform.PropertyHandler;

/**
 * Action to add a new property to a PropertyContainer.
 * @author Anders Nawroth
 */
public class NewAction extends Action
{
    private final PropertyHandler propertyHandler;
    
    private NetworkPropertySheetPage page;

    public NewAction( final Composite parent, NetworkPropertySheetPage page, final PropertyHandler propertyHandler) {
        super(propertyHandler.name());
        this.propertyHandler = propertyHandler;
        this.page = page;
    }

    @Override
    public void run()
    {
        PropertyContainer propertyContainer = page.getCurrentNode().getNode();
        if ( propertyContainer == null )
        {
            return;
        }
        InputDialog keyInput = new InputDialog( null, "Key entry",
            "Please enter the key of the new property", null, null );
        if ( keyInput.open() != Dialog.OK || keyInput.getReturnCode() != Dialog.OK )
        {
            return;
        }
        String key = keyInput.getValue();
        addProperty( propertyContainer, key, propertyHandler);
    }
    
    /**
     * Add a property to Node/Relationship. The user will be asked for
     * confirmation if the key already exists.
     * 
     * @param container
     * @param key
     * @param propertyHandler
     * @param propertySheet
     */
    public void addProperty( final PropertyContainer container,
            final String key, final PropertyHandler propertyHandler)
    {
        if ( container.hasProperty( key ) )
        {
            if ( !MessageDialog.openQuestion(
                    null,
                    "Key exists",
                    "The key \""
                            + key
                            + "\" already exists, do you want to overwrite the old value?" ) )
            {
                return;
            }
        }
        InputDialog valueInput = new InputDialog( null, "Value entry",
                "Please enter the value of the new property",
                propertyHandler.render( propertyHandler.value() ),
                propertyHandler.getValidator() );
        if ( valueInput.open() != Dialog.OK && valueInput.getReturnCode() != Dialog.OK )
        {
            return;
        }
        Object val = null;
        try
        {
            val = propertyHandler.parse( valueInput.getValue() );
        }
        catch ( IOException e )
        {
            MessageDialog.openError( null, "Error message",
                    "Error parsing the input value, no changes will be performed." );
            return;
        }
        setProperty( container, key, val);
    }
    
    /**
     * Set a property value, no questions asked.
     * 
     * @param container
     * @param key
     * @param value
     * @param propertySheet
     */
    public void setProperty( final PropertyContainer container,
            final String key, final Object value)
    {
        try
        {
            container.setProperty( key, value );
        }
        catch ( Exception e )
        {
            MessageDialog.openError( null, "Error", "Error in Neo service: "
                                                    + e.getMessage() );
            e.printStackTrace();
        }
        page.refresh();
    }
}
