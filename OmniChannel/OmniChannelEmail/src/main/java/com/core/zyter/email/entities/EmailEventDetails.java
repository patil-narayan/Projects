package com.core.zyter.email.entities;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EmailEventDetails implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	Long id;
	@Column(name = "DATE")
	Date date;
	@Column(name = "EMAIL")
	String email;
	@Column(name = "EVENT")
	String event;
	@Column(name = "IP")
	String ip;
	@Column(name = "SG_EVENT_ID")
	String sg_event_id;
	@Column(name = "MESSAGE_ID")
	String sg_message_id;
	@Column(name = "TIME_STAMP")
	Long timestamp;
	@Column(name = "USER_AGENT")
	String useragent;
}
