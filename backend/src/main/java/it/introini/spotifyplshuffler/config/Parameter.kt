package it.introini.spotifyplshuffler.config


enum class Parameter(val parameter: String, val default: Any) {
    LOG_LEVEL ("log.level", "DEBUG"),
    LOG_FILE  ("log.file", "spotifyplshuffler.log"),

    HTTP_PORT ("http.port", 8082),

    CLIENT_ID     ("spotify.auth.client_id", "2fb703bd5e5440cfa007f3c6b41baa89"),
    CLIENT_SECRET ("spotify.auth.client_secret", "b4c369c23c7642f18bb89f83b823e7c3"),
    SCOPES        ("spotify.auth.scopes", "playlist-read-private playlist-modify-private user-read-private user-read-email user-read-birthdate user-read-playback-state"),

    MONGO_DB_NAME              ("mongo.db.name", "spotify_shuffler"),
    MONGO_DB_HOST              ("mongo.db.host", "localhost"),
    MONGO_DB_PORT              ("mongo.db.port", 27017)
}