package com.core.zyter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = OmnichannelEmailApplication.class,
// Normally spring.cloud.config.enabled:true is the default but since we have the config server on the classpath
// we need to set it explicitly.
properties = {
        "spring.cloud.config.enabled:true",
        "management.security.enabled=false",
        "management.endpoints.web.exposure.include=*"
},
webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OmnichannelEmailApplicationTests {

	@Test
	void contextLoads() {
	}

}
