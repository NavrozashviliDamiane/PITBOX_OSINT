package com.bitbox.osint.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "scanResults")
data class ScanResult(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val scanId: String,
    val emails: List<String> = emptyList(),
    val subdomains: List<String> = emptyList(),
    val ipAddresses: List<String> = emptyList(),
    val hostnames: List<String> = emptyList(),
    val urls: List<String> = emptyList(),
    val banners: List<String> = emptyList(),
    val ports: List<Int> = emptyList()
)
