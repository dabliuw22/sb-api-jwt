
package com.leysoft.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.leysoft.service.inter.JwtService;
import com.leysoft.util.Constants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
    
    private JwtService jwtService;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtService jwtService) {
        super(authenticationManager);
        this.jwtService = jwtService;
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
            body = jwtService.getClaims(header);
            validToken = true;
        } catch (JwtException | IllegalArgumentException e) {
        	LOGGER.error("Error -> {}", e.getMessage());
            validToken = false;
        }
        UsernamePasswordAuthenticationToken authentication = null;
        if (validToken) {
            String username = body.getSubject();
            List<GrantedAuthority> authority = jwtService.getGrantedAuthorities(body);
            authentication = new UsernamePasswordAuthenticationToken(username, null, authority);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    protected boolean requireAuthentication(String header) {
    	boolean required = false;
        if (header != null && header.startsWith(Constants.PREFIX_BEARER)) {
            required = true;
        }
        return required;
    }
}
