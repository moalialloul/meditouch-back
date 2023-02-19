package com.example.meditouch;

import java.security.SecureRandom;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

public class CommonFunctions {
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final int LENGTH = 6;

	public static ResponseEntity<Object> createResponse(int responseCode, Object body) {
		return ResponseEntity.status(responseCode).body(body);

	}

	public static String generateToken() {
		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder(LENGTH);

		for (int i = 0; i < LENGTH; i++) {
			int randomIndex = random.nextInt(CHARACTERS.length());
			char randomChar = CHARACTERS.charAt(randomIndex);
			sb.append(randomChar);
		}

		return sb.toString();
	}

	public static double distance(double lat1, double lon1, double lat2, double lon2) {
		// Earth's radius in kilometers
		final double R = 6371.01;

		// Convert latitude and longitude to radians
		double lat1_rad = Math.toRadians(lat1);
		double lon1_rad = Math.toRadians(lon1);
		double lat2_rad = Math.toRadians(lat2);
		double lon2_rad = Math.toRadians(lon2);

		// Calculate the difference between the two latitudes and longitudes
		double dlat = lat2_rad - lat1_rad;
		double dlon = lon2_rad - lon1_rad;

		// Calculate the Haversine formula
		double a = Math.pow(Math.sin(dlat / 2), 2)
				+ Math.cos(lat1_rad) * Math.cos(lat2_rad) * Math.pow(Math.sin(dlon / 2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c;

		return distance;
	}

}
