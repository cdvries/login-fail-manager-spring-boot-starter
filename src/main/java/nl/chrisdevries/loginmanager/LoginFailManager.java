package nl.chrisdevries.loginmanager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class LoginFailManager {

	private Map<String, LoginAttempt> loginAttempts = new HashMap<>();

	@Value("${login-fail-manager.maxLoginAttempts:6}")
	private int maxAttempts;

	@Value("${login-fail-manager.banSeconds:60}")
	private int banSeconds;

	public void loginFail(String name, String remoteAddress) {
		log.warn("Login failed: user:{}, remote address: {}", name, remoteAddress);
		LoginAttempt loginAttempt = loginAttempts.get(remoteAddress);
		if (loginAttempt == null) {
			loginAttempt = new LoginAttempt();
			loginAttempts.put(remoteAddress, loginAttempt);
		}
		loginAttempt.attempts++;
		loginAttempt.dateTime = LocalDateTime.now();
		if (loginAttempt.attempts >= maxAttempts) {
			log.warn("Banned user:{}, remote address: {}", name, remoteAddress);
		}
	}

	public void success(String name, String remoteAddress) {
		log.info("Successful login: user:{}, remote address: {}", name, remoteAddress);
	}

	public boolean checkBan(String remoteAddress) {
		clearOldBans();
		LoginAttempt loginAttempt = loginAttempts.get(remoteAddress);
		if (loginAttempt != null) {
			return (loginAttempt.attempts >= maxAttempts);
		}
		return false;
	}

	public void clearOldBans() {
		loginAttempts.values().removeIf(entry -> LocalDateTime.now()
				.minusSeconds(banSeconds).isAfter(entry.dateTime));
	}

}
