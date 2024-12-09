package com.bitbox.osint.client

import com.bitbox.osint.domain.ScanResult
import com.bitbox.osint.dto.ScanResultResponse
import com.bitbox.osint.repository.ScanResultRepository
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class HarvesterWebSocketClient(
    private val scanResultRepository: ScanResultRepository,
    private val objectMapper: ObjectMapper // Inject ObjectMapper for better testability
) {

    private val logger = LoggerFactory.getLogger(HarvesterWebSocketClient::class.java)
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect() {
        val request = Request.Builder().url("ws://harvester-container:5000").build()
        webSocket = client.newWebSocket(request, createWebSocketListener())
    }

    private fun createWebSocketListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                logger.info("Connected to the Flask WebSocket server.")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                logger.info("Received message: $text")
                try {
                    val scanResultResponse = parseMessage(text)
                    handleScanResult(scanResultResponse)
                } catch (e: Exception) {
                    logger.error("Error processing message: ${e.message}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                logger.error("WebSocket error: ${t.message}")
            }
        }
    }

    private fun parseMessage(message: String): ScanResultResponse {
        return try {
            objectMapper.readValue(message, ScanResultResponse::class.java)
        } catch (e: Exception) {
            logger.error("Failed to parse WebSocket message: ${e.message}")
            throw IllegalArgumentException("Invalid message format")
        }
    }

    private fun handleScanResult(scanResult: ScanResultResponse) {
        val scanResultEntity = mapToEntity(scanResult)
        saveScanResult(scanResultEntity)
    }

    private fun mapToEntity(scanResult: ScanResultResponse): ScanResult {
        return ScanResult(
            id = scanResult.scanId,
            scanId = scanResult.scanId,
            emails = scanResult.emails,
            subdomains = scanResult.subdomains,
            ipAddresses = scanResult.ipAddresses,
            hostnames = scanResult.hostnames,
            urls = scanResult.urls,
            banners = scanResult.banners,
            ports = scanResult.ports
        )
    }

    private fun saveScanResult(scanResult: ScanResult) {
        try {
            scanResultRepository.save(scanResult)
            logger.info("Saved ScanResult to database: $scanResult")
        } catch (e: Exception) {
            logger.error("Error saving ScanResult to database: ${e.message}")
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Client disconnecting")
        logger.info("Disconnected from the Flask WebSocket server.")
    }
}
