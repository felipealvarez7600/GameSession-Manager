
// Save all routes in an array
const routes = []
/**
 * Default route handler for unknown routes that goes to the main Home page

 */
let notFoundRouteHandler = () => { throw new Error("Route handler for unknown routes not defined") }

/**
 * Function to add a route and its handler to the routes array
 */
function addRouteHandler(path, handler){
    routes.push({path, handler})
}

/**
 * Function to add a default route handler for unknown routes
 */
function addDefaultNotFoundRouteHandler(notFoundRH) {
    notFoundRouteHandler = notFoundRH
}

/**
 * Function to get the route handler for a given path
 */
function getRouteHandler(path) {
    // Remove query parameters if present
    const routePath = path.split("?")[0];

    // Check if the exact path exists in routes array
    const routeHandler = routes.find(route => {
        const routeSegments = route.path.split("/");
        const pathSegments = routePath.split("/");

        if (routeSegments.length !== pathSegments.length) {
            return false;
        }

        for (let i = 0; i < routeSegments.length; i++) {
            if (routeSegments[i] !== pathSegments[i] && !routeSegments[i].startsWith(":")) {
                return false;
            }
        }

        return true;
    });

    return routeHandler ? routeHandler.handler : notFoundRouteHandler;
}


const router = {
    addRouteHandler,
    getRouteHandler,
    addDefaultNotFoundRouteHandler
}

export default router