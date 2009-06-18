/**
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * $Id$
 */
package net.refractions.udig.project.internal.impl;

import java.util.Iterator;

import net.refractions.udig.project.IProject;
import net.refractions.udig.project.IProjectElement;
import net.refractions.udig.project.IRubyProjectElement;

import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectElement;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.RubyFile;
import net.refractions.udig.project.internal.RubyProject;
import net.refractions.udig.project.internal.RubyProjectElement;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Ruby File</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.refractions.udig.project.internal.impl.RubyFileImpl#getName <em>Name</em>}</li>
 *   <li>{@link net.refractions.udig.project.internal.impl.RubyFileImpl#getProjectInternal <em>Project Internal</em>}</li>
 *   <li>{@link net.refractions.udig.project.internal.impl.RubyFileImpl#getRubyProjectInternal <em>Ruby Project Internal</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RubyFileImpl extends EObjectImpl implements RubyFile {
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
	 * The cached value of the '{@link #getRubyProjectInternal() <em>Ruby Project Internal</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRubyProjectInternal()
	 * @generated
	 * @ordered
	 */
	protected RubyProject rubyProjectInternal;
	
	/**
	 * Field for Resource of file
	 * 
	 * @author Lagutko_N
	 */
	
	private IResource resource;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RubyFileImpl() {
		super();		
	}
	
	/*
	 * Getter and setter for Resource
	 * (non-Javadoc)
	 * @see net.refractions.udig.project.IRubyFile#getResource()
	 */
	
	public IResource getResource() {
		return resource;
	}
	
	public void setResource(IResource resource) {
		this.resource = resource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ProjectPackage.eINSTANCE.getRubyFile();
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
					ProjectPackage.RUBY_FILE__NAME, oldName, name));
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
                            ProjectPackage.RUBY_FILE__PROJECT_INTERNAL, oldProjectInternal,
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
					ProjectPackage.RUBY_FILE__PROJECT_INTERNAL,
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
					ProjectPackage.RUBY_FILE__PROJECT_INTERNAL,
					newProjectInternal, newProjectInternal));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RubyProject getRubyProjectInternal() {
		if (rubyProjectInternal != null && rubyProjectInternal.eIsProxy()) {
			InternalEObject oldRubyProjectInternal = (InternalEObject) rubyProjectInternal;
			rubyProjectInternal = (RubyProject) eResolveProxy(oldRubyProjectInternal);
			if (rubyProjectInternal != oldRubyProjectInternal) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							ProjectPackage.RUBY_FILE__RUBY_PROJECT_INTERNAL,
							oldRubyProjectInternal, rubyProjectInternal));
			}
		}
		return rubyProjectInternal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RubyProject basicGetRubyProjectInternal() {
		return rubyProjectInternal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRubyProjectInternal(
			RubyProject newRubyProjectInternal, NotificationChain msgs) {
		RubyProject oldRubyProjectInternal = rubyProjectInternal;
		rubyProjectInternal = newRubyProjectInternal;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this,
					Notification.SET,
					ProjectPackage.RUBY_FILE__RUBY_PROJECT_INTERNAL,
					oldRubyProjectInternal, newRubyProjectInternal);
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
	public void setRubyProjectInternal(RubyProject newRubyProjectInternal) {
		rubyProjectInternal = newRubyProjectInternal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ProjectPackage.RUBY_FILE__PROJECT_INTERNAL:
			if (projectInternal != null)
				msgs = ((InternalEObject) projectInternal).eInverseRemove(this,
						ProjectPackage.PROJECT__ELEMENTS_INTERNAL,
						Project.class, msgs);
			return basicSetProjectInternal((Project) otherEnd, msgs);
		case ProjectPackage.RUBY_FILE__RUBY_PROJECT_INTERNAL:
			if (rubyProjectInternal != null)
				msgs = ((InternalEObject) rubyProjectInternal).eInverseRemove(
						this,
						ProjectPackage.RUBY_PROJECT__RUBY_ELEMENTS_INTERNAL,
						RubyProject.class, msgs);
			return basicSetRubyProjectInternal((RubyProject) otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ProjectPackage.RUBY_FILE__PROJECT_INTERNAL:
			return basicSetProjectInternal(null, msgs);
		case ProjectPackage.RUBY_FILE__RUBY_PROJECT_INTERNAL:
			return basicSetRubyProjectInternal(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ProjectPackage.RUBY_FILE__NAME:
			return getName();
		case ProjectPackage.RUBY_FILE__PROJECT_INTERNAL:
			if (resolve)
				return getProjectInternal();
			return basicGetProjectInternal();
		case ProjectPackage.RUBY_FILE__RUBY_PROJECT_INTERNAL:
			if (resolve)
				return getRubyProjectInternal();
			return basicGetRubyProjectInternal();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case ProjectPackage.RUBY_FILE__NAME:
			setName((String) newValue);
			return;
		case ProjectPackage.RUBY_FILE__PROJECT_INTERNAL:
			setProjectInternal((Project) newValue);
			return;
		case ProjectPackage.RUBY_FILE__RUBY_PROJECT_INTERNAL:
			setRubyProjectInternal((RubyProject) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(int featureID) {
		switch (featureID) {
		case ProjectPackage.RUBY_FILE__NAME:
			setName(NAME_EDEFAULT);
			return;
		case ProjectPackage.RUBY_FILE__PROJECT_INTERNAL:
			setProjectInternal((Project) null);
			return;
		case ProjectPackage.RUBY_FILE__RUBY_PROJECT_INTERNAL:
			setRubyProjectInternal((RubyProject) null);
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
		case ProjectPackage.RUBY_FILE__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT
					.equals(name);
		case ProjectPackage.RUBY_FILE__PROJECT_INTERNAL:
			return projectInternal != null;
		case ProjectPackage.RUBY_FILE__RUBY_PROJECT_INTERNAL:
			return rubyProjectInternal != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class baseClass) {
		if (baseClass == IProjectElement.class) {
			switch (derivedFeatureID) {
			default:
				return -1;
			}
		}
		if (baseClass == IAdaptable.class) {
			switch (derivedFeatureID) {
			default:
				return -1;
			}
		}
		if (baseClass == ProjectElement.class) {
			switch (derivedFeatureID) {
			case ProjectPackage.RUBY_FILE__NAME:
				return ProjectPackage.PROJECT_ELEMENT__NAME;
			case ProjectPackage.RUBY_FILE__PROJECT_INTERNAL:
				return ProjectPackage.PROJECT_ELEMENT__PROJECT_INTERNAL;
			default:
				return -1;
			}
		}
		if (baseClass == IRubyProjectElement.class) {
			switch (derivedFeatureID) {
			default:
				return -1;
			}
		}
		if (baseClass == RubyProjectElement.class) {
			switch (derivedFeatureID) {
			case ProjectPackage.RUBY_FILE__RUBY_PROJECT_INTERNAL:
				return ProjectPackage.RUBY_PROJECT_ELEMENT__RUBY_PROJECT_INTERNAL;
			default:
				return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class baseClass) {
		if (baseClass == IProjectElement.class) {
			switch (baseFeatureID) {
			default:
				return -1;
			}
		}
		if (baseClass == IAdaptable.class) {
			switch (baseFeatureID) {
			default:
				return -1;
			}
		}
		if (baseClass == ProjectElement.class) {
			switch (baseFeatureID) {
			case ProjectPackage.PROJECT_ELEMENT__NAME:
				return ProjectPackage.RUBY_FILE__NAME;
			case ProjectPackage.PROJECT_ELEMENT__PROJECT_INTERNAL:
				return ProjectPackage.RUBY_FILE__PROJECT_INTERNAL;
			default:
				return -1;
			}
		}
		if (baseClass == IRubyProjectElement.class) {
			switch (baseFeatureID) {
			default:
				return -1;
			}
		}
		if (baseClass == RubyProjectElement.class) {
			switch (baseFeatureID) {
			case ProjectPackage.RUBY_PROJECT_ELEMENT__RUBY_PROJECT_INTERNAL:
				return ProjectPackage.RUBY_FILE__RUBY_PROJECT_INTERNAL;
			default:
				return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
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
		return "urf";
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

} //RubyFileImpl
