/**
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * $Id$
 */
package net.refractions.udig.project.internal;

import java.util.List;

import net.refractions.udig.project.IRubyProject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ruby Project</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.refractions.udig.project.internal.RubyProject#getRubyElementsInternal <em>Ruby Elements Internal</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.refractions.udig.project.internal.ProjectPackage#getRubyProject()
 * @model superTypes="net.refractions.udig.project.internal.ProjectElement net.refractions.udig.project.internal.IRubyProject"
 * @generated
 */
public interface RubyProject extends ProjectElement, IRubyProject {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Ruby Elements Internal</b></em>' reference list.
	 * The list contents are of type {@link net.refractions.udig.project.internal.RubyProjectElement}.
	 * It is bidirectional and its opposite is '{@link net.refractions.udig.project.internal.RubyProjectElement#getRubyProjectInternal <em>Ruby Project Internal</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ruby Elements Internal</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ruby Elements Internal</em>' reference list.
	 * @see net.refractions.udig.project.internal.ProjectPackage#getRubyProject_RubyElementsInternal()
	 * @see net.refractions.udig.project.internal.RubyProjectElement#getRubyProjectInternal
	 * @model type="net.refractions.udig.project.internal.RubyProjectElement" opposite="rubyProjectInternal"
	 * @generated
	 */
	List<RubyProjectElement> getRubyElementsInternal();
	
	/**
	 * Function that adds RubyProjectElement to RubyProject
	 * 
	 * Note: we don't load RubyProjectElement from file like any other EMF object. 
	 * 
	 * @param element
	 * @author Lagutko_N
	 */
	
	public void addRubyElementInternal(RubyProjectElement element);
	
	/**
	 * Function that removes RubyProjectElement from RubyProject
	 * 
	 * Note: we don't load RubyProjectElement from file like any other EMF object. 
	 * 
	 * @param element
	 * @author Lagutko_N
	 */
	
	public void removeRubyElementInternal(RubyProjectElement element);

} // RubyProject
