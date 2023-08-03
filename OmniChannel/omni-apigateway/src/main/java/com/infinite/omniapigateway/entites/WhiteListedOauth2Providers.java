package com.infinite.omniapigateway.entites;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhiteListedOauth2Providers {
	
	@Id
	private int id;
	private String tenantId;
	private String tenantIndex;
	private String issuerUrl;
	private String introspectionUrl;
	private String clientId;
	private String clientSecret;
	private boolean opaque;
}
