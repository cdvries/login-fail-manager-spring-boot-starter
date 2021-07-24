package nl.chrisdevries.loginmanager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationEventListener
		implements ApplicationListener<AbstractAuthenticationEvent> {

	@Autowired
	private LoginFailManager loginManager;

	@Override
	public void onApplicationEvent(AbstractAuthenticationEvent authenticationEvent) {
		if (authenticationEvent instanceof InteractiveAuthenticationSuccessEvent) {

			return;
		}
		Authentication authentication = authenticationEvent.getAuthentication();
		WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();

		if (authentication.isAuthenticated()) {
			loginManager.success(authentication.getName(), details.getRemoteAddress());
		}
		else {
			loginManager.loginFail(authentication.getName(), details.getRemoteAddress());
		}
	}

}
