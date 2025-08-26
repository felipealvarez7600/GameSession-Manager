import {a, button, div, div2, h1, li, p, ul} from "../DSL.js";
import SessionServices from "../Services/SessionServices.js";
import PlayerServices from "../Services/PlayerServices.js";
import {playersView} from "./playerView.js";
import sessionServices from "../Services/SessionServices.js";
import handlers from "../handlers.js";


export async function sessionDetailsView(mainContent, url){
    const sessionId = window.location.hash.split("/")[1]
    const session = await SessionServices.getSessionDetails(sessionId, url)
    const playerId = Number(window.sessionStorage.getItem("id"))
    const playerNamesPromises = Array.from(session.playersId).map(async listPlayerId => {
        const playerName = await PlayerServices.getPlayerName(listPlayerId, url)
        return div(
            a({
                href: `#player/${listPlayerId}`,
                class: "link-primary"
            }, playerName),
        )
    })
    const join = button({ type: "button", id: "joinButton", class: "button primary"}, "Join Session")
    const joinSession =  async () => {
            await PlayerServices.joinSession(playerId, sessionId, url)
            await handlers.getSessionDetails(mainContent)
        }
    join.addEventListener("click", joinSession)
    const leave =  button({ type: "button", id: "leaveButton", class: "button primary"}, "Leave Session")
    leave.addEventListener("click", async () => {
            await sessionServices.leaveSession(playerId, sessionId, url)
            window.location.href = "#sessionsSearch"
        }
    );
    const playerNames = await Promise.all(playerNamesPromises)
    const playerSearch = session.playersId.includes(playerId) ? div2( { class: "div centered" },
        await playersView(mainContent, url, sessionId, "div lists players down")
    ): p()
    const update = session.playersId.includes(playerId) ? a({href: "#sessions/" + session.id + "/update", class: "button primary"}, "Update") : p()

    return div(
        div2({class: "div centered-up"},
            h1("Session Details"),
            ul(
                li("Game : ", a({
                    href: "#gamesDetails/" + session.gameId,
                    class: "link-primary"
                }, session.gameName)),
                li("Date : " + session.date.slice(0, 19).replace("T", " ")),
                li("State : " + session.state),
                li("Capacity : " + session.capacity),
                li("Players : ", div2(
                    {id: "playersInSession", class: "container space-between"},
                    ...playerNames
                    )
                ),
            ),
            session.playersId.includes(playerId) || session.state !== "OPEN" ? p() : join,
            update,
            p(),
            session.playersId.includes(playerId) ? leave : p(),
        ),
        playerSearch
    )
}