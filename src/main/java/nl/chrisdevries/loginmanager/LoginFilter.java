package nl.chrisdevries.loginmanager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class LoginFilter extends OncePerRequestFilter {

	@Autowired
	private LoginFailManager loginManager;

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
			throws ServletException, IOException {
		if (loginManager.checkBan(httpServletRequest.getRemoteAddr())) {
			httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
			httpServletResponse.getWriter().write("Banned");
		}
		else {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
		}
	}

}
