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
package org.amanzi.splash.views.importbuilder;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.*;

public class ImportBuilderView extends ViewPart {
	private TableViewer viewer;
	
	public static final String IMPORT_BUILDER_VIEW_ID = "org.amanzi.splash.views.ImportBuilderView";

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public ImportBuilderView() {
	}
	private ImportBuilderTableViewer importBuilderTableViewer;

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		importBuilderTableViewer = new ImportBuilderTableViewer(parent);
		
		importBuilderTableViewer.getControl().addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				importBuilderTableViewer.dispose();			
			}
			
		});
	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}


	public ImportBuilderTableViewer getImportBuilderTableViewer() {
		return importBuilderTableViewer;
	}


	public void setImportBuilderTableViewer(
			ImportBuilderTableViewer importBuilderTableViewer) {
		this.importBuilderTableViewer = importBuilderTableViewer;
	}
}
