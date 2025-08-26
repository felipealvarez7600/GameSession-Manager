package pt.isel.ls

import org.http4k.server.Jetty
import org.http4k.server.asServer
import pt.isel.ls.project.api.winterboot.Winter

fun main() {
    Winter.addSinglePageApplication("frontend").setup().asServer(Jetty(3030)).start()
}