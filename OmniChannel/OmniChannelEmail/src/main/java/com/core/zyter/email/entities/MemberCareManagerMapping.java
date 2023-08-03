/*
 * @MemberCareManagerMapping.java@
 * Created on 21Dec2022
 *
 * Copyright (c) 2022 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.email.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MEMBER_CARE_MANAGER_MAPPING")
public class MemberCareManagerMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    String id;
    String careManager;
    String careManagerFullName;
    String careManagerPhoneNumber;
    String careManagerEmailId;
    String member;
    String memberPhoneNumber;
    String memberEmailId;
    String memberFullName;
    boolean active;
    Date createdOn;
    String createdBy;
    Date modifiedOn;
    String modifiedBy;

}
