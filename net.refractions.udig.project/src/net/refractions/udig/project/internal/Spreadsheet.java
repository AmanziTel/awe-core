/**
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * $Id$
 */
package net.refractions.udig.project.internal;

import java.net.URL;

import java.util.List;
import net.refractions.udig.project.ISpreadsheet;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ruby Class</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.refractions.udig.project.internal.Spreadsheet#getSpreadsheetPath <em>Spreadsheet Path</em>}</li>
 *   <li>{@link net.refractions.udig.project.internal.Spreadsheet#getSpreadsheetType <em>Spreadsheet Type</em>}</li>
 *   <li>{@link net.refractions.udig.project.internal.Spreadsheet#getChildSpreadsheets <em>Child Spreadsheets</em>}</li>
 *   <li>{@link net.refractions.udig.project.internal.Spreadsheet#getParentSpreadsheet <em>Parent Spreadsheet</em>}</li>
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
	 * Returns the value of the '<em><b>Spreadsheet Path</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Spreadsheet Path</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Spreadsheet Path</em>' attribute.
	 * @see #setSpreadsheetPath(URL)
	 * @see net.refractions.udig.project.internal.ProjectPackage#getSpreadsheet_SpreadsheetPath()
	 * @model default="" dataType="net.refractions.udig.project.internal.URL"
	 * @generated
	 */
	URL getSpreadsheetPath();

	/**
	 * Sets the value of the '{@link net.refractions.udig.project.internal.Spreadsheet#getSpreadsheetPath <em>Spreadsheet Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Spreadsheet Path</em>' attribute.
	 * @see #getSpreadsheetPath()
	 * @generated
	 */
	void setSpreadsheetPath(URL value);

	/**
	 * Returns the value of the '<em><b>Spreadsheet Type</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Spreadsheet Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Spreadsheet Type</em>' attribute.
	 * @see #setSpreadsheetType(SpreadsheetType)
	 * @see net.refractions.udig.project.internal.ProjectPackage#getSpreadsheet_SpreadsheetType()
	 * @model default="" dataType="net.refractions.udig.project.internal.SpreadsheetType"
	 * @generated
	 */
	SpreadsheetType getSpreadsheetType();

	/**
	 * Sets the value of the '{@link net.refractions.udig.project.internal.Spreadsheet#getSpreadsheetType <em>Spreadsheet Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Spreadsheet Type</em>' attribute.
	 * @see #getSpreadsheetType()
	 * @generated
	 */
	void setSpreadsheetType(SpreadsheetType value);

	/**
	 * Returns the value of the '<em><b>Child Spreadsheets</b></em>' containment reference list.
	 * The list contents are of type {@link net.refractions.udig.project.internal.Spreadsheet}.
	 * It is bidirectional and its opposite is '{@link net.refractions.udig.project.internal.Spreadsheet#getParentSpreadsheet <em>Parent Spreadsheet</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Child Spreadsheets</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Child Spreadsheets</em>' containment reference list.
	 * @see net.refractions.udig.project.internal.ProjectPackage#getSpreadsheet_ChildSpreadsheets()
	 * @see net.refractions.udig.project.internal.Spreadsheet#getParentSpreadsheet
	 * @model type="net.refractions.udig.project.internal.Spreadsheet" opposite="parentSpreadsheet" containment="true"
	 * @generated
	 */
	List<Spreadsheet> getChildSpreadsheets();

	/**
	 * Returns the value of the '<em><b>Parent Spreadsheet</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link net.refractions.udig.project.internal.Spreadsheet#getChildSpreadsheets <em>Child Spreadsheets</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent Spreadsheet</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent Spreadsheet</em>' container reference.
	 * @see #setParentSpreadsheet(Spreadsheet)
	 * @see net.refractions.udig.project.internal.ProjectPackage#getSpreadsheet_ParentSpreadsheet()
	 * @see net.refractions.udig.project.internal.Spreadsheet#getChildSpreadsheets
	 * @model opposite="childSpreadsheets" transient="false"
	 * @generated
	 */
	Spreadsheet getParentSpreadsheet();

	/**
	 * Sets the value of the '{@link net.refractions.udig.project.internal.Spreadsheet#getParentSpreadsheet <em>Parent Spreadsheet</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent Spreadsheet</em>' container reference.
	 * @see #getParentSpreadsheet()
	 * @generated
	 */
	void setParentSpreadsheet(Spreadsheet value);

} // Spreadsheet
