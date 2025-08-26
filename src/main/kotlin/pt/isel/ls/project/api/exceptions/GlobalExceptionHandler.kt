package pt.isel.ls.project.api.exceptions

import org.http4k.core.Response
import org.http4k.core.Status
import pt.isel.ls.project.api.winterboot.annotations.injection.Insect
import pt.isel.ls.project.api.winterboot.annotations.injection.Pesticide
import pt.isel.ls.project.services.exceptionType.GameExceptions
import pt.isel.ls.project.services.exceptionType.PlayerExceptions
import pt.isel.ls.project.services.exceptionType.SessionExceptions
import pt.isel.ls.project.services.exceptionType.TokenExceptions

@Pesticide
class GlobalExceptionHandler {
    @Insect(GameExceptions::class)
    fun gameExceptionHandler(ex: GameExceptions): Response{
        return when(ex){
            GameExceptions.GameNotFound -> createResponse(Status.NOT_FOUND, "Game not found", ex)
            GameExceptions.DatabaseError -> createResponse(Status.INTERNAL_SERVER_ERROR, "Database error", ex)
            GameExceptions.GameAlreadyExists -> createResponse(Status.BAD_REQUEST, "Game already exists", ex)
            GameExceptions.GameBadParameter -> createResponse(Status.BAD_REQUEST, "Request parameter incorrect", ex)
            GameExceptions.GameMissingParameter -> createResponse(Status.BAD_REQUEST, "Request parameter missing", ex)
            GameExceptions.DevelopersNotFound -> createResponse(Status.NOT_FOUND, "Developers not found", ex)
            GameExceptions.GenresNotFound -> createResponse(Status.NOT_FOUND, "Genres not found", ex)
        }
    }

    @Insect(PlayerExceptions::class)
    fun playerExceptionsHandler(ex: PlayerExceptions): Response{
        return when(ex){
            PlayerExceptions.PlayerInvalidImage -> createResponse(Status.BAD_REQUEST, "Player invalid image. Must be either a png or jpg file", ex)
            PlayerExceptions.PlayerInvalidPassword -> createResponse(Status.UNAUTHORIZED, "Invalid password", ex)
            PlayerExceptions.PlayerNotFound -> createResponse(Status.NOT_FOUND, "Player not found", ex)
            PlayerExceptions.DatabaseError -> createResponse(Status.INTERNAL_SERVER_ERROR, "Database error", ex)
            PlayerExceptions.PlayerAlreadyExists -> createResponse(Status.BAD_REQUEST, "Player already exists", ex)
            PlayerExceptions.PlayerBadParameter -> createResponse(Status.BAD_REQUEST, "Request parameter incorrect", ex)
            PlayerExceptions.PlayerMissingParameter -> createResponse(Status.BAD_REQUEST, "Request parameter missing", ex)
            PlayerExceptions.PlayerNotAuthorized -> createResponse(Status.UNAUTHORIZED, "Player not authorized", ex)
            PlayerExceptions.PlayerNotInSession -> createResponse(Status.FORBIDDEN, "Player not in session", ex)
            PlayerExceptions.PlayerAlreadyInSession -> createResponse(Status.BAD_REQUEST, "Player already in session", ex)
        }
    }

    @Insect(SessionExceptions::class)
    fun sessionExceptionHandler(ex: SessionExceptions): Response{
        return when(ex){
            SessionExceptions.SessionNotFound -> createResponse(Status.NOT_FOUND, "Session not found", ex)
            SessionExceptions.DatabaseError -> createResponse(Status.INTERNAL_SERVER_ERROR, "Database error", ex)
            SessionExceptions.SessionAlreadyExists -> createResponse(Status.BAD_REQUEST, "Session already exists", ex)
            SessionExceptions.SessionBadParameter -> createResponse(Status.BAD_REQUEST, "Request parameter incorrect", ex)
            SessionExceptions.SessionFull -> createResponse(Status.FORBIDDEN, "Session is full", ex)
        }
    }

    @Insect(TokenExceptions::class)
    fun tokenExceptionHandler(ex: TokenExceptions): Response{
        return when(ex){
            TokenExceptions.TokenNotAuthorized -> createResponse(Status.UNAUTHORIZED, "Invalid token", ex)
            TokenExceptions.TokenMissingParameter -> createResponse(Status.NOT_FOUND, "Token not found", ex)
        }
    }

    private fun createResponse(status: Status, title: String, ex: Exception): Response {
        val message = "{\n" +
                "   \"title\": \"${title}\",\n" +
                "   \"status\": \"${status.code}\",\n" +
                "   \"detail\": \"${ex.message}\"\n" +
                "}"

        return Response(status).header("Content-Type","application/problem+json").body(
            message
        )
    }
}