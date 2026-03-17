package com.example.card_management_system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "card.ms.url=http://localhost:8080"
})
class CardManagementSystemApplicationTests {

	@Test
	void contextLoads() {
	}


}
