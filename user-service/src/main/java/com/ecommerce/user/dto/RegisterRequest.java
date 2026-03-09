package com.ecommerce.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String first_name;

    @NotBlank(message = "Last name is required")
    private String last_name;

    public RegisterRequest() {}

    public String getEmail()      { return email; }
    public String getPassword()   { return password; }
    public String getFirstName()  { return first_name; }
    public String getLastName()   { return last_name; }

    public void setEmail(String email)           { this.email = email; }
    public void setPassword(String password)     { this.password = password; }
    public void setFirstName(String first_name)  { this.first_name = first_name; }
    public void setLastName(String last_name)    { this.last_name = last_name; }
}