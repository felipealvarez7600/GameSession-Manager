import router from "./router.js"
import handlers from "./handlers.js"
import authServices from "./Services/authServices.js";

// For more information on ES6 modules, see https://www.javascripttutorial.net/es6/es6-modules/ or
// https://www.w3schools.com/js/js_modules.asp

window.addEventListener('load', loadHandler)
window.addEventListener('hashchange', hashChangeHandler)

window.addEventListener('beforeunload', async (event) => {
    event.preventDefault();
    if(sessionStorage.getItem('token') === null) return null;
    else {
        try {
            const response = await authServices.logout();
            if (response.ok) {
                return null;
            } else {
                alert('Token could not be invalidated. Are you sure you want to leave this page?');
            }
        } catch (error) {
            console.error('Error while invalidating token:', error);
            return 'An error occurred. Are you sure you want to leave this page?';
        }
    }
});
/**
 * Function to load the routes and handlers
 */
function loadHandler(){
    router.addRouteHandler("home", handlers.home)
    router.addRouteHandler("signup", handlers.createPlayer)
    router.addRouteHandler("login", handlers.login)
    router.addRouteHandler("players", handlers.getPlayers)
    router.addRouteHandler("player", handlers.getPlayerHome)
    router.addRouteHandler("player/:id", handlers.getPlayerProfile)
    router.addRouteHandler("player/:id/update", handlers.updatePlayerProfile)
    router.addRouteHandler("player/:id/invites", handlers.getPlayerInvites)
    router.addRouteHandler("gameSearch", handlers.getGameSearch)
    router.addRouteHandler("gameCreate", handlers.createGame)
    router.addRouteHandler("gamesDetails/:id", handlers.getGameDetails)
    router.addRouteHandler("sessionsSearch", handlers.sessionSearch)
    router.addRouteHandler("sessions", handlers.getSessions)
    router.addRouteHandler("createSession", handlers.createSession)
    router.addRouteHandler("sessions/:id", handlers.getSessionDetails)
    router.addRouteHandler("sessions/:id/update", handlers.updateSessionDetails)
    router.addDefaultNotFoundRouteHandler(() => window.location.hash = "home")

    hashChangeHandler()
}

/**
 * Function to handle hash changes
 */
function hashChangeHandler(){
    const mainContent = document.getElementById("mainContent")
    const path =  window.location.hash.replace("#", "")
    const handler = router.getRouteHandler(path)
    handler(mainContent)
}