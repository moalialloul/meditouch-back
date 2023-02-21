package com.example.meditouch.controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

import Enums.NotificationType;
import models.AppointmentPrescriptionModel;
import models.AppointmentReferralModel;
import models.AppointmentResultModel;
import models.BlockModel;
import models.BusinessAccountModel;
import models.BusinessAccountScheduleModel;
import models.BusinessAccountScheduleSlotModel;
import models.GlobalSearchModel;
import models.NotificationsModel;
import models.ServiceModel;

@RestController
public class BusinessAccountController {
	private SimpMessagingTemplate messagingTemplate;
	static PreparedStatement myStmt;
	List<NotificationsModel> notificationModel = new ArrayList<NotificationsModel>();

	public BusinessAccountController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	@PostMapping("/addReferrals")
	public ResponseEntity<Object> addReferrals(@RequestBody List<AppointmentReferralModel> appointmentReferralModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "insert into business_account_referrals_table (userFk, appointmentFk,referralDescription, referredByBusinessAccountFk, referredToBusinessAccountFk) values(?,?,?,?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		for (int i = 0; i < appointmentReferralModel.size(); i++) {
			myStmt.setInt(1, appointmentReferralModel.get(i).getUserFk());
			myStmt.setInt(2, appointmentReferralModel.get(i).getAppointmentFk());
			myStmt.setString(3, appointmentReferralModel.get(i).getReferralDescription());
			myStmt.setInt(4, appointmentReferralModel.get(i).getReferredByBusinessAccountFk());
			myStmt.setInt(5, appointmentReferralModel.get(i).getReferredToBusinessAccountFk());

			myStmt.addBatch();

		}

		myStmt.executeBatch();
		ResultSet slotsGeneratedKeys = myStmt.getGeneratedKeys();
		int i = 0;
		while (slotsGeneratedKeys.next()) {
			appointmentReferralModel.get(i).setReferralId(slotsGeneratedKeys.getInt(1));

			JSONObject json = new JSONObject();
			json.put("type", "ADD");
			json.put("referral", appointmentReferralModel.get(i));

			messagingTemplate.convertAndSend(
					"/topic/referral/" + appointmentReferralModel.get(i).getReferredToBusinessAccountFk(),
					json.toString());
			i++;
		}
		myStmt.close();

		jsonResponse.put("message", "Referral Added Successfully");
		jsonResponse.put("referral", appointmentReferralModel);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@DeleteMapping("/deleteReferrals")
	public ResponseEntity<Object> deleteReferrals(@RequestBody List<AppointmentReferralModel> appointmentReferralModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "delete from business_account_referrals_table where referralId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		for (int i = 0; i < appointmentReferralModel.size(); i++) {
			myStmt.setInt(1, appointmentReferralModel.get(i).getReferralId());

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
		JSONObject jsonResponse = new JSONObject();
		String query = "insert into notifications_table (userToFk, userFromFk, notificationText, notificationType, appointmentFk, commentFk, promoCodeFk, referralFk, favoriteFk, appointmentResultFk) values (?,?,?,?,?,?,?,?,?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		for (int i = 0; i < notificationModel.size(); i++) {
			myStmt.setInt(1, notificationModel.get(i).getUserToFk());
			myStmt.setInt(2, notificationModel.get(i).getUserToFk());
			myStmt.setString(3, notificationModel.get(i).getNotificationText());
			myStmt.setString(4, notificationModel.get(i).getNotificationType().toString());
			myStmt.setInt(5, notificationModel.get(i).getAppointmentFk());
			myStmt.setInt(6, notificationModel.get(i).getCommentFk());
			myStmt.setInt(7, notificationModel.get(i).getPromoCodeFk());
			myStmt.setInt(8, notificationModel.get(i).getReferralFk());
			myStmt.setInt(9, notificationModel.get(i).getFavoriteFk());
			myStmt.setInt(10, notificationModel.get(i).getFavoriteFk());

			myStmt.addBatch();

		}

