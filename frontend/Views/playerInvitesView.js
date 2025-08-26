import {a, button, div, div2, h1, p} from "../DSL.js";
import PlayerServices from "../Services/PlayerServices.js";


export async function playerInvitesView(API_BASE_URL){
    const playerId = window.location.hash.split("/")[1];
    const invites = await PlayerServices.getPlayerInvites(playerId, API_BASE_URL);
    return div2( {class: "div centered-up" },
        h1("Invites"),
        invites.data.length === 0 ? p("No invites yet") :
            div(
                invites.data.map(invite => {
                    const session = invite.session;
                    const fromPlayer = invite.fromPlayer;
                    const joinButton = button({ type: "button", class: "button primary" }, "Join")
                    joinButton.addEventListener("click", async () => {
                        PlayerServices.joinSession(playerId, session.id, API_BASE_URL)
                            .then(() => {
                                window.location.href = `#sessions/${session.id}`
                            })
                    })
                    return div(
                        p("Game: ",
                            a({ href: `#gamesDetails/${session.gameId}` }, session.gameName)
                        ),
                        p("Date : " + session.date),
                        p("State : " + session.state),
                        p("From: ",
                            a({ href: `#player/${fromPlayer.id}` }, fromPlayer.name)
                        ),
                        joinButton
                    )
                })
            )
    )
}