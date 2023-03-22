package edu.neu.coe.csye6225.webapp.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.neu.coe.csye6225.webapp.exeception.DataNotFoundExeception;
import edu.neu.coe.csye6225.webapp.exeception.UserAuthrizationExeception;
import edu.neu.coe.csye6225.webapp.exeception.UserExistException;
import edu.neu.coe.csye6225.webapp.model.User;
import edu.neu.coe.csye6225.webapp.model.UserDto;
import edu.neu.coe.csye6225.webapp.model.UserUpdateRequestModel;
import edu.neu.coe.csye6225.webapp.repository.UserRepository;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	UserRepository userrepo;

	@Bean
	public BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	public UserDto createUser(User user) throws UserExistException {
		logger.info("Start of UserService.createUser with userId " + user.getId());
		User userDto = userrepo.findByUsername(user.getUsername());
		if (userDto == null) {
			user.setPassword(encoder().encode(user.getPassword()));
			userrepo.save(user);
			UserDto dto = UserDto.getUserDto(user);
			return dto;
		}
		throw new UserExistException("User Exists Already");
	}

	public UserDto getUserDetails(Long userId) throws DataNotFoundExeception {
		logger.info("Start of UserService.getUserDetails with userId " + userId);
		Optional<User> user = userrepo.findById(userId);
		if (user.isPresent()) {
			UserDto dto = UserDto.getUserDto(user.get());
			return dto;
		}
		throw new DataNotFoundExeception("User Not Found");
	}

	public String updateUserDetails(Long userId, UserUpdateRequestModel user)
			throws DataNotFoundExeception, UserAuthrizationExeception {
		logger.info("Start of UserService.updateUserDetails with userId " + userId);
		Optional<User> userObj = userrepo.findById(userId);
		if (userObj.isPresent()) {
			if (!userObj.get().getUsername().equals(user.getUsername()))
				throw new UserAuthrizationExeception("Forbidden to Update Data");
			User dto = userObj.get();
			dto.setFirstName(user.getFirstName());
			dto.setLastName(user.getLastName());
			dto.setPassword(encoder().encode(user.getPassword()));
			dto.setUsername(user.getUsername());
			userrepo.save(dto);
			return "Updated User Details Successfully";

		}
		throw new DataNotFoundExeception("User Not Found");
	}

	public User loadUserByUsername(String username) {
		// TODO Auto-generated method stub
		logger.info("Start of UserService.loadUserByUsername with username " + username);
		User user = userrepo.findByUsername(username);
		if (user == null) {
			return null;
		}
		return user;
	}

}
