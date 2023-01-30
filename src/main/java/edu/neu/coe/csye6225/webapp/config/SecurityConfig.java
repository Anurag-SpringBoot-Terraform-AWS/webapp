package edu.neu.coe.csye6225.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import edu.neu.coe.csye6225.webapp.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
//	@Autowired
//    private DataSource dataSource;
//     
//    @Autowired
//    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
//        auth.jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder())
//            .dataSource(dataSource)
//            .usersByUsernameQuery("select username, password, enabled from user where username=?")
//        ;
//    }
 

	@Autowired
	CustomUserDetailsService userService;

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userService);
		authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
		return authProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests((authz) -> authz.requestMatchers(HttpMethod.POST, "/v1/user").permitAll()
				.requestMatchers(new AntPathRequestMatcher("/healthz", "GET")).permitAll()
//				.requestMatchers(new AntPathRequestMatcher("v1/user", "GET")).hasAuthority("USER")
				.anyRequest()
				.authenticated());
		http.csrf((csrf) -> csrf.disable());
		http.authenticationProvider(authenticationProvider());
		return http.build();
	}
}