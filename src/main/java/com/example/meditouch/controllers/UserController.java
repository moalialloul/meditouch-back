package com.example.meditouch.controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

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
import com.example.meditouch.PasswordUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import Enums.TokenType;
import Enums.UserRoles;
import models.AppointmentModel;
import models.BlogModel;
import models.BusinessAccountModel;
import models.BusinessAccountScheduleSlotModel;
import models.CommunityPostCommentModel;
import models.CommunityPostModel;
import models.ContactUsModel;
import models.FavoriteModel;
import models.FeedbackModel;
import models.NotificationsModel;
import models.PostponeAppointmentModel;
import models.ReservationSlot;
import models.SubscriptionModel;
import models.TokenModel;
import models.UpdatePasswordModel;
import models.UserModel;

@RestController
public class UserController {
	PreparedStatement myStmt;

	public UserController(SimpMessagingTemplate messagingTemplate) {
//		this.messagingTemplate = messagingTemplate;
	}

	@PostMapping("/updateBlog")
	public ResponseEntity<Object> updateBlog(@RequestBody BlogModel blogModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "update blogs_table set blogType=?, blogDate=?, blogTitle=?, blogUrl=? wjere blogId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setString(1, blogModel.getBlogType());
		myStmt.setTimestamp(2, blogModel.getBlogDate());
		myStmt.setString(3, blogModel.getBlogTitle());
		myStmt.setString(4, blogModel.getBlogUrl());
		myStmt.setInt(5, blogModel.getBlogId());

		myStmt.executeQuery();

		myStmt.close();

		jsonResponse.put("message", "Blog Updated Successfully");
		jsonResponse.put("blog", blogModel);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@DeleteMapping("/deleteBlogs")
	public ResponseEntity<Object> deleteBlogs(@RequestBody List<BlogModel> blogModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "insert into blogs_table (blogType, blogDate, blogTitle, blogUrl) values (?,?,?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		for (int i = 0; i < blogModel.size(); i++) {
			myStmt.setInt(1, blogModel.get(i).getBlogId());
			myStmt.addBatch();
		}

		myStmt.executeBatch();

		myStmt.close();

		jsonResponse.put("message", "Blogs Deleted Successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@PostMapping("/addBlog")
	public ResponseEntity<Object> addBlog(@RequestBody BlogModel blogModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "insert into blogs_table (blogType, blogDate, blogTitle, blogUrl) values (?,?,?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setString(1, blogModel.getBlogType());
		myStmt.setTimestamp(2, blogModel.getBlogDate());
		myStmt.setString(3, blogModel.getBlogTitle());
		myStmt.setString(4, blogModel.getBlogUrl());

		myStmt.executeQuery();
		ResultSet slotsGeneratedKeys = myStmt.getGeneratedKeys();
		while (slotsGeneratedKeys.next()) {
			blogModel.setBlogId(slotsGeneratedKeys.getInt(1));
		}
		myStmt.close();

		jsonResponse.put("message", "Blog Created Successfully");
		jsonResponse.put("blog", blogModel);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@GetMapping("/getBlogs/{pageNumber}/{recordsByPage}")
	public ResponseEntity<Object> getBlogs(@PathVariable("pageNumber") int pageNumber,
			@PathVariable("recordsByPage") int recordsByPage)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "select * from blogs_table Limit " + recordsByPage + " OFFSET "
				+ ((pageNumber - 1) * recordsByPage);

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		ResultSet myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs.next()) {
			JSONObject json = new JSONObject();
			json.put("blogId", myRs.getInt("blogId"));

			json.put("businessAccountId", myRs.getString("blogType"));
			json.put("slotDate", myRs.getTimestamp("blogDate"));

			json.put("biography", myRs.getString("blogTitle"));
			json.put("clinicLocation", myRs.getString("blogUrl"));

			jsonArray.put(json);
		}
		jsonResponse.put("message", "All Blogs");
		jsonResponse.put("blogs", jsonArray);

		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	@GetMapping("/getNotifications/{pageNumber}/{recordsByPage}/{userFk}")
	public ResponseEntity<Object> getNotifications(@PathVariable("pageNumber") int pageNumber,
			@PathVariable("recordsByPage") int recordsByPage, @PathVariable(name = "userFk") int userFk)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "select COUNT(*) AS total_count from notifications_table";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") / recordsByPage);
		}

		query = "select * from notifications_table nt join users_table ut on ut.userId=cpt.userFromFk where nt.userToFk=? ";

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

	@PostMapping("/deleteNotification")
	public ResponseEntity<Object> deleteNotification(@RequestBody List<NotificationsModel> notificationModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "delete from notifications_table where notificationId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		for (int i = 0; i < notificationModel.size(); i++) {
			myStmt.setInt(1, notificationModel.get(i).getNotificationId());
			myStmt.addBatch();
		}

		myStmt.executeBatch();

		myStmt.close();

		jsonResponse.put("message", "Notifications Updated Successfully");
		jsonResponse.put("notifications", notificationModel);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@DeleteMapping("/deleteAllNotifications/{userFk}")
	public ResponseEntity<Object> deleteAllNotifications(@PathVariable("userFk") int userFk)
			throws SQLException, IOException, NoSuchAlgorithmException {
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

	@PostMapping("/updateNotification")
	public ResponseEntity<Object> updateNotification(@RequestBody List<NotificationsModel> notificationModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "update notifications_table set isOpen=? where notificationId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		for (int i = 0; i < notificationModel.size(); i++) {
			myStmt.setBoolean(1, notificationModel.get(i).getIsOpen());
			myStmt.setInt(2, notificationModel.get(i).getNotificationId());
			myStmt.addBatch();
		}

		myStmt.executeBatch();

		myStmt.close();

		jsonResponse.put("message", "Notifications Updated Successfully");
		jsonResponse.put("notifications", notificationModel);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@PostMapping("/addNotification")
	public ResponseEntity<Object> addNotification(@RequestBody List<NotificationsModel> notificationModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "insert into notifications_table (userToFk, userFromFk,notificationText, notificationType, appointmentFk, commentFk, ,promoCodeFk, referralFk, favoriteFk) values(?,?,?,?,?,?,?,?,?)";

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

	@PostMapping("/contactUs")
	public ResponseEntity<Object> contactUs(@RequestBody ContactUsModel contactUsModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
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
				jsonResponse.put("message", "Message Sent successfully");
				jsonResponse.put("feedbacks", contactUsModel);
				jsonResponse.put("responseCode", 200);
				myStmt.close();

				return ResponseEntity.ok(jsonResponse.toString());
			}
		}
		return null;

	}

	@PostMapping("/postponeAppointment")
	public ResponseEntity<Object> postponeAppointment(@RequestBody PostponeAppointmentModel postponeAppointmentModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();

		String query = "update appointments_table set slotFk=?, isApproved=0 where appointmentId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		myStmt.setInt(1, postponeAppointmentModel.getAppointmentId());
		myStmt.executeUpdate();

		jsonResponse.put("message", "Appointment postponed successfully. Wait doctor approvalF");
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	@PostMapping("/updateUser")
	public ResponseEntity<Object> updateUser(@RequestBody UserModel userModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();

		String query = "update users_table set userEmail=?, userLanguage=?, profilePicture=? where userId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setString(1, userModel.getUserEmail());
		myStmt.setString(2, userModel.getUserLanguage());
		myStmt.setString(3, userModel.getProfilePicture());
		myStmt.setInt(4, userModel.getUserId());
		myStmt.executeUpdate();
		jsonResponse.put("message", "User Updated successfully");
		jsonResponse.put("subscription", userModel);
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	@PostMapping("/subscribe")
	public ResponseEntity<Object> subscribe(@RequestBody SubscriptionModel subscriptionModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
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
					jsonResponse.put("message", "Subscription Added successfully");
					jsonResponse.put("subscription", subscriptionModel);
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

	@DeleteMapping("/subscribe/{userEmail}")
	public ResponseEntity<Object> subscribe(@PathVariable("userEmail") String userEmail)
			throws SQLException, IOException, NoSuchAlgorithmException {
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

	@DeleteMapping("/deleteCommunityPostComment")
	public ResponseEntity<Object> deleteCommunityPostComment(@PathVariable("commentId") int commentId)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "delete from posts_comments_table  where commentId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		myStmt.setInt(1, commentId);

		myStmt.executeUpdate();
		jsonResponse.put("message", "Comment Deleted successfully");
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	@PostMapping("/updateCommunityPostComment")
	public ResponseEntity<Object> updateCommunityPostComment(
			@RequestBody CommunityPostCommentModel communityPostCommentModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "update posts_comments_table set commentDescription=? where commentId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		myStmt.setString(1, communityPostCommentModel.getCommentDescription());
		myStmt.setInt(2, communityPostCommentModel.getCommentId());

		myStmt.executeUpdate();
		jsonResponse.put("message", "Comment Updated successfully");
		jsonResponse.put("Comment", communityPostCommentModel);
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	@GetMapping("/getCommunityPostComment/{postId}/{pageNumber}/{recordsByPage}")
	public ResponseEntity<Object> getCommunityPostComment(@PathVariable("postId") int postId,
			@PathVariable("pageNumber") int pageNumber, @PathVariable("recordsByPage") int recordsByPage)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "select COUNT(*) AS total_count from posts_comments_table";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") / recordsByPage);
		}

		query = "select * from posts_comments_table pct join users_table ut on ut.userId=pct.userFk  where postId=? limit "
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

	@PostMapping("/addCommunityPostComment")
	public ResponseEntity<Object> addCommunityPostComment(
			@RequestBody CommunityPostCommentModel communityPostCommentModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "insert into posts_comments_table (userFk, postFk, commentDescription) values(?,?, ?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		myStmt.setInt(1, communityPostCommentModel.getUserFk());
		myStmt.setInt(2, communityPostCommentModel.getPostFk());
		myStmt.setString(3, communityPostCommentModel.getCommentDescription());

		myStmt.executeUpdate();
		try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				int id = generatedKeys.getInt(1);
				communityPostCommentModel.setCommentId(id);
				jsonResponse.put("message", "Comment Added successfully");
				jsonResponse.put("post", communityPostCommentModel);
				jsonResponse.put("responseCode", 200);
				myStmt.close();

				return ResponseEntity.ok(jsonResponse.toString());
			}
		}
		return null;

	}

	@GetMapping("/getCommunityPosts/{pageNumber}/{recordsByPage}/{searchText}")
	public ResponseEntity<Object> getCommunityPosts(@PathVariable("pageNumber") int pageNumber,
			@PathVariable("recordsByPage") int recordsByPage,
			@PathVariable(name = "searchText", required = false) String searchText)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "select COUNT(*) AS total_count from community_posts_table";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") / recordsByPage);
		}

		query = "select * from community_posts_table cpt join users_table ut on ut.userId=cpt.userFk ";
		if (searchText != null) {
			query += " where cpt.postService LIKE '%" + searchText + "%' or cpt.postDescription LIKE '%" + searchText
					+ "%'";
		}
		query += " limit " + recordsByPage + " OFFSET " + (pageNumber - 1) * recordsByPage;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		myRs = myStmt.executeQuery();
		JSONArray jsonArray = new JSONArray();
		while (myRs.next()) {
			JSONObject json = new JSONObject();
			json.put("postService", myRs.getString("postService"));
			json.put("postDescription", myRs.getString("postDescription"));

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

	@PostMapping("/registerAppointment")
	public ResponseEntity<Object> registerAppointment(@RequestBody AppointmentModel appointmentModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "insert into appointments_table (slotFk, businessAccountFk, userFk, serviceFk, appointmentActualStartTime, ,appointmentActualEndTime) values(?,?, ?,?,?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		myStmt.setInt(1, appointmentModel.getSlotFk());
		myStmt.setInt(2, appointmentModel.getBusinessAccountFk());
		myStmt.setInt(3, appointmentModel.getUserFk());
		myStmt.setInt(4, appointmentModel.getServiceFk());
		myStmt.setTimestamp(5, appointmentModel.getAppointmentActualStartTime());
		myStmt.setTimestamp(6, appointmentModel.getAppointmentActualEndTime());

		myStmt.executeUpdate();
		try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				int id = generatedKeys.getInt(1);
				appointmentModel.setAppointmentId(id);
				jsonResponse.put("message", "Appointment Added successfully");
				jsonResponse.put("appointment", appointmentModel);
				jsonResponse.put("responseCode", 200);
				myStmt.close();

				return ResponseEntity.ok(jsonResponse.toString());
			}
		}
		return null;

	}

	@PostMapping("/updateAppointment")
	public ResponseEntity<Object> updateAppointment(

			@RequestBody AppointmentModel appointmentModel) throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "update appointments_table set appointmentActualStartTime=?, appointmentActualEndTime=?, appointmentStatus=?, isApproved=?, isCancelled=?, cancelledBy=? where appointmentId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		myStmt.setTimestamp(1, appointmentModel.getAppointmentActualStartTime());
		myStmt.setTimestamp(2, appointmentModel.getAppointmentActualEndTime());
		myStmt.setString(3, appointmentModel.getAppointmentStatus().toString());
		myStmt.setBoolean(4, appointmentModel.getIsApproved());
		myStmt.setBoolean(5, appointmentModel.getIsCancelled());
		myStmt.setString(6, appointmentModel.getCancelledBy().toString());
		myStmt.setInt(7, appointmentModel.getAppointmentId());

		myStmt.executeUpdate();
		ResultSet generatedKeys = myStmt.getGeneratedKeys();

		while (generatedKeys.next()) {
			String affectedRowName = generatedKeys.getString(1);
			// doctor has all appointments all start of appointments will be disabled except
			// first
			// press start on first appointment
			// two btns will appear Alertnextuser and EndAppointmentBtn
			// EndAppointmentBtn will be disabled until he alerts next user and thus send
			// message to next user (already has his id from appointments array)
			// after he alerts Alertnextuser will disappear and EndAppointmentBtn will be
			// enabled and thus update appointmentActualEndTime
			// after EndAppointmentBtn pressed status of appointment will be ended (replace
			// btns with ended)
			// and start of next appointment will be enabled ...
			if (affectedRowName.equals("appointmentActualStartTime")) {
				JSONObject json = new JSONObject();
				json.put("message", "It's Your Turn. Doctor Is Waiting You");
				WebSocketController.sendMessage("/topic/appointments/" + appointmentModel.getUserFk(), json.toString());

			}

		}
		jsonResponse.put("message", "Appointment Updated successfully");
		jsonResponse.put("appointment", appointmentModel);
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	@PostMapping("/addCommunityPost")
	public ResponseEntity<Object> addCommunityPost(@RequestBody CommunityPostModel communityPostModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "insert into community_posts_table (userFk, postService, postDescription) values(?, ?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		myStmt.setInt(2, communityPostModel.getUserFk());
		myStmt.setString(3, communityPostModel.getPostService());
		myStmt.setString(4, communityPostModel.getPostDescription());

		myStmt.executeUpdate();
		try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				int id = generatedKeys.getInt(1);
				communityPostModel.setPostId(id);
				jsonResponse.put("message", "Post Added successfully");
				jsonResponse.put("post", communityPostModel);
				jsonResponse.put("responseCode", 200);
				myStmt.close();

				return ResponseEntity.ok(jsonResponse.toString());
			}
		}
		return null;

	}

	@PostMapping("/updateCommunityPost")
	public ResponseEntity<Object> updateCommunityPost(@RequestBody CommunityPostModel communityPostModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "update community_posts_table set postService=?, postDescription=? where postId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		myStmt.setString(1, communityPostModel.getPostService());
		myStmt.setString(2, communityPostModel.getPostDescription());
		myStmt.setInt(3, communityPostModel.getPostId());

		myStmt.executeUpdate();
		jsonResponse.put("message", "Post Updated successfully");
		jsonResponse.put("post", communityPostModel);
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	@DeleteMapping("/deleteCommunityPost/{postId}")
	public ResponseEntity<Object> deleteCommunityPost(@PathVariable("postId") int postId)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "delete from community_posts_table  where postId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

		myStmt.setInt(1, postId);

		myStmt.executeUpdate();
		jsonResponse.put("message", "Post Deleted successfully");
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	@GetMapping("/getFavorites/{userFk}/{pageNumber}/{recordsByPage}")
	public ResponseEntity<Object> getFavorites(@PathVariable("userFk") int userFk,
			@PathVariable("pageNumber") int pageNumber, @PathVariable("recordsByPage") int recordsByPage)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "select COUNT(*) AS total_count from favorites_table where userFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") / recordsByPage);
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

	@PostMapping("/addFavorite")
	public ResponseEntity<Object> addFavorite(@RequestBody FavoriteModel favoriteModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
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
				jsonResponse.put("message", "Favorite Added successfully");
				jsonResponse.put("favorite", favoriteModel);
				jsonResponse.put("responseCode", 200);
				myStmt.close();

				return ResponseEntity.ok(jsonResponse.toString());
			}
		}
		return null;

	}

	@PostMapping("/addFeedback/")
	public ResponseEntity<Object> addFeedback(@RequestBody FeedbackModel feedbackModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "insert into feedback_table (businessAccountFk, userFk,feedbackDescription) values(?,?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		myStmt.setInt(1, feedbackModel.getBusinessAccountFk());
		myStmt.setInt(2, feedbackModel.getUserFk());
		myStmt.setString(3, feedbackModel.getFeedbackDescription());

		myStmt.executeUpdate();
		try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				int id = generatedKeys.getInt(1);
				feedbackModel.setFeedbackId(id);
				jsonResponse.put("message", "Favorite Added successfully");
				jsonResponse.put("feedbacks", feedbackModel);
				jsonResponse.put("responseCode", 200);
				myStmt.close();

				return ResponseEntity.ok(jsonResponse.toString());
			}
		}
		return null;

	}

	@DeleteMapping("/deleteFeedback/{feedbackId}")
	public ResponseEntity<Object> deleteFeedback(@PathVariable("feedbackId") int feedbackId)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "delete from feedback_table where favoriteId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, feedbackId);

		myStmt.executeUpdate();
		jsonResponse.put("message", "Feedback deleted successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@DeleteMapping("/deleteFavorite/{favoriteId}")
	public ResponseEntity<Object> deleteFavorite(@PathVariable("favoriteId") int favoriteId)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "delete from favorites_table where favoriteId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, favoriteId);

		myStmt.executeUpdate();
		jsonResponse.put("message", "Favorite deleted successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@GetMapping("/getAllFeedbacks/{pageNumber}/{recordsByPage}/{searchText}")
	public ResponseEntity<Object> getAllFeedbacks(@PathVariable("pageNumber") int pageNumber,
			@PathVariable("recordsByPage") int recordsByPage,
			@PathVariable(name = "searchText", required = false) String searchText)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "select COUNT(*) AS total_count from feedbacks_table";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") / recordsByPage);
		}

		query = "select bat.biography, bat.clinicLocation, bat.clinicLocationLongitude, bat.clinicLocationLatitude, bat.businessAccountId, u.firstName, u.lastName, u.userEmail, u.profilePicture, st.specialityName, st.specialityDescription from feedbacks_table f join business_account_table bat on bat.businessAccountId = f.businessAccountFk join users_table u on u.userId = bat.userFk join specialities_table st on st.specialityId = bat.specialityFk ";
		if (searchText != null) {
			query += " where f.feedbackDescription LIKE '%" + searchText + "%'";
		}
		query += "limit " + recordsByPage + " OFFSET " + (pageNumber - 1) * recordsByPage;

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);

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

	@GetMapping("/getUserFeedbacks/{userFk}/{pageNumber}/{recordsByPage}/{searchText}")
	public ResponseEntity<Object> getUserFeedbacks(@PathVariable("userFk") int userFk,
			@PathVariable("pageNumber") int pageNumber, @PathVariable("recordsByPage") int recordsByPage,
			@PathVariable(name = "searchText", required = false) String searchText)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "select COUNT(*) AS total_count from feedbacks_table where userFk=?";
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userFk);
		ResultSet myRs = myStmt.executeQuery();
		int totalNumberOfPages = 0;

		if (myRs.next()) {
			totalNumberOfPages = (int) Math.ceil(myRs.getInt("total_count") / recordsByPage);
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

	@PostMapping("/addReservationSlot")
	public ResponseEntity<Object> addReservationSlot(@RequestBody ReservationSlot reservationSlot)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "insert into reserved_slots_chosen_table (slotFk, userFk) values(?,?)";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		myStmt.setInt(1, reservationSlot.getSlotFk());
		myStmt.setInt(2, reservationSlot.getUserFk());

		myStmt.executeUpdate();
		try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				int id = generatedKeys.getInt(1);
				reservationSlot.setReservationId(id);
				jsonResponse.put("message", "Reservation Added successfully");
				jsonResponse.put("reservation", reservationSlot);
				jsonResponse.put("responseCode", 200);
				myStmt.close();

				return ResponseEntity.ok(jsonResponse.toString());
			}
		}
		return null;

	}

	@DeleteMapping("/deleteReservationSlot/{reservationId}")
	public ResponseEntity<Object> deleteReservationSlot(@PathVariable("reservationId") int reservationId)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "delete from reserved_slots_chosen_table where reservationId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, reservationId);

		myStmt.executeUpdate();
		jsonResponse.put("message", "Reservation Deleted successfully");
		jsonResponse.put("responseCode", 200);
		myStmt.close();

		return ResponseEntity.ok(jsonResponse.toString());

	}

	@GetMapping("/getReservationSlots/{userFk}")
	public ResponseEntity<Object> getReservationSlots(@PathVariable("userFk") int userFk)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "select * from reserved_slots_chosen_table rst join business_account_schedule_slots_table basst on basst.slotId = rst.slotFk join business_account_schedule_table bast on bast.scheduleId = basst.scheduleFk join business_account_table bat on bat.businessAccountId = bast.businessAccountFk join users_table ut on ut.userId = bat.userFk join specialities_table st on st.specialityId = bat.specialityFk where rst.userFk=?";

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

	@PostMapping("/forgetPassword")
	public ResponseEntity<Object> forgetPassword(@RequestBody UserModel user)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String query = "update users_table set password = ? where userId=?";
		String hashedPassword = PasswordUtils.hashPassword(user.getPassword());

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setString(1, hashedPassword);
		myStmt.setInt(2, user.getUserId());

		myStmt.executeUpdate();
		myStmt.close();

		jsonResponse.put("message", "User password is updated successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@PostMapping("/updatePassword")
	public ResponseEntity<Object> updatePassword(@RequestBody UpdatePasswordModel updatePasswordModel)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();
		String hashedCurrentPassword = PasswordUtils.hashPassword(updatePasswordModel.getCurrentPassword());
		String hashedNewPassword = PasswordUtils.hashPassword(updatePasswordModel.getNewPassword());
		String query = "";
		query = "select password from users_table where userId=?";

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
			json.put("registrationDate", myRs.getDate("registrationDate"));
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

	@PostMapping("/loginUser")
	public ResponseEntity<Object> loginUser(@RequestBody UserModel user)
			throws SQLException, IOException, NoSuchAlgorithmException {
		UserModel userReturned = isUserExist(user);
		JSONObject jsonResponse = new JSONObject();

		if (userReturned == null) {
			jsonResponse.put("message", "User Doesnot Exist");
			jsonResponse.put("responseCode", -1);
			return ResponseEntity.ok(jsonResponse.toString());
		}
		if (userReturned.isLocked()) {
			jsonResponse.put("message", "User is locked");
			jsonResponse.put("responseCode", -1);

		} else {
			if (userReturned.getUserRole() != UserRoles.HEALTH_PROFESSIONAL) {
				if (userReturned.isVerified()) {
					jsonResponse.put("message", "Welcome back");
					jsonResponse.put("user", userReturned);
					jsonResponse.put("responseCode", 200);

				} else {
					jsonResponse.put("message", "User is not verified");
					jsonResponse.put("responseCode", -1);

				}
			} else {
				if (userReturned.isVerified()) {
					if (userReturned.isApproved()) {
						jsonResponse.put("message", "Welcome back");
						jsonResponse.put("user", userReturned);
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

	@PostMapping("/generateToken/{tokenType}")
	public ResponseEntity<Object> generateToken(@RequestBody UserModel user,
			@PathVariable("tokenType") String tokenType) throws SQLException, IOException {
		JSONObject jsonResponse = new JSONObject();
		String query = "insert into "
				+ (tokenType.equals(TokenType.REGISTRATION.toString()) ? "tokens_table" : "password_tokens_table")
				+ "(userFk, tokenValue) values ( ?, ?)";

		String randomToken = CommonFunctions.generateToken();
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, user.getUserId());
		myStmt.setString(2, randomToken);
		myStmt.executeUpdate();
		myStmt.close();

		jsonResponse.put("message", "Check your email");
		jsonResponse.put("user", user);
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@PostMapping("/verifyUser/{userId}")
	public ResponseEntity<Object> verifyUser(@PathVariable("userId") int userId) throws SQLException, IOException {
		JSONObject jsonResponse = new JSONObject();
		String query = "update users_table set isVerified = true where userId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userId);
		myStmt.executeUpdate();
		myStmt.close();

		jsonResponse.put("message", "User is verified successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@PostMapping("/approveUser/{userId}")
	public ResponseEntity<Object> approveUser(@PathVariable("userId") int userId) throws SQLException, IOException {
		JSONObject jsonResponse = new JSONObject();
		String query = "update users_table set isApproved = true where userId=?";

		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(query);
		myStmt.setInt(1, userId);
		myStmt.executeUpdate();
		myStmt.close();

		jsonResponse.put("message", "User is approved successfully");
		jsonResponse.put("responseCode", 200);
		return ResponseEntity.ok(jsonResponse.toString());

	}

	@PostMapping("/checkToken/{tokenType}")
	public ResponseEntity<Object> checkToken(@RequestBody TokenModel token, @PathVariable("tokenType") String tokenType)
			throws SQLException, IOException {
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

	@PostMapping("/registerUser")
	public ResponseEntity<Object> registerUser(@RequestBody UserModel user)
			throws SQLException, IOException, NoSuchAlgorithmException {
		JSONObject jsonResponse = new JSONObject();

		if (isUserEmailExist(user)) {
			jsonResponse.put("message", "User Already Found");
			jsonResponse.put("responseCode", -1);
			return ResponseEntity.ok(jsonResponse.toString());
		}
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement(
				"insert into users_table (firstName, lastName, userEmail, password, userRole, userLanguage, profilePicture) values ( ?, ?, ?, ?, ?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS);
		String hashedPassword = PasswordUtils.hashPassword(user.getPassword());

		myStmt.setString(1, user.getFirstName());
		myStmt.setString(2, user.getLastName());
		myStmt.setString(3, user.getUserEmail());
		myStmt.setString(4, hashedPassword);
		myStmt.setString(5, user.getUserRole().toString());
		myStmt.setString(6, user.getUserLanguage());
		myStmt.setString(7, user.getProfilePicture());

		myStmt.executeUpdate();

		try (ResultSet generatedKeys = myStmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				int id = generatedKeys.getInt(1);
				user.setUserId(id);

				generatedKeys.close();
				myStmt.close();
				if (user.getUserRole() == UserRoles.HEALTH_PROFESSIONAL) {
					BusinessAccountController
							.registerBusinessAccount(new BusinessAccountModel(-1, id, 1, "", "", -1, -1));
				}
				return generateToken(user, TokenType.REGISTRATION.toString());

			} else {
				generatedKeys.close();
				myStmt.close();
				throw new SQLException("Insert failed, no ID obtained.");
			}
		}
	}
}
