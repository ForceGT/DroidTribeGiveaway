package dev.gtxtreme.giveaway

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.net.InetAddress
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.*

fun main(args: Array<String>) {

    // For cloud deployment: use HOST environment variable if available
    // Otherwise, get the local IP address for the QR code URL
    val deployedHost = System.getenv("HOST")
    val localIpAddress = InetAddress.getLocalHost().hostAddress

    // Priority order for host:
    // 1. Command line argument (if provided)
    // 2. HOST environment variable (for cloud deployment)
    // 3. Local IP address (for local development)
    val qrCodeHost = when {
        args.isNotEmpty() -> args[0]
        deployedHost != null -> deployedHost
        else -> localIpAddress
    }

    // For cloud deployment, we might not need to show the port in the URL
    val portSuffix = if (System.getenv("PORT") != null && System.getenv("HOST") != null) "" else ":$SERVER_PORT"

    println("Server will be accessible at: http://$qrCodeHost$portSuffix")
    println("QR code will use URL: http://$qrCodeHost$portSuffix/enterRoom")

    // Use "0.0.0.0" to bind to all available network interfaces
    // This makes the server accessible from other machines
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0"){
        install(ContentNegotiation) { json() }
        install(WebSockets) {
            pingPeriod = 15.seconds
            timeout = 15.seconds
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
        routing {
            // WebSocket endpoint for real-time updates
            webSocket("/updates") {
                try {
                    RoomUtils.addSession(this)
                    // Keep the connection open
                    for (frame in incoming) {
                        // Process incoming frames if needed
                    }
                } catch (e: Exception) {
                    println("Error in WebSocket: ${e.message}")
                } finally {
                    RoomUtils.removeSession(this)
                }
            }

            get("/") {
                val url = "http://$qrCodeHost$portSuffix/enterRoom"
                val qrCodeImage = QrCodeUtils.generateQrCode(url)
                val qrCodeBase64 = java.util.Base64.getEncoder().encodeToString(qrCodeImage)

                // Use the kotlinx.html template
                call.respondText(
                    renderHomePage(qrCodeHost + portSuffix, qrCodeBase64),
                    ContentType.Text.Html
                )
            }
            get("/enterRoom") {
                // Use the kotlinx.html template
                call.respondText(
                    renderEnterRoomPage(),
                    ContentType.Text.Html
                )
            }
            post("/register") {
                val params = call.receiveParameters()
                val name = params["name"] ?: return@post call.respond(HttpStatusCode.BadRequest)

                // Call the suspend function
                val userId = RoomUtils.addUserToRoom(name)
                call.respondRedirect("/user/$userId")
            }

            get("/user/{id}") {
                val users = RoomUtils.getUsers()
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null || !users.containsKey(id)) {
                    call.respondText("User not found", status = HttpStatusCode.NotFound)
                    return@get
                }

                val userName = users[id]
                // Use the kotlinx.html template
                call.respondText(
                    renderUserPage(userName!!, id),
                    ContentType.Text.Html
                )
            }
        }
    }
        .start(wait = true)
}
