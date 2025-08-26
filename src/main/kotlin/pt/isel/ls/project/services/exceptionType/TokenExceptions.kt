package pt.isel.ls.project.services.exceptionType

sealed class TokenExceptions(override val message: String) : Exception(message){
    data object TokenMissingParameter : TokenExceptions(message = "Missing parameter on token")
    data object TokenNotAuthorized : TokenExceptions(message = "Token not authorized")
}