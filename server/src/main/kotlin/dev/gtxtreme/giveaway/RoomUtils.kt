package dev.gtxtreme.giveaway

import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

@Serializable
data class User(
    val name: String,
    val uniqueId: String, // Additional identifier for users with the same name
    val kickedOut: Boolean = false // Flag to indicate if the user has been kicked out
)

@Serializable
data class UserUpdate(
    val users: Map<Int, User>,
    val winner: Int? = null, // ID of the winning user, if any
    val previousWinners: List<PreviousWinner> = emptyList(), // List of previous winners
    val isRoomClosed: Boolean = false,
    val isGiveawayRunning: Boolean = false
)

@Serializable
data class PreviousWinner(
    val id: Int,
    val user: User,
    val timestamp: String
)

@Serializable
data class WinnerResponse(
    val success: Boolean,
    val winner: Int? = null,
    val error: String? = null
)

object RoomUtils {
    // Store registered users using a thread-safe map
    private var users = ConcurrentHashMap<Int, User>()

    // Store the current winner, if any
    private var winner: Int? = null

    // Store previous winners
    private val previousWinners = mutableListOf<Pair<Int, User>>()

    // Flag to indicate if the room is closed for new entries
    private var isRoomClosed = false

    // Flag to indicate if the giveaway animation is running
    private var isGiveawayRunning = false

    // Formatter for timestamps in logs
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    // Log with timestamp
    fun log(message: String) {
        val timestamp = LocalDateTime.now().format(formatter)
        println("[$timestamp] $message")
    }

    // Counter for generating unique user IDs
    private var userIdCounter = 0

    suspend fun addUserToRoom(name: String): Int? {
        log("Adding user to room: $name")

        // Check if the room is closed
        if (isRoomClosed) {
            log("Room is closed, cannot add user: $name")
            return null
        }

        // Synchronize the ID assignment to ensure uniqueness
        val userId: Int
        synchronized(this) {
            userIdCounter++
            userId = userIdCounter
        }

        // Generate a unique identifier for this user
        val uniqueId = UUID.randomUUID().toString().substring(0, 8)

        users[userId] = User(name, uniqueId)
        log("User added with ID: $userId, uniqueId: $uniqueId. Total users: ${users.size}")
        log("Current users: $users")
        return userId
    }

    fun getUsers(): Map<Int, User> = users

    fun selectWinner(): Int? {
        // Filter out users who have already won
        val eligibleUsers = users.filter { (id, _) -> 
            previousWinners.none { it.first == id }
        }

        if (eligibleUsers.isEmpty()) {
            log("Cannot select a winner: no eligible users in the room")
            return null
        }

        // Select a random user ID from the list of eligible users
        val userIds = eligibleUsers.keys.toList()
        val randomIndex = Random.nextInt(userIds.size)
        winner = userIds[randomIndex]

        log("Selected winner: $winner (${users[winner]?.name})")
        return winner
    }

    fun getWinner(): Int? = winner

    fun getPreviousWinners(): List<PreviousWinner> {
        return previousWinners.map { (id, user) ->
            PreviousWinner(id, user, LocalDateTime.now().format(formatter))
        }
    }

    fun resetWinner() {
        // Add current winner to previous winners if there is one
        if (winner != null && users.containsKey(winner!!)) {
            val winnerUser = users[winner!!]
            if (winnerUser != null) {
                previousWinners.add(Pair(winner!!, winnerUser))
                log("Added winner $winner to previous winners list")
            }
        }

        winner = null
        log("Winner reset")
    }

    // Get room closure status
    fun isRoomClosed(): Boolean = isRoomClosed

    // Close the room for new entries
    fun closeRoom() {
        isRoomClosed = true
        log("Room closed for new entries")
    }

    // Open the room for new entries
    fun openRoom() {
        isRoomClosed = false
        log("Room opened for new entries")
    }

    // Get giveaway animation status
    fun isGiveawayRunning(): Boolean = isGiveawayRunning

    // Start the giveaway animation
    fun startGiveaway() {
        isGiveawayRunning = true
        log("Giveaway animation started")
    }

    // Stop the giveaway animation
    fun stopGiveaway() {
        isGiveawayRunning = false
        log("Giveaway animation stopped")
    }

    // Kick out a user (mark them as kicked out)
    fun kickOutUser(userId: Int): Boolean {
        if (!users.containsKey(userId)) {
            log("Cannot kick out user: User with ID $userId not found")
            return false
        }

        val user = users[userId]!!
        users[userId] = user.copy(kickedOut = true)
        log("User kicked out: $userId (${user.name})")
        return true
    }

    // Check if a user is kicked out
    fun isUserKickedOut(userId: Int): Boolean {
        return users[userId]?.kickedOut ?: false
    }

}
