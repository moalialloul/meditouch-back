package models;

import java.sql.Timestamp;

public class GlobalSearchModel {
	int specialityFk = -1;
	double minDistance = -1;
	double maxDistance = -1;

	double minPrice = -1;
	double maxPrice = -1;

	double myLongitude = -1;
	double myLatitude = -1;

	Timestamp minDateTime;
	Timestamp maxDateTime;

	public Timestamp getMaxDateTime() {
		return maxDateTime;
	}

	public void setMaxDateTime(Timestamp maxDateTime) {
		this.maxDateTime = maxDateTime;
	}

	public double getMyLongitude() {
		return myLongitude;
	}

	public void setMyLongitude(double myLongitude) {
		this.myLongitude = myLongitude;
	}

	public double getMyLatitude() {
		return myLatitude;
	}

	public void setMyLatitude(double myLatitude) {
		this.myLatitude = myLatitude;
	}

	public int getSpecialityFk() {
		return specialityFk;
	}

	public void setSpecialityFk(int specialityFk) {
		this.specialityFk = specialityFk;
	}

	public double getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(double minDistance) {
		this.minDistance = minDistance;
	}

	public double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(double minPrice) {
		this.minPrice = minPrice;
	}

	public GlobalSearchModel(int specialityFk, double minDistance, double maxDistance, double minPrice, double maxPrice,
			double myLatitude, double myLongitude, Timestamp minDateTime, Timestamp maxDateTime) {
		super();
		this.specialityFk = specialityFk;
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;

		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.myLatitude = myLatitude;
		this.myLongitude = myLongitude;
		this.minDateTime = minDateTime;
		this.maxDateTime = maxDateTime;

	}

	public GlobalSearchModel() {

	}

	@Override
	public String toString() {
		return "{\"specialityFk\":" + specialityFk + ",\"minDistance\":" + minDistance + ",\"maxDistance\":"
				+ maxDistance + ", \"minPrice\":" + minPrice + ", \"maxPrice\":" + maxPrice + ", \"myLatitude\" : "
				+ myLatitude + ", \"myLongitude\":" + myLongitude + ", \"minDateTime\":" + minDateTime
				+ ",\"maxDateTime\":" + maxDateTime + "}";
	}

	public double getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(double maxDistance) {
		this.maxDistance = maxDistance;
	}

	public double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(double maxPrice) {
		this.maxPrice = maxPrice;
	}
}
