/*
 * @TenantAuthenticationManagerResolver.java@
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

package com.core.zyter.securityconfig;

import com.core.zyter.dao.WhiteListedOauth2ProvidersRepository;
import com.core.zyter.exceptions.Oauth2ClaimsValidationFailedException;
import com.core.zyter.vos.TenantCliams;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TenantAuthenticationManagerResolver implements AuthenticationManagerResolver<HttpServletRequest> {

    private final Map<String, AuthenticationManager> authenticationManagers = new ConcurrentHashMap<>();
    @Autowired
    WhiteListedOauth2ProvidersRepository whiteListedOauth2ProvidersRepository;
    private final BearerTokenResolver resolver = new DefaultBearerTokenResolver();

    @Override
    public AuthenticationManager resolve(HttpServletRequest context) {

        final TenantCliams tenantClaim = toTenant(context);
        log.info("tenant resolution being done to ########################## {}", tenantClaim);
        return this.authenticationManagers.computeIfAbsent(
                tenantClaim.getTenanatId() + "_" + tenantClaim.getTenantIndex(), str -> this.fromTenant(tenantClaim));
    }

    private TenantCliams toTenant(HttpServletRequest request) {
        var token = this.resolver.resolve(request);
        try {
            JWTClaimsSet jwtClaimsSet = JWTParser.parse(token).getJWTClaimsSet();
            var tenantId = (String) jwtClaimsSet.getClaim("tenant_id");
            var tenantIndex = (String) jwtClaimsSet.getClaim("tenant_index");
            if (StringUtils.isBlank(tenantId)) {
                throw new Oauth2ClaimsValidationFailedException("tenant_id claim not set");
            }
            var tenantClaims = TenantCliams.builder().tenanatId(tenantId)
                    .tenantIndex(StringUtils.isBlank(tenantIndex) ? "0" : tenantIndex).build();

            return tenantClaims;

        } catch (Exception e) {
            log.error("error parsing token", e);
            throw new IllegalArgumentException(e);
        }

    }

    private AuthenticationManager fromTenant(TenantCliams claims) {
        var tenantIssuerList = whiteListedOauth2ProvidersRepository
                .getWhiteListedOauth2ProvidersByTenantIdAndTenantIndex(claims.getTenanatId(), claims.getTenantIndex());
        if (tenantIssuerList == null || tenantIssuerList.isEmpty()) {
            log.info("tenant id not white listed: {0} ", claims);
            throw new IllegalArgumentException("tenant id not white listed: " + claims);
        }
        return Optional.of(tenantIssuerList.get(0)).map(tenant -> tenant.getIssuerUrl())
                .map(issuerUrl -> (JwtDecoder) JwtDecoders.fromIssuerLocation(issuerUrl))
                .map(JwtAuthenticationProvider::new)
                .orElseThrow(() -> new IllegalArgumentException("unknown tenant"))::authenticate;

    }

}
