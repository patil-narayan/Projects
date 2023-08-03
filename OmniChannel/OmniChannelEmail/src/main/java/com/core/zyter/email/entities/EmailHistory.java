package com.core.zyter.email.entities;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "EMAIL_HISTORY")
public class EmailHistory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@Column(name = "USER_ID")
	String userId;
	@Column(name = "MEMBER_ID")
	String memberId;
	@Column(name = "FROM_EMAIL")
	String fromEmail;
	@Column(name = "TO_EMAIL")
	String toEmail;
	@Column(name = "BODY")
	String body;
	@Column(name = "EMAIL_SUBJECT")
	String subject;
	@Column(name="MESSAGE_ID")
	String messageId;
	@Column(name = "CREATED_BY")
	String createdby;
	
	@Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy hh:mm a")
    @Column(name = "CREATED_ON", nullable = false)
    Date createdOn;

}
