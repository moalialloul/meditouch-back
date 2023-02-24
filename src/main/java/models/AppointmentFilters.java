package models;

import Enums.AppointmentStatus;

public class AppointmentFilters {
	boolean isApproved;
	boolean isCancelled;
	AppointmentStatus appointmentStatus = null;
	boolean isUpcoming = true;
	boolean additionalFilters = false;
	boolean isAll;

	public AppointmentFilters() {
		super();
	}

	public boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public boolean getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	@Override
	public String toString() {
		return "{\"isApproved\":" + isApproved + ", \"isCancelled\":" + isCancelled + ", \"appointmentStatus\":\""
				+ appointmentStatus + "\", \"isUpcoming\":" + isUpcoming + ",\"isAll\":" + isAll
				+ ",\"additionalFilters\":" + additionalFilters + "}";
	}

	public AppointmentFilters(boolean isApproved, boolean isCancelled, AppointmentStatus appointmentStatus,
			boolean isUpcoming, boolean isAll, boolean additionalFilters) {
		super();
		this.isApproved = isApproved;
		this.isCancelled = isCancelled;
		this.appointmentStatus = appointmentStatus;
		this.isUpcoming = isUpcoming;
		this.isAll = isAll;
		this.additionalFilters = additionalFilters;
	}

	public boolean getAdditionalFilters() {
		return additionalFilters;
	}

	public void setAdditionalFilters(boolean additionalFilters) {
		this.additionalFilters = additionalFilters;
	}

	public boolean getIsAll() {
		return isAll;
	}

	public void setIsAll(boolean isAll) {
		this.isAll = isAll;
	}

	public AppointmentStatus getAppointmentStatus() {
		return appointmentStatus;
	}

	public void setAppointmentStatus(AppointmentStatus appointmentStatus) {
		this.appointmentStatus = appointmentStatus;
	}

	public boolean getIsUpcoming() {
		return isUpcoming;
	}

	public void setIsUpcoming(boolean isUpcoming) {
		this.isUpcoming = isUpcoming;
	}
}
