/**
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * $Id$
 */
package net.refractions.udig.project.internal.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.project.IProject;
import net.refractions.udig.project.IProjectElement;
import net.refractions.udig.project.IRubyProjectElement;

import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectElement;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.Spreadsheet;
import net.refractions.udig.project.internal.SpreadsheetType;
import net.refractions.udig.project.internal.RubyProject;
import net.refractions.udig.project.internal.RubyProjectElement;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Ruby Class</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.refractions.udig.project.internal.impl.SpreadsheetImpl#getName <em>Name</em>}</li>
 *   <li>{@link net.refractions.udig.project.internal.impl.SpreadsheetImpl#getProjectInternal <em>Project Internal</em>}</li>
 *   <li>{@link net.refractions.udig.project.internal.impl.SpreadsheetImpl#getRubyProjectInternal <em>Ruby Project Internal</em>}</li>
 *   <li>{@link net.refractions.udig.project.internal.impl.SpreadsheetImpl#getSpreadsheetPath <em>Spreadsheet Path</em>}</li>
 *   <li>{@link net.refractions.udig.project.internal.impl.SpreadsheetImpl#getSpreadsheetType <em>Spreadsheet Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SpreadsheetImpl extends EObjectImpl implements Spreadsheet {
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
     * The default value of the '{@link #getSpreadsheetPath() <em>Spreadsheet Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSpreadsheetPath()
     * @generated
     * @ordered
     */
    protected static final URL SPREADSHEET_PATH_EDEFAULT = null; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getSpreadsheetPath() <em>Spreadsheet Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSpreadsheetPath()
     * @generated
     * @ordered
     */
    protected URL spreadsheetPath = SPREADSHEET_PATH_EDEFAULT;

    /**
     * The default value of the '{@link #getSpreadsheetType() <em>Spreadsheet Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSpreadsheetType()
     * @generated
     * @ordered
     */
    protected static final SpreadsheetType SPREADSHEET_TYPE_EDEFAULT = null; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getSpreadsheetType() <em>Spreadsheet Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSpreadsheetType()
     * @generated
     * @ordered
     */
    protected SpreadsheetType spreadsheetType = SPREADSHEET_TYPE_EDEFAULT;

    /**
     * Field for Resource of file
     * 
     * @author Lagutko_N
     */

    private IResource resource;

    private Adapter spreadsheetPersistenceListener = new AdapterImpl(){
        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
         */
        public void notifyChanged(Notification msg) {
            switch (msg.getFeatureID(Spreadsheet.class)) {
            case ProjectPackage.SPREADSHEET__NAME:
            case ProjectPackage.SPREADSHEET__PROJECT_INTERNAL:
            case ProjectPackage.SPREADSHEET__RUBY_PROJECT_INTERNAL:
            case ProjectPackage.SPREADSHEET__SPREADSHEET_PATH:
                if (SpreadsheetImpl.this.eResource() != null)
                    SpreadsheetImpl.this.eResource().setModified(true);
            }
        }
    };

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SpreadsheetImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return ProjectPackage.eINSTANCE.getSpreadsheet();
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
        //Lagutko, 18.08.2009, update SpreadsheetPath
        if (spreadsheetType == SpreadsheetType.NEO4J_SPREADSHEET) {
            IPath path = new Path(getSpreadsheetPath().toString());
            String last = path.lastSegment().replaceAll(oldName, newName);            
            IPath newPath = path.removeLastSegments(1);
            newPath = newPath.append(last);
            try {
                setSpreadsheetPath(URLUtils.constructURL(new File("."), newPath.toOSString()));
            }
            catch (MalformedURLException e) {
                ProjectPlugin.log(null, e);
            }
        }
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.SPREADSHEET__NAME, oldName, name));
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
            while (parent != null) {
                if (parent instanceof Project) {
                    return (Project)parent;
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
            InternalEObject oldProjectInternal = (InternalEObject)projectInternal;
            projectInternal = (Project)eResolveProxy(oldProjectInternal);
            if (projectInternal != oldProjectInternal) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, ProjectPackage.SPREADSHEET__PROJECT_INTERNAL,
                            oldProjectInternal, projectInternal));
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
    public NotificationChain basicSetProjectInternal(Project newProjectInternal, NotificationChain msgs) {
        Project oldProjectInternal = projectInternal;
        projectInternal = newProjectInternal;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.SPREADSHEET__PROJECT_INTERNAL, oldProjectInternal, newProjectInternal);
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
                msgs = ((InternalEObject)projectInternal).eInverseRemove(this, ProjectPackage.PROJECT__ELEMENTS_INTERNAL,
                        Project.class, msgs);
            if (newProjectInternal != null)
                msgs = ((InternalEObject)newProjectInternal).eInverseAdd(this, ProjectPackage.PROJECT__ELEMENTS_INTERNAL,
                        Project.class, msgs);
            msgs = basicSetProjectInternal(newProjectInternal, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.SPREADSHEET__PROJECT_INTERNAL, newProjectInternal,
                    newProjectInternal));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RubyProject getRubyProjectInternal() {
        if (rubyProjectInternal != null && rubyProjectInternal.eIsProxy()) {
            InternalEObject oldRubyProjectInternal = (InternalEObject)rubyProjectInternal;
            rubyProjectInternal = (RubyProject)eResolveProxy(oldRubyProjectInternal);
            if (rubyProjectInternal != oldRubyProjectInternal) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, ProjectPackage.SPREADSHEET__RUBY_PROJECT_INTERNAL,
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
    public NotificationChain basicSetRubyProjectInternal(RubyProject newRubyProjectInternal, NotificationChain msgs) {
        RubyProject oldRubyProjectInternal = rubyProjectInternal;
        rubyProjectInternal = newRubyProjectInternal;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.SPREADSHEET__RUBY_PROJECT_INTERNAL, oldRubyProjectInternal, newRubyProjectInternal);
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
        if (newRubyProjectInternal != rubyProjectInternal) {
            NotificationChain msgs = null;
            if (rubyProjectInternal != null)
                msgs = ((InternalEObject)rubyProjectInternal).eInverseRemove(this,
                        ProjectPackage.RUBY_PROJECT__RUBY_ELEMENTS_INTERNAL, RubyProject.class, msgs);
            if (newRubyProjectInternal != null)
                msgs = ((InternalEObject)newRubyProjectInternal).eInverseAdd(this,
                        ProjectPackage.RUBY_PROJECT__RUBY_ELEMENTS_INTERNAL, RubyProject.class, msgs);
            msgs = basicSetRubyProjectInternal(newRubyProjectInternal, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.SPREADSHEET__RUBY_PROJECT_INTERNAL,
                    newRubyProjectInternal, newRubyProjectInternal));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public URL getSpreadsheetPath() {
        return spreadsheetPath;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSpreadsheetPath(URL newSpreadsheetPath) {
        URL oldSpreadsheetPath = spreadsheetPath;
        spreadsheetPath = newSpreadsheetPath;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.SPREADSHEET__SPREADSHEET_PATH, oldSpreadsheetPath,
                    spreadsheetPath));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SpreadsheetType getSpreadsheetType() {
        return spreadsheetType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSpreadsheetType(SpreadsheetType newSpreadsheetType) {
        SpreadsheetType oldSpreadsheetType = spreadsheetType;
        spreadsheetType = newSpreadsheetType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.SPREADSHEET__SPREADSHEET_TYPE, oldSpreadsheetType,
                    spreadsheetType));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
        case ProjectPackage.SPREADSHEET__PROJECT_INTERNAL:
            if (projectInternal != null)
                msgs = ((InternalEObject)projectInternal).eInverseRemove(this, ProjectPackage.PROJECT__ELEMENTS_INTERNAL,
                        Project.class, msgs);
            return basicSetProjectInternal((Project)otherEnd, msgs);
        case ProjectPackage.SPREADSHEET__RUBY_PROJECT_INTERNAL:
            if (rubyProjectInternal != null)
                msgs = ((InternalEObject)rubyProjectInternal).eInverseRemove(this,
                        ProjectPackage.RUBY_PROJECT__RUBY_ELEMENTS_INTERNAL, RubyProject.class, msgs);
            return basicSetRubyProjectInternal((RubyProject)otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
        case ProjectPackage.SPREADSHEET__PROJECT_INTERNAL:
            return basicSetProjectInternal(null, msgs);
        case ProjectPackage.SPREADSHEET__RUBY_PROJECT_INTERNAL:
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
        case ProjectPackage.SPREADSHEET__NAME:
            return getName();
        case ProjectPackage.SPREADSHEET__PROJECT_INTERNAL:
            if (resolve)
                return getProjectInternal();
            return basicGetProjectInternal();
        case ProjectPackage.SPREADSHEET__RUBY_PROJECT_INTERNAL:
            if (resolve)
                return getRubyProjectInternal();
            return basicGetRubyProjectInternal();
        case ProjectPackage.SPREADSHEET__SPREADSHEET_PATH:
            return getSpreadsheetPath();
        case ProjectPackage.SPREADSHEET__SPREADSHEET_TYPE:
            return getSpreadsheetType();
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
        case ProjectPackage.SPREADSHEET__NAME:
            setName((String)newValue);
            return;
        case ProjectPackage.SPREADSHEET__PROJECT_INTERNAL:
            setProjectInternal((Project)newValue);
            return;
        case ProjectPackage.SPREADSHEET__RUBY_PROJECT_INTERNAL:
            setRubyProjectInternal((RubyProject)newValue);
            return;
        case ProjectPackage.SPREADSHEET__SPREADSHEET_PATH:
            setSpreadsheetPath((URL)newValue);
            return;
        case ProjectPackage.SPREADSHEET__SPREADSHEET_TYPE:
            setSpreadsheetType((SpreadsheetType)newValue);
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
        case ProjectPackage.SPREADSHEET__NAME:
            setName(NAME_EDEFAULT);
            return;
        case ProjectPackage.SPREADSHEET__PROJECT_INTERNAL:
            setProjectInternal((Project)null);
            return;
        case ProjectPackage.SPREADSHEET__RUBY_PROJECT_INTERNAL:
            setRubyProjectInternal((RubyProject)null);
            return;
        case ProjectPackage.SPREADSHEET__SPREADSHEET_PATH:
            setSpreadsheetPath(SPREADSHEET_PATH_EDEFAULT);
            return;
        case ProjectPackage.SPREADSHEET__SPREADSHEET_TYPE:
            setSpreadsheetType(SPREADSHEET_TYPE_EDEFAULT);
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
        case ProjectPackage.SPREADSHEET__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case ProjectPackage.SPREADSHEET__PROJECT_INTERNAL:
            return projectInternal != null;
        case ProjectPackage.SPREADSHEET__RUBY_PROJECT_INTERNAL:
            return rubyProjectInternal != null;
        case ProjectPackage.SPREADSHEET__SPREADSHEET_PATH:
            return SPREADSHEET_PATH_EDEFAULT == null ? spreadsheetPath != null : !SPREADSHEET_PATH_EDEFAULT.equals(spreadsheetPath);
        case ProjectPackage.SPREADSHEET__SPREADSHEET_TYPE:
            return SPREADSHEET_TYPE_EDEFAULT == null ? spreadsheetType != null : !SPREADSHEET_TYPE_EDEFAULT.equals(spreadsheetType);
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
            case ProjectPackage.SPREADSHEET__NAME:
                return ProjectPackage.PROJECT_ELEMENT__NAME;
            case ProjectPackage.SPREADSHEET__PROJECT_INTERNAL:
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
            case ProjectPackage.SPREADSHEET__RUBY_PROJECT_INTERNAL:
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
                return ProjectPackage.SPREADSHEET__NAME;
            case ProjectPackage.PROJECT_ELEMENT__PROJECT_INTERNAL:
                return ProjectPackage.SPREADSHEET__PROJECT_INTERNAL;
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
                return ProjectPackage.SPREADSHEET__RUBY_PROJECT_INTERNAL;
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
        result.append(", spreadsheetPath: "); //$NON-NLS-1$
        result.append(spreadsheetPath);
        result.append(", spreadsheetType: "); //$NON-NLS-1$
        result.append(spreadsheetType);
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
        return "uss";
    }

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter) {
        for (Iterator i = eAdapters().iterator(); i.hasNext();) {
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
            return new WorkbenchAdapter(){

                @Override
                public String getLabel(Object object) {
                    return getName();
                }

            };
        }
        return null;
    }

    /*
     * Getter and setter for Resource
     * (non-Javadoc)
     * @see net.refractions.udig.project.IRubyFile#getResource()
     */

    public IResource getResource() {
        if (resource == null) {
            resource = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(new Path(spreadsheetPath.getPath()))[0];
        }

        return resource;
    }

    public void setResource(IResource resource) {
        if (resource != null) {
            try {
                setSpreadsheetPath(resource.getLocationURI().toURL());
            } catch (MalformedURLException e) {
                ProjectPlugin.log(Messages.Spreadsheet_UncorrectResource, e);
            }
        }

        this.resource = resource;
    }

} //SpreadsheetImpl
