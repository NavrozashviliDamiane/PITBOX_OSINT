package com.bitbox.osint.controller


import com.bitbox.osint.dto.ScanRequest
import com.bitbox.osint.dto.ScanResponse
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

    @PostMapping("/amass")
    fun initiateAmassScan(@RequestBody scanRequest: ScanRequest): Any {
        logger.info { "Received request to initiate Amass scan for domain: ${scanRequest.domain}" }
        return try {
            val command = listOf(
                "docker", "exec", "amass-container", // Ensure the container name is correct
                "amass", "enum", "-d", scanRequest.domain
            )

            val process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .start()

            val output = BufferedReader(InputStreamReader(process.inputStream)).use { it.readText() }
            process.waitFor()

            if (process.exitValue() == 0) {
                logger.info { "Amass scan completed for domain: ${scanRequest.domain}" }
                ResponseEntity.ok(mapOf("status" to "completed", "domain" to scanRequest.domain, "output" to output))
            } else {
                logger.error { "Amass scan failed for domain: ${scanRequest.domain}" }
                ResponseEntity.internalServerError().body(
                    mapOf("status" to "failed", "domain" to scanRequest.domain, "error" to "Amass scan failed")
                )
            }
        } catch (e: Exception) {
            return  e;
    }}
}
