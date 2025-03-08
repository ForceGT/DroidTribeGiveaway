package dev.gtxtreme.giveaway

import io.ktor.websocket.*
import kotlinx.coroutines.channels.broadcast
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class UserUpdate(
    val users: Map<Int, String>
)

object RoomUtils {
    // Store registered users
    private var users = mutableMapOf<Int, String>()

    // Store active WebSocket sessions
    private val sessions = mutableListOf<WebSocketSession>()

    // Add a session
    suspend fun addSession(session: WebSocketSession) {
        sessions.add(session)
        // Send current users to the new session
        val update = UserUpdate(users)
        session.send(Frame.Text(Json.encodeToString(update)))
    }

    // Remove a session
    fun removeSession(session: WebSocketSession) {
        sessions.remove(session)
    }

    // Notify all sessions about user updates
    private suspend fun notifySessions() {
        val update = UserUpdate(users)
        val updateJson = Json.encodeToString(update)
        sessions.forEach { session ->
            try {
                session.send(Frame.Text(updateJson))
            } catch (e: Exception) {
                // Remove failed sessions
                sessions.remove(session)
            }
        }
    }

    suspend fun addUserToRoom(name: String): Int {
        val userId = users.size + 1  // Assign a new unique number (starting from 1)
        users[userId] = name
        notifySessions()
        return userId
    }

    fun getUsers(): Map<Int, String> = users
}
