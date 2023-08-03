/*
 * @TenantAwareAuthenticationManagerResolver.java@
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

import com.infinite.omniapigateway.repository.WhiteListedOauth2ProvidersRepository;
import com.infinite.omniapigateway.securityconfig.exceptions.Oauth2ClaimsValidationFailedException;
import com.infinite.omniapigateway.vos.TenantClaims;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class TenantAwareAuthenticationManagerResolver
        implements ReactiveAuthenticationManagerResolver<ServerWebExchange> {

    private static final Pattern authorizationPattern = Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$",
            Pattern.CASE_INSENSITIVE);
    private final Map<String, Mono<ReactiveAuthenticationManager>> authenticationManagers = new ConcurrentHashMap<>();
    WhiteListedOauth2ProvidersRepository whiteListedOauth2ProvidersRepository;
    ServerBearerTokenAuthenticationConverter converter;

    public TenantAwareAuthenticationManagerResolver(
            WhiteListedOauth2ProvidersRepository whiteListedOauth2ProvidersRepository) {
        super();
        this.converter = new ServerBearerTokenAuthenticationConverter();
        converter.setBearerTokenHeaderName(HttpHeaders.AUTHORIZATION);
        this.whiteListedOauth2ProvidersRepository = whiteListedOauth2ProvidersRepository;
    }

    private static BearerTokenError invalidTokenError() {
        return BearerTokenErrors.invalidToken("Bearer token is malformed");
    }

    @Override
    public Mono<ReactiveAuthenticationManager> resolve(ServerWebExchange exchange) {
        // TODO Auto-generated method stub

        // converter.convert(context).map(auth -> auth.);

        // String jwtToken = token(exchange.getRequest());
        var tenantClaims = toTenant(exchange);

        return fromTenant(tenantClaims).cast(ReactiveAuthenticationManager.class);

        /*
         * this.authenticationManagers.computeIfAbsent( tenantClaims.tenanatId() + "_" +
         * tenantClaims.tenantIndex(), (str) -> {
         * log.info("computing Authentication manager from scratch for {}",tenantClaims)
         * ; return fromTenant(tenantClaims).cast(ReactiveAuthenticationManager.class);
         * });
         */
    }

    private TenantClaims toTenant(ServerWebExchange exchange) {
        String jwtToken = token(exchange.getRequest());
        log.info("token is {}", jwtToken);
        try {
            var tenantClaims = getJwtClaimSet(jwtToken);
            return tenantClaims;

        } catch (Exception e) {
            log.error("error parsing token", e);
            throw new IllegalArgumentException(e);
        }

    }

    private Mono<ReactiveAuthenticationManager> fromTenant(TenantClaims claims) {
        String tenantKey = claims.tenanatId() + "_" + claims.tenantIndex();
        Mono<ReactiveAuthenticationManager> tenenatMono = null;

        try {
            if (authenticationManagers.containsKey(tenantKey)) {
                log.info("tenant key {} exists returning cached Authentication manager", tenantKey);
                return authenticationManagers.get(tenantKey);
            }

            var tenantIssuerList = whiteListedOauth2ProvidersRepository.findByTenantIdAndTenantIndex(claims.tenanatId(),
                    claims.tenantIndex());

            Mono<ReactiveAuthenticationManager> authManager = tenantIssuerList.next().log()
                    .switchIfEmpty(Mono.error(
                            new OAuth2AuthenticationException("tenant issuer URL  claim not set: " + claims.tenanatId())))
                    .map(oauth -> ReactiveJwtDecoders.fromIssuerLocation(oauth.getIssuerUrl()))
                    .map(JwtReactiveAuthenticationManager::new).cast(ReactiveAuthenticationManager.class)
                    .doOnError(e -> e.printStackTrace());

            tenenatMono = authenticationManagers.computeIfAbsent(tenantKey, str -> authManager);
        } catch (Exception e) {

            log.error("fromTenant:: ", e);
            throw new RuntimeException(e);

        }
        return tenenatMono;

    }

    private TenantClaims getJwtClaimSet(String token) {
        try {
            JWTClaimsSet claims = JWTParser.parse(token).getJWTClaimsSet();
            var tenantId = (String) claims.getClaim("tenant_id");
            var tenantIndex = (String) claims.getClaim("tenant_index");
            if (StringUtils.isBlank(tenantId)) {
                throw new Oauth2ClaimsValidationFailedException("tenant_id claim not set");
            }
            var tenantClaims = new TenantClaims(tenantId, StringUtils.isBlank(tenantIndex) ? "0" : tenantIndex);
            return tenantClaims;
        } catch (Exception e) {
            log.error("error parsing token", e);
            throw new IllegalArgumentException(e);
        }

    }

    private String token(ServerHttpRequest request) {
        String authorizationHeaderToken = resolveFromAuthorizationHeader(request.getHeaders());

        if (authorizationHeaderToken != null) {
            return authorizationHeaderToken;
        }

        return null;
    }

    private String resolveFromAuthorizationHeader(HttpHeaders headers) {
        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.startsWithIgnoreCase(authorization, "bearer")) {
            return null;
        }
        Matcher matcher = authorizationPattern.matcher(authorization);
        if (!matcher.matches()) {
            BearerTokenError error = invalidTokenError();
            throw new OAuth2AuthenticationException(error);
        }
        return matcher.group("token");
    }

}
