package pt.isel.ls.project.services

import pt.isel.ls.project.storage.model.Game
import pt.isel.ls.project.storage.model.Player
import pt.isel.ls.project.storage.model.Session
import java.time.Instant

interface SessionsService {

    /*
    ---------------------Authorization-------------------
     */
    fun authorizedUserService(token: String): Int
    fun createTokenService(playerId: Int): String

    /*
    ------------------------Player------------------------
     */
    fun createPlayerService(name: String, email: String, password:String): Pair<String, Int>

    fun deleteTokenService(playerId: Int)

    fun authenticatePlayerService(name: String, password: String): Pair<String, Int>
    fun getPlayers(limit: Int, skip: Int, username: String?): List<Player>
    fun getPlayerByIdService(id: Int): Pair<Player, String?>
    fun getPlayerByEmailService(email: String): Player
    fun updatePlayerService(id: Int, details: String?, image: String?)

    /*
    ------------------------Game------------------------
     */

    fun createGameService(name: String, developer: String, genres: List<String>): Int
    fun getGameDetailsService(id: Int): Game?
    fun getGameDetailsService(name: String): Game?
    fun getGamesListByService(genres: List<String>, developer: String, limit: Int, skip: Int, gameName:String?): List<Game>

    fun getGamesListByNameService(gameName: String?, limit: Int, skip: Int): List<Game>
    fun getDevelopers(): List<String>

    fun getGenres(): List<String>

    /*
    ------------------------Session------------------------
     */
    fun createSessionService(capacity: Int, game: Int, date: Instant): Int
    fun addPlayerToSessionService(sid: Int, pid: Int): Session
    fun getSessionDetailsService(sid: Int): Session?
    fun getSessionsListByService(game: Int?, date: Instant?, state: String?, pid: Int?, limit: Int, skip: Int): List<Session>
    fun removePlayerFromSessionService(sid: Int, pid: Int)
    fun deleteSessionService(sid: Int)
    fun updateSession(sid: Int, capacity: Int?, date: Instant?)

    fun invitePlayerToSession(sid: Int, fromPid: Int, toPid: Int)
    fun getInvites(pid: Int): List<Pair<Session, Player>>
}