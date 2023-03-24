package models;

public class PostponeAppointmentModel {
	int appointmentId;
	int newSlotFk;
	int oldSlotFk;
	int isApproved = 0;
	int businessAccountFk;
	int businessAccountUserId;
	int userFk;

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
		return "{\"businessAccountUserId\":" + businessAccountUserId + ",\"businessAccountFk\":" + businessAccountFk
				+ ",\"appointmentId\":" + appointmentId + ", \"oldSlotFk\":" + oldSlotFk + "\"newSlotFk\":" + newSlotFk
				+ ", \"isApproved\":" + isApproved + ",\"userFk\":" + userFk + "}";
	}

	public int getBusinessAccountUserId() {
		return businessAccountUserId;
	}

	public void setBusinessAccountUserId(int businessAccountUserId) {
		this.businessAccountUserId = businessAccountUserId;
	}

	public PostponeAppointmentModel(int appointmentId, int newSlotFk, int oldSlotFk, int isApproved,
			int businessAccountFk, int userFk) {
		super();
		this.appointmentId = appointmentId;
		this.newSlotFk = newSlotFk;
		this.oldSlotFk = oldSlotFk;
		this.isApproved = isApproved;
		this.businessAccountFk = businessAccountFk;
		this.userFk = userFk;
	}

	public int getUserFk() {
		return userFk;
	}

	public void setUserFk(int userFk) {
		this.userFk = userFk;
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
