/*
 * @OpenAPIConfiguration.java@
 * Created on 02Feb2023
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

import com.core.zyter.util.Constants;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = Constants.BEARER_AUTH_HEADER,
        type = SecuritySchemeType.HTTP,
        bearerFormat = Constants.JWT,
        scheme = "bearer"
)
public class OpenAPIConfiguration {
}