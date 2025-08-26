package pt.isel.ls.project.api

import kotlinx.serialization.json.*
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.AfterClass
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.project.api.model.GetInviteReturn
import pt.isel.ls.project.api.model.GetPlayerDetails
import pt.isel.ls.project.api.model.GetSessionReturn
import pt.isel.ls.project.api.winterboot.Winter
import pt.isel.ls.project.storage.model.Game
import pt.isel.ls.project.storage.model.Player
import pt.isel.ls.project.storage.model.Session
import java.sql.SQLException
import java.time.Instant
import kotlin.test.*


class SessionsMemServerTests {
    private val client = Winter.setup()
    private fun playerPostRequest() = Request(Method.POST,"/players").body(
        JsonObject(mapOf(
            "name" to JsonPrimitive("John Doe${(Math.random()*1000000).toInt()}"),
            "email" to JsonPrimitive("john.${(Math.random()*1000000).toInt()}doe@example.com"),
            "password" to JsonPrimitive("thisisaverysecurepassword")
        )).toString())
    private fun gamePostRequest() = Request(Method.POST,"/games").body(
        JsonObject(mapOf(
            "name" to JsonPrimitive("Destiny ${(Math.random()*1000000).toInt()}"),
            "developer" to JsonPrimitive("Bungie"),
            "genres" to JsonArray(listOf("FPS","MMO","RPG").map { JsonPrimitive(it) })
        )).toString()
    )
    private fun sessionPostRequest(gameName: String): Request{
        return Request(Method.POST,"/sessions").body(
            JsonObject(mapOf(
                "capacity" to JsonPrimitive("10"),
                "gameName" to JsonPrimitive(gameName),
                "date" to JsonPrimitive(Instant.now().plusSeconds(100000).toString())
            )).toString()
        )
    }
    @Test
    fun `Should add new player`(){
        val response = client(playerPostRequest())
        assertEquals(response.status, Status.CREATED)
        val json = Json.parseToJsonElement(response.body.toString()).jsonObject
        assertNotNull(json["token"])
        assertNotNull(json["playerId"])
    }

    @Test
    fun `Should login player`(){
        val player = playerPostRequest()
        val name = Json.parseToJsonElement(player.body.toString()).jsonObject["name"]!!.jsonPrimitive.content
        val password = Json.parseToJsonElement(player.body.toString()).jsonObject["password"]!!.jsonPrimitive.content
        client(player)
        val request = Request(Method.POST, "/authenticate").query("name", name).query("password", password)
        val response = client(request)
        val json = Json.parseToJsonElement(response.body.toString()).jsonObject
        assertNotNull(json["token"])
    }

    @Test
    fun `Should return player id from token`(){
        val (id, token) = getNewPlayer()
        val request = Request(Method.GET, "/authenticated").header("Authorization", "Bearer $token")
        val response = client(request)
        assertEquals(response.status, Status.OK)
        val json = Json.parseToJsonElement(response.body.toString()).jsonObject
        assertNotNull(json["id"])
        assertEquals(id, json["id"]?.jsonPrimitive?.int)
    }

    @Test
    fun `Should add new game`(){
        val player = getNewPlayer()
        val response = client(gamePostRequest().header("Authorization", "Bearer ${player.second}"))
        assertEquals(response.status, Status.CREATED)
        val json = Json.parseToJsonElement(response.body.toString()).jsonObject
        assertNotNull(json["gameId"])
    }

    @Test
    fun `Should add new session`(){
        val player = getNewPlayer()
        val gameName = createGameAndGetNameAndId(player.second).first
        val response = client(sessionPostRequest(gameName).header("Authorization", "Bearer ${player.second}"))
        assertEquals(response.status, Status.CREATED)
        val json = Json.parseToJsonElement(response.body.toString()).jsonObject
        assertNotNull(json["sessionId"])
    }

    @Test
    fun `Should return existing player`(){
        val (id, token) = getNewPlayer()
        val request = Request(Method.GET, "/players/${id}").header("Authorization", "Bearer $token")
        val response = client(request)
        assertEquals(response.status, Status.OK)
        val playerJson = Json.decodeFromString<GetPlayerDetails>(response.body.toString())
        assertEquals(id, playerJson.id)
    }

    @Test
    fun `Should update the player details`(){
        val (id, token) = getNewPlayer()
        val request = Request(Method.PUT, "/players/$id")
            .header("Authorization", "Bearer $token")
            .body("{\"details\": \"These are some dope details\"}")
        val response = client(request)
        assertTrue { response.status.successful }
        val detailsRequest = Request(Method.GET, "/players/${id}").header("Authorization", "Bearer $token")
        val detailsResponse = client(detailsRequest)
        val playerJson = Json.decodeFromString<GetPlayerDetails>(detailsResponse.body.toString())
        assertTrue { playerJson.details == "These are some dope details" }
    }