		myStmt.executeBatch();
		ResultSet slotsGeneratedKeys = myStmt.getGeneratedKeys();
		int i = 0;
		while (slotsGeneratedKeys.next()) {
			notificationModel.get(i).setNotificationId(slotsGeneratedKeys.getInt(1));
			i++;
		}
		myStmt.close();
		notificationModel.clear();
		jsonResponse.put("message", "Notifications Added Successfully");
		jsonResponse.put("services", notificationModel);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@GetMapping("/getAppointments/{userFk}/{pageNumber}/{recordsByPage}")
	public ResponseEntity<Object> getAppointments(@PathVariable("userFk") int userFk,
			@PathVariable("pageNumber") int pageNumber, @PathVariable("recordsByPage") int recordsByPage)
			throws SQLException, IOException {
		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"select * from appointments_tabel at join business_account_table bat on bat.businessAccountId=at.businessAccountFk join users_table ut on ut.userId=bat.userFk where at.userFk=? Limit "
						+ recordsByPage + " OFFSET " + (pageNumber - 1) * recordsByPage);
		myStmt.setInt(1, userFk);

		ResultSet myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();

		while (myRs.next()) {
			JSONObject json = new JSONObject();

			json.put("appointmentActualStartTime", myRs.getTimestamp("appointmentActualStartTime"));
			json.put("appointmentActualEndTime", myRs.getTimestamp("appointmentActualEndTime"));
			json.put("appointmentStatus", myRs.getString("appointmentStatus"));
			json.put("firstName", myRs.getString("firstName"));
			json.put("lastName", myRs.getString("lastName"));
			json.put("userEmail", myRs.getString("userEmail"));
			json.put("profilePicture", myRs.getString("profilePicture"));
			jsonArray.put(json);

		}
		jsonResponse.put("message", "Appointments Returned");
		jsonResponse.put("appointments", jsonArray);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@PostMapping("/addAppointmentResult")
	public ResponseEntity<Object> addAppointmentResult(AppointmentResultModel appointmentResultModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
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
		// send notification to the user
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"select *, bat.userFk as userFromFk from appointments_table ap join business_account_schedule_slots_table basst on basst.slotId=ap.slotFk join business_account_table bat on bat.businessAccountId = ap.businessAccountFk join users_table ut on ut.userId=bat.userFk where ap.appointmentFk=?");
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
					NotificationType.APPOINTMENT_RESULT, -1, -1, -1, -1, -1, appointmentResultId);
			notificationModel.add(notification);
			addNotification(notificationModel);
			notificationModel.clear();
			messagingTemplate.convertAndSend("/topic/appointmentResult/" + userFk, json.toString());

		}
		myStmt.close();

		jsonResponse.put("message", "Appointment Result Created Successfully");
		jsonResponse.put("result", appointmentResultModel);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@GetMapping("/getAppointmentPrescription/{appointmentFk}")
	public ResponseEntity<Object> getAppointmentPrescription(@PathVariable("appointmentFk") int appointmentFk)
			throws SQLException, IOException {
		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon()
				.prepareStatement("select * from appointment_prescriptions_table where appointmentFk=?");
		myStmt.setInt(1, appointmentFk);

		ResultSet myRs = myStmt.executeQuery();
		while (myRs.next()) {
			JSONObject json = new JSONObject();

			json.put("prescriptionId", myRs.getInt("prescriptionId"));
			json.put("appointmentFk", myRs.getInt("appointmentFk"));
			json.put("prescriptionDescription", myRs.getString("prescriptionDescription"));

			jsonResponse.put("message", "Prescription Returned");
			jsonResponse.put("result", json);

			jsonResponse.put("responseCode", 200);
			return ResponseEntity.ok(jsonResponse.toString());

		}
		return null;

	}

	@GetMapping("/getAppointmentResult/{appointmentFk}")
	public ResponseEntity<Object> getAppointmentResult(@PathVariable("appointmentFk") int appointmentFk)
			throws SQLException, IOException {
		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"select * from appointment_result_table art join appointments_table at on at.appointmentId=art.appointmentFk join business_account_table bat on bat.businessAccountId= at.businessAccountId join users_table u on u.userId=bat.userFk where  art.appointmentFk=?");
		myStmt.setInt(1, appointmentFk);

		ResultSet myRs = myStmt.executeQuery();
		while (myRs.next()) {
			JSONObject json = new JSONObject();

			json.put("resultId", myRs.getInt("resultId"));
			json.put("businessAccountFk", myRs.getInt("businessAccountFk"));
			json.put("firstName", myRs.getString("firstName"));
			json.put("lastName", myRs.getString("lastName"));
			json.put("userEmail", myRs.getString("userEmail"));
			json.put("profilePicture", myRs.getString("profilePicture"));
			json.put("resultDescription", myRs.getString("resultDescription"));
			json.put("resultDocument", myRs.getString("resultDocument"));

			jsonResponse.put("message", "Result Returned");
			jsonResponse.put("result", json);

			jsonResponse.put("responseCode", 200);
			return ResponseEntity.ok(jsonResponse.toString());

		}
		return null;

	}

	@PostMapping("/addAppointmentPrescription")
	public ResponseEntity<Object> addAppointmentPrescription(
			@RequestBody AppointmentPrescriptionModel appointmentPrescriptionModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
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

		// send notification to the user
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"select *, bat.userFk as userFromFk from appointments_table ap join business_account_schedule_slots_table basst on basst.slotId=ap.slotFk join business_account_table bat on bat.businessAccountId = ap.businessAccountFk join users_table ut on ut.userId=bat.userFk where ap.appointmentId=?");
		myStmt.setInt(1, appointmentPrescriptionModel.getAppointmentFk());
		ResultSet myRs = myStmt.executeQuery();
		while (myRs.next()) {
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
					NotificationType.RESERVED_APPOINTMENT, prescriptionId, -1, -1, -1, -1, -1);
			notificationModel.add(notification);
			addNotification(notificationModel);
			notificationModel.clear();
			messagingTemplate.convertAndSend("/topic/appointmentPrescription/" + userFk, json.toString());

		}

		myStmt.close();
		Gson gson = new Gson();
		String appointmentPrescriptionModelJson = gson.toJson(appointmentPrescriptionModel);
		JsonObject jsonObject = JsonParser.parseString(appointmentPrescriptionModelJson).getAsJsonObject();
		JSONObject jsonReturned = new JSONObject(jsonObject.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAsJsonPrimitive().getAsString())));
		jsonResponse.put("message", "Appointment Prescription Result Successfully");
		jsonResponse.put("result", jsonReturned);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@DeleteMapping("/deleteService/{serviceId}")
	public ResponseEntity<Object> deleteService(@PathVariable("serviceId") int serviceId)
			throws SQLException, IOException {
		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon()
				.prepareStatement("delete from business_accounts_services_table where serviceId=?");

		myStmt.setInt(1, serviceId);

		myStmt.executeUpdate();
		jsonResponse.put("message", "Service Deleted successfully");

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/addService")
	public ResponseEntity<Object> addService(@RequestBody ServiceModel[] serviceModel)
			throws SQLException, IOException {
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
		myStmt.close();

		jsonResponse.put("message", "Service Created Successfully");
		jsonResponse.put("services", serviceModel);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/updateService")
	public ResponseEntity<Object> updateService(@RequestBody ServiceModel[] serviceModel)
			throws SQLException, IOException {
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
		JSONObject jsonResponse = new JSONObject();

		String query = "select COUNT(*) AS total_count from business_account_blockings_table where businessAccountFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, businessAccountFk);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") / recordsByPage);
		}

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"select * from business_account_blockings_table babt join users_table u on u.userId=babt.userFk where businessAccountFk=? Limit "
						+ recordsByPage + " OFFSET " + (pageNumber - 1) * recordsByPage);

		myStmt.setInt(1, businessAccountFk);

		myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs.next()) {
			JSONObject json = new JSONObject();

			json.put("blockId", myRs.getInt("blockId"));
			json.put("businessAccountFk", myRs.getInt("businessAccountFk"));
			json.put("userFk", myRs.getInt("userFk"));
			json.put("firstName", myRs.getString("firstName"));
			json.put("lastName", myRs.getString("lastName"));
			json.put("userEmail", myRs.getString("userEmail"));
			json.put("profilePicture", myRs.getString("profilePicture"));

			jsonArray.put(json);

		}
		jsonResponse.put("message", "All Favorites");
		jsonResponse.put("favorites", jsonArray);
		jsonResponse.put("totalNumberOfPages", totalNumberOfPages);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/blockUser")
	public ResponseEntity<Object> blockUser(@RequestBody BlockModel blockModel) throws SQLException, IOException {
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
		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon()
				.prepareStatement("delete from business_account_blockings_table where blockId=?");

		myStmt.setInt(1, blockId);

		myStmt.executeUpdate();
		jsonResponse.put("message", "User Unblocked successfully");

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	public static void registerBusinessAccount(BusinessAccountModel businessAccountModel)
			throws SQLException, IOException {
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

	}

	// passed
	@PostMapping("/updateBusinessAccount")
	public ResponseEntity<Object> updateBusinessAccount(@RequestBody BusinessAccountModel businessAccountModel)
			throws SQLException, IOException {
		JSONObject jsonResponse = new JSONObject();
		String query = "update business_account_table set specialityFk = ? , biography=?, clinicLocation=?, clinicLocationLongitude=?, clinicLocationLatitude=? where userFk=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		try {
			myStmt.setInt(1, businessAccountModel.getSpecialityFk());
			myStmt.setString(2, businessAccountModel.getBiography());
			myStmt.setString(3, businessAccountModel.getClinicLocation());
			myStmt.setDouble(4, businessAccountModel.getClinicLocationLongitude());
			myStmt.setDouble(5, businessAccountModel.getClinicLocationLatitude());
			myStmt.setInt(6, businessAccountModel.getUserFk());
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
		ObjectMapper mapper = new ObjectMapper();
		JSONObject jsonResponse = new JSONObject();

		String query = "select * from business_account_table where userFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);

		ResultSet myRs = myStmt.executeQuery();
		while (myRs.next()) {
			JSONObject json = new JSONObject();
			json.put("businessAccountId", myRs.getInt("businessAccountId"));
			json.put("userFk", myRs.getInt("userFk"));
			json.put("specialityFk", myRs.getInt("specialityFk"));
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
		int scheduleId = -1;
		String query = "select scheduleId from business_account_schedule_table where businessAccountFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, businessAccountId);
		ResultSet myRs = myStmt.executeQuery();

		if (myRs.next()) {
			scheduleId = myRs.getInt("scheduleId");
			return scheduleId;
		}
		return -1;
	}

	@PostMapping("/modifySlotLock/{slotId}")
	public ResponseEntity<Object> modifySlotLock(int slotId) throws SQLException, IOException {
		JSONObject jsonResponse = new JSONObject();
		String query = "update business_account_schedule_slots_table set isLocked = CASE WHEN isLocked = true THEN false ELSE true END  where slotId=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, slotId);
		myStmt.executeUpdate();
		jsonResponse.put("message", "Slot status modified Successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getBusinessAccountSchedule/{businessAccountId}/{pageNumber}/{recordsByPage}")
	public ResponseEntity<Object> getBusinessAccountSchedule(@PathVariable("businessAccountId") int businessAccountId,
			@PathVariable("pageNumber") int pageNumber, @PathVariable("recordsByPage") int recordsByPage)
			throws SQLException, IOException, NoSuchAlgorithmException {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Timestamp.class, new TimestampDeserializer());
		mapper.registerModule(module);
		JSONObject jsonResponse = new JSONObject();
		int scheduleId = getScheduleId(businessAccountId);
		if (scheduleId == -1) {
			return null;
		}
		String query = "select COUNT(*) AS total_count from business_account_schedule_slots_table where scheduleFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, scheduleId);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") / recordsByPage);
		}
		query = "select slotId,scheduleFk,slotDate,slotStartTime,slotEndTime,isLocked from business_account_schedule_slots_table where scheduleFk=? Group By slotId limit "
				+ recordsByPage + " OFFSET " + (pageNumber - 1) * recordsByPage;
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

			jsonArray.put(json);

		}
		myRs.close();
		myStmt.close();
		JSONObject response = new JSONObject();
		response.put("totalNumberOfPages", totalNumberOfPages);
		response.put("businessAccountSchedule", jsonArray);

		jsonResponse.put("message", "Success account");
		jsonResponse.put("body", response);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	public boolean isBusinessAccountExist(@PathVariable("businessAccountId") int businessAccountId)
			throws SQLException, IOException {

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
		JSONObject jsonResponse = new JSONObject();
		String query = "select scheduleId from business_account_schedule_table where businessAccountFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		int scheduleId = -1;
		myStmt.setInt(1, businessAccountFk);
		ResultSet rs = myStmt.executeQuery();
		while (rs.next()) {
			scheduleId = rs.getInt("scheduleId");
		}
		query = "delete from business_account_schedule_slots_table where scheduleFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, scheduleId);
		myStmt.executeUpdate();

		query = "delete from business_account_schedule_table where businessAccountFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		myStmt.setInt(1, businessAccountFk);
		myStmt.executeUpdate();
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
				myStmt.close();

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
				}
				myStmt.close();

			}

			query = "INSERT INTO business_account_schedule_slots_table (scheduleFk, slotDate, slotStartTime, slotEndTime, serviceFk) VALUES (?, ?, ?, ?, ?)";
			myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query,
					Statement.RETURN_GENERATED_KEYS);

			for (int i = 0; i < businessAccountScheduleSlotModel.length; i++) {
				myStmt.setInt(1, scheduleId);
				myStmt.setDate(2, businessAccountScheduleSlotModel[i].getSlotDate());
				myStmt.setTimestamp(3, businessAccountScheduleSlotModel[i].getSlotStartTime());
				myStmt.setTimestamp(4, businessAccountScheduleSlotModel[i].getSlotEndTime());
				myStmt.setInt(5, businessAccountScheduleSlotModel[i].getServiceFk());

				myStmt.addBatch();
			}
			myStmt.executeBatch();
			ResultSet slotsGeneratedKeys = myStmt.getGeneratedKeys();
			int i = 0;
			while (slotsGeneratedKeys.next()) {
				businessAccountScheduleSlotModel[i].setSlotId(slotsGeneratedKeys.getInt(1));
				i++;
			}
			myStmt.close();

			jsonResponse.put("message", "Schedule Created Successfully");
			jsonResponse.put("schedule", businessAccountScheduleSlotModel);
			jsonResponse.put("responseCode", 200);
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
		boolean appendWhere = true;
		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") / recordsByPage);
		}
		query = "SELECT * from (select * from business_account_table bat join users_table u on u.userId = bat.userFk where u.isVerified=1 and u.isApproved=1 Limit 2 OFFSET 0) as temp join specialities_table "
				+ " st on temp.specialityFk = st.specialityId join business_account_schedule_table bast "
				+ " on bast.businessAccountFk = temp.businessAccountId join business_account_schedule_slots_table basst on basst.scheduleFk = bast.scheduleId "
				+ " join business_accounts_services_table srt on srt.serviceId = basst.serviceFk ";
		if (globalSearchModel.getSpecialityFk() != -1) {
			appendWhere = false;
			query += " where st.specialityId=" + globalSearchModel.getSpecialityFk();
		}

		if (globalSearchModel.getMinPrice() != -1 && globalSearchModel.getMaxPrice() != -1
				&& !Double.isNaN(globalSearchModel.getMinPrice()) && !Double.isNaN(globalSearchModel.getMaxPrice())) {
			appendWhere = false;

			query += (appendWhere ? " where " : " and ") + "  st.servicePrice >=  " + globalSearchModel.getMinPrice()
					+ " and st.servicePrice <= " + globalSearchModel.getMaxPrice();

		}
		if (globalSearchModel.getMinAvailability() != null && globalSearchModel.getMaxAvailability() != null) {
			query += (appendWhere ? " where " : " and ") + " basst.slotStartTime >=  "
					+ globalSearchModel.getMinAvailability() + " and baast.slotEndTime <= "
					+ globalSearchModel.getMaxAvailability();
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
				userSchedule.put("servicePrice", myRs.getString("servicePrice"));
				userSchedule.put("serviceName", myRs.getString("serviceName"));
				userSchedule.put("serviceId", myRs.getInt("serviceId"));
				userSchedule.put("slotDate", myRs.getDate("slotDate"));
				userSchedule.put("slotStartTime", myRs.getTimestamp("slotStartTime"));
				userSchedule.put("slotEndTime", myRs.getTimestamp("slotEndTime"));
				userSchedule.put("isLocked", myRs.getBoolean("isLocked"));
				if (map.containsKey(businessAccountId)) {
					JSONObject json = new JSONObject();

					JSONArray userScheduleArray = (JSONArray) map.get(businessAccountId).get("userSchedule");
					userScheduleArray.put(userSchedule);
					json.put("userDetails", map.get(businessAccountId).get("userDetails"));
					json.put("userSchedule", userScheduleArray);

					map.put(businessAccountId, json);
				} else {
					JSONObject json = new JSONObject();
					JSONArray userScheduleArray = new JSONArray();
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
}
