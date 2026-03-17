package com.ricky.controller;

import com.ricky.pojo.UserDTO;
import com.ricky.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @DubboReference(
            version = "1.0",
            timeout = 2000,
            retries = 2,
            loadbalance = "roundrobin"
    )
    private UserService userService;

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping
    public List<UserDTO> listUsers() {
        return userService.listUsers();
    }

    @PostMapping
    public String create(@RequestParam String name) {
        return userService.createUser(name);
    }

}