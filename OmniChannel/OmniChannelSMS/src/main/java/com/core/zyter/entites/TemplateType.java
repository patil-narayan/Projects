package com.core.zyter.entites;

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
@Table(name = "TEMPLATE_TYPE")
public class TemplateType {


		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id")
	    Long id;
		@Column(name = "NAME")
	    String name;
	    @Column(name = "TYPE")
	    String type;
	    @Column(name = "MODE")
	    String mode;
	    @JsonIgnore
	    @Column(name = "FILE_PATH")
	    String filepath;
	    @Temporal(TemporalType.TIMESTAMP)
	    @Column(name = "CREATED_ON", nullable = false)
	    Date createdOn;

	    @PrePersist
	    void onCreate() {
	        createdOn = new Date();
	    }
	
}
