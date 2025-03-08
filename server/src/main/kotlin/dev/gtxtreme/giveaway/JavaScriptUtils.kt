package dev.gtxtreme.giveaway

/**
 * Utility function to generate JavaScript code for polling updates and animations
 */
fun generatePollingJavaScript(): String {
    return """
    <script>
    // Get DOM elements
    var winnerNotification = document.getElementById('winner-notification');
    var resetWinnerBtn = document.getElementById('reset-winner-btn');
    var closeRoomBtn = document.getElementById('close-room-btn');
    var openRoomBtn = document.getElementById('open-room-btn');
    var startGiveawayBtn = document.getElementById('start-giveaway-btn');
    var qrCodeContainer = document.getElementById('qr-code-container');
    var giveawayAnimation = document.getElementById('giveaway-animation');
    var numberDisplay = document.getElementById('number-display');

    // Animation variables
    var animationInterval;
    var userIds = [];

    // Get the current user ID from the URL if on the user page
    var currentUserId = null;
    var userIdMatch = window.location.pathname.match(/\/user\/([0-9]+)/);
    if (userIdMatch) {
        currentUserId = userIdMatch[1];
        console.log('Current user ID:', currentUserId);
    }

    // Check if the current user is the winner and show notification
    function checkIfCurrentUserIsWinner(winnerId, users) {
        var previousWinnersSection = document.getElementById('previous-winners-section');

        // Only show notification if the user is the winner and not kicked out
        if (winnerNotification && currentUserId && winnerId && currentUserId == winnerId) {
            // Check if the user is kicked out
            var isKickedOut = users && users[currentUserId] && users[currentUserId].kickedOut;

            if (isKickedOut) {
                // If user is kicked out, hide the notification
                console.log('Current user is the winner but has been kicked out');
                winnerNotification.style.display = 'none';

                // Show previous winners section for kicked out winners
                if (previousWinnersSection) {
                    previousWinnersSection.style.display = 'block';
                }
                return;
            }

            console.log('Current user is the winner!');
            winnerNotification.style.display = 'block';

            // Hide previous winners section for current winner
            if (previousWinnersSection) {
                previousWinnersSection.style.display = 'none';
            }

            // Add sound effect if not already played
            if (!winnerNotification.hasAttribute('data-played')) {
                try {
                    // Play a celebratory sound
                    var audio = new Audio('data:audio/mpeg;base64,SUQzBAAAAAABEVRYWFgAAAAtAAADY29tbWVudABCaWdTb3VuZEJhbmsuY29tIC8gTGFTb25vdGhlcXVlLm9yZwBURU5DAAAAHQAAA1N3aXRjaCBQbHVzIMKpIE5DSCBTb2Z0d2FyZQBUSVQyAAAABgAAAzIyMzUAVFNTRQAAAA8AAANMYXZmNTcuODMuMTAwAAAAAAAAAAAAAAD/80DEAAAAA0gAAAAATEFNRTMuMTAwVVVVVVVVVVVVVUxBTUUzLjEwMFVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVf/zQsRbAAADSAAAAABVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVf/zQMSkAAADSAAAAABVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV');
                    audio.play();

                    // Mark as played to avoid playing multiple times
                    winnerNotification.setAttribute('data-played', 'true');
                } catch (e) {
                    console.error('Error playing sound:', e);
                }
            }
        } else {
            // Not the current winner, show previous winners section
            if (previousWinnersSection) {
                previousWinnersSection.style.display = 'block';
            }

            // Hide winner notification if it was previously shown
            if (winnerNotification) {
                winnerNotification.style.display = 'none';
            }
        }
    }

    // Function to update UI based on room status
    function updateRoomStatus(isRoomClosed, isGiveawayRunning, winner) {
        // Get the winner section element
        var winnerSection = document.querySelector('.winner-section');

        // Update room closure UI
        if (isRoomClosed) {
            if (closeRoomBtn) closeRoomBtn.classList.add('hidden');
            if (openRoomBtn) openRoomBtn.classList.remove('hidden');
            if (giveawayAnimation) giveawayAnimation.style.display = 'block';
            // Show winner section only when room is closed and there is a winner
            if (winnerSection) winnerSection.style.display = winner ? 'block' : 'none';
            // Hide QR code when room is closed
            if (qrCodeContainer) {
                var qrImage = qrCodeContainer.querySelector('img');
                var qrHeading = qrCodeContainer.querySelector('h2');
                var qrDescription = qrCodeContainer.querySelector('p');
                if (qrImage) qrImage.style.display = 'none';
                if (qrHeading) qrHeading.textContent = 'Room Closed';
                if (qrDescription) qrDescription.textContent = 'The room is closed for new entries.';
            }
        } else {
            if (closeRoomBtn) closeRoomBtn.classList.remove('hidden');
            if (openRoomBtn) openRoomBtn.classList.add('hidden');
            if (giveawayAnimation) giveawayAnimation.style.display = 'none';
            // Hide winner section when room is open
            if (winnerSection) winnerSection.style.display = 'none';
            // Show QR code when room is open
            if (qrCodeContainer) {
                var qrImage = qrCodeContainer.querySelector('img');
                var qrHeading = qrCodeContainer.querySelector('h2');
                var qrDescription = qrCodeContainer.querySelector('p');
                if (qrImage) qrImage.style.display = 'block';
                if (qrHeading) qrHeading.textContent = 'Scan to Join';
                if (qrDescription) qrDescription.textContent = 'Scan this QR code to enter the room';
            }
        }

        // Update giveaway animation UI
        if (isGiveawayRunning) {
            if (startGiveawayBtn) {
                startGiveawayBtn.textContent = "Stop Giveaway";
                startGiveawayBtn.classList.add('stop-giveaway-btn');
                startGiveawayBtn.classList.remove('start-giveaway-btn');
            }
        } else {
            if (startGiveawayBtn) {
                startGiveawayBtn.textContent = "Start Giveaway";
                startGiveawayBtn.classList.add('start-giveaway-btn');
                startGiveawayBtn.classList.remove('stop-giveaway-btn');
            }
            // Stop the animation if it's running
            if (animationInterval) {
                clearInterval(animationInterval);
                animationInterval = null;
            }
        }
    }

    // Casino-like animation for shuffling user numbers
    function startNumberAnimation(users) {
        // Get all user IDs
        userIds = Object.keys(users);

        if (userIds.length === 0) {
            console.log('No users to animate');
            return;
        }

        // Stop any existing animation
        if (animationInterval) {
            clearInterval(animationInterval);
        }

        // Start the animation
        animationInterval = setInterval(function() {
            // Pick a random user ID
            var randomIndex = Math.floor(Math.random() * userIds.length);
            var randomId = userIds[randomIndex];

            // Update the display
            if (numberDisplay) {
                numberDisplay.textContent = randomId;
            }
        }, 100); // Update every 100ms for fast animation
    }

    // Function to update the previous winners list
    function updatePreviousWinnersList(previousWinnersList, previousWinners) {
        // Clear the list
        previousWinnersList.innerHTML = '';

        // Check if there are previous winners
        if (previousWinners && previousWinners.length > 0) {
            // Add each previous winner to the list
            for (var i = 0; i < previousWinners.length; i++) {
                var winner = previousWinners[i];
                var winnerItem = document.createElement('div');
                winnerItem.className = 'previous-winner-item';

                // Create container for name and ID
                var infoDiv = document.createElement('div');
                infoDiv.className = 'previous-winner-info';

                // Create name span
                var nameSpan = document.createElement('div');
                nameSpan.className = 'previous-winner-name';
                nameSpan.textContent = winner.user.name;

                // Create ID span
                var idSpan = document.createElement('div');
                idSpan.className = 'previous-winner-id';
                idSpan.textContent = 'ID: ' + winner.id + ' (' + winner.user.uniqueId + ')';

                // Add name and ID to info container
                infoDiv.appendChild(nameSpan);
                infoDiv.appendChild(idSpan);

                var timeSpan = document.createElement('span');
                timeSpan.className = 'previous-winner-time';
                timeSpan.textContent = winner.timestamp;

                winnerItem.appendChild(infoDiv);
                winnerItem.appendChild(timeSpan);

                previousWinnersList.appendChild(winnerItem);
            }
            return true; // Indicates there are previous winners
        } else {
            return false; // Indicates there are no previous winners
        }
    }

    // Function to update the user list in the UI
    function updateUserList(users, winnerId, previousWinners) {
        console.log('updateUserList called with users:', users);

        var userList = document.getElementById('user-list');
        console.log('userList element:', userList);

        var noUsersMessage = document.getElementById('no-users-message');
        console.log('noUsersMessage element:', noUsersMessage);

        var winnerDisplay = document.getElementById('winner-display');
        var previousWinnersSection = document.getElementById('previous-winners-section');
        var previousWinnersList = document.getElementById('previous-winners-list');
        var userCount = document.getElementById('user-count');
        console.log('userCount element:', userCount);

        if (!userList) {
            console.log('userList element not found, exiting updateUserList');
            return; // Exit if user list element doesn't exist
        }

        // Clear the list
        userList.innerHTML = '';

        // Update user count - only count users who haven't been kicked out
        var count = 0;
        if (users) {
            for (var key in users) {
                if (!users[key].kickedOut) {
                    count++;
                }
            }
        }
        if (userCount) {
            userCount.textContent = '(' + count + ')';
            console.log('Updated user count to:', count);
        }

        // Check if there are users
        if (!users || count === 0) {
            if (noUsersMessage) {
                userList.appendChild(noUsersMessage.cloneNode(true));
            } else {
                var message = document.createElement('p');
                message.textContent = 'No users have joined yet.';
                userList.appendChild(message);
            }

            if (winnerDisplay) {
                winnerDisplay.style.display = 'none';
            }
            return;
        }

        // Add each user to the list
        for (var key in users) {
            var user = users[key];

            // Skip users that have been kicked out
            if (user.kickedOut) {
                console.log('Skipping kicked out user:', user);
                continue;
            }

            var userItem = document.createElement('div');
            userItem.className = 'user-item';

            // Highlight the winner
            if (winnerId && key == winnerId) {
                userItem.className += ' winner';
            }

            // Create name span
            var nameSpan = document.createElement('span');
            nameSpan.className = 'user-name';
            nameSpan.textContent = user.name;

            // Create ID span
            var idSpan = document.createElement('span');
            idSpan.className = 'user-id';
            idSpan.textContent = 'ID: ' + key + ' (' + user.uniqueId + ')';
            idSpan.style.color = '#666';
            idSpan.style.fontSize = '0.9em';

            // Add spans to user item
            userItem.appendChild(nameSpan);
            userItem.appendChild(idSpan);

            userList.appendChild(userItem);

            // Update winner display if available
            if (winnerDisplay && winnerId && key == winnerId) {
                winnerDisplay.innerHTML = '<h3>Winner:</h3>' + 
                    '<div class="winner-info">' + 
                    '<div class="winner-name">' + user.name + '</div>' + 
                    '<div class="winner-id">ID: ' + key + ' (' + user.uniqueId + ')</div>' + 
                    '</div>';
                winnerDisplay.style.display = 'block';
            }
        }

        // Hide winner display if no winner
        if (winnerDisplay && !winnerId) {
            winnerDisplay.style.display = 'none';
        }

        // Update previous winners section
        if (previousWinnersSection && previousWinnersList) {
            var hasPreviousWinners = updatePreviousWinnersList(previousWinnersList, previousWinners);

            // Show or hide the section based on whether there are previous winners
            previousWinnersSection.style.display = hasPreviousWinners ? 'block' : 'none';
        }
    }

    // Function to check status updates
    function checkStatus() {
        // Add a cache-busting parameter to ensure we get fresh data
        var cacheBuster = new Date().getTime();
        fetch('/api/users?_=' + cacheBuster)
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('Failed to fetch status: ' + response.status);
                }
                return response.json();
            })
            .then(function(data) {
                console.log('Status data received:', data);
                console.log('Users data:', data.users);
                console.log('Users count:', data.users ? Object.keys(data.users).length : 0);

                // Update the user list
                updateUserList(data.users, data.winner, data.previousWinners);

                // Update winner notification if on user page
                if (currentUserId) {
                    // Check if the current user is kicked out
                    var isKickedOut = data.users && data.users[currentUserId] && data.users[currentUserId].kickedOut;

                    if (isKickedOut) {
                        // If the user is kicked out but the page doesn't show the kicked-out message, reload the page
                        var kickedOutMessage = document.querySelector('.kicked-out-message');
                        if (!kickedOutMessage || kickedOutMessage.style.display === 'none') {
                            console.log('User is kicked out. Reloading page to show kicked-out message...');
                            window.location.reload();
                            return;
                        }
                    } else {
                        // If the user is not kicked out, check if they are the winner
                        checkIfCurrentUserIsWinner(data.winner, data.users);
                    }

                    // Always update the previous winners list on user pages
                    // This will be shown or hidden by checkIfCurrentUserIsWinner based on winner status
                    var userPagePreviousWinnersSection = document.getElementById('previous-winners-section');
                    var userPagePreviousWinnersList = document.getElementById('previous-winners-list');

                    if (userPagePreviousWinnersSection && userPagePreviousWinnersList) {
                        var hasPreviousWinners = updatePreviousWinnersList(userPagePreviousWinnersList, data.previousWinners);
                        // Show or hide the section based on whether there are previous winners
                        userPagePreviousWinnersSection.style.display = hasPreviousWinners ? 'block' : 'none';
                    }
                }

                // Update room status if on admin page
                if (closeRoomBtn && openRoomBtn) {
                    updateRoomStatus(data.isRoomClosed, data.isGiveawayRunning, data.winner);

                    // Start or stop animation based on giveaway status
                    if (data.isGiveawayRunning && !animationInterval) {
                        startNumberAnimation(data.users);
                    } else if (!data.isGiveawayRunning && animationInterval) {
                        clearInterval(animationInterval);
                        animationInterval = null;
                    }
                }
            })
            .catch(function(error) {
                console.error('Error fetching status:', error);
            });
    }

    // Check status every 2 seconds
    checkStatus(); // Initial check
    setInterval(checkStatus, 2000); // Polling interval

    // Event listeners for winner selection buttons have been consolidated with the Start Giveaway button

    if (resetWinnerBtn) {
        resetWinnerBtn.addEventListener('click', function() {
            // Get the current winner ID
            fetch('/api/users')
                .then(function(response) {
                    if (!response.ok) {
                        throw new Error('Failed to fetch winner status: ' + response.status);
                    }
                    return response.json();
                })
                .then(function(data) {
                    var winnerId = data.winner;

                    if (!winnerId) {
                        alert('No winner selected yet. Nothing to reset.');
                        return;
                    }

                    // Confirm before resetting and kicking out
                    if (confirm('Are you sure you want to reset and kick out the winner (ID: ' + winnerId + ')?')) {
                        // First kick out the winner
                        // Add a cache-busting parameter to ensure we get fresh data
                        var kickOutCacheBuster = new Date().getTime();
                        fetch('/api/kick-out-user/' + winnerId + '?_=' + kickOutCacheBuster, { method: 'POST' })
                            .then(function(response) {
                                return response.json();
                            })
                            .then(function(kickOutData) {
                                console.log('Winner kicked out:', kickOutData);

                                // Update the user list immediately after kicking out the user
                                checkStatus();

                                // Then reset the winner
                                // Add a cache-busting parameter to ensure we get fresh data
                                var cacheBuster = new Date().getTime();
                                return fetch('/api/reset-winner?_=' + cacheBuster, { method: 'POST' });
                            })
                            .then(function(response) {
                                return response.json();
                            })
                            .then(function(resetData) {
                                console.log('Winner reset:', resetData);

                                // Reset number display to 0
                                if (numberDisplay) {
                                    numberDisplay.textContent = '0';
                                    numberDisplay.classList.remove('winner-highlight');
                                }

                                alert('Winner has been kicked out and reset successfully!');

                                // Add a delay before updating the status to give the server time to process the reset
                                setTimeout(function() {
                                    checkStatus(); // Update status
                                }, 500);
                            })
                            .catch(function(error) {
                                console.error('Error in kick out and reset process:', error);
                                alert('Error: ' + error.message);
                            });
                    }
                })
                .catch(function(error) {
                    console.error('Error fetching winner ID:', error);
                    alert('Error fetching winner ID: ' + error.message);
                });
        });
    }

    // Add event listeners for room control buttons if they exist
    if (closeRoomBtn) {
        closeRoomBtn.addEventListener('click', function() {
            fetch('/api/close-room', { method: 'POST' })
                .then(function(response) {
                    return response.json();
                })
                .then(function(data) {
                    console.log('Room closed:', data);
                    checkStatus(); // Update status
                })
                .catch(function(error) {
                    console.error('Error closing room:', error);
                });
        });
    }

    if (openRoomBtn) {
        openRoomBtn.addEventListener('click', function() {
            fetch('/api/open-room', { method: 'POST' })
                .then(function(response) {
                    return response.json();
                })
                .then(function(data) {
                    console.log('Room opened:', data);
                    checkStatus(); // Update status
                })
                .catch(function(error) {
                    console.error('Error opening room:', error);
                });
        });
    }

    // Global variables to store intervals and timeouts
    var countdownInterval;
    var winnerSelectionTimeout;

    // Add event listeners for giveaway control buttons if they exist
    if (startGiveawayBtn) {
        startGiveawayBtn.addEventListener('click', function(event) {
            // Only handle the click if the button is in "Start Giveaway" mode
            if (startGiveawayBtn.textContent === "Start Giveaway") {
                // Prevent default behavior and stop propagation
                event.preventDefault();
                event.stopPropagation();

                console.log('Start Giveaway button clicked');

                // Disable the button to prevent multiple clicks
                startGiveawayBtn.disabled = true;

                // Start the giveaway animation
                fetch('/api/start-giveaway', { method: 'POST' })
                .then(function(response) {
                    return response.json();
                })
                .then(function(data) {
                    console.log('Giveaway started:', data);
                    checkStatus(); // Update status

                    // Show a countdown message
                    var animationDuration = 5; // seconds
                    var countdownMessage = document.createElement('div');
                    countdownMessage.id = 'countdown-message';
                    countdownMessage.style.position = 'absolute';
                    countdownMessage.style.top = '40%'; // Moved up to not cover the button
                    countdownMessage.style.left = '50%';
                    countdownMessage.style.transform = 'translate(-50%, -50%)';
                    countdownMessage.style.backgroundColor = 'rgba(0, 0, 0, 0.7)';
                    countdownMessage.style.color = 'white';
                    countdownMessage.style.padding = '20px';
                    countdownMessage.style.borderRadius = '10px';
                    countdownMessage.style.fontSize = '24px';
                    countdownMessage.style.fontWeight = 'bold';
                    countdownMessage.style.zIndex = '1000';
                    countdownMessage.style.textAlign = 'center';
                    countdownMessage.textContent = 'Selecting winner in ' + animationDuration + ' seconds...';

                    // Add the countdown message to the giveaway animation container
                    if (giveawayAnimation) {
                        giveawayAnimation.style.position = 'relative';
                        giveawayAnimation.appendChild(countdownMessage);
                    }

                    // Change the button text to "Stop Giveaway"
                    startGiveawayBtn.textContent = "Stop Giveaway";
                    startGiveawayBtn.classList.add('stop-giveaway-btn');
                    startGiveawayBtn.classList.remove('start-giveaway-btn');

                    // Clear any existing countdown interval
                    if (countdownInterval) {
                        clearInterval(countdownInterval);
                    }

                    // Update the countdown every second
                    var countdown = animationDuration;
                    countdownInterval = setInterval(function() {
                        countdown--;
                        if (countdown > 0) {
                            countdownMessage.textContent = 'Selecting winner in ' + countdown + ' seconds...';
                        } else {
                            countdownMessage.textContent = 'Selecting winner...';
                        }
                    }, 1000);

                    // After the animation duration, select a winner and stop the animation
                    // Clear any existing timeout
                    if (winnerSelectionTimeout) {
                        clearTimeout(winnerSelectionTimeout);
                    }

                    winnerSelectionTimeout = setTimeout(function() {
                        // Clear the countdown interval
                        if (countdownInterval) {
                            clearInterval(countdownInterval);
                            countdownInterval = null;
                        }

                        // Select a winner
                        fetch('/api/select-winner', { method: 'POST' })
                            .then(function(response) {
                                return response.json();
                            })
                            .then(function(data) {
                                console.log('Winner selected:', data);

                                // Get the winner ID
                                var winnerId = data.winner;

                                if (winnerId) {
                                    // Clear any existing animation
                                    if (animationInterval) {
                                        clearInterval(animationInterval);
                                        animationInterval = null;
                                    }

                                    // Display the winning number
                                    if (numberDisplay) {
                                        numberDisplay.textContent = winnerId;

                                        // Add a highlight effect to the number display
                                        numberDisplay.classList.add('winner-highlight');

                                        // Play a celebratory sound
                                        try {
                                            var audio = new Audio('data:audio/mpeg;base64,SUQzBAAAAAABEVRYWFgAAAAtAAADY29tbWVudABCaWdTb3VuZEJhbmsuY29tIC8gTGFTb25vdGhlcXVlLm9yZwBURU5DAAAAHQAAA1N3aXRjaCBQbHVzIMKpIE5DSCBTb2Z0d2FyZQBUSVQyAAAABgAAAzIyMzUAVFNTRQAAAA8AAANMYXZmNTcuODMuMTAwAAAAAAAAAAAAAAD/80DEAAAAA0gAAAAATEFNRTMuMTAwVVVVVVVVVVVVVUxBTUUzLjEwMFVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVf/zQsRbAAADSAAAAABVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVf/zQMSkAAADSAAAAABVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV');
                                            audio.play();
                                        } catch (e) {
                                            console.error('Error playing sound:', e);
                                        }

                                        // Keep the highlight for 5 seconds, then remove it
                                        setTimeout(function() {
                                            numberDisplay.classList.remove('winner-highlight');
                                        }, 5000);
                                    }
                                }

                                // Stop the giveaway animation
                                return fetch('/api/stop-giveaway', { method: 'POST' });
                            })
                            .then(function(response) {
                                return response.json();
                            })
                            .then(function(data) {
                                console.log('Giveaway stopped:', data);

                                // Remove the countdown message
                                if (countdownMessage.parentNode) {
                                    countdownMessage.parentNode.removeChild(countdownMessage);
                                }

                                // Update the UI
                                checkStatus();

                                // Re-enable the button and change text back to "Start Giveaway"
                                startGiveawayBtn.disabled = false;
                                startGiveawayBtn.textContent = "Start Giveaway";
                                startGiveawayBtn.classList.add('start-giveaway-btn');
                                startGiveawayBtn.classList.remove('stop-giveaway-btn');

                                // Clear the winner selection timeout reference
                                winnerSelectionTimeout = null;
                            })
                            .catch(function(error) {
                                console.error('Error in giveaway process:', error);
                                alert('Error: ' + error.message);

                                // Re-enable the button and change text back to "Start Giveaway"
                                startGiveawayBtn.disabled = false;
                                startGiveawayBtn.textContent = "Start Giveaway";
                                startGiveawayBtn.classList.add('start-giveaway-btn');
                                startGiveawayBtn.classList.remove('stop-giveaway-btn');

                                // Clear the winner selection timeout reference
                                winnerSelectionTimeout = null;
                            });
                    }, animationDuration * 1000);
                })
                .catch(function(error) {
                    console.error('Error starting giveaway:', error);
                    alert('Error starting giveaway: ' + error.message);

                    // Clear any countdown interval
                    if (countdownInterval) {
                        clearInterval(countdownInterval);
                        countdownInterval = null;
                    }

                    // Clear any winner selection timeout
                    if (winnerSelectionTimeout) {
                        clearTimeout(winnerSelectionTimeout);
                        winnerSelectionTimeout = null;
                    }

                    // Remove any countdown message if it exists
                    var countdownMessage = document.getElementById('countdown-message');
                    if (countdownMessage && countdownMessage.parentNode) {
                        countdownMessage.parentNode.removeChild(countdownMessage);
                    }

                    // Re-enable the button
                    startGiveawayBtn.disabled = false;
                });
            }
        });
    }

    // We now use a single button for both starting and stopping the giveaway
    // The stopGiveawayBtn event listener is no longer needed as we've consolidated functionality

    // Add event listener for the Stop Giveaway functionality to the Start Giveaway button
    // This is triggered when the button text is "Stop Giveaway"
    if (startGiveawayBtn) {
        // Add a second event listener for when the button is in "Stop Giveaway" mode
        startGiveawayBtn.addEventListener('click', function(event) {
            // Prevent the default click behavior and stop propagation for all clicks
            event.preventDefault();
            event.stopPropagation();

            // Only handle the click if the button is in "Stop Giveaway" mode
            if (startGiveawayBtn.textContent === "Stop Giveaway") {
                console.log('Stop Giveaway button clicked');

                // Remove any countdown message if it exists
                var countdownMessage = document.getElementById('countdown-message');
                if (countdownMessage && countdownMessage.parentNode) {
                    console.log('Removing countdown message');
                    countdownMessage.parentNode.removeChild(countdownMessage);
                }

                // Clear any animation interval
                if (animationInterval) {
                    console.log('Clearing animation interval');
                    clearInterval(animationInterval);
                    animationInterval = null;
                }

                // Clear any countdown interval
                if (countdownInterval) {
                    console.log('Clearing countdown interval');
                    clearInterval(countdownInterval);
                    countdownInterval = null;
                } else {
                    console.log('No countdown interval to clear');
                }

                // Clear any winner selection timeout
                if (winnerSelectionTimeout) {
                    console.log('Clearing winner selection timeout');
                    clearTimeout(winnerSelectionTimeout);
                    winnerSelectionTimeout = null;
                }

                // Reset number display to default state
                if (numberDisplay) {
                    console.log('Resetting number display to 0');
                    numberDisplay.textContent = '0';
                    numberDisplay.classList.remove('winner-highlight');
                }

                // Change button text back to "Start Giveaway"
                startGiveawayBtn.textContent = "Start Giveaway";
                startGiveawayBtn.classList.add('start-giveaway-btn');
                startGiveawayBtn.classList.remove('stop-giveaway-btn');

                fetch('/api/stop-giveaway', { method: 'POST' })
                    .then(function(response) {
                        return response.json();
                    })
                    .then(function(data) {
                        console.log('Giveaway stopped:', data);
                        checkStatus(); // Update status

                        // Re-enable the start giveaway button if it was disabled
                        if (startGiveawayBtn && startGiveawayBtn.disabled) {
                            startGiveawayBtn.disabled = false;
                        }
                    })
                    .catch(function(error) {
                        console.error('Error stopping giveaway:', error);
                        alert('Error stopping giveaway: ' + error.message);

                        // Re-enable the start giveaway button if it was disabled
                        if (startGiveawayBtn && startGiveawayBtn.disabled) {
                            startGiveawayBtn.disabled = false;
                        }
                    });
            }
        });
    }

    </script>
    """.trimIndent()
}
