package pt.isel.ls.project.services.exceptionType

sealed class PlayerExceptions(override val message: String?) : Exception(message) {
    data object PlayerNotFound : PlayerExceptions(message = "Player not found")
    data object PlayerNotAuthorized : PlayerExceptions(message = "Player not authorized")
    data object PlayerInvalidImage : PlayerExceptions(message = "Player invalid image. Must be either a png or jpg file")
    data object PlayerInvalidPassword : PlayerExceptions(message = "Player invalid password")
    data object PlayerAlreadyExists : PlayerExceptions(message = "Player already exists")
    data object PlayerBadParameter : PlayerExceptions(message = "Player bad parameter")
    data object PlayerNotInSession : PlayerExceptions(message = "Player not in session")
    data object PlayerAlreadyInSession : PlayerExceptions(message = "Player already in session")
    data object PlayerMissingParameter : PlayerExceptions(message = "Player missing parameter")
    data object DatabaseError : PlayerExceptions(message = "Database connection error")
}