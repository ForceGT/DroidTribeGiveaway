# Giveaway

A simple web application for managing giveaways. Users can join a giveaway by scanning a QR code or accessing a URL, entering their name, and receiving a unique number.

## Project Overview

This project is built using Kotlin and Ktor, providing a lightweight server for hosting giveaway events. It generates a QR code that participants can scan to join the giveaway, making it easy to collect participant information in a physical event setting.

## Features

- QR code generation for easy access to the registration page
- Simple user registration with name input
- Unique number assignment for each participant
- In-memory storage of participant information
- Web-based interface for participants

## Technologies Used

- **Kotlin**: Programming language
- **Ktor**: Web framework for building asynchronous servers
- **Gradle**: Build system
- **ZXing**: Library for QR code generation
- **Websockets**: For real-time communication (planned for future enhancements)

## Project Structure

- `/server`: Contains the Ktor server application
  - Main application logic
  - QR code generation utilities
  - Room management for participants

## Setup Instructions

### Local Development

1. Clone the repository
2. Make sure you have JDK 8 or higher installed
3. Run the application using Gradle:
   ```
   ./gradlew :server:run
   ```
4. The server will start on your local IP address at port 8080

### Cloud Deployment

The project is configured for easy deployment to [Render](https://render.com/), a cloud platform with a free tier.

#### Deployment Changes

The following changes have been made to support cloud deployment:

- Modified the application to use the `PORT` environment variable instead of a hardcoded port
- Added support for the `HOST` environment variable to set the correct domain for QR codes
- Created a Dockerfile for containerized deployment
- Added a `render.yaml` configuration file for Render deployment

These changes ensure the application works correctly in both local and cloud environments.

1. Fork or clone this repository to your GitHub account
2. Sign up for a free Render account
3. In Render dashboard, click "New" and select "Blueprint"
4. Connect your GitHub repository
5. Render will automatically detect the configuration in `render.yaml`
6. Click "Apply" to deploy the application
7. Once deployed, your application will be available at the URL provided by Render (typically `https://giveaway-app.onrender.com` or similar)

> **Note:** After deployment, make sure to update any references to the application URL in your documentation or communications with users. The deployed URL will be different from your local development URL.

Alternatively, you can deploy to other platforms that support Docker:

1. Build the Docker image:
   ```
   docker build -t giveaway-app .
   ```
2. Run the Docker container:
   ```
   docker run -p 8080:8080 -e PORT=8080 -e HOST=your-domain.com giveaway-app
   ```
3. Replace `your-domain.com` with your actual domain or IP address

## Usage

1. Start the server
2. Access the root URL (e.g., `http://your-ip-address:8080/`) to see the QR code
3. Scan the QR code or access the URL directly to open the registration page
4. Enter your name and submit to join the giveaway
5. You will be assigned a unique number that can be used for the giveaway drawing

## Planned Enhancements

- Improved UI on the home page showing the QR code
- Real-time display of users who have joined the room
- Automatic updates as new users join

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open source and available under the [MIT License](LICENSE).

## Learn More

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
