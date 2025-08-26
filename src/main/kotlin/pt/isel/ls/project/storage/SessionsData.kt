package pt.isel.ls.project.storage

import org.eclipse.jetty.util.security.Password
import pt.isel.ls.project.storage.model.Game
import pt.isel.ls.project.storage.model.Invite
import pt.isel.ls.project.storage.model.Player
import pt.isel.ls.project.storage.model.Session
import java.time.Instant

interface SessionsData {
    /*
    ---------------------Authorization-------------------
     */
    fun authorizedPlayer(token: String): Int?
    fun createToken(playerId: Int): String?

    /*
    ------------------------Player------------------------
     */
    fun createPlayer(name: String, email: String, password: String): Int?
    fun getPlayers(limit: Int, skip: Int, username: String?): List<Player>

    fun deleteToken(playerId: Int): Boolean
    fun getPlayerById(id: Int): Player?
    fun getPlayerByEmail(email: String): Player?
    fun getPlayerByName(name: String): Player?
    fun updatePlayer(id: Int, details: String?, image: ByteArray?): Boolean

    /*
    ------------------------Game------------------------

     */
    fun createGame(name: String, developer: String, genres: List<String>): Int?
    fun getGameDetailsById(id: Int): Game?
    fun getGameDetailsByName(name: String): Game?
    fun getGamesListBy(genres: List<String>, developer: String, limit: Int, gameName:String?): List<Game>

    fun getGamesListByName(gameName: String?, limit: Int): List<Game>
    fun getDevelopers(): List<String>
    fun getGenres(): List<String>

    /*
    ------------------------Session------------------------
     */
    fun createSession(capacity: Int, gid: Int, date: Instant): Int?
    fun addPlayerToSession(session: Session, pid: Int)
    fun getSessionDetails(sid: Int): Session?
    fun getSessionsListBy(game: Int?, date: Instant?, state: String?, pid: Int?): List<Session>
    fun deleteSession(sid: Int): Boolean

    fun removePlayerFromSession(sid: Int, pid: Int): Boolean?

    fun updateSession(sid: Int, capacity: Int): Boolean

    fun updateSession(sid: Int, date: Instant): Boolean

    /*
    ------------------------Invites------------------------
     */
    fun invitePlayerToSession(sid: Int, fromPid: Int, toPid: Int): Boolean
    fun getInvites(pid: Int): List<Invite>
}