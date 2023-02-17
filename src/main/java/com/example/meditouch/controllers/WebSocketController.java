package com.example.meditouch.controllers;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
	private final SimpMessagingTemplate messagingTemplate;

	public WebSocketController() {
		this.messagingTemplate = null;
	}

	public WebSocketController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	public void sendMessage(String channelName, Object message) {

		messagingTemplate.convertAndSend(channelName, message);
	}
}
