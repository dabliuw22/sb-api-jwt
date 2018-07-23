
package com.leysoft.service.imple;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leysoft.entity.SimpleUser;
import com.leysoft.repository.SimpleUserRepository;
import com.leysoft.service.inter.SimpleUserService;

@Service
public class SimpleUserServiceImp implements SimpleUserService {

    @Autowired
    private SimpleUserRepository simpleUserRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public SimpleUser save(SimpleUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return simpleUserRepository.save(user);
    }

    @Override
    @Transactional(
            readOnly = true)
    public SimpleUser findById(Long id) {
        return simpleUserRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(
            readOnly = true)
    public SimpleUser findByUsername(String username) {
        return simpleUserRepository.findByUsername(username);
    }

    @Override
    @Transactional(
            readOnly = true)
    public List<SimpleUser> findAll() {
        return (List<SimpleUser>) simpleUserRepository.findAll();
    }

    @Override
    @Transactional
    public boolean update(SimpleUser user) {
        return simpleUserRepository.save(user) != null;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        boolean deleted = false;
        SimpleUser user = findById(id);
        if (user != null) {
            simpleUserRepository.deleteById(id);
            deleted = true;
        }
        return deleted;
    }
}
