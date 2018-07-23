package com.leysoft.service.inter;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

public interface JwtService {
	
	public String sing(Authentication authResult) throws JsonProcessingException;
	
	public String getBodyAuthentication(Map<String, Object> params) throws JsonProcessingException;
	
	public Claims getClaims(String header) throws JwtException, IllegalArgumentException;
	
	public List<GrantedAuthority> getGrantedAuthorities(Claims claims);
}
