package org.amanzi.neo.loader.core.newsaver;

public class Signal {
	private String chan_code = "";
	private float[] chan_array = new float[2];

	/**
	 * create class instance
	 */
	public Signal() {
	}

	public float[] getChanarray() {
		return chan_array;
	}

	public String getChanCode() {
		return chan_code;
	}

	public void setChanCode(String chan_code) {
		this.chan_code = chan_code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((chan_code == null) ? 0 : chan_code.hashCode());
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
		Signal other = (Signal) obj;
		if (chan_code == null) {
			if (other.chan_code != null)
				return false;
		} else if (!chan_code.equals(other.chan_code))
			return false;
		return true;
	}
}
