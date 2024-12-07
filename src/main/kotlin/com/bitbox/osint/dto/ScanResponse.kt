package com.bitbox.osint.dto


import java.time.LocalDateTime

data class ScanResponse(
    val id: String,               // Scan ID
    val domain: String,           // Target domain
    val tool: String,             // Tool used
    val status: String,           // Status ("in_progress", "completed", "failed")
    val startTime: LocalDateTime, // Start time
    val endTime: LocalDateTime?   // End time (nullable if still in progress)
)
