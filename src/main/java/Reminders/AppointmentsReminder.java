package Reminders;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.meditouch.DatabaseConnection;

public class AppointmentsReminder {
	private SimpMessagingTemplate messagingTemplate;
	Timer timer;
	static PreparedStatement myStmt;

	public AppointmentsReminder(int seconds, SimpMessagingTemplate messagingTemplate) {
		timer = new Timer();
		timer.schedule(new RemindTask(), seconds * 1000, seconds * 1000);
		this.messagingTemplate = messagingTemplate;

	}

	class RemindTask extends TimerTask {

		public void run() {
			try {
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());

				myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
						"select COALESCE(onAppointmentReminder,-1) as onAppointmentReminder, bat.clinicLocation as clinicLocation,ut.profilePicture, ut2.userId as userToFk, ut.userId as userFromFk, ut.firstName as firstName, ut.lastName as lastName, ut.userEmail as userEmail,basst.slotStartTime as slotStartTime from appointments_table apt left join notifications_settings ns on ns.userFk=apt.userFk right join business_account_schedule_slots_table basst on basst.slotId=apt.slotFk right join business_account_table bat on bat.businessAccountId=apt.businessAccountFk right join users_table ut on ut.userid=bat.userFk right join users_table ut2 on ut2.userId=apt.userFk where basst.slotStartTime > ?");
				myStmt.setTimestamp(1, timestamp);
				ResultSet rs = myStmt.executeQuery();
				while (rs.next()) {
					if (rs.getBoolean("onAppointmentReminder")) {
						JSONObject jsonReturned = new JSONObject();
						jsonReturned.put("userFromFk", rs.getInt("userFromFk"));
						jsonReturned.put("userToFk", rs.getInt("userToFk"));
						jsonReturned.put("notificationText",
								"Reminder for your appointment with doctor " + rs.getString("firstName") + " "
										+ rs.getString("lastName") + " on " + rs.getTimestamp("slotStartTime") + " in"
										+ rs.getString("clinicLocation"));
						jsonReturned.put("notificationType", "APPOINTMENT");
						jsonReturned.put("isOpen", false);
						jsonReturned.put("notificationUrl", "");
						jsonReturned.put("userFromProfile", rs.getString("profilePicture"));

						messagingTemplate.convertAndSend("/topic/appointmentsReminder/" + rs.getInt("userToFk"),
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
