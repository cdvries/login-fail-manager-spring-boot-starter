package nl.chrisdevries.loginmanager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoginFailManagerAutoConfiguration {

	@Bean
	public AuthenticationEventListener authenticationEventListener() {
		return new AuthenticationEventListener();
	}

	@Bean
	public LoginFilter loginFilter() {
		return new LoginFilter();
	}

	@Bean
	public LoginFailManager loginManager() {
		return new LoginFailManager();
	}

}
