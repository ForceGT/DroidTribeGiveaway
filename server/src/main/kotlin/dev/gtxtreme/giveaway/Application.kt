package dev.gtxtreme.giveaway

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.net.InetAddress
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {

    // Check for Railway-specific environment variables first
    val railwayUrl = System.getenv("RAILWAY_STATIC_URL") ?: System.getenv("RAILWAY_PUBLIC_DOMAIN")

    // For other cloud deployments: use HOST environment variable if available
    // Otherwise, get the local IP address for the QR code URL
    val deployedHost = System.getenv("HOST")
    val localIpAddress = InetAddress.getLocalHost().hostAddress

    // Determine if we're running on Railway
    val isRailway = railwayUrl != null

    // Priority order for host:
    // 1. Command line argument (if provided)
    // 2. Railway URL (for Railway deployment)
    // 3. HOST environment variable (for other cloud deployments)
    // 4. Local IP address (for local development)
    val qrCodeHost = when {
        args.isNotEmpty() -> args[0]
        isRailway -> railwayUrl
        deployedHost != null -> deployedHost
        else -> localIpAddress
    }

    // For cloud deployment, we might not need to show the port in the URL
    val isCloudDeployed = isRailway || (System.getenv("PORT") != null && System.getenv("HOST") != null)
    val portSuffix = if (isCloudDeployed) "" else ":$SERVER_PORT"

    // Use HTTPS for Railway, HTTP for everything else
    val protocol = if (isRailway) "https://" else "http://"

    // If we're on Railway and the URL already includes the protocol, don't add it again
    val fullHost = if (isRailway && qrCodeHost!!.startsWith("http")) {
        qrCodeHost
    } else {
        "$protocol$qrCodeHost"
    }

    println("Server will be accessible at: $fullHost$portSuffix")
    println("QR code will use URL: $fullHost$portSuffix/enterRoom")

    // Use "0.0.0.0" to bind to all available network interfaces
    // This makes the server accessible from other machines
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0"){
        install(ContentNegotiation) { 
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }

        // Install CORS feature
        install(CORS) {
            anyHost() // Allow requests from any host
            allowMethod(HttpMethod.Get) // Allow GET requests
            allowMethod(HttpMethod.Post) // Allow POST requests
            allowMethod(HttpMethod.Options) // Allow preflight requests
            allowHeader(HttpHeaders.ContentType) // Allow Content-Type header
            allowHeader(HttpHeaders.Authorization) // Allow Authorization header
        }
        // WebSockets removed in favor of polling
        routing {
            // WebSocket endpoint removed in favor of polling

            get("/") {
                RoomUtils.log("Home page requested")
                val url = "$fullHost$portSuffix/enterRoom"
                RoomUtils.log("QR code URL: $url")

                val qrCodeImage = QrCodeUtils.generateQrCode(url)
                val qrCodeBase64 = java.util.Base64.getEncoder().encodeToString(qrCodeImage)
                RoomUtils.log("QR code generated successfully")

                // Use the kotlinx.html template
                call.respondText(
                    renderHomePage(fullHost.removePrefix("http://").removePrefix("https://") + portSuffix, qrCodeBase64),
                    ContentType.Text.Html
                )
                RoomUtils.log("Home page rendered successfully")
            }
            get("/enterRoom") {
                RoomUtils.log("Enter room page requested")
                // Use the kotlinx.html template
                call.respondText(
                    renderEnterRoomPage(),
                    ContentType.Text.Html
                )
                RoomUtils.log("Enter room page rendered successfully")
            }
            post("/register") {
                try {
                    val params = call.receiveParameters()
                    val name = params["name"] ?: return@post call.respond(HttpStatusCode.BadRequest)

                    RoomUtils.log("Registering user: $name")

                    // Call the suspend function
                    val userId = RoomUtils.addUserToRoom(name)

                    // Check if the room is closed
                    if (userId == null) {
                        RoomUtils.log("Room is closed, cannot register user: $name")
                        call.respondText("Sorry, the room is closed for new entries.", status = HttpStatusCode.Forbidden)
                        return@post
                    }

                    RoomUtils.log("User registered with ID: $userId")

                    // Use the full URL for redirection
                    val redirectUrl = "$fullHost$portSuffix/user/$userId"
                    RoomUtils.log("Redirecting to: $redirectUrl")

                    call.respondRedirect(redirectUrl)
                } catch (e: Exception) {
                    RoomUtils.log("Error in registration: ${e.message}")
                    e.printStackTrace()
                    call.respondText("An error occurred during registration: ${e.message}", status = HttpStatusCode.InternalServerError)
                }
            }

            get("/user/{id}") {
                val users = RoomUtils.getUsers()
                val id = call.parameters["id"]?.toIntOrNull()
                RoomUtils.log("User page requested for ID: $id")

                if (id == null || !users.containsKey(id)) {
                    RoomUtils.log("User not found: $id")
                    call.respondText("User not found", status = HttpStatusCode.NotFound)
                    return@get
                }

                val user = users[id]
                val isKickedOut = RoomUtils.isUserKickedOut(id)
                RoomUtils.log("Rendering user page for: ${user?.name} (ID: $id), kicked out: $isKickedOut")

                // Use the kotlinx.html template
                call.respondText(
                    renderUserPage(user?.name ?: "Unknown", id, isKickedOut),
                    ContentType.Text.Html
                )
                RoomUtils.log("User page rendered successfully")
            }

            // API endpoint to select a winner
            post("/api/select-winner") {
                RoomUtils.log("Select winner request received")
                try {
                    val winner = RoomUtils.selectWinner()
                    if (winner != null) {
                        RoomUtils.log("Winner selected: $winner")
                        call.respond(WinnerResponse(success = true, winner = winner))
                    } else {
                        RoomUtils.log("No users to select a winner from")
                        call.respond(HttpStatusCode.BadRequest, WinnerResponse(success = false, error = "No users to select a winner from"))
                    }
                } catch (e: Exception) {
                    RoomUtils.log("Error selecting winner: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, WinnerResponse(success = false, error = "An error occurred: ${e.message}"))
                }
            }

            // API endpoint to reset the winner
            post("/api/reset-winner") {
                RoomUtils.log("Reset winner request received")
                try {
                    RoomUtils.resetWinner()
                    RoomUtils.log("Winner reset successfully")
                    call.respond(WinnerResponse(success = true))
                } catch (e: Exception) {
                    RoomUtils.log("Error resetting winner: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, WinnerResponse(success = false, error = "An error occurred: ${e.message}"))
                }
            }

            // API endpoint to get the current list of users
            get("/api/users") {
                RoomUtils.log("API request received: /api/users")
                try {
                    val users = RoomUtils.getUsers()
                    val winner = RoomUtils.getWinner()
                    val previousWinners = RoomUtils.getPreviousWinners()
                    val isRoomClosed = RoomUtils.isRoomClosed()
                    val isGiveawayRunning = RoomUtils.isGiveawayRunning()
                    RoomUtils.log("Returning users: $users, winner: $winner, previousWinners: $previousWinners, isRoomClosed: $isRoomClosed, isGiveawayRunning: $isGiveawayRunning")

                    // Create the response object with all fields
                    val response = UserUpdate(
                        users = users,
                        winner = winner,
                        previousWinners = previousWinners,
                        isRoomClosed = isRoomClosed,
                        isGiveawayRunning = isGiveawayRunning
                    )
                    RoomUtils.log("Response object created: $response")

                    // Respond with the user list, winner, and previous winners
                    call.respond(response)
                    RoomUtils.log("API response sent successfully")
                } catch (e: Exception) {
                    RoomUtils.log("Error in API endpoint: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, "An error occurred: ${e.message}")
                }
            }

            // API endpoint to close the room
            post("/api/close-room") {
                RoomUtils.log("Close room request received")
                try {
                    RoomUtils.closeRoom()
                    call.respond(WinnerResponse(success = true))
                } catch (e: Exception) {
                    RoomUtils.log("Error closing room: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, WinnerResponse(success = false, error = "An error occurred: ${e.message}"))
                }
            }

            // API endpoint to open the room
            post("/api/open-room") {
                RoomUtils.log("Open room request received")
                try {
                    RoomUtils.openRoom()
                    call.respond(WinnerResponse(success = true))
                } catch (e: Exception) {
                    RoomUtils.log("Error opening room: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, WinnerResponse(success = false, error = "An error occurred: ${e.message}"))
                }
            }

            // API endpoint to start the giveaway animation
            post("/api/start-giveaway") {
                RoomUtils.log("Start giveaway request received")
                try {
                    RoomUtils.startGiveaway()
                    call.respond(WinnerResponse(success = true))
                } catch (e: Exception) {
                    RoomUtils.log("Error starting giveaway: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, WinnerResponse(success = false, error = "An error occurred: ${e.message}"))
                }
            }

            // API endpoint to stop the giveaway animation
            post("/api/stop-giveaway") {
                RoomUtils.log("Stop giveaway request received")
                try {
                    RoomUtils.stopGiveaway()
                    call.respond(WinnerResponse(success = true))
                } catch (e: Exception) {
                    RoomUtils.log("Error stopping giveaway: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, WinnerResponse(success = false, error = "An error occurred: ${e.message}"))
                }
            }

            // API endpoint to kick out a user
            post("/api/kick-out-user/{id}") {
                val userId = call.parameters["id"]?.toIntOrNull()
                RoomUtils.log("Kick out user request received for user ID: $userId")

                if (userId == null) {
                    RoomUtils.log("Invalid user ID: $userId")
                    call.respond(HttpStatusCode.BadRequest, WinnerResponse(success = false, error = "Invalid user ID"))
                    return@post
                }

                try {
                    val success = RoomUtils.kickOutUser(userId)
                    if (success) {
                        call.respond(WinnerResponse(success = true))
                    } else {
                        call.respond(HttpStatusCode.NotFound, WinnerResponse(success = false, error = "User not found"))
                    }
                } catch (e: Exception) {
                    RoomUtils.log("Error kicking out user: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, WinnerResponse(success = false, error = "An error occurred: ${e.message}"))
                }
            }
        }
    }
        .start(wait = true)
}
