package com.core.zyter.email.bulk.entities;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "DISTRIBUTION_LIST")
public class DistributionList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	Long id;

	@Column(name = "NAME")
	String name;

	@JsonIgnore
	@Column(name = "FILE_PATH")
	String filePath;

	@Column(name = "TYPE")
	String type; 

	@Column(name = "RECORD_STATUS")
	boolean recordStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM'/'dd'/'yyyy hh:mm a")
	@Column(name = "CREATED_ON", nullable = false)
	Date createdOn;

	@PrePersist
	void onCreate() {
		createdOn = new Date();
	}
}
