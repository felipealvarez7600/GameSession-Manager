import {a, button, div, div2, form, h1, li, p, ul2} from "../DSL.js";
import PlayerServices from "../Services/PlayerServices.js";
import SessionServices from "../Services/SessionServices.js";
import {playersPartialNameView} from "./playersPartialNameView.js";


export async function playersView(mainContent, API_BASE_URL, sessionId, position){
    const limit = 5+1;
    const state = {
        skip: 0,
        players: []
    };

    const nextButton = button({ type: "button", id: "nextButton", class: "button secondary" }, "Next");
    const prevButton = button({ type: "button", id: "prevButton", class: "button secondary" }, "Previous");

    nextButton.addEventListener("click", async () => {
        state.skip += limit-1;
        document.querySelector("form").dispatchEvent(new Event("submit"));
    });

    prevButton.addEventListener("click", async () => {
        state.skip -= limit-1;
        if (state.skip < 0) state.skip = 0;
        document.querySelector("form").dispatchEvent(new Event("submit"));
        toggleButtons();
    });

    function toggleButtons() {
        nextButton.disabled = state.players.length < limit;
        prevButton.disabled = state.skip <= 0;
    }
    const params = {};
    const playerInfo =  form(
        {},
        async (event) => {
            event.preventDefault();
            console.log("searching players")
            const username = document.getElementById("username").value;
            params.limit = limit;
            params.skip = state.skip;

            if (username) {
                params.username = username;
            } else {
                delete params.username;
            }

            state.players = await PlayerServices.getPlayersByPartialUsername(params, API_BASE_URL);
            const playersNew = state.players.length < limit ? state.players : state.players.slice(0, -1);
            const displayClass = sessionId < 0 ? "div centered-below" : "div centered-bottom";
            mainContent.appendChild(
                div2( { class: displayClass},
                    displayPlayers(mainContent, playersNew, sessionId, API_BASE_URL)
                )

            );
            toggleButtons();
        },
        div(
            await playersPartialNameView(mainContent, API_BASE_URL, position),
            p(),
            button({ type: "submit", class: "button primary"}, "Search")
        )
    );
    return div2( { class: "div centered-up"},
        sessionId < 0 ? h1("Search Players") : h1("Invite other players"),
        playerInfo,
        p(),
        div2( { class: "container space-between" },
            prevButton,
            nextButton
        )

    )
}

function displayPlayers(mainContent, players, sessionId, API_BASE_URL) {
    const playerId = window.sessionStorage.getItem("id");
    // Remove previous player list if it exists
    const previousPlayerList = mainContent.querySelector('#playerList');
    if (previousPlayerList) {
        previousPlayerList.remove();
    }

    const playerList = players.length === 0 ? p("No players found.") : ul2( { class: "container space-between"},
        div2({ class: "container space-between"},
            ...players.map(player => {
                const inviteButton = button({ type: "button", id: "inviteButton" , class: "button primary"}, "Invite Player");
                inviteButton.addEventListener(
                    "click",
                    async () => {
                        const data = await SessionServices.sendInvite(playerId, player.id, sessionId, API_BASE_URL)
                        alert("Invitation sent!")
                    }
                )
                return li(
                    a({
                        href: `#player/${player.id}`,
                        class: "link-primary"
                    }, player.name),
                    sessionId !== -1 ? inviteButton : div()
                )
            })
        )

    );

    playerList.id = 'playerList';
    return playerList;

}