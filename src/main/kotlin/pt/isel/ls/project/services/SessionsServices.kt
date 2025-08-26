package pt.isel.ls.project.services

import pt.isel.ls.project.storage.SessionsData
import pt.isel.ls.project.storage.model.Game
import pt.isel.ls.project.storage.model.Player
import pt.isel.ls.project.storage.model.Session
import pt.isel.ls.project.services.exceptionType.GameExceptions
import pt.isel.ls.project.services.exceptionType.PlayerExceptions
import pt.isel.ls.project.services.exceptionType.SessionExceptions
import pt.isel.ls.project.services.exceptionType.TokenExceptions
import java.sql.SQLException
import java.time.Instant
import java.util.Base64

class SessionsServices(private val data: SessionsData) : SessionsService {
    /**
     * Function that creates a token for a player
     */
    override fun createTokenService(playerId: Int): String {
        try{
            if (playerId <= 0) throw PlayerExceptions.PlayerBadParameter
            val token = data.createToken(playerId) ?: throw PlayerExceptions.DatabaseError
            return token
        } catch (e: SQLException) {
            throw PlayerExceptions.DatabaseError
        }
    }

    /**
     * Function that receives a token and returns the player id if the token is valid
     */
    override fun authorizedUserService(token: String): Int {
        try{
            if (token.isEmpty()) throw TokenExceptions.TokenMissingParameter
            val id = data.authorizedPlayer(token) ?: throw TokenExceptions.TokenNotAuthorized
            return id
        } catch (e: SQLException) {
            throw PlayerExceptions.DatabaseError
        }
    }

    /**
     * Function that creates a player
     */
    override fun createPlayerService(name: String, email: String, password:String): Pair<String, Int> {
        try{
            if(name.isEmpty() || email.isEmpty() || password.isEmpty()) throw PlayerExceptions.PlayerMissingParameter
            if (data.getPlayerByEmail(email) != null) throw PlayerExceptions.PlayerAlreadyExists
            if (data.getPlayerByName(name) != null) throw PlayerExceptions.PlayerAlreadyExists
            val hashedPassword = Player.hashPassword(name + password)
            val player = data.createPlayer(name, email, password = hashedPassword) ?: throw PlayerExceptions.DatabaseError
            val token = data.createToken(player) ?: throw PlayerExceptions.DatabaseError
            return Pair(token, player)
        } catch (e: SQLException) {
            throw PlayerExceptions.DatabaseError
        }
    }

    /**
     * Function that deletes a token
     */
    override fun deleteTokenService(playerId: Int) {
        try {
            if (playerId <= 0) throw PlayerExceptions.PlayerBadParameter
            data.deleteToken(playerId)
        } catch (e: SQLException) {
            throw PlayerExceptions.DatabaseError
        }
    }

    /**
     * Function that authenticates a player
     */
    override fun authenticatePlayerService(name: String, password: String): Pair<String, Int> {
        try {
            if (name.isEmpty() || password.isEmpty()) throw PlayerExceptions.PlayerMissingParameter
            val player = data.getPlayerByName(name) ?: throw PlayerExceptions.PlayerNotFound
            if (!Player.checkPassword(name + password, player.password)) throw PlayerExceptions.PlayerInvalidPassword
            val token = data.createToken(player.id) ?: throw PlayerExceptions.DatabaseError
            return Pair(token, player.id)
        } catch (e: SQLException) {
            throw PlayerExceptions.DatabaseError
        }
    }

    /**
     * Function that returns a list of players
     */
    override fun getPlayers(limit: Int, skip: Int, username: String?): List<Player> {
        try {
            if (limit < 0 || skip < 0) throw PlayerExceptions.PlayerBadParameter
            return data.getPlayers(limit, skip, username)
        } catch (e: SQLException) {
            throw PlayerExceptions.DatabaseError
        }
    }

    /**
     * Function that returns a player by id
      */
    override fun getPlayerByIdService(id: Int): Pair<Player, String?>{
        try {
            if (id <= 0) throw PlayerExceptions.PlayerBadParameter
            val player = data.getPlayerById(id) ?: throw PlayerExceptions.PlayerNotFound
            return if(player.image == null) {
                Pair(player, null)
            } else {
                val encodedImage = Base64.getUrlEncoder().encodeToString(player.image)
                val imageParsed = encodedImage.replace('-', '+').replace('_', '/')
                val image = "data:image/png;base64,$imageParsed"
                Pair(player, image)
            }
        } catch (e: SQLException) {
            throw PlayerExceptions.DatabaseError
        }
    }
    /**
     * Function that returns a player by email
     */

