package org.example.service;

import org.example.dto.request.UserRequest;
import org.example.dto.response.UserResponse;
import org.example.entity.User;
import org.example.exceptions.AppException;
import org.example.exceptions.ErrorCode;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public UserResponse getUserById(Integer id) {
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

        HashSet<String> roles = new HashSet<>();
        roles.add("USER");

        user.setRoles(roles);
        return userRepository.save(user);
    }

    public User update(Integer id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateUser(user, userRequest);
        return userRepository.save(user);
    }
}
