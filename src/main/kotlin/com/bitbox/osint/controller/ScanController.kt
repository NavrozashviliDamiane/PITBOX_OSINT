package com.bitbox.osint.controller


import com.bitbox.osint.dto.ScanRequest
import com.bitbox.osint.dto.ScanResponse
import com.ptbox.osint.service.ScanService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/scans")
class ScanController(
    private val scanService: ScanService
) {

    @PostMapping
    fun initiateScan(@RequestBody scanRequest: ScanRequest): ResponseEntity<ScanResponse> {
        logger.info { "Received request to initiate scan for domain: ${scanRequest.domain} using tool: ${scanRequest.tool}" }
        return try {
            val scanResponse = scanService.initiateScan(scanRequest)
            logger.info { "Successfully initiated scan for domain: ${scanRequest.domain}" }
            ResponseEntity.ok(scanResponse)
        } catch (e: IllegalArgumentException) {
            logger.error { "Bad request for scan: ${e.message}" }
            ResponseEntity.badRequest().body(null)
        } catch (e: Exception) {
            logger.error(e) { "Error initiating scan: ${e.message}" }
            ResponseEntity.internalServerError().body(null)
        }
    }

    @GetMapping
    fun getScans(): ResponseEntity<List<ScanResponse>> {
        logger.info { "Received request to retrieve all scans" }
        return try {
            val scans = scanService.getScans()
            logger.info { "Successfully retrieved ${scans.size} scans" }
            ResponseEntity.ok(scans)
        } catch (e: Exception) {
            logger.error(e) { "Error retrieving scans: ${e.message}" }
            ResponseEntity.internalServerError().body(emptyList())
        }
    }
}
