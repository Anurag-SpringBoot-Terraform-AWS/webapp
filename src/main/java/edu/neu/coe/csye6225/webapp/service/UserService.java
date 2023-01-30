package edu.neu.coe.csye6225.webapp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.neu.coe.csye6225.webapp.exeception.DataNotFoundExeception;
import edu.neu.coe.csye6225.webapp.exeception.UserExistException;
import edu.neu.coe.csye6225.webapp.model.User;
import edu.neu.coe.csye6225.webapp.model.UserDto;
import edu.neu.coe.csye6225.webapp.model.UserUpdateRequestModel;
import edu.neu.coe.csye6225.webapp.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Service
public class UserService {

	@Autowired
	UserRepository userrepo;
	
	public String createUser(User user) throws UserExistException {
		User userDto = userrepo.findByUsername(user.getUsername());
		if (userDto == null) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userrepo.save(user);
			return "Created User";
		}
		throw new UserExistException("User Exists Already");
	}

	public UserDto getUserDetails(String userId) throws DataNotFoundExeception {
		Optional<User> user = userrepo.findById(Long.parseLong(userId));
		if (user.isPresent()) {
			UserDto dto=UserDto.getUserDto(user.get());
			return dto;
		}
		throw new DataNotFoundExeception("User Not Found");
	}

	public String updateUserDetails(String userId, UserUpdateRequestModel user) throws DataNotFoundExeception {
		Optional<User> userObj = userrepo.findById(Long.parseLong(userId));
		if (userObj.isPresent()) {
			User dto=userObj.get();
			dto.setFirstName(user.getFirstName());
			dto.setLastName(user.getLastName());
			dto.setPassword(user.getPassword());
			userrepo.save(dto);
			return "Updated User Details Successfully";
			
		}
		throw new DataNotFoundExeception("User Not Found");
	}

}