    @Test
    fun `Should return existing players`(){
        val playerIds = listOf(getNewPlayer(), getNewPlayer(), getNewPlayer())
        val request = Request(Method.GET, "/players")
            .query("limit",100.toString())
            .query("skip",0.toString())
        val response = client(request)
        assertEquals(response.status, Status.OK)
        val json = Json.parseToJsonElement(response.body.toString()).jsonObject["data"] ?: throw NullPointerException()
        val players = Json.decodeFromJsonElement<List<Player>>(json)
        assertTrue(playerIds.all {id ->
            players.any { player ->
                player.id == id.first
            }
        })
    }

    @Test
    fun `Should return existing game`(){
        val player = getNewPlayer()
        val gameName = createGameAndGetNameAndId(player.second)
        val request = Request(Method.GET, "/games/${gameName.second}").header("Authorization", "Bearer ${player.second}")
        val response = client(request)
        val game = Json.decodeFromString<Game>(response.body.toString())
        assertEquals(gameName.second, game.id)
    }

    @Test
    fun `Should return existing genres`(){
        val (_, token) = getNewPlayer()
        createGameAndGetNameAndId(token)
        val request = Request(Method.GET, "/genres")
        val response = client(request)
        val json = Json.parseToJsonElement(response.body.toString()).jsonObject["data"] ?: throw NullPointerException()
        val genres = Json.decodeFromJsonElement<List<String>>(json)
        assertTrue {
            genres.isNotEmpty()
            genres.contains("FPS")
            genres.contains("MMO")
            genres.contains("RPG")
        }
    }

    @Test
    fun `Should return existing developers`(){
        val (_, token) = getNewPlayer()
        createGameAndGetNameAndId(token)
        val request = Request(Method.GET, "/developers")
        val response = client(request)
        val json = Json.parseToJsonElement(response.body.toString()).jsonObject["data"] ?: throw NullPointerException()
        val genres = Json.decodeFromJsonElement<List<String>>(json)
        assertTrue {
            genres.isNotEmpty()
            genres.contains("Bungie")
        }
    }

    @Test
    fun `Should return existing games`(){
        val player = getNewPlayer()
        val gameNames = listOf(
            createGameAndGetNameAndId(player.second),
            createGameAndGetNameAndId(player.second),
            createGameAndGetNameAndId(player.second)
        )
        val request = Request(Method.GET, "/games").header("Authorization", "Bearer ${player.second}")
            .query("limit", 30.toString())
            .query("skip", 0.toString())
            .query("developer", "Bungie")
            .query("genres","FPS")
            .query("genres","MMO")

        val response = client(request)
        val json = Json.parseToJsonElement(response.body.toString()).jsonObject["data"] ?: throw NullPointerException()
        val games = Json.decodeFromJsonElement<List<Game>>(json)
        assertTrue(gameNames.all { name ->
            games.any{ game ->
                game.name == name.first
            }
        })
    }

    @Test
    fun `Should return existing session`(){
        val player = getNewPlayer()
        val gameName = createGameAndGetNameAndId(player.second).first
        val sessionId = createSessionsAndGetId(player.second, gameName)
        val request = Request(Method.GET, "/sessions/$sessionId").header("Authorization", "Bearer ${player.second}")
        val response = client(request)
        val session = Json.decodeFromString<GetSessionReturn>(response.body.toString())
        assertEquals(sessionId, session.id)
        assertEquals(gameName, session.gameName)
        assertEquals(10,session.capacity)
    }
    @Test
    fun `Should add existing player to session`(){
        val player = getNewPlayer()
        val gameName = createGameAndGetNameAndId(player.second).first
        val sessionId = createSessionsAndGetId(player.second, gameName)
        val request = Request(Method.PUT,
            "/sessions/$sessionId/players/${player.first}").header("Authorization", "Bearer ${player.second}")
        val response = client(request)
        val session = Json.decodeFromString<Session>(response.bodyString())
        assertEquals(sessionId, session.id)
        assertEquals(10,session.capacity)
        assertTrue(session.playersId.contains(player.first))
    }

    @Test
    fun `Should return existing sessions`(){
        val player = getNewPlayer()
        val gameName = createGameAndGetNameAndId(player.second).first
        val sessionIds = listOf(
            createSessionsAndGetId(player.second, gameName),
            createSessionsAndGetId(player.second, gameName),
            createSessionsAndGetId(player.second, gameName),
        )
        val request = Request(Method.GET, "/sessions")
                .header("Authorization", "Bearer ${player.second}")
                .query("limit", "10")
                .query("skip", "0")
                .query("date",Instant.now().plusSeconds(100000).toString())
                .query("game", gameName)
        val response = client(request)
        val json = Json.parseToJsonElement(response.body.toString()).jsonObject["data"] ?: throw NullPointerException()
        val sessions = Json.decodeFromJsonElement<List<GetSessionReturn>>(json)
        assertTrue(
            sessionIds.all {id ->
                sessions.any {session ->
                    session.id == id
                }
            }
        )
    }

