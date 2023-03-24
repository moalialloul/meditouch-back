package models;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Test {
	LocalDateTime date;

	public Test() {
		super();
	}

	public Test(LocalDateTime date) {
		super();
		this.date = date;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}
}
