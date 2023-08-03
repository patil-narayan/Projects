/*
 * @SmsConversation.java@
 * Created on 15Dec2022
 *
 * Copyright (c) 2022 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.entites;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "SMS_CONVERSATION")
public class SmsConversation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SMS_ID")
    String smsId;
    String memberId;
    String receiverFullName;
    String userId;
    String senderFullName;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy hh:mm a")
    Date createdOn;
    String messageType;
    @Column(name = "MESSAGE_STATUS")
    int messageStatus;
    String createdBy;
    @Transient
    List<DeliveryStatus> deliveryStatus;

}
