

package com.example.meditouch.controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

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
import com.example.meditouch.PasswordUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Enums.NotificationType;
import Enums.TokenType;
import Enums.UserRoles;
import models.AppointmentModel;
import models.BlogModel;
import models.BusinessAccountModel;
import models.CommunityPostCommentModel;
import models.CommunityPostModel;
import models.ContactUsModel;
import models.FavoriteModel;
import models.FeedbackModel;
import models.MedicalInformationModel;
import models.NotificationsModel;
import models.PostponeAppointmentModel;
import models.ReservationSlot;
import models.SubscriptionModel;
import models.SurveyAnswersModel;
import models.SurveyModel;
import models.SurveyQuestionAnswersModel;
import models.Test;
import models.TokenModel;
import models.UpdatePasswordModel;
import models.UserModel;

@RestController
public class UserController {
	private SimpMessagingTemplate messagingTemplate;

	public UserController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	// passed
	@GetMapping("/getSurveyQuestionsAnswers/{surveyFk}")
	public ResponseEntity<Object> getSurveyQuestionsAnswers(@PathVariable("surveyFk") int surveyFk)
			throws SQLException, IOException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement("SELECT "
				+ "  sq.questionId AS questionId," + "  sq.questionText," + "  spa.answerId AS answerId,"
				+ "  spa.answerText," + "  COUNT(sqa.answerFk) AS answerCount" + " FROM survey_questions_table sq"
				+ " JOIN survey_questions_answers_table spa ON sq.questionId = spa.questionFk"
				+ " LEFT JOIN survey_answers_table sqa ON spa.answerId = sqa.answerFk" + " WHERE sq.surveyFk = ?"
				+ " GROUP BY sq.questionId, spa.answerId" + " ORDER BY sq.questionId, spa.answerId;" + "");
		myStmt.setInt(1, surveyFk);

		ResultSet myRs = myStmt.executeQuery();
		JSONArray resultArray = new JSONArray();
		while (myRs.next()) {
			int questionId = myRs.getInt("questionId");
			String questionText = myRs.getString("questionText");
			int answerId = myRs.getInt("answerId");
			String answerText = myRs.getString("answerText");
			int answerCount = myRs.getInt("answerCount");
			JSONObject questionObject = null;
			for (int i = 0; i < resultArray.length(); i++) {
				JSONObject obj = resultArray.getJSONObject(i);
				if (obj.getInt("questionId") == questionId) {
					questionObject = obj;
					break;
				}
			}
			if (questionObject == null) {
				questionObject = new JSONObject();
				questionObject.put("questionId", questionId);
				questionObject.put("questionText", questionText);
				questionObject.put("answers", new JSONArray());
				resultArray.put(questionObject);
			}

			JSONObject answerObject = new JSONObject();
			answerObject.put("answerId", answerId);
			answerObject.put("answerText", answerText);
			answerObject.put("answerCount", answerCount);
			questionObject.getJSONArray("answers").put(answerObject);

		}
		jsonResponse.put("message", "Answers Returned");
		jsonResponse.put("answers", resultArray);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/addSurveyAnswers")
	public ResponseEntity<Object> addSurveyAnswers(@RequestBody SurveyAnswersModel[] surveyAnswersModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "insert into survey_answers_table (questionFk, answerFk) values (?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		for (int i = 0; i < surveyAnswersModel.length; i++) {
			myStmt.setInt(1, surveyAnswersModel[i].getQuestionFk());
			myStmt.setInt(2, surveyAnswersModel[i].getAnswerFk());
			myStmt.addBatch();
		}

		myStmt.executeBatch();

		myStmt.close();

