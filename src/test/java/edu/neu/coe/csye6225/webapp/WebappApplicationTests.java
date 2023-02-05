package edu.neu.coe.csye6225.webapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import edu.neu.coe.csye6225.webapp.model.User;
import edu.neu.coe.csye6225.webapp.repository.UserRepository;
import edu.neu.coe.csye6225.webapp.service.UserService;
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ContextConfiguration(classes = { WebappApplicationTests.class })
class WebappApplicationTests {

	@Test
	public void Test() {
		assertTrue("Hello".equals("Hello"));
	}

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;

	@Test
	public void saveUserTest() {
		User user = new User();
		user.setId(Long.valueOf(100));user.setFirstName("karthik");
		user.setLastName("P");user.setPassword("1234");user.setUsername("a1100@dddfgii.com");
		String username = "a1100@dddfgii.com";
		when(repository.findByUsername(username)).thenReturn(user);
		assertEquals(user.getUsername(), service.loadUserByUsername(username).getUsername());
	}
}