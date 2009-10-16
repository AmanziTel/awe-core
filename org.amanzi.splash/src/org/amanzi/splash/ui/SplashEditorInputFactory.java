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
package org.amanzi.splash.ui;

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
