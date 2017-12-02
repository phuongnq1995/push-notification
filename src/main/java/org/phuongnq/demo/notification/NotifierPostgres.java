package org.phuongnq.demo.notification;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

import org.phuongnq.demo.controller.HomeController;
import org.phuongnq.demo.model.NotificationEntity;
import org.phuongnq.demo.service.NotificationServiceInterface;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

public class NotifierPostgres extends Thread{

	public static Connection conn = null;
	private NotificationServiceInterface service;
	
	public NotifierPostgres(NotificationServiceInterface service) {
		this.service = service;
	}

	public static Connection getConection() throws SQLException {
		if(conn == null){
			ResourceBundle rb = ResourceBundle.getBundle("application");
			conn = DriverManager.getConnection(
					rb.getString("jdbc.url"),
					rb.getString("jdbc.username"),
					rb.getString("jdbc.password"));
		}
		return conn;
	}
	
	@Transactional
	public void run() {
		while (true) {
			List<NotificationEntity> notifies = (ArrayList<NotificationEntity>) this.service.findAll();
			for(NotificationEntity notify : notifies){
				notify.setHight(ThreadLocalRandom.current().nextInt(0, 50 + 1));
				this.service.update(notify);
			}
			sendNotify();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean sendNotify() {
		try {
			Statement stmt = getConection().createStatement();
			stmt.execute("NOTIFY mymessage");
			stmt.close();
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return false;
	}
	
}
