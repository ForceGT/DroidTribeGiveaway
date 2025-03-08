package dev.gtxtreme.giveaway

// Use environment variable PORT if available, otherwise default to 8080
val SERVER_PORT = System.getenv("PORT")?.toIntOrNull() ?: 8080
