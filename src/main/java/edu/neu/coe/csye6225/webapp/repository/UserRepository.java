package edu.neu.coe.csye6225.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.neu.coe.csye6225.webapp.model.User;

public interface UserRepository extends JpaRepository<User,Long> {

	User findByUsername(String username);
}
