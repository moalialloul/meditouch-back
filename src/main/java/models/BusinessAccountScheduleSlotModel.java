package models;

import java.sql.Date;
import java.sql.Timestamp;

public class BusinessAccountScheduleSlotModel {
	int slotId;
	int scheduleFk;
	Date slotDate;
	Timestamp slotStartTime;
	Timestamp slotEndTime;
	boolean isLocked;
	int serviceFk;

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
				+ isLocked + ",\"serviceFk\"" + serviceFk + "}";
	}

	public BusinessAccountScheduleSlotModel(int slotId, int scheduleFk, Date slotDate, Timestamp slotStartTime,
			Timestamp slotEndTime, boolean isLocked) {
		super();
		this.slotId = slotId;
		this.scheduleFk = scheduleFk;
		this.slotDate = slotDate;
		this.slotStartTime = slotStartTime;
		this.slotEndTime = slotEndTime;
		this.isLocked = isLocked;
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

	public Timestamp getSlotStartTime() {
		return slotStartTime;
	}

	public void setSlotStartTime(Timestamp slotStartTime) {
		this.slotStartTime = slotStartTime;
	}

	public Timestamp getSlotEndTime() {
		return slotEndTime;
	}

	public void setSlotEndTime(Timestamp slotEndTime) {
		this.slotEndTime = slotEndTime;
	}

	public boolean getIsLocked() {
		return isLocked;
	}

	public void setIsLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}
}
