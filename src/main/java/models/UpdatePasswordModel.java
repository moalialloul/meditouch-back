package models;

public class UpdatePasswordModel {
	String currentPassword;
	String newPassword;
	int userFk;

	@Override
	public String toString() {
		return "{\"currentPassword\":\"" + currentPassword + "\", \"newPassword\":\"" + newPassword + "\", userFk:"
				+ userFk + "}";
	}

	public UpdatePasswordModel(String currentPassword, String newPassword, int userFk) {
		super();
		this.currentPassword = currentPassword;
		this.newPassword = newPassword;
		this.userFk = userFk;
	}

	public UpdatePasswordModel() {

	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public int getUserFk() {
		return userFk;
	}

	public void setUserFk(int userFk) {
		this.userFk = userFk;
	}
}
