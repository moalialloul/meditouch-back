package models;

public class BusinessAccountModel {
	int businessAccountId;
	int userFk;
	int specialityFk;
	String biography;
	String clinicLocation;
	double clinicLocationLongitude;
	double clinicLocationLatitude;

	public BusinessAccountModel() {

	}

	public int getBusinessAccountId() {
		return businessAccountId;
	}

	public void setBusinessAccountId(int businessAccountId) {
		this.businessAccountId = businessAccountId;
	}

	public int getUserFk() {
		return userFk;
	}

	@Override
	public String toString() {
		return "{\"businessAccountId\":" + businessAccountId + ",\"userFk\":" + userFk + ", \"specialityFk\":"
				+ specialityFk + ", \"biography\":\"" + biography + "\", \"clinicLocation\":\"" + clinicLocation
				+ "\", \"clinicLocationLongitude\":" + clinicLocationLongitude + ", \"clinicLocationLatitude\":"
				+ clinicLocationLatitude + "}";
	}

	public BusinessAccountModel(int businessAccountId, int userFk, int specialityFk, String biography,
			String clinicLocation, double clinicLocationLongitude, double clinicLocationLatitude) {
		super();
		this.businessAccountId = businessAccountId;
		this.userFk = userFk;
		this.specialityFk = specialityFk;
		this.biography = biography;
		this.clinicLocation = clinicLocation;
		this.clinicLocationLongitude = clinicLocationLongitude;
		this.clinicLocationLatitude = clinicLocationLatitude;
	}

	public void setUserFk(int userFk) {
		this.userFk = userFk;
	}

	public int getSpecialityFk() {
		return specialityFk;
	}

	public void setSpecialityFk(int specialityFk) {
		this.specialityFk = specialityFk;
	}

	public String getBiography() {
		return biography;
	}

	public void setBiography(String biography) {
		this.biography = biography;
	}

	public String getClinicLocation() {
		return clinicLocation;
	}

	public void setClinicLocation(String clinicLocation) {
		this.clinicLocation = clinicLocation;
	}

	public double getClinicLocationLongitude() {
		return clinicLocationLongitude;
	}

	public void setClinicLocationLongitude(double clinicLocationLongitude) {
		this.clinicLocationLongitude = clinicLocationLongitude;
	}

	public double getClinicLocationLatitude() {
		return clinicLocationLatitude;
	}

	public void setClinicLocationLatitude(double clinicLocationLatitude) {
		this.clinicLocationLatitude = clinicLocationLatitude;
	}
}
