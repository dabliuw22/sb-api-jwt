
package com.leysoft.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leysoft.dto.LoginRequest;
import com.leysoft.service.inter.JwtService;
import com.leysoft.util.Constants;
import com.leysoft.util.Util;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private AuthenticationManager authenticationManager;

    private JwtService jwtService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter(Constants.Name.USERNAME_NAME);
        String password = request.getParameter(Constants.Name.PASW_NAME);
        LoginRequest loginRequest = null;
        if (username == null || password == null) {
            try {
                loginRequest =
                        new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
                username = username == null ? loginRequest.getUsername() : username.trim();
                password = password == null ? loginRequest.getPassword() : password;
            } catch (IOException e) {
                LOGGER.error("Error -> {}", e.getMessage());
            }
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        User user = (User) authResult.getPrincipal();
        String jwt = jwtService.sing(authResult);

        Map<String, Object> body = new HashMap<>();
        body.put(Constants.Name.MESSAGE_NAME, Constants.Message.SUCCESSFUL_AUTHENTICATION);
        body.put(Constants.Name.TOKEN_NAME, jwt);
        body.put(Constants.Name.ROLES_NAME, Util.authoritiesToString(user.getAuthorities()));

        response.addHeader(Constants.HEADER_NAME, Constants.PREFIX_BEARER + jwt);
        response.getWriter().write(jwtService.getBodyAuthentication(body));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(Constants.APPLICATION_JSON);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        Map<String, Object> body = new HashMap<>();
        body.put(Constants.Name.MESSAGE_NAME, Constants.Message.UNSUCCESSFUL_AUTHENTICATION);
        body.put(Constants.Name.ERROR_NAME, failed.getMessage());

        response.getWriter().write(jwtService.getBodyAuthentication(body));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(Constants.APPLICATION_JSON);
    }
}
