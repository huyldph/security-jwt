package org.example.mapper;

import org.example.dto.request.RoleRequest;
import org.example.dto.response.RoleResponse;
import org.example.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "description", source = "roleRequest.description")
    @Mapping(target = "name", source = "roleRequest.name")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest roleRequest);

    RoleResponse toRoleResponse(Role role);
}
