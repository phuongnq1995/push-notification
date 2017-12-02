package org.phuongnq.demo.service;

import java.util.List;

import org.phuongnq.demo.model.NotificationEntity;
import org.phuongnq.demo.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationServiceImpl implements NotificationServiceInterface{
	
	@Autowired
	private NotificationRepository repository;

	@Transactional
	public List<NotificationEntity> findAll() {
		return repository.findAll();
	}
	
	
	public void update(NotificationEntity notify){
		repository.save(notify);
	}
}
