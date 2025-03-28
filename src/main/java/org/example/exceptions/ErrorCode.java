package org.example.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_EXISTS(1000, "User already exists", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(1002, "User not found", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_CODED(9999, "Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSWORD_LENGTH(1003, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1004, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1005, "You do not have permission", HttpStatus.FORBIDDEN),
    ;
    private final Integer code;
    private final String message;
    private final HttpStatusCode httpStatusCode;
}
