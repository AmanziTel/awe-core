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

package org.amanzi.neo.loader.ui.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * <p>
 *Part of view provide work with file/directory choosing
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class FileSelection extends ViewPart {
    private class FileLabelProvider extends LabelProvider {
        
        //TODO: LN: instead of writing plugin ID, please add constant to GpehPlugin class and use it
        //TODO: LN: move image names to constants
        private  final Image folderImage = AbstractUIPlugin
                                                .imageDescriptorFromPlugin("org.amanzi.awe.gpeh",
                                                "icons/filebrowser/folder.png").createImage();
        private  final Image driveImage = AbstractUIPlugin
                                                .imageDescriptorFromPlugin("org.amanzi.awe.gpeh",
                                                "icons/filebrowser/filenav_nav.png").createImage();
        private  final Image fileImage = AbstractUIPlugin
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
    private static class FileContentProvider implements ITreeContentProvider {

        @Override
        public Object[] getChildren(Object parent) {
            File file = (File) parent;
            
            List<File> files = new ArrayList<File>();
            List<File> directories = new ArrayList<File>();
            List<File> others = new ArrayList<File>();
            
            for (File singleFile : file.listFiles()) {
                if (!singleFile.isHidden()) {
                    if (singleFile.isFile()) {
                        files.add(singleFile);
                    }
                    else if (singleFile.isDirectory()) {
                        directories.add(singleFile);
                    }
                    else {
                        others.add(singleFile);
                    }
                }
            }
            
            Collections.sort(files);
            Collections.sort(directories);
            Collections.sort(others);

            directories.addAll(files);
            directories.addAll(others);
            Object[] objects = directories.toArray();
            
            return objects;
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

    private TreeViewer viewer;

    public void createPartControl(Composite parent) {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(new FileContentProvider());
        viewer.setLabelProvider(new FileLabelProvider());
        viewer.setInput(File.listRoots());
        String defDir = LoaderUiUtils.getDefaultDirectory();
        if (defDir!=null){
            viewer.reveal(defDir);
        }
//        viewer.addOpenListener(new IOpenListener() {
//
//            @Override
//            public void open(OpenEvent event) {
//                IStructuredSelection selection = (IStructuredSelection) event
//                        .getSelection();
//
//                File file = (File) selection.getFirstElement();
//                if (Desktop.isDesktopSupported()) {
//                    Desktop desktop = Desktop.getDesktop();
//                    if (desktop.isSupported(Desktop.Action.OPEN)) {
//                        try {
//                            desktop.open(file);
//                        } catch (IOException e) {
//                            // DO NOTHING
//                        }
//                    }
//                }
//            }
//        });
    }
    
    public void setFocus() {
        viewer.getControl().setFocus();
    }
    
    public TreeViewer getTreeViewer() {
        return viewer;
    }
@Override
public void dispose() {
    super.dispose();
}
   
}
