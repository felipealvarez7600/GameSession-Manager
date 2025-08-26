package pt.isel.ls.project.services.exceptionType

sealed class SessionExceptions(override val message : String) : Exception(message){
    data object SessionNotFound : SessionExceptions(message = "Session not found")
    data object SessionBadParameter : SessionExceptions(message = "Session bad parameter")
    data object SessionAlreadyExists : SessionExceptions(message = "Session already exists")
    data object SessionFull : SessionExceptions(message = "Session at full capacity")
    data object DatabaseError : SessionExceptions(message = "Database connection error")
}