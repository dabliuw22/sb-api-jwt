
package com.leysoft.service.imple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.leysoft.entity.SimpleUser;
import com.leysoft.service.inter.SimpleUserService;
import com.leysoft.util.Util;

@Service
public class SimpleUserDetailService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleUserDetailService.class);

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
                Util.getGrantedAuthorities(simpleUser));
    }
}
