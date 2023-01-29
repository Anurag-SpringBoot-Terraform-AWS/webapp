package edu.neu.coe.csye6225.webapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	private final UserDetailsService userDetailsService;

	public SecurityConfig(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

//	@Autowired
//    private DataSource dataSource;
// 
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
//    
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .requestMatchers(new AntPathRequestMatcher("/v1/user/", "POST"))
//                .permitAll()
//                .and()
//                .authorizeRequests()
//                .anyRequest()
//                .authenticated()
//                ;
// 
//        return http.build();
//    }
// 
//    @Bean
//    public AuthenticationManager authenticationManager() {
//        return new ProviderManager(Collections.singletonList(authenticationProvider()));
//    }
// 
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService());
//        provider.setPasswordEncoder(passwordEncoder());
//        return provider;
//    }
// 
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new JdbcUserDetailsManager(dataSource);
//    }
//	
	
//	@Bean
//	 public HttpSecurity httpSecurity() throws Exception {
//	        return new HttpSecurity() {
//	            protected void configure(HttpSecurity http) throws Exception {
//	                http
//	                    .authorizeRequests()
//	                        .requestMatchers(new AntPathRequestMatcher("/v1/user", "POST"))
//	                        .permitAll()
//	                        .anyRequest()
//	                        .authenticated()
//	                    .and()
//	                    .formLogin()
//	                    .and()
//	                    .httpBasic()
//	                    .and()
//	                    .csrf().disable();
//	            }
//	        };
//	    }
	
	@Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http
          .authorizeHttpRequests((authz) -> {
          	authz.requestMatchers(new AntPathRequestMatcher("/v1/user/", "POST")).permitAll();
          	authz.requestMatchers(new AntPathRequestMatcher("/healthz", "GET")).permitAll();
          	authz.anyRequest().authenticated();
          	
          }
          );
//      new AntPathMatcher()
      return http.build();
  }

////	@Bean
//	public void configure(HttpSecurity http) throws Exception {
//		http.csrf().disable().authorizeRequests().requestMatchers(PathRequest.toStaticResources().atCommonLocations())
//				.permitAll().requestMatchers(new AntPathRequestMatcher("/v1/user", "POST")).permitAll().
//				requestMatchers(new AntPathRequestMatcher("/healthz", "GET"))
//				.permitAll().anyRequest().authenticated();
////                .and()
////                .formLogin().loginPage("/login").permitAll()
////                .and()
////                .logout().permitAll();
//	}
}
