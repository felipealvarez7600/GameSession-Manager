package pt.isel.ls.project.api.filters

import org.http4k.core.HttpHandler
import pt.isel.ls.project.api.winterboot.annotations.injection.Branch
import pt.isel.ls.project.api.winterboot.annotations.injection.Leaf
import pt.isel.ls.project.services.exceptionType.PlayerExceptions
import pt.isel.ls.project.storage.SessionsData

@Branch
class AuthFilter(val data: SessionsData) {
    private val authorizedUris = setOf(
        "/games",
        "/sessions",
        "/players/"
    )
    @Leaf
    fun checkBearerToken(next: HttpHandler): HttpHandler{
        return {  request ->
            if(authorizedUris.any{ request.uri.path.contains(it) }){
                val header = request.header("Authorization")
                if(header != null){
                    val (type, token) = header.split(" ")
                    if(type == "Bearer" && data.authorizedPlayer(token) != null){
                        next(request)
                    }
                    else {
                        throw PlayerExceptions.PlayerNotAuthorized
                    }
                } else {
                    throw PlayerExceptions.PlayerNotAuthorized
                }
            } else {
                next(request)
            }
        }
    }
}