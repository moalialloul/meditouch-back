package com.example.meditouch.controllers;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
	static SimpMessagingTemplate messagingTemplate;

	public WebSocketController() {
		WebSocketController.messagingTemplate = null;
	}

	public WebSocketController(SimpMessagingTemplate messagingTemplate) {
		WebSocketController.messagingTemplate = messagingTemplate;
	}

	public static void sendMessage(String channelName, Object message) {

		messagingTemplate.convertAndSend(channelName, message);
	}
}
