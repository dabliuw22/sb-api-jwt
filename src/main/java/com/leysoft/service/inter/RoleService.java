
package com.leysoft.service.inter;

import java.util.List;

import com.leysoft.entity.Role;

public interface RoleService {

    public Role save(Role role);

    public Role findById(Long id);

    public List<Role> findAll();

    public boolean update(Role role);

    public boolean delete(Long id);
}