    override fun getPlayerByEmailService(email: String): Player {
        try{
            if(email.isEmpty()) throw PlayerExceptions.PlayerMissingParameter
            val player = data.getPlayerByEmail(email) ?: throw PlayerExceptions.PlayerNotFound
            return player
        } catch (e: SQLException) {
            throw PlayerExceptions.DatabaseError
        }
    }

    /**
     * Function that updates a player
     */
    override fun updatePlayerService(id: Int, details: String?, image: String?) {
        try{
            if(id <= 0) throw PlayerExceptions.PlayerBadParameter
            if(data.getPlayerById(id) == null) throw PlayerExceptions.PlayerNotFound
            val imageDecoded = if(image != null) {
                if(!image.startsWith("data:image/png;base64,") && !image.startsWith("data:image/jpeg;base64,")) throw PlayerExceptions.PlayerInvalidImage
                val imageWithoutPrefix = image.split(",")[1].replace('+', '-').replace('/', '_')
                Base64.getUrlDecoder().decode(imageWithoutPrefix)
            } else {
                null
            }
            data.updatePlayer(id, details, imageDecoded)
        } catch (e: SQLException) {
            throw PlayerExceptions.DatabaseError
        }
    }

    /**
     * Function that creates a game
     */

    override fun createGameService(name: String, developer: String, genres: List<String>): Int {
        try{
            if(name.isEmpty() || developer.isEmpty() || genres.isEmpty()) throw GameExceptions.GameMissingParameter
            if(data.getGameDetailsByName(name) != null) throw GameExceptions.GameAlreadyExists
            return data.createGame(name, developer, genres) ?: throw PlayerExceptions.DatabaseError
        } catch (e: SQLException) {
            throw GameExceptions.DatabaseError
        }
    }

    /**
     * Function that returns a game by id
     */

    override fun getGameDetailsService(id: Int): Game {
        try{
            if(id <= 0) throw GameExceptions.GameBadParameter
            return data.getGameDetailsById(id) ?: throw GameExceptions.GameNotFound
        } catch (e: SQLException) {
            throw GameExceptions.DatabaseError
        }
    }

    /**
     * Function that returns a game by name
     */
    override fun getGameDetailsService(name: String): Game? {
        try {
            if(name.isBlank()) throw GameExceptions.GameBadParameter
            return data.getGameDetailsByName(name)
        } catch (e: SQLException) {
            throw GameExceptions.DatabaseError
        }
    }

    /**
     * Function that returns a list of games
     */
    override fun getGamesListByService(genres: List<String>, developer: String, limit: Int, skip: Int, gameName:String?): List<Game> {
        try{
            if(genres.isEmpty() || developer.isEmpty()) throw GameExceptions.GameMissingParameter
            val games = data.getGamesListBy(genres, developer, limit+skip, gameName)
            return games.drop(skip)
        } catch (e: SQLException) {
            throw GameExceptions.DatabaseError
        }
    }

    override fun getGamesListByNameService(gameName: String?, limit: Int, skip: Int): List<Game> {
        try{
            if(gameName == null) throw GameExceptions.GameMissingParameter
            val games = data.getGamesListByName(gameName, limit+skip)
            return games
        } catch (e: SQLException) {
            throw GameExceptions.DatabaseError
        }
    }


    /**
     * Function that returns a list of developers
     */
    override fun getDevelopers(): List<String> {
        try {
            if(data.getDevelopers().isEmpty()) throw GameExceptions.DevelopersNotFound
            return data.getDevelopers()
        } catch (e: SQLException) {
            throw GameExceptions.DatabaseError
        }

    }

    /**
     * Function that returns a list of genres
     */
    override fun getGenres(): List<String> {
        try {
            if(data.getGenres().isEmpty()) throw GameExceptions.GenresNotFound
            return data.getGenres()
        } catch (e: SQLException) {
            throw GameExceptions.DatabaseError
        }

    }

    /**
     * Function that creates a session
     */
    override fun createSessionService(capacity: Int, game: Int, date: Instant): Int {
        try {
            if(Instant.now() > date) throw SessionExceptions.SessionBadParameter
            if(capacity <= 0) throw SessionExceptions.SessionBadParameter
            if(data.getGameDetailsById(game) == null) throw GameExceptions.GameNotFound
            return data.createSession(capacity, game, date) ?: throw PlayerExceptions.DatabaseError
        } catch (e: SQLException) {
            throw SessionExceptions.DatabaseError
        }
    }

