package edu.neu.coe.csye6225.webapp.service;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.neu.coe.csye6225.webapp.exeception.DataNotFoundExeception;
import edu.neu.coe.csye6225.webapp.exeception.UserAuthrizationExeception;
import edu.neu.coe.csye6225.webapp.model.User;
import edu.neu.coe.csye6225.webapp.repository.UserRepository;

@Service
public class AuthService {

	@Autowired
	UserRepository userrepo;

    public BCryptPasswordEncoder PassEncoder() {
        return new BCryptPasswordEncoder();
    }
	public User getUserDetailsAuth(Long userId) throws DataNotFoundExeception{
		Optional<User> user = userrepo.findById(userId);
		if (user.isPresent()) {
			return user.get();
		}
		throw new DataNotFoundExeception("User Not Found");
	}

	public boolean isAuthorised(Long userId,String tokenEnc) throws DataNotFoundExeception, UserAuthrizationExeception {

		User user=getUserDetailsAuth(userId);
		byte[] token = Base64.getDecoder().decode(tokenEnc);
        String decodedStr = new String(token, StandardCharsets.UTF_8);

        String userName = decodedStr.split(":")[0];
        String passWord = decodedStr.split(":")[1];
        System.out.println("Value of Token" + " "+ decodedStr);
        if(!((user.getUsername().equals(userName)) && (PassEncoder().matches(passWord,user.getPassword())))){
        	throw new UserAuthrizationExeception("Forbidden to access");
        }
		return true;
	}
	
	public String getUserNameFromToken(String tokenEnc) {
		byte[] token = Base64.getDecoder().decode(tokenEnc);
        String decodedStr = new String(token, StandardCharsets.UTF_8);
        String userName = decodedStr.split(":")[0];
        String passWord = decodedStr.split(":")[1];
//        System.out.println("Value of Token" + " "+ decodedStr);
        return userName;
	}

}