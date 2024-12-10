package com.bitbox.osint.controller


import com.bitbox.osint.dto.ScanRequest
import com.bitbox.osint.dto.ScanResponse
import com.bitbox.osint.validation.DomainValidation
import com.ptbox.osint.service.ScanService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import mu.KotlinLogging
import java.io.BufferedReader
import java.io.InputStreamReader

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/scans")
class ScanController(
    private val scanService: ScanService,
    private val domainValidation: DomainValidation
) {

    @PostMapping
    fun initiateScan(@RequestBody scanRequest: ScanRequest): ResponseEntity<String> {
        logger.info { "Received request to initiate scan for domain: ${scanRequest.domain} using tool: ${scanRequest.tool}" }
        return try {
            domainValidation.validate(scanRequest.domain)

            val scanResponse = scanService.initiateScan(scanRequest)
            logger.info { "Successfully initiated scan for domain: ${scanRequest.domain}" }
            ResponseEntity.ok("Scan successfully initiated for domain: ${scanRequest.domain}")
        } catch (e: IllegalArgumentException) {
            logger.error { "Bad request for scan: ${e.message}" }
            ResponseEntity.badRequest().body(e.message)
        } catch (e: Exception) {
            logger.error(e) { "Error initiating scan: ${e.message}" }
            ResponseEntity.internalServerError().body("An unexpected error occurred: ${e.message}")
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
