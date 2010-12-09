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

//TODO: LN: comments!!!!

/**
 * @author Kasnitskij_V
 * @since 1.0.0
 */
import java.io.File;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class FileLabelProvider extends LabelProvider {
	
    //TODO: LN: instead of writing plugin ID, please add constant to GpehPlugin class and use it
    //TODO: LN: move image names to constants
	private static final Image folderImage = AbstractUIPlugin
											.imageDescriptorFromPlugin("org.amanzi.awe.gpeh",
											"icons/filebrowser/folder.png").createImage();
	private static final Image driveImage = AbstractUIPlugin
											.imageDescriptorFromPlugin("org.amanzi.awe.gpeh",
											"icons/filebrowser/filenav_nav.png").createImage();
	private static final Image fileImage = AbstractUIPlugin
											.imageDescriptorFromPlugin("org.amanzi.awe.gpeh",
											"icons/filebrowser/file_obj.png").createImage();
	
	@Override
	public Image getImage(Object element) {
		File file = (File) element;
		if (file.isDirectory())
			return file.getParent() != null ? folderImage : driveImage;
		return fileImage;
	}
	
	@Override
	public String getText(Object element) {
		String fileName = ((File) element).getName();
		if (fileName.length() > 0) {
			return fileName;
		}
		return ((File) element).getPath();
	}
}