package models;

import java.sql.Timestamp;

import Enums.AppointmentStatus;
import Enums.UserRoles;

public class AppointmentModel {
	int appointmentId;
	int slotFk;
	int businessAccountFk;
	int userFk;
	int serviceFk;
	Timestamp appointmentActualStartTime;
	Timestamp appointmentActualEndTime;
	AppointmentStatus appointmentStatus;
	boolean isApproved = false;
	boolean isCancelled = false;
	UserRoles cancelledBy = null;
	@Override
	public String toString() {
		return "{\"appointmentId\":" + appointmentId + ", \"slotFk\":" + slotFk + ", \"businessAccountFk\":"
				+ businessAccountFk + ", \"userFk\":" + userFk + ", \"serviceFk\":" + serviceFk
				+ ", \"appointmentActualStartTime\":" + appointmentActualStartTime + ", \"appointmentActualEndTime\":"
				+ appointmentActualEndTime + ", \"appointmentStatus\":\"" + appointmentStatus + "\", \"isApproved\":" + isApproved
				+ ", \"isCancelled\":" + isCancelled + ", \"cancelledBy\":" + cancelledBy + "}";
	}
	public AppointmentModel() {
		super();
	}
	public AppointmentModel(int appointmentId, int slotFk, int businessAccountFk, int userFk, int serviceFk,
			Timestamp appointmentActualStartTime, Timestamp appointmentActualEndTime,
			AppointmentStatus appointmentStatus, boolean isApproved, boolean isCancelled, UserRoles cancelledBy) {
		super();
		this.appointmentId = appointmentId;
		this.slotFk = slotFk;
		this.businessAccountFk = businessAccountFk;
		this.userFk = userFk;
		this.serviceFk = serviceFk;
		this.appointmentActualStartTime = appointmentActualStartTime;
		this.appointmentActualEndTime = appointmentActualEndTime;
		this.appointmentStatus = appointmentStatus;
		this.isApproved = isApproved;
		this.isCancelled = isCancelled;
		this.cancelledBy = cancelledBy;
	}
	public int getAppointmentId() {
		return appointmentId;
	}
	public void setAppointmentId(int appointmentId) {
		this.appointmentId = appointmentId;
	}
	public int getSlotFk() {
		return slotFk;
	}
	public void setSlotFk(int slotFk) {
		this.slotFk = slotFk;
	}
	public int getBusinessAccountFk() {
		return businessAccountFk;
	}
	public void setBusinessAccountFk(int businessAccountFk) {
		this.businessAccountFk = businessAccountFk;
	}
	public int getUserFk() {
		return userFk;
	}
	public void setUserFk(int userFk) {
		this.userFk = userFk;
	}
	public int getServiceFk() {
		return serviceFk;
	}
	public void setServiceFk(int serviceFk) {
		this.serviceFk = serviceFk;
	}
	public Timestamp getAppointmentActualStartTime() {
		return appointmentActualStartTime;
	}
	public void setAppointmentActualStartTime(Timestamp appointmentActualStartTime) {
		this.appointmentActualStartTime = appointmentActualStartTime;
	}
	public Timestamp getAppointmentActualEndTime() {
		return appointmentActualEndTime;
	}
	public void setAppointmentActualEndTime(Timestamp appointmentActualEndTime) {
		this.appointmentActualEndTime = appointmentActualEndTime;
	}
	public AppointmentStatus getAppointmentStatus() {
		return appointmentStatus;
	}
	public void setAppointmentStatus(AppointmentStatus appointmentStatus) {
		this.appointmentStatus = appointmentStatus;
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
	public UserRoles getCancelledBy() {
		return cancelledBy;
	}
	public void setCancelledBy(UserRoles cancelledBy) {
		this.cancelledBy = cancelledBy;
	}
}
