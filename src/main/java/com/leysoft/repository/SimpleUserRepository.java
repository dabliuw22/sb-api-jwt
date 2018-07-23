
package com.leysoft.repository;

import org.springframework.data.repository.CrudRepository;

import com.leysoft.entity.SimpleUser;

public interface SimpleUserRepository extends CrudRepository<SimpleUser, Long> {

    public SimpleUser findByUsername(String username);
}
