package org.phuongnq.demo.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="notification")
public class NotificationEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private long id;
	
	private String name;
	private long hight;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getHight() {
		return hight;
	}
	public void setHight(long hight) {
		this.hight = hight;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
