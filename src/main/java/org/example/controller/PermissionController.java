package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.ApiResponse;
import org.example.dto.request.PermissionRequest;
import org.example.dto.response.PermissionResponse;
import org.example.service.PermissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@Slf4j
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping
    private ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest permissionRequest) {
        return ApiResponse.<PermissionResponse>builder()
                .code(200)
                .message("Created permission is success")
                .data(permissionService.create(permissionRequest))
                .build();
    }

    @GetMapping
    private ApiResponse<List<PermissionResponse>> getAll(){
        List<PermissionResponse> list = permissionService.getAll();
        return ApiResponse.<List<PermissionResponse>>builder()
                .code(200)
                .message("Find all permission is success")
                .data(list)
                .build();
    }

    @DeleteMapping("/{id}")
    private ApiResponse<Void> delete(@PathVariable Integer id) {
        boolean deleted = permissionService.delete(id);
        return ApiResponse.<Void>builder()
                .code(deleted ? 200 : 404)
                .message(deleted ? "Delete permission is successful" : "Permission not found")
                .build();
    }
}
