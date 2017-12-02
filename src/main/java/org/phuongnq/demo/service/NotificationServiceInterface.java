package org.phuongnq.demo.service;

import java.util.List;

import org.phuongnq.demo.model.NotificationEntity;

public interface NotificationServiceInterface {

	public List<NotificationEntity> findAll();
	
	public void update(NotificationEntity notify);

}
