package org.phuongnq.demo.repository;

import org.phuongnq.demo.model.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>{

}
