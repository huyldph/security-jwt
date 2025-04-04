package org.example.mapper;

import org.example.dto.request.PermissionRequest;
import org.example.dto.response.PermissionResponse;
import org.example.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    Permission permissionRequestToPermission(PermissionRequest permissionRequest);

    PermissionResponse permissionToPermissionResponse(Permission permission);
}
