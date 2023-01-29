package edu.neu.coe.csye6225.webapp;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebappApplicationTests {

//	@Autowired
//	private UserRepository userRepo;
//	@Autowired
//	private UserService userService;
//	
//	@Test
//    void canAddStudent() {
//        // given
//	  User user = new User();
//      user.setUsername("test.1998@gmail.com");
//      user.setFirstName("Tester");
//      user.setLastName("Junit");
//      user.setPassword("123456");
//
//        // when
//      try {
//		userService.createUser(user);
//	} catch (UserExistException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//
//        // then
//        ArgumentCaptor<User> userArgumentCaptor =
//                ArgumentCaptor.forClass(User.class);
//        verify(userRepo)
//                .save(userArgumentCaptor.capture());
//        User capturedStudent = userArgumentCaptor.getValue();
//        assertThat(capturedStudent).isEqualTo(user);
//    }

//	
//	@Test
//    @Order(1)
//    @Rollback(value = false)
//    public void saveUserTest(){
//      User user = new User();
//      user.setUsername("test.1998@gmail.com");
//      user.setFirstName("Tester");
//      user.setLastName("Junit");
//      user.setPassword("123456");
//      User userObj = userRepo.save(user);
//	  System.out.println(userObj);
//	  Assertions.assertThat(userObj.getId()).isGreaterThan(0);
//    }
	
//	@Test
//    public void Test() {
//		Assertions.assertThat(1).isGreaterThan(0);
//	}
	
}
