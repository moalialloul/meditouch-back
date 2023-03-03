package models;

import Enums.AppointmentStatus;

public class AppointmentFilters {
	int isCancelled = -1;
	String appointmentStatus = null;
	String appointmentType = "ALL";

	public AppointmentFilters() {
		super();
	}

	
	public int getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(int isCancelled) {
		this.isCancelled = isCancelled;
	}

	

	public AppointmentFilters(int isCancelled, String appointmentStatus,
			String appointmentType) {
		super();
		this.isCancelled = isCancelled;
		this.appointmentStatus = appointmentStatus;
		this.appointmentType = appointmentType;
	}

	

	@Override
	public String toString() {
		return "{\"isCancelled\":" + isCancelled + ", \"appointmentStatus\":\"" + appointmentStatus
				+ "\", \"appointmentType\":\"" + appointmentType + "\"}";
	}


	public String getAppointmentType() {
		return appointmentType;
	}


	public void setAppointmentType(String appointmentType) {
		this.appointmentType = appointmentType;
	}


	

	public String getAppointmentStatus() {
		return appointmentStatus;
	}

	public void setAppointmentStatus(String appointmentStatus) {
		this.appointmentStatus = appointmentStatus;
	}

	
}
