/*
 * @ConfigProperties.java@
 * Created on 08Dec2022
 *
 * Copyright (c) 2022 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sms")
@Data
public class ConfigProperties {

	private String accountSid;
	private String accountToken;
	private String messageSid;
	private String notifySid;
	private String callbackUrl;
	private String uploadFilePath;
	private String genericMessageSid;

}
