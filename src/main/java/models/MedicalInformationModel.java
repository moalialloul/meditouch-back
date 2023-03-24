package models;

public class MedicalInformationModel {
	int medicalInformationId;
	int userFk;
	int height;
	int weight;
	String diseasesDescription;
	String vaccinationDescription;

	public MedicalInformationModel() {
		super();
	}

	public int getMedicalInformationId() {
		return medicalInformationId;
	}

	@Override
	public String toString() {
		return "{\"medicalInformationId\":" + medicalInformationId + ", \"userFk\":" + userFk + ", \"height\":" + height
				+ ", \"weight\":" + weight + ", \"diseasesDescription\":\"" + diseasesDescription
				+ "\", \"vaccinationDescription\":\"" + vaccinationDescription + "\"}";
	}

	public MedicalInformationModel(int medicalInformationId, int userFk, int height, int weight,
			String diseasesDescription, String vaccinationDescription) {
		super();
		this.medicalInformationId = medicalInformationId;
		this.userFk = userFk;
		this.height = height;
		this.weight = weight;
		this.diseasesDescription = diseasesDescription;
		this.vaccinationDescription = vaccinationDescription;
	}

	public void setMedicalInformationId(int medicalInformationId) {
		this.medicalInformationId = medicalInformationId;
	}

	public int getUserFk() {
		return userFk;
	}

	public void setUserFk(int userFk) {
		this.userFk = userFk;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getDiseasesDescription() {
		return diseasesDescription;
	}

	public void setDiseasesDescription(String diseasesDescription) {
		this.diseasesDescription = diseasesDescription;
	}

	public String getVaccinationDescription() {
		return vaccinationDescription;
	}

	public void setVaccinationDescription(String vaccinationDescription) {
		this.vaccinationDescription = vaccinationDescription;
	}
}
