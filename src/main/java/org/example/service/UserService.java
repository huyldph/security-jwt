package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.UserRequest;
import org.example.dto.response.UserResponse;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.exceptions.AppException;
import org.example.exceptions.ErrorCode;
import org.example.mapper.UserMapper;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserMapper userMapper;

    //Thoả mãn đk mới gọi method
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAll(){
        log.info("In method get users");
        return userRepository.findAll();
    }

    //gọi method -> kiểm tra đk ? -> kết quả : dừng
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUserById(Integer id) {
        log.info("In method get user by id");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.userToUserResponse(user);
    }

    public User save(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTS);
        }

        User user = userMapper.userRequestToUser(userRequest);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role role = Role.builder().name("USER").description("user role").build();
        roleRepository.save(role);
        roles.add(role);

        user.setRoles(roles);
        return userRepository.save(user);
    }

    public User update(Integer id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateUser(user, userRequest);
        return userRepository.save(user);
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext(); //Save current login information
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_EXISTS)
        );

        return userMapper.userToUserResponse(user);
    }
}
