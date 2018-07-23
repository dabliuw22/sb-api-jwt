
package com.leysoft.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leysoft.entity.SimpleUser;
import com.leysoft.service.inter.RoleService;
import com.leysoft.service.inter.SimpleUserService;

@RestController
@RequestMapping(
        value = {
            "/user"
        })
public class UserController {

    @Autowired
    private SimpleUserService simpleUserService;

    @Autowired
    private RoleService roleService;

    @PostMapping(
            value = {
                "/add"
            })
    public ResponseEntity<String> add(@Valid @RequestBody SimpleUser user) {
        SimpleUser newUser = simpleUserService.save(user);
        String message = "Error";
        if (newUser != null) {
            message = newUser.getUsername() + " added";
            newUser.setRoles(roleService.findAll());
            simpleUserService.update(newUser);
        }
        return ResponseEntity.ok(message);
    }

    @GetMapping(
            value = {
                "/hello"
            })
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello, user");
    }
}
