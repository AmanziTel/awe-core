/**
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * $Id$
 */
package net.refractions.udig.project.internal;

import net.refractions.udig.project.ISpreadsheet;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ruby Class</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.refractions.udig.project.internal.Spreadsheet#getSpreadsheetFile <em>Spreadsheet File</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.refractions.udig.project.internal.ProjectPackage#getSpreadsheet()
 * @model superTypes="net.refractions.udig.project.internal.ISpreadsheet net.refractions.udig.project.internal.RubyProjectElement net.refractions.udig.project.internal.ProjectElement"
 * @generated
 */
public interface Spreadsheet extends ISpreadsheet, RubyProjectElement,
		ProjectElement {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Spreadsheet File</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Spreadsheet File</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Spreadsheet File</em>' attribute.
	 * @see #setSpreadsheetFile(String)
	 * @see net.refractions.udig.project.internal.ProjectPackage#getSpreadsheet_SpreadsheetFile()
	 * @model default=""
	 * @generated
	 */
	String getSpreadsheetFile();

	/**
	 * Sets the value of the '{@link net.refractions.udig.project.internal.Spreadsheet#getSpreadsheetFile <em>Spreadsheet File</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Spreadsheet File</em>' attribute.
	 * @see #getSpreadsheetFile()
	 * @generated
	 */
	void setSpreadsheetFile(String value);

} // Spreadsheet
