package dev.gtxtreme.giveaway

import kotlinx.html.*
import kotlinx.html.stream.createHTML

/**
 * Renders the home page with QR code and user list
 */
fun renderHomePage(ipAddress: String, qrCodeBase64: String): String {
    return createHTML().html {
        head {
            title("Giveaway Room")
            style {
                +"""
                body {
                    font-family: Arial, sans-serif;
                    max-width: 800px;
                    margin: 0 auto;
                    padding: 20px;
                }
                .container {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 20px;
                }
                .qr-section {
                    flex: 1;
                    min-width: 300px;
                    text-align: center;
                }
                #qr-code-container img {
                    display: block;
                    margin: 0 auto;
                    max-width: 100%;
                }
                .users-section {
                    flex: 1;
                    min-width: 300px;
                }
                .users-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 10px;
                }
                .user-count {
                    color: #666;
                    font-size: 0.9em;
                    font-weight: normal;
                }
                .user-list {
                    border: 1px solid #ddd;
                    border-radius: 5px;
                    padding: 10px;
                    min-height: 200px;
                    max-height: 600px; /* Increased height to show more users */
                    overflow-y: auto; /* Enables vertical scrolling */
                    overflow-x: hidden; /* Prevents horizontal scrolling */
                    scrollbar-width: thin; /* For Firefox */
                    scrollbar-color: #888 #f1f1f1; /* For Firefox */
                }

                /* Scrollbar styling for WebKit browsers (Chrome, Safari, etc.) */
                .user-list::-webkit-scrollbar {
                    width: 8px;
                }

                .user-list::-webkit-scrollbar-track {
                    background: #f1f1f1;
                    border-radius: 4px;
                }

                .user-list::-webkit-scrollbar-thumb {
                    background: #888;
                    border-radius: 4px;
                }

                .user-list::-webkit-scrollbar-thumb:hover {
                    background: #555;
                }
                .user-item {
                    padding: 6px 8px;
                    margin: 3px 0;
                    background-color: #f5f5f5;
                    border-radius: 3px;
                    font-size: 0.95em;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    transition: background-color 0.2s;
                }

                .user-item:hover {
                    background-color: #e9e9e9;
                }
                .user-item.winner {
                    background-color: #ffd700;
                    font-weight: bold;
                }
                .winner-section {
                    margin-top: 20px;
                    text-align: center;
                    display: none; /* Hidden by default, will be shown when room is closed */
                }
                .winner-display {
                    margin: 20px 0;
                    padding: 15px;
                    background-color: #ffd700;
                    border-radius: 5px;
                    display: none;
                }
                .winner-info {
                    display: flex;
                    flex-direction: column;
                    gap: 5px;
                    padding: 10px;
                    background-color: #fff9e6;
                    border-radius: 5px;
                    border: 1px solid #ffd700;
                }
                .winner-name {
                    font-size: 1.5em;
                    font-weight: bold;
                }
                .winner-id {
                    color: #666;
                    font-size: 1em;
                }
                .previous-winners-section {
                    margin-top: 30px;
                    text-align: center;
                    display: none;
                }
                .previous-winners-list {
                    margin-top: 15px;
                    border: 1px solid #ddd;
                    border-radius: 5px;
                    padding: 10px;
                    max-height: 300px;
                    overflow-y: auto;
                    overflow-x: hidden;
                    scrollbar-width: thin;
                    scrollbar-color: #888 #f1f1f1;
                }

                /* Scrollbar styling for WebKit browsers (Chrome, Safari, etc.) */
                .previous-winners-list::-webkit-scrollbar {
                    width: 8px;
                }

                .previous-winners-list::-webkit-scrollbar-track {
                    background: #f1f1f1;
                    border-radius: 4px;
                }

                .previous-winners-list::-webkit-scrollbar-thumb {
                    background: #888;
                    border-radius: 4px;
                }

                .previous-winners-list::-webkit-scrollbar-thumb:hover {
                    background: #555;
                }
                .previous-winner-item {
                    padding: 8px;
                    margin: 5px 0;
                    background-color: #f0f0f0;
                    border-radius: 3px;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    transition: background-color 0.2s;
                }

                .previous-winner-item:hover {
                    background-color: #e4e4e4;
                }
                .previous-winner-info {
                    display: flex;
                    flex-direction: column;
                    gap: 2px;
                }
                .previous-winner-name {
                    font-weight: bold;
                }
                .previous-winner-id {
                    color: #666;
                    font-size: 0.85em;
                }
                .previous-winner-time {
                    color: #666;
                    font-size: 0.9em;
                    white-space: nowrap;
                    margin-left: 10px;
                }
                .button-container {
                    display: flex;
                    gap: 10px;
                    justify-content: center;
                    margin-top: 20px;
                }
                button {
                    padding: 10px 15px;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    font-weight: bold;
                }
                .select-winner-btn {
                    background-color: #4CAF50;
                    color: white;
                }
                .reset-winner-btn {
                    background-color: #f44336;
                    color: white;
                }
                .close-room-btn {
                    background-color: #ff9800;
                    color: white;
                }
                .open-room-btn {
                    background-color: #2196F3;
                    color: white;
                }
                .start-giveaway-btn {
                    background-color: #9c27b0;
                    color: white;
                }
                .stop-giveaway-btn {
                    background-color: #e91e63;
                    color: white;
                }
                .hidden {
                    display: none;
                }
                .giveaway-animation {
                    margin: 20px 0;
                    padding: 20px;
                    background-color: #f5f5f5;
                    border-radius: 10px;
                    text-align: center;
                    display: none;
                }
                .number-display {
                    font-size: 3em;
                    font-weight: bold;
                    color: #333;
                    margin: 20px 0;
                    transition: all 0.5s ease;
                }

                @keyframes winnerGlow {
                    0% { text-shadow: 0 0 10px rgba(255, 215, 0, 0.5); }
                    50% { text-shadow: 0 0 20px rgba(255, 215, 0, 0.8); }
                    100% { text-shadow: 0 0 10px rgba(255, 215, 0, 0.5); }
                }

                .winner-highlight {
                    color: #ffd700 !important;
                    transform: scale(1.2);
                    animation: winnerGlow 1.5s infinite;
                }
                """
            }
        }
        body {
            h1 { +"Giveaway Room" }

            div(classes = "container") {
                div(classes = "qr-section") {
                    // QR code section (can be hidden when room is closed)
                    div {
                        id = "qr-code-container"
                        h2 { +"Scan to Join" }
                        img(src = "data:image/png;base64,$qrCodeBase64", alt = "QR Code")
                        p { +"Scan this QR code to enter the room" }

                        // Room control buttons
                        div(classes = "button-container") {
                            button(classes = "close-room-btn") {
                                id = "close-room-btn"
                                +"Close Room"
                            }
                            button(classes = "open-room-btn hidden") {
                                id = "open-room-btn"
                                +"Open Room"
                            }
                        }
                    }

                    // Giveaway animation container (hidden by default)
                    div(classes = "giveaway-animation") {
                        id = "giveaway-animation"
                        h2 { +"Giveaway in Progress" }
                        div(classes = "number-display") {
                            id = "number-display"
                            +"0"
                        }

                        // Giveaway control buttons
                        div(classes = "button-container") {
                            button(classes = "start-giveaway-btn") {
                                id = "start-giveaway-btn"
                                +"Start Giveaway"
                            }
                        }
                    }

                    // Winner section
                    div(classes = "winner-section") {
                        h2 { +"Giveaway Winner" }
                        div(classes = "winner-display") {
                            id = "winner-display"
                        }
                        div(classes = "button-container") {
                            button(classes = "reset-winner-btn") {
                                id = "reset-winner-btn"
                                +"Reset Winner"
                            }
                        }
                    }

                    // Previous winners section
                    div(classes = "previous-winners-section") {
                        id = "previous-winners-section"
                        h2 { +"Previous Winners" }
                        div(classes = "previous-winners-list") {
                            id = "previous-winners-list"
                        }
                    }
                }

                div(classes = "users-section") {
                    div(classes = "users-header") {
                        h2 { +"Users in Room" }
                        span(classes = "user-count") {
                            id = "user-count"
                            +"(0)"
                        }
                    }
                    div(classes = "user-list") {
                        id = "user-list"
                        p {
                            id = "no-users-message"
                            +"No users have joined yet."
                        }
                    }
                }
            }

            // Use the JavaScript utility function to generate the polling code
            unsafe {
                +generatePollingJavaScript()
            }
        }
    }
}

