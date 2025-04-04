package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.ApiResponse;
import org.example.dto.request.RoleRequest;
import org.example.dto.response.RoleResponse;
import org.example.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Slf4j
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    private ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse>builder()
                .code(200)
                .message("Created role is success")
                .data(roleService.create(roleRequest))
                .build();
    }

    @GetMapping
    private ApiResponse<List<RoleResponse>> getAll(){
        List<RoleResponse> list = roleService.getAll();
        return ApiResponse.<List<RoleResponse>>builder()
                .code(200)
                .message("Find all role is success")
                .data(list)
                .build();
    }

    @DeleteMapping("/{id}")
    private ApiResponse<Void> delete(@PathVariable Integer id) {
        boolean deleted = roleService.delete(id);
        return ApiResponse.<Void>builder()
                .code(deleted ? 200 : 404)
                .message(deleted ? "Delete role is successful" : "Role not found")
                .build();
    }
}
