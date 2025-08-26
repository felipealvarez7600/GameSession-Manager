import { button, div, div2, form, input, label, li, p, ul } from "../DSL.js";
import GameServices from "../Services/GameServices.js";

export async function gamePartialNameView(mainContent,divSelect) {
    const state = {
        games: []
    };

    const gameSearchForm = form(
        {},
        async (event) => {
            event.preventDefault();
            const formData = new FormData(event.target);
            const gameName = formData.get("gameName");

            const limit = 5;
            const skip = 0;


            state.games = await GameServices.getPartialGamesByName(gameName, limit, skip);

            const previousGameList = mainContent.querySelector('#gameList');
            if (previousGameList) {
                previousGameList.remove();
            }

            if (gameName) {
                mainContent.appendChild(
                    div2({ class: divSelect },
                        displayGames(mainContent, state.games)
                    )
                );
            }
        },
        div(
            label({ for: "gameName", class: "label" }, "Game Name: "),
            input({ type: "search", name: "gameName", id: "gameName", class: "input", oninput: "this.form.dispatchEvent(new Event('submit'))" }),
        )
    );

    return div(
        gameSearchForm,
    );
}

function displayGames(mainContent, games) {
    const previousGameList = mainContent.querySelector('#gameList');
    if (previousGameList) {
        previousGameList.remove();
    }
    console.log(games);

    const gameList = games.data.length === 0 ? p({ class: "p last" }, "No games found.") : ul(
        ...games.data.map((game, index) => {
            const listItemClass = index === games.data.length - 1 ? "button list-last" : "button list";
            const gameNameButton = button({ type: "button", class: listItemClass }, game.name);
            gameNameButton.addEventListener("click", async () => {
                const searchInput = document.getElementById('gameName');
                if (searchInput) {
                    searchInput.value = game.name;
                }
                // Remove the game list
                gameList.remove();
            });

            return li(
                gameNameButton
            );
        })
    );

    gameList.id = 'gameList';
    return gameList;
}