/**
 * Renders the enter room page with name input form
 */
fun renderEnterRoomPage(): String {
    return createHTML().html {
        head {
            title("Enter Room")
            style {
                +"""
                body {
                    font-family: Arial, sans-serif;
                    max-width: 800px;
                    margin: 0 auto;
                    padding: 20px;
                    text-align: center;
                    background-color: #f9f9f9;
                }
                .container {
                    background-color: white;
                    border-radius: 10px;
                    padding: 30px;
                    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
                    margin-top: 50px;
                }
                h1 {
                    color: #333;
                    margin-bottom: 30px;
                }
                .form-group {
                    margin-bottom: 20px;
                }
                input[type="text"] {
                    width: 100%;
                    max-width: 300px;
                    padding: 12px;
                    border: 1px solid #ddd;
                    border-radius: 5px;
                    font-size: 16px;
                }
                button {
                    background-color: #4CAF50;
                    color: white;
                    padding: 12px 24px;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    font-size: 16px;
                    font-weight: bold;
                    transition: background-color 0.3s;
                }
                button:hover {
                    background-color: #45a049;
                }
                .description {
                    color: #666;
                    margin-bottom: 30px;
                }
                """
            }
        }
        body {
            div(classes = "container") {
                h1 { +"Join the Giveaway" }
                p(classes = "description") { 
                    +"Enter your name below to join the giveaway and get a chance to win!"
                }
                form(action = "/register", method = FormMethod.post) {
                    div(classes = "form-group") {
                        input(type = InputType.text, name = "name") {
                            placeholder = "Enter your name"
                            required = true
                        }
                    }
                    div(classes = "form-group") {
                        button(type = ButtonType.submit) {
                            +"Join Giveaway"
                        }
                    }
                }
            }
        }
    }
}

