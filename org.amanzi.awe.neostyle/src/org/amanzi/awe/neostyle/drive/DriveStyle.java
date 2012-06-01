/**
 * 
 */
package org.amanzi.awe.neostyle.drive;

import java.awt.Color;

import org.amanzi.awe.neostyle.BaseNeoStyle;

/**
 * 
 * @author Bondoronok_p
 */
public class DriveStyle extends BaseNeoStyle implements Cloneable {

	private static final long serialVersionUID = 6384086280356718948L;

	/*
	 * colors
	 */
	private Color locationColor;
	private Color labelColor;
	private Color lineColor;

	private Integer fontSize;
	private String locationLabelType;
	private String measurementNameProperty;

	public DriveStyle() {
		super();
	}

	public Color getLabelColor() {
		return labelColor;
	}

	public Integer getFontSize() {
		return fontSize;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public void setLabelColor(Color labelColor) {
		this.labelColor = labelColor;
	}

	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	public Color getLocationColor() {
		return locationColor;
	}

	public void setLocationColor(Color locationColor) {
		this.locationColor = locationColor;
	}

	public String getLocationLabelType() {
		return locationLabelType;
	}

	public void setLocationLabelType(String locationLabelType) {
		this.locationLabelType = locationLabelType;
	}

	public String getMeasurementNameProperty() {
		return measurementNameProperty;
	}

	public void setMeasurementNameProperty(String measurementNameProperty) {
		this.measurementNameProperty = measurementNameProperty;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fontSize == null) ? 0 : fontSize.hashCode());
		result = prime * result
				+ ((labelColor == null) ? 0 : labelColor.hashCode());
		result = prime * result
				+ ((lineColor == null) ? 0 : lineColor.hashCode());
		result = prime * result
				+ ((locationColor == null) ? 0 : locationColor.hashCode());
		result = prime
				* result
				+ ((locationLabelType == null) ? 0 : locationLabelType
						.hashCode());
		result = prime
				* result
				+ ((measurementNameProperty == null) ? 0
						: measurementNameProperty.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DriveStyle other = (DriveStyle) obj;
		if (fontSize == null) {
			if (other.fontSize != null)
				return false;
		} else if (!fontSize.equals(other.fontSize))
			return false;
		if (labelColor == null) {
			if (other.labelColor != null)
				return false;
		} else if (!labelColor.equals(other.labelColor))
			return false;
		if (lineColor == null) {
			if (other.lineColor != null)
				return false;
		} else if (!lineColor.equals(other.lineColor))
			return false;
		if (locationColor == null) {
			if (other.locationColor != null)
				return false;
		} else if (!locationColor.equals(other.locationColor))
			return false;
		if (locationLabelType == null) {
			if (other.locationLabelType != null)
				return false;
		} else if (!locationLabelType.equals(other.locationLabelType))
			return false;
		if (measurementNameProperty == null) {
			if (other.measurementNameProperty != null)
				return false;
		} else if (!measurementNameProperty
				.equals(other.measurementNameProperty))
			return false;
		return true;
	}

	@Override
	protected Object clone() {
		DriveStyle clone = new DriveStyle();
		clone.setFontSize(getFontSize());
		clone.setLabelColor(getLabelColor());
		clone.setLineColor(getLineColor());
		clone.setLocationColor(getLocationColor());
		clone.setLocationLabelType(getLocationLabelType());
		clone.setMeasurementNameProperty(getMeasurementNameProperty());
		return clone;
	}

}
