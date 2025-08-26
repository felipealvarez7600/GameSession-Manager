package pt.isel.ls.project.services.exceptionType


sealed class GameExceptions(override val message: String) : Exception(message) {
    data object GameNotFound : GameExceptions(message = "Game not found")
    data object GameAlreadyExists : GameExceptions(message = "Game already exists")
    data object GameMissingParameter : GameExceptions(message = "Game missing parameter")
    data object GameBadParameter : GameExceptions(message = "Game bad parameter")
    data object DatabaseError : GameExceptions(message = "Database connection error")
    data object GenresNotFound : GameExceptions(message = "Genres not found")
    data object DevelopersNotFound : GameExceptions(message = "Developers not found")
}
