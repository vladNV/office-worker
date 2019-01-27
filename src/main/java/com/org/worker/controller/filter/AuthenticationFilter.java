package com.org.worker.controller.filter;

import com.org.worker.exception.AuthenticationFailed;
import com.org.worker.repository.PlainTextRepository;
import com.org.worker.util.Crypto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Order(2)
public class AuthenticationFilter implements Filter {
    private PlainTextRepository plainTextRepository;

    public AuthenticationFilter(PlainTextRepository plainTextRepository) {
        this.plainTextRepository = plainTextRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession();

        String token = (String) session.getAttribute("token");
        if (StringUtils.isBlank(token)) {
            LOG.info("Token must not be null");
            ((HttpServletResponse) response).sendRedirect("/users/");
            return;
        }

        String[] secrets = Crypto.decrypt(token).split("_");
        if (!plainTextRepository.authIgnoreEncrypt(secrets[0], secrets[1])) {
            throw new AuthenticationFailed("Token didn't match");
        }

        chain.doFilter(request, response);
    }
}
