package models;

public class PostponeAppointmentModel {
	int appointmentId;
	int newSlotFk;
	int oldSlotFk;
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

	
	public int getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(int isApproved) {
		this.isApproved = isApproved;
	}

	
	public PostponeAppointmentModel() {
		super();
	}

	@Override
	public String toString() {
		return "{\"businessAccountFk\":" + businessAccountFk + ",\"appointmentId\":" + appointmentId + ", \"oldSlotFk\":"
				+ oldSlotFk + "\"newSlotFk\":" + newSlotFk+", \"isApproved\":" + isApproved + "}";
	}

	public PostponeAppointmentModel(int appointmentId, int newSlotFk, int oldSlotFk, int isApproved,
			int businessAccountFk) {
		super();
		this.appointmentId = appointmentId;
		this.newSlotFk = newSlotFk;
		this.oldSlotFk = oldSlotFk;
		this.isApproved = isApproved;
		this.businessAccountFk = businessAccountFk;
	}

	public int getNewSlotFk() {
		return newSlotFk;
	}

	public void setNewSlotFk(int newSlotFk) {
		this.newSlotFk = newSlotFk;
	}

	public int getOldSlotFk() {
		return oldSlotFk;
	}

	public void setOldSlotFk(int oldSlotFk) {
		this.oldSlotFk = oldSlotFk;
	}

}
