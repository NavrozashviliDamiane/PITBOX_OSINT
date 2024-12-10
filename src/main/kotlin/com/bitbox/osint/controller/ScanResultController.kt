package com.bitbox.osint.controller

import com.bitbox.osint.domain.ScanResult
import com.bitbox.osint.service.mapper.ScanResultService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

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

    @GetMapping("/{id}")
    fun getScanResultById(@PathVariable id: UUID): ResponseEntity<ScanResult> {
        val result = scanResultService.getScanResultById(id)
        return if (result != null) {
            ResponseEntity.ok(result)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
