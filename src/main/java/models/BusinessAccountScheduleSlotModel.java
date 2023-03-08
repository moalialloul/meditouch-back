package models;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class BusinessAccountScheduleSlotModel {
	int slotId;
	int scheduleFk;
	Date slotDate;
	LocalDateTime slotStartTime;
	LocalDateTime slotEndTime;
	boolean isLocked;
	int serviceFk;
	boolean isReserved;
	boolean isDeleted;

	public BusinessAccountScheduleSlotModel() {

	}

	public int getServiceFk() {
		return serviceFk;
	}

	public void setServiceFk(int serviceFk) {
		this.serviceFk = serviceFk;
	}

	@Override
	public String toString() {
		return "{\"slotId\":" + slotId + ", \"scheduleFk\":" + scheduleFk + ", \"slotDate\":" + slotDate
				+ ", \"slotStartTime\":" + slotStartTime + ", \"slotEndTime\":" + slotEndTime + ", \"isLocked\":"
				+ isLocked + ",\"serviceFk\"" + serviceFk + ",\"isReserved\":" + isReserved + ",\"isDeleted\":"
				+ isDeleted + "}";
	}

	public BusinessAccountScheduleSlotModel(int slotId, int scheduleFk, Date slotDate, LocalDateTime slotStartTime,
			LocalDateTime slotEndTime, boolean isLocked, boolean isReserved, boolean isDeleted) {
		super();
		this.slotId = slotId;
		this.scheduleFk = scheduleFk;
		this.slotDate = slotDate;
		this.slotStartTime = slotStartTime;
		this.slotEndTime = slotEndTime;
		this.isLocked = isLocked;
		this.isReserved = isReserved;
		this.isDeleted = isDeleted;
	}

	public boolean getIsReserved() {
		return isReserved;
	}

	public void setIsReserved(boolean isReserved) {
		this.isReserved = isReserved;
	}
	public boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public int getSlotId() {
		return slotId;
	}

	public void setSlotId(int slotId) {
		this.slotId = slotId;
	}

	public int getScheduleFk() {
		return scheduleFk;
	}

	public void setScheduleFk(int scheduleFk) {
		this.scheduleFk = scheduleFk;
	}

	public Date getSlotDate() {
		return slotDate;
	}

	public void setSlotDate(Date slotDate) {
		this.slotDate = slotDate;
	}

	public LocalDateTime getSlotStartTime() {
		return slotStartTime;
	}

	public void setSlotStartTime(LocalDateTime slotStartTime) {
		this.slotStartTime = slotStartTime;
	}

	public LocalDateTime getSlotEndTime() {
		return slotEndTime;
	}

	public void setSlotEndTime(LocalDateTime slotEndTime) {
		this.slotEndTime = slotEndTime;
	}

	public boolean getIsLocked() {
		return isLocked;
	}

	public void setIsLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}
}
