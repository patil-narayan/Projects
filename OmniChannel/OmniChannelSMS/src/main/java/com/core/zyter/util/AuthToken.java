package com.core.zyter.util;

import org.springframework.beans.factory.annotation.Autowired;

import com.core.zyter.securityconfig.TenantAuthenticationManagerResolver;

public class AuthToken {
	
	@Autowired
	TenantAuthenticationManagerResolver tenantAuthenticationManagerResolver;
	
	

}
