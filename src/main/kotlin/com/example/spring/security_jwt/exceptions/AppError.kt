package com.example.spring.security_jwt.exceptions

import java.util.Date

data class AppError(
    val status: Int,
    val message: String,
    val timestamp: Date = Date(),
)
