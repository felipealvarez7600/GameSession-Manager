package pt.isel.ls.project.storage.memory
import pt.isel.ls.project.storage.SessionsData
import pt.isel.ls.project.storage.model.*
import java.time.Instant
import java.util.*

class SessionsDataMem : SessionsData {

    private val tokens = hashSetOf<Token>()

    private val users = hashSetOf<Player>()

    private val games = hashSetOf<Game>()

    private val sessions = hashSetOf<Session>()

    private val invites = hashSetOf<Invite>()

    private val inviteTimerForExpiration = 300.toLong() // 5 minutes

    private val tokenTimerForExpiration = 1800.toLong() // 30 minutes

    /**
     * Function that receives a token and returns the player id if the token is valid
     */
    override fun authorizedPlayer(token: String): Int? {
        val playerToken =  tokens.find { it.value == token }
        return if (playerToken != null && playerToken.expiration.isAfter(Instant.now())) {
            val newToken = playerToken.copy(expiration = Instant.now().plusSeconds(tokenTimerForExpiration))
            tokens.remove(playerToken)
            tokens.add(newToken)
            playerToken.playerId
        }
        else{
            null
        }
    }
    /**
     * Function that receives a player id and returns a token
     */
    override fun createToken(playerId: Int): String? {
        return if (users.any { it.id == playerId }){
            val token = UUID.randomUUID().toString()
            tokens.add(Token(token, playerId, Instant.now().plusSeconds(tokenTimerForExpiration)))
            token
        }else{
            null
        }
    }

    /**
     * Function that receives a name and an email and returns the player id
     */
    override fun createPlayer(name: String, email: String, password:String): Int{
        return if (users.isEmpty()){
            val id = 1
            users.add(Player(id,name, email, password = password))
            id
        }else{
            val id = users.size + 1
            users.add(Player(id,name, email, password = password))
            id
        }
    }

    /**
     * Function that receives a player id and deletes the token
     */
    override fun deleteToken(playerId: Int): Boolean {
        return tokens.removeIf { it.playerId == playerId }
    }
    /**
     * Function that receives a limit and skip and returns a list of players
     */
    override fun getPlayers(limit: Int, skip: Int, username: String?): List<Player> {
        if(skip >= users.size) return emptyList()
        val to = if(skip + limit > users.size) users.size else skip + limit
        return if(username != null){
            users.filter { it.name.startsWith(username) }.drop(skip).take(to)
        } else {
            users.toList().subList(skip, to)
        }
    }
    /**
     * Function that receives a player id and returns the player
     */
    override fun getPlayerById(id: Int): Player? {
        return users.find { it.id == id }
    }
    /**
    Function that receives an email and returns the player
     */
    override fun getPlayerByEmail(email: String): Player? {
        return users.find { it.email == email }
    }

    /**
     * Function that receives a name and returns the player
     */
    override fun getPlayerByName(name: String): Player? {
        return users.find { it.name == name }
    }

    /**
     * Function that receives a player id and updates the player details
     */
    override fun updatePlayer(id: Int, details: String?, image: ByteArray?): Boolean {
        val player = users.find { it.id == id } ?: return false
        val newPlayer = player.copy(details = details, image = image)
        users.remove(player)
        users.add(newPlayer)
        return true
    }

    /**
     *  Function that receives a name, a developer and a set of genres and returns the game id
     */
    override fun createGame(name: String, developer: String, genres: List<String>): Int {
        val newGenres = genres.toHashSet()
        return if (games.isEmpty()){
            val id = 1
            games.add(Game(games.size + 1, name, developer, newGenres))
            id
        }else{
            val id = games.size + 1
            games.add(Game(id, name, developer, newGenres))
            id
        }
    }

    /**
     * Function that receives a game id and returns the game
     */
    override fun getGameDetailsById(id: Int): Game? {
        return games.find { it.id == id }
    }
    /**
     * Function that receives a name and returns the game
     */
    override fun getGameDetailsByName(name: String): Game? {
        return games.find { it.name == name }
    }
    /**
     * Function that receives a set of genres, a developer and a limit and returns a list of games
     */
    override fun getGamesListBy(genres: List<String>, developer: String, limit: Int, gameName:String?): List<Game> {
        val to = if (limit > games.size) games.size else limit
        return if (gameName != null){
            games.filter { it.genres.containsAll(genres) && it.developer == developer && it.name.startsWith(gameName)}.subList(0, to)
        }else{
            games.filter { it.genres.containsAll(genres) && it.developer == developer }.subList(0, to)
        }
    }

