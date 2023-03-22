package edu.neu.coe.csye6225.webapp.controller;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.timgroup.statsd.StatsDClient;

import edu.neu.coe.csye6225.webapp.constants.UserConstants;
import edu.neu.coe.csye6225.webapp.exeception.DataNotFoundExeception;
import edu.neu.coe.csye6225.webapp.exeception.InvalidInputException;
import edu.neu.coe.csye6225.webapp.exeception.UserAuthrizationExeception;
import edu.neu.coe.csye6225.webapp.exeception.UserExistException;
import edu.neu.coe.csye6225.webapp.model.User;
import edu.neu.coe.csye6225.webapp.model.UserDto;
import edu.neu.coe.csye6225.webapp.model.UserUpdateRequestModel;
import edu.neu.coe.csye6225.webapp.service.AuthService;
import edu.neu.coe.csye6225.webapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController()
@RequestMapping("v1/user")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	UserService userService;
	
	@Autowired
	AuthService authService;
	
	@Autowired
	private StatsDClient statsDClient;
	
    @GetMapping(value = "/{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable("userId") Long userId,HttpServletRequest request){
    	try {
    		logger.info("Start of UserController.getUserDetails with userId "+userId);
    		statsDClient.incrementCounter("endpoint.getUser.http.get");
    		if(userId.toString().isBlank()||userId.toString().isEmpty()) {
            	throw new InvalidInputException("Enter Valid User Id");
            }
    		authService.isAuthorised(userId,request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<UserDto>( userService.getUserDetails(userId),HttpStatus.OK);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			logger.warn("Bad Request error in UserController.getUserDetails with userId "+e);
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.BAD_REQUEST);
		}
    	catch (UserAuthrizationExeception e) {
			// TODO Auto-generated catch block
    		logger.warn("Authorization error in UserController.getUserDetails with userId "+e);
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.FORBIDDEN);
		}
    	catch (DataNotFoundExeception e) {
    		logger.warn("Data Not found error in UserController.getUserDetails with userId "+userId);
			// TODO Auto-generated catch block
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.NOT_FOUND);
		}
    	catch(Exception e) {
    		logger.error("Server error in UserController.getUserDetails "+e);
    		return new ResponseEntity<String>(UserConstants.InternalErr,HttpStatus.INTERNAL_SERVER_ERROR);
    	}
        
    }
    
    @PutMapping(value = "/{userId}")
    public ResponseEntity<?> updateUserDetails(@PathVariable("userId") Long userId,@Valid @RequestBody UserUpdateRequestModel user,
    		HttpServletRequest request,Errors error){
    	try {
    		logger.info("Start of UserController.updateUserDetails with userId "+userId);
    		statsDClient.incrementCounter("endpoint.updateUser.http.put");
    		if(userId.toString().isBlank()||userId.toString().isEmpty()) {
            	throw new InvalidInputException("Enter Valid User Id");
            }
    		authService.isAuthorised(userId,request.getHeader("Authorization").split(" ")[1]);
    		if(error.hasErrors()) {
    			String response = error.getAllErrors().stream().map(ObjectError::getDefaultMessage)
    					.collect(Collectors.joining(","));
    			throw new InvalidInputException(response);
    		}
			return new ResponseEntity<String>( userService.updateUserDetails(userId,user),HttpStatus.NO_CONTENT);
		} catch (InvalidInputException e) {
			logger.warn("Bad Request error in UserController.updateUserDetails with userId "+e);
			// TODO Auto-generated catch block
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.BAD_REQUEST);
		}
    	catch (UserAuthrizationExeception e) {
			// TODO Auto-generated catch block
    		logger.warn("Authorization error in UserController.updateUserDetails with userId "+e);
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.FORBIDDEN);
		}
    	catch (DataNotFoundExeception e) {
    		logger.warn("Data Not found error in UserController.updateUserDetails with userId "+userId);
			// TODO Auto-generated catch block
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.NOT_FOUND);
		}
    	catch(Exception e) {
    		logger.error("Server error in UserController.updateUserDetails "+e);
    		return new ResponseEntity<String>(UserConstants.InternalErr,HttpStatus.INTERNAL_SERVER_ERROR);
    	}
        
    }
    
    @PostMapping()
    public ResponseEntity<?> createUser(@Valid @RequestBody User user,Errors error){
    	try {
    		logger.info("Start of UserController.createUser with userId "+user.getId());
    		statsDClient.incrementCounter("endpoint.createUser.http.post");
    		if(error.hasErrors()) {
    			String response = error.getAllErrors().stream().map(ObjectError::getDefaultMessage)
    					.collect(Collectors.joining(","));
    			throw new InvalidInputException(response);
    		}
			return new ResponseEntity<UserDto>( userService.createUser(user),HttpStatus.CREATED);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			logger.warn("Bad Request error in UserController.createUser with userId "+e);
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.BAD_REQUEST);
		}
    	catch (UserExistException e) {
    		logger.warn("User exists error in UserController.createUser with userId ");
			// TODO Auto-generated catch block
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.BAD_REQUEST);
		}
    	catch(Exception e) {
    		logger.error("Server error in UserController.createUser "+e);
    		return new ResponseEntity<String>(UserConstants.InternalErr,HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
}
