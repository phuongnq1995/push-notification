package org.phuongnq.demo.controller;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.phuongnq.demo.model.NotificationEntity;
import org.phuongnq.demo.notification.ListenerPostgres;
import org.phuongnq.demo.notification.NotifierPostgres;
import org.phuongnq.demo.service.NotificationServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.gson.Gson;

@Controller
public class HomeController {
	
	@Autowired
	private NotificationServiceInterface service;
	
	private SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
	
	public static SseEmitter emitter;
	public volatile static int notifiLength = 0;
	
	@SuppressWarnings("restriction")
	@PostConstruct
	public void init() throws SQLException {
		emitter = new SseEmitter();
		ListenerPostgres listener = new ListenerPostgres(service);
		NotifierPostgres notifer = new NotifierPostgres(service);
		listener.start();
		notifer.start();
	}
	
	@RequestMapping("/")
	public String home(Model model){
		List<NotificationEntity> notifications = (ArrayList<NotificationEntity>)service.findAll();
		model.addAttribute("notifications", new Gson().toJson(notifications));
		return "home";
	}
	
	 @RequestMapping("/sseTest")
    public ResponseEntity<SseEmitter> handleRequest (final HttpServletResponse response) {
        final SseEmitter emitter = new SseEmitter();
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 500; i++) {
	                try {
	                	response.setContentType("text/event-stream");
	                    emitter.send(df.format(new Date()) , MediaType.TEXT_PLAIN);
	                    Thread.sleep(2000);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                    emitter.completeWithError(e);
	                    return;
	                }
	                emitter.complete();
	            }
	            
			}
        });
        return new ResponseEntity(emitter, HttpStatus.OK);
    }
	
	
	@RequestMapping("/sse")
    public ResponseEntity<SseEmitter> handleRequest1(final HttpServletResponse response) throws SQLException {
		emitter = new SseEmitter();
		System.out.println("Begin-method");
		response.setContentType("text/event-stream");
		System.out.println("Return");
        return new ResponseEntity<SseEmitter>(emitter, HttpStatus.OK);
    }
	
}
