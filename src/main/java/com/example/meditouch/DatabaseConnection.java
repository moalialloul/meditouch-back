package com.example.meditouch;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
	private static DatabaseConnection instance = null;
	static Connection myCon = null;

	private DatabaseConnection() {
	}

	public static DatabaseConnection getInstance() throws SQLException, IOException {
		if (instance == null) {
			instance = new DatabaseConnection();
			Properties props = new Properties();
			String projectPath = System.getProperty("user.dir");
			String dbConfigpath = projectPath + "/dbconfig.properties";

			FileInputStream file = new FileInputStream(dbConfigpath);
			props.load(file);
			String url = props.getProperty("db.url");
			String username = props.getProperty("db.username");
			String password = props.getProperty("db.password");
			myCon = DriverManager.getConnection(url, username, password);
		}
		return instance;
	}

	public Connection getMyCon() {
		return myCon;
	}

	public static void setMyCon(Connection myCon) {
		DatabaseConnection.myCon = myCon;
	}

	public void doSomething() {
		System.out.println("Doing something...");
	}

}
