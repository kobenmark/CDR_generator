package com.bgandrew.cdr_generator.model;

/**
 *
 * simple value class for location
 */
public class Location {

	public final double longitude;
	public final double latitude;

	public Location (double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	} 

	@Override
	public String toString() {
		return "(" + latitude + ", " + longitude + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Location other = (Location) obj;
		if (this.latitude != other.latitude)
			return false;
		if (this.longitude != other.longitude)
			return false;
		return true;
	}

}
