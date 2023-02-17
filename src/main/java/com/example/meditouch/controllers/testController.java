package com.example.meditouch.controllers;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meditouch.DatabaseConnection;

import models.User;

@RestController
public class testController {
	private SimpMessagingTemplate messagingTemplate;
	PreparedStatement myStmt;

	public testController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	@GetMapping("/hello")
	@MessageMapping("/hello")
	public void sayHello(User user) throws SQLException, IOException {
		JSONObject json = new JSONObject();
		json.put("name", user.getName());
		messagingTemplate.convertAndSend("/topic/greetings", json.toString());
		myStmt = DatabaseConnection.getInstance().getMyCon().prepareStatement("insert into user (name) values (?)");
		myStmt.setString(1, user.getName());
		int myRs = myStmt.executeUpdate();
		System.out.println(myRs);

	}
}
