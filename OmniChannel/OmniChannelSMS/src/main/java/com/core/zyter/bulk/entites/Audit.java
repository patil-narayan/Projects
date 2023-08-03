package com.core.zyter.bulk.entites;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
@Table(name = "AUDIT")
public class Audit {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
	Long id;
	@Column(name = "CARE_MANAGER")
	String careManager; 
	@Column(name = "NOTE")
	String note;
	@Column(name = "TEMPLATE")
	String template;
	@Column(name = "DISTRIBUTION_LIST")
	String distributionList;
	@Column(name = "MODE")
	String mode;
	@Column(name = "STATUS")
	String status;
	@Column(name = "REASON")
	String reason;
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_ON", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM'/'dd'/'yyyy '-' hh:mm aa")
    Date createdOn;

    @PrePersist
    void onCreate() {
        createdOn = new Date();
    }
	@OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "ID")
    List<AuditDetails> details;

}
