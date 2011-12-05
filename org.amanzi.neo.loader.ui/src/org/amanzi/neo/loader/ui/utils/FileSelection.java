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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * <p>
 *Part of view provide work with file/directory choosing
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class FileSelection extends ViewPart {
    
    private static final Image folderImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
    private static final Image fileImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
    
    private class FileLabelProvider extends LabelProvider {

        @Override
        public Image getImage(Object element) {
            File file = (File) element;
            if (file.isFile())
                return fileImage;
            return folderImage;
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
    private class FileContentProvider implements ITreeContentProvider {

        @Override
        public Object[] getChildren(Object parent) {
            File file = (File) parent;
            
            List<File> files = new ArrayList<File>();
            List<File> directories = new ArrayList<File>();
            List<File> others = new ArrayList<File>();
            
            for (File singleFile : file.listFiles()) {
                if (!singleFile.isHidden()) {
                    if (singleFile.isFile() && FileSelection.this.showFiles) {
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
    
    /*
     * Should Files be also visible, or only directories 
     */
    private boolean showFiles;
    
    /*
     * Text of FileSelection View Label
     */
    private String labelText;
    
    public FileSelection(boolean showFiles, String labelText) { 
        this.showFiles = showFiles;
        this.labelText = labelText;
    }

    public void createPartControl(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelText);
        GridData labelLayout = new GridData(SWT.FILL);
        labelLayout.horizontalSpan = 3;
        label.setLayoutData(labelLayout);
        
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(new FileContentProvider());
        viewer.setLabelProvider(new FileLabelProvider());
        viewer.setInput(File.listRoots());
        String defDir = LoaderUiUtils.getDefaultDirectory();
        if (StringUtils.isNotEmpty(defDir)){
            viewer.reveal(new File(defDir));
        }
    }
    
    public void setFocus() {
        viewer.getControl().setFocus();
    }
    
    public TreeViewer getTreeViewer() {
        return viewer;
    }
    public List<File>getSelectedFiles(FileFilter filter){
        ITreeSelection treeSelection = (ITreeSelection) viewer.getSelection();
        ArrayList<File> results = new ArrayList<File>();
        for (TreePath path : treeSelection.getPaths()) {
            File file = new File(path.getLastSegment().toString());
            if (filter==null||filter.accept(file)){
                results.add(file);
            }
        } 
        return results;
    }
    public void storeDefSelection(File defSelection){
        if (defSelection==null){
            ITreeSelection treeSelection = (ITreeSelection) viewer.getSelection();
            defSelection=(File)treeSelection.getFirstElement();
        }
        if (defSelection!=null){
            LoaderUiUtils.setDefaultDirectory(defSelection.getAbsolutePath());
        }
    }
   
}
