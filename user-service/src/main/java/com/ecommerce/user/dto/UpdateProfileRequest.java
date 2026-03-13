package com.ecommerce.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class UpdateProfileRequest {
    @NotBlank(message = "First name is required")
    private String first_name;

    @NotBlank(message = "Last name is required")
    private String last_name;

    public UpdateProfileRequest() {}

    public String getFirstName() { return first_name; }
    public String getLastName()  { return last_name; }

    public void setFirstName(String first_name) { this.first_name = first_name; }
    public void setLastName(String last_name)   { this.last_name = last_name; }
}