    override fun getGamesListByName(gameName: String?, limit: Int): List<Game> {
        val to = if (limit > games.size) games.size else limit
        return if (gameName != null){
            games.filter { it.name.startsWith(gameName)}.subList(0, to)
        }else{
            games.toList().subList(0, to)
        }
    }

    /**
     * Function that returns a list of developers
     */
    override fun getDevelopers(): List<String> {
        return games.map { it.developer }.distinct()
    }

    /**
     * Function that returns a list of genres
     */
    override fun getGenres(): List<String> {
        return games.flatMap { it.genres }.distinct()
    }

    /**
     * Function that receives a capacity, a game id, a date and returns the session id
     */
    override fun createSession(capacity: Int, gid: Int, date: Instant): Int {
        return if(sessions.isEmpty()){
            val id = 1
            sessions.add(Session(id, capacity, date, gid, hashSetOf(), SessionState.OPEN))
            id
        }else{
            val id = sessions.size + 1
            sessions.add(Session(id, capacity, date, gid, hashSetOf(), SessionState.OPEN))
            id
        }
    }

    /**
     * Function that receives a session and a player id and adds the player to the session
     */
    override fun addPlayerToSession(session: Session, pid: Int) {
        session.playersId.add(pid)
    }

    /**
     * Function that receives a session id and returns the session
      */
    override fun getSessionDetails(sid: Int): Session? {
        return sessions.find { it.id == sid }
    }

    /**
     * Function that receives a game id, a date, a state and a limit and returns a list of sessions that match the parameters
     */
    override fun getSessionsListBy(game: Int?, date: Instant?, state: String?, pid: Int?): List<Session> {
        return sessions.filter {
                    (date == null || it.gameId == game) &&
                    (date == null || it.date <= date) &&
                    (state == null || it.state == SessionState.valueOf(state.uppercase(Locale.getDefault()))) &&
                    (pid == null || it.playersId.contains(pid))
        }
    }

    /**
     * Function that receives a session id and deletes the session
     */
    override fun deleteSession(sid: Int): Boolean {
        return sessions.removeIf{
            it.id == sid
        }
    }

    /**
     * Function that receives a session id and a player id and removes the player from the session
     */
    override fun removePlayerFromSession(sid: Int, pid: Int): Boolean? {
        return sessions.firstOrNull{it.id == sid}?.playersId?.removeIf { it == pid }
    }

    /**
     * Function that receives a session id and capacity and updates the session capacity
     */
    override fun updateSession(sid: Int, capacity: Int): Boolean {
        val newSession = sessions.firstOrNull{ it.id == sid }?.copy(capacity= capacity) ?: return false
        sessions.removeIf { it.id == sid }
        sessions.add(newSession)
        return true
    }

    /**
     * Function that receives a session id and a date and updates the session date
     */
    override fun updateSession(sid: Int, date: Instant): Boolean {
        val newSession = sessions.firstOrNull{ it.id == sid }?.copy(date=date) ?: return false
        sessions.removeIf { it.id == sid }
        sessions.add(newSession)
        return true
    }

    /**
     * Function that receives a session id, the player id that sent the invite and the player id that received the invite and returns true if the invite was sent
     */
    override fun invitePlayerToSession(sid: Int, fromPid: Int, toPid: Int): Boolean {
        val invite = Invite(fromPid, toPid, sid, Instant.now().plusSeconds(inviteTimerForExpiration))
        invites.add(invite)
        return true
    }

    /**
     * Function that receives a player id and returns a list of that player invites
     */
    override fun getInvites(pid: Int): List<Invite>{
        invites.removeIf { it.expiration.isBefore(Instant.now()) }
        return invites.filter { it.toPid == pid }.toList()
    }

}