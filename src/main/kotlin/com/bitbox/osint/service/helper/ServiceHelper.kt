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
            "python",
            "theHarvester.py",
            "-d", scanRequest.domain,
            "-b", "all"
        )
        logger.info { "Executing command: ${command.joinToString(" ")}" }

        val process = ProcessBuilder(command)
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()

        if (exitCode != 0) {
            logger.error { "Command failed with exit code $exitCode for domain: ${scanRequest.domain}" }
            throw RuntimeException("Command execution failed for ${scanRequest.domain}")
        }

        logger.info { "Command executed successfully for domain: ${scanRequest.domain}" }
        logger.debug { "Command output:\n$output" }

        return output
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
