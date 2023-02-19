package models;

import java.util.ArrayList;
import java.util.List;

public class BusinessAccountScheduleModel {
	List<BusinessAccountScheduleSlotModel> scheduleSlots = new ArrayList<BusinessAccountScheduleSlotModel>();

	@Override
	public String toString() {
		String slotsString = "{scheduleSlots:[";
		for (int i = 0; i < scheduleSlots.size(); i++) {
			slotsString += "{\"slotDate\": \"" + scheduleSlots.get(i).getSlotDate() + "\" , \"slotStartTime\":\""
					+ scheduleSlots.get(i).getSlotStartTime() + "\",slotEndTime\" : \"" + scheduleSlots.get(i).getSlotEndTime() + "\"}"
					+ (i != scheduleSlots.size() - 1 ? "," : "");
		}
		return slotsString + "]}";
	}

	public BusinessAccountScheduleModel(List<BusinessAccountScheduleSlotModel> scheduleSlots) {
		super();
		this.scheduleSlots = scheduleSlots;
	}

	public BusinessAccountScheduleModel() {

	}

	public List<BusinessAccountScheduleSlotModel> getScheduleSlots() {
		return scheduleSlots;
	}

	public void setScheduleSlots(List<BusinessAccountScheduleSlotModel> scheduleSlots) {
		this.scheduleSlots = scheduleSlots;
	}
}
