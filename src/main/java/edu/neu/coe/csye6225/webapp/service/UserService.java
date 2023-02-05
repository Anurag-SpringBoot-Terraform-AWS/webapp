package edu.neu.coe.csye6225.webapp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.neu.coe.csye6225.webapp.exeception.DataNotFoundExeception;
import edu.neu.coe.csye6225.webapp.exeception.UserAuthrizationExeception;
import edu.neu.coe.csye6225.webapp.exeception.UserExistException;
import edu.neu.coe.csye6225.webapp.model.Product;
import edu.neu.coe.csye6225.webapp.model.User;
import edu.neu.coe.csye6225.webapp.model.UserDto;
import edu.neu.coe.csye6225.webapp.model.UserUpdateRequestModel;
import edu.neu.coe.csye6225.webapp.repository.UserRepository;
import jakarta.validation.Valid;

@Service
public class UserService {

	@Autowired
	UserRepository userrepo;

	@Bean
	public BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	public String createUser(User user) throws UserExistException {
		User userDto = userrepo.findByUsername(user.getUsername());
		if (userDto == null) {
			user.setPassword(encoder().encode(user.getPassword()));
			userrepo.save(user);
			return "Created User";
		}
		throw new UserExistException("User Exists Already");
	}

	public UserDto getUserDetails(Long userId) throws DataNotFoundExeception {
		Optional<User> user = userrepo.findById(userId);
		if (user.isPresent()) {
			UserDto dto = UserDto.getUserDto(user.get());
			return dto;
		}
		throw new DataNotFoundExeception("User Not Found");
	}

	public String updateUserDetails(Long userId, UserUpdateRequestModel user) throws DataNotFoundExeception, UserAuthrizationExeception {
		Optional<User> userObj = userrepo.findById(userId);
		if (userObj.isPresent()) {
			if(!userObj.get().getUsername().equals(user.getUsername()))
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
		User user = userrepo.findByUsername(username);
		if (user == null) {
			return null;
		}
		return user;
	}

}
