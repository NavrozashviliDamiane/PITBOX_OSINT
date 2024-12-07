package com.bitbox.osint.controller

import com.bitbox.osint.domain.ScanResult
import com.bitbox.osint.service.mapper.ScanResultService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/scan-results")
class ScanResultController(
    private val scanResultService: ScanResultService
) {

    @GetMapping
    fun getAllScanResults(): ResponseEntity<List<ScanResult>> {
        val results = scanResultService.getAllScanResults()
        return ResponseEntity.ok(results)
    }
}
