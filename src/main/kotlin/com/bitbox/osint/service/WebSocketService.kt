package com.bitbox.osint.service

import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Service
class WebSocketService {
    private val sessions = ConcurrentHashMap<String, WebSocketSession>()

    fun addSession(scanId: String, session: WebSocketSession) {
        sessions[scanId] = session
    }

    fun removeSession(scanId: String) {
        sessions.remove(scanId)
    }

    fun sendUpdate(scanId: String, message: String) {
        sessions[scanId]?.sendMessage(TextMessage(message))
    }
}
