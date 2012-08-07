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

package org.amanzi.neo.loader.ui.page.widgets.impl;

import org.amanzi.neo.loader.ui.internal.Messages;
import org.amanzi.neo.loader.ui.page.widgets.impl.ResourceSelectorWidget.IResourceSelectorListener;
import org.amanzi.neo.loader.ui.page.widgets.impl.internal.AdvancedFileFieldEditor;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractPageWidget;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ResourceSelectorWidget extends AbstractPageWidget<Composite, IResourceSelectorListener> implements ModifyListener {

	public interface IResourceSelectorListener extends AbstractPageWidget.IPageEventListener {
		void onResourceChanged();
	}

	protected enum ResourceType {
		FILE, DIRECTORY;
	}

	private final ResourceType resourceType;

	private StringButtonFieldEditor editor;

	private final String[] fileExtensions;

	/**
	 * @param isEnabled
	 * @param loaderPage
	 * @param projectModelProvider
	 */
	protected ResourceSelectorWidget(final ResourceType resourceType, final Composite parent,
			final IResourceSelectorListener listener, final IProjectModelProvider projectModelProvider,
			final String... fileExtensions) {
		super(true, parent, listener, projectModelProvider);
		this.resourceType = resourceType;
		this.fileExtensions = fileExtensions;
	}

	@Override
	protected Composite createWidget(final Composite parent, final int style) {
		switch (resourceType) {
		case DIRECTORY:
			editor = new DirectoryFieldEditor("resource", Messages.ResourceSelectorWidget_SelectDirectoryTitle, parent); //$NON-NLS-1$
			break;
		case FILE:
			AdvancedFileFieldEditor fileEditor = new AdvancedFileFieldEditor(
					"resource", Messages.ResourceSelectorWidget_SelectFileTitle, parent); //$NON-NLS-1$
			fileEditor.setFileExtensions(fileExtensions);

			editor = fileEditor;
			break;
		default:
			break;
		}

		editor.getTextControl(parent).addModifyListener(this);

		return parent;
	}

	@Override
	protected int getStyle() {
		return 0;
	}

	@Override
	public void modifyText(final ModifyEvent e) {
		for (IResourceSelectorListener listener : getListeners()) {
			listener.onResourceChanged();
		}
	}

	public String getFileName() {
		return editor.getStringValue();
	}

}
