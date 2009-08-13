/**
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * $Id$
 */
package net.refractions.udig.project.internal.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.project.IProject;
import net.refractions.udig.project.IRubyProjectElement;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.RubyProject;
import net.refractions.udig.project.internal.RubyProjectElement;
import net.refractions.udig.project.internal.Spreadsheet;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Ruby Project</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.refractions.udig.project.internal.impl.RubyProjectImpl#getName <em>Name</em>}</li>
 *   <li>{@link net.refractions.udig.project.internal.impl.RubyProjectImpl#getProjectInternal <em>Project Internal</em>}</li>
 *   <li>{@link net.refractions.udig.project.internal.impl.RubyProjectImpl#getRubyElementsInternal <em>Ruby Elements Internal</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RubyProjectImpl extends EObjectImpl implements RubyProject {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getProjectInternal() <em>Project Internal</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProjectInternal()
	 * @generated
	 * @ordered
	 */
	protected Project projectInternal;

	/**
	 * The cached value of the '{@link #getRubyElementsInternal() <em>Ruby Elements Internal</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRubyElementsInternal()
	 * @generated
	 * @ordered
	 */
	protected List<RubyProjectElement> rubyElementsInternal = null;
	
	private Adapter projectPersistenceListener = new AdapterImpl(){
        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
         */
        public void notifyChanged( Notification msg ) {
            switch( msg.getFeatureID(RubyProject.class) ) {        
            case ProjectPackage.RUBY_PROJECT__RUBY_ELEMENTS_INTERNAL:
            case ProjectPackage.RUBY_PROJECT__NAME:
            case ProjectPackage.RUBY_PROJECT__PROJECT_INTERNAL:                     	
                if (RubyProjectImpl.this.eResource() != null)
                    RubyProjectImpl.this.eResource().setModified(true);
            }
        }
    };
    
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RubyProjectImpl() {
		super();
		eAdapters().add(projectPersistenceListener);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ProjectPackage.eINSTANCE.getRubyProject();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ProjectPackage.RUBY_PROJECT__NAME, oldName, name));		
	}

	/**
     * Retrieves this map's project, searching its parents until it finds one, or returns null if it
     * can't find one.
     *
     * @uml.property name="projectInternal"
     */
    public Project getProjectInternal() {
        Project genResult = getProjectInternalGen();
        if (genResult == null) {
            EObject parent = eContainer();
            while( parent != null ) {
                if (parent instanceof Project) {
                    return (Project) parent;
                }

                parent = parent.eContainer();
            }
        }
        return genResult;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Project getProjectInternalGen() {
        if (projectInternal != null && projectInternal.eIsProxy()) {
            Project oldProjectInternal = projectInternal;
            projectInternal = (Project) eResolveProxy((InternalEObject) projectInternal);
            if (projectInternal != oldProjectInternal) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                            ProjectPackage.RUBY_PROJECT__PROJECT_INTERNAL, oldProjectInternal,
                            projectInternal));
            }
        }
        return projectInternal;
    }

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Project basicGetProjectInternal() {
		return projectInternal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProjectInternal(
			Project newProjectInternal, NotificationChain msgs) {
		Project oldProjectInternal = projectInternal;
		projectInternal = newProjectInternal;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this,
					Notification.SET,
					ProjectPackage.RUBY_PROJECT__PROJECT_INTERNAL,
					oldProjectInternal, newProjectInternal);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProjectInternal(Project newProjectInternal) {
		if (newProjectInternal != projectInternal) {
			NotificationChain msgs = null;
			if (projectInternal != null)
				msgs = ((InternalEObject) projectInternal).eInverseRemove(this,
						ProjectPackage.PROJECT__ELEMENTS_INTERNAL,
						Project.class, msgs);
			if (newProjectInternal != null)
				msgs = ((InternalEObject) newProjectInternal).eInverseAdd(this,
						ProjectPackage.PROJECT__ELEMENTS_INTERNAL,
						Project.class, msgs);
			msgs = basicSetProjectInternal(newProjectInternal, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ProjectPackage.RUBY_PROJECT__PROJECT_INTERNAL,
					newProjectInternal, newProjectInternal));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated not
	 */
	@SuppressWarnings("unchecked")
	public List getRubyElementsInternal() {		
		if (rubyElementsInternal == null) {
			rubyElementsInternal = new SynchronizedEObjectWithInverseResolvingEList(
					RubyProjectElement.class, this,
					ProjectPackage.RUBY_PROJECT__RUBY_ELEMENTS_INTERNAL,
					ProjectPackage.RUBY_PROJECT_ELEMENT__RUBY_PROJECT_INTERNAL) {
				
				@Override
				protected void didAdd(int index, Object newObject) {
					createResourceAndAddElement(RubyProjectImpl.this, (RubyProjectElement) newObject);
					super.didAdd(index, newObject);
				}

				@Override
				protected void didSet(int index, Object newObject, Object oldObject) {
					createResourceAndAddElement(RubyProjectImpl.this, (RubyProjectElement) newObject);
					super.didSet(index, newObject, oldObject);
				}
			};
		}		
		return rubyElementsInternal;
	}
	
	/**
	 * Creates a new Resource from map.  The new Resource will be in the same directory as the project's
	 * resource.  The Resource will start with map appended with a number that will make the name unique.
	 * The resource will end in .umap.
	 */
	private void createResourceAndAddElement(RubyProject value, RubyProjectElement projectElement) {
        if( projectElement==null || projectElement.eIsProxy() )
            return;
        if (!(projectElement instanceof Spreadsheet)) {
        	return;
        }
		Resource projectResource = eResource();
		if (projectResource != null) {
			URI projectURI = projectResource.getURI();
			String elementPath = null;
			elementPath = findElementResourcePath(projectElement, elementPath);

			String projectPath = findProjectResourcePath(projectURI);

            if (!projectPath.equals(elementPath)) 
                doCreation(projectElement, projectResource, elementPath, projectPath);
                
		}
	}
	
	 private static URI generateResourceName(String projectPath,
	            RubyProjectElement projectElement, int i) {
	        URI uri;
	        String resourceName = (projectElement.getName()==null?"element":projectElement.getName())+i; //$NON-NLS-1$
	        resourceName = resourceName.replaceAll("[/\\\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$
	        resourceName = resourceName.replaceAll("\\s", "_"); //$NON-NLS-1$ //$NON-NLS-2$
	        resourceName = resourceName.replaceAll("_+", "_"); //$NON-NLS-1$ //$NON-NLS-2$
	        String extension = projectElement.getFileExtension();
	        if( !extension.startsWith(".") ) //$NON-NLS-1$
	            extension="."+extension; //$NON-NLS-1$
	        String tempPath = "file://" + projectPath + File.separator + resourceName + extension; //$NON-NLS-1$  
	        uri = URI.createURI(tempPath);
	        return uri;
	    }
	
	@SuppressWarnings("unchecked")
	private static URI createNewResource(Resource projectResource, String projectPath, RubyProjectElement projectElement) {
		int i = 0;
		List<Resource> list = projectResource.getResourceSet().getResources();
		URIConverter uriConverter = projectResource.getResourceSet()
				.getURIConverter();
		URI uri = null;
		boolean found = false;
		do {
			found = false;
			i++;
			//TODO Add file extension name to ProjectElement
			uri = generateResourceName(projectPath, projectElement, i);
			
			URI normalizedURI = uriConverter.normalize(uri);
			for (Resource resource2 : list) {
				if (uriConverter.normalize(resource2.getURI()).equals(
						normalizedURI)) {
					found = true;
					break;
				}
			}
			if (!found) {
				File file = new File(uri.toFileString());
				if (file.exists())
					found = true;
			}
		} while (found);
		uri.deresolve(projectResource.getURI(), true, true, true);
		return uri;
	}
	
	@SuppressWarnings("unchecked")
    private static void doCreation(RubyProjectElement projectElement, Resource projectResource, String elementPath, String projectPath ) {
        	Resource resource = null;

        	URI uri = createNewResource(projectResource, projectPath, projectElement);
        	resource = projectResource.getResourceSet().createResource(uri);
        	resource.getContents().add(projectElement);
            resource.setTrackingModification(true);
            resource.setModified(true);
    }
	
	private static String findProjectResourcePath( URI projectURI ) {
        String projectPath = projectURI.toFileString();
        projectPath = projectPath.substring(0, projectPath
        		.lastIndexOf(File.separatorChar));
        while (projectPath.startsWith(File.separator + File.separator)) { 
        	projectPath = projectPath.substring(1);
        }
        if (Platform.getOS().equals(Platform.OS_WIN32)
        		&& projectPath.startsWith(File.separator)) { 
        	projectPath = projectPath.substring(1);
        }
        return projectPath;
    }
	
	private String findElementResourcePath( RubyProjectElement projectElement, String elementPath2 ) {
        String elementPath=elementPath2;
        if (projectElement.eResource() != null) {
        	elementPath = projectElement.eResource().getURI().toFileString();
            elementPath = elementPath.substring(0, elementPath
                    .lastIndexOf(File.separatorChar));
            while (elementPath.startsWith(File.separator + File.separator)) { 
                elementPath = elementPath.substring(1);
            }
            if (Platform.getOS().equals(Platform.OS_WIN32)
                    && elementPath.startsWith(File.separator)) { 
                elementPath = elementPath.substring(1);
            }
        }
        return elementPath;
    }
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd,
			int featureID, Class baseClass, NotificationChain msgs) {
		switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
		case ProjectPackage.RUBY_PROJECT__PROJECT_INTERNAL:
			if (projectInternal != null)
				msgs = ((InternalEObject) projectInternal).eInverseRemove(this,
						ProjectPackage.PROJECT__ELEMENTS_INTERNAL,
						Project.class, msgs);	
			break;
		case ProjectPackage.RUBY_PROJECT__RUBY_ELEMENTS_INTERNAL:
			return ((InternalEList) getRubyElementsInternal()).basicAdd(
					otherEnd, msgs);
		}
		return eBasicSetContainer(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ProjectPackage.RUBY_PROJECT__PROJECT_INTERNAL:
			return basicSetProjectInternal(null, msgs);
		case ProjectPackage.RUBY_PROJECT__RUBY_ELEMENTS_INTERNAL:
            return ((InternalEList) getRubyElementsInternal()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet( EStructuralFeature eFeature, boolean resolve ) {
		switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.RUBY_PROJECT__NAME:
            return getName();
        case ProjectPackage.RUBY_PROJECT__PROJECT_INTERNAL:
            return getProjectInternal();                
		case ProjectPackage.RUBY_PROJECT__RUBY_ELEMENTS_INTERNAL:
			return getRubyElementsInternal();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet( EStructuralFeature eFeature, Object newValue ) {
		switch( eDerivedStructuralFeatureID(eFeature) ) {
			case ProjectPackage.RUBY_PROJECT__NAME:
	            setName((String) newValue);
	            return;
	        case ProjectPackage.RUBY_PROJECT__PROJECT_INTERNAL:
	            setProjectInternal((Project) newValue);
	            return;
	        case ProjectPackage.RUBY_PROJECT__RUBY_ELEMENTS_INTERNAL:
	        	getRubyElementsInternal().clear();
	        	getRubyElementsInternal().addAll((Collection) newValue);
			return;
		}
		eDynamicSet(eFeature, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(int featureID) {
		switch (featureID) {
		case ProjectPackage.RUBY_PROJECT__NAME:
			setName(NAME_EDEFAULT);
			return;
		case ProjectPackage.RUBY_PROJECT__PROJECT_INTERNAL:
			setProjectInternal((Project) null);
			return;
		case ProjectPackage.RUBY_PROJECT__RUBY_ELEMENTS_INTERNAL:
			getRubyElementsInternal().clear();
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case ProjectPackage.RUBY_PROJECT__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT
					.equals(name);
		case ProjectPackage.RUBY_PROJECT__PROJECT_INTERNAL:
			return projectInternal != null;
		case ProjectPackage.RUBY_PROJECT__RUBY_ELEMENTS_INTERNAL:
			return rubyElementsInternal != null
					&& !rubyElementsInternal.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: "); //$NON-NLS-1$
		result.append(name);
		result.append(')');
		return result.toString();
	}
	
	/**
     * @see net.refractions.udig.project.IProjectElement#getProject()
     */
    public IProject getProject() {
        return getProjectInternal();
    }

	public String getFileExtension() {
		return "urp";
	}

	/**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter ) {
    	for( Iterator i = eAdapters().iterator(); i.hasNext(); ) {
            Object o = i.next();
            if (adapter.isAssignableFrom(o.getClass()))
                return o;
        }
    	
    	/*
         * Adapt to an IWorkbenchAdapter. Other aspects of Eclipse can read the
         * properties we provide access to. (example: Property page dialogs
         * can read the label and display that in their title.)
         */
    	if (adapter.isAssignableFrom(IWorkbenchAdapter.class)) {
    		return new WorkbenchAdapter() {
			
				@Override
				public String getLabel(Object object) {
					return getName();
				}
			
			};
    	}
        return null;
    }
    
    @Override
    public List<IRubyProjectElement> getElements(Class type ) {
        List<IRubyProjectElement> lists = new ArrayList<IRubyProjectElement>();
        for( Iterator<IRubyProjectElement> iter = getRubyElementsInternal().iterator(); iter.hasNext(); ) {
            IRubyProjectElement obj = iter.next();
            if (type.isAssignableFrom(obj.getClass()))
                lists.add(obj);
        }
        return lists;
    }

} //RubyProjectImpl
