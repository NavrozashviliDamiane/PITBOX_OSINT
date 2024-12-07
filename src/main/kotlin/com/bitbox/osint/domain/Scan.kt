package com.bitbox.osint.domain


import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document
data class Scan(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val domain: String,
    val tool: String,
    val status: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null
)
