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
package org.amanzi.neo.core.database.nodes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class that wraps ID of Cell and provides it in different forms
 * 
 * @author Lagutko_N
 */

public class CellID {

	/*
	 * Name of Column
	 */
	private String columnName;

	/*
	 * Name of Row
	 */
	private String rowName;

	/*
	 * Index of Column
	 */
	private Integer columnIndex;

	/*
	 * Index of Row
	 */
	private Integer rowIndex;

	/*
	 * Id of Column
	 */
	private String fullID;

	/**
	 * Constructor for ID from Strings
	 * 
	 * @param row
	 *            name of Row
	 * @param column
	 *            name of Column
	 */
	public CellID(String row, String column) {
		columnName = column;
		rowName = row;

		fullID = column + row;

		columnIndex = getColumnIndexFromCellID(fullID);
		rowIndex = getRowIndexFromCellID(fullID);
	}

	/**
	 * Constructor for ID from indexes
	 * 
	 * @param row
	 *            index of Row
	 * @param column
	 *            index of Column
	 */
	public CellID(Integer row, Integer column) {
		columnIndex = column;
		rowIndex = row;

		fullID = getCellIDfromRowColumn(row, column);

		columnName = getColumnLetter(column);
		rowName = Integer.toString(row + 1);
	}

	/**
	 * Constructor for ID from String ID
	 * 
	 * @param fullId
	 *            Cell ID
	 */
	public CellID(String fullId) {
		fullID = fullId;

		columnIndex = getColumnIndexFromCellID(fullId);
		rowIndex = getRowIndexFromCellID(fullId);

		columnName = getColumnLetter(columnIndex);
		rowName = Integer.toString(rowIndex + 1);
	}

	/**
	 * Returns name of Column
	 * 
	 * @return name of Column
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * Returns name of Row
	 * 
	 * @return name of Row
	 */
	public String getRowName() {
		return rowName;
	}

	/**
	 * Returns index of Column
	 * 
	 * @return index of Column
	 */
	public Integer getColumnIndex() {
		return columnIndex;
	}

	/**
	 * Returns index of Row
	 * 
	 * @return index of Row
	 */
	public Integer getRowIndex() {
		return rowIndex;
	}

	/**
	 * Returns Full ID
	 * 
	 * @return full ID
	 */
	public String getFullID() {
		return fullID;
	}

	public String toString() {
		return fullID;
	}

	public static int getColumnIndexFromCellID(String cellID) {
		String STD_HEADINGS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		// Lagutko, 4.06.2009, CellId can contain more than one letter, so count
		// ColumnIndex until we have letter in CellId
		String id = cellID.toUpperCase();
		int i = 0;

		char c;
		int index = 0;

		while (i < id.length() && !Character.isDigit(c = id.charAt(i))) {
			index = index * 26;

			index = index + (STD_HEADINGS.indexOf(c) + 1);

			i++;
		}

		return index - 1;
	}

	public static int getRowIndexFromCellID(String cellID) {
		String regex = "\\d+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(cellID);

		int ret = 0;
		while (matcher.find()) {
			ret = Integer.parseInt(matcher.group());
		}

		return ret - 1;
	}

	public static String getColumnLetter(int columnIndex) {
		// Lagutko, 4.06.2009, correct bug with columns more than 26
		String STD_HEADINGS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder letterIndex = new StringBuilder();
		int iColumn = columnIndex;

		letterIndex.insert(0, STD_HEADINGS.charAt(columnIndex % 26));

		iColumn = iColumn / 26;

		STD_HEADINGS = "AABCDEFGHIJKLMNOPQRSTUVWXYZ";

		while (iColumn > 0) {
			int index = iColumn % 27;

			letterIndex.insert(0, STD_HEADINGS.charAt(index));

			iColumn = iColumn / 27;
		}

		return letterIndex.toString();
	}

	public static String getCellIDfromRowColumn(int row, int column) {
		String letterIndex = getColumnLetter(column);

		return letterIndex + Integer.toString(row + 1);
	}

}
