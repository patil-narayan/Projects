/*
 * @TwilioSMSDeliveryStatus.java@
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

import com.netflix.discovery.provider.Serializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DELIVERY_STATUS")
public class DeliveryStatus {

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

/*    @ManyToOne
    @JoinColumn(referencedColumnName = "SID")
    SmsConversation smsConversation;*/

}
