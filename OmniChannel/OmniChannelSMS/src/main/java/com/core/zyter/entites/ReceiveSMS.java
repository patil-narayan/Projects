/*
 * @TwilioReceiveSMS.java@
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

import java.util.Date;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "RECEIVE_MESSAGE")
public class ReceiveSMS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    String id;
    String toCountry;
    String toState;
    String smsMessageSid;
    String numMedia;
    String toCity;
    String fromZip;
    String smsSid;
    String fromState;
    String smsStatus;
    String fromCity;
    @Column(name = "MESSAGE")
    String body;
    String FromCountry;
    @Column(name = "TO_NUMBER")
    String to;
    String messagingServiceSid;
    String toZip;
    String numSegments;
   // String referralNumMedia;
    @Column(name = "FROM_NUMBER")
    String from;
    String messageSid;
    String accountSid;
    String apiVersion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_ON", nullable = false)
    Date createdOn;

    @PrePersist
    void onCreate() {
        createdOn = new Date();
    }
}
