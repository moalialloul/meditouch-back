package models;

public class AppointmentPrescriptionModel {
	int prescriptionId;

	public int getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(int prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	int appointmentFk;
	String prescriptionDescription;

	@Override
	public String toString() {
		return "\"prescriptionId\":" + prescriptionId + ",\"appointmentFk\":" + appointmentFk
				+ ", \"prescriptionDescription\":\"" + prescriptionDescription + "\"}";
	}

	public AppointmentPrescriptionModel() {
	}

	public AppointmentPrescriptionModel(int appointmentFk, String prescriptionDescription, int prescriptionId) {
		super();
		this.appointmentFk = appointmentFk;
		this.prescriptionDescription = prescriptionDescription;
		this.prescriptionId = prescriptionId;
	}

	public int getAppointmentFk() {
		return appointmentFk;
	}

	public void setAppointmentFk(int appointmentFk) {
		this.appointmentFk = appointmentFk;
	}

	public String getPrescriptionDescription() {
		return prescriptionDescription;
	}

	public void setPrescriptionDescription(String prescriptionDescription) {
		this.prescriptionDescription = prescriptionDescription;
	}
}
