import {a, nav} from "../DSL.js";

/**
 * Function to create the navigation bar for the application if the user is logged in
 */
export function appNavBar(){
    const playerId = window.sessionStorage.getItem("id");

    return nav( {class: "nav"},
        a(
            {
                href: "#player",
                class: "link-secondary"
            },
            "Player Home"
        ),
        a(
            {
                href: "#gameCreate",
                class: "link-secondary"
            },
            "Create a Game"
        ),
        a(
            {
                href: "#gameSearch",
                class: "link-secondary"
            },
            "Game Search"
        ),
        a(
            {
                href: "#createSession",
                class: "link-secondary"
            },
            "Create New Session"
        ),
        a(
            {
                href: "#sessionsSearch",
                class: "link-secondary"
            },
            "Session Search"
        ),
        a(
            {
                href: "#players",
                class: "link-secondary"
            },
            "Search for a player profile"
        ),
        a(
            {
                href: "#player/" + playerId + "/invites",
                class: "link-secondary"
            },
            "Invites"
        ),
        a(
            {
                href: "#player/" + playerId,
                class: "link-secondary"
            },
            "My Profile"
        ),
    )
}