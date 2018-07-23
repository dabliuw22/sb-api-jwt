
package com.leysoft.service.inter;

import java.util.List;

import com.leysoft.entity.SimpleUser;

public interface SimpleUserService {

    public SimpleUser save(SimpleUser user);

    public SimpleUser findById(Long id);

    public SimpleUser findByUsername(String username);

    public List<SimpleUser> findAll();

    public boolean update(SimpleUser user);

    public boolean deleteById(Long id);
}
