import {button, div, div2, h1} from "./DSL.js"
import AuthServices from "./Services/authServices.js"
import {registerView} from "./Views/registerView.js"
import {gameSearchView} from "./Views/GameSearch.js"
import {playersView} from "./Views/playerView.js"
import GameServices from "./Services/GameServices.js"
import {gameDetails} from "./Views/gameDetails.js"
import {playerProfileView} from "./Views/playerProfileView.js";
import {playerHomeView} from "./Views/playerHomeView.js";
import {homeView} from "./Views/homeView.js";
import {sessionSearchView} from "./Views/sessionSearchView.js";
import {displaySessions} from "./Views/displayLists/displaySessions.js";
import SessionServices from "./Services/SessionServices.js";
import {parseHashParams} from "./utils/queryAndParamUtils.js";
import {sessionCreateView} from "./Views/sessionCreateView.js";
import {sessionDetailsView} from "./Views/sessionDetailsView.js";
import {gameCreationView} from "./Views/GameCreation.js";
import {playerInvitesView} from "./Views/playerInvitesView.js";
import {appNavBar} from "./utils/appNavBar.js";
import {sessionUpdateView} from "./Views/sessionUpdateView.js";
import {playerUpdateView} from "./Views/playerUpdateView.js";
import {loginView} from "./Views/loginView.js";

function getFullUrlUpToHostPort() {
    return window.location.protocol + "//" + window.location.host + "/";
}
// API baser URL
const API_BASE_URL = getFullUrlUpToHostPort();

/**
 * Handler for the main Home page
 */
function home (mainContent){
    document.title = "Home"
    mainContent.replaceChildren(
        homeView()
    )
}

/**
 * Handler to get all players given a certain username
 * Also supporting partial username search
 */

async function getPlayers(mainContent){
    document.title = "Players";
    mainContent.replaceChildren(
        appNavBar(),
        await playersView(mainContent, API_BASE_URL, -1, "div lists players up")
    )
}

/**
 * Handler for the player registration page
 */
async function createPlayer(mainContent){
    document.title = "Sign Up"
    mainContent.replaceChildren(
        registerView( await AuthServices.register)
    )
}

async function login(mainContent){
    document.title = "Log In"
    mainContent.replaceChildren(
        loginView( await AuthServices.login)
    )
}

/**
 * Handler for the player home page for the authorized user
 */
async function getPlayerHome(mainContent){
    document.title = "Player Home"
    mainContent.replaceChildren(
        appNavBar(),
        await playerHomeView()
    )
}

/**
 * Handler for the player profile page displaying the name and the email of the player
 */
async function getPlayerProfile(mainContent){
    document.title = "Player Profile"
    mainContent.replaceChildren(
        appNavBar(),
        await playerProfileView(API_BASE_URL)
    )
}

/**
 * Handler for the player profile update page
 */
async function updatePlayerProfile(mainContent){
    document.title = "Update Player"
    mainContent.replaceChildren(
        appNavBar(),
        await playerUpdateView(API_BASE_URL)
    )

}

/**
 * Handler for the game search page
 */
async function getGameSearch(mainContent){
    document.title = "Game Search"
    const gameSearch =  GameServices.getGames;
    const genres =  GameServices.getGenres;
    const developers =  GameServices.getDevelopers;
    mainContent.replaceChildren(
        appNavBar(),
        await gameSearchView(mainContent,gameSearch, genres, developers)
    )
}

/**
 * Handler for the game creation page
 */
async function createGame(mainContent){
    document.title = "Create Game"
    const game = GameServices.createGame
    const genres =  GameServices.getGenres;
    const developers =  GameServices.getDevelopers;
    mainContent.replaceChildren(
        appNavBar(),
        await gameCreationView(game, genres, developers)
    )
}

/**
 * Handler for the game details page
 */
async function getGameDetails(mainContent){
    document.title = "Game Details"
    const details = GameServices.getGameDetails
    mainContent.replaceChildren(
        appNavBar(),
        await gameDetails(details)
    )

}

/**
 * Handler for the session search page
 */
async function sessionSearch(mainContent) {
    document.title = "Sessions Search";
    mainContent.replaceChildren(
        appNavBar(),
        await sessionSearchView(mainContent, API_BASE_URL)
    );
}


/**
 * Get sessions and display them in case the search was made from either the player profile or the game details
 */
async function getSessions(mainContent) {
    document.title = "Sessions";
    const limit = 5;
    const state = {
        skip: 0,
        sessions: []
    };

    const nextButton = button({ type: "button", id: "nextButton", class: "button secondary" }, "Next");
    const prevButton = button({ type: "button", id: "prevButton", class: "button secondary" }, "Previous");
    const searchSessions = button({ type: "button", id: "searchSessions",  class: "button primary"  }, "Search Sessions");

    searchSessions.addEventListener("click", async () => {
        window.location.href = "#sessionsSearch";
    });

    nextButton.addEventListener("click", async () => {
        state.skip += limit;
        await updateSessions(mainContent, state.skip, limit);
    });

    prevButton.addEventListener("click", async () => {
        state.skip -= limit;
        if (state.skip < 0) state.skip = 0;
        await updateSessions(mainContent, state.skip, limit);
    });

    function toggleButtons() {
        nextButton.disabled = state.sessions.length < limit;
        prevButton.disabled = state.skip <= 0;
    }

    async function updateSessions(mainContent, skip, limit) {
        const params = parseHashParams();
        params.limit = limit;
        params.skip = skip;
        state.sessions = await SessionServices.fetchSessions(params, API_BASE_URL);
        const sessionList = displaySessions(mainContent, state.sessions);
        toggleButtons();
        mainContent.replaceChildren(
            appNavBar(),
            div2( { class: "div centered-up"},
                h1("Sessions"),
                sessionList,
                div(searchSessions),
                prevButton,
                nextButton,
            )
        );
    }
    // Initial load
    await updateSessions(mainContent, state.skip, limit);
}

/**
 * Create a session
 */
async function createSession(mainContent){
    document.title = "Create Session"
    mainContent.replaceChildren(
        appNavBar(),
        await sessionCreateView(mainContent, API_BASE_URL)
    );

}

/**
 * Get the session details
 */
async function getSessionDetails(mainContent) {
    document.title = "Session Details"
    mainContent.replaceChildren(
        appNavBar(),
        await sessionDetailsView(mainContent, API_BASE_URL)
    )

}

/**
 * Get the player invites
 */
async function getPlayerInvites(mainContent) {
    document.title = "Invites";
    mainContent.replaceChildren(
        appNavBar(),
        await playerInvitesView(API_BASE_URL)
    )
}

/**
 * Update the session details
 */
async function updateSessionDetails(mainContent){
    document.title = "Update Session"
    mainContent.replaceChildren(
        appNavBar(),
        await sessionUpdateView(API_BASE_URL)
    )
}


export const handlers = {
    home,
    login,
    createPlayer,
    getPlayers,
    getPlayerHome,
    getPlayerProfile,
    updatePlayerProfile,
    getGameSearch,
    getGameDetails,
    sessionSearch,
    createSession,
    getSessions,
    getSessionDetails,
    createGame,
    getPlayerInvites,
    updateSessionDetails
}

export default handlers