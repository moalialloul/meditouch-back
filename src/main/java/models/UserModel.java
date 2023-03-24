package models;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import Enums.UserRoles;

public class UserModel {
	int userId = -1;
	String firstName;
	String lastName;
	String userEmail;
	String password;
	boolean isVerified;
	int numberOfLoginTrials;
	LocalDateTime registrationDate;
	boolean isApproved;
	boolean isLocked;
	UserRoles userRole;
	String userLanguage;
	String profilePicture;

	public UserModel() {

	}

	@Override
	public String toString() {
		return "{\"userId\":" + userId + ",\"firstName\":\"" + firstName + "\", \"lastName\":\"" + lastName
				+ "\",\"userEmail\":\"" + userEmail + "\", \"password\":\"" + password + "\", \"isVerified\":"
				+ isVerified + ", \"numberOfLoginTrials\":" + numberOfLoginTrials + ", \"registrationDate\":"
				+ registrationDate + ", \"isApproved\":" + isApproved + ", \"isLocked\":" + isLocked
				+ ", \"userRole\":\"" + userRole + "\", \"userLanguage\":\"" + userLanguage
				+ "\", \"profilePicture\":\"" + profilePicture + "\"}";
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	public int getNumberOfLoginTrials() {
		return numberOfLoginTrials;
	}

	public void setNumberOfLoginTrials(int numberOfLoginTrials) {
		this.numberOfLoginTrials = numberOfLoginTrials;
	}

	public LocalDateTime getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}

	public boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public boolean getIsLocked() {
		return isLocked;
	}

	

	public void setIsLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public UserRoles getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRoles userRole) {
		this.userRole = userRole;
	}

	public String getUserLanguage() {
		return userLanguage;
	}

	public void setUserLanguage(String userLanguage) {
		this.userLanguage = userLanguage;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public UserModel(int userId, String firstName, String lastName, String userEmail, String password,
			boolean isVerified, int numberOfLoginTrials, LocalDateTime registrationDate, boolean isApproved, boolean isLocked,
			UserRoles userRole, String userLanguage, String profilePicture) {
		super();
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userEmail = userEmail;
		this.password = password;
		this.isVerified = isVerified;
		this.numberOfLoginTrials = numberOfLoginTrials;
		this.registrationDate = registrationDate;
		this.isApproved = isApproved;
		this.isLocked = isLocked;
		this.userRole = userRole;
		this.userLanguage = userLanguage;
		this.profilePicture = profilePicture;
	}

}