    @Test
    fun `Should delete session`(){
        val player = getNewPlayer()
        val gameName = createGameAndGetNameAndId(player.second).first
        val sessionIds = listOf(
            createSessionsAndGetId(player.second, gameName),
            createSessionsAndGetId(player.second, gameName),
            createSessionsAndGetId(player.second, gameName),
        )
        val deleteRequest = Request(Method.DELETE, "/sessions/${sessionIds.first()}")
            .header("Authorization", "Bearer ${player.second}")
        val deleteResponse = client(deleteRequest)
        assertEquals(deleteResponse.status, Status.OK)
        val request = Request(Method.GET, "/sessions")
            .header("Authorization", "Bearer ${player.second}")
            .query("limit", "10")
            .query("skip", "0")
            .query("date",Instant.now().toString())
            .query("game", gameName)
        val response = client(request)
        val json = Json.parseToJsonElement(response.body.toString()).jsonObject["data"] ?: throw NullPointerException()
        val sessions = Json.decodeFromJsonElement<List<GetSessionReturn>>(json)
        assertFalse(
            sessions.any{it.id == sessionIds.first()}
        )

    }
    @Test
    fun `Should remove player from session`(){
        val player = getNewPlayer()
        val gameName = createGameAndGetNameAndId(player.second).first
        val sessionId = createSessionsAndGetId(player.second, gameName)
        val request = Request(Method.PUT,
            "/sessions/$sessionId/players/${player.first}").header("Authorization", "Bearer ${player.second}")
        val response = client(request)
        val session = Json.decodeFromString<Session>(response.bodyString())
        assertEquals(sessionId, session.id)
        assertEquals(10,session.capacity)
        assertTrue(session.playersId.contains(player.first))
        val removeRequest = Request(Method.DELETE, "/sessions/${sessionId}/players/${player.first}")
            .header("Authorization", "Bearer ${player.second}")
        client(removeRequest)
        val getSessionRequest = Request(Method.GET, "/sessions/${sessionId}")
            .header("Authorization", "Bearer ${player.second}")
        val getSessionResponse = client(getSessionRequest)
        val getSession = Json.decodeFromString<GetSessionReturn>(getSessionResponse.bodyString())
        assertFalse(getSession.playersId.contains(player.first))
    }

    @Test
    fun `Should update session capacity`(){
        val player = getNewPlayer()
        val gameName = createGameAndGetNameAndId(player.second).first
        val sessionId = createSessionsAndGetId(player.second, gameName)
        val request = Request(Method.PUT, "/sessions/$sessionId/")
            .header("Authorization", "Bearer ${player.second}")
            .query("capacity","100")
        client(request)
        val getSessionRequest = Request(Method.GET, "/sessions/${sessionId}")
            .header("Authorization", "Bearer ${player.second}")
        val getSessionResponse = client(getSessionRequest)
        val getSession = Json.decodeFromString<GetSessionReturn>(getSessionResponse.bodyString())
        assertTrue(getSession.capacity == 100)
    }

    @Test
    fun `Should update session date`(){
        val player = getNewPlayer()
        val gameName = createGameAndGetNameAndId(player.second).first
        val sessionId = createSessionsAndGetId(player.second, gameName)
        val date = Instant.now().plusSeconds(10000)
        val request = Request(Method.PUT, "/sessions/$sessionId/")
            .header("Authorization", "Bearer ${player.second}")
            .query("date",date.toString())
        client(request)
        val getSessionRequest = Request(Method.GET, "/sessions/${sessionId}")
            .header("Authorization", "Bearer ${player.second}")
        val getSessionResponse = client(getSessionRequest)
        val getSession = Json.decodeFromString<GetSessionReturn>(getSessionResponse.bodyString())
        assertEquals(getSession.date.toEpochMilli(), date.toEpochMilli())
    }

    @Test
    fun `Should send invite`(){
        val (id1, token1) = getNewPlayer()
        val (id2, _) = getNewPlayer()
        val (gameName, _) = createGameAndGetNameAndId(token1)
        val sessionId = createSessionsAndGetId(token1, gameName)
        val insertPlayerRequest = Request(Method.PUT,
            "/sessions/$sessionId/players/${id1}").header("Authorization", "Bearer $token1")
        client(insertPlayerRequest)
        val request = Request(Method.POST, "/sessions/$sessionId/players/$id1/invite/$id2").header("Authorization", "Bearer $token1")
        val response = client(request)
        assertTrue { response.status.successful }
    }

