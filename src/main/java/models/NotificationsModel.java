package models;

import Enums.NotificationType;

public class NotificationsModel {
	int notificationId;
	int userToFk;
	int userFromFk;
	String notificationText;
	NotificationType notificationType;
	String notificationUrl;

	public String getNotificationUrl() {
		return notificationUrl;
	}

	public void setNotificationUrl(String notificationUrl) {
		this.notificationUrl = notificationUrl;
	}

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
				+ "\",\"notificationUrl\":\"" + notificationUrl + "\"}";

	}

	public NotificationsModel() {
		super();
	}

	public NotificationsModel(boolean isOpen, int notificationId, int userToFk, int userFromFk, String notificationText,
			NotificationType notificationType, String notificationUrl) {
		super();
		this.notificationId = notificationId;
		this.userToFk = userToFk;
		this.userFromFk = userFromFk;
		this.notificationText = notificationText;
		this.notificationType = notificationType;
		this.notificationUrl = notificationUrl;
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

}
