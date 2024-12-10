package com.bitbox.osint.websocket

import com.bitbox.osint.client.HarvesterWebSocketClient
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

private val logger = KotlinLogging.logger {}

@Component
class ChatHandler(
    private val harvesterWebSocketClient: HarvesterWebSocketClient,
    private val objectMapper: ObjectMapper
) : TextWebSocketHandler() {

    private val sessions = mutableSetOf<WebSocketSession>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)
        logger.info { "Connection established: ${session.id}" }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        logger.info { "Message received from React: ${message.payload}" }
        try {
            val payload = parsePayload(message.payload)
            val domain = payload["domain"] as? String ?: throw IllegalArgumentException("Missing 'domain'")
            val tool = payload["tool"] as? String ?: "all"

            session.sendMessage(TextMessage("Scan request forwarded successfully"))
        } catch (e: Exception) {
            logger.error { "Error handling message: ${e.message}" }
            session.sendMessage(TextMessage("Error processing request: ${e.message}"))
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session)
        logger.info { "Connection closed: ${session.id}" }
    }

    fun broadcastMessage(message: String) {
        val textMessage = TextMessage(message)
        sessions.filter { it.isOpen }.forEach { session ->
            try {
                session.sendMessage(textMessage)
            } catch (e: Exception) {
                logger.error { "Error sending message to session ${session.id}: ${e.message}" }
            }
        }
    }

    private fun parsePayload(payload: String): Map<String, Any> {
        return try {
            objectMapper.readValue(payload, Map::class.java) as Map<String, Any>
        } catch (e: Exception) {
            logger.error { "Error parsing payload: ${e.message}" }
            throw IllegalArgumentException("Invalid payload format")
        }
    }
}
