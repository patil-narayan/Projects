/*
 * @GatewaySecurityConfig.java@
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

package com.infinite.omniapigateway.securityconfig;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

	@Autowired
	ReactiveAuthenticationManagerResolver<ServerWebExchange> authenticationManagerResolver;
	@Autowired
	private YAMLConfig yamlConfig;

	/*
	 * @Value("${omnichannel.webOrigins}") private String webOrigins;
	 */

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

		/*
		 * JwtIssuerReactiveAuthenticationManagerResolver
		 * tenantAuthenticationManagerResolver = new
		 * JwtIssuerReactiveAuthenticationManagerResolver (yamlConfig.getRealm(),
		 * "https://rtest.authz.cloudentity.io/rtest/ce-samples-oidc-client-apps", "");
		 */
		http.csrf().disable().cors().and().authorizeExchange().pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.pathMatchers(HttpMethod.POST, "/api/v1/receiveMessage").permitAll()
				.pathMatchers(HttpMethod.POST, "/api/v1/deliveryStatus").permitAll()
				.pathMatchers(HttpMethod.POST, "/api/v1/bulk/deliveryStatus").permitAll()
				.pathMatchers(HttpMethod.POST, "/api/v1/webhook").permitAll()
				.pathMatchers(HttpMethod.POST, "/api/v1/sms/deliveryStatus").permitAll()
				.pathMatchers(HttpMethod.POST, "/api/v2/email/emailEvent").permitAll()
				.pathMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
				.pathMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll().pathMatchers("/actuator/**").permitAll()
				.anyExchange().authenticated().and()
				.oauth2ResourceServer(oauth2 -> oauth2.authenticationManagerResolver(authenticationManagerResolver));
		return http.build();
	}

	@Bean
	CorsConfigurationSource corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		// Possibly...
		// config.applyPermitDefaultValues()
		config.setAllowCredentials(false);
		config.setAllowedOrigins(Arrays.asList("*"));
		config.addAllowedMethod("*");
		config.addAllowedHeader("*");

		// config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT",
		// "DELETE","OPTIONS"));
		/*
		 * config.setAllowedHeaders(Arrays.asList("X-User-Id",
		 * "X-Application-Access-Key", "Origin", "Accept", "X-Requested-With",
		 * "Content-Type", "Access-Control-Request-Method",
		 * "Access-Control-Request-Headers", "schemaName", "X-Auth-Token",
		 * "X-Exchange-Id"));
		 */
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	private Mono<JwtReactiveAuthenticationManager> addManager(
			Map<String, ReactiveAuthenticationManager> authenticationManagers, String issuer) {

		return Mono.fromCallable(() -> ReactiveJwtDecoders.fromIssuerLocation(issuer))
				.subscribeOn(Schedulers.boundedElastic()).map(JwtReactiveAuthenticationManager::new)
				.doOnNext(authenticationManager -> authenticationManagers.put(issuer, authenticationManager));
	}

}
