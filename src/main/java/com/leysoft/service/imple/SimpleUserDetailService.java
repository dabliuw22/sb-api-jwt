
package com.leysoft.service.imple;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.leysoft.entity.SimpleUser;
import com.leysoft.service.inter.SimpleUserService;

@Service
public class SimpleUserDetailService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleUserDetailService.class);

    private static final String PREFIX_ROLE = "ROLE_";

    @Autowired
    private SimpleUserService simpleUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SimpleUser simpleUser = simpleUserService.findByUsername(username);
        if (simpleUser == null) {
            LOGGER.error("Not found username: {}", username);
            throw new UsernameNotFoundException("Not found username: " + username);
        }
        return new User(simpleUser.getUsername(), simpleUser.getPassword(), true, true, true, true,
                getGrantedAuthority(simpleUser));
    }

    private List<GrantedAuthority> getGrantedAuthority(SimpleUser user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(rol -> {
            GrantedAuthority authority = new SimpleGrantedAuthority(PREFIX_ROLE + rol.getName());
            authorities.add(authority);
        });
        return authorities;
    }
}