    /**
     * Function that adds a player to a session
     */
    override fun addPlayerToSessionService(sid: Int, pid: Int): Session {
        try{
            if(sid <= 0) throw SessionExceptions.SessionBadParameter
            if(pid <= 0) throw PlayerExceptions.PlayerBadParameter
            if(data.getPlayerById(pid) == null) throw PlayerExceptions.PlayerNotFound
            val session = data.getSessionDetails(sid) ?: throw SessionExceptions.SessionNotFound
            if(session.playersId.size >= session.capacity) throw SessionExceptions.SessionFull
            if(session.playersId.any { it == pid }) throw PlayerExceptions.PlayerAlreadyInSession
            data.addPlayerToSession(session, pid)
            val updatedSession = data.getSessionDetails(sid) ?: throw SessionExceptions.SessionNotFound
            return updatedSession
        } catch (e: SQLException) {
            throw SessionExceptions.DatabaseError
        }
    }

    /**
     * Function that returns a session by id
     */
    override fun getSessionDetailsService(sid: Int): Session {
        try{
            if(sid <= 0) throw SessionExceptions.SessionBadParameter
            return data.getSessionDetails(sid) ?: throw SessionExceptions.SessionNotFound
        } catch (e: SQLException) {
            throw SessionExceptions.DatabaseError
        }
    }

    /**
     *  Function that returns a list of sessions
     */
    override fun getSessionsListByService(game: Int?, date: Instant?, state: String?, pid: Int?, limit: Int, skip: Int): List<Session> {
        try{
            return data.getSessionsListBy(game, date, state, pid).drop(skip).take(limit)
        } catch (e: SQLException) {
            throw SessionExceptions.DatabaseError
        }
    }

    /**
     * Function that deletes a session
     */
    override fun deleteSessionService(sid: Int) {
        try{
            val session = data.getSessionDetails(sid) ?: throw SessionExceptions.SessionNotFound
            session.playersId.forEach { data.removePlayerFromSession(sid, it) }
            if(!data.deleteSession(sid)) throw SessionExceptions.SessionNotFound
        } catch (e: SQLException) {
            throw SessionExceptions.DatabaseError
        }
    }

    /**
     * Function that removes a player from a session
     */
    override fun removePlayerFromSessionService(sid: Int, pid: Int) {
        try{
            val result = data.removePlayerFromSession(sid, pid)
            if(result == null){
                throw SessionExceptions.SessionNotFound
            } else if(!result) {
                throw PlayerExceptions.PlayerNotFound
            }
        } catch (e: SQLException) {
            throw SessionExceptions.DatabaseError
        }
    }

    /**
     * Function that updates a session
     */
    override fun updateSession(sid: Int, capacity: Int?, date: Instant?) {
        if(capacity != null && !data.updateSession(sid, capacity)) throw SessionExceptions.SessionNotFound
        if(date != null && !data.updateSession(sid, date)) throw SessionExceptions.SessionNotFound
    }

    /**
     * Function that invites a player to a session
     */
    override fun invitePlayerToSession(sid: Int, fromPid: Int, toPid:Int) {
        try {
            if(fromPid <= 0 || toPid <= 0) throw PlayerExceptions.PlayerBadParameter
            if(data.getPlayerById(fromPid) == null || data.getPlayerById(toPid) == null) throw PlayerExceptions.PlayerNotFound
            val session = data.getSessionDetails(sid) ?: throw SessionExceptions.SessionNotFound
            if(session.playersId.any { it == toPid }) throw PlayerExceptions.PlayerAlreadyInSession
            if(session.playersId.none { it == fromPid }) throw PlayerExceptions.PlayerNotInSession
            data.invitePlayerToSession(sid, fromPid, toPid)
        } catch (e: SQLException) {
            throw SessionExceptions.DatabaseError
        }
    }

    /**
     * Function that gets the player invites by its id
     */
    override fun getInvites(pid: Int): List<Pair<Session, Player>> {
        try{
            if(pid <= 0) throw PlayerExceptions.PlayerBadParameter
            if(data.getPlayerById(pid) == null) throw PlayerExceptions.PlayerNotFound
            return data.getInvites(pid).map { invite ->
                Pair(
                    data.getSessionDetails(invite.sessionId) ?: throw SessionExceptions.SessionNotFound,
                    data.getPlayerById(invite.fromPid) ?: throw PlayerExceptions.PlayerNotFound
                )
            }
        } catch (e: SQLException) {
            throw SessionExceptions.DatabaseError
        }
    }

}