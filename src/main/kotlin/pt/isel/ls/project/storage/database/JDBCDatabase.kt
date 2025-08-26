package pt.isel.ls.project.storage.database

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.project.storage.SessionsData
import pt.isel.ls.project.storage.model.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import kotlin.collections.HashSet

class JDBCDatabase(url: String? = null) : SessionsData {
    private val ENV = "JDBC_DATABASE_URL"
    /**
     * Initialize the [jdbcDatabaseURL] variable with the value of the environment variable
     * named "JDBC_DATABASE_URL" if url is not defined.
     */

    private val dataSource = PGSimpleDataSource()
    private val jdbcDatabaseURL: String = url ?: System.getenv(ENV)
            ?: throw IllegalArgumentException("No environment variable of name \"$ENV\" found.")
    init {   
        dataSource.setURL(jdbcDatabaseURL)
    }

    /**
     * The expiration time for an invitation in seconds.
     */
    private val inviteExpirationTime = 300 // 5 minute

    /**
     * Checks if the user is authenticated by seeing if the token is in the database
     */
    override fun authorizedPlayer(token: String): Int? {
        dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT player_id FROM players_tokens WHERE token_validation = ?").use { stmt ->
                stmt.setString(1, token)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    val userId = rs.getInt("player_id")
                    connection.prepareStatement("UPDATE players_tokens SET last_used_at = CURRENT_TIMESTAMP WHERE token_validation = ?").use { updateStmt ->
                        updateStmt.setString(1, token)
                        updateStmt.executeUpdate()
                    }
                    return userId
                }
            }
        }
        return null
    }

    /**
     * Create the token for the player and insert it into the database.
     */
    override fun createToken(playerId: Int): String? {
        val token = UUID.randomUUID().toString()
        dataSource.connection.use { connection ->
            connection.prepareStatement("INSERT INTO players_tokens (player_id, token_validation, last_used_at) VALUES (?, ?, CURRENT_TIMESTAMP)").use { stmt ->
                stmt.setInt(1, playerId)
                stmt.setString(2, token)
                val execute = stmt.executeUpdate()
                if (execute == 0) {
                    return null
                }
            }
        }
        return token
    }

    /**
     * Creates a player with the given name and email and returns a pair with the player's token and id.
     */
    override fun createPlayer(name: String, email: String, password:String): Int? {
        val sql = "INSERT INTO players (name, email, password) VALUES (?, ?, ?) RETURNING id"
        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, name)
                stmt.setString(2, email)
                stmt.setString(3, password)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    return rs.getInt("id")
                }
            }
        }
        return null
    }

    /**
     * Deletes the token from the database.
     */
    override fun deleteToken(playerId: Int): Boolean {
        dataSource.connection.use { connection ->
            connection.prepareStatement("DELETE FROM players_tokens WHERE player_id = ?").use { stmt ->
                stmt.setInt(1, playerId)
                return stmt.executeUpdate() > 0
            }
        }
    }

    /**
     * Returns the player with the given id.
     */
    override fun getPlayerById(id: Int): Player? {
        dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT * FROM players WHERE id = ?").use { stmt ->
                stmt.setInt(1, id)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    return Player(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("details"), rs.getBytes("image"), rs.getString("password"))
                }
            }
        }
        return null
    }

    /**
     * Returns the player with the given email.
     */
    override fun getPlayerByEmail(email: String): Player? {
        dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT * FROM players WHERE email = ?").use { stmt ->
                stmt.setString(1, email)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    return Player(rs.getInt("id"), rs.getString("name"), rs.getString("email"), password = rs.getString("password"))
                }
            }
        }
        return null
    }

    /**
     * Returns the player with the given name.
     */
    override fun getPlayerByName(name: String): Player? {
        dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT * FROM players WHERE name = ?").use { stmt ->
                stmt.setString(1, name)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    return Player(rs.getInt("id"), rs.getString("name"), rs.getString("email"), password = rs.getString("password") )
                }
            }
        }
        return null
    }

    /**
     * Returns a list of players with the given limit, skip and username if passed.
     */
    override fun getPlayers(limit: Int, skip: Int, username: String?): List<Player> {
        dataSource.connection.use { connection ->
            val query = StringBuilder("SELECT * FROM players")
            if (username != null) {
                query.append(" WHERE name ILIKE ?")
            }
            query.append(" ORDER BY LENGTH(name) LIMIT ? OFFSET ?")
            connection.prepareStatement(query.toString()).use { stmt ->
                if (username != null) {
                    stmt.setString(1, "$username%") // Using '%' to match any substring
                    stmt.setInt(2, limit)
                    stmt.setInt(3, skip)
                } else {
                    stmt.setInt(1, limit)
                    stmt.setInt(2, skip)
                }
                val rs = stmt.executeQuery()
                val players = mutableListOf<Player>()
                while (rs.next()) {
                    players.add(Player(rs.getInt("id"), rs.getString("name"), rs.getString("email"), password = rs.getString("password")))
                }
                return players
            }
        }
    }

    /**
     * Updates the player's details and image if passed.
     */
    override fun updatePlayer(id: Int, details: String?, image: ByteArray?): Boolean {
            dataSource.connection.use { connection ->
            val query = StringBuilder("UPDATE players SET")
            val params = mutableListOf<Any>()
            if (details != null) {
                query.append(" details = ?,")
                params.add(details)
            } else {
                query.append(" details = NULL,")
            }

            if (image != null) {
                query.append(" image = ?,")
                params.add(image)
            } else {
                query.append(" image = NULL,")
            }
            query.deleteCharAt(query.length - 1) // Remove the last comma
            query.append(" WHERE id = ?")
            params.add(id)
            connection.prepareStatement(query.toString()).use { stmt ->
                params.forEachIndexed { index, param ->
                    when (param) {
                        is String -> stmt.setString(index + 1, param)
                        is ByteArray -> stmt.setBytes(index + 1, param)
                        is Int -> stmt.setInt(index + 1, param)
                    }
                }
                return stmt.executeUpdate() == 1
            }
        }
    }



    /**
     * Creates a game with the given name, developer and genres and returns the game's id.
     * It also adds the genre to the database if it doesn't exist.
     */
    override fun createGame(name: String, developer: String, genres: List<String>): Int? {
        val newGenres = genres.toHashSet()
        val genresId = addGenresIfNotInTable(newGenres)
        dataSource.connection.use { connection ->
            connection.prepareStatement("INSERT INTO games (name, developer) VALUES (?, ?) RETURNING id").use { stmt ->
                stmt.setString(1, name)
                stmt.setString(2, developer)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    val gameId = rs.getInt("id")
                    genresId.forEach { genreId ->
                        connection.prepareStatement("INSERT INTO games_genres (game_id, genre_id) VALUES (?, ?)").use { stmt2 ->
                            stmt2.setInt(1, gameId)
                            stmt2.setInt(2, genreId)
                            stmt2.executeUpdate()
                        }
                    }
                    return gameId
                }
            }
        }
        return null
    }

    /**
     * Returns the game with the given id.
     */
    override fun getGameDetailsById(id: Int): Game? {
        dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT * FROM games WHERE id = ?").use { stmt ->
                stmt.setInt(1, id)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    val genresId = HashSet<Int>()
                    connection.prepareStatement("SELECT genre_id FROM games_genres WHERE game_id = ?").use { stmt2 ->
                        stmt2.setInt(1, id)
                        val rs2 = stmt2.executeQuery()
                        while (rs2.next()) {
                            genresId.add(rs2.getInt("genre_id"))
                        }
                    }
                    return Game(rs.getInt("id"), rs.getString("name"), rs.getString("developer"), getGenresById(genresId))
                }
            }
        }
        return null
    }

    /**
     * Returns the game with the given name.
     */
    override fun getGameDetailsByName(name: String): Game? {
        dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT * FROM games WHERE name = ?").use { stmt ->
                stmt.setString(1, name)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    val genresId = HashSet<Int>()
                    connection.prepareStatement("SELECT genre_id FROM games_genres WHERE game_id = ?").use { stmt2 ->
                        stmt2.setInt(1, rs.getInt("id"))
                        val rs2 = stmt2.executeQuery()
                        while (rs2.next()) {
                            genresId.add(rs2.getInt("genre_id"))
                        }
                    }
                    return Game(rs.getInt("id"), rs.getString("name"), rs.getString("developer"), getGenresById(genresId))
                }
            }
        }
        return null
    }

    /**
     * Returns a list of games with the given genres, developer, limit and game name if passed.
     */
    override fun getGamesListBy(genres: List<String>, developer: String, limit: Int, gameName:String?): List<Game> {
        val newGenres = genres.toHashSet()
        val genresId = addGenresIfNotInTable(newGenres)
        dataSource.connection.use { connection ->
            val query = StringBuilder("SELECT * FROM games WHERE id IN (SELECT game_id FROM games_genres WHERE genre_id = ANY(?))")
            query.append(" AND developer = ?")
            if (gameName != null) {
                query.append(" AND name ILIKE ?")
            }
            connection.prepareStatement(query.toString()).use { stmt ->
                stmt.setArray(1, connection.createArrayOf("int", genresId.toTypedArray()))
                stmt.setString(2, developer)
                if (gameName != null) {
                    stmt.setString(3, "$gameName%")
                }
                val rs = stmt.executeQuery()
                val games = mutableListOf<Game>()
                while (rs.next() && games.size < limit) {
                    val gameGenresId = HashSet<Int>()
                    connection.prepareStatement("SELECT genre_id FROM games_genres WHERE game_id = ?").use { stmt2 ->
                        stmt2.setInt(1, rs.getInt("id"))
                        val rs2 = stmt2.executeQuery()
                        while (rs2.next()) {
                            gameGenresId.add(rs2.getInt("genre_id"))
                        }
                    }
                    games.add(Game(rs.getInt("id"), rs.getString("name"), rs.getString("developer"), getGenresById(gameGenresId)))
                }
                return games
            }
        }

    }


    override fun getGamesListByName(gameName: String?, limit: Int): List<Game> {
            dataSource.connection.use { connection ->
            val query = StringBuilder("SELECT * FROM games")

            if (gameName != null) {
                query.append(" WHERE name ILIKE ?")
            }
            query.append(" LIMIT ?")
            connection.prepareStatement(query.toString()).use { stmt ->
                if (gameName != null) {
                    stmt.setString(1, "$gameName%") // Using '%' to match any substring
                    stmt.setInt(2, limit)
                } else {
                    stmt.setInt(1, limit)
                }
                val rs = stmt.executeQuery()
                val games = mutableListOf<Game>()
                while (rs.next()) {
                    val gameGenresId = HashSet<Int>()
                    connection.prepareStatement("SELECT genre_id FROM games_genres WHERE game_id = ?").use { stmt2 ->
                        stmt2.setInt(1, rs.getInt("id"))
                        val rs2 = stmt2.executeQuery()
                        while (rs2.next()) {
                            gameGenresId.add(rs2.getInt("genre_id"))
                        }
                    }
                    games.add(Game(rs.getInt("id"), rs.getString("name"), rs.getString("developer"), getGenresById(gameGenresId)))
                }
                return games
            }
        }
    }

    /**
     * Returns a list of developers.
     */
    override fun getDevelopers(): List<String> {
        val developers = mutableListOf<String>()
        dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT DISTINCT developer FROM games").use { stmt ->
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    developers.add(rs.getString("developer"))
                }
            }
        }
        return developers
    }


    /**
     * Returns a list of genres.
     */
    override fun getGenres(): List<String> {
        val genres = mutableListOf<String>()
        dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT name FROM genres").use { stmt ->
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    genres.add(rs.getString("name"))
                }
            }
        }
        return genres
    }



    /**
     * Creates a session with the given capacity, game id and date and returns the session's id.
     */
    override fun createSession(capacity: Int, gid: Int, date: Instant): Int? {
        dataSource.connection.use { connection ->
            connection.prepareStatement("INSERT INTO sessions (capacity, date, gameId, state) VALUES (?, ?, ?, ?) RETURNING id").use { stmt ->
                stmt.setInt(1, capacity)
                stmt.setTimestamp(2, Timestamp.from(date))
                stmt.setInt(3, gid)
                stmt.setString(4, SessionState.OPEN.name)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    return rs.getInt("id")
                }
            }
        }
        return null
    }

    /**
     * Adds a player to the session.
     */
    override fun addPlayerToSession(session: Session, pid: Int) {
        dataSource.connection.use { connection ->
            connection.prepareStatement("INSERT INTO players_sessions (player_id, session_id) VALUES (?, ?)").use { stmt ->
                stmt.setInt(1, pid)
                stmt.setInt(2, session.id)
                stmt.executeUpdate()
            }
        }
    }

    /**
     * Returns the session with the given id.
     */
    override fun getSessionDetails(sid: Int): Session? {
        dataSource.connection.use { connection ->
            connection.prepareStatement("""
        SELECT s.id, s.capacity, s.date, s.gameId, s.state, p.id AS player_id
        FROM sessions s
        LEFT JOIN players_sessions ps ON s.id = ps.session_id
        LEFT JOIN players p ON ps.player_id = p.id
        WHERE s.id = ?
    """).use { stmt ->
                stmt.setInt(1, sid)
                val rs = stmt.executeQuery()
                val playersIds = HashSet<Int>()
                if(rs.next()) {
                    val id = rs.getInt("id")
                    val capacity = rs.getInt("capacity")
                    val date = rs.getTimestamp("date").toInstant()
                    val gameId = rs.getInt("gameId")
                    val state = SessionState.valueOf(rs.getString("state"))
                    val playerId = rs.getInt("player_id")
                    if (playerId != 0) {
                        playersIds.add(playerId)
                    }
                    while(rs.next()){
                        if (rs.getInt("player_id") != 0) {
                            playersIds.add(rs.getInt("player_id"))
                        }
                    }
                    return Session(id, capacity, date, gameId, playersIds, state)
                }
            }
        }
        return null
    }

    /**
     * Returns a list of sessions with the given game id, date, state, player id and limit if passed.
     */
    override fun getSessionsListBy(game: Int?, date: Instant?, state: String?, pid: Int?): List<Session> {
        updateExpiredSessions() // Assuming this function updates any expired sessions
        dataSource.connection.use { connection ->
            val query = StringBuilder("SELECT s.id, s.capacity, s.date, s.gameId, s.state, p.id AS player_id FROM sessions s LEFT JOIN players_sessions ps ON s.id = ps.session_id LEFT JOIN players p ON ps.player_id = p.id WHERE 1=1")
            val params = mutableListOf<Any>()

            if (game != null) {
                query.append(" AND s.gameId = ?")
                params.add(game)
            }
            if (date != null) {
                query.append(" AND s.date <= ?")
                params.add(Timestamp.from(date))
            }
            if (state != null) {
                query.append(" AND s.state = ?")
                params.add(SessionState.valueOf(state))
            }
            if (pid != null) {
                query.append(" AND ps.player_id = ?")
                params.add(pid)
            }

            query.append(" ORDER BY s.id")

            connection.prepareStatement(query.toString()).use { stmt ->
                // Set parameters
                params.forEachIndexed { index, param ->
                    when (param) {
                        is String -> stmt.setString(index + 1, param)
                        is Int -> stmt.setInt(index + 1, param)
                        is Instant -> stmt.setTimestamp(index + 1, Timestamp.from(param))
                        is SessionState -> stmt.setString(index + 1, param.name)
                        else -> stmt.setObject(index + 1, param)
                    }
                }

                // Execute query
                val rs = stmt.executeQuery()
                val sessions = mutableListOf<Session>()
                while (rs.next()) {
                    val sessionId = rs.getInt("id")
                    val gameId = rs.getInt("gameId")
                    val session = sessions.find { it.id == sessionId }
                    if(session != null){
                        session.playersId.add(rs.getInt("player_id"))
                    } else {
                        val sessionPlayers = HashSet<Int>()
                        val playerId = rs.getInt("player_id")
                        if (playerId != 0) {
                            sessionPlayers.add(playerId)
                        }
                        sessions.add(
                            Session(
                                sessionId,
                                rs.getInt("capacity"),
                                rs.getTimestamp("date").toInstant(),
                                gameId,
                                sessionPlayers,
                                SessionState.valueOf(rs.getString("state"))
                            )
                        )
                    }
                }
                return sessions
            }
        }
    }

    /**
     * Updates the session's state to closed if it has passed its time or someone requested to changed its state
     */
    private fun updateExpiredSessions() {
        dataSource.connection.use { connection ->
            val currentTime = Instant.now()
            val updateQuery = "UPDATE sessions SET state = ? WHERE date <= ? AND state = ?"
            connection.prepareStatement(updateQuery).use { stmt ->
                stmt.setString(1, SessionState.CLOSED.name)
                stmt.setTimestamp(2, Timestamp.from(currentTime))
                stmt.setString(3, SessionState.OPEN.name)
                stmt.executeUpdate()
            }
        }
    }

    /**
     * Returns true if the session has been deleted or false otherwise.
     */
    override fun deleteSession(sid: Int): Boolean {
        dataSource.connection.use { connection ->
            connection.prepareStatement("DELETE FROM sessions WHERE id = ?").use { stmt ->
                stmt.setInt(1, sid)
                return stmt.executeUpdate() == 1
            }
        }
    }

    /**
     * Returns true if the player has been removed from the session or false otherwise.
     */
    override fun removePlayerFromSession(sid: Int, pid: Int): Boolean {
        dataSource.connection.use { connection ->
            connection.prepareStatement("DELETE FROM players_sessions WHERE session_id = ? AND player_id = ?").use { stmt ->
                stmt.setInt(1, sid)
                stmt.setInt(2, pid)
                return stmt.executeUpdate() == 1 // Returns true if one row was deleted
            }
        }
    }

    /**
     * Returns true if the session's capacity has been updated or false otherwise.
     */
    override fun updateSession(sid: Int, capacity: Int): Boolean {
        dataSource.connection.use { connection ->
            connection.prepareStatement("UPDATE sessions SET capacity = ? WHERE id = ?").use {
                stmt ->
                stmt.setInt(1, capacity)
                stmt.setInt(2, sid)
                return stmt.executeUpdate() == 1
            }
        }
    }

    /**
     * Returns true if the session's date has been updated or false otherwise.
     */
    override fun updateSession(sid: Int, date: Instant): Boolean {
        dataSource.connection.use { connection ->
            connection.prepareStatement("UPDATE sessions SET date = ? WHERE id = ?").use {
                    stmt ->
                stmt.setTimestamp(1, Timestamp.from(date))
                stmt.setInt(2, sid)
                return stmt.executeUpdate() == 1
            }
        }
    }

    /**
     * Auxiliary function that adds the genres to the genres table if they don't exist and returns their ids.
     */
    private fun addGenresIfNotInTable(genres: HashSet<String>): HashSet<Int> {
        val genresId = HashSet<Int>()
        genres.forEach { genre ->
            dataSource.connection.use { connection ->
                connection.prepareStatement("SELECT id FROM genres WHERE name = ?").use { stmt ->
                    stmt.setString(1, genre)
                    val rs = stmt.executeQuery()
                    if (rs.next()) {
                        genresId.add(rs.getInt("id"))
                    } else {
                        connection.prepareStatement("INSERT INTO genres (name) VALUES (?) RETURNING id").use { stmt2 ->
                            stmt2.setString(1, genre)
                            val rs2 = stmt2.executeQuery()
                            if (rs2.next()) {
                                genresId.add(rs2.getInt("id"))
                            }
                        }
                    }
                }
            }
        }
        return genresId
    }

    /**
     * Auxiliary function that returns the genres with the given ids.
     */
    private fun getGenresById(genresId: HashSet<Int>): HashSet<String> {
        val genres = HashSet<String>()
        dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT name FROM genres WHERE id = ANY(?)").use { stmt ->
                stmt.setArray(1, connection.createArrayOf("int", genresId.toTypedArray()))
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    genres.add(rs.getString("name"))
                }
            }
        }
        return genres
    }

    /**
     * Returns true if the player has been invited successfully or false otherwise.
     */
    override fun invitePlayerToSession(sid: Int, fromPid: Int, toPid: Int): Boolean {
        dataSource.connection.use { connection ->
            // First, check if there's an existing invitation for the same session and players
            connection.prepareStatement(
                "SELECT * FROM player_invites WHERE from_player_id = ? AND to_player_id = ? AND session_id = ?"
            ).use { stmt ->
                stmt.setInt(1, fromPid)
                stmt.setInt(2, toPid)
                stmt.setInt(3, sid)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    // If an invitation already exists, update its expiration time
                    connection.prepareStatement(
                        "UPDATE player_invites SET invite_expiration = CURRENT_TIMESTAMP + INTERVAL '${inviteExpirationTime} SECOND' WHERE from_player_id = ? AND to_player_id = ? AND session_id = ?"
                    ).use { updateStmt ->
                        updateStmt.setInt(1, fromPid)
                        updateStmt.setInt(2, toPid)
                        updateStmt.setInt(3, sid)
                        return updateStmt.executeUpdate() == 1
                    }
                } else {
                    // If no existing invitation, create a new one
                    connection.prepareStatement(
                        "INSERT INTO player_invites (from_player_id, to_player_id, session_id, invite_expiration) VALUES (?, ?, ?, CURRENT_TIMESTAMP + INTERVAL '${inviteExpirationTime} SECOND')"
                    ).use { insertStmt ->
                        insertStmt.setInt(1, fromPid)
                        insertStmt.setInt(2, toPid)
                        insertStmt.setInt(3, sid)
                        return insertStmt.executeUpdate() == 1
                    }
                }
            }
        }
    }


    /**
     * Returns a list of invites for the player with the given id.
     */
    override fun getInvites(pid: Int): List<Invite> {
        deleteExpiredInvites()
        dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT * FROM player_invites WHERE to_player_id = ?").use { stmt ->
                stmt.setInt(1, pid)
                val rs = stmt.executeQuery()
                val invites = mutableListOf<Invite>()
                while (rs.next()) {
                    invites.add(
                        Invite(
                            rs.getInt("from_player_id"),
                            rs.getInt("to_player_id"),
                            rs.getInt("session_id"),
                            rs.getTimestamp("invite_expiration").toInstant()
                        )
                    )
                }
                return invites
            }
        }
    }

    /**
     * Deletes the expired invites and returns true if any invite was deleted.
     */
    private fun deleteExpiredInvites(): Boolean {
        dataSource.connection.use { connection ->
            connection.prepareStatement("DELETE FROM player_invites WHERE invite_expiration <= CURRENT_TIMESTAMP").use { stmt ->
                return stmt.executeUpdate() > 0
            }
        }
    }
}