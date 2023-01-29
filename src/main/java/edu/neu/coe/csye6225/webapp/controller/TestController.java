package edu.neu.coe.csye6225.webapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.neu.coe.csye6225.webapp.model.UserDto;

@RestController
@RequestMapping("/healthz")
public class TestController {

	@GetMapping()
	public ResponseEntity<?> getHealth() {
		return new ResponseEntity<UserDto>( HttpStatus.OK);
	}
}
