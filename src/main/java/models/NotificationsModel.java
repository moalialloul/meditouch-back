package models;

import Enums.NotificationType;

public class NotificationsModel {
	int notificationId;
	int userToFk;
	int userFromFk;
	String notificationText;
	NotificationType notificationType;
	int appointmentFk;
	int commentFk;
	int promoCodeFk;
	int referralFk;
	int favoriteFk;
	boolean isOpen;

	public boolean getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	@Override
	public String toString() {
		return "{\"notificationId\":" + notificationId + ", \"userToFk\":" + userToFk + ", \"userFromFk\":" + userFromFk
				+ ", \"notificationText\":\"" + notificationText + "\", \"notificationType\":\"" + notificationType
				+ "\", \"appointmentFk\":" + appointmentFk + ", \"commentFk\":" + commentFk + ", \"promoCodeFk\":"
				+ promoCodeFk + ", \"referralFk\":" + referralFk + ", \"favoriteFk\":" + favoriteFk + ",\"isOpen\":"
				+ isOpen + "}";
	}

	public NotificationsModel() {
		super();
	}

	public NotificationsModel(boolean isOpen, int notificationId, int userToFk, int userFromFk, String notificationText,
			NotificationType notificationType, int appointmentFk, int commentFk, int promoCodeFk, int referralFk,
			int favoriteFk) {
		super();
		this.notificationId = notificationId;
		this.userToFk = userToFk;
		this.userFromFk = userFromFk;
		this.notificationText = notificationText;
		this.notificationType = notificationType;
		this.appointmentFk = appointmentFk;
		this.commentFk = commentFk;
		this.promoCodeFk = promoCodeFk;
		this.referralFk = referralFk;
		this.favoriteFk = favoriteFk;
		this.isOpen = isOpen;
	}

	public int getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}

	public int getUserToFk() {
		return userToFk;
	}

	public void setUserToFk(int userToFk) {
		this.userToFk = userToFk;
	}

	public int getUserFromFk() {
		return userFromFk;
	}

	public void setUserFromFk(int userFromFk) {
		this.userFromFk = userFromFk;
	}

	public String getNotificationText() {
		return notificationText;
	}

	public void setNotificationText(String notificationText) {
		this.notificationText = notificationText;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public int getAppointmentFk() {
		return appointmentFk;
	}

	public void setAppointmentFk(int appointmentFk) {
		this.appointmentFk = appointmentFk;
	}

	public int getCommentFk() {
		return commentFk;
	}

	public void setCommentFk(int commentFk) {
		this.commentFk = commentFk;
	}

	public int getPromoCodeFk() {
		return promoCodeFk;
	}

	public void setPromoCodeFk(int promoCodeFk) {
		this.promoCodeFk = promoCodeFk;
	}

	public int getReferralFk() {
		return referralFk;
	}

	public void setReferralFk(int referralFk) {
		this.referralFk = referralFk;
	}

	public int getFavoriteFk() {
		return favoriteFk;
	}

	public void setFavoriteFk(int favoriteFk) {
		this.favoriteFk = favoriteFk;
	}
}
