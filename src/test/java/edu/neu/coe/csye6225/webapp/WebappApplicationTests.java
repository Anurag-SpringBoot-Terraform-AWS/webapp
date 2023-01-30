package edu.neu.coe.csye6225.webapp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { WebappApplicationTests.class })
@SpringBootTest
class WebappApplicationTests {
	
	@Test
    public void Test() {
		assertTrue("Hello".equals("Hello"));
	}
	
}
