package de.riednic.taskflow.auth.controller

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(

    @NotBlank
    @Email
    @JsonProperty("email")
    val email: String,

    @NotBlank
    @JsonProperty("password")
    val password: String,
)
