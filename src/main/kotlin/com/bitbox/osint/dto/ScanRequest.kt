package com.bitbox.osint.dto

data class ScanRequest(
    val domain: String, // Target domain
    val tool: String    // Tool to use ("theHarvester" or "Amass")
)
