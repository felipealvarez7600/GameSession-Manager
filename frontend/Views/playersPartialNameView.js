import {button, div, div2, form, input, label, li,  p2, ul} from "../DSL.js";
import PlayerServices from "../Services/PlayerServices.js";


export async function playersPartialNameView(mainContent, API_BASE_URL, position){
    const state = {
        players: []
    };

    const params = {};
    const playerInfo =  form(
        {},
        async (event) => {
            event.preventDefault();
            const formData = new FormData(event.target);
            const username = formData.get("username");

            params.limit = 5;
            params.skip = 0;

            if (username) {
                params.username = username;
            }

            state.players = await PlayerServices.getPlayersByPartialUsername(params, API_BASE_URL);
            if(username) {
                mainContent.appendChild(
                    div2({class: position},
                        displayPlayers(mainContent, state.players)
                    )
                )
            } else {
                const playersList = mainContent.querySelector('#playerList');
                if (playersList) {
                    playersList.remove();
                }
            }
        },
        div(
            label({ for: "username", class: "label"}, "Username: "),
            input({ type: "search", name: "username", id: "username", class: "input", oninput: "this.form.dispatchEvent(new Event('submit'))" }),
        )
    );
    return div(
        playerInfo,
    )
}

function displayPlayers(mainContent, players) {
    const previousPlayerList = mainContent.querySelector('#playerList');
    if (previousPlayerList) {
        previousPlayerList.remove();
    }

    const playerList = players.length === 0 ? p2({class: "p last"}, "No players found.") : ul(
        ...players.map((player, index) => {

            const listItemClass = index === players.length - 1 ? "button list-last" : "button list";
            const playerNameButton = button({ type: "button", class: listItemClass }, player.name);
            playerNameButton.addEventListener("click", async () => {
                const searchInput = document.getElementById('username');
                if (searchInput) {
                    searchInput.value = player.name;
                }
                // Remove the player list
                playerList.remove()
            });

            return li(
                playerNameButton
            )
        })
    );

    playerList.id = 'playerList';
    return playerList;

}
