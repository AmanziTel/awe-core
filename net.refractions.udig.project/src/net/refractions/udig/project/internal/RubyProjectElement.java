/**
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * $Id$
 */
package net.refractions.udig.project.internal;

import net.refractions.udig.project.IRubyProjectElement;

import org.eclipse.core.runtime.IAdaptable;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ruby Project Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.refractions.udig.project.internal.RubyProjectElement#getRubyProjectInternal <em>Ruby Project Internal</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.refractions.udig.project.internal.ProjectPackage#getRubyProjectElement()
 * @model interface="true" abstract="true" superTypes="net.refractions.udig.project.internal.ProjectElement net.refractions.udig.project.internal.IRubyProjectElement net.refractions.udig.project.internal.IAdaptable"
 * @generated
 */
public interface RubyProjectElement extends ProjectElement,
		IRubyProjectElement, IAdaptable {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Ruby Project Internal</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link net.refractions.udig.project.internal.RubyProject#getRubyElementsInternal <em>Ruby Elements Internal</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ruby Project Internal</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ruby Project Internal</em>' reference.
	 * @see #setRubyProjectInternal(RubyProject)
	 * @see net.refractions.udig.project.internal.ProjectPackage#getRubyProjectElement_RubyProjectInternal()
	 * @see net.refractions.udig.project.internal.RubyProject#getRubyElementsInternal
	 * @model opposite="rubyElementsInternal"
	 * @generated
	 */
	RubyProject getRubyProjectInternal();

	/**
	 * Sets the value of the '{@link net.refractions.udig.project.internal.RubyProjectElement#getRubyProjectInternal <em>Ruby Project Internal</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ruby Project Internal</em>' reference.
	 * @see #getRubyProjectInternal()
	 * @generated
	 */
	void setRubyProjectInternal(RubyProject value);

} // RubyProjectElement
