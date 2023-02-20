package models;

public class PostponeAppointmentModel {
	int appointmentId;
	int slotFk;
	int isApproved = 0;
	int businessAccountFk;

	public int getBusinessAccountFk() {
		return businessAccountFk;
	}

	public void setBusinessAccountFk(int businessAccountFk) {
		this.businessAccountFk = businessAccountFk;
	}

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

	public PostponeAppointmentModel(int appointmentId, int slotFk, int isApproved, int businessAccountFk) {
		super();
		this.appointmentId = appointmentId;
		this.slotFk = slotFk;
		this.isApproved = isApproved;
		this.businessAccountFk = businessAccountFk;
	}

	public PostponeAppointmentModel() {
		super();
	}

	@Override
	public String toString() {
		return "{\"businessAccountFk\":" + businessAccountFk + ",\"appointmentId\":" + appointmentId + ", \"slotFk\":"
				+ slotFk + ", \"isApproved\":" + isApproved + "}";
	}

}
