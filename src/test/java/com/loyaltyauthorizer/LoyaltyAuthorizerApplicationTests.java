package com.loyaltyauthorizer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.loyaltyauthorizer.controllers.HealthController;

@SpringBootTest
class LoyaltyAuthorizerApplicationTests {

	@Autowired
	private HealthController healthController;

	@Test
	void contextLoads() throws Exception {
		assertNotNull(healthController);
	}

}
