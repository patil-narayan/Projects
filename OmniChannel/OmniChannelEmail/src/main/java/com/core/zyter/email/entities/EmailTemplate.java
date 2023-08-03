package com.core.zyter.email.entities;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EMAIL_TEMPLATE")
public class EmailTemplate {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	Long id;
	@Column(name = "NAME")
	String name;
	@Column(name = "SUBJECT")
	String subject;
	@Column(name = "FILE_PATH")
	@JsonIgnore
	String filePath;
	@Column(name = "MODE")
	String mode;
	@Column(name = "ACTIVE")
	boolean active;
	@Column(name = "CREATED_BY")
	String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_ON", nullable = false)
	Date createdOn;

	@PrePersist
	void onCreate() {
		createdOn = new Date();
	}

}