    @Test
    fun `Should get invites`(){
        val (id1, token1) = getNewPlayer()
        val (id2, token2) = getNewPlayer()
        val (gameName, _) = createGameAndGetNameAndId(token1)
        val sessionId = createSessionsAndGetId(token1, gameName)
        val insertPlayerRequest = Request(Method.PUT,
            "/sessions/$sessionId/players/${id1}").header("Authorization", "Bearer $token1")
        client(insertPlayerRequest)
        val sendInviteRequest = Request(Method.POST, "/sessions/$sessionId/players/$id1/invite/$id2").header("Authorization", "Bearer $token1")
        client(sendInviteRequest)
        val request = Request(Method.GET, "/players/$id2/invites").header("Authorization", "Bearer $token2")
        val response = client(request)
        val json = Json.parseToJsonElement(response.body.toString()).jsonObject["data"] ?: throw NullPointerException()
        val invites = Json.decodeFromJsonElement<List<GetInviteReturn>>(json)
        assertTrue {
            invites.any {
                it.session.id == sessionId && it.fromPlayer.id == id1
            }
        }
    }

    @Test
    fun `Should logout`(){
        val (id, token) = getNewPlayer()
        val request = Request(Method.DELETE, "/players/logout").header("Authorization", "Bearer $token").query("playerId",id.toString())
        val response = client(request)
        assertTrue { response.status.successful }
        val testAuthorizedRequest = Request(Method.GET, "/authenticated").header("Authorization", "Bearer $token")
        val testAuthorizedResponse = client(testAuthorizedRequest)
        assertTrue { testAuthorizedResponse.status.code == 401 }
    }

    @Test
    fun `Should return partial game name`(){
        val (_, token) = getNewPlayer()
        val (name, gameId) = createGameAndGetNameAndId(token)
        val partialName = name.substring(0,3)
        val request = Request(Method.GET, "/games/partial")
            .header("Authorization", "Bearer $token")
            .query("gameName",partialName)
            .query("limit",100.toString())
            .query("skip",0.toString())
        val response = client(request)
        val json = Json.parseToJsonElement(response.body.toString()).jsonObject["data"] ?: throw NullPointerException()
        val games = Json.decodeFromJsonElement<List<Game>>(json)
        assertTrue {
            games.any {
                it.id == gameId && it.name == name
            }
        }
    }

    private fun createSessionsAndGetId(token: String, gameName: String): Int{
        val sessionResponse = client(sessionPostRequest(gameName).header("Authorization", "Bearer $token"))
        val json = Json.parseToJsonElement(sessionResponse.body.toString()).jsonObject
        val id = json["sessionId"]?.jsonPrimitive?.int ?: throw NullPointerException()
        return id
    }

    private fun getNewPlayer(): Pair<Int, String>{
        val playerResponse = client(playerPostRequest())
        val playerJson = Json.parseToJsonElement(playerResponse.body.toString()).jsonObject
        val id = playerJson["playerId"]?.jsonPrimitive?.int ?: throw NullPointerException()
        val token = playerJson["token"]?.jsonPrimitive?.content ?: throw NullPointerException()
        return id to token
    }

    private fun createGameAndGetNameAndId(token: String): Pair<String, Int> {
        val request = gamePostRequest().header("Authorization", "Bearer $token")
        val response = client(request)
        val jsonResponse = Json.parseToJsonElement(response.body.toString()).jsonObject
        val jsonRequest =Json.parseToJsonElement(request.body.toString()).jsonObject
        val name = jsonRequest["name"]?.jsonPrimitive?.content ?: throw NullPointerException()
        val gameId = jsonResponse["gameId"]?.jsonPrimitive?.int ?: throw NullPointerException()

        return (name to gameId)
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun clearDatabase() {
            try {
                val dataSource = PGSimpleDataSource()
                val jdbcDatabaseURL = "jdbc:postgresql://localhost:5433/test?user=test&password=test"
                dataSource.setURL(jdbcDatabaseURL)
                dataSource.connection.use { connection ->
                    val statement = connection.createStatement()
                    // Query to get all table names in the public schema
                    val tablesQuery = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_type='BASE TABLE'"
                    val tablesResultSet = statement.executeQuery(tablesQuery)
                    while (tablesResultSet.next()) {
                        val tableName = tablesResultSet.getString("table_name")
                        // Truncate each table
                        val truncateQuery = "TRUNCATE TABLE $tableName CASCADE"
                        connection.prepareStatement(truncateQuery).use { preparedStatement ->
                            preparedStatement.executeUpdate()
                        }
                        println("Truncated table: $tableName")
                    }
                }
            } catch (e: SQLException) {
                println("SQL Database not initialized")
            }
        }
    }
}
