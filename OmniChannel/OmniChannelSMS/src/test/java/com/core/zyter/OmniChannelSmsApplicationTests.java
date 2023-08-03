/*
 * @OmniChannelSmsApplicationTests.java@
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

package com.core.zyter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = OmniChannelSmsApplication.class,
        // Normally spring.cloud.config.enabled:true is the default but since we have the config server on the classpath
        // we need to set it explicitly.
        properties = {
                "spring.cloud.config.enabled:true",
                "management.security.enabled=false",
                "management.endpoints.web.exposure.include=*"
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OmniChannelSmsApplicationTests {

    @Test
    void contextLoads() {
    }

}
