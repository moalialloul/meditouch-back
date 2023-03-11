package Reminders;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.meditouch.DatabaseConnection;

public class ScheduleReminders {
	private SimpMessagingTemplate messagingTemplate;
	Timer timer;
	static PreparedStatement myStmt;

	public ScheduleReminders(int seconds, SimpMessagingTemplate messagingTemplate) {
		timer = new Timer();
		timer.schedule(new RemindTask(), seconds * 1000, seconds * 1000);
		this.messagingTemplate = messagingTemplate;

	}

	class RemindTask extends TimerTask {

		public void run() {
			try {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
				Date startDate = calendar.getTime();
				String formattedStartDate = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
				calendar.add(Calendar.DAY_OF_WEEK, 6);
				Date endDate = calendar.getTime();
				String formattedEndDate = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
				String query = "SELECT" + " bat.userFk, bat.businessAccountId,"
						+ "  COALESCE(COUNT(basst.slotId), 0) AS numSlots,"
						+ "  COALESCE(ns.onScheduleReminder, 1) AS onScheduleReminder"
						+ "FROM business_account_table bat"
						+ "LEFT JOIN business_account_schedule_table bast ON bast.businessAccountFk = bat.businessAccountId"
						+ "LEFT JOIN business_account_schedule_slots_table basst ON basst.scheduleFk = bast.scheduleId AND basst.slotStartTime >= '"
						+ formattedStartDate + "' AND basst.slotEndTime <= '" + formattedEndDate + "'"
						+ "LEFT JOIN users_table ut ON ut.userId = bat.userFk"
						+ "LEFT JOIN notifications_settings ns ON ns.userFk = ut.userId "
						+ "GROUP BY bat.businessAccountId, ns.onScheduleReminder,  bat.userFk";

				myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

				ResultSet rs = myStmt.executeQuery();

				while (rs.next()) {
					if (rs.getInt("numSlots") == 0 && rs.getBoolean("onScheduleReminder") == true) {
						JSONObject jsonReturned = new JSONObject();
						jsonReturned.put("userFromFk", "");
						jsonReturned.put("userToFk", "");
						jsonReturned.put("notificationText", "Reminder to setup your schedule for this week between "
								+ formattedStartDate + " " + formattedEndDate);
						jsonReturned.put("notificationType", "SCHEDULE");
						jsonReturned.put("isOpen", false);
						jsonReturned.put("notificationUrl", "");
						jsonReturned.put("userFromProfile", "");

						messagingTemplate.convertAndSend("/topic/scheduleReminder/" + rs.getInt("userFk"),
								jsonReturned.toString());
					}
				}

			} catch (SQLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
