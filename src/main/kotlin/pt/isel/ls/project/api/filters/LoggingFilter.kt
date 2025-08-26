package pt.isel.ls.project.api.filters

import org.http4k.core.HttpHandler
import pt.isel.ls.project.api.winterboot.annotations.injection.Branch
import pt.isel.ls.project.api.winterboot.annotations.injection.Leaf
import pt.isel.ls.project.services.exceptionType.PlayerExceptions
import pt.isel.ls.project.storage.SessionsData

@Branch
class LoggingFilter(val data: SessionsData) {
    @Leaf
    fun log(next: HttpHandler): HttpHandler{
        return {  request ->
            println("Received new request: ${request.uri}: ${request.method}")
            val result = next(request)
            println("Responding: ${result.status}: ${result.headers}")
            result
        }
    }
}