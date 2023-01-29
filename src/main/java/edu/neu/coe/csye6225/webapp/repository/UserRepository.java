package edu.neu.coe.csye6225.webapp.repository;

import edu.neu.coe.csye6225.webapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

	User findByUsername(String username);
}
