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
                .users-section {
                    flex: 1;
                    min-width: 300px;
                }
                .user-list {
                    border: 1px solid #ddd;
                    border-radius: 5px;
                    padding: 10px;
                    min-height: 200px;
                }
                .user-item {
                    padding: 8px;
                    margin: 5px 0;
                    background-color: #f5f5f5;
                    border-radius: 3px;
                }
                """
            }
        }
        body {
            h1 { +"Giveaway Room" }
            
            div(classes = "container") {
                div(classes = "qr-section") {
                    h2 { +"Scan to Join" }
                    img(src = "data:image/png;base64,$qrCodeBase64", alt = "QR Code")
                    p { +"Scan this QR code to enter the room" }
                }
                
                div(classes = "users-section") {
                    h2 { +"Users in Room" }
                    div(classes = "user-list") {
                        id = "user-list"
                        p {
                            id = "no-users-message"
                            +"No users have joined yet."
                        }
                    }
                }
            }
            
            script {
                +"""
                // WebSocket connection
                const socket = new WebSocket("ws://${ipAddress}:${SERVER_PORT}/updates");
                const userList = document.getElementById('user-list');
                const noUsersMessage = document.getElementById('no-users-message');
                
                socket.onmessage = function(event) {
                    const data = JSON.parse(event.data);
                    updateUserList(data.users);
                };
                
                socket.onclose = function(event) {
                    console.log("WebSocket connection closed");
                };
                
                socket.onerror = function(error) {
                    console.error("WebSocket error:", error);
                };
                
                function updateUserList(users) {
                    // Clear the list
                    userList.innerHTML = '';
                    
                    // Check if there are users
                    if (Object.keys(users).length === 0) {
                        userList.appendChild(noUsersMessage);
                        return;
                    }
                    
                    // Add each user to the list
                    for (const [id, name] of Object.entries(users)) {
                        const userItem = document.createElement('div');
                        userItem.className = 'user-item';
                        userItem.textContent = name + ' (ID: ' + id + ')';
                        userList.appendChild(userItem);
                    }
                }
                """
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
        }
        body {
            h2 { +"Enter Your Name" }
            form(action = "/register", method = FormMethod.post) {
                input(type = InputType.text, name = "name") {
                    placeholder = "Enter your name"
                    required = true
                }
                button(type = ButtonType.submit) {
                    +"Join Giveaway"
                }
            }
        }
    }
}

/**
 * Renders the user page with welcome message
 */
fun renderUserPage(userName: String, userId: Int): String {
    return createHTML().html {
        head {
            title("Welcome")
        }
        body {
            h2 { +"Welcome, $userName!" }
            p {
                +"Your assigned number: "
                strong { +"$userId" }
            }
        }
    }
}