package com.bitbox.osint.dto


import java.time.LocalDateTime

data class ScanResponse(
    val id: String,
    val domain: String,
    val tool: String,
    val status: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val error: String
)