/**
 * Renders the user page with welcome message
 */
fun renderUserPage(userName: String, userId: Int, isKickedOut: Boolean = false): String {
    return createHTML().html {
        head {
            title("Welcome")
            style {
                +"""
                body {
                    font-family: Arial, sans-serif;
                    max-width: 800px;
                    margin: 0 auto;
                    padding: 20px;
                }
                .container {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 20px;
                }
                .welcome-section {
                    flex: 1;
                    min-width: 300px;
                }
                .winner-notification {
                    display: none;
                    margin: 20px 0;
                    padding: 20px;
                    background-color: #ffd700;
                    border-radius: 10px;
                    text-align: center;
                    animation: pulse 2s infinite;
                }
                @keyframes pulse {
                    0% { box-shadow: 0 0 0 0 rgba(255, 215, 0, 0.7); }
                    70% { box-shadow: 0 0 0 15px rgba(255, 215, 0, 0); }
                    100% { box-shadow: 0 0 0 0 rgba(255, 215, 0, 0); }
                }
                .winner-notification h2 {
                    color: #333;
                    margin-top: 0;
                }
                .winner-notification p {
                    font-size: 1.2em;
                }

                /* Previous winners styles for user page */
                .welcome-section .previous-winners-section {
                    margin-top: 30px;
                    text-align: center;
                    display: none; /* Hidden by default, will be shown by JavaScript */
                }

                .welcome-section .previous-winners-list {
                    margin-top: 15px;
                    border: 1px solid #ddd;
                    border-radius: 5px;
                    padding: 10px;
                    max-height: 300px;
                    overflow-y: auto;
                    overflow-x: hidden;
                    scrollbar-width: thin;
                    scrollbar-color: #888 #f1f1f1;
                }

                .welcome-section .previous-winners-list::-webkit-scrollbar {
                    width: 8px;
                }

                .welcome-section .previous-winners-list::-webkit-scrollbar-track {
                    background: #f1f1f1;
                    border-radius: 4px;
                }

                .welcome-section .previous-winners-list::-webkit-scrollbar-thumb {
                    background: #888;
                    border-radius: 4px;
                }

                .welcome-section .previous-winners-list::-webkit-scrollbar-thumb:hover {
                    background: #555;
                }
                .kicked-out-message {
                    margin: 20px 0;
                    padding: 20px;
                    background-color: #f44336;
                    color: white;
                    border-radius: 10px;
                    text-align: center;
                }
                """
            }
        }
        body {
            div(classes = "container") {
                div(classes = "welcome-section") {
                    // Show different content based on whether the user is kicked out
                    if (isKickedOut) {
                        h2 { +"Thank you for participating, $userName!" }

                        // Kicked out message
                        div(classes = "kicked-out-message") {
                            h2 { +"You have been removed from the giveaway" }
                            p { +"Congratulations on winning! You've been removed from the current giveaway to give others a chance to win." }
                        }
                    } else {
                        h2 { +"Welcome, $userName!" }
                        p {
                            +"Your assigned number: "
                            strong { +"$userId" }
                        }

                        // Winner notification (hidden by default)
                        div(classes = "winner-notification") {
                            id = "winner-notification"
                            h2 { +"🎉 Congratulations! 🎉" }
                            p { +"You are the winner of the giveaway!" }
                        }

                        // Previous winners section (will be populated by JavaScript)
                        div(classes = "previous-winners-section") {
                            id = "previous-winners-section"
                            h2 { +"Previous Winners" }
                            div(classes = "previous-winners-list") {
                                id = "previous-winners-list"
                            }
                        }
                    }
                }
            }

            // Use the JavaScript utility function to generate the polling code
            unsafe {
                +generatePollingJavaScript()
            }
        }
    }
}
