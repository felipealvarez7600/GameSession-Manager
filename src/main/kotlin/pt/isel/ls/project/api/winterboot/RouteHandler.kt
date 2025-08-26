package pt.isel.ls.project.api.winterboot

import org.http4k.core.ContentType
import org.http4k.routing.ResourceLoader
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.singlePageApp

class RouteHandler {
    private val methodProcessor = MethodProcessor()
    private val objectInstantiator = ObjectInstantiator()

    fun get(seedsMap: Map<String, Any>, controllerList: List<Class<*>>): List<RoutingHttpHandler> {
        val objControllerList = controllerList.map { objectInstantiator.instantiateObject(it,seedsMap) }
        return controllerList.map { methodProcessor.methodsToRoutes(objControllerList[controllerList.indexOf(it)], it) }.flatten()
    }
}