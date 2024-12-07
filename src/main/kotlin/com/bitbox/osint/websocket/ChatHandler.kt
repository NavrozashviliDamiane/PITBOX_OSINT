package com.bitbox.osint.websocket

import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

private val logger = KotlinLogging.logger {}

@Component
class ChatHandler : TextWebSocketHandler() {

    private val sessions = mutableListOf<WebSocketSession>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)
        logger.info { "Connection established: ${session.id}" }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        logger.info { "Message received: ${message.payload}" }
        session.sendMessage(TextMessage("Acknowledged: ${message.payload}"))
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session)
        logger.info { "Connection closed: ${session.id}" }
    }

    fun broadcastMessage(message: String) {
        sessions.forEach { session ->
            if (session.isOpen) {
                session.sendMessage(TextMessage(message))
            }
        }
    }
}
