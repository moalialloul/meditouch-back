package com.example.meditouch;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class TimestampDeserializer extends JsonDeserializer<Timestamp> {

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public Timestamp deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		String dateStr = parser.getText();
		try {
			Date date = dateFormat.parse(dateStr);
			return new Timestamp(date.getTime());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
