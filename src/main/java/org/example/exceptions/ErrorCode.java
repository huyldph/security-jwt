package org.example.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_EXISTS(400, "User already exists"),
    USER_NOT_FOUND(404, "User not found"),
    UNCATEGORIZED_CODED(500, "Something went wrong"),
    PASSWORD_LENGTH(400, "Password must be at least 8 characters"),
    UNAUTHENTICATED(401, "Unauthenticated"),
    ;
    private final Integer code;
    private final String message;
}
