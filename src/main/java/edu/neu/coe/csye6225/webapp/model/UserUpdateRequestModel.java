package edu.neu.coe.csye6225.webapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@JsonIgnoreProperties(allowGetters = true, ignoreUnknown = false)
@Data
public class UserUpdateRequestModel {

	
    @NotEmpty(message="First Name cannot be null/empty")
    private String firstName;

    
    @NotEmpty(message="Last Name cannot be null/empty")
    private String lastName;
    
    
    @NotEmpty(message="Password cannot be null/empty")
    private String password;


    
}