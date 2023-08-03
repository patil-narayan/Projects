/*
 * @APIGatewayConfiguration.java@
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class APIGatewayConfiguration {

    private final Logger logger = LoggerFactory.getLogger(APIGatewayConfiguration.class);

    @Value("${omnichannel.singleSmsUrl}")
    private String singleSmsUrl;
    @Value("${omnichannel.campaignUrl}")
    private String campaignUrl;

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        logger.info("{}", "Inside Service APIGatewayConfiguration");
        return builder
                .routes()
                .route(p -> p.path("/api/v1/**").uri("lb://OmniChannelSMS"))
                .route(p -> p.path("/swagger-ui/**").uri("lb://OmniChannelSMS"))
                .route(p -> p.path("/v3/api-docs/**").uri("lb://OmniChannelSMS"))
                .route(p -> p.path("/api/v2/email/**").uri("lb://OmniChannelEmail"))
                .build();
    }


    @Bean
    public RouteLocator gatewayUrlRouter(RouteLocatorBuilder builder) {
        logger.info("{}", "Inside URI APIGatewayConfiguration");
        return builder
                .routes()
                .route(p -> p.path("/api/sms/**").filters(f -> f.stripPrefix(2)).uri(singleSmsUrl))
                .route(p -> p.path("/api/campaign/**").filters(f -> f.stripPrefix(2)).uri(campaignUrl))
                .build();
    }

}
