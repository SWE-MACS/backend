package com.example.demo.dto.ai

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AIMessageResponse(
    val sql: String,
    val message: String,
    val context: List<ContextDTO>
)
