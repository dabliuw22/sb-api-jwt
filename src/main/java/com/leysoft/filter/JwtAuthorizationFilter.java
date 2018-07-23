
package com.leysoft.filter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leysoft.util.Constants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    private String key;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, String key) {
        super(authenticationManager);
        this.key = key;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(Constants.HEADER_NAME);
        if (!requireAuthentication(header)) {
            chain.doFilter(request, response);
            return;
        }

        boolean validToken;
        Claims body = null;
        try {
            String jwt = header.replace(Constants.PREFIX_BEARER, "");
            body = Jwts.parser().setSigningKey(key.getBytes()).parseClaimsJws(jwt).getBody();
            validToken = true;
        } catch (JwtException | IllegalArgumentException e) {
            validToken = false;
        }
        UsernamePasswordAuthenticationToken authentication = null;
        if (validToken) {
            String username = body.getSubject();
            String stringRoles = (String) body.get("roles");
            LOGGER.info("{} -> {}", username, stringRoles);
            List<String> roles =
                    new ObjectMapper().readValue(stringRoles, new TypeReference<List<String>>() {
                    });
            List<GrantedAuthority> authority = roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
            authentication = new UsernamePasswordAuthenticationToken(username, null, authority);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    protected boolean requireAuthentication(String header) {
        if (header != null) {
            if (header.startsWith(Constants.PREFIX_BEARER)) {
                return true;
            }
        }
        return false;
    }
}
