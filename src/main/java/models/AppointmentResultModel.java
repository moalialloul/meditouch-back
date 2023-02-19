package models;

public class AppointmentResultModel {
	int resultId;
	int appointmentFk;
	String resultDescription;
	String resultDocument;

	public AppointmentResultModel() {
	}

	@Override
	public String toString() {
		return "{\"resultId\":" + resultId + ", \"appointmentFk\":" + appointmentFk + ", \"resultDescription\":\""
				+ resultDescription + "\", \"resultDocument\":\"" + resultDocument + "\"}";
	}

	public AppointmentResultModel(int resultId, int appointmentFk, String resultDescription, String resultDocument) {
		super();
		this.resultId = resultId;
		this.appointmentFk = appointmentFk;
		this.resultDescription = resultDescription;
		this.resultDocument = resultDocument;
	}

	public int getResultId() {
		return resultId;
	}

	public void setResultId(int resultId) {
		this.resultId = resultId;
	}

	public int getAppointmentFk() {
		return appointmentFk;
	}

	public void setAppointmentFk(int appointmentFk) {
		this.appointmentFk = appointmentFk;
	}

	public String getResultDescription() {
		return resultDescription;
	}

	public void setResultDescription(String resultDescription) {
		this.resultDescription = resultDescription;
	}

	public String getResultDocument() {
		return resultDocument;
	}

	public void setResultDocument(String resultDocument) {
		this.resultDocument = resultDocument;
	}
}
