
package com.leysoft.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = "/")
public class InitController {

    @GetMapping(
            value = {
                ""
            })
    public ResponseEntity<String> init() {
        return ResponseEntity.ok("Hello, public");
    }
}
