package com.core.zyter.entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "webhook_payload")
public class WebhookPayload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_sid")
    private String accountSid;

    private String sid;

    @Column(name = "parent_account_sid")
    private String parentAccountSid;

    private Date timestamp;

    private String level;

    @Column(name = "payload_type")
    private String payloadType;

    private String payload;

}
