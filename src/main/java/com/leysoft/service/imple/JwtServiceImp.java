package com.leysoft.service.imple;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leysoft.service.inter.JwtService;
import com.leysoft.service.inter.SimpleUserService;
import com.leysoft.util.Constants;
import com.leysoft.util.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtServiceImp implements JwtService {
	
	@Value(
            value = "${jwt.sing.key}")
    private String singKey;

    @Value(
            value = "${jwt.time-to-live}")
    private long timeToLive;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private SimpleUserService simpleUserService;

	@Override
	public String sing(Authentication authResult) throws JsonProcessingException {
		User user = (User) authResult.getPrincipal();
        List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        Claims claims = Jwts.claims();
        claims.put("roles", objectMapper.writeValueAsString(roles));
		return Jwts.builder().setClaims(claims).setSubject(user.getUsername())
                .signWith(SignatureAlgorithm.HS512, this.singKey.getBytes()).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + this.timeToLive)).compact();
	}

	@Override
	public String getBodyAuthentication(Map<String, Object> params) throws JsonProcessingException {
		return objectMapper.writeValueAsString(params);
	}
	
	@Override
	public Claims getClaims(String header) throws JwtException, IllegalArgumentException {
		String jwt = header.replace(Constants.PREFIX_BEARER, "");
		return Jwts.parser().setSigningKey(singKey.getBytes()).parseClaimsJws(jwt).getBody();
	}
	
	@Override
	public List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
		String username = claims.getSubject();
		return Util.getGrantedAuthorities(simpleUserService.findByUsername(username));
	}
}
