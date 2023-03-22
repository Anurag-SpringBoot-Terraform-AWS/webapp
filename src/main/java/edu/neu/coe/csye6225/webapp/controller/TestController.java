package edu.neu.coe.csye6225.webapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.timgroup.statsd.StatsDClient;

import edu.neu.coe.csye6225.webapp.model.UserDto;

@RestController
@RequestMapping("/healthz")
public class TestController {

	private static final Logger logger = LoggerFactory.getLogger(TestController.class);
	
	@Value("${logging.file.name}")
	private String loggingFilename;
	
	@Autowired
	private StatsDClient statsDClient;
	
	@GetMapping()
	public ResponseEntity<?> getHealth() {
		logger.info("Logger filePath "+loggingFilename);
		statsDClient.incrementCounter("endpoint.getHealth.http.get");
		return new ResponseEntity<UserDto>( HttpStatus.OK);
	}
}
