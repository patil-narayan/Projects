package com.core.zyter.sms.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SMS_DELIVERY_STATUS")
public class SMSDeliveryStatus {
	
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "ID")
	    String id;
	    String messageStatus;
	    String apiVersion;
	    @Column(name = "SMS_SID")
	    String smsSid;
	    String smsStatus;
	    @Column(name = "TO_NUMBER")
	    String to;
	    @Column(name = "FROM_NUMBER")
	    String from;
	    String messageSid;
	    String accountSid;

}
