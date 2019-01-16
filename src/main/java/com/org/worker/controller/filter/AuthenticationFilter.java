package com.org.worker.controller.filter;

import com.org.worker.exception.AuthenticationFailed;
import com.org.worker.repository.PlainTextRepository;
import com.org.worker.util.Crypto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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
            throw new AuthenticationFailed("token must not be blank");
        }

        String[] secrets = Crypto.decrypt(token).split("_");
        if (!plainTextRepository.authIgnoreEncrypt(secrets[0], secrets[1])) {
            throw new AuthenticationFailed("token didn't match");
        }

        chain.doFilter(request, response);
    }
}
