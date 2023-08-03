package com.core.zyter.sms.entities;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "SMS_HISTORY")
public class SMSHistory {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "SMS_ID")
	    String smsId;
	    String memberId;
	    String userId;
	    @Column(name = "MESSAGE")
	    String body;
	    String numSegments;
	    String direction;
	    @Column(name = "SENDER_NUMBER")
	    String fromNumber;
	    @Column(name = "RECEIVER_NUMBER")
	    String toNumber;
	    String price;
	    String errorMessage;
	    String accountSid;
	    String numMedia;
	    String errorCode;
	    String sid;
	    @Column(name = "CREATED_ON")
	    Date createdOn;
	    String messageType;
	    String createdBy;
	    String serviceType;
	    @OneToMany(cascade = {CascadeType.ALL})
	    @JoinColumn(name = "SMS_SID", referencedColumnName = "SID")
	    List<SMSDeliveryStatus> otpDeliveryStatus;

}
