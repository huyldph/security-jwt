package org.example.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.ApiResponse;
import org.example.dto.request.UserRequest;
import org.example.dto.response.UserResponse;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable("id") Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<User> getAllUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return userService.getAll();
    }

    @PostMapping("")
    public ApiResponse<User> saveUser(@RequestBody @Valid UserRequest userRequest) {

        return userMapper.userToApiResponse(userService.save(userRequest), 200, "User created successfully");
    }

    @PutMapping("{id}")
    public ApiResponse<User> updateUser(@PathVariable("id") Integer id, @RequestBody @Valid UserRequest userRequest) {
        return userMapper.userToApiResponse(userService.update(id, userRequest), 200, "User updated successfully");
    }
}
