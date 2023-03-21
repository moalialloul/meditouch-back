package com.example.meditouch.controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.meditouch.CommonFunctions;
import com.example.meditouch.DatabaseConnection;
import com.example.meditouch.TimestampDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Enums.AppointmentStatus;
import Enums.NotificationType;
import models.AppointmentFilters;
import models.AppointmentPrescriptionModel;
import models.AppointmentReferralModel;
import models.AppointmentResultModel;
import models.BlockModel;
import models.BusinessAccountModel;
import models.BusinessAccountScheduleSlotModel;
import models.GlobalSearchModel;
import models.NotificationsModel;
import models.NotificationsSettings;
import models.ServiceModel;
import models.UserModel;

@RestController
public class BusinessAccountController {
	private SimpMessagingTemplate messagingTemplate;
	List<NotificationsModel> notificationModel = new ArrayList<NotificationsModel>();

	public BusinessAccountController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	@MessageMapping("/hello")
	public void sayHello(UserModel user) throws SQLException, IOException {
		JSONObject json = new JSONObject();
		json.put("name", user.getFirstName());
		messagingTemplate.convertAndSend("/topic/greetings", json.toString());
	}

	// passed
	@GetMapping("/getTodayAppointments/{businessAccountFk}")
	public ResponseEntity<Object> getTodayAppointments(@PathVariable("businessAccountFk") int businessAccountFk)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"select  mi.height, mi.weight, mi.diseasesDescription, mi.vaccinationDescription, COALESCE(art.prescriptionId, -1) as prescriptionId,COALESCE(art.prescriptionDescription, -1) as prescriptionDescription, atb.appointmentId,basst.slotStartTime, basst.slotEndTime, atb.appointmentActualStartTime, atb.appointmentActualEndTime, atb.appointmentStatus,atb.isApproved, atb.isCancelled, atb.userFk, ut.firstName, ut.lastName, ut.userEmail , ut.profilePicture from appointments_table atb join business_account_schedule_slots_table basst on basst.slotId = atb.slotFk left join appointment_prescriptions_table art on art.appointmentFk=atb.appointmentId join users_table ut on ut.userId=atb.userFk join medical_information mi on mi.userFk=ut.userId where atb.appointmentStatus = 'ACCEPTED' and Date(basst.slotDate)=CURDATE() and atb.businessAccountFk=? order by timestamp(basst.slotStartTime) ASC");

		myStmt.setInt(1, businessAccountFk);

		ResultSet myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs.next()) {
			JSONObject json = new JSONObject();

			json.put("appointmentId", myRs.getInt("appointmentId"));
			json.put("slotStartTime", myRs.getTimestamp("slotStartTime"));
			json.put("slotEndTime", myRs.getTimestamp("slotEndTime"));
			json.put("appointmentStatus", myRs.getString("appointmentStatus"));
			json.put("isApproved", myRs.getBoolean("isApproved"));
			json.put("isCancelled", myRs.getBoolean("isCancelled"));
			json.put("userFk", myRs.getInt("userFk"));

			json.put("appointmentActualStartTime",
					myRs.getTimestamp("appointmentActualStartTime") != null
							? myRs.getTimestamp("appointmentActualStartTime")
							: -1);
			json.put("appointmentActualEndTime",
					myRs.getTimestamp("appointmentActualEndTime") != null
							? myRs.getTimestamp("appointmentActualEndTime")
							: -1);
			json.put("prescriptionId", myRs.getInt("prescriptionId"));
			json.put("prescriptionDescription", myRs.getString("prescriptionDescription"));

			json.put("firstName", myRs.getString("firstName"));
			json.put("lastName", myRs.getString("lastName"));
			json.put("userEmail", myRs.getString("userEmail"));
			json.put("profilePicture", myRs.getString("profilePicture"));
			json.put("height", myRs.getInt("height"));
			json.put("weight", myRs.getInt("weight"));
			json.put("diseasesDescription", myRs.getString("diseasesDescription"));
			json.put("vaccinationDescription", myRs.getString("vaccinationDescription"));

			jsonArray.put(json);

		}
		jsonResponse.put("message", "Appointments Returned");
		jsonResponse.put("appointments", jsonArray);

		jsonResponse.put("responseCode", 200);
		myRs.close();
		myStmt.close();
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@GetMapping("/getBusinessAccountAppointmentsStatistics/{businessAccountFk}/{fromDate}/{toDate}")
	public ResponseEntity<Object> getBusinessAccountStatistics(@PathVariable("businessAccountFk") int businessAccountFk,
			@PathVariable("fromDate") Date fromDate, @PathVariable("toDate") Date toDate)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"SELECT sum(a.isCancelled = 1) as num_appointments_cancelled,sum(a.isCancelled = 0) as num_appointments_not_cancelled, sum(a.isApproved = 1) as num_appointments_approved ,sum(a.isApproved = 0) as num_appointments_not_approved, DATE(basst.slotDate) AS appointment_date, COUNT(a.appointmentId) AS num_appointments FROM appointments_table a INNER JOIN business_account_schedule_slots_table basst ON a.slotFk = basst.slotId where date(basst.slotDate) between '"
						+ fromDate + "' and '" + toDate + "' and a.businessAccountFk=? GROUP BY DATE(basst.slotDate)"
						+ "");
		myStmt.setInt(1, businessAccountFk);

		ResultSet myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs.next()) {
			JSONObject json = new JSONObject();

			json.put("appointmentDate", myRs.getDate("appointment_date"));
			json.put("numAppointments", myRs.getInt("num_appointments"));
			json.put("numAppointmentsCancelled", myRs.getInt("num_appointments_cancelled"));
			json.put("numAppointmentsNotCancelled", myRs.getInt("num_appointments_not_cancelled"));
			json.put("numAppointmentsNotApproved", myRs.getInt("num_appointments_not_approved"));
			json.put("numAppointmentsApproved", myRs.getInt("num_appointments_approved"));

			jsonArray.put(json);

		}
		myRs.close();
		myStmt.close();
		jsonResponse.put("message", "Results Returned");
		jsonResponse.put("results", jsonArray);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getBusinessAccountPatients/{businessAccountId}")
	public ResponseEntity<Object> getBusinessAccountPatients(@PathVariable("businessAccountId") int businessAccountId)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();
		String query = "select DISTINCT apt.userFk as userId, ut.firstName, ut.lastName, ut.userEmail, ut.profilePicture, COALESCE(babt.blockId, -1) as blockId from appointments_table apt join users_table ut on ut.userId=apt.userFk left join business_account_blockings_table babt on babt.userFk = apt.userFk where apt.businessAccountFk="
				+ businessAccountId;
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		ResultSet myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs.next()) {
			JSONObject json = new JSONObject();
			json.put("blockId", myRs.getInt("blockId"));

			json.put("userId", myRs.getInt("userId"));
			json.put("firstName", myRs.getString("firstName"));
			json.put("lastName", myRs.getString("lastName"));
			json.put("userEmail", myRs.getString("userEmail"));

			json.put("profilePicture", myRs.getString("profilePicture"));
			jsonArray.put(json);

		}
		myRs.close();
		myStmt.close();
		jsonResponse.put("message", "Patients Returned");
		jsonResponse.put("patients", jsonArray);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getBusinessAccountStatistics/{businessAccountId}")
	public ResponseEntity<Object> getBusinessAccountStatistics(@PathVariable("businessAccountId") int businessAccountId)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();
		String query = "Select * from (SELECT COUNT(apt.appointmentId) as total_appointments, COUNT(DISTINCT apt.userFk) AS total_patients FROM appointments_table apt where apt.businessAccountFk="
				+ businessAccountId
				+ ") as p1 JOIN (SELECT COUNT(babt.blockId) as total_blocked_users from business_account_blockings_table babt where businessAccountFk="
				+ businessAccountId
				+ ") as p2 JOIN (SELECT COUNT(bart.referralId) as total_referrals from business_account_referrals_table bart where bart.referredToBusinessAccountFk="
				+ businessAccountId + ") as p3";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		ResultSet myRs = myStmt.executeQuery();
		JSONObject json = new JSONObject();
		if (myRs.next()) {

			json.put("totalAppointments", myRs.getInt("total_appointments"));
			json.put("totalPatients", myRs.getInt("total_patients"));
			json.put("totalBlockedUsers", myRs.getInt("total_blocked_users"));
			json.put("totalReferrals", myRs.getInt("total_referrals"));

		}
		myRs.close();
		myStmt.close();
		jsonResponse.put("message", "Results Returned");
		jsonResponse.put("result", json);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getHealthProfessionals/{userFk}/{pageNumber}/{recordsByPage}/{searchText}")
	public ResponseEntity<Object> getHealthProfessionals(@PathVariable("userFk") int userFk,
			@PathVariable("pageNumber") int pageNumber, @PathVariable("recordsByPage") int recordsByPage,
			@PathVariable("searchText") String searchText) throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();
		String query = "select count(*) as total_count from users_table where userRole='HEALTH_PROFESSIONAL' and isApproved=1 and isVerified=1 and userId!=? ";
		if (!searchText.equals("null")) {
			query += " and (firstName LIKE  '%" + searchText + "%' or lastName LIKE '%" + searchText
					+ "%' or userEmail LIKE '%" + searchText + "%')";
		}
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") * 1.0 / recordsByPage);
		}
		myRs.close();
		query = "select * from users_table ut join business_account_table bat on bat.userFk=ut.userId where userRole='HEALTH_PROFESSIONAL' and isApproved=1 and isVerified=1 and userId!=?";
		if (!searchText.equals("null")) {
			query += " and (firstName LIKE  '%" + searchText + "%' or lastName LIKE '%" + searchText
					+ "%' or userEmail LIKE '%" + searchText + "%')";
		}
		query += " limit " + recordsByPage + " OFFSET " + (pageNumber - 1) * recordsByPage;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);

		ResultSet myRs2 = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs2.next()) {
			JSONObject json = new JSONObject();
			json.put("userId", myRs2.getInt("userId"));
			json.put("firstName", myRs2.getString("firstName"));
			json.put("businessAccountId", myRs2.getInt("businessAccountId"));

			json.put("lastName", myRs2.getString("lastName"));
			json.put("userEmail", myRs2.getString("userEmail"));
			json.put("profilePicture", myRs2.getString("profilePicture"));

			jsonArray.put(json);

		}
		myRs.close();
		myStmt.close();
		jsonResponse.put("message", "All Health Professionals");
		jsonResponse.put("health_professionals", jsonArray);
		jsonResponse.put("totalNumberOfPages", totalNumberOfPages);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getReferrals/{businessAccountFk}/{pageNumber}/{recordsByPage}")
	public ResponseEntity<Object> getReferrals(@PathVariable("businessAccountFk") int businessAccountFk,
			@PathVariable("pageNumber") int pageNumber, @PathVariable("recordsByPage") int recordsByPage)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();
		String query = "select COUNT(*) AS total_count from business_account_referrals_table where userFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, businessAccountFk);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") * 1.0 / recordsByPage);
		}
		myRs.close();
		query = "select ut2.firstName as patientFirstName,ut2.lastName as patientLastName,"
				+ "ut2.userEmail as patientUserEmail,ut2.profilePicture as patientProfilePicture,"
				+ " ut.firstName as referredByFirstName,ut.lastName as referredByLastName,ut.userEmail "
				+ "as referredByUserEmail, ut.profilePicture as referredByProfilePicture,"
				+ " bart.referralDescription, bart.referralId, bart.appointmentFk"
				+ "  from business_account_referrals_table bart join business_account_table"
				+ " bat on bat.businessAccountId=bart.referredByBusinessAccountFk join "
				+ "users_table ut on ut.userId=bat.userFk join users_table ut2 on ut2.userId=bart.userFk where bart.referredToBusinessAccountFk=?"
				+ " limit " + recordsByPage + " OFFSET " + (pageNumber - 1) * recordsByPage;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, businessAccountFk);

		ResultSet myRs2 = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs2.next()) {
			JSONObject json = new JSONObject();

			json.put("referredByFirstName", myRs2.getString("referredByFirstName"));
			json.put("referredByLastName", myRs2.getString("referredByLastName"));
			json.put("referredByUserEmail", myRs2.getString("referredByUserEmail"));
			json.put("referredByProfilePicture", myRs2.getString("referredByProfilePicture"));

			json.put("patientFirstName", myRs2.getString("patientFirstName"));
			json.put("patientLastName", myRs2.getString("patientLastName"));
			json.put("patientUserEmail", myRs2.getString("patientUserEmail"));
			json.put("patientProfilePicture", myRs2.getString("patientProfilePicture"));

			json.put("referralDescription", myRs2.getString("referralDescription"));
			json.put("referralId", myRs2.getInt("referralId"));
			json.put("appointmentFk", myRs2.getInt("appointmentFk"));

			jsonArray.put(json);

		}
		myRs2.close();
		myStmt.close();
		jsonResponse.put("message", "All Referrals");
		jsonResponse.put("referrals", jsonArray);
		jsonResponse.put("totalNumberOfPages", totalNumberOfPages);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/addReferrals")
	public ResponseEntity<Object> addReferrals(@RequestBody AppointmentReferralModel[] appointmentReferralModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();
		String query = "insert into business_account_referrals_table (userFk, appointmentFk,referralDescription, referredByBusinessAccountFk, referredToBusinessAccountFk) values(?,?,?,?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		for (int i = 0; i < appointmentReferralModel.length; i++) {
			myStmt.setInt(1, appointmentReferralModel[i].getUserFk());
			myStmt.setInt(2, appointmentReferralModel[i].getAppointmentFk());
			myStmt.setString(3, appointmentReferralModel[i].getReferralDescription());
			myStmt.setInt(4, appointmentReferralModel[i].getReferredByBusinessAccountFk());
			myStmt.setInt(5, appointmentReferralModel[i].getReferredToBusinessAccountFk());

			myStmt.addBatch();

		}

		myStmt.executeBatch();
		ResultSet slotsGeneratedKeys = myStmt.getGeneratedKeys();
		int i = 0;
		while (slotsGeneratedKeys.next()) {
			appointmentReferralModel[i].setReferralId(slotsGeneratedKeys.getInt(1));
			query = "select t1.userId as businessAccountReferredByUserId, t3.userId as businessAccountReferredToUserId, COALESCE(onReferral,1), t2.firstName as patientFirstName, t2.lastName as patientLastName,"
					+ "t2.userEmail as patientUserEmail, t2.profilePicture as patientProfilePicture,"
					+ "t1.firstName as referredByFirstName, t1.lastName as referredByLastName, "
					+ "t1.userEmail as referredByUserEmail, t1.profilePicture as referredByProfilePicture  "
					+ "from (select * from users_table ut join business_account_table bat on "
					+ "ut.userId=bat.userFk where bat.businessAccountId=?) as t1"
					+ " join (select * from users_table ut2 where ut2.userId=?) as t2 join (select ns.onReferral from business_account_table bat join notifications_settings ns on ns.userFk = bat.userFk"
					+ " where bat.businessAccountId=?) as t3   ";
			myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
			myStmt.setInt(1, appointmentReferralModel[i].getReferredByBusinessAccountFk());

			myStmt.setInt(2, appointmentReferralModel[i].getUserFk());
			myStmt.setInt(3, appointmentReferralModel[i].getReferredToBusinessAccountFk());

			ResultSet rs = myStmt.executeQuery();
			if (rs.next()) {

				JSONObject json = new JSONObject();
				json.put("referralId", appointmentReferralModel[i].getReferralId());
				json.put("referralDescription", appointmentReferralModel[i].getReferralDescription());
				json.put("appointmentFk", appointmentReferralModel[i].getAppointmentFk());

				json.put("referredByFirstName", rs.getString("referredByFirstName"));
				json.put("referredByLastName", rs.getString("referredByLastName"));
				json.put("referredByUserEmail", rs.getString("referredByUserEmail"));
				json.put("referredByProfilePicture", rs.getString("referredByProfilePicture"));
				json.put("patientFirstName", rs.getString("patientFirstName"));
				json.put("patientLastName", rs.getString("patientLastName"));
				json.put("patientUserEmail", rs.getString("patientUserEmail"));
				json.put("patientProfilePicture", rs.getString("patientProfilePicture"));
				messagingTemplate.convertAndSend(
						"/topic/referral/" + appointmentReferralModel[i].getReferredToBusinessAccountFk(),
						json.toString());
				if (rs.getBoolean("onReferral") == true) {
					String notificationText = "Dr " + rs.getString("referredByFirstName") + " "
							+ rs.getString("referredByLastName") + "  referred you to "
							+ rs.getString("patientFirstName") + " " + rs.getString("patientLastName");
					JSONObject jsonNotificationReturned = new JSONObject();
					jsonNotificationReturned.put("userFromFk", rs.getInt("businessAccountReferredByUserId"));
					jsonNotificationReturned.put("userToFk", rs.getInt("businessAccountReferredToUserId"));
					jsonNotificationReturned.put("notificationText", notificationText);
					jsonNotificationReturned.put("notificationType", "REFERRAL");
					jsonNotificationReturned.put("isOpen", false);
					jsonNotificationReturned.put("notificationUrl", "");
					jsonNotificationReturned.put("userFromProfile", rs.getString("patientProfilePicture"));
					List<NotificationsModel> list = new ArrayList<>();
					list.add(new NotificationsModel(false, 0, rs.getInt("businessAccountReferredToUserId"),
							rs.getInt("businessAccountReferredByUserId"), notificationText, NotificationType.REFERRAL,
							""));
					addNotification(list);
					messagingTemplate.convertAndSend(
							"/topic/notifications/" + rs.getInt("businessAccountReferredToUserId"),
							jsonNotificationReturned.toString());
				}

				i++;
			}
			rs.close();

		}
		slotsGeneratedKeys.close();
		myStmt.close();

		jsonResponse.put("message", "Referrals Added Successfully");
		jsonResponse.put("referrals", appointmentReferralModel);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@DeleteMapping("/deleteReferrals")
	public ResponseEntity<Object> deleteReferrals(@RequestBody AppointmentReferralModel[] appointmentReferralModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();
		String query = "delete from business_account_referrals_table where referralId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		for (int i = 0; i < appointmentReferralModel.length; i++) {
			myStmt.setInt(1, appointmentReferralModel[i].getReferralId());

			myStmt.addBatch();

		}

		myStmt.executeBatch();

		myStmt.close();

		jsonResponse.put("message", "Referrals Deleted Successfully");
		jsonResponse.put("referral", appointmentReferralModel);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	public ResponseEntity<Object> addNotification(@RequestBody List<NotificationsModel> notificationModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();
		String query = "insert into notifications_table (userToFk, userFromFk, notificationText, notificationType, notificationUrl) values (?,?,?,?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		for (int i = 0; i < notificationModel.size(); i++) {
			myStmt.setInt(1, notificationModel.get(i).getUserToFk());
			myStmt.setInt(2, notificationModel.get(i).getUserFromFk());
			myStmt.setString(3, notificationModel.get(i).getNotificationText());
			myStmt.setString(4, notificationModel.get(i).getNotificationType().toString());
			myStmt.setString(5, notificationModel.get(i).getNotificationUrl());

			myStmt.addBatch();

		}

		myStmt.executeBatch();
		ResultSet slotsGeneratedKeys = myStmt.getGeneratedKeys();
		int i = 0;
		while (slotsGeneratedKeys.next()) {
			notificationModel.get(i).setNotificationId(slotsGeneratedKeys.getInt(1));
			i++;
		}
		slotsGeneratedKeys.close();
		myStmt.close();
		notificationModel.clear();
		jsonResponse.put("message", "Notifications Added Successfully");
		jsonResponse.put("services", notificationModel);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@PostMapping("/getAppointments/{id}/{userType}/{pageNumber}/{recordsByPage}")
	public ResponseEntity<Object> getAppointments(@PathVariable("id") int id, @PathVariable("userType") String userType,
			@PathVariable("pageNumber") int pageNumber, @PathVariable("recordsByPage") int recordsByPage,
			@RequestBody AppointmentFilters appointmentFilters) throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		String query = "select COUNT(*) AS total_count from appointments_table at join business_account_schedule_slots_table basset on basset.slotId = at.slotFk  where "
				+ (userType.equals("PATIENT") ? "at.userFk =?" : "at.businessAccountFk =?") + " "
				+ (appointmentFilters.getAppointmentType().equals("ALL") ? ""
						: appointmentFilters.getAppointmentType().equals("UPCOMING")
								? " and basset.slotStartTime > '" + timestamp + "'"
								: " and basset.slotStartTime < '" + timestamp + "'")
				+ (appointmentFilters.getAppointmentStatus().toString().equals("ACCEPTED")
						? " and at.appointmentStatus = 'ACCEPTED'"
						: appointmentFilters.getAppointmentStatus().toString().equals("PENDING")
								? " and at.appointmentStatus = 'PENDING'"
								: appointmentFilters.getAppointmentStatus().toString().equals("REJECTED")
										? " and at.appointmentStatus = 'REJECTED'"
										: "" + (appointmentFilters.getIsCancelled() == 1 ? " and at.isCancelled=1"
												: appointmentFilters.getIsCancelled() == 0 ? " and at.isCancelled= 0"
														: ""));

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, id);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;
		if (myRs.next()) {

			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") * 1.0 / recordsByPage);
		}
		myRs.close();

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"select at.isCancelled, at.businessAccountFk, at.userFk as currentUserId,basset.slotId, basset.slotStartTime,"
						+ "at.appointmentActualStartTime," + " at.appointmentActualEndTime "
						+ ",at.appointmentStatus, ut.firstName, ut.lastName, ut.userEmail, ut.userId as businessAccountUserId,"
						+ "ut.profilePicture,bast.serviceName,bast.servicePrice, bast.currencyUnit,"
						+ " at.appointmentId,   COALESCE(apt.prescriptionId, -1) as prescriptionId,"
						+ "COALESCE(apt.prescriptionDescription, -1) as prescriptionDescription from appointments_table "
						+ "at left join appointment_prescriptions_table apt on apt.appointmentFk=at.appointmentId  "
						+ "join business_accounts_services_table bast on bast.serviceId=at.serviceFk"
						+ " join business_account_schedule_slots_table basset on basset.slotId = at.slotFk "
						+ "join business_account_table bat on bat.businessAccountId=at.businessAccountFk "
						+ "join users_table ut on ut.userId="
						+ (userType.equals("PATIENT") ? "bat.userFk" : "at.userFk") + " where "
						+ (userType.equals("PATIENT") ? "at.userFk =?" : "at.businessAccountFk =?") + " "
						+ (appointmentFilters.getAppointmentType().equals("ALL") ? ""
								: appointmentFilters.getAppointmentType().equals("UPCOMING")
										? " and basset.slotStartTime > '" + timestamp + "'"
										: " and basset.slotStartTime < '" + timestamp + "'")
						+ (appointmentFilters.getAppointmentStatus().toString().equals("ACCEPTED")
								? " and at.appointmentStatus = 'ACCEPTED'"
								: appointmentFilters.getAppointmentStatus().toString().equals("PENDING")
										? " and at.appointmentStatus = 'PENDING'"
										: appointmentFilters.getAppointmentStatus().toString().equals("REJECTED")
												? " and at.appointmentStatus = 'REJECTED'"
												: "" + (appointmentFilters.getIsCancelled() == 1
														? " and at.isCancelled=1"
														: appointmentFilters.getIsCancelled() == 0
																? " and at.isCancelled= 0"
																: ""))

						+ " ORDER BY timestamp(basset.slotStartTime) DESC Limit " + recordsByPage + " OFFSET "
						+ (pageNumber - 1) * recordsByPage);
		myStmt.setInt(1, id);

		ResultSet myRs2 = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();

		while (myRs2.next()) {
			JSONObject json = new JSONObject();
			json.put("slotStartTime", myRs2.getTimestamp("slotStartTime"));
			json.put("appointmentActualStartTime", myRs2.getTimestamp("appointmentActualStartTime"));
			json.put("appointmentActualEndTime", myRs2.getTimestamp("appointmentActualEndTime"));
			json.put("appointmentStatus", myRs2.getString("appointmentStatus"));
			json.put("currentUserId", myRs2.getInt("currentUserId"));
			json.put("businessAccountFk", myRs2.getInt("businessAccountFk"));
			json.put("slotId", myRs2.getInt("slotId"));
			json.put("isCancelled", myRs2.getBoolean("isCancelled"));
			json.put("businessAccountUserId", myRs2.getInt("businessAccountUserId"));

			json.put("firstName", myRs2.getString("firstName"));
			json.put("lastName", myRs2.getString("lastName"));
			json.put("userEmail", myRs2.getString("userEmail"));
			json.put("profilePicture", myRs2.getString("profilePicture"));
			json.put("serviceName", myRs2.getString("serviceName"));
			json.put("prescriptionDescription", myRs2.getString("prescriptionDescription"));
			json.put("prescriptionId", myRs2.getInt("prescriptionId"));

			json.put("servicePrice", myRs2.getDouble("servicePrice"));
			json.put("currencyUnit", myRs2.getString("currencyUnit"));
			json.put("appointmentId", myRs2.getInt("appointmentId"));

			jsonArray.put(json);

		}
		myRs2.close();

		myStmt.close();
		jsonResponse.put("message", "Appointments Returned");
		jsonResponse.put("appointments", jsonArray);
		jsonResponse.put("totalNumberOfPages", totalNumberOfPages);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/addAppointmentResult")
	public ResponseEntity<Object> addAppointmentResult(@RequestBody AppointmentResultModel appointmentResultModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"insert into appointment_result_table (appointmentFk, resultDescription, resultDocument) values ( ?, ?,?)",
				Statement.RETURN_GENERATED_KEYS);

		myStmt.setInt(1, appointmentResultModel.getAppointmentFk());
		myStmt.setString(2, appointmentResultModel.getResultDescription());
		myStmt.setString(3, appointmentResultModel.getResultDocument());

		myStmt.executeUpdate();
		ResultSet slotsGeneratedKeys = myStmt.getGeneratedKeys();
		int appointmentResultId = -1;
		while (slotsGeneratedKeys.next()) {
			appointmentResultId = slotsGeneratedKeys.getInt(1);
			appointmentResultModel.setResultId(appointmentResultId);
		}
		slotsGeneratedKeys.close();

		// send notification to the user
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"select *, bat.userFk as userFromFk from appointments_table ap join business_account_schedule_slots_table basst on basst.slotId=ap.slotFk join business_account_table bat on bat.businessAccountId = ap.businessAccountFk join users_table ut on ut.userId=bat.userFk where ap.appointmentId=?");
		myStmt.setInt(1, appointmentResultModel.getAppointmentFk());
		ResultSet myRs = myStmt.executeQuery();
		while (myRs.next()) {
			JSONObject json = new JSONObject();
			String firstName = myRs.getString("firstName");
			String lastName = myRs.getString("lastName");
			Date slotDate = myRs.getDate("slotDate");
			int userFromFk = myRs.getInt("userFromFk");

			int userFk = myRs.getInt("userFk");
			String message = "The result of the appointment with doctor " + firstName + " " + lastName + " on "
					+ slotDate + " is ready. Check it out";
			json.put("message", message);
			json.put("appointmentResultId", appointmentResultId);
			NotificationsModel notification = new NotificationsModel(false, -1, userFk, userFromFk, message,
					NotificationType.APPOINTMENT_RESULT, "/appointment-result/" + appointmentResultId);
			notificationModel.add(notification);
			addNotification(notificationModel);
			notificationModel.clear();
			messagingTemplate.convertAndSend("/topic/appointmentResult/" + userFk, json.toString());

		}
		myRs.close();
		myStmt.close();
		Gson gson = new Gson();
		String appointmentResultModelJson = gson.toJson(appointmentResultModel);
		JsonObject jsonObject = JsonParser.parseString(appointmentResultModelJson).getAsJsonObject();
		JSONObject jsonReturned = new JSONObject(jsonObject.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAsJsonPrimitive().getAsString())));
		jsonResponse.put("message", "Appointment Result Created Successfully");
		jsonResponse.put("result", jsonReturned);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getAppointmentPrescription/{appointmentFk}")
	public ResponseEntity<Object> getAppointmentPrescription(@PathVariable("appointmentFk") int appointmentFk)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon()
				.prepareStatement("select * from appointment_prescriptions_table where appointmentFk=?");
		myStmt.setInt(1, appointmentFk);

		ResultSet myRs = myStmt.executeQuery();
		JSONObject json = new JSONObject();

		if (myRs.next()) {

			json.put("prescriptionId", myRs.getInt("prescriptionId"));
			json.put("appointmentFk", myRs.getInt("appointmentFk"));
			json.put("prescriptionDescription", myRs.getString("prescriptionDescription"));

		}
		myRs.close();
		myStmt.close();

		jsonResponse.put("message", "Prescription Returned");
		jsonResponse.put("result", json);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getAppointmentById/{appointmentFk}")
	public ResponseEntity<Object> getAppointmentById(@PathVariable("appointmentFk") int appointmentFk)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon()
				.prepareStatement("select basst.slotStartTime ,atp.appointmentDescription, "
						+ "ut2.firstName as patientFirstName, ut2.lastName as patientLastName,"
						+ " ut2.userEmail as patientUserEmail, ut2.profilePicture as" + " patientProfilePicture,"
						+ " ut1.firstName as doctorFirstName, ut1.lastName as doctorLastName,"
						+ " ut1.userEmail as doctorUserEmail, ut1.profilePicture as doctorProfilePicture"
						+ "  from appointments_table atp join " + "business_account_schedule_slots_table basst "
						+ "on basst.slotId=atp.slotFk join business_account_table bat on "
						+ "bat.businessAccountId=atp.businessAccountFk join users_table ut1"
						+ " on ut1.userId=bat.userFk join users_table ut2 on ut2.userId=atp.userFk "
						+ "where atp.appointmentId=?");
		myStmt.setInt(1, appointmentFk);

		ResultSet myRs = myStmt.executeQuery();
		JSONObject json = new JSONObject();

		if (myRs.next()) {

			json.put("slotStartTime", myRs.getTimestamp("slotStartTime"));
			json.put("appointmentDescription", myRs.getString("appointmentDescription"));
			json.put("patientFirstName", myRs.getString("patientFirstName"));
			json.put("patientLastName", myRs.getString("patientLastName"));
			json.put("patientUserEmail", myRs.getString("patientUserEmail"));
			json.put("patientProfilePicture", myRs.getString("patientProfilePicture"));
			json.put("doctorFirstName", myRs.getString("doctorFirstName"));
			json.put("doctorLastName", myRs.getString("doctorLastName"));
			json.put("doctorUserEmail", myRs.getString("doctorUserEmail"));
			json.put("doctorProfilePicture", myRs.getString("doctorProfilePicture"));

		}
		myRs.close();
		myStmt.close();

		jsonResponse.put("message", "Appointment Returned");
		jsonResponse.put("appointment", json);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getAppointmentResult/{appointmentFk}")
	public ResponseEntity<Object> getAppointmentResult(@PathVariable("appointmentFk") int appointmentFk)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"select * from appointment_result_table art join appointments_table at on at.appointmentId=art.appointmentFk join business_account_table bat on bat.businessAccountId= at.businessAccountFk join users_table u on u.userId=bat.userFk where  art.appointmentFk=?");
		myStmt.setInt(1, appointmentFk);

		ResultSet myRs = myStmt.executeQuery();
		JSONObject json = new JSONObject();

		if (myRs.next()) {

			json.put("resultId", myRs.getInt("resultId"));
			json.put("businessAccountFk", myRs.getInt("businessAccountFk"));
			json.put("firstName", myRs.getString("firstName"));
			json.put("lastName", myRs.getString("lastName"));
			json.put("userEmail", myRs.getString("userEmail"));
			json.put("profilePicture", myRs.getString("profilePicture"));
			json.put("resultDescription", myRs.getString("resultDescription"));
			json.put("resultDocument", myRs.getString("resultDocument"));

		}
		myRs.close();
		myStmt.close();
		jsonResponse.put("message", "Result Returned");
		jsonResponse.put("result", json);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getRevenueOfYear/{businessaccountFk}/{userFk}")
	public ResponseEntity<Object> getRevenueOfYear(@PathVariable("businessaccountFk") int businessaccountFk,
			@PathVariable("userFk") int userFk) throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();
		String query = "SELECT DATE_FORMAT(a.slotDate, '%m') AS month, COUNT(*) AS num_appointments, "
				+ "SUM(s.servicePrice) AS revenue, s.currencyUnit FROM appointments_table AS"
				+ " ap JOIN business_account_schedule_slots_table AS a ON ap.slotFk = a.slotId JOIN "
				+ "business_accounts_services_table AS s ON a.serviceFk = s.serviceId WHERE"
				+ " YEAR(a.slotDate) = YEAR(NOW()) and "
				+ (businessaccountFk == -1 ? "ap.userFk=?" : " ap.businessAccountFk=?") + " GROUP BY "
				+ "DATE_FORMAT(a.slotDate, '%m'), s.currencyUnit";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, businessaccountFk == -1 ? userFk : businessaccountFk);

		ResultSet myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();

		while (myRs.next()) {
			JSONObject json = new JSONObject();
			json.put("month", myRs.getInt("month"));

			json.put("currencyUnit", myRs.getString("currencyUnit"));
			json.put("revenue", myRs.getDouble("revenue"));
			jsonArray.put(json);

		}
		myRs.close();
		myStmt.close();
		jsonResponse.put("message", "Result Returned");
		jsonResponse.put("result", jsonArray);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/addAppointmentPrescription")
	public ResponseEntity<Object> addAppointmentPrescription(
			@RequestBody AppointmentPrescriptionModel appointmentPrescriptionModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"insert into appointment_prescriptions_table (appointmentFk, prescriptionDescription) values ( ?, ?)",
				Statement.RETURN_GENERATED_KEYS);

		myStmt.setInt(1, appointmentPrescriptionModel.getAppointmentFk());
		myStmt.setString(2, appointmentPrescriptionModel.getPrescriptionDescription());

		myStmt.executeUpdate();
		ResultSet slotsGeneratedKeys = myStmt.getGeneratedKeys();
		int prescriptionId = -1;
		while (slotsGeneratedKeys.next()) {
			prescriptionId = slotsGeneratedKeys.getInt(1);
			appointmentPrescriptionModel.setPrescriptionId(prescriptionId);
		}
		slotsGeneratedKeys.close();
		// send notification to the user
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"select *, bat.userFk as userFromFk from appointments_table ap join business_account_schedule_slots_table basst on basst.slotId=ap.slotFk join business_account_table bat on bat.businessAccountId = ap.businessAccountFk join users_table ut on ut.userId=bat.userFk where ap.appointmentId=?");
		myStmt.setInt(1, appointmentPrescriptionModel.getAppointmentFk());
		ResultSet myRs = myStmt.executeQuery();
		JSONObject jsonReturned = new JSONObject();

		if (myRs.next()) {
			JSONObject json = new JSONObject();
			String firstName = myRs.getString("firstName");
			String lastName = myRs.getString("lastName");
			Date slotDate = myRs.getDate("slotDate");
			int userFromFk = myRs.getInt("userFromFk");

			int userFk = myRs.getInt("userFk");
			String message = "Doctor " + firstName + " " + lastName + " added prescription for your appointment on "
					+ slotDate + " . Check it out";
			json.put("message", message);
			json.put("prescriptionId", prescriptionId);
			json.put("appointmentFk", appointmentPrescriptionModel.getAppointmentFk());

			NotificationsModel notification = new NotificationsModel(false, -1, userFk, userFromFk, message,
					NotificationType.RESERVED_APPOINTMENT, "/appointment_prescription/" + prescriptionId);
			notificationModel.add(notification);
			addNotification(notificationModel);
			notificationModel.clear();
			myStmt.close();

			messagingTemplate.convertAndSend("/topic/appointmentPrescription/" + userFk, json.toString());

		}
		myRs.close();
		myStmt.close();

		jsonReturned.put("prescriptionId", prescriptionId);
		jsonReturned.put("appointmentFk", appointmentPrescriptionModel.getAppointmentFk());
		jsonReturned.put("prescriptionDescription", appointmentPrescriptionModel.getPrescriptionDescription());

		jsonResponse.put("message", "Appointment Prescription Result Successfully");
		jsonResponse.put("result", jsonReturned);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@PutMapping("/updateAppointmentPrescription")
	public ResponseEntity<Object> updateAppointmentPrescription(
			@RequestBody AppointmentPrescriptionModel appointmentPrescriptionModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"update appointment_prescriptions_table set prescriptionDescription=? where appointmentFk=?");
		myStmt.setString(1, appointmentPrescriptionModel.getPrescriptionDescription());
		myStmt.setInt(2, appointmentPrescriptionModel.getAppointmentFk());

		myStmt.executeUpdate();

		// send notification to the user
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"select *, bat.userFk as userFromFk from appointments_table ap join business_account_schedule_slots_table basst on basst.slotId=ap.slotFk join business_account_table bat on bat.businessAccountId = ap.businessAccountFk join users_table ut on ut.userId=bat.userFk where ap.appointmentId=?");
		myStmt.setInt(1, appointmentPrescriptionModel.getAppointmentFk());
		ResultSet myRs = myStmt.executeQuery();
		if (myRs.next()) {
			JSONObject json = new JSONObject();
			String firstName = myRs.getString("firstName");
			String lastName = myRs.getString("lastName");
			Date slotDate = myRs.getDate("slotDate");
			int userFromFk = myRs.getInt("userFromFk");

			int userFk = myRs.getInt("userFk");
			String message = "Doctor " + firstName + " " + lastName + " added prescription for your appointment on "
					+ slotDate + " . Check it out";
			json.put("message", message);
			json.put("prescriptionId", appointmentPrescriptionModel.getPrescriptionId());
			json.put("appointmentFk", appointmentPrescriptionModel.getAppointmentFk());

			NotificationsModel notification = new NotificationsModel(false, -1, userFk, userFromFk, message,
					NotificationType.RESERVED_APPOINTMENT,
					"/appointment_prescription/" + appointmentPrescriptionModel.getPrescriptionId());
			notificationModel.add(notification);
			addNotification(notificationModel);
			notificationModel.clear();
			myStmt.close();

			messagingTemplate.convertAndSend("/topic/appointmentPrescription/" + userFk, json.toString());
			JSONObject jsonReturned = new JSONObject();
			jsonReturned.put("prescriptionId", appointmentPrescriptionModel.getPrescriptionId());
			jsonReturned.put("appointmentFk", appointmentPrescriptionModel.getAppointmentFk());
			jsonReturned.put("prescriptionDescription", appointmentPrescriptionModel.getPrescriptionDescription());

			jsonResponse.put("message", "Appointment Prescription Updated Successfully");
			jsonResponse.put("result", jsonReturned);
			jsonResponse.put("responseCode", 200);
			return ResponseEntity.ok(jsonResponse.toString());
		}

		return null;

	}

	// passed
	@DeleteMapping("/deleteService")
	public ResponseEntity<Object> deleteService(@RequestBody ServiceModel[] serviceModel)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon()
				.prepareStatement("delete from business_accounts_services_table where serviceId=?");
		for (int i = 0; i < serviceModel.length; i++) {
			myStmt.setInt(1, serviceModel[i].getServiceId());

			myStmt.addBatch();
		}
		myStmt.executeBatch();

		myStmt.executeUpdate();
		myStmt.close();
		jsonResponse.put("message", "Service Deleted successfully");

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getServices/{businessAccountFk}")
	public ResponseEntity<Object> getServices(@PathVariable("businessAccountFk") int businessAccountFk)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon()
				.prepareStatement("select * from business_accounts_services_table where businessAccountFk=?");

		myStmt.setInt(1, businessAccountFk);

		ResultSet rs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (rs.next()) {
			JSONObject json = new JSONObject();
			json.put("serviceName", rs.getString("serviceName"));
			json.put("servicePrice", rs.getString("servicePrice"));
			json.put("currencyUnit", rs.getString("currencyUnit"));
			json.put("serviceId", rs.getInt("serviceId"));
			jsonArray.put(json);
		}
		rs.close();
		myStmt.close();
		jsonResponse.put("message", "Service");
		jsonResponse.put("services", jsonArray);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/addService")
	public ResponseEntity<Object> addService(@RequestBody ServiceModel[] serviceModel)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"insert into business_accounts_services_table (businessAccountFk, servicePrice, serviceName, currencyUnit) values ( ?,?,?, ?)",
				Statement.RETURN_GENERATED_KEYS);

		for (int i = 0; i < serviceModel.length; i++) {
			myStmt.setInt(1, serviceModel[i].getBusinessAccountFk());
			myStmt.setDouble(2, serviceModel[i].getServicePrice());
			myStmt.setString(3, serviceModel[i].getServiceName());
			myStmt.setString(4, serviceModel[i].getCurrencyUnit().toString());

			myStmt.addBatch();
		}
		myStmt.executeBatch();
		ResultSet slotsGeneratedKeys = myStmt.getGeneratedKeys();
		int i = 0;
		while (slotsGeneratedKeys.next()) {
			serviceModel[i].setServiceId(slotsGeneratedKeys.getInt(1));
			i++;
		}
		slotsGeneratedKeys.close();
		myStmt.close();

		jsonResponse.put("message", "Service Created Successfully");
		jsonResponse.put("services", serviceModel);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@PostMapping("/updateNotificationsSettings/{userFk}")
	public ResponseEntity<Object> updateNotificationsSettings(@PathVariable("userFk") int userFk,
			@RequestBody NotificationsSettings notificationsSettings) throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();
		String query = "update notifications_settings set onReferral=?,onFavorite=?,onScheduleReminder=?,onAppointmentReservation=?,onAddFeatureEmail=?,onAppointmentReminder=? where userFk=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		myStmt.setBoolean(1, notificationsSettings.getOnReferral());
		myStmt.setBoolean(2, notificationsSettings.getOnFavorite());
		myStmt.setBoolean(3, notificationsSettings.getOnScheduleReminder());
		myStmt.setBoolean(4, notificationsSettings.getOnAppointmentReservation());
		myStmt.setBoolean(5, notificationsSettings.getOnAddFeatureEmail());

		myStmt.setBoolean(6, notificationsSettings.getOnAppointmentReminder());

		myStmt.setInt(7, userFk);

		myStmt.executeUpdate();
		ResultSet slotsGeneratedKeys = myStmt.getGeneratedKeys();
		while (slotsGeneratedKeys.next()) {
			notificationsSettings.setNotificationSettingsId(slotsGeneratedKeys.getInt(1));
		}
		slotsGeneratedKeys.close();
		myStmt.close();

		jsonResponse.put("notificationsSettings", notificationsSettings);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@GetMapping("/getNotificationsSettings/{userFk}")
	public ResponseEntity<Object> getNotificationsSettings(@PathVariable("userFk") int userFk)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		String query = "select COALESCE(onReferral, 1) as onReferral, COALESCE(onFavorite, 1) as onFavorite, COALESCE(onScheduleReminder, 1) as onScheduleReminder,"
				+ " COALESCE(onAppointmentReservation, 1) as onAppointmentReservation, COALESCE(onAddFeatureEmail, 1) as onAddFeatureEmail, COALESCE(onAppointmentReminder, 1) as onAppointmentReminder from notifications_settings where userFk=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);

		ResultSet rs = myStmt.executeQuery();
		JSONObject jsonResponse = new JSONObject();
		JSONObject json = new JSONObject();

		if (rs.next()) {
			json.put("onReferral", rs.getBoolean("onReferral"));
			json.put("onFavorite", rs.getBoolean("onFavorite"));
			json.put("onScheduleReminder", rs.getBoolean("onScheduleReminder"));
			json.put("onAppointmentReservation", rs.getBoolean("onAppointmentReservation"));
			json.put("onAddFeatureEmail", rs.getBoolean("onAddFeatureEmail"));
			json.put("onAppointmentReminder", rs.getBoolean("onAppointmentReminder"));

		} else {
			query = "insert into notifications_settings (onReferral,onFavorite, onScheduleReminder, onAppointmentReservation,onAddFeatureEmail,onAppointmentReminder, userFk) values (1,1,1,1,1,1,?)";
			myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
			myStmt.setInt(1, userFk);

			myStmt.executeUpdate();
			json.put("onReferral", 1);
			json.put("onFavorite", 1);
			json.put("onScheduleReminder", 1);
			json.put("onAppointmentReservation", 1);
			json.put("onAddFeatureEmail", 1);
			json.put("onAppointmentReminder", 1);
		}
		jsonResponse.put("notificationsSettings", json);
		jsonResponse.put("responseCode", 200);
		rs.close();
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PutMapping("/updateService")
	public ResponseEntity<Object> updateService(@RequestBody ServiceModel[] serviceModel)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"update business_accounts_services_table set servicePrice = ? , serviceName = ? , currencyUnit =? where serviceId=?");

		for (int i = 0; i < serviceModel.length; i++) {
			myStmt.setDouble(1, serviceModel[i].getServicePrice());
			myStmt.setString(2, serviceModel[i].getServiceName());
			myStmt.setString(3, serviceModel[i].getCurrencyUnit().toString());
			myStmt.setInt(4, serviceModel[i].getServiceId());

			myStmt.addBatch();
		}
		myStmt.executeBatch();

		myStmt.close();

		jsonResponse.put("message", "Services Updated Successfully");
		jsonResponse.put("services", serviceModel);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getBlockedUsers/{businessAccountFk}/{pageNumber}/{recordsByPage}")
	public ResponseEntity<Object> getBlockedUsers(@PathVariable("businessAccountFk") int businessAccountFk,
			@PathVariable("pageNumber") int pageNumber, @PathVariable("recordsByPage") int recordsByPage)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		String query = "select COUNT(*) AS total_count from business_account_blockings_table where businessAccountFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, businessAccountFk);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") * 1.0 / recordsByPage);
		}
		myRs.close();

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"select * from business_account_blockings_table babt join users_table u on u.userId=babt.userFk where businessAccountFk=? Limit "
						+ recordsByPage + " OFFSET " + (pageNumber - 1) * recordsByPage);

		myStmt.setInt(1, businessAccountFk);

		ResultSet myRs2 = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs2.next()) {
			JSONObject json = new JSONObject();

			json.put("blockId", myRs2.getInt("blockId"));
			json.put("businessAccountFk", myRs2.getInt("businessAccountFk"));
			json.put("userFk", myRs2.getInt("userFk"));
			json.put("firstName", myRs2.getString("firstName"));
			json.put("lastName", myRs2.getString("lastName"));
			json.put("userEmail", myRs2.getString("userEmail"));
			json.put("profilePicture", myRs2.getString("profilePicture"));

			jsonArray.put(json);

		}
		myRs2.close();
		myStmt.close();
		jsonResponse.put("message", "All Blocked");
		jsonResponse.put("blocked", jsonArray);
		jsonResponse.put("totalNumberOfPages", totalNumberOfPages);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/blockUser")
	public ResponseEntity<Object> blockUser(@RequestBody BlockModel blockModel) throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"insert into business_account_blockings_table (businessAccountFk, userFk) values ( ?, ?)",
				Statement.RETURN_GENERATED_KEYS);

		myStmt.setInt(1, blockModel.getBusinessAccountFk());
		myStmt.setInt(2, blockModel.getUserFk());

		myStmt.executeUpdate();
		try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				int id = generatedKeys.getInt(1);
				JSONObject json = new JSONObject();
				json.put("blockId", id);
				json.put("businessAccountFk", blockModel.getBusinessAccountFk());
				json.put("userFk", blockModel.getUserFk());
				generatedKeys.close();
				myStmt.close();
				jsonResponse.put("message", "User Blocked successfully");
				jsonResponse.put("blockInfo", json);

				jsonResponse.put("responseCode", 200);
				return ResponseEntity.ok(jsonResponse.toString());
			}

		}
		return null;

	}

	// passed
	@DeleteMapping("/removeBlockUser/{blockId}")
	public ResponseEntity<Object> removeBlockUser(@PathVariable("blockId") int blockId)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon()
				.prepareStatement("delete from business_account_blockings_table where blockId=?");

		myStmt.setInt(1, blockId);

		myStmt.executeUpdate();
		myStmt.close();
		jsonResponse.put("message", "User Unblocked successfully");

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	public static void registerBusinessAccount(BusinessAccountModel businessAccountModel)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"insert into business_account_table (userFk, specialityFk, biography, clinicLocation, clinicLocationLongitude, clinicLocationLatitude) values ( ?, ?, ?, ?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS);

		myStmt.setInt(1, businessAccountModel.getUserFk());
		myStmt.setInt(2, businessAccountModel.getSpecialityFk());
		myStmt.setString(3, businessAccountModel.getBiography());
		myStmt.setString(4, businessAccountModel.getClinicLocation());
		myStmt.setDouble(5, businessAccountModel.getClinicLocationLongitude());
		myStmt.setDouble(6, businessAccountModel.getClinicLocationLatitude());
		myStmt.executeUpdate();
		myStmt.close();

	}

	// passed
	@PutMapping("/updateBusinessAccount")
	public ResponseEntity<Object> updateBusinessAccount(@RequestBody BusinessAccountModel businessAccountModel)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();
		String query = "update business_account_table set specialityFk = ? , biography=?, clinicLocation=?, clinicLocationLongitude=?, clinicLocationLatitude=? where businessAccountId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		try {
			myStmt.setInt(1, businessAccountModel.getSpecialityFk());
			myStmt.setString(2, businessAccountModel.getBiography());
			myStmt.setString(3, businessAccountModel.getClinicLocation());
			myStmt.setDouble(4, businessAccountModel.getClinicLocationLongitude());
			myStmt.setDouble(5, businessAccountModel.getClinicLocationLatitude());
			myStmt.setInt(6, businessAccountModel.getBusinessAccountId());
			myStmt.executeUpdate();
			myStmt.close();

			jsonResponse.put("message", "Business account updated successfully");
			jsonResponse.put("responseCode", 200);
			return ResponseEntity.ok(jsonResponse.toString());
		} catch (Exception e) {
			myStmt.close();

			jsonResponse.put("message", "Unknown Error");
			jsonResponse.put("responseCode", -1);
			return ResponseEntity.ok(jsonResponse.toString());
		}

	}

	// passed
	@GetMapping("/getBusinessAccount/{userFk}")
	public ResponseEntity<Object> getBusinessAccount(@PathVariable("userFk") int userFk)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		String query = "select businessAccountId,specialityFk,specialityName,specialityDescription,biography,"
				+ " clinicLocation,   COALESCE(clinicLocationLongitude, -1) as clinicLocationLongitude,"
				+ " COALESCE(clinicLocationLatitude, -1) as clinicLocationLatitude from "
				+ "business_account_table bat right join specialities_table "
				+ "st on st.specialityId=bat.specialityFk where userFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);

		ResultSet myRs = myStmt.executeQuery();
		while (myRs.next()) {
			JSONObject json = new JSONObject();
			json.put("businessAccountId", myRs.getInt("businessAccountId"));
			json.put("userFk", userFk);
			json.put("specialityFk", myRs.getInt("specialityFk"));
			json.put("specialityName", myRs.getString("specialityName"));
			json.put("specialityDescription", myRs.getString("specialityDescription"));

			json.put("biography", myRs.getString("biography"));
			json.put("clinicLocation", myRs.getString("clinicLocation"));
			json.put("clinicLocationLongitude", myRs.getFloat("clinicLocationLongitude"));
			json.put("clinicLocationLatitude", myRs.getFloat("clinicLocationLatitude"));

			myRs.close();
			myStmt.close();
			jsonResponse.put("message", "Success account");
			jsonResponse.put("businessAccount", json);
			jsonResponse.put("responseCode", 200);
			return ResponseEntity.ok(jsonResponse.toString());
		}
		myRs.close();
		myStmt.close();
		return null;
	}

	// passed
	public int getScheduleId(int businessAccountId) throws SQLException, IOException {
		PreparedStatement myStmt = null;

		int scheduleId = -1;
		String query = "select scheduleId from business_account_schedule_table where businessAccountFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, businessAccountId);
		ResultSet myRs = myStmt.executeQuery();

		if (myRs.next()) {
			scheduleId = myRs.getInt("scheduleId");
			myRs.close();
			myStmt.close();
			return scheduleId;
		}
		myRs.close();
		myStmt.close();
		return -1;
	}

	// passed
	@PutMapping("/modifySlotLock/{slotId}")
	public ResponseEntity<Object> modifySlotLock(@PathVariable("slotId") int slotId) throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();
		String query = "update business_account_schedule_slots_table set isLocked = CASE WHEN isLocked = true THEN false ELSE true END  where slotId=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, slotId);
		myStmt.executeUpdate();
		myStmt.close();
		jsonResponse.put("message", "Slot status modified Successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getBusinessAccountSchedule/{businessAccountId}/{pageNumber}/{recordsByPage}")
	public ResponseEntity<Object> getBusinessAccountSchedule(@PathVariable("businessAccountId") int businessAccountId,
			@PathVariable("pageNumber") int pageNumber, @PathVariable("recordsByPage") int recordsByPage)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt = null;

		String query = "";
		ResultSet myRs = null;
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Timestamp.class, new TimestampDeserializer());
		mapper.registerModule(module);
		JSONObject jsonResponse = new JSONObject();
		int scheduleId = getScheduleId(businessAccountId);
		if (scheduleId == -1) {
			jsonResponse.put("message", "No schedule");
			jsonResponse.put("responseCode", -1);
			return ResponseEntity.ok(jsonResponse.toString());
		}
		int totalNumberOfPages = 0;
		if (pageNumber != -1 || recordsByPage != -1) {
			query = "select COUNT(*) AS total_count from business_account_schedule_slots_table basst where basst.isDeleted=0 and basst.scheduleFk=? ";
			myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
			myStmt.setInt(1, scheduleId);
			myRs = myStmt.executeQuery();

			if (myRs.next()) {
				totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") * 1.0 / recordsByPage);
			}
			myRs.close();
		}
		query = "select slotId,scheduleFk,slotDate,slotStartTime,slotEndTime,isLocked, isReserved from business_account_schedule_slots_table basst where basst.scheduleFk=? and basst.isDeleted=0  Group By slotId "
				+ (pageNumber != -1 || recordsByPage != -1
						? "limit " + recordsByPage + " OFFSET " + (pageNumber - 1) * recordsByPage
						: "");
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, scheduleId);

		myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();

		while (myRs.next()) {
			JSONObject json = new JSONObject();
			json.put("slotId", myRs.getInt("slotId"));
			json.put("scheduleFk", myRs.getInt("scheduleFk"));
			json.put("slotDate", myRs.getDate("slotDate"));
			json.put("slotStartTime", myRs.getTimestamp("slotStartTime"));
			json.put("slotEndTime", myRs.getTimestamp("slotEndTime"));
			json.put("isLocked", myRs.getBoolean("isLocked"));
			json.put("isReserved", myRs.getBoolean("isReserved"));

			jsonArray.put(json);

		}
		myRs.close();
		myStmt.close();
		JSONObject response = new JSONObject();
		if (pageNumber != -1 || recordsByPage != -1) {
			response.put("totalNumberOfPages", totalNumberOfPages);

		}
		response.put("businessAccountSchedule", jsonArray);

		jsonResponse.put("message", "Success account");
		jsonResponse.put("body", response);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	public boolean isBusinessAccountExist(@PathVariable("businessAccountId") int businessAccountId)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		String query = "select * from business_account_table where businessAccountId=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, businessAccountId);

		ResultSet myRs = myStmt.executeQuery();
		if (myRs.next()) {

			myRs.close();
			myStmt.close();

			return true;
		}
		myRs.close();
		myStmt.close();
		return false;
	}

	// passed
	@DeleteMapping("/deleteSchedule/{businessAccountFk}")
	public ResponseEntity<Object> deleteSchedule(@PathVariable("businessAccountFk") int businessAccountFk)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();
		String query = "select scheduleId from business_account_schedule_table where businessAccountFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		int scheduleId = -1;
		myStmt.setInt(1, businessAccountFk);
		ResultSet rs = myStmt.executeQuery();
		while (rs.next()) {
			scheduleId = rs.getInt("scheduleId");
		}
		query = "update business_account_schedule_slots_table set isDeleted=1 where scheduleFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, scheduleId);
		myStmt.executeUpdate();

		rs.close();
		myStmt.close();

		jsonResponse.put("message", "Schedule deleted Successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());
	}

	// passed
	@PostMapping("/setSchedule/{businessAccountFk}")
	public ResponseEntity<Object> setSchedule(@PathVariable("businessAccountFk") int businessAccountFk,
			@RequestBody BusinessAccountScheduleSlotModel[] businessAccountScheduleSlotModel)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();
		String query = "";
		try {

			if (!isBusinessAccountExist(businessAccountFk)) {
				jsonResponse.put("message", "Business Account Not Found");
				jsonResponse.put("responseCode", -1);
				return ResponseEntity.ok(jsonResponse.toString());

			}
			int scheduleId = getScheduleId(businessAccountFk);

			if (scheduleId != -1) {
				deleteSchedule(scheduleId);

			}
			if (scheduleId == -1) {
				query = "INSERT INTO business_account_schedule_table (businessAccountFk) VALUES (?)";
				myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query,
						Statement.RETURN_GENERATED_KEYS);
				myStmt.setInt(1, businessAccountFk);
				myStmt.executeUpdate();
				try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						scheduleId = generatedKeys.getInt(1);
					}
					generatedKeys.close();
					myStmt.close();
				}

			}

			query = "INSERT INTO business_account_schedule_slots_table (scheduleFk, slotDate, slotStartTime, slotEndTime, serviceFk) VALUES (?, ?, ?, ?, ?)";
			myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query,
					Statement.RETURN_GENERATED_KEYS);

			for (int i = 0; i < businessAccountScheduleSlotModel.length; i++) {
				myStmt.setInt(1, scheduleId);
				myStmt.setDate(2, businessAccountScheduleSlotModel[i].getSlotDate());
				myStmt.setTimestamp(3, Timestamp.valueOf(businessAccountScheduleSlotModel[i].getSlotStartTime()));
				myStmt.setTimestamp(4, Timestamp.valueOf(businessAccountScheduleSlotModel[i].getSlotEndTime()));
				myStmt.setInt(5, businessAccountScheduleSlotModel[i].getServiceFk());

				myStmt.addBatch();
			}
			myStmt.executeBatch();
			ResultSet slotsGeneratedKeys = myStmt.getGeneratedKeys();
			int i = 0;
			while (slotsGeneratedKeys.next()) {
				businessAccountScheduleSlotModel[i].setSlotId(slotsGeneratedKeys.getInt(1));
				businessAccountScheduleSlotModel[i].setScheduleFk(scheduleId);

				i++;
			}
			slotsGeneratedKeys.close();
			myStmt.close();

			jsonResponse.put("message", "Schedule Created Successfully");
			jsonResponse.put("schedule", businessAccountScheduleSlotModel);
			jsonResponse.put("businessAccountFk", businessAccountFk);

			jsonResponse.put("responseCode", 200);
			myStmt = DatabaseConnection.getInstance().getMyCon()
					.prepareStatement("select * from business_account_schedule_slots_table" + " basst"
							+ " join business_accounts_services_table "
							+ " bast on bast.serviceId=basst.serviceFk where scheduleFk=?");
			myStmt.setInt(1, scheduleId);
			ResultSet s2 = myStmt.executeQuery();
			JSONArray jsonArraySocketResponse = new JSONArray();
			JSONObject jsonSocketResponse = new JSONObject();

			while (s2.next()) {
				JSONObject jsonSocket = new JSONObject();
				jsonSocket.put("currencyUnit", s2.getString("currencyUnit"));
				jsonSocket.put("isLocked", s2.getBoolean("isLocked"));
				jsonSocket.put("isReserved", s2.getString("isReserved"));
				jsonSocket.put("serviceId", s2.getInt("serviceId"));
				jsonSocket.put("serviceName", s2.getString("serviceName"));
				jsonSocket.put("servicePrice", s2.getInt("servicePrice"));
				jsonSocket.put("slotDate", s2.getString("slotDate"));
				jsonSocket.put("slotEndTime", s2.getTimestamp("slotEndTime"));
				jsonSocket.put("slotId", s2.getInt("slotId"));
				jsonSocket.put("slotStartTime", s2.getTimestamp("slotStartTime"));
				jsonArraySocketResponse.put(jsonSocket);

			}
			jsonSocketResponse.put("schedule", jsonArraySocketResponse);

			jsonSocketResponse.put("businessAccountFk", businessAccountFk);

			messagingTemplate.convertAndSend("/topic/schedules", jsonSocketResponse.toString());
			return ResponseEntity.ok(jsonResponse.toString());
		} catch (Exception e) {
			jsonResponse.put("message", e.getMessage());
			jsonResponse.put("responseCode", -1);
			return ResponseEntity.ok(jsonResponse.toString());
		}

	}

	@PostMapping("/globalSearch/{pageNumber}/{recordsByPage}")
	public ResponseEntity<Object> globalSearch(@RequestBody GlobalSearchModel globalSearchModel,
			@PathVariable("pageNumber") int pageNumber, @PathVariable("recordsByPage") int recordsByPage)
			throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		if (!Double.isNaN(globalSearchModel.getMinDistance())) {
			if (Double.isNaN(globalSearchModel.getMaxDistance())) {
				jsonResponse.put("message", "Provide max distance");
				jsonResponse.put("responseCode", -1);
				return ResponseEntity.ok(jsonResponse.toString());

			}
		}
		if (!Double.isNaN(globalSearchModel.getMaxDistance())) {
			if (Double.isNaN(globalSearchModel.getMinDistance())) {
				jsonResponse.put("message", "Provide min distance");
				jsonResponse.put("responseCode", -1);
				return ResponseEntity.ok(jsonResponse.toString());

			}
		}

		if (!Double.isNaN(globalSearchModel.getMyLatitude())) {
			if (Double.isNaN(globalSearchModel.getMyLongitude())) {
				jsonResponse.put("message", "Provide longitude value");
				jsonResponse.put("responseCode", -1);
				return ResponseEntity.ok(jsonResponse.toString());

			}
		}

		if (!Double.isNaN(globalSearchModel.getMyLongitude())) {
			if (Double.isNaN(globalSearchModel.getMyLatitude())) {
				jsonResponse.put("message", "Provide latitude value");
				jsonResponse.put("responseCode", -1);
				return ResponseEntity.ok(jsonResponse.toString());

			}
		}

		String query = "select COUNT(*) AS total_count from business_account_table bat join users_table u on u.userId = bat.userFk  where u.isApproved=1 and u.isVerified=1";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;
		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") * 1.0 / recordsByPage);
		}
		myRs.close();
		query = "select * from (SELECT "
				+ (globalSearchModel.getUserId() == -1 ? "" : "COALESCE(fv.favoriteId, -1) as favoriteId,")
				+ "  basst.isDeleted, temp.userId,srt.servicePrice,srt.currencyUnit, srt.serviceName,srt.serviceId,basst.slotId,basst.isReserved, basst.slotDate, basst.slotStartTime, basst.slotEndTime, basst.isLocked,temp.biography, temp.clinicLocation,temp.clinicLocationLongitude,temp.clinicLocationLatitude,  temp.businessAccountId,  temp.firstName, temp.lastName, temp.userEmail, temp.profilePicture, st.specialityName, st.specialityDescription    from (select *  from business_account_table bat join users_table u on u.userId = bat.userFk where u.isVerified=1 and u.isApproved=1 Limit "
				+ recordsByPage + " OFFSET " + ((pageNumber - 1) * recordsByPage) + ") as temp join specialities_table "
				+ " st on temp.specialityFk = st.specialityId join business_account_schedule_table bast "
				+ " on bast.businessAccountFk = temp.businessAccountId left join business_account_schedule_slots_table basst on basst.scheduleFk = bast.scheduleId "
				+ " left join business_accounts_services_table srt on srt.serviceId = basst.serviceFk ";
		if (globalSearchModel.getUserId() != -1) {
			query += " left join (select COALESCE(ft.businessAccountFk, -1) as favoriteBusinessAccountFk, COALESCE(ft.favoriteId, -1) as favoriteId from favorites_table ft where ft.userFk="
					+ globalSearchModel.getUserId()
					+ ")  as fv ON fv.favoriteBusinessAccountFk = temp.businessAccountId";
		}
		boolean appendWhere = true;
		if (globalSearchModel.getSpecialityFk() != -1) {
			query += " where st.specialityId=" + globalSearchModel.getSpecialityFk();
			appendWhere=false;
		}

		if (globalSearchModel.getMinPrice() != -1 && globalSearchModel.getMaxPrice() != -1
				&& !Double.isNaN(globalSearchModel.getMinPrice()) && !Double.isNaN(globalSearchModel.getMaxPrice())) {

			query += (!appendWhere ? "and " : " where ") + "srt.servicePrice >=  " + globalSearchModel.getMinPrice()
					+ " and srt.servicePrice <= " + globalSearchModel.getMaxPrice();
			appendWhere=false;


		}
		if (globalSearchModel.getMinAvailability() != null && globalSearchModel.getMaxAvailability() != null) {
			query += (!appendWhere ? "and " : " where ") + " basst.slotStartTime >=  '"
					+ globalSearchModel.getMinAvailability() + "' and basst.slotEndTime <= '"
					+ globalSearchModel.getMaxAvailability() + "'";
			appendWhere=false;


		}
		query += ") as total";
		if (!globalSearchModel.getSearchText().equals("null")) {
			query += (!appendWhere ? " and " : " where ") + " firstName LIKE '%" + globalSearchModel.getSearchText()
					+ "%' or lastName LIKE '%" + globalSearchModel.getSearchText() + "%' or clinicLocation LIKE '%"
					+ globalSearchModel.getSearchText() + "%' ";
			appendWhere=false;

		}
		if (globalSearchModel.getIsFavorite() != -2 && globalSearchModel.getUserId() != -1) {
			query += (!appendWhere ? " and " : " where ") + " total.favoriteId " + (globalSearchModel.getIsFavorite() == -1 ? " = -1 " : " != -1");
			appendWhere=false;

		}

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		try {
			myRs = myStmt.executeQuery();
			JSONArray jsonArray = new JSONArray();
			Map<Integer, JSONObject> map = new HashMap<Integer, JSONObject>();

			while (myRs.next()) {

				JSONObject userDetails = new JSONObject();
				JSONObject userSchedule = new JSONObject();
				int businessAccountId = myRs.getInt("businessAccountId");
				userSchedule.put("servicePrice", myRs.getInt("servicePrice"));
				userSchedule.put("serviceName", myRs.getString("serviceName"));
				userSchedule.put("currencyUnit", myRs.getString("currencyUnit"));

				userSchedule.put("serviceId", myRs.getInt("serviceId"));
				userSchedule.put("slotDate", myRs.getDate("slotDate"));
				userSchedule.put("slotStartTime", myRs.getTimestamp("slotStartTime"));
				userSchedule.put("slotEndTime", myRs.getTimestamp("slotEndTime"));
				userSchedule.put("isLocked", myRs.getBoolean("isLocked"));
				userSchedule.put("slotId", myRs.getInt("slotId"));
				userSchedule.put("isReserved", myRs.getBoolean("isReserved"));

				if (map.containsKey(businessAccountId)) {
					JSONObject json = new JSONObject();

					JSONArray userScheduleArray = (JSONArray) map.get(businessAccountId).get("userSchedule");
					boolean isDeleted = myRs.getBoolean("isDeleted");
					if (!isDeleted) {
						userScheduleArray.put(userSchedule);
					}
					json.put("userDetails", map.get(businessAccountId).get("userDetails"));
					json.put("userSchedule", userScheduleArray);

					map.put(businessAccountId, json);
				} else {
					JSONObject json = new JSONObject();
					JSONArray userScheduleArray = new JSONArray();
					userDetails.put("userId", myRs.getInt("userId"));
					if (globalSearchModel.getUserId() != -1) {
						userDetails.put("favoriteId", myRs.getInt("favoriteId"));

					} else {
						userDetails.put("favoriteId", -1);

					}
					userDetails.put("biography", myRs.getString("biography"));
					userDetails.put("clinicLocation", myRs.getString("clinicLocation"));
					userDetails.put("clinicLocationLongitude", myRs.getDouble("clinicLocationLongitude"));
					userDetails.put("clinicLocationLatitude", myRs.getDouble("clinicLocationLatitude"));
					userDetails.put("businessAccountId", businessAccountId);
					userDetails.put("firstName", myRs.getString("firstName"));
					userDetails.put("lastName", myRs.getString("lastName"));
					userDetails.put("userEmail", myRs.getString("userEmail"));
					userDetails.put("profilePicture", myRs.getString("profilePicture"));
					userDetails.put("specialityName", myRs.getString("specialityName"));
					userDetails.put("specialityDescription", myRs.getString("specialityDescription"));
					json.put("userDetails", userDetails);
					userScheduleArray.put(userSchedule);
					json.put("userSchedule", userScheduleArray);

					map.put(businessAccountId, json);

				}

			}
			myRs.close();
			myStmt.close();
			for (Entry<Integer, JSONObject> entry : map.entrySet()) {
				JSONObject value = entry.getValue();
				JSONObject userDetails = value.getJSONObject("userDetails");
				if (globalSearchModel.getMinDistance() != -1 && globalSearchModel.getMaxDistance() != -1
						&& userDetails.getDouble("clinicLocationLatitude") != -1) {
					double distance = CommonFunctions.distance(globalSearchModel.getMyLatitude(),
							globalSearchModel.getMyLongitude(), userDetails.getDouble("clinicLocationLatitude"),
							userDetails.getDouble("clinicLocationLongitude"));
					if (distance >= globalSearchModel.getMinDistance()
							&& distance <= globalSearchModel.getMaxDistance()) {
						value.put("distance", distance);

						jsonArray.put(value);

					}
				} else {
					jsonArray.put(value);

				}
			}
			jsonResponse.put("message", "All Results");
			jsonResponse.put("data", jsonArray);
			jsonResponse.put("totalNumberOfPages", totalNumberOfPages);
			jsonResponse.put("responseCode", 200);
			return ResponseEntity.ok(jsonResponse.toString());
		} catch (Exception e) {
			jsonResponse.put("message", "Fields Error");
			jsonResponse.put("responseCode", -1);
			return ResponseEntity.ok(jsonResponse.toString());
		}

	}

	// passed
	@GetMapping("/getAdminStatistics")
	public ResponseEntity<Object> getAdminStatistics() throws SQLException, IOException {
		JSONObject jsonResponse = new JSONObject();
		PreparedStatement myStmt = null;

		String query = "Select * from (SELECT COUNT(apt.appointmentId) as total_appointments, COUNT(DISTINCT apt.userFk) AS total_patients FROM appointments_table apt"

				+ ") as p1 JOIN (SELECT COUNT(ut.userId) as total_hps from users_table ut where ut.userRole='HEALTH_PROFESSIONAL'"

				+ ") as p2 JOIN (SELECT COUNT(ut.userId) as total_users from users_table ut where ut.userRole='PATIENT'"
				+ ") as p3";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		ResultSet myRs = myStmt.executeQuery();
		JSONObject json = new JSONObject();
		if (myRs.next()) {

			json.put("totalAppointments", myRs.getInt("total_appointments"));
			json.put("totalPatients", myRs.getInt("total_patients"));
			json.put("totalHps", myRs.getInt("total_hps"));
			json.put("totalUsers", myRs.getInt("total_users"));

		}
		myRs.close();
		myStmt.close();
		jsonResponse.put("message", "Results Returned");
		jsonResponse.put("result", json);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getAllHealthProfessionals/{pageNumber}/{recordsByPage}/{searchText}/{isApproved}")
	public ResponseEntity<Object> getAllHealthProfessionals(@PathVariable("searchText") String searchText,
			@PathVariable("pageNumber") int pageNumber, @PathVariable("recordsByPage") int recordsByPage,
			@PathVariable("isApproved") int isApproved) throws SQLException, IOException {
		PreparedStatement myStmt = null;

		JSONObject jsonResponse = new JSONObject();

		String query = "select count(*) as total_count from users_table ut where ut.userRole='HEALTH_PROFESSIONAL'";
		if (!searchText.equals("null")) {
			query += " and (ut.userEmail LIKE '%" + searchText + "%' or ut.firstName LIKE '%" + searchText
					+ "%' or ut.lastName LIKE '%" + searchText + "%')";
		}
		if (isApproved != -2) {
			query += " and isApproved=" + isApproved;
		}
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") * 1.0 / recordsByPage);
		}
		myRs.close();
		query = "select * from users_table ut where ut.userRole='HEALTH_PROFESSIONAL'"
				+ (!searchText.equals("null") ? " and (ut.userEmail LIKE '%" + searchText + "%' or ut.firstName LIKE '%"
						+ searchText + "%' or ut.lastName LIKE '%" + searchText + "%')" : "");
		if (isApproved != -2) {
			query += " and isApproved=" + isApproved;
		}
		query += " Limit " + recordsByPage + " OFFSET " + (pageNumber - 1) * recordsByPage;
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs.next()) {
			JSONObject json = new JSONObject();

			json.put("userId", myRs.getInt("userId"));
			json.put("firstName", myRs.getString("firstName"));
			json.put("lastName", myRs.getString("lastName"));
			json.put("userEmail", myRs.getString("userEmail"));
			json.put("profilePicture", myRs.getString("profilePicture"));
			json.put("isApproved", myRs.getBoolean("isApproved"));
			json.put("isVerified", myRs.getBoolean("isVerified"));

			jsonArray.put(json);

		}
		myRs.close();
		myStmt.close();
		jsonResponse.put("message", "All Hps");
		jsonResponse.put("hps", jsonArray);
		jsonResponse.put("totalNumberOfPages", totalNumberOfPages);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}
}
