package org.amanzi.awe.cassidian.structure;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.amanzi.awe.cassidian.constants.*;

public class ServingData implements IXmlTag {

	private String probeId;
	private Calendar deliveryTime;
	private Integer rssi;
	private Integer locationArea;
	private Double frequency;
	private Integer cl;
//	  <servingData>
//      <probeID>PROBE006</probeID>
//      <deliveryTime>2010-05-16T02:40:26,663+00:00</deliveryTime>
//      <rssi>-36</rssi>
//      <locationArea>15998</locationArea>
//      <frequency>390.6375</frequency>
//      <c1>69</c1>
	@Override
	public String getType() {
		return ChildTypes.SERVING_DATA.getId();
	}

	@Override
	public void setValueByTagType(String tagName, Object value) {
		if (tagName.equals(LoaderConstants.PROBE_ID)) {
			probeId = value.toString();
		} else if (tagName.equals(LoaderConstants.DELIVERY_TIME)) {
			calendar.setTimeInMillis(getTime(value.toString()));
			setDeliveryTime(calendar);
		} else if (tagName.equals(LoaderConstants.LOCATION_AREA)) {
			locationArea = Integer.parseInt(value.toString());
		} else if (tagName.equals(LoaderConstants.FREQUENCY)) {
			frequency = Double.parseDouble(value.toString());
		} else if (tagName.equals(LoaderConstants.RSSI)) {
			rssi = Integer.parseInt(value.toString());
		} else if (tagName.equals(LoaderConstants.CL)) {
			cl = Integer.parseInt(value.toString());
		}
	}

	@Override
	public Object getValueByTagType(String tagName) {
		if (tagName.equals(LoaderConstants.PROBE_ID)) {
			return probeId ;
		} else if (tagName.equals(LoaderConstants.DELIVERY_TIME)) {
			return deliveryTime;
		} else if (tagName.equals(LoaderConstants.LOCATION_AREA)) {
			return locationArea;
		} else if (tagName.equals(LoaderConstants.FREQUENCY)) {
			return frequency;
		} else if (tagName.equals(LoaderConstants.RSSI)) {
			return rssi;
		} else if (tagName.equals(LoaderConstants.CL)) {
			return cl;
		}
		return null;
	}

	public String getTimeiInXMLformat(Calendar calendar) {
		try {
			Date calendarDate = calendar.getTime();
			String calendarString = AbstractTOCTTC.SDF.format(calendarDate);
			int i = calendarString.lastIndexOf('+');
			StringBuilder time = new StringBuilder(calendarString.substring(0,
					i += 3)).append(":").append(
					calendarString.substring(i, calendarString.length()));
			// long time2 = SDF.parse(time.toString()).getTime();
			return time.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Long getTime(String stringData) {
		try {
			if (stringData == null) {
				return null;
			}
			int i = stringData.lastIndexOf(':');
			StringBuilder time = new StringBuilder(stringData.substring(0, i))
					.append(stringData.substring(i + 1, stringData.length()));
			long time2;

			time2 = SDF.parse(time.toString()).getTime();
			return time2;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public String getProbeId() {
		return probeId;
	}

	public void setProbeId(String probeId) {
		this.probeId = probeId;
	}

	public Calendar getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Calendar deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public Integer getRssi() {
		return rssi;
	}

	public void setRssi(Integer rssi) {
		this.rssi = rssi;
	}

	public Integer getLocationArea() {
		return locationArea;
	}

	public void setLocationArea(Integer locationArea) {
		this.locationArea = locationArea;
	}

	public Double getFrequency() {
		return frequency;
	}

	public void setFrequency(Double frequency) {
		this.frequency = frequency;
	}

	public Integer getCl() {
		return cl;
	}

	public void setCl(Integer cl) {
		this.cl = cl;
	}

}
