package com.loyaltyauthorizer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.loyaltyauthorizer.controllers.AuthenticationController;
import com.loyaltyauthorizer.controllers.ConsumerController;
import com.loyaltyauthorizer.controllers.HealthController;
import com.loyaltyauthorizer.controllers.UserController;

@SpringBootTest
class LoyaltyAuthorizerApplicationTests {

	@Autowired
	private HealthController healthController;

	@Autowired
	private UserController userController;

	@Autowired
	private ConsumerController consumerController;

	@Autowired
	private AuthenticationController authenticationController;

	@Test
	void contextLoads() throws Exception {
		assertNotNull(healthController);
		assertNotNull(userController);
		assertNotNull(consumerController);
		assertNotNull(authenticationController);
	}

}
