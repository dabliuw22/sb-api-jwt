package com.leysoft.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leysoft.entity.SimpleUser;

public class Util {
	
	private Util() {
	}
	
	public static List<GrantedAuthority> getGrantedAuthorities(SimpleUser user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(rol -> {
            GrantedAuthority authority = new SimpleGrantedAuthority(Constants.PREFIX_ROLE + rol.getName());
            authorities.add(authority);
        });
        return authorities;
    }
	
	public static List<String> authoritiesToString(Collection<GrantedAuthority> authorities) {
		return authorities.stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
	}
	
	public static List<String> authoritiesToString(String authorities, ObjectMapper mapper) throws IOException {
		return mapper.readValue(authorities, new TypeReference<List<String>>() {
        });
	}
}
