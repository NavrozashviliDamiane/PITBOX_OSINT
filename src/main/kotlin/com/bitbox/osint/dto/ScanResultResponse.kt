package com.bitbox.osint.dto

data class ScanResultResponse(
    val scanId: String,
    val emails: List<String>,
    val subdomains: List<String>,
    val ipAddresses: List<String>,
    val hostnames: List<String>,
    val urls: List<String>,
    val banners: List<String>,
    val ports: List<Int>
)
