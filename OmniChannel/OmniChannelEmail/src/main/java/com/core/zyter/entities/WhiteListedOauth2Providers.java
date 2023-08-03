/*
 * @WhiteListedOauth2Providers.java@
 * Created on 10Mar2023
 *
 * Copyright (c) 2023 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.entities;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="oauth2_providers_whitelist")
@Data
public class WhiteListedOauth2Providers {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;
	/* id | tenanat_id | issuer_url| introspection_url | client_id | client_secret | opaque |*/
	private String tenantId;
	private String tenantIndex;
	private String issuerUrl;
	private String introspectionUrl;
	private String clientId;
	private String clientSecret;
	private boolean opaque;
	
}