		jsonResponse.put("message", "Answers Submitted Successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/addSurvey")
	public ResponseEntity<Object> addSurvey(@RequestBody SurveyModel surveyModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "insert into surveys_table (surveyName, surveyDescription) values (?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		myStmt.setString(1, surveyModel.getSurveyName());
		myStmt.setString(2, surveyModel.getSurveyDescription());

		myStmt.executeUpdate();
		ResultSet slotsGeneratedKeys = myStmt.getGeneratedKeys();
		while (slotsGeneratedKeys.next()) {
			surveyModel.setSurveyId(slotsGeneratedKeys.getInt(1));
		}
		myStmt.close();

		Gson gson = new Gson();
		String surveyModelJson = gson.toJson(surveyModel);
		JsonObject jsonObject = JsonParser.parseString(surveyModelJson).getAsJsonObject();
		JSONObject jsonReturned = new JSONObject(jsonObject.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAsJsonPrimitive().getAsString())));

		jsonResponse.put("message", "Survey Created Successfully");
		jsonResponse.put("survey", jsonReturned);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getSurvey/{surveyFk}")
	public ResponseEntity<Object> getSurvey(@PathVariable("surveyFk") int surveyFk)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "select * from survey_questions_table sq join survey_questions_answers_table sqa on sqa.questionFk = sq.questionId where sq.surveyFk=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, surveyFk);

		ResultSet myRs = myStmt.executeQuery();
		Map<Integer, JSONObject> map = new HashMap<Integer, JSONObject>();
		while (myRs.next()) {
			int questionFk = myRs.getInt("questionFk");
			if (map.containsKey(questionFk)) {
				JSONObject json = map.get(questionFk);
				JSONObject jsonAnswer = new JSONObject();

				JSONArray answers = json.getJSONArray("answers");
				jsonAnswer.put("answerText", myRs.getString("answerText"));
				jsonAnswer.put("answerId", myRs.getInt("answerId"));
				answers.put(jsonAnswer);
				json.put("questionText", map.get(questionFk).get("questionText"));

				json.put("answers", answers);
				map.replace(questionFk, json);

			} else {
				JSONObject json = new JSONObject();
				JSONArray answers = new JSONArray();
				JSONObject jsonAnswers = new JSONObject();

				json.put("questionText", myRs.getString("questionText"));
				json.put("questionId", myRs.getInt("questionId"));

				jsonAnswers.put("answerId", myRs.getInt("answerId"));
				jsonAnswers.put("answerText", myRs.getString("answerText"));
				answers.put(jsonAnswers);
				json.put("answers", answers);
				map.put(questionFk, json);
			}

		}
		myStmt.close();

		// Create a JSONArray to store the final results
		JSONArray jsonArray = new JSONArray();

		// Convert the map to a JSONArray and add each JSONObject to the array
		for (JSONObject jsonObject : map.values()) {
			jsonArray.put(jsonObject);
		}

		jsonResponse.put("message", "Survey Questions");
		jsonResponse.put("surveyQuestions", jsonArray);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/addSurveyQuestionsAnswers/{surveyFk}")
	public ResponseEntity<Object> addSurveyQuestionsAnswers(@PathVariable("surveyFk") int surveyFk,
			@RequestBody SurveyQuestionAnswersModel[] surveyQuestionAnswersModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "insert into survey_questions_table (surveyFk,questionText) values (?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

		for (int i = 0; i < surveyQuestionAnswersModel.length; i++) {
			myStmt.setInt(1, surveyFk);
			myStmt.setString(2, surveyQuestionAnswersModel[i].getQuestionText());

			myStmt.addBatch();
		}

		myStmt.executeBatch();
		ResultSet slotsGeneratedKeys = myStmt.getGeneratedKeys();
		int i = 0;
		while (slotsGeneratedKeys.next()) {
			surveyQuestionAnswersModel[i].setQuestionFk(slotsGeneratedKeys.getInt(1));
			i++;
		}
		query = "insert into survey_questions_answers_table (questionFk, answerText) values (?,?)";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		for (int j = 0; j < surveyQuestionAnswersModel.length; j++) {
			for (int k = 0; k < surveyQuestionAnswersModel[j].getAnswerText().size(); k++) {
				myStmt.setInt(1, surveyQuestionAnswersModel[j].getQuestionFk());
				myStmt.setString(2, surveyQuestionAnswersModel[j].getAnswerText().get(k));
				myStmt.addBatch();

			}

		}
		myStmt.executeBatch();

		myStmt.close();

		jsonResponse.put("message", "Survey Questions Created Successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PutMapping("/updateBlog")
	public ResponseEntity<Object> updateBlog(@RequestBody BlogModel blogModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "update blogs_table set blogType=?, blogDate=?, blogTitle=?, blogUrl=? where blogId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setString(1, blogModel.getBlogType());
		myStmt.setTimestamp(2, Timestamp.valueOf(blogModel.getBlogDate()));
		myStmt.setString(3, blogModel.getBlogTitle());
		myStmt.setString(4, blogModel.getBlogUrl());
		myStmt.setInt(5, blogModel.getBlogId());

		myStmt.executeUpdate();

		myStmt.close();

		jsonResponse.put("message", "Blog Updated Successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@DeleteMapping("/deleteBlogs")
	public ResponseEntity<Object> deleteBlogs(@RequestBody BlogModel[] blogModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "delete from blogs_table where blogId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		for (int i = 0; i < blogModel.length; i++) {
			myStmt.setInt(1, blogModel[i].getBlogId());
			myStmt.addBatch();
		}

		myStmt.executeBatch();

		myStmt.close();

		jsonResponse.put("message", "Blogs Deleted Successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/addBlog")
	public ResponseEntity<Object> addBlog(@RequestBody BlogModel blogModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "insert into blogs_table (blogType, blogDate, blogTitle, blogUrl) values (?,?,?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		myStmt.setString(1, blogModel.getBlogType());
		myStmt.setTimestamp(2, Timestamp.valueOf(blogModel.getBlogDate()));
		myStmt.setString(3, blogModel.getBlogTitle());
		myStmt.setString(4, blogModel.getBlogUrl());

		myStmt.executeUpdate();
		ResultSet slotsGeneratedKeys = myStmt.getGeneratedKeys();
		while (slotsGeneratedKeys.next()) {
			blogModel.setBlogId(slotsGeneratedKeys.getInt(1));
		}
		myStmt.close();

		JSONObject json = new JSONObject();
		json.put("blog", blogModel);
		Gson gson = new Gson();
		String blogModelJson = gson.toJson(blogModel);
		JsonObject jsonObject = JsonParser.parseString(blogModelJson).getAsJsonObject();
		JSONObject jsonReturned = new JSONObject(jsonObject.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAsJsonPrimitive().getAsString())));
		messagingTemplate.convertAndSend("/topic/blogs/", json.toString());

		jsonResponse.put("message", "Blog Created Successfully");
		jsonResponse.put("blog", jsonReturned);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getBlogs/{pageNumber}/{recordsByPage}")
	public ResponseEntity<Object> getBlogs(@PathVariable("pageNumber") int pageNumber,
			@PathVariable("recordsByPage") int recordsByPage)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "select * from blogs_table Limit " + recordsByPage + " OFFSET "
				+ ((pageNumber - 1) * recordsByPage);

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		ResultSet myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs.next()) {
			JSONObject json = new JSONObject();
			json.put("blogId", myRs.getInt("blogId"));

			json.put("blogType", myRs.getString("blogType"));
			json.put("blogDate", myRs.getTimestamp("blogDate"));

			json.put("blogTitle", myRs.getString("blogTitle"));
			json.put("blogUrl", myRs.getString("blogUrl"));

			jsonArray.put(json);
		}
		jsonResponse.put("message", "All Blogs");
		jsonResponse.put("blogs", jsonArray);

		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getNotifications/{pageNumber}/{recordsByPage}/{userFk}")
	public ResponseEntity<Object> getNotifications(@PathVariable("pageNumber") int pageNumber,
			@PathVariable("recordsByPage") int recordsByPage, @PathVariable(name = "userFk") int userFk)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "select COUNT(*) AS total_count from notifications_table";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") * 1.0 / recordsByPage);
		}

		query = "select * from notifications_table nt join users_table ut on ut.userId=nt.userFromFk where nt.userToFk=? ";

		query += " limit " + recordsByPage + " OFFSET " + (pageNumber - 1) * recordsByPage;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);

		myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs.next()) {
			JSONObject json = new JSONObject();
			json.put("notificationText", myRs.getString("notificationText"));
			json.put("notificationType", myRs.getString("notificationType"));
			json.put("firstName", myRs.getString("firstName"));
			json.put("lastName", myRs.getString("lastName"));
			json.put("userEmail", myRs.getString("userEmail"));
			json.put("profilePicture", myRs.getString("profilePicture"));

			jsonArray.put(json);

		}
		jsonResponse.put("message", "Notifications");
		jsonResponse.put("notifications", jsonArray);
		jsonResponse.put("totalNumberOfPages", totalNumberOfPages);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@DeleteMapping("/deleteNotifications")
	public ResponseEntity<Object> deleteNotifications(@RequestBody NotificationsModel[] notificationModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "delete from notifications_table where notificationId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		for (int i = 0; i < notificationModel.length; i++) {
			myStmt.setInt(1, notificationModel[i].getNotificationId());
			myStmt.addBatch();
		}

		myStmt.executeBatch();

		myStmt.close();

		jsonResponse.put("message", "Notifications Deleted Successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@DeleteMapping("/deleteAllNotifications/{userFk}")
	public ResponseEntity<Object> deleteAllNotifications(@PathVariable("userFk") int userFk)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "delete from notifications_table where userToFk=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);

		myStmt.executeUpdate();

		myStmt.close();

		jsonResponse.put("message", "Notifications Deleted Successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PutMapping("/updateNotification")
	public ResponseEntity<Object> updateNotification(@RequestBody NotificationsModel[] notificationModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "update notifications_table set isOpen=? where notificationId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		for (int i = 0; i < notificationModel.length; i++) {
			myStmt.setBoolean(1, notificationModel[i].getIsOpen());
			myStmt.setInt(2, notificationModel[i].getNotificationId());
			myStmt.addBatch();
		}

		myStmt.executeBatch();

		myStmt.close();

		jsonResponse.put("message", "Notifications Updated Successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@PostMapping("/addNotification")
	public ResponseEntity<Object> addNotification(@RequestBody List<NotificationsModel> notificationModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "insert into notifications_table (userToFk, userFromFk,notificationText, notificationType, notificationUrl) values(?,?,?,?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		for (int i = 0; i < notificationModel.size(); i++) {
			myStmt.setInt(1, notificationModel.get(i).getUserToFk());
			myStmt.setInt(2, notificationModel.get(i).getUserToFk());
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
		myStmt.close();

		jsonResponse.put("message", "Notifications Added Successfully");
		jsonResponse.put("services", notificationModel);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	public void sendNotificationForFreeSlots(int oldSlotFk, int userFromFk)
			throws SQLException, IOException, NoSuchAlgorithmException {
		String query = "select * from reserved_slots_chosen_table"
				+ " rst join business_account_schedule_slots_table basst"
				+ " on basst.slotId = rst.slotFk join business_account_schedule_table bast"
				+ " on bast.scheduleId = basst.scheduleFk join business_account_table bat"
				+ " on bat.businessAccountId = bast.businessAccountFk join users_table ut"
				+ " on ut.userId = bat.userFk " + "join specialities_table st on "
				+ "st.specialityId = bat.specialityFk where rst.slotFk=?";
		PreparedStatement myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, oldSlotFk);
		ResultSet myRs = myStmt.executeQuery();
		JSONObject reservationJson = new JSONObject();
		JSONObject jsonSocket = new JSONObject();

		while (myRs.next()) {

			String notificationText = "The slot at " + myRs.getTimestamp("slotStartTime")
					+ " is availabe now. You can reserve it.";
			jsonSocket.put("notificationText", notificationText);
			jsonSocket.put("notificationType", "FREE_APPOINTMENT");
			jsonSocket.put("isOpen", false);
			jsonSocket.put("notificationUrl", "");
			jsonSocket.put("userFromProfile", myRs.getString("profilePicture"));

			reservationJson.put("slotFk", myRs.getInt("slotFk"));
			reservationJson.put("userFk", myRs.getInt("userFk"));
			reservationJson.put("businessAccountId", myRs.getInt("businessAccountId"));
			reservationJson.put("slotDate", myRs.getDate("slotDate"));
			reservationJson.put("slotStartTime", myRs.getTimestamp("slotStartTime"));
			reservationJson.put("slotEndTime", myRs.getTimestamp("slotEndTime"));
			reservationJson.put("biography", myRs.getString("biography"));
			reservationJson.put("clinicLocation", myRs.getString("clinicLocation"));
			reservationJson.put("clinicLocationLongitude", myRs.getFloat("clinicLocationLongitude"));
			reservationJson.put("clinicLocationLatitude", myRs.getFloat("clinicLocationLatitude"));
			reservationJson.put("firstName", myRs.getString("firstName"));
			reservationJson.put("lastName", myRs.getString("lastName"));
			reservationJson.put("userEmail", myRs.getString("userEmail"));
			reservationJson.put("profilePicture", myRs.getString("profilePicture"));
			reservationJson.put("specialityName", myRs.getString("specialityName"));
			reservationJson.put("specialityDescription", myRs.getString("specialityDescription"));
			List<NotificationsModel> list = new ArrayList<>();
			list.add(new NotificationsModel(false, 0, myRs.getInt("userFk"), userFromFk, notificationText,
					NotificationType.APPOINTMENT_STATUS, ""));
			addNotification(list);
		}
	}

	// passed
	@PostMapping("/contactUs")
	public ResponseEntity<Object> contactUs(@RequestBody ContactUsModel contactUsModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "insert into contact_us_table (firstName, lastName,subject, message) values(?,?,?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		myStmt.setString(1, contactUsModel.getFirstName());
		myStmt.setString(2, contactUsModel.getLastName());
		myStmt.setString(3, contactUsModel.getSubject());
		myStmt.setString(4, contactUsModel.getMessage());

		myStmt.executeUpdate();
		try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				int id = generatedKeys.getInt(1);
				contactUsModel.setContactId(id);
				Gson gson = new Gson();
				String contactUsModelJson = gson.toJson(contactUsModel);
				JsonObject jsonObject = JsonParser.parseString(contactUsModelJson).getAsJsonObject();
				JSONObject jsonReturned = new JSONObject(jsonObject.entrySet().stream().collect(
						Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAsJsonPrimitive().getAsString())));
				jsonResponse.put("message", "Message Sent successfully");
				jsonResponse.put("feedbacks", jsonReturned);
				jsonResponse.put("responseCode", 200);
				myStmt.close();

				return ResponseEntity.ok(jsonResponse.toString());
			}
		}
		return null;

	}

	// passed
	@GetMapping("/getSpecialities")
	public ResponseEntity<Object> getSpecialities() throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();

		String query = "select * from  specialities_table";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		ResultSet rs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (rs.next()) {
			JSONObject json = new JSONObject();
			json.put("specialityId", rs.getInt("specialityId"));
			json.put("specialityName", rs.getString("specialityName"));
			json.put("specialityDescription", rs.getString("specialityDescription"));
			jsonArray.put(json);
		}

		jsonResponse.put("message", "Specialities");
		jsonResponse.put("specialities", jsonArray);
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/postponeAppointment")
	public ResponseEntity<Object> postponeAppointment(@RequestBody PostponeAppointmentModel postponeAppointmentModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();

		String query = "update appointments_table set slotFk=?, isApproved=0 where appointmentId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, postponeAppointmentModel.getNewSlotFk());

		myStmt.setInt(2, postponeAppointmentModel.getAppointmentId());
		myStmt.executeUpdate();
		query = "update business_account_schedule_slots_table set isReserved=0 where slotId=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, postponeAppointmentModel.getOldSlotFk());
		myStmt.executeUpdate();

		query = "update business_account_schedule_slots_table set isReserved=1 where slotId=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, postponeAppointmentModel.getNewSlotFk());
		myStmt.executeUpdate();

		sendNotificationForFreeSlots(postponeAppointmentModel.getOldSlotFk(), postponeAppointmentModel.getUserFk());
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"select ut.firstName, ut.lastName, ut.profilePicture, basst2.slotStartTime as oldSlotStartTime, basst.slotStartTime as newSlotStartTime from business_account_schedule_slots_table basst cross join  business_account_schedule_slots_table basst2 cross join users_table ut where ut.userId=? and basst.slotId=? and basst2.slotId=?");
		myStmt.setInt(1, postponeAppointmentModel.getUserFk());
		myStmt.setInt(2, postponeAppointmentModel.getNewSlotFk());
		myStmt.setInt(3, postponeAppointmentModel.getOldSlotFk());

		ResultSet rs3 = myStmt.executeQuery();
		if (rs3.next()) {
			JSONObject json = new JSONObject();

			json.put("appointmentId", postponeAppointmentModel.getAppointmentId());
			json.put("key", "slotId");
			json.put("value", postponeAppointmentModel.getNewSlotFk());

			messagingTemplate.convertAndSend(
					"/topic/appointmentModifications/" + postponeAppointmentModel.getBusinessAccountUserId(),
					json.toString());
			messagingTemplate.convertAndSend("/topic/appointmentModifications/" + postponeAppointmentModel.getUserFk(),
					json.toString());

			json.clear();
			json.put("appointmentId", postponeAppointmentModel.getAppointmentId());
			json.put("key", "slotStartTime");
			json.put("value", rs3.getTimestamp("newSlotStartTime"));
			messagingTemplate.convertAndSend(
					"/topic/appointmentModifications/" + postponeAppointmentModel.getBusinessAccountUserId(),
					json.toString());
			messagingTemplate.convertAndSend("/topic/appointmentModifications/" + postponeAppointmentModel.getUserFk(),
					json.toString());

			String notificationText = rs3.getString("firstName") + " " + rs3.getString("lastName")
					+ " postponed the appointment from " + rs3.getTimestamp("oldSlotStartTime") + " to "
					+ rs3.getTimestamp("newSlotStartTime");
			JSONObject jsonNotificationReturned = new JSONObject();
			jsonNotificationReturned.put("userFromFk", postponeAppointmentModel.getUserFk());
			jsonNotificationReturned.put("userToFk", postponeAppointmentModel.getBusinessAccountUserId());
			jsonNotificationReturned.put("notificationText", notificationText);
			jsonNotificationReturned.put("notificationType", "APPOINTMENT_STATUS");
			jsonNotificationReturned.put("isOpen", false);
			jsonNotificationReturned.put("notificationUrl", "");
			jsonNotificationReturned.put("userFromProfile", rs3.getString("profilePicture"));
			List<NotificationsModel> list = new ArrayList<>();
			list.add(new NotificationsModel(false, 0, postponeAppointmentModel.getBusinessAccountUserId(),
					postponeAppointmentModel.getUserFk(), notificationText, NotificationType.APPOINTMENT_STATUS, ""));
			addNotification(list);
			messagingTemplate.convertAndSend(
					"/topic/notifications/" + postponeAppointmentModel.getBusinessAccountUserId(),
					jsonNotificationReturned.toString());
			JSONObject reservedSlot = new JSONObject();
			reservedSlot.put("reservedSlotId", postponeAppointmentModel.getOldSlotFk());
			reservedSlot.put("type", "DELETE");
			messagingTemplate.convertAndSend("/topic/reservedSlots/", reservedSlot.toString());
			reservedSlot.clear();
			reservedSlot.put("reservedSlotId", postponeAppointmentModel.getNewSlotFk());
			reservedSlot.put("type", "ADD");
			messagingTemplate.convertAndSend("/topic/reservedSlots/", reservedSlot.toString());

		}

		jsonResponse.put("message", "Appointment postponed successfully. Wait doctor approval");
		jsonResponse.put("responseCode", 200);
		rs3.close();
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PutMapping("/updateUser")
	public ResponseEntity<Object> updateUser(@RequestBody UserModel userModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();

		String query = "update users_table set userEmail=?, userLanguage=?, profilePicture=? where userId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setString(1, userModel.getUserEmail());
		myStmt.setString(2, userModel.getUserLanguage());
		myStmt.setString(3, userModel.getProfilePicture());
		myStmt.setInt(4, userModel.getUserId());
		myStmt.executeUpdate();
		jsonResponse.put("message", "User Updated successfully");
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/subscribe")
	public ResponseEntity<Object> subscribe(@RequestBody SubscriptionModel subscriptionModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();

		String query = "insert into subscriptions_table (userEmail) values(?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		myStmt.setString(1, subscriptionModel.getUserEmail());

		try {
			myStmt.executeUpdate();
			try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int id = generatedKeys.getInt(1);
					subscriptionModel.setSubscriptionId(id);
					Gson gson = new Gson();
					String subscriptionModelJson = gson.toJson(subscriptionModel);
					JsonObject jsonObject = JsonParser.parseString(subscriptionModelJson).getAsJsonObject();
					JSONObject jsonReturned = new JSONObject(jsonObject.entrySet().stream().collect(
							Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAsJsonPrimitive().getAsString())));
					jsonResponse.put("message", "Subscription Added successfully");
					jsonResponse.put("subscription", jsonReturned);
					jsonResponse.put("responseCode", 200);
					myStmt.close();

					return ResponseEntity.ok(jsonResponse.toString());
				}
			}
		} catch (Exception e) {
			jsonResponse.put("message", "User Subscription already exist");
			jsonResponse.put("responseCode", -1);
			myStmt.close();

			return ResponseEntity.ok(jsonResponse.toString());
		}
		return null;

	}

	// passed
	@DeleteMapping("/deleteSubscription/{userEmail}")
	public ResponseEntity<Object> deleteSubscription(@PathVariable("userEmail") String userEmail)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();

		String query = "select * from subscriptions_table where userEmail=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setString(1, userEmail);

		ResultSet myRs = myStmt.executeQuery();
		if (myRs.next()) {
			query = "delete from subscriptions_table where userEmail=?";
			myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
			myStmt.setString(1, userEmail);
			jsonResponse.put("message", "Subscription Deleted successfully");
			jsonResponse.put("responseCode", 200);
			myStmt.close();

			return ResponseEntity.ok(jsonResponse.toString());
		} else {
			jsonResponse.put("message", "This email is not subscribed");
			jsonResponse.put("responseCode", 200);
			myStmt.close();

			return ResponseEntity.ok(jsonResponse.toString());
		}

	}

	// passed
	@DeleteMapping("/deleteCommunityPostComment/{commentId}")
	public ResponseEntity<Object> deleteCommunityPostComment(@PathVariable("commentId") int commentId)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "delete from posts_comments_table  where commentId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		myStmt.setInt(1, commentId);

		myStmt.executeUpdate();

		JSONObject json = new JSONObject();
		json.put("type", "DELETE");
		json.put("commentId", commentId);

		messagingTemplate.convertAndSend("/topic/communityPostComment/", json.toString());

		jsonResponse.put("message", "Comment Deleted successfully");
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PutMapping("/updateCommunityPostComment")
	public ResponseEntity<Object> updateCommunityPostComment(
			@RequestBody CommunityPostCommentModel communityPostCommentModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "update posts_comments_table set commentDescription=? where commentId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		myStmt.setString(1, communityPostCommentModel.getCommentDescription());
		myStmt.setInt(2, communityPostCommentModel.getCommentId());

		myStmt.executeUpdate();

		JSONObject json = new JSONObject();
		json.put("type", "UPDATE");
		json.put("communityPostComment", communityPostCommentModel);

		messagingTemplate.convertAndSend("/topic/communityPostComment/", json.toString());
		Gson gson = new Gson();
		String communityPostCommentModelJson = gson.toJson(communityPostCommentModel);
		JsonObject jsonObject = JsonParser.parseString(communityPostCommentModelJson).getAsJsonObject();
		JSONObject jsonReturned = new JSONObject(jsonObject.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAsJsonPrimitive().getAsString())));
		jsonResponse.put("message", "Comment Updated successfully");
		jsonResponse.put("Comment", jsonReturned);
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getCommunityPostComment/{postId}/{pageNumber}/{recordsByPage}")
	public ResponseEntity<Object> getCommunityPostComment(@PathVariable("postId") int postId,
			@PathVariable("pageNumber") int pageNumber, @PathVariable("recordsByPage") int recordsByPage)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "select COUNT(*) AS total_count from posts_comments_table";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") * 1.0 / recordsByPage);
		}

		query = "select * from posts_comments_table pct join users_table ut on ut.userId=pct.userFk  where postFk=? order by commentId DESC limit "
				+ recordsByPage + " OFFSET " + (pageNumber - 1) * recordsByPage;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, postId);

		myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs.next()) {
			JSONObject json = new JSONObject();
			json.put("commentDescription", myRs.getString("commentDescription"));
			json.put("commentId", myRs.getInt("commentId"));
			json.put("userFk", myRs.getInt("userFk"));
			json.put("firstName", myRs.getString("firstName"));
			json.put("lastName", myRs.getString("lastName"));
			json.put("userEmail", myRs.getString("userEmail"));
			json.put("profilePicture", myRs.getString("profilePicture"));
			jsonArray.put(json);

		}
		jsonResponse.put("message", "Post Comments");
		jsonResponse.put("postComments", jsonArray);
		jsonResponse.put("totalNumberOfPages", totalNumberOfPages);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/addCommunityPostComment")
	public ResponseEntity<Object> addCommunityPostComment(
			@RequestBody CommunityPostCommentModel communityPostCommentModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "insert into posts_comments_table (userFk, postFk, commentDescription) values(?,?, ?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		myStmt.setInt(1, communityPostCommentModel.getUserFk());
		myStmt.setInt(2, communityPostCommentModel.getPostFk());
		myStmt.setString(3, communityPostCommentModel.getCommentDescription());

		myStmt.executeUpdate();
		try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				int id = generatedKeys.getInt(1);
				communityPostCommentModel.setCommentId(id);
				query = "select * from users_table where userId=?";
				myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
				myStmt.setInt(1, communityPostCommentModel.getUserFk());
				ResultSet rs = myStmt.executeQuery();
				if (rs.next()) {
					JSONObject json = new JSONObject();
					json.put("userFk", communityPostCommentModel.getUserFk());
					json.put("postId", communityPostCommentModel.getPostFk());
					json.put("commentDescription", communityPostCommentModel.getCommentDescription());
					json.put("commentId", id);
					json.put("firstName", rs.getString("firstName"));
					json.put("lastName", rs.getString("lastName"));
					json.put("userEmail", rs.getString("userEmail"));
					json.put("profilePicture", rs.getString("profilePicture"));

					messagingTemplate.convertAndSend("/topic/communityPostComment/", json.toString());

					jsonResponse.put("message", "Comment Added successfully");
					jsonResponse.put("post", json);
					jsonResponse.put("responseCode", 200);
					myStmt.close();

					return ResponseEntity.ok(jsonResponse.toString());
				}

			}
		}
		return null;

	}

	// passed
	@GetMapping("/getCommunityPosts/{pageNumber}/{recordsByPage}/{searchText}")
	public ResponseEntity<Object> getCommunityPosts(@PathVariable("pageNumber") int pageNumber,
			@PathVariable("recordsByPage") int recordsByPage,
			@PathVariable(name = "searchText", required = false) String searchText)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		searchText = searchText.equals("null") ? null : searchText;

		JSONObject jsonResponse = new JSONObject();
		String query = "select COUNT(*) AS total_count from community_posts_table";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") * 1.0 / recordsByPage);
		}

		query = "SELECT" + "    p.postId,    COUNT(c.commentId) AS commentCount,"
				+ "    u.firstName, u.lastName,u.userEmail, u.profilePicture, p.postService, p.postDescription FROM "
				+ "    community_posts_table p" + "    LEFT JOIN posts_comments_table c ON p.postid = c.postFk"
				+ "    JOIN users_table u ON p.userFk = u.userId";

		if (searchText != null) {
			query += " where cpt.postService LIKE '%" + searchText + "%' or cpt.postDescription LIKE '%" + searchText
					+ "%'";
		}
		query += " GROUP BY" + "    p.postid, u.firstName";
		query += " limit " + recordsByPage + " OFFSET " + (pageNumber - 1) * recordsByPage;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs.next()) {
			JSONObject json = new JSONObject();
			json.put("postService", myRs.getString("postService"));
			json.put("postDescription", myRs.getString("postDescription"));
			json.put("postId", myRs.getInt("postId"));
			json.put("commentCount", myRs.getInt("commentCount"));

			json.put("firstName", myRs.getString("firstName"));
			json.put("lastName", myRs.getString("lastName"));
			json.put("userEmail", myRs.getString("userEmail"));
			json.put("profilePicture", myRs.getString("profilePicture"));

			jsonArray.put(json);

		}
		jsonResponse.put("message", "All Community Posts");
		jsonResponse.put("communityPosts", jsonArray);
		jsonResponse.put("totalNumberOfPages", totalNumberOfPages);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/registerAppointment")
	public ResponseEntity<Object> registerAppointment(@RequestBody AppointmentModel appointmentModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "select isReserved from business_account_schedule_slots_table where slotId=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, appointmentModel.getSlotFk());
		ResultSet rs = myStmt.executeQuery();
		if (rs.next()) {
			boolean isReserved = rs.getBoolean("isReserved");
			if (!isReserved) {
				query = "update business_account_schedule_slots_table set isReserved = 1 where slotId=?";
				myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
				myStmt.setInt(1, appointmentModel.getSlotFk());
				myStmt.executeUpdate();

				query = "insert into appointments_table (slotFk, businessAccountFk, userFk, serviceFk) values(?, ?,?,?)";

				myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query,
						Statement.RETURN_GENERATED_KEYS);
				myStmt.setInt(1, appointmentModel.getSlotFk());
				myStmt.setInt(2, appointmentModel.getBusinessAccountFk());
				myStmt.setInt(3, appointmentModel.getUserFk());
				myStmt.setInt(4, appointmentModel.getServiceFk());

				myStmt.executeUpdate();

				try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						int id = generatedKeys.getInt(1);
						appointmentModel.setAppointmentId(id);

						JSONObject json = new JSONObject();
						JSONObject appointmentJson = new JSONObject();
						appointmentJson.put("appointmentId", appointmentModel.getAppointmentId());
						appointmentJson.put("slotFk", appointmentModel.getSlotFk());
						appointmentJson.put("businessAccountFk", appointmentModel.getBusinessAccountFk());
						appointmentJson.put("userFk", appointmentModel.getUserFk());
						appointmentJson.put("serviceFk", appointmentModel.getServiceFk());
						appointmentJson.put("appointmentActualStartTime",
								appointmentModel.getAppointmentActualStartTime());
						appointmentJson.put("appointmentActualEndTime", appointmentModel.getAppointmentActualEndTime());
						appointmentJson.put("appointmentStatus", appointmentModel.getAppointmentStatus());
						appointmentJson.put("isApproved", appointmentModel.getIsApproved());
						appointmentJson.put("isCancelled", appointmentModel.getIsCancelled());
						appointmentJson.put("cancelledBy", appointmentModel.getCancelledBy());

						json.put("type", "ADD");
						json.put("appointment", appointmentJson);

						messagingTemplate.convertAndSend(
								"/topic/addAppointment/" + appointmentModel.getBusinessAccountUserId(),
								appointmentJson.toString());
						messagingTemplate.convertAndSend("/topic/addAppointment/" + appointmentModel.getUserFk(),
								appointmentJson.toString());

						JSONObject reservedSlot = new JSONObject();
						reservedSlot.put("reservedSlotId", appointmentModel.getSlotFk());
						reservedSlot.put("type", "DELETE");

						query = "select * from users_table ut cross join business_account_schedule_slots_table basst where  userId=? and basst.slotId=?";
						myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
						myStmt.setInt(1, appointmentModel.getUserFk());
						myStmt.setInt(2, appointmentModel.getSlotFk());

						ResultSet rs2 = myStmt.executeQuery();
						if (rs2.next()) {
							JSONObject jsonSocket = new JSONObject();
							jsonSocket.put("userFromFk", appointmentModel.getUserFk());
							jsonSocket.put("userToFk", rs2.getInt("userId"));
							jsonSocket.put("notificationText",
									rs2.getString("firstName") + " " + rs2.getString("lastName")
											+ " reserved an appointment on " + rs2.getTimestamp("slotStartTime"));
							jsonSocket.put("notificationType", "RESERVED_APPOINTMENT");
							jsonSocket.put("isOpen", false);
							jsonSocket.put("notificationUrl", "");
							jsonSocket.put("userFromProfile", rs2.getString("profilePicture"));

							messagingTemplate.convertAndSend(
									"/topic/notifications/" + appointmentModel.getBusinessAccountUserId(),
									jsonSocket.toString());
							rs2.close();
						}

						messagingTemplate.convertAndSend("/topic/reservedSlots/", reservedSlot.toString());
						Gson gson = new Gson();

						jsonResponse.put("message", "Appointment Added successfully");
						jsonResponse.put("appointment", appointmentJson);
						jsonResponse.put("responseCode", 200);
						myStmt.close();

						return ResponseEntity.ok(jsonResponse.toString());
					}
				}
			} else {
				jsonResponse.put("message", "Slot Already taken");
				jsonResponse.put("responseCode", -1);
				myStmt.close();

				return ResponseEntity.ok(jsonResponse.toString());
			}
		}

		return null;

	}

	// passed
	@PutMapping("/updateAppointment")
	public ResponseEntity<Object> updateAppointment(

			@RequestBody AppointmentModel appointmentModel) throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String selectQuery = "SELECT basst.slotStartTime,ut.firstName as hpFirstName, ut.lastName as hpLastName ,ut.profilePicture, ut2.firstName as patientFirstName, ut2.lastName as patientLastName ,ut2.profilePicture as patientProfilePicture, isCancelled,appointmentStatus,appointmentActualStartTime, appointmentActualEndTime, basst.slotStartTime    FROM appointments_table apt cross join business_account_schedule_slots_table basst  cross join users_table ut cross join users_table ut2 WHERE appointmentId=? and ut.userId=? and basst.slotId=? and ut2.userId=?";
		PreparedStatement selectStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(selectQuery);
		selectStmt.setInt(1, appointmentModel.getAppointmentId());
		selectStmt.setInt(2, appointmentModel.getBusinessAccountUserId());
		selectStmt.setInt(3, appointmentModel.getSlotFk());
		selectStmt.setInt(4, appointmentModel.getUserFk());

		ResultSet rs = selectStmt.executeQuery();
		if (rs.next()) {

			boolean isCancelled = rs.getBoolean("isCancelled");
			boolean isCancelledNewValue = appointmentModel.getIsCancelled();

			String appointmentStatusNewValue = appointmentModel.getAppointmentStatus().toString();
			String appointmentStatusOldValue = rs.getString("appointmentStatus");

			if (appointmentModel.getAppointmentActualStartTime() != null) {
				Timestamp appointmentActualStartTime = rs.getTimestamp("appointmentActualStartTime");
				Timestamp appointmentActualStartTimeNewValue = Timestamp
						.valueOf(appointmentModel.getAppointmentActualStartTime());
				if (!Objects.equals(appointmentActualStartTime, appointmentActualStartTimeNewValue)) {
					JSONObject json = new JSONObject();
					json.put("message", "It's Your Turn. Doctor Is Waiting You");
					messagingTemplate.convertAndSend("/topic/clinicTurn/" + appointmentModel.getUserFk(),
							json.toString());
					JSONObject json2 = new JSONObject();
					json2.put("appointmentId", appointmentModel.getAppointmentId());
					json2.put("key", "appointmentActualStartTime");

					json2.put("value", appointmentActualStartTimeNewValue);

					messagingTemplate.convertAndSend("/topic/appointmentModifications/" + appointmentModel.getUserFk(),
							json2.toString());
					messagingTemplate.convertAndSend(
							"/topic/appointmentModifications/" + appointmentModel.getBusinessAccountUserId(),
							json2.toString());
				}
			}
			if (appointmentModel.getAppointmentActualEndTime() != null) {
				Timestamp appointmentActualEndTime = rs.getTimestamp("appointmentActualEndTime");
				Timestamp appointmentActualEndTimeNewValue = Timestamp
						.valueOf(appointmentModel.getAppointmentActualEndTime());
				if (!Objects.equals(appointmentActualEndTime, appointmentActualEndTimeNewValue)) {
					JSONObject json = new JSONObject();
					json.put("message", "Be ready for your appointment");
					messagingTemplate.convertAndSend("/topic/clinicTurn/" + appointmentModel.getUserFk(),
							json.toString());

					JSONObject json2 = new JSONObject();
					json2.put("appointmentId", appointmentModel.getAppointmentId());
					json2.put("key", "appointmentActualEndTime");

					json2.put("value", appointmentActualEndTimeNewValue);

					messagingTemplate.convertAndSend("/topic/appointmentModifications/" + appointmentModel.getUserFk(),
							json2.toString());
					messagingTemplate.convertAndSend(
							"/topic/appointmentModifications/" + appointmentModel.getBusinessAccountUserId(),
							json2.toString());
				}
			}

			if (!Objects.equals(appointmentStatusNewValue, appointmentStatusOldValue)) {
				JSONObject json = new JSONObject();
				json.put("appointmentId", appointmentModel.getAppointmentId());
				json.put("key", "appointmentStatus");

				json.put("value", appointmentStatusNewValue);
				String notificationText = "Doctor " + rs.getString("hpFirstName") + " " + rs.getString("hpLastName")
						+ " " + (appointmentModel.getAppointmentStatus().toString().equals("ACCEPTED") ? "accepted"
								: "rejected")
						+ " your appointment you reserved on " + rs.getTimestamp("slotStartTime");
				JSONObject jsonNotificationReturned = new JSONObject();
				jsonNotificationReturned.put("userFromFk", appointmentModel.getBusinessAccountUserId());
				jsonNotificationReturned.put("userToFk", appointmentModel.getUserFk());
				jsonNotificationReturned.put("notificationText", notificationText);
				jsonNotificationReturned.put("notificationType", "APPOINTMENT_STATUS");
				jsonNotificationReturned.put("isOpen", false);
				jsonNotificationReturned.put("notificationUrl", "");
				jsonNotificationReturned.put("userFromProfile", rs.getString("hpProfilePicture"));
				List<NotificationsModel> list = new ArrayList<>();
				list.add(new NotificationsModel(false, 0, appointmentModel.getUserFk(),
						appointmentModel.getBusinessAccountUserId(), notificationText,
						NotificationType.APPOINTMENT_STATUS, ""));
				addNotification(list);
				messagingTemplate.convertAndSend("/topic/appointmentModifications/" + appointmentModel.getUserFk(),
						json.toString());
				messagingTemplate.convertAndSend(
						"/topic/appointmentModifications/" + appointmentModel.getBusinessAccountUserId(),
						json.toString());
				messagingTemplate.convertAndSend("/topic/notifications/" + appointmentModel.getUserFk(),
						jsonNotificationReturned.toString());
				if (appointmentStatusNewValue.equals("REJECTED")) {
					JSONObject reservedSlot = new JSONObject();
					reservedSlot.put("reservedSlotId", appointmentModel.getSlotFk());
					reservedSlot.put("type", "DELETE");

					messagingTemplate.convertAndSend("/topic/reservedSlots/", reservedSlot.toString());
					myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
							"update business_account_schedule_slots_table set isReserved=false where slotId=?");
					myStmt.setInt(1, appointmentModel.getSlotFk());
					myStmt.executeUpdate();
					sendNotificationForFreeSlots(appointmentModel.getSlotFk(), appointmentModel.getUserFk());

				}

			}

			if (!Objects.equals(isCancelled, isCancelledNewValue)) {
				JSONObject reservedSlot = new JSONObject();
				reservedSlot.put("reservedSlotId", appointmentModel.getSlotFk());
				reservedSlot.put("type", "DELETE");

				messagingTemplate.convertAndSend("/topic/reservedSlots/", reservedSlot.toString());
				myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
						"update business_account_schedule_slots_table set isReserved=false where slotId=?");
				myStmt.setInt(1, appointmentModel.getSlotFk());
				myStmt.executeUpdate();
				sendNotificationForFreeSlots(appointmentModel.getSlotFk(), appointmentModel.getUserFk());

				JSONObject json2 = new JSONObject();
				json2.put("appointmentId", appointmentModel.getAppointmentId());
				json2.put("key", "isCancelled");

				json2.put("value", true);
				String notificationText = "Patient " + rs.getString("patientFirstName") + " "
						+ rs.getString("patientLastName") + " "

						+ "cancelled the appointment reserved on " + rs.getTimestamp("slotStartTime");
				JSONObject jsonNotificationReturned = new JSONObject();
				jsonNotificationReturned.put("userFromFk", appointmentModel.getUserFk());
				jsonNotificationReturned.put("userToFk", appointmentModel.getBusinessAccountFk());
				jsonNotificationReturned.put("notificationText", notificationText);
				jsonNotificationReturned.put("notificationType", "APPOINTMENT_STATUS");
				jsonNotificationReturned.put("isOpen", false);
				jsonNotificationReturned.put("notificationUrl", "");
				jsonNotificationReturned.put("userFromProfile", rs.getString("patientProfilePicture"));
				List<NotificationsModel> list = new ArrayList<>();
				list.add(new NotificationsModel(false, 0, appointmentModel.getUserFk(),
						appointmentModel.getBusinessAccountUserId(), notificationText,
						NotificationType.APPOINTMENT_STATUS, ""));
				addNotification(list);
				messagingTemplate.convertAndSend("/topic/notifications/" + appointmentModel.getBusinessAccountUserId(),
						jsonNotificationReturned.toString());
				if (appointmentModel.getCancelledBy() == UserRoles.PATIENT) {

					messagingTemplate.convertAndSend("/topic/appointmentModifications/" + appointmentModel.getUserFk(),
							json2.toString());
					messagingTemplate.convertAndSend(
							"/topic/appointmentModifications/" + appointmentModel.getBusinessAccountUserId(),
							json2.toString());

				} else {

					messagingTemplate.convertAndSend("/topic/appointmentModifications/" + appointmentModel.getUserFk(),
							json2.toString());
					messagingTemplate.convertAndSend(
							"/topic/appointmentModifications/" + appointmentModel.getBusinessAccountUserId(),
							json2.toString());

				}
			}

		}
		String query = "update appointments_table set appointmentActualStartTime=?, appointmentActualEndTime=?, appointmentStatus=?, isApproved=?, isCancelled=? "
				+ (appointmentModel.getCancelledBy() != null ? " ,cancelledBy=? " : " ") + " where appointmentId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		myStmt.setTimestamp(1, appointmentModel.getAppointmentActualStartTime() == null ? null
				: Timestamp.valueOf(appointmentModel.getAppointmentActualStartTime()));
		myStmt.setTimestamp(2, appointmentModel.getAppointmentActualEndTime() == null ? null
				: Timestamp.valueOf(appointmentModel.getAppointmentActualEndTime()));
		myStmt.setString(3, appointmentModel.getAppointmentStatus().toString());
		myStmt.setBoolean(4, appointmentModel.getIsApproved());
		myStmt.setBoolean(5, appointmentModel.getIsCancelled());
		if (appointmentModel.getCancelledBy() != null) {
			myStmt.setString(6, appointmentModel.getCancelledBy().toString());
			myStmt.setInt(7, appointmentModel.getAppointmentId());
		} else {
			myStmt.setInt(6, appointmentModel.getAppointmentId());

		}

		myStmt.executeUpdate();

		jsonResponse.put("message", "Appointment Updated successfully");
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/addCommunityPost")
	public ResponseEntity<Object> addCommunityPost(@RequestBody CommunityPostModel communityPostModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "insert into community_posts_table (userFk, postService, postDescription) values(?, ?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		myStmt.setInt(1, communityPostModel.getUserFk());
		myStmt.setString(2, communityPostModel.getPostService());
		myStmt.setString(3, communityPostModel.getPostDescription());

		myStmt.executeUpdate();
		try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				int id = generatedKeys.getInt(1);
				communityPostModel.setPostId(id);
				query = "select * from users_table where userId=" + communityPostModel.getUserFk();
				myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
				ResultSet rs = myStmt.executeQuery();
				if (rs.next()) {
					JSONObject jsonResponseData = new JSONObject();
					jsonResponseData.put("postId", id);
					jsonResponseData.put("userFk", communityPostModel.getUserFk());
					jsonResponseData.put("postService", communityPostModel.getPostService());
					jsonResponseData.put("postDescription", communityPostModel.getPostDescription());
					jsonResponseData.put("firstName", rs.getString("firstName"));
					jsonResponseData.put("lastName", rs.getString("lastName"));
					jsonResponseData.put("userEmail", rs.getString("userEmail"));
					jsonResponseData.put("profilePicture", rs.getString("profilePicture"));

					JSONObject json = new JSONObject();

					json.put("communityPost", jsonResponseData);

					messagingTemplate.convertAndSend("/topic/communityPosts/", json.toString());

					jsonResponse.put("message", "Post Added successfully");
					jsonResponse.put("communityPost", jsonResponseData);
					jsonResponse.put("responseCode", 200);
					myStmt.close();

					return ResponseEntity.ok(jsonResponse.toString());
				}

			}
		}
		return null;

	}

	// passed
	@PutMapping("/updateCommunityPost")
	public ResponseEntity<Object> updateCommunityPost(@RequestBody CommunityPostModel communityPostModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "update community_posts_table set postService=?, postDescription=? where postId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		myStmt.setString(1, communityPostModel.getPostService());
		myStmt.setString(2, communityPostModel.getPostDescription());
		myStmt.setInt(3, communityPostModel.getPostId());

		myStmt.executeUpdate();
		Gson gson = new Gson();
		String communityPostJson = gson.toJson(communityPostModel);
		JsonObject jsonObject = JsonParser.parseString(communityPostJson).getAsJsonObject();
		JSONObject jsonReturned = new JSONObject(jsonObject.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAsJsonPrimitive().getAsString())));

		JSONObject json = new JSONObject();
		json.put("type", "UPDATE");
		json.put("communityPost", jsonReturned);

		messagingTemplate.convertAndSend("/topic/communityPosts/", json.toString());

		jsonResponse.put("message", "Post Updated successfully");
		jsonResponse.put("post", jsonReturned);
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@DeleteMapping("/deleteCommunityPost/{postId}")
	public ResponseEntity<Object> deleteCommunityPost(@PathVariable("postId") int postId)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "delete from community_posts_table  where postId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		myStmt.setInt(1, postId);

		myStmt.executeUpdate();
		JSONObject json = new JSONObject();
		json.put("type", "DELETE");
		json.put("communityPostId", postId);

		messagingTemplate.convertAndSend("/topic/communityPosts/", json.toString());

		jsonResponse.put("message", "Post Deleted successfully");
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getFavorites/{userFk}/{pageNumber}/{recordsByPage}")
	public ResponseEntity<Object> getFavorites(@PathVariable("userFk") int userFk,
			@PathVariable("pageNumber") int pageNumber, @PathVariable("recordsByPage") int recordsByPage)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "select COUNT(*) AS total_count from favorites_table where userFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") * 1.0 / recordsByPage);
		}

		query = "select bat.biography, bat.clinicLocation, bat.clinicLocationLongitude, bat.clinicLocationLatitude, bat.businessAccountId, u.firstName, u.lastName, u.userEmail, u.profilePicture, st.specialityName, st.specialityDescription from favorites_table f join business_account_table bat on bat.businessAccountId = f.businessAccountFk join users_table u on u.userId = bat.userFk join specialities_table st on st.specialityId = bat.specialityFk where f.userFk =? limit "
				+ recordsByPage + " OFFSET " + (pageNumber - 1) * recordsByPage;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);

		myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs.next()) {
			JSONObject json = new JSONObject();
			json.put("biography", myRs.getString("biography"));
			json.put("clinicLocation", myRs.getString("clinicLocation"));
			json.put("clinicLocationLongitude", myRs.getFloat("clinicLocationLongitude"));
			json.put("clinicLocationLatitude", myRs.getFloat("clinicLocationLatitude"));
			json.put("businessAccountId", myRs.getInt("businessAccountId"));
			json.put("firstName", myRs.getString("firstName"));
			json.put("lastName", myRs.getString("lastName"));
			json.put("userEmail", myRs.getString("userEmail"));
			json.put("profilePicture", myRs.getString("profilePicture"));
			json.put("specialityName", myRs.getString("specialityName"));
			json.put("specialityDescription", myRs.getString("specialityDescription"));
			jsonArray.put(json);

		}
		jsonResponse.put("message", "All Favorites");
		jsonResponse.put("favorites", jsonArray);
		jsonResponse.put("totalNumberOfPages", totalNumberOfPages);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/addFavorite")
	public ResponseEntity<Object> addFavorite(@RequestBody FavoriteModel favoriteModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "insert into favorites_table (businessAccountFk, userFk) values(?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		myStmt.setInt(1, favoriteModel.getBusinessAccountFk());
		myStmt.setInt(2, favoriteModel.getUserFk());

		myStmt.executeUpdate();
		try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				int id = generatedKeys.getInt(1);
				favoriteModel.setFavoriteId(id);
				JSONObject json = new JSONObject();
				json.put("favoriteId", id);
				json.put("businessAccountFk", favoriteModel.getBusinessAccountFk());

				jsonResponse.put("message", "Favorite Added successfully");
				jsonResponse.put("favorite", json);
				jsonResponse.put("responseCode", 200);
				JSONObject socketJson = new JSONObject();
				socketJson.put("type", "ADD");
				socketJson.put("favoriteDoctorInfo", json);
				query = "select COALESCE(onFavorite,1) as onFavorite, ut2.firstName as patientFirstName, "
						+ "ut2.lastName patientLastName, ut2.profilePicture patientProfilePicture, ut.userId "
						+ "as businessAccountUserId from notifications_settings ns cross join users_table ut2 cross join users_table ut on ut.userId=ns.userFk join "
						+ "business_account_table bat on bat.userFk=ut.userId where bat.businessAccountId=? "
						+ "and ut2.userId=?";
				myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
				myStmt.setInt(1, favoriteModel.getBusinessAccountFk());
				myStmt.setInt(2, favoriteModel.getUserFk());

				ResultSet rs = myStmt.executeQuery();
				messagingTemplate.convertAndSend("/topic/favoriteDoctors/" + favoriteModel.getUserFk(),
						socketJson.toString());
				if (rs.next()) {
					if (rs.getBoolean("onFavorite") == true) {
						String notificationText = rs.getString("patientFirstName") + " "
								+ rs.getString("patientLastName") + " added you to their favorties";
						JSONObject jsonNotificationReturned = new JSONObject();
						jsonNotificationReturned.put("userFromFk", favoriteModel.getUserFk());
						jsonNotificationReturned.put("userToFk", rs.getInt("businessAccountUserId"));
						jsonNotificationReturned.put("notificationText", notificationText);
						jsonNotificationReturned.put("notificationType", "FAVORITE");
						jsonNotificationReturned.put("isOpen", false);
						jsonNotificationReturned.put("notificationUrl", "");
						jsonNotificationReturned.put("userFromProfile", rs.getString("patientProfilePicture"));
						List<NotificationsModel> list = new ArrayList<>();
						list.add(new NotificationsModel(false, 0, rs.getInt("businessAccountUserId"),
								favoriteModel.getUserFk(), notificationText, NotificationType.FAVORITE, ""));
						addNotification(list);
						messagingTemplate.convertAndSend("/topic/notifications/" + rs.getInt("businessAccountUserId"),
								jsonNotificationReturned.toString());
					}
				}
				myStmt.close();

				return ResponseEntity.ok(jsonResponse.toString());
			}
		}
		return null;

	}

	// passed
	@PostMapping("/addFeedback")
	public ResponseEntity<Object> addFeedback(@RequestBody FeedbackModel feedbackModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "insert into feedbacks_table (businessAccountFk, userFk,feedbackDescription) values(?,?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		myStmt.setInt(1, feedbackModel.getBusinessAccountFk());
		myStmt.setInt(2, feedbackModel.getUserFk());
		myStmt.setString(3, feedbackModel.getFeedbackDescription());

		myStmt.executeUpdate();
		try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				int id = generatedKeys.getInt(1);
				feedbackModel.setFeedbackId(id);
				JSONObject json = new JSONObject();
				json.put("type", "ADD");
				json.put("feedback", feedbackModel);
				messagingTemplate.convertAndSend("/topic/feedbacks/", json.toString());

				Gson gson = new Gson();
				String feedbackJson = gson.toJson(feedbackModel);
				JsonObject jsonObject = JsonParser.parseString(feedbackJson).getAsJsonObject();
				json = new JSONObject(jsonObject.entrySet().stream().collect(
						Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAsJsonPrimitive().getAsString())));

				jsonResponse.put("message", "Favorite Added successfully");
				jsonResponse.put("feedbacks", json);
				jsonResponse.put("responseCode", 200);
				myStmt.close();

				return ResponseEntity.ok(jsonResponse.toString());
			}
		}
		return null;

	}

	// passed
	@DeleteMapping("/deleteFeedback/{feedbackId}")
	public ResponseEntity<Object> deleteFeedback(@PathVariable("feedbackId") int feedbackId)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "delete from feedbacks_table where feedbackId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, feedbackId);

		myStmt.executeUpdate();

		JSONObject json = new JSONObject();
		json.put("type", "DELETE");
		json.put("feedbackId", feedbackId);

		messagingTemplate.convertAndSend("/topic/feedbacks/", json.toString());

		jsonResponse.put("message", "Feedback deleted successfully");
		jsonResponse.put("responseCode", 200);

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@DeleteMapping("/deleteFavorite/{favoriteId}")
	public ResponseEntity<Object> deleteFavorite(@PathVariable("favoriteId") int favoriteId)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		int userId = -1;
		String query = "select * from favorites_table where favoriteId=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, favoriteId);
		ResultSet rs = myStmt.executeQuery();
		if (rs.next()) {
			userId = rs.getInt("userFk");
		}
		query = "delete from favorites_table where favoriteId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, favoriteId);

		myStmt.executeUpdate();
		JSONObject json = new JSONObject();
		json.put("favoriteId", favoriteId);
		json.put("type", "DELETE");

		jsonResponse.put("message", "Favorite deleted successfully");
		jsonResponse.put("responseCode", 200);

		messagingTemplate.convertAndSend("/topic/favoriteDoctors/" + userId, json.toString());

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getAllFeedbacks/{pageNumber}/{recordsByPage}/{searchText}")
	public ResponseEntity<Object> getAllFeedbacks(@PathVariable("pageNumber") int pageNumber,
			@PathVariable("recordsByPage") int recordsByPage,
			@PathVariable(name = "searchText", required = false) String searchText)
			throws SQLException, IOException, NoSuchAlgorithmException {
		PreparedStatement myStmt;

		searchText = searchText.equals("null") ? null : searchText;

		JSONObject jsonResponse = new JSONObject();
		String query = "select COUNT(*) AS total_count from feedbacks_table";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") * 1.0 / recordsByPage);
		}

		query = "select f.feedbackDescription,f.feedbackId, bat.biography, bat.clinicLocation, bat.clinicLocationLongitude, bat.clinicLocationLatitude, bat.businessAccountId, u.firstName, u.lastName, u.userEmail, u.profilePicture, st.specialityName, st.specialityDescription from feedbacks_table f join business_account_table bat on bat.businessAccountId = f.businessAccountFk join users_table u on u.userId = bat.userFk join specialities_table st on st.specialityId = bat.specialityFk ";
		if (searchText != null) {
			query += " where f.feedbackDescription LIKE '%" + searchText + "%'";
		}
		query += "limit " + recordsByPage + " OFFSET " + (pageNumber - 1) * recordsByPage;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs.next()) {
			JSONObject json = new JSONObject();
			json.put("feedbackDescription", myRs.getString("feedbackDescription"));
			json.put("feedbackId", myRs.getInt("feedbackId"));

			json.put("biography", myRs.getString("biography"));
			json.put("clinicLocation", myRs.getString("clinicLocation"));
			json.put("clinicLocationLongitude", myRs.getFloat("clinicLocationLongitude"));
			json.put("clinicLocationLatitude", myRs.getFloat("clinicLocationLatitude"));
			json.put("businessAccountId", myRs.getInt("businessAccountId"));
			json.put("firstName", myRs.getString("firstName"));
			json.put("lastName", myRs.getString("lastName"));
			json.put("userEmail", myRs.getString("userEmail"));
			json.put("profilePicture", myRs.getString("profilePicture"));
			json.put("specialityName", myRs.getString("specialityName"));
			json.put("specialityDescription", myRs.getString("specialityDescription"));
			jsonArray.put(json);

		}
		jsonResponse.put("message", "All Feedbacks");
		jsonResponse.put("feedbacks", jsonArray);
		jsonResponse.put("totalNumberOfPages", totalNumberOfPages);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getUserFeedbacks/{userFk}/{pageNumber}/{recordsByPage}/{searchText}")
	public ResponseEntity<Object> getUserFeedbacks(@PathVariable("userFk") int userFk,
			@PathVariable("pageNumber") int pageNumber, @PathVariable("recordsByPage") int recordsByPage,
			@PathVariable(name = "searchText", required = false) String searchText)
			throws SQLException, IOException, NoSuchAlgorithmException {
		searchText = searchText.equals("null") ? null : searchText;
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "select COUNT(*) AS total_count from feedbacks_table where userFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") * 1.0 / recordsByPage);
		}

		query = "select bat.biography, bat.clinicLocation, bat.clinicLocationLongitude, bat.clinicLocationLatitude, bat.businessAccountId, u.firstName, u.lastName, u.userEmail, u.profilePicture, st.specialityName, st.specialityDescription from feedbacks_table f join business_account_table bat on bat.businessAccountId = f.businessAccountFk join users_table u on u.userId = bat.userFk join specialities_table st on st.specialityId = bat.specialityFk where f.userFk =? ";
		if (searchText != null) {
			query += " and f.feedbackDescription LIKE '%" + searchText + "%'";
		}
		query += "limit " + recordsByPage + " OFFSET " + (pageNumber - 1) * recordsByPage;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);

		myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs.next()) {
			JSONObject json = new JSONObject();
			json.put("biography", myRs.getString("biography"));
			json.put("clinicLocation", myRs.getString("clinicLocation"));
			json.put("clinicLocationLongitude", myRs.getFloat("clinicLocationLongitude"));
			json.put("clinicLocationLatitude", myRs.getFloat("clinicLocationLatitude"));
			json.put("businessAccountId", myRs.getInt("businessAccountId"));
			json.put("firstName", myRs.getString("firstName"));
			json.put("lastName", myRs.getString("lastName"));
			json.put("userEmail", myRs.getString("userEmail"));
			json.put("profilePicture", myRs.getString("profilePicture"));
			json.put("specialityName", myRs.getString("specialityName"));
			json.put("specialityDescription", myRs.getString("specialityDescription"));
			jsonArray.put(json);

		}
		jsonResponse.put("message", "All Feedbacks");
		jsonResponse.put("feedbacks", jsonArray);
		jsonResponse.put("totalNumberOfPages", totalNumberOfPages);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/addReservationSlot")
	public ResponseEntity<Object> addReservationSlot(@RequestBody ReservationSlot reservationSlot)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "insert into reserved_slots_chosen_table (slotFk, userFk) values(?,?)";
		PreparedStatement myStmt;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		myStmt.setInt(1, reservationSlot.getSlotFk());
		myStmt.setInt(2, reservationSlot.getUserFk());

		myStmt.executeUpdate();
		try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				int id = generatedKeys.getInt(1);
				reservationSlot.setReservationId(id);
				JSONObject jsonSocketResponse = new JSONObject();
				JSONObject json = new JSONObject();

				json.put("reservationId", id);
				query = "select * from reserved_slots_chosen_table"
						+ " rst join business_account_schedule_slots_table basst"
						+ " on basst.slotId = rst.slotFk join business_account_schedule_table bast"
						+ " on bast.scheduleId = basst.scheduleFk join business_account_table bat"
						+ " on bat.businessAccountId = bast.businessAccountFk join users_table ut"
						+ " on ut.userId = bat.userFk " + "join specialities_table st on "
						+ "st.specialityId = bat.specialityFk where rst.reservationId=?";
				myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query,
						Statement.RETURN_GENERATED_KEYS);
				myStmt.setInt(1, id);
				ResultSet myRs = myStmt.executeQuery();
				jsonSocketResponse.put("message", "ADD");

				if (myRs.next()) {
					json.put("slotFk", myRs.getInt("slotFk"));
					json.put("userFk", myRs.getInt("userFk"));
					json.put("businessAccountId", myRs.getInt("businessAccountId"));
					json.put("slotDate", myRs.getDate("slotDate"));
					json.put("slotStartTime", myRs.getTimestamp("slotStartTime"));
					json.put("slotEndTime", myRs.getTimestamp("slotEndTime"));
					json.put("biography", myRs.getString("biography"));
					json.put("clinicLocation", myRs.getString("clinicLocation"));
					json.put("clinicLocationLongitude", myRs.getFloat("clinicLocationLongitude"));
					json.put("clinicLocationLatitude", myRs.getFloat("clinicLocationLatitude"));
					json.put("firstName", myRs.getString("firstName"));
					json.put("lastName", myRs.getString("lastName"));
					json.put("userEmail", myRs.getString("userEmail"));
					json.put("profilePicture", myRs.getString("profilePicture"));
					json.put("specialityName", myRs.getString("specialityName"));
					json.put("specialityDescription", myRs.getString("specialityDescription"));
				}
				jsonSocketResponse.put("reservation", json);
				jsonResponse.put("message", "Reservation Added successfully");
				jsonResponse.put("reservation", json);
				jsonResponse.put("responseCode", 200);
				myStmt.close();
				messagingTemplate.convertAndSend("/topic/myReservedSlots/" + reservationSlot.getUserFk(),
						jsonSocketResponse.toString());

				return ResponseEntity.ok(jsonResponse.toString());
			}
		}
		return null;

	}

	// passed
	@DeleteMapping("/deleteReservationSlotByReservationId/{reservationId}/{userFk}")
	public ResponseEntity<Object> deleteReservationSlotByReservationId(@PathVariable("reservationId") int reservationId,
			@PathVariable("userFk") int userFk) throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "delete from reserved_slots_chosen_table where reservationId=?";
		PreparedStatement myStmt;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, reservationId);

		myStmt.executeUpdate();
		jsonResponse.put("message", "Reservation Deleted successfully");
		jsonResponse.put("responseCode", 200);
		JSONObject jsonSocketResponse = new JSONObject();
		jsonSocketResponse.put("message", "DELETE");
		jsonSocketResponse.put("reservationId", reservationId);

		messagingTemplate.convertAndSend("/topic/myReservedSlots/" + userFk, jsonSocketResponse.toString());
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@DeleteMapping("/deleteReservationSlotBySlot/{slotFk}/{userFk}")
	public ResponseEntity<Object> deleteReservationSlotBySlot(@PathVariable("slotFk") int slotFk,
			@PathVariable("userFk") int userFk) throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "delete from reserved_slots_chosen_table where userFk=? and slotFk=?";
		PreparedStatement myStmt;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);
		myStmt.setInt(2, slotFk);

		myStmt.executeUpdate();
		jsonResponse.put("message", "Reservation Deleted successfully");
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/isSlotReservedByUser/{userFk}/{slotFk}")
	public ResponseEntity<Object> isSlotReservedByUser(@PathVariable("userFk") int userFk,
			@PathVariable("slotFk") int slotFk) throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "select * from reserved_slots_chosen_table where userFk=? and slotFk=?";
		PreparedStatement myStmt;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);
		myStmt.setInt(2, slotFk);

		ResultSet myRs = myStmt.executeQuery();
		if (myRs.next()) {
			jsonResponse.put("message", true);

			jsonResponse.put("responseCode", 200);
			myStmt.close();

			return ResponseEntity.ok(jsonResponse.toString());

		}
		jsonResponse.put("message", false);

		jsonResponse.put("responseCode", -1);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getReservationSlots/{userFk}")
	public ResponseEntity<Object> getReservationSlots(@PathVariable("userFk") int userFk)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "select * from reserved_slots_chosen_table rst join business_account_schedule_slots_table basst on basst.slotId = rst.slotFk join business_account_schedule_table bast on bast.scheduleId = basst.scheduleFk join business_account_table bat on bat.businessAccountId = bast.businessAccountFk join users_table ut on ut.userId = bat.userFk join specialities_table st on st.specialityId = bat.specialityFk where rst.userFk=?";
		PreparedStatement myStmt;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);

		ResultSet myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs.next()) {
			JSONObject json = new JSONObject();
			json.put("reservationId", myRs.getInt("reservationId"));
			json.put("slotFk", myRs.getInt("slotFk"));
			json.put("userFk", myRs.getInt("userFk"));
			json.put("businessAccountId", myRs.getInt("businessAccountId"));
			json.put("slotDate", myRs.getDate("slotDate"));
			json.put("slotStartTime", myRs.getTimestamp("slotStartTime"));
			json.put("slotEndTime", myRs.getTimestamp("slotEndTime"));
			json.put("biography", myRs.getString("biography"));
			json.put("clinicLocation", myRs.getString("clinicLocation"));
			json.put("clinicLocationLongitude", myRs.getFloat("clinicLocationLongitude"));
			json.put("clinicLocationLatitude", myRs.getFloat("clinicLocationLatitude"));
			json.put("firstName", myRs.getString("firstName"));
			json.put("lastName", myRs.getString("lastName"));
			json.put("userEmail", myRs.getString("userEmail"));
			json.put("profilePicture", myRs.getString("profilePicture"));
			json.put("specialityName", myRs.getString("specialityName"));
			json.put("specialityDescription", myRs.getString("specialityDescription"));

			jsonArray.put(json);
		}
		jsonResponse.put("message", "All Reservations");
		jsonResponse.put("reservations", jsonArray);

		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getMedicalInformation/{userFk}")
	public ResponseEntity<Object> getMedicalInformation(@PathVariable("userFk") int userFk)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "select COALESCE(height, 0) as height, COALESCE(weight, 0) as weight, COALESCE(diseasesDescription, 0) as diseasesDescription, COALESCE(vaccinationDescription, 0) as vaccinationDescription from medical_information where userFk=?";
		PreparedStatement myStmt;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);

		ResultSet myRs = myStmt.executeQuery();
		JSONObject json = new JSONObject();
		if (myRs.next()) {
			json.put("height", myRs.getInt("height"));
			json.put("weight", myRs.getInt("weight"));

			json.put("diseasesDescription", myRs.getString("diseasesDescription"));
			json.put("vaccinationDescription", myRs.getString("vaccinationDescription"));

		}
		jsonResponse.put("message", "Medical Information");
		jsonResponse.put("medical_information", json);

		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PutMapping("/setMedicalInformation")
	public ResponseEntity<Object> setMedicalInformation(@RequestBody MedicalInformationModel medicalInformationModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "update medical_information set height=?, weight=?, diseasesDescription=?,vaccinationDescription=? where userFk=?";
		PreparedStatement myStmt;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, medicalInformationModel.getHeight());
		myStmt.setInt(2, medicalInformationModel.getWeight());
		myStmt.setString(3, medicalInformationModel.getDiseasesDescription());
		myStmt.setString(4, medicalInformationModel.getVaccinationDescription());
		myStmt.setInt(5, medicalInformationModel.getUserFk());

		myStmt.executeUpdate();

		jsonResponse.put("message", "Medical Information Updated Successully");

		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PutMapping("/forgetPassword")
	public ResponseEntity<Object> forgetPassword(@RequestBody UserModel user)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "update users_table set password = ? where userId=?";
		String hashedPassword = PasswordUtils.hashPassword(user.getPassword());
		PreparedStatement myStmt;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setString(1, hashedPassword);
		myStmt.setInt(2, user.getUserId());

		myStmt.executeUpdate();
		myStmt.close();

		jsonResponse.put("message", "User password is updated successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PutMapping("/updatePassword")
	public ResponseEntity<Object> updatePassword(@RequestBody UpdatePasswordModel updatePasswordModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String hashedCurrentPassword = PasswordUtils.hashPassword(updatePasswordModel.getCurrentPassword());
		String hashedNewPassword = PasswordUtils.hashPassword(updatePasswordModel.getNewPassword());
		String query = "";
		query = "select password from users_table where userId=?";
		PreparedStatement myStmt;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, updatePasswordModel.getUserFk());

		ResultSet myRs = myStmt.executeQuery();
		while (myRs.next()) {
			String oldPassword = myRs.getString("password");

			if (oldPassword.equals(hashedCurrentPassword)) {
				if (hashedNewPassword.equals(oldPassword)) {
					jsonResponse.put("message", "New password can't be as current password");
					jsonResponse.put("responseCode", 200);

					return ResponseEntity.ok(jsonResponse.toString());
				}
				query = "update users_table set password = ? where userId = ?";
				myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

				myStmt.setString(1, hashedNewPassword);
				myStmt.setInt(2, updatePasswordModel.getUserFk());

				myStmt.executeUpdate();
				myStmt.close();

				jsonResponse.put("message", "User password is updated successfully");
				jsonResponse.put("responseCode", 200);

				return ResponseEntity.ok(jsonResponse.toString());
			} else {
				myStmt.close();

				jsonResponse.put("message", "Current Password doesnot match");
				jsonResponse.put("responseCode", -1);
				return ResponseEntity.ok(jsonResponse.toString());
			}
		}
		jsonResponse.put("message", "User Not Found");
		jsonResponse.put("responseCode", -1);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	public boolean isUserEmailExist(UserModel user) throws SQLException, IOException {
		PreparedStatement myStmt;

		String query = "select * from users_table where userEmail=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setString(1, user.getUserEmail());

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

	public UserModel isUserExist(UserModel user) throws SQLException, IOException, NoSuchAlgorithmException {
		ObjectMapper mapper = new ObjectMapper();
		PreparedStatement myStmt;

		String query = "select * from users_table where userEmail=? and password=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setString(1, user.getUserEmail());
		String hashedPassword = PasswordUtils.hashPassword(user.getPassword());

		myStmt.setString(2, hashedPassword);

		ResultSet myRs = myStmt.executeQuery();
		while (myRs.next()) {
			JSONObject json = new JSONObject();
			json.put("userId", myRs.getInt("userId"));
			json.put("firstName", myRs.getString("firstName"));
			json.put("lastName", myRs.getString("lastName"));
			json.put("userEmail", myRs.getString("userEmail"));
			json.put("password", myRs.getString("password"));
			json.put("isVerified", myRs.getBoolean("isVerified"));
			json.put("numberOfLoginTrials", myRs.getInt("numberOfLoginTrials"));
			json.put("isApproved", myRs.getBoolean("isApproved"));
			json.put("isLocked", myRs.getBoolean("isLocked"));
			json.put("userRole", myRs.getString("userRole"));
			json.put("userLanguage", myRs.getString("userLanguage"));
			json.put("profilePicture", myRs.getString("profilePicture"));
			UserModel userReturned = mapper.readValue(json.toString(), UserModel.class);

			myRs.close();
			myStmt.close();

			return userReturned;
		}
		myRs.close();
		myStmt.close();
		return null;
	}

	// passed
	@PostMapping("/loginUser")
	public ResponseEntity<Object> loginUser(@RequestBody UserModel user)
			throws SQLException, IOException, NoSuchAlgorithmException {
		UserModel userReturned = isUserExist(user);
		JSONObject jsonResponse = new JSONObject();
		JSONObject userJson = new JSONObject();

		if (userReturned == null) {
			jsonResponse.put("message", "User Doesnot Exist");
			jsonResponse.put("responseCode", -1);
			return ResponseEntity.ok(jsonResponse.toString());
		} else {
			userJson.put("userId", userReturned.getUserId());
			userJson.put("firstName", userReturned.getFirstName());
			userJson.put("lastName", userReturned.getLastName());
			userJson.put("userEmail", userReturned.getUserEmail());
			userJson.put("userRole", userReturned.getUserRole());
			userJson.put("userLanguage", userReturned.getUserLanguage());
			userJson.put("profilePicture", userReturned.getProfilePicture());
		}
		if (userReturned.getIsLocked()) {
			jsonResponse.put("message", "User is locked");
			jsonResponse.put("responseCode", -1);

		} else {
			if (userReturned.getUserRole() != UserRoles.HEALTH_PROFESSIONAL) {
				if (userReturned.getIsVerified()) {
					jsonResponse.put("message", "Welcome back");
					jsonResponse.put("user", userJson);
					jsonResponse.put("responseCode", 200);

				} else {
					jsonResponse.put("message", "User is not verified");
					jsonResponse.put("responseCode", -1);

				}
			} else {
				if (userReturned.getIsVerified()) {
					if (userReturned.getIsApproved()) {
						jsonResponse.put("message", "Welcome back");
						jsonResponse.put("user", userJson);
						jsonResponse.put("responseCode", 200);

					} else {
						jsonResponse.put("message", "User is not approved");
						jsonResponse.put("responseCode", -1);
					}

				} else {
					jsonResponse.put("message", "User is not verified");
					jsonResponse.put("responseCode", -1);

				}
			}
		}

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/generateToken/{tokenType}")
	public ResponseEntity<Object> generateToken(@RequestBody UserModel user,
			@PathVariable("tokenType") String tokenType) throws SQLException, IOException {
		JSONObject jsonResponse = new JSONObject();
		PreparedStatement myStmt;

		if (user.getUserId() == -1) {
			if (isUserEmailExist(user)) {
				String query = "select * from users_table where userEmail=?";
				myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
				myStmt.setString(1, user.getUserEmail());

				ResultSet myRs = myStmt.executeQuery();
				if (myRs.next()) {
					user.setUserId(myRs.getInt("userId"));
				} else {
					jsonResponse.put("message", "User doesnot Exist");
					jsonResponse.put("responseCode", -1);
					return ResponseEntity.ok(jsonResponse.toString());
				}
			} else {
				jsonResponse.put("message", "User doesnot Exist");
				jsonResponse.put("responseCode", -1);
				return ResponseEntity.ok(jsonResponse.toString());
			}
		}
		String query = "insert into "
				+ (tokenType.equals(TokenType.REGISTRATION.toString()) ? "tokens_table" : "password_tokens_table")
				+ "(userFk, tokenValue, expiryDate) values ( ?, ?,?)";

		String randomToken = CommonFunctions.generateToken();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		cal.add(Calendar.MINUTE, 5);
		Timestamp newTimestamp = new Timestamp(cal.getTimeInMillis());

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, user.getUserId());
		myStmt.setString(2, randomToken);
		myStmt.setTimestamp(3, newTimestamp);

		myStmt.executeUpdate();
		myStmt.close();
		jsonResponse.put("userId", user.getUserId());

		jsonResponse.put("message", "Check your email");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PutMapping("/verifyUser")
	public ResponseEntity<Object> verifyUser(@RequestBody UserModel user) throws SQLException, IOException {
		JSONObject jsonResponse = new JSONObject();
		PreparedStatement myStmt;

		String query = "update users_table set isVerified = true where "
				+ (user.getUserId() == -1 ? "userEmail=?" : "userId=?");

		if (user.getUserId() == -1) {
			if (isUserEmailExist(user)) {
				String query2 = "select businessAccountId, isVerified, userId, userRole from users_table ut  left join business_account_table bat on bat.userFk=ut.userId where userEmail=?";
				myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query2);

				myStmt.setString(1, user.getUserEmail());
				ResultSet rs = myStmt.executeQuery();
				if (rs.next()) {
					boolean isVerified = rs.getBoolean("isVerified");
					if (isVerified) {
						String userRole = rs.getString("userRole");
						jsonResponse.put("userRole", userRole);
						jsonResponse.put("businessAccountId", rs.getInt("businessAccountId"));

						jsonResponse.put("message", "User Already Verified");
						jsonResponse.put("responseCode", -1);
						return ResponseEntity.ok(jsonResponse.toString());
					} else {
						user.setUserId(rs.getInt("userId"));
						myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
						myStmt.setString(1, user.getUserEmail());

					}
				}
			} else {
				jsonResponse.put("message", "User Email not found");
				jsonResponse.put("responseCode", -1);
				return ResponseEntity.ok(jsonResponse.toString());
			}
		} else {
			myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

			myStmt.setInt(1, user.getUserId());

		}
		myStmt.executeUpdate();
		String query2 = "select * from users_table where " + (user.getUserId() == -1 ? "userEmail=?" : "userId=?");
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query2);

		if (user.getUserId() == -1) {
			myStmt.setString(1, user.getUserEmail());
		} else {
			myStmt.setInt(1, user.getUserId());

		}
		ResultSet rs = myStmt.executeQuery();
		if (rs.next()) {
			String userRole = rs.getString("userRole");
			jsonResponse.put("userRole", userRole);
			if (userRole.toString().equals("HEALTH_PROFESSIONAL")) {
				String query3 = "select * from business_account_table where userFk=?";
				myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query3);
				myStmt.setInt(1, user.getUserId());
				ResultSet rs2 = myStmt.executeQuery();
				if (rs2.next()) {
					jsonResponse.put("businessAccountId", rs2.getInt("businessAccountId"));

				}
			}
		}

		myStmt.close();

		jsonResponse.put("message", "User is verified successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PutMapping("/approveUser/{userId}")
	public ResponseEntity<Object> approveUser(@PathVariable("userId") int userId) throws SQLException, IOException {
		JSONObject jsonResponse = new JSONObject();
		String query = "update users_table set isApproved = true where userId=?";
		PreparedStatement myStmt;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userId);
		myStmt.executeUpdate();
		myStmt.close();

		jsonResponse.put("message", "User is approved successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/checkToken/{tokenType}")
	public ResponseEntity<Object> checkToken(@RequestBody TokenModel token, @PathVariable("tokenType") String tokenType)
			throws SQLException, IOException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "select * from "
				+ (tokenType.equals(TokenType.REGISTRATION.toString()) ? "tokens_table" : "password_tokens_table")
				+ " where tokenValue=? and userFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setString(1, token.getTokenValue());
		myStmt.setInt(2, token.getUserFk());
		ResultSet myRs = myStmt.executeQuery();
		if (myRs.next()) {
			Timestamp date = myRs.getTimestamp("expiryDate");
			Timestamp myTimestamp = new Timestamp(System.currentTimeMillis());

			int comparisonResult = myTimestamp.compareTo(date);
			if (comparisonResult < 0) {
				jsonResponse.put("message", "Valid token");
				jsonResponse.put("responseCode", 200);
			} else {
				jsonResponse.put("message", "Token is expired");
				jsonResponse.put("responseCode", -1);
			}

		} else {
			jsonResponse.put("message", "Invalid token");
			jsonResponse.put("responseCode", -1);
		}

		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@PostMapping("/registerUser")
	public ResponseEntity<Object> registerUser(@RequestBody UserModel user)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		PreparedStatement myStmt;

		if (isUserEmailExist(user)) {
			jsonResponse.put("message", "User Already Found");
			jsonResponse.put("responseCode", -1);
			return ResponseEntity.ok(jsonResponse.toString());
		}
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"insert into users_table (firstName, lastName, userEmail, password, userRole, userLanguage, profilePicture, registrationDate) values ( ?,?, ?, ?, ?, ?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS);
		String hashedPassword = PasswordUtils.hashPassword(user.getPassword());
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		LocalDateTime ldt = LocalDateTime.now();

		myStmt.setString(1, user.getFirstName());
		myStmt.setString(2, user.getLastName());
		myStmt.setString(3, user.getUserEmail());
		myStmt.setString(4, hashedPassword);
		myStmt.setString(5, user.getUserRole().toString());
		myStmt.setString(6, user.getUserLanguage());
		myStmt.setString(7, user.getProfilePicture());
		myStmt.setTimestamp(8, timestamp);
		user.setRegistrationDate(ldt);
		myStmt.executeUpdate();

		try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				int id = generatedKeys.getInt(1);
				user.setUserId(id);

				generatedKeys.close();
				if (user.getUserRole() == UserRoles.HEALTH_PROFESSIONAL) {
					BusinessAccountController
							.registerBusinessAccount(new BusinessAccountModel(-1, id, 1, "", "", -1, -1));
				} else {
					String query = "insert into medical_information (userFk) values(?)";
					myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
					myStmt.setInt(1, id);
					myStmt.executeUpdate();

				}
				myStmt.close();

				return generateToken(user, TokenType.REGISTRATION.toString());

			} else {
				generatedKeys.close();
				myStmt.close();
				throw new SQLException("Insert failed, no ID obtained.");
			}
		}
	}

	// passed
	@PostMapping("/test")
	public ResponseEntity<Object> test(@RequestBody Test test)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		PreparedStatement myStmt;

		String query = "insert into test_dates (date) values(?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		myStmt.setTimestamp(1, Timestamp.valueOf(test.getDate()));
		myStmt.executeUpdate();

		jsonResponse.put("message", "added");
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	// passed
	@GetMapping("/getTest")
	public ResponseEntity<Object> getTest() throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		PreparedStatement myStmt;

		String query = "select *  from test_dates ";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		ResultSet rs = myStmt.executeQuery();
		while (rs.next()) {
			jsonResponse.put("message", rs.getTimestamp("date"));
			jsonResponse.put("responseCode", 200);
			myStmt.close();

			return ResponseEntity.ok(jsonResponse.toString());
		}

		return null;

	}

	// passed
	@GetMapping("/getUserStatistics/{userFk}")
	public ResponseEntity<Object> getUserStatistics(@PathVariable("userFk") int userFk)
			throws SQLException, IOException {
		PreparedStatement myStmt;

		JSONObject jsonResponse = new JSONObject();
		String query = "Select * from (select count(appointmentId) as total_appointments from appointments_table where userFk="
				+ userFk
				+ ") as p1 JOIN (SELECT COUNT(appointmentId) as total_done_appointments from appointments_table where appointmentActualStartTime is not null and appointmentActualEndTime is not null and userFk="
				+ userFk
				+ ") as p2 JOIN (SELECT COUNT(appointmentId) as total_rejected_appointments from appointments_table where appointmentStatus='REJECTED' and userFk="
				+ userFk
				+ ") as p3 JOIN (SELECT COUNT(appointmentId) as total_accepted_appointments from appointments_table where appointmentStatus='ACCEPTED' and userFk="
				+ userFk + ") as p4";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		ResultSet myRs = myStmt.executeQuery();
		while (myRs.next()) {
			JSONObject json = new JSONObject();

			json.put("totalAppointments", myRs.getInt("total_appointments"));
			json.put("totalDoneAppointments", myRs.getInt("total_done_appointments"));
			json.put("totalRejectedAppointments", myRs.getInt("total_rejected_appointments"));
			json.put("totalAcceptedAppointments", myRs.getInt("total_accepted_appointments"));

			jsonResponse.put("message", "Results Returned");
			jsonResponse.put("result", json);

			jsonResponse.put("responseCode", 200);
			return ResponseEntity.ok(jsonResponse.toString());

		}
		return null;

	}

	@GetMapping("/getUserAppointmentsStatistics/{userFk}/{fromDate}/{toDate}")
	public ResponseEntity<Object> getUserAppointmentsStatistics(@PathVariable("userFk") int userFk,
			@PathVariable("fromDate") Date fromDate, @PathVariable("toDate") Date toDate)
			throws SQLException, IOException {
		JSONObject jsonResponse = new JSONObject();
		PreparedStatement myStmt;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"SELECT sum(a.isCancelled = 1) as num_appointments_cancelled,sum(a.isCancelled = 0) as num_appointments_not_cancelled, sum(a.isApproved = 1) as num_appointments_approved ,sum(a.isApproved = 0) as num_appointments_not_approved, DATE(basst.slotDate) AS appointment_date, COUNT(a.appointmentId) AS num_appointments FROM appointments_table a INNER JOIN business_account_schedule_slots_table basst ON a.slotFk = basst.slotId where date(basst.slotDate) between '"
						+ fromDate + "' and '" + toDate + "' and a.userFk=? GROUP BY DATE(basst.slotDate)" + "");
		myStmt.setInt(1, userFk);

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
		jsonResponse.put("message", "Results Returned");
		jsonResponse.put("results", jsonArray);

		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

}