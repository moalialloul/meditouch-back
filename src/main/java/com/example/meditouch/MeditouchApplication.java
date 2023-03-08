package com.example.meditouch;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import Reminders.AppointmentsReminder;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class MeditouchApplication {

	public static SimpMessagingTemplate messagingTemplate;

	public MeditouchApplication(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	public static void main(String[] args) throws SQLException, IOException {
		SpringApplication.run(MeditouchApplication.class, args);
		DatabaseConnection.getInstance().getMyCon();

		AppointmentsReminder reminder = new AppointmentsReminder(12 * 60 * 60, messagingTemplate);
	}

}
