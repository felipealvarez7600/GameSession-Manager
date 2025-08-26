package pt.isel.ls.project.services

import org.junit.Test
import pt.isel.ls.project.storage.memory.SessionsDataMem
import kotlin.test.assertFails
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SessionsServicesTests {
    private val sessionsDataMem = SessionsDataMem()
    private val sessionsServices = SessionsServices(sessionsDataMem)

    @Test
    fun createPlayerServiceTest1() {
        val (name, email) = "André" to "email123@gmail.com"
        val (token, id) = sessionsServices.createPlayerService(name, email, "ReallyCoolPsswd")
        assert(token.isNotEmpty())
        assert(id > 0)
    }

    @Test
    fun createPlayerServiceTest2() {
        val (name1, email1) = "João" to "email1234@gmail.com"
        val (name2, email2) = "João" to "email1234@gmail.com"
        sessionsServices.createPlayerService(name1, email1, "ReallyCoolPsswd")
        assertFails { sessionsServices.createPlayerService(name2, email2, "ReallyCoolPsswd") }
    }
    @Test
    fun getPlayerByIdServiceTest() {
        val (name, email) = "Roger" to "benficaegrande@gmail.com"
        val createdPlayer = sessionsServices.createPlayerService(name, email, "ReallyCoolPsswd")
        val (player, image) = sessionsServices.getPlayerByIdService(createdPlayer.second)
        assert(player.name == name)
        assert(player.email == email)
    }
    @Test
    fun getPlayerByEmailServiceTest() {
        val (name, email) = "Roger123" to "benficaemaior@gmail.com"
        sessionsServices.createPlayerService(name, email, "ReallyCoolPsswd")
        val player = sessionsServices.getPlayerByEmailService(email)
        assert(player.name == name)
        assert(player.email == email)
    }
    @Test
    fun getPlayerByEmailServiceTest2(){
        val (name, email) = "diogo" to "diogo123@gmail.com"
        sessionsServices.createPlayerService(name, email, "ReallyCoolPsswd")
        assertFails { sessionsServices.getPlayerByEmailService("eqwewqeqweqweqwe@gmail.com") }
    }

    @Test
    fun getPlayerByIdServiceTest2() {
        val (name, email) = "diogoA" to "diogo1234@gmail.com"
        sessionsServices.createPlayerService(name, email, "ReallyCoolPsswd")
        assertFails { sessionsServices.getPlayerByIdService(-1) }
    }

    @Test
    fun getPlayersTest() {
        val (name1, email1) = "diogo" to "diogo123@gmail.com"
        val (name2, email2) = "Roger123" to "benficaemaior@gmail.com"
        sessionsServices.createPlayerService(name1, email1, "ReallyCoolPsswd")
        sessionsServices.createPlayerService(name2, email2, "ReallyCoolPsswd")
        val players1 = sessionsServices.getPlayers(2, 0, null)
        assertEquals(2, players1.size)
        val players2 = sessionsServices.getPlayers(2, 0, "diogo")
        assertEquals(1, players2.size)

    }

    @Test
    fun testUpdatePlayerService() {
        val (name1, email1) = "diogo" to "diogo123@gmail.com"
        sessionsServices.createPlayerService(name1, email1, "ReallyCoolPsswd")
        sessionsServices.updatePlayerService(1, "Hello Its me Diogo", null)
        val player = sessionsServices.getPlayerByIdService(1)
        assertEquals("Hello Its me Diogo", player.first.details)
    }

    @Test
    fun testCreateGameServices() {
        val name = "Call of Duty: Modern Warfare 3"
        val developer = "Infinity Ward"

        val genres = listOf("Action", "First-Person Shooter", "Multiplayer")

        val gameId = sessionsServices.createGameService(name, developer, genres)

        assertNotNull(gameId)
        assertTrue(gameId > 0)
    }

    @Test
    fun testGameAlreadyExistsExceptions() {
        val name = "Call of Duty: Modern Warfare 3"
        val developer = "Infinity Ward"
        val genres = listOf("Action", "First-Person Shooter", "Multiplayer")

        sessionsServices.createGameService(name, developer, genres)

        assertFailsWith<Exception> {
            sessionsServices.createGameService(name, developer, genres)
        }
    }

    @Test
    fun testGetGameDetailsService() {
        val name = "Call of Duty: Modern Warfare 3"
        val developer = "Infinity Ward"
        val genres = listOf("Action", "First-Person Shooter", "Multiplayer")

        val gameId = sessionsServices.createGameService(name, developer, genres)

        val game = sessionsServices.getGameDetailsService(gameId)

        assertNotNull(game)
    }

    @Test
    fun testGetGamesListByService() {
        val name1 = "Call of Duty: Modern Warfare 3"
        val developer1 = "Infinity Ward"
        val genres1 = listOf("Action", "First-Person Shooter", "Multiplayer")

        val name2 = "Call of Duty: Modern Warfare 2"
        val developer2 = "Infinity Ward"
        val genres2 = listOf("Action", "First-Person Shooter", "Multiplayer")

        sessionsServices.createGameService(name1, developer1, genres1)
        sessionsServices.createGameService(name2, developer2, genres2)

        val games = sessionsServices.getGamesListByService(genres1, developer1, 2, 0, null)

        assertNotNull(games)
        assertTrue(games.isNotEmpty())
    }

    @Test
    fun testCreateSessionService() {
        val capacity = 10
        val name = "Call of Duty: Modern Warfare 3"
        val developer = "Infinity Ward"
        val genres = listOf("Action", "First-Person Shooter", "Multiplayer")
        val date = Instant.now().plusSeconds(1000000)

        val gameId = sessionsServices.createGameService(name, developer, genres)

        val sessionId = sessionsServices.createSessionService(capacity, gameId, date)

        assertNotNull(sessionId)
        assertTrue(sessionId > 0)
    }

    @Test
    fun testAddPlayerToSessionService() {
        // Game Info
        val gameName = "Call of Duty: Modern Warfare 3"
        val gameDeveloper = "Infinity Ward"
        val genres = listOf("Action", "First-Person Shooter", "Multiplayer")

        // Session Info
        val date = Instant.now().plusSeconds(10000)
        val capacity = 10

        // Player Info
        val (name, email) = "André" to "email123@gmail.com"

        val id = sessionsServices.createPlayerService(name, email, "ReallyCoolPsswd").second
        val gameId = sessionsServices.createGameService(gameName, gameDeveloper, genres)
        val sessionId = sessionsServices.createSessionService(capacity, gameId, date)

        val session = sessionsServices.addPlayerToSessionService(sessionId, id)

        assertNotNull(session)
        assertTrue(session.playersId.contains(id))
    }

    @Test
    fun testGetSessionDetailsService() {
        val capacity = 10
        val date = Instant.now().plusSeconds(10000)
        val name = "Call of Duty: Modern Warfare 3"
        val developer = "Infinity Ward"
        val genres = listOf("Action", "First-Person Shooter", "Multiplayer")

        val gameId = sessionsServices.createGameService(name, developer, genres)
        val sessionId = sessionsServices.createSessionService(capacity, gameId, date)

        val session = sessionsServices.getSessionDetailsService(sessionId)

        assertNotNull(session)
        assertTrue(session.capacity == capacity)
        assertTrue(session.date == date)
        assertTrue(session.gameId == gameId)
    }

    @Test
    fun testGetSessionsListByService() {
        val capacity = 10
        val date = Instant.now().plusSeconds(10000)
        val gameName1 = "Call of Duty: Modern Warfare 3"
        val developer = "Infinity Ward"
        val genres = listOf("Action", "First-Person Shooter", "Multiplayer")

        val gameId1 = sessionsServices.createGameService(gameName1, developer, genres)

        sessionsServices.createSessionService(capacity, gameId1, date)
        sessionsServices.createSessionService(capacity, gameId1, date)

        val sessions = sessionsServices.getSessionsListByService(gameId1, date, "open", null, 2, 0)

        assertNotNull(sessions)
        assertTrue(sessions.isNotEmpty())
        assertTrue(sessions.size == 2)
    }

    @Test
    fun testInvitePlayerToSessionService() {
        val capacity = 10
        val date = Instant.now().plusSeconds(10000)
        val name = "Call of Duty: Modern Warfare 3"
        val developer = "Infinity Ward"
        val genres = listOf("Action", "First-Person Shooter", "Multiplayer")

        val gameId = sessionsServices.createGameService(name, developer, genres)
        val sessionId = sessionsServices.createSessionService(capacity, gameId, date)

        val (name1, email1) = "André" to "andré@gmail.com"
        val (name2, email2) = "João" to "joao@gmail.com"

        val playerId1 = sessionsServices.createPlayerService(name1, email1, "ReallyCoolPsswd").second
        val playerId2 = sessionsServices.createPlayerService(name2, email2, "ReallyCoolPsswd").second

        sessionsServices.addPlayerToSessionService(sessionId, playerId1)
        sessionsServices.invitePlayerToSession(sessionId, playerId1, playerId2)

    }

    @Test
    fun testGetPlayerInvitesService() {
        val capacity = 10
        val date = Instant.now().plusSeconds(10000)
        val name = "Call of Duty: Modern Warfare 3"
        val developer = "Infinity Ward"
        val genres = listOf("Action", "First-Person Shooter", "Multiplayer")

        val gameId = sessionsServices.createGameService(name, developer, genres)
        val sessionId = sessionsServices.createSessionService(capacity, gameId, date)

        val (name1, email1) = "André" to "andré@gmail.com"
        val (name2, email2) = "João" to "joao@gmail.com"

        val playerId1 = sessionsServices.createPlayerService(name1, email1, "ReallyCoolPsswd").second
        val playerId2 = sessionsServices.createPlayerService(name2, email2, "ReallyCoolPsswd").second

        sessionsServices.addPlayerToSessionService(sessionId, playerId1)
        sessionsServices.invitePlayerToSession(sessionId, playerId1, playerId2)

        val invites = sessionsServices.getInvites(playerId2)
        assertEquals(1, invites.size)
    }
}