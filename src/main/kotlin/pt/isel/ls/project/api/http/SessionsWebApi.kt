package pt.isel.ls.project.api.http

import pt.isel.ls.project.api.model.*
import pt.isel.ls.project.api.winterboot.annotations.Controller
import pt.isel.ls.project.api.winterboot.annotations.mappings.DeleteMapping
import pt.isel.ls.project.api.winterboot.annotations.mappings.GetMapping
import pt.isel.ls.project.api.winterboot.annotations.mappings.PostMapping
import pt.isel.ls.project.api.winterboot.annotations.mappings.PutMapping
import pt.isel.ls.project.api.winterboot.annotations.parameters.Body
import pt.isel.ls.project.api.winterboot.annotations.parameters.Header
import pt.isel.ls.project.api.winterboot.annotations.parameters.Path
import pt.isel.ls.project.api.winterboot.annotations.parameters.Query
import pt.isel.ls.project.services.SessionsService
import pt.isel.ls.project.services.exceptionType.GameExceptions
import pt.isel.ls.project.services.exceptionType.PlayerExceptions
import pt.isel.ls.project.services.exceptionType.SessionExceptions
import pt.isel.ls.project.storage.model.Game
import pt.isel.ls.project.storage.model.Player
import pt.isel.ls.project.storage.model.Session
import java.time.Instant


@Controller
class SessionsWebApi(private val sessionsService: SessionsService){

    /**
     * Function that receives a registeringPlayer and returns a map with the token and the player id
     */
    @PostMapping("/players")
    fun addPlayer(@Body registeringPlayer: RegisteringPlayer): Map<String, String> {
        val (token, id) = sessionsService.createPlayerService(registeringPlayer.name, registeringPlayer.email, registeringPlayer.password)
        return mapOf(
            "token" to token,
            "playerId" to id.toString()
        )
    }

    /**
     * Function that receives a playerId and deletes the token
     */
    @DeleteMapping("/players/logout")
    fun deleteToken(@Query playerId: Int){
        sessionsService.deleteTokenService(playerId)
    }


    /**
     * Function that receives a name and a password and returns a map with the token and the player id
     */
    @PostMapping("/authenticate")
    fun loginPlayer(@Query name: String, @Query password: String): Map<String, String>{
        val player = sessionsService.authenticatePlayerService(name, password)
        return mapOf(
            "token" to player.first,
            "playerId" to player.second.toString()
        )
    }

    /**
     * Function that receives a token and returns the player id
     */
    @GetMapping("/authenticated")
    fun getPlayerIdFromToken(@Header authorization: String): Map<String, String>{
        val (type, token) = authorization.split(" ")
        if(type == "Bearer"){
            return mapOf("id" to "${sessionsService.authorizedUserService(token)}")
        }
        throw PlayerExceptions.PlayerNotAuthorized
    }

    /**
     * Function that receives a limit, a skip and an optional username and returns a list of players
     */
    @GetMapping("/players")
    fun getPlayers(@Query limit: Int, @Query skip: Int, @Query username: String? = null): List<Player> {
        return sessionsService.getPlayers(limit, skip, username)
    }

    /**
     * Function that receives a playerId and returns the player
     */
    @GetMapping("/players/{playerId}")
    fun getPlayer(@Path playerId: Int): GetPlayerDetails {
        val player = sessionsService.getPlayerByIdService(playerId)
        return GetPlayerDetails(player.first.id, player.first.name, player.first.email, player.first.details, player.second)
    }

    /**
     * Function that receives a playerId and updates the player details
     */
    @PutMapping("/players/{playerId}")
    fun updatePlayer(@Path playerId: Int, @Body player: UpdatePlayer){
        sessionsService.updatePlayerService(playerId, player.details, player.image)
    }

    /**
     * Function that receives a RegisteringGame and returns a map with the gameId
     */
    @PostMapping("/games")
    fun addGame(@Body registeringGame: RegisteringGame): Map<String, Int>{
        val gameId = sessionsService.createGameService(
            registeringGame.name,
            registeringGame.developer,
            registeringGame.genres
        )
        return mapOf(
            "gameId" to gameId
        )
    }

    /**
     * Function that receives a developer, a list of genres, a limit, a skip and an optional gameName and returns a list of games
     */
    @GetMapping("/games")
    fun getGames(@Query developer: String, @Query genres: List<String>, @Query limit: Int, @Query skip: Int, @Query gameName:String? = null): List<Game>{
        return sessionsService.getGamesListByService(genres, developer, limit, skip, gameName)
    }

    @GetMapping("/games/partial")
    fun getGamesByName(@Query gameName: String?, @Query limit: Int, @Query skip: Int): List<Game>{
        return sessionsService.getGamesListByNameService(gameName, skip, limit)
    }

