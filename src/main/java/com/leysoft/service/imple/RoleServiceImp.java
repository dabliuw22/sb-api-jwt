
package com.leysoft.service.imple;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leysoft.entity.Role;
import com.leysoft.repository.RoleRepository;
import com.leysoft.service.inter.RoleService;

@Service
public class RoleServiceImp implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    @Transactional(
            readOnly = true)
    public Role findById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(
            readOnly = true)
    public List<Role> findAll() {
        return (List<Role>) roleRepository.findAll();
    }

    @Override
    @Transactional
    public boolean update(Role role) {
        return roleRepository.save(role) != null;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        boolean deleted = false;
        Role role = findById(id);
        if (role != null) {
            roleRepository.deleteById(id);
            deleted = true;
        }
        return deleted;
    }
}
