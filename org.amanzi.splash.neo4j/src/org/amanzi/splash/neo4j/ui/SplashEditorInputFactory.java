/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.amanzi.splash.neo4j.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * TODO Purpose of
 * <p>
 * Extensions of elementFactories
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class SplashEditorInputFactory implements IElementFactory {

    private static final String FACTORY_ID = SplashEditorInputFactory.class.getName();
    @Override
    public IAdaptable createElement(IMemento memento) {
        return SplashEditorInput.create(memento);
    }

    public static String getFactoryId() {
        return FACTORY_ID;
    }
}
