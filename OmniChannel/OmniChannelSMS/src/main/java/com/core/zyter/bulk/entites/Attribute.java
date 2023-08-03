package com.core.zyter.bulk.entites;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ATTRIBUTE")
public class Attribute {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "ID")
		Long id;
		@Column(name = "NAME")
		String name;
		@Column(name = "DESCRIPTION")
		String description;
		@Column(name = "RECORD_STATUS")
	    boolean recordstatus;
		@Temporal(TemporalType.TIMESTAMP)
		@Column(name = "CREATED_ON", nullable = false)
		Date createdOn;

		@PrePersist
		void onCreate() {
			createdOn = new Date();
		}
	
}
