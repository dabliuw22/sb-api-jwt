
package com.leysoft.filter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leysoft.dto.LoginRequest;
import com.leysoft.util.Constants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private AuthenticationManager authenticationManager;

    private String key;

    private long timeToLive;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, String key,
            long timeToLive) {
        this.authenticationManager = authenticationManager;
        this.key = key;
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
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
        LOGGER.info("username -> {}", user.getUsername());
        List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        Claims claims = Jwts.claims();
        claims.put("roles", new ObjectMapper().writeValueAsString(roles));

        String jwt = Jwts.builder().setClaims(claims).setSubject(user.getUsername())
                .signWith(SignatureAlgorithm.HS512, this.key.getBytes()).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + this.timeToLive)).compact();

        Map<String, Object> body = new HashMap<>();
        body.put("token", jwt);
        body.put("roles", roles);

        response.addHeader(Constants.HEADER_NAME, Constants.PREFIX_BEARER + jwt);
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(200);
        response.setContentType("application/json");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Error de autenticación, credenciales incorrectas");
        body.put("error", failed.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType("application/json");
    }
}
