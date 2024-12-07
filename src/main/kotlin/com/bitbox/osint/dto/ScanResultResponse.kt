package com.bitbox.osint.dto

data class ScanResultResponse(
    val scanId: String,              // Scan ID
    val emails: List<String>,        // Discovered email addresses
    val subdomains: List<String>,    // Discovered subdomains
    val ipAddresses: List<String>,  // Associated IP addresses
    val hostnames: List<String>,    // Hostnames resolved
    val urls: List<String>,         // URLs identified
    val banners: List<String>,      // Service banners
    val ports: List<Int>            // Open ports
)
