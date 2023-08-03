package com.core.zyter.email.bulk.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "AUDIT_DETAILS")
public class AuditDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	Long ID;
	@Column(name = "AUDIT_ID")
	Long AuditId;
	@Column(name = "CREATED_ON", nullable = false)
	Date createdOn;

	@PrePersist
	void onCreate() {
		createdOn = new Date();
	}

}
