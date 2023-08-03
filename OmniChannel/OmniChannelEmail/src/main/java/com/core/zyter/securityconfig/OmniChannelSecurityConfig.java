/*
 * @OmniChannelSecurityConfig.java@
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

package com.core.zyter.securityconfig;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class OmniChannelSecurityConfig {

    @Autowired
    AuthenticationManagerResolver<HttpServletRequest> tenantAuthenticationManagerResolver;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
         * http .authorizeHttpRequests(authz -> authz .requestMatchers(HttpMethod.GET ,
         * "/foos") .hasAuthority("SCOPE_email") .requestMatchers(HttpMethod.POST,
         * "/foos") .hasAuthority("SCOPE_profile") .anyRequest() .authenticated())
         * .oauth2ResourceServer(oauth2 -> oauth2.opaqueToken()) .;
         */

        //TODO replace this with our own implementation of AuthenticationManagerResolver
        //TODO Our implementation will fetch the tenant specific issuer URL from DB and
        //based upon the request --->token sent as part of auth header of request
        //build the AuthenicationManager and save it in a cache
        //If the Authentication manager is not already present ......search DB based on the issuer and
        //add it to the AuthenicationManager cache!!!!!!!!


        //Further manage all the Oauth2 providers in Keycloak broker-----stretch goal

        //1.  should be able to get Auth tokens from multiple Auth servers----------White listed set of issuers
        //2.  I should not restart my application each time a new tenant is onboarded
        //3.
        /*
         * JwtIssuerAuthenticationManagerResolver authenticationManagerResolver = new
         * JwtIssuerAuthenticationManagerResolver
         * ("http://localhost:8181/auth/realms/zyter",
         * "https://rtest.authz.cloudentity.io/rtest/ce-samples-oidc-client-apps", "");
         */
// "/receiveMessage", method = RequestMethod.POST http://localhost:8082/api/v2/email
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/receiveMessage").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/deliveryStatus").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/bulk/deliveryStatus").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/sms/deliveryStatus").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v2/email/emailEvent").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "http://localhost:8090/api/v1/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer(oauth2 -> oauth2.authenticationManagerResolver(tenantAuthenticationManagerResolver));
        return http.build();

    }
}