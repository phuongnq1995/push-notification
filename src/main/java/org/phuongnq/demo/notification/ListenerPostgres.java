package org.phuongnq.demo.notification;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.phuongnq.demo.controller.HomeController;
import org.phuongnq.demo.model.NotificationEntity;
import org.phuongnq.demo.service.NotificationServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.gson.Gson;

@Component
public class ListenerPostgres extends Thread{
	
	private NotificationServiceInterface service;
	private Connection conn;
	private org.postgresql.PGConnection pgconn;
	
	
	public ListenerPostgres(NotificationServiceInterface service) throws SQLException {
			ResourceBundle rb = ResourceBundle.getBundle("application");
			this.conn = DriverManager.getConnection(
					rb.getString("jdbc.url"),
					rb.getString("jdbc.username"),
					rb.getString("jdbc.password"));
			this.service = service;
			this.pgconn = (org.postgresql.PGConnection)conn;
			Statement stmt = conn.createStatement();
			stmt.execute("LISTEN mymessage");
			stmt.close();
	}
	
	public ListenerPostgres() throws SQLException {
		ResourceBundle rb = ResourceBundle.getBundle("application");
		this.conn = DriverManager.getConnection(
				rb.getString("jdbc.url"),
				rb.getString("jdbc.username"),
				rb.getString("jdbc.password"));
		this.pgconn = (org.postgresql.PGConnection)conn;
		Statement stmt = conn.createStatement();
		stmt.execute("LISTEN mymessage");
		stmt.close();
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public void run() {
		while (true) {
			try {
				org.postgresql.PGNotification notifications[] = pgconn.getNotifications();
				if (notifications != null) {
					HomeController.notifiLength += notifications.length;
					System.out.println("Listener notifiLength:"+HomeController.notifiLength);
				}
				// wait a while before checking again for new
				// notifications
				while (HomeController.notifiLength > 0) {
					if(HomeController.emitter != null){
						System.out.println("Send notifiLength:"+HomeController.notifiLength);
						for(int i = 0; i < HomeController.notifiLength; i++){
							try {
								List<NotificationEntity> notifies = (ArrayList<NotificationEntity>) service.findAll();
								HomeController.emitter.send(new Gson().toJson(notifies), MediaType.APPLICATION_JSON);
								HomeController.notifiLength--;
							} catch (Exception e) {
								HomeController.emitter.completeWithError(e);
								System.out.println("break");
				            	break;
							}
						}
						System.out.println("complete");
						HomeController.emitter.complete();
					}
					Thread.sleep(3000);
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}
	
	public NotificationServiceInterface getService() {
		return service;
	}

	public void setService(NotificationServiceInterface service) {
		this.service = service;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public org.postgresql.PGConnection getPgconn() {
		return pgconn;
	}

	public void setPgconn(org.postgresql.PGConnection pgconn) {
		this.pgconn = pgconn;
	}
	
}
