package models;

public class PostponeAppointmentModel {
	int appointmentId;
	int slotFk;
	int isApproved = 0;

	public int getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(int appointmentId) {
		this.appointmentId = appointmentId;
	}

	public int getSlotFk() {
		return slotFk;
	}

	public void setSlotFk(int slotFk) {
		this.slotFk = slotFk;
	}

	public int getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(int isApproved) {
		this.isApproved = isApproved;
	}

	public PostponeAppointmentModel(int appointmentId, int slotFk, int isApproved) {
		super();
		this.appointmentId = appointmentId;
		this.slotFk = slotFk;
		this.isApproved = isApproved;
	}

	public PostponeAppointmentModel() {
		super();
	}

	@Override
	public String toString() {
		return "{\"appointmentId\":" + appointmentId + ", \"slotFk\":" + slotFk + ", \"isApproved\":" + isApproved
				+ "}";
	}

}
