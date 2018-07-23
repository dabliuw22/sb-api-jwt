
package com.leysoft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.leysoft.entity.Role;
import com.leysoft.service.inter.RoleService;

@SpringBootApplication
public class SbApiJwtApplication implements CommandLineRunner {

    @Autowired
    private RoleService roleService;

    public static void main(String[] args) {
        SpringApplication.run(SbApiJwtApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Role roleUser = new Role();
        roleUser.setName("USER");
        roleService.save(roleUser);
        Role roleAdmin = new Role();
        roleAdmin.setName("ADMIN");
        roleService.save(roleAdmin);
    }
}
