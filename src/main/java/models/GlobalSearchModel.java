package models;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class GlobalSearchModel {
	int specialityFk = -1;
	double minDistance = -1;
	double maxDistance = -1;
	int userId = -1;
	double minPrice = -1;
	double maxPrice = -1;
	int isFavorite = -2;
	String searchText = "null";

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	double myLongitude = -1;
	double myLatitude = -1;
	LocalDateTime minAvailability;
	LocalDateTime maxAvailability;

	public LocalDateTime getMaxAvailability() {
		return maxAvailability;
	}

	public void setMaxAvailability(LocalDateTime maxAvailability) {
		this.maxAvailability = maxAvailability;
	}

	LocalDateTime minDateTime;
	LocalDateTime maxDateTime;

	public LocalDateTime getMaxDateTime() {
		return maxDateTime;
	}

	public void setMaxDateTime(LocalDateTime maxDateTime) {
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
			double myLatitude, double myLongitude, LocalDateTime minDateTime, LocalDateTime maxDateTime,
			LocalDateTime minAvailability, LocalDateTime maxAvailability, int userId, int isFavorite,
			String searchText) {
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
		this.minAvailability = minAvailability;
		this.maxAvailability = maxAvailability;
		this.userId = userId;
		this.isFavorite = isFavorite;
		this.searchText = searchText;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public GlobalSearchModel() {

	}

	@Override
	public String toString() {
		return "{\"specialityFk\":" + specialityFk + ",\"minDistance\":" + minDistance + ",\"maxDistance\":"
				+ maxDistance + ", \"minPrice\":" + minPrice + ", \"maxPrice\":" + maxPrice + ", \"myLatitude\" : "
				+ myLatitude + ", \"myLongitude\":" + myLongitude + ", \"minDateTime\":" + minDateTime
				+ ",\"maxDateTime\":" + maxDateTime + ",\"minAvailability\":" + minAvailability
				+ ",\"maxAvailability\":" + maxAvailability + ",\"userId\":" + userId + ",\"isFavorite\":" + isFavorite
				+ ",\"searchText\":\"" + searchText + "\"}";
	}

	public int getIsFavorite() {
		return isFavorite;
	}

	public void setIsFavorite(int isFavorite) {
		this.isFavorite = isFavorite;
	}

	public LocalDateTime getMinAvailability() {
		return minAvailability;
	}

	public void setMinAvailability(LocalDateTime minAvailability) {
		this.minAvailability = minAvailability;
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
