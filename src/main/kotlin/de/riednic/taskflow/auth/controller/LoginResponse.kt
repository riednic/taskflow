package de.riednic.taskflow.auth.controller

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginResponse(

    @JsonProperty("token")
    val token: String,

    @JsonProperty("expiresIn")
    val expiresIn: Int,
)
