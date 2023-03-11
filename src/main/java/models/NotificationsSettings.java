package models;

public class NotificationsSettings {
	int notificationSettingsId = -1;
	boolean onReferral;
	boolean onFavorite;
	boolean onScheduleReminder;
	boolean onAppointmentReservation;
	boolean onAddFeatureEmail;
	boolean onAppointmentReminder;
	int userFk;
	
	public NotificationsSettings() {
	}

	public NotificationsSettings(int notificationSettingsId, boolean onReferral, boolean onFavorite,
			boolean onScheduleReminder, boolean onAppointmentReservation, boolean onAddFeatureEmail, int userFk, boolean onAppointmentReminder) {
		this.notificationSettingsId = notificationSettingsId;
		this.onReferral = onReferral;
		this.onFavorite = onFavorite;
		this.onScheduleReminder = onScheduleReminder;
		this.onAppointmentReservation = onAppointmentReservation;
		this.onAddFeatureEmail = onAddFeatureEmail;
		this.onAppointmentReminder=onAppointmentReminder;
		this.userFk = userFk;
	}

	

	public int getNotificationSettingsId() {
		return notificationSettingsId;
	}

	public void setNotificationSettingsId(int notificationSettingsId) {
		this.notificationSettingsId = notificationSettingsId;
	}

	public boolean getOnReferral() {
		return onReferral;
	}

	public void setOnReferral(boolean onReferral) {
		this.onReferral = onReferral;
	}

	public boolean getOnFavorite() {
		return onFavorite;
	}

	public void setOnFavorite(boolean onFavorite) {
		this.onFavorite = onFavorite;
	}

	public boolean getOnScheduleReminder() {
		return onScheduleReminder;
	}

	public void setOnScheduleReminder(boolean onScheduleReminder) {
		this.onScheduleReminder = onScheduleReminder;
	}

	public boolean getOnAppointmentReservation() {
		return onAppointmentReservation;
	}

	public void setOnAppointmentReservation(boolean onAppointmentReservation) {
		this.onAppointmentReservation = onAppointmentReservation;
	}

	public boolean getOnAddFeatureEmail() {
		return onAddFeatureEmail;
	}

	public void setOnAddFeatureEmail(boolean onAddFeatureEmail) {
		this.onAddFeatureEmail = onAddFeatureEmail;
	}

	public int getUserFk() {
		return userFk;
	}

	public void setUserFk(int userFk) {
		this.userFk = userFk;
	}

	public boolean getOnAppointmentReminder() {
		return onAppointmentReminder;
	}

	public void setOnAppointmentReminder(boolean onAppointmentReminder) {
		this.onAppointmentReminder = onAppointmentReminder;
	}
	@Override
	public String toString() {
		return "{\"notificationSettingsId\":" + notificationSettingsId + ", \"onReferral\":" + onReferral
				+ ", \"onFavorite\":" + onFavorite + ", \"onScheduleReminder\":" + onScheduleReminder
				+ ", \"onAppointmentReservation\":" + onAppointmentReservation + ", \"onAddFeatureEmail\":"
				+ onAddFeatureEmail + ", \"userFk\":" + userFk + ",\"onAppointmentReminder\":" + onAppointmentReminder
				+ "}";
	}
}
