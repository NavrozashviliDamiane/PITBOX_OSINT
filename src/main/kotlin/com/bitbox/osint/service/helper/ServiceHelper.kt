package com.bitbox.osint.service.helper

import com.bitbox.osint.domain.Scan
import com.bitbox.osint.domain.ScanResult
import com.bitbox.osint.dto.ScanRequest
import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.UUID

private val logger = KotlinLogging.logger {}

class ServiceHelper {

    fun createInitialScan(scanRequest: ScanRequest): Scan {
        return Scan(
            id = UUID.randomUUID().toString(),
            domain = scanRequest.domain,
            tool = scanRequest.tool,
            status = "in_progress",
            startTime = LocalDateTime.now()
        )
    }

    fun executeScanCommand(scanRequest: ScanRequest): String {
        val command = listOf(
            "python-env",  // Ensure this matches the Dockerfile setup
            "/opt/theHarvester/theHarvester.py", // Path to theHarvester
            "-d", scanRequest.domain,
            "-b", "all"
        )

        logger.info { "Executing command: ${command.joinToString(" ")}" }

        return try {
            val process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()

            if (exitCode != 0) {
                logger.error { "Scan command failed with exit code $exitCode" }
                throw RuntimeException("Command execution failed with exit code $exitCode: $output")
            }

            output
        } catch (e: Exception) {
            logger.error(e) { "Error executing scan command" }
            throw RuntimeException("Error executing scan command: ${e.message}", e)
        }
    }

    fun parseScanResult(scanId: String, domain: String, output: String): ScanResult {
        val emails = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}").findAll(output).map { it.value }.toList()
        val subdomains = Regex("(?i)([a-zA-Z0-9.-]+\\.${domain})").findAll(output).map { it.value }.toList()
        val ipAddresses = Regex("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b").findAll(output).map { it.value }.toList()
        val urls = Regex("(https?://[\\w./-]+)").findAll(output).map { it.value }.toList()

        return ScanResult(
            scanId = scanId,
            emails = emails,
            subdomains = subdomains,
            ipAddresses = ipAddresses,
            hostnames = subdomains.distinct(),
            urls = urls,
            banners = emptyList(),
            ports = emptyList()
        )
    }
}
