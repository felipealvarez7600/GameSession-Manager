import {button, div, form, h1, input, label, p, h2, hr, div2, span} from "../DSL.js";
import {gamePartialNameView} from "./GamePartialNameView.js";


export async function gameSearchView(mainContent, gameSearch, genres, developers) {
    const genresList = await genres();
    const developersList = await developers();

    function getCheckboxes(items, typeOfItem) {
        return items.data.map(item => {
            const checkbox = input({
                type: "checkbox",
                name: typeOfItem,
                value: item,
                id: `${typeOfItem}-${item}`,
            });

            if (typeOfItem === 'developer') {
                checkbox.addEventListener('click', () => {
                    const checkboxes = document.querySelectorAll(`input[name="${typeOfItem}"]`);
                    checkboxes.forEach(cb => {
                        if (cb !== checkbox) cb.checked = false;
                    });
                });
            }

            const labelElement = label({ for: checkbox.id }, item);

            return div2({ class: "div checkbox" }, checkbox, labelElement);
        });
    }


    const nextButton = button({ type: "button", class: "button secondary" }, "Next");
    const prevButton = button({ type: "button", class: "button secondary" }, "Previous");

    nextButton.addEventListener("click", async () => {
        const limit = parseInt(document.querySelector("#gamesSearchLimit").value);
        const skip = parseInt(document.querySelector("#gamesSearchSkip").value) + limit;
        if (skip < 0) document.querySelector("#gamesSearchSkip").value = 0
        else document.querySelector("#gamesSearchSkip").value = skip;
        document.querySelector("form").dispatchEvent(new Event("submit"));
    });

    prevButton.addEventListener("click", async () => {
        const limit = parseInt(document.querySelector("#gamesSearchLimit").value);
        const skip = parseInt(document.querySelector("#gamesSearchSkip").value) - limit;
        if (skip < 0) document.querySelector("#gamesSearchSkip").value = 0
        else document.querySelector("#gamesSearchSkip").value = skip;

        document.querySelector("form").dispatchEvent(new Event("submit"));
    });

    function toggleButtons(games) {
        const limit = parseInt(document.querySelector("#gamesSearchLimit").value);
        const skip = parseInt(document.querySelector("#gamesSearchSkip").value);
        nextButton.disabled = games.data.length < limit + 1;
        prevButton.disabled = skip === 0;
    }

    return div2({ class: "div selection" },
        h1("Game Search"),
        form(
            {},
            async (e) => {
                e.preventDefault();
                const limit = parseInt(document.querySelector("#gamesSearchLimit").value) || 2;
                const skip = parseInt(document.querySelector("#gamesSearchSkip").value) || 0;
                document.querySelector("#gamesSearchSkip").value = skip;
                document.querySelector("#gamesSearchLimit").value = limit;
                const checkedGenres = document.querySelectorAll('input[name="genres"]:checked');
                const checkedDevelopers = document.querySelectorAll('input[name="developer"]:checked');
                const selectedGenres = Array.from(checkedGenres).map(genre => genre.value);
                const selectedDevelopers = Array.from(checkedDevelopers).map(developer => developer.value);
                const gameQuery = {
                    genres: selectedGenres,
                    developer: selectedDevelopers.join(',')
                };
                const gameName = document.querySelector("#gameName").value;

                const games = await gameSearch(gameQuery, limit + 1, skip, gameName);
                const searchResultsDiv = document.querySelector("#searchResults");
                searchResultsDiv.innerHTML = "";

                const gamesData = games.data;

                if (gamesData.length === 0) {
                    searchResultsDiv.innerHTML = "<p>No games found</p>";
                } else {

                    for (let i = 0; i < limit; i++) {
                        const game = gamesData[i];
                        if (!game) break;

                        const gameButton = button(
                            {
                                type: "button",
                                class: "button secondary"
                            },
                            "Get Details"
                        );
                        gameButton.addEventListener("click", () => {
                            window.location.href = "#gamesDetails/" + game.id;
                        });

                        const gameDiv = div(
                            h2(game.name),
                            p(`Genres: ${game.genres}`),
                            p(`Developer: ${game.developer}`),
                            gameButton
                        );

                        searchResultsDiv.appendChild(gameDiv);
                    }
                }
                toggleButtons(games);
            },
            div2(
                { id: "upper-part" },
                input({ type: "hidden", id: "gamesSearchLimit", name: "limit" }),
                input({ type: "hidden", id: "gamesSearchSkip", name: "skip" }),
                label({ for: "game" }, "Game : "),
                await gamePartialNameView(mainContent, "div lists B"),
                p(),
                div2(
                    //get the css style
                    { id: "search-box" },
                    div2(
                        { id: "genres" },
                        h2("Genres"),
                        getCheckboxes(genresList, "genres") // Pass the retrieved genres list
                    ),

                    div2(
                        { id: "developer" },
                        h2("Developer"),
                        getCheckboxes(developersList, "developer")
                    )),
                p(),
                button({ type: "submit", class: "button primary" }, "Search Games")
            ), div2(
                { id: "pagination" },
                p(),
                prevButton,
                span("  "),
                nextButton
            ),
            hr(),
            div2(
                { id: "searchResults" }
            )
        )
    )
}

