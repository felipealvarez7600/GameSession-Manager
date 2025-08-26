package pt.isel.ls.project.storage

import org.junit.Assert.assertEquals
import org.junit.Test
import pt.isel.ls.project.storage.memory.SessionsDataMem
import pt.isel.ls.project.storage.model.Game
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SessionsDataMemTests {
    /*
    ------------------------Player Tests------------------------
     */

    //test to create a player given a name and an email
    @Test
    fun testCreatePlayer() {

        val name = "Arnaldo Pereira"
        val email = "arnaldinho@gmail.com"
        val classUnderTest = SessionsDataMem()

        val player = classUnderTest.createPlayer(name, email, "ReallyCoolPsswd")// returns a token and the ID of the player

       assertNotNull(player)
    }

    //test to get a player given an ID
    @Test
    fun testGetPlayerById() {

        val name1 = "gonçalo"
        val email1 = "gonçalo@gmail.com"
        val name2 = "André"
        val email2 = "andre@gmail.com"
        val classUnderTest = SessionsDataMem()

        classUnderTest.createPlayer(name1, email1, "ReallyCoolPsswd")// returns a token and the ID of the player
        val player2 = classUnderTest.createPlayer(name2, email2, "ReallyCoolPsswd")// returns a token and the ID of the player
        assertNotNull(player2)
        val result = classUnderTest.getPlayerById(player2) // returns the player with the given ID

        assertNotNull(result)
        assertEquals(name2, result.name)
        assertEquals(email2, result.email)
    }

    @Test
    fun testGetPlayerByEmail() {

        val name1 = "gonçalo"
        val email1 = "gonçalo@gmail.com"
        val name2 = "André"
        val email2 = "andre@gmail.com"
        val classUnderTest = SessionsDataMem()

        classUnderTest.createPlayer(name1, email1, "ReallyCoolPsswd")// returns a token and the ID of the player
        classUnderTest.createPlayer(name2, email2, "ReallyCoolPsswd")// returns a token and the ID of the player
        val result = classUnderTest.getPlayerByEmail(email2) // returns the player with the given ID

        assertNotNull(result)
        assertEquals(name2, result.name)
        assertEquals(email2, result.email)
    }

    @Test
    fun testGetPlayers() {
        val name1 = "gonçalo"
        val email1 = "gonçalo@gmail.com"
        val name2 = "André"
        val email2 = "andre@gmail.com"
        val classUnderTest = SessionsDataMem()
        classUnderTest.createPlayer(name1, email1, "ReallyCoolPsswd")// returns a token and the ID of the player
        classUnderTest.createPlayer(name2, email2, "ReallyCoolPsswd")// returns a token and the ID of the player
        val result1 = classUnderTest.getPlayers(10, 0, null) // returns a list of players
        assertEquals(2, result1.size)
        val result2 = classUnderTest.getPlayers(10, 0, "gonçalo") // returns a list of players
        assertEquals(1, result2.size)
    }

    @Test
    fun testUpdatePlayer() {
        val name1 = "gonçalo"
        val email1 = "gonçalo@gmail.com"
        val classUnderTest = SessionsDataMem()
        classUnderTest.createPlayer(name1, email1, "ReallyCoolPsswd")
        val result = classUnderTest.updatePlayer(1, "New Description", null)
        assertTrue(result)
        val player = classUnderTest.getPlayerById(1)
        assertTrue(player != null)
        assertEquals("New Description", player.details)
    }



    /*
    ------------------------Game Tests------------------------
     */

    @Test
    fun testCreateGame() {

        val name = "Call of Duty: Modern Warfare 3"
        val developer = "Infinity Ward"

        val genres = listOf("Action", "First-Person Shooter", "Multiplayer")

        val classUnderTest = SessionsDataMem()

        val gameId = classUnderTest.createGame(name, developer, genres)

        assertNotNull(gameId)
        assertTrue(gameId > 0)
    }

    @Test
    fun testGetGameDetails() {

        val name = "Call of Duty: Modern Warfare 3"
        val developer = "Infinity Ward"

        val genres = listOf("Action", "First-Person Shooter", "Multiplayer")

        val classUnderTest = SessionsDataMem()

        val gameId :Int = classUnderTest.createGame(name, developer, genres)

        val game :Game? = classUnderTest.getGameDetailsById(gameId)

        assertNotNull(game)
        assertEquals(name, game.name)
        assertEquals(developer, game.developer)
        assertContentEquals(genres, game.genres)
    }

    @Test
    fun testGetGamesListBy() {

        val name1 = "Call of Duty: Modern Warfare 3"
        val developer1 = "Infinity Ward"
        val genres1 = listOf("Action", "First-Person Shooter", "Multiplayer")

        val name2 = "Call of Duty: Modern Warfare 2"
        val developer2 = "Infinity Ward"
        val genres2 = listOf("Action", "First-Person Shooter", "Multiplayer")

        val classUnderTest = SessionsDataMem()

        classUnderTest.createGame(name1, developer1, genres1)
        classUnderTest.createGame(name2, developer2, genres2)

        val genres = listOf("Action", "Multiplayer")

        val games :List<Game> = classUnderTest.getGamesListBy(genres, "Infinity Ward", 10, null)

        assertNotNull(games)
        assertEquals(2, games.size)

    }

    /*
    ------------------------Session Tests------------------------
     */

    @Test
    fun testCreateSession() {

        val capacity = 10
        val date = java.time.Instant.now()

        val classUnderTest = SessionsDataMem()
        val gameName = "Call of Duty: Modern Warfare 3"
        val gameId = classUnderTest.createGame(
            gameName,
            "Infinity Ward",
            listOf("Action", "First-Person Shooter", "Multiplayer")
        )
        val sessionId = classUnderTest.createSession(capacity, gameId, date)

        assertNotNull(sessionId)
        assertTrue(sessionId > 0)

    }

    @Test
    fun testAddPlayerToSession() {

        val capacity = 10
        val date = java.time.Instant.now()

        val classUnderTest = SessionsDataMem()
        val gameName = "Call of Duty: Modern Warfare 3"
        val gameId = classUnderTest.createGame(
            gameName,
            "Infinity Ward",
            listOf("Action", "First-Person Shooter", "Multiplayer")
        ) // Black Ops 2 >>>

        val player = classUnderTest.createPlayer(
            "Arnaldo Pereira",
            "arnaldinho@gmail.com",
            "ReallyCoolPsswd"

        )


        val sessionId = classUnderTest.createSession(capacity, gameId, date)

        val session = classUnderTest.getSessionDetails(sessionId)!!
        assertNotNull(player)
        classUnderTest.addPlayerToSession(session, player)

        val sessionChanged = classUnderTest.getSessionDetails(sessionId)!!

        assert(sessionChanged.playersId.contains(player))

    }

    @Test
    fun testGetSessionDetails() {

        val capacity = 10
        val date = java.time.Instant.now()

        val classUnderTest = SessionsDataMem()
        val gameName = "Call of Duty: Modern Warfare 3"
        val gameId = classUnderTest.createGame(
            gameName,
            "Infinity Ward",
            listOf("Action", "First-Person Shooter", "Multiplayer")
        )
        val sessionId = classUnderTest.createSession(capacity, gameId, date)

        val session = classUnderTest.getSessionDetails(sessionId)

        assertNotNull(session)
        assertEquals(capacity, session.capacity)
        assertEquals(date, session.date)
        assertEquals(gameId, session.gameId)
    }

    @Test
    fun testGetSessionsListBy() {

        val capacity = 10
        val date1 = java.time.Instant.now()

        val classUnderTest = SessionsDataMem()
        val gameName = "Call of Duty: Modern Warfare 3"
        val gameId = classUnderTest.createGame(
            gameName,
            "Infinity Ward",
            listOf("Action", "First-Person Shooter", "Multiplayer")
        )
        val sessionId1 = classUnderTest.createSession(capacity, gameId, date1)

        val checkDate = java.time.Instant.now()

        val date2 = java.time.Instant.now()
        val sessionId2 = classUnderTest.createSession(capacity, gameId, date2)

        val player1 = classUnderTest.createPlayer(
            "Arnaldo Pereira",
            "arnaldinho@gmail.com",
            "ReallyCoolPsswd"
        )
        assertNotNull(player1)

        val player2 = classUnderTest.createPlayer(
            "Gonçalo Pereira",
            "gonçalo@gmail.com",
            "ReallyCoolPsswd"
        )
        assertNotNull(player2)

        val session1 = classUnderTest.getSessionDetails(sessionId1)!!
        val session2 = classUnderTest.getSessionDetails(sessionId2)!!

        classUnderTest.addPlayerToSession(session1, player1)
        classUnderTest.addPlayerToSession(session2, player2)
        val sessions = classUnderTest.getSessionsListBy(gameId, checkDate, "open", player1)

        assertNotNull(sessions)
        assertEquals(1, sessions.size)
    }


    /*
    ------------------------Invites Tests------------------------
     */

    @Test
    fun testInvitePlayerToSession(){
        val classUnderTest = SessionsDataMem()
        val playerId1 = classUnderTest.createPlayer("Arnaldo Pereira", "arnaldo@gmail.com", "ReallyCoolPsswd")
        val playerId2 = classUnderTest.createPlayer("Gonçalo Pereira", "gonçalo@gmail.com", "ReallyCoolPsswd")
        val gameId = classUnderTest.createGame("Call of Duty: Modern Warfare 3", "Infinity Ward", listOf("Action", "First-Person Shooter", "Multiplayer"))
        val sessionId = classUnderTest.createSession(10, gameId, java.time.Instant.now() + java.time.Duration.ofDays(1))
        val session = classUnderTest.getSessionDetails(sessionId)
        assertNotNull(session)
        classUnderTest.addPlayerToSession(session, playerId1)
        val invite = classUnderTest.invitePlayerToSession(sessionId, playerId1, playerId2)
        assertTrue(invite)
    }

    @Test
    fun testGetInvites(){
        val classUnderTest = SessionsDataMem()
        val playerId1 = classUnderTest.createPlayer("Arnaldo Pereira", "arnaldo@gmail.com", "ReallyCoolPsswd")
        val playerId2 = classUnderTest.createPlayer("Gonçalo Pereira", "gonçalo@gmail.com", "ReallyCoolPsswd")
        val gameId = classUnderTest.createGame("Call of Duty: Modern Warfare 3", "Infinity Ward", listOf("Action", "First-Person Shooter", "Multiplayer"))
        val sessionId = classUnderTest.createSession(10, gameId, java.time.Instant.now() + java.time.Duration.ofDays(1))
        val session = classUnderTest.getSessionDetails(sessionId)
        assertNotNull(session)
        classUnderTest.addPlayerToSession(session, playerId1)
        val invite = classUnderTest.invitePlayerToSession(sessionId, playerId1, playerId2)
        assertTrue(invite)
        val invites = classUnderTest.getInvites(playerId2)
        assertEquals(1, invites.size)
    }
}