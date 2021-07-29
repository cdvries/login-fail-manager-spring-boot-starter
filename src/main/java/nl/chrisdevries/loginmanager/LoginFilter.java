package nl.chrisdevries.loginmanager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class LoginFilter extends OncePerRequestFilter {

    @Autowired
    private LoginFailManager loginManager;

    @Value("${login-fail-manager.bannedMessageHtmlPath:}")
    private String bannedMessageHtmlPath;

    @Value("${login-fail-manager.enabled:}")
    private boolean enabled = true;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        if (enabled && loginManager.checkBan(httpServletRequest.getRemoteAddr())) {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            if (bannedMessageHtmlPath == null || bannedMessageHtmlPath.isBlank()) {
                httpServletResponse.getWriter().write("Too many login attempts.");
            } else {
                fileToOutputStream(bannedMessageHtmlPath, httpServletResponse.getOutputStream());
            }
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    private void fileToOutputStream(String filename, OutputStream output) throws IOException {
        InputStream in = this.getClass().getResourceAsStream(filename);
        StreamUtils.copy(in, output);
    }
}
