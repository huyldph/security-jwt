package org.example.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private Integer id;

    private String firstName;

    private String lastName;

    @Email(regexp = ".+@.+\\.[a-z]+", message = "Email must be valid")
    private String email;

    private String username;

    @Length(min = 8, message = "PASSWORD_LENGTH")
    private String password;

    private LocalDate dob;

    List<Integer> roles;
}
