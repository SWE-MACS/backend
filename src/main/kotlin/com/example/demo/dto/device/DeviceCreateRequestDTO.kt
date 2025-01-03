package com.example.demo.dto.device

import com.example.demo.model.DeviceCategory
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class DeviceCreateRequestDTO(
    val name: String,
    val nickname: String,
    val category: DeviceCategory,
    val extraFunction: String,
)