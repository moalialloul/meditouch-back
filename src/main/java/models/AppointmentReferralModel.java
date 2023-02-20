package models;

public class AppointmentReferralModel {
	int referralId;
	int userFk;
	int appointmentFk;
	String referralDescription;
	int referredByBusinessAccountFk;
	int referredToBusinessAccountFk;

	public AppointmentReferralModel() {
		super();
	}

	@Override
	public String toString() {
		return "{\"referralId\":" + referralId + ", \"userFk\":" + userFk + ", \"appointmentFk\":" + appointmentFk
				+ ", \"referralDescription\":\"" + referralDescription + "\", \"referredByBusinessAccountFk\":"
				+ referredByBusinessAccountFk + ", \"referredToBusinessAccountFk\":" + referredToBusinessAccountFk
				+ "}";
	}

	public AppointmentReferralModel(int referralId, int userFk, int appointmentFk, String referralDescription,
			int referredByBusinessAccountFk, int referredToBusinessAccountFk) {
		super();
		this.referralId = referralId;
		this.userFk = userFk;
		this.appointmentFk = appointmentFk;
		this.referralDescription = referralDescription;
		this.referredByBusinessAccountFk = referredByBusinessAccountFk;
		this.referredToBusinessAccountFk = referredToBusinessAccountFk;
	}

	public int getReferralId() {
		return referralId;
	}

	public void setReferralId(int referralId) {
		this.referralId = referralId;
	}

	public int getUserFk() {
		return userFk;
	}

	public void setUserFk(int userFk) {
		this.userFk = userFk;
	}

	public int getAppointmentFk() {
		return appointmentFk;
	}

	public void setAppointmentFk(int appointmentFk) {
		this.appointmentFk = appointmentFk;
	}

	public String getReferralDescription() {
		return referralDescription;
	}

	public void setReferralDescription(String referralDescription) {
		this.referralDescription = referralDescription;
	}

	public int getReferredByBusinessAccountFk() {
		return referredByBusinessAccountFk;
	}

	public void setReferredByBusinessAccountFk(int referredByBusinessAccountFk) {
		this.referredByBusinessAccountFk = referredByBusinessAccountFk;
	}

	public int getReferredToBusinessAccountFk() {
		return referredToBusinessAccountFk;
	}

	public void setReferredToBusinessAccountFk(int referredToBusinessAccountFk) {
		this.referredToBusinessAccountFk = referredToBusinessAccountFk;
	}
}