    /**
     * Function that receives a gameId and returns the game
     */
    @GetMapping("/games/{gameId}")
    fun getGame(@Path gameId: Int): Game{
        return sessionsService.getGameDetailsService(gameId) ?: throw GameExceptions.GameNotFound
    }

    /**
     * Function that returns a list of developers
     */
    @GetMapping("/developers")
    fun getAllDevelopers(): List<String>{
        return sessionsService.getDevelopers()
    }

    /**
     * Function that returns a list of genres
     */
    @GetMapping("/genres")
    fun getGenres(): List<String>{
        return sessionsService.getGenres()
    }

    /**
     * Function that receives a RegisteringSession and returns a map with the sessionId
     */
    @PostMapping("/sessions")
    fun addSession(@Body registeringSession: RegisteringSession): Map<String, Int>{
        val game = sessionsService.getGameDetailsService(registeringSession.gameName) ?: throw GameExceptions.GameNotFound
        val sessionId = sessionsService.createSessionService(
            registeringSession.capacity,
            game.id,
            registeringSession.date
        )
        return mapOf(
            "sessionId" to sessionId
        )
    }

    /**
     * Function that receives a limit, a skip, an optional game, an optional date, an optional state and an optional playerId and returns a list of sessions
     */
    @GetMapping("/sessions")
    fun getSessions(@Query limit: Int,
                    @Query skip: Int,
                    @Query game: String? = null,
                    @Query date: Instant? = null,
                    @Query state: String? = null,
                    @Query pid: Int? = null): List<GetSessionReturn> {
        val gameId = if(game != null) sessionsService.getGameDetailsService(game)?.id ?: return emptyList() else null
         return sessionsService.getSessionsListByService(
            gameId,
            date,
            state,
            pid,
            limit,
            skip
        ).map { session ->
             val gameInfo = sessionsService.getGameDetailsService(session.gameId)!!
             GetSessionReturn(session.id, session.capacity, session.date, session.gameId, session.playersId, session.state, gameInfo.name)
         }
    }

    /**
     * Function that receives a sessionId and returns the session
     */
    @GetMapping("/sessions/{sessionId}")
    fun getSession(@Path sessionId: Int): GetSessionReturn {
        val session =  sessionsService.getSessionDetailsService(sessionId) ?: throw SessionExceptions.SessionNotFound
        val game = sessionsService.getGameDetailsService(session.gameId) ?: throw GameExceptions.GameNotFound
        return GetSessionReturn(session.id, session.capacity, session.date, session.gameId, session.playersId, session.state, game.name)
    }

    /**
     * Function that receives a sessionId and a playerId and adds the player to the session
     */
    @PutMapping("/sessions/{sessionId}/players/{playerId}")
    fun addPlayerToSession(@Path sessionId: Int, @Path playerId: Int): Session {
        return sessionsService.addPlayerToSessionService(sessionId, playerId)
    }

    /**
     * Function that receives a sessionId and deletes the session
     */
    @DeleteMapping("/sessions/{sessionId}")
    fun deleteSession(@Path sessionId: Int){
        sessionsService.deleteSessionService(sessionId)
    }

    /**
     * Function that receives a sessionId and a playerId and removes the player from the session
     */
    @DeleteMapping("/sessions/{sessionId}/players/{playerId}")
    fun removePlayerFromSession(@Path sessionId: Int, @Path playerId: Int){
        sessionsService.removePlayerFromSessionService(sessionId, playerId)
    }

    /**
     * Function that receives a sessionId and capacity and updates the session capacity
     */
    @PutMapping("/sessions/{sessionId}")
    fun updateSession(@Path sessionId: Int, @Query capacity: Int? = null, @Query date: Instant? = null){
        sessionsService.updateSession(sessionId, capacity, date)
    }

    /**
     * Function that receives a sessionId, a fromPlayerId and a toPlayerId and invites the toPlayerId to the session
     */
    @PostMapping("/sessions/{sessionId}/players/{fromPlayerId}/invite/{toPlayerId}")
    fun invitePlayerToSession(@Path sessionId: Int, @Path fromPlayerId: Int, @Path toPlayerId: Int){
        sessionsService.invitePlayerToSession(sessionId, fromPlayerId, toPlayerId)
    }

    /**
     * Function that receives a playerId and returns the invites for that player
     */
    @GetMapping("/players/{playerId}/invites")
    fun getInvites(@Path playerId: Int): List<GetInviteReturn>{
        val invites = sessionsService.getInvites(playerId)
        return invites.map{ invite ->
            val session = invite.first
            val game = sessionsService.getGameDetailsService(session.gameId) ?: throw GameExceptions.GameNotFound
            GetInviteReturn(
                GetSessionReturn(session.id, session.capacity, session.date, session.gameId, session.playersId, session.state, game.name),
                invite.second
            )
        }
    }
}
