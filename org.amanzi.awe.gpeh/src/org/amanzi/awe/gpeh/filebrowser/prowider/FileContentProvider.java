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

package org.amanzi.awe.gpeh.filebrowser.prowider;

/**
 * @author Kasnitskij_V
 * @since 1.0.0
 */
import java.io.File;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FileContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object parent) {
		File file = (File) parent;
		return file.listFiles();
	}
	
	public Object[] getElements(Object inputElement) {
		return (Object[]) inputElement;
	}
	
	@Override
	public Object getParent(Object element) {
		File file = (File) element;
		return file.getParentFile();
	}
	
	@Override
	public boolean hasChildren(Object parent) {
		File file = (File) parent;
		return file.isDirectory();
	}
	
	@Override
	public void dispose() {
	
	}
	
	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}

}
