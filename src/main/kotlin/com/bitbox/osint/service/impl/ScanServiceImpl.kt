package com.bitbox.osint.service.impl


import com.bitbox.osint.dto.ScanRequest
import com.bitbox.osint.dto.ScanResponse
import com.bitbox.osint.repository.ScanRepository
import com.bitbox.osint.repository.ScanResultRepository
import com.bitbox.osint.service.WebSocketService
import com.bitbox.osint.service.helper.ServiceHelper
import com.bitbox.osint.service.mapper.MapperService
import com.bitbox.osint.websocket.ChatHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.ptbox.osint.service.ScanService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val helper = ServiceHelper()

private val logger = KotlinLogging.logger {}

@Service
class ScanServiceImpl(
    private val scanRepository: ScanRepository,
    private val scanResultRepository: ScanResultRepository,
    private val mapperService: MapperService,
    private val chatHandler: ChatHandler,
    private val objectMapper: ObjectMapper
) : ScanService {

    override fun initiateScan(scanRequest: ScanRequest): ScanResponse {
        val scan = helper.createInitialScan(scanRequest)
        scanRepository.save(scan)

        Thread {
            try {
                chatHandler.broadcastMessage("Scan started for ${scanRequest.domain}")

                val output = helper.executeScanCommand(scanRequest)
                val scanResult = helper.parseScanResult(scan.id, scanRequest.domain, output)
                scanResultRepository.save(scanResult)

                scanRepository.save(scan.copy(status = "completed", endTime = LocalDateTime.now()))

                chatHandler.broadcastMessage("Scan completed successfully for ${scanRequest.domain}")

                Thread.sleep(500)

                val combinedResult = mapOf(
                    "id" to scan.id,
                    "domain" to scan.domain,
                    "tool" to scan.tool,
                    "status" to scan.status,
                    "startTime" to scan.startTime,
                    "endTime" to scan.endTime,
                    "result" to scanResult
                )

                val resultJson = objectMapper.writeValueAsString(combinedResult)
                chatHandler.broadcastMessage(resultJson)
            } catch (e: Exception) {
                val errorMessage = objectMapper.writeValueAsString(mapOf(
                    "domain" to scanRequest.domain,
                    "status" to "failed",
                    "error" to e.message
                ))
                chatHandler.broadcastMessage(errorMessage)
            }
        }.start()

        return mapperService.mapScanToResponse(scan)
    }

    override fun getScans(): List<ScanResponse> {
        logger.info { "Fetching all scans from repository" }
        val scans = scanRepository.findAll()
        logger.info { "Retrieved ${scans.size} scans" }
        return scans.map { mapperService.mapScanToResponse(it) }
    }
}
