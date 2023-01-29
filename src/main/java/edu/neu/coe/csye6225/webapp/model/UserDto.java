package edu.neu.coe.csye6225.webapp.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserDto {

	private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private LocalDateTime accountCreated;
    private LocalDateTime accountUpdated;
    
    public static UserDto getUserDto(User user) {
    	UserDto dto=new UserDto();
    	dto.setId(user.getId());
    	dto.setAccountCreated(user.getAccountCreated());
    	dto.setAccountUpdated(user.getAccountUpdated());
    	dto.setFirstName(user.getFirstName());
    	dto.setLastName(user.getLastName());
    	dto.setUsername(user.getUsername());
    	return dto;
    }
}
