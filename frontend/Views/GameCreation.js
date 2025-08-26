import {button, form, h1, input, label, p, h2, hr, div2} from "../DSL.js";


export async function gameCreationView(gameCreation, genres, developers) {

    const genresList = await genres();
    const developersList = await developers();

    function getCheckboxes(items, typeOfItem) {
        return items.data.map(item => {
            const checkbox = input({
                type: "checkbox",
                name: typeOfItem, // Group checkboxes by typeOfItem
                value: item, // Use value to identify the item
                id: `${typeOfItem}-${item}`, // Unique id for each checkbox
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
    const CreateButton = button(
        { type: "submit", class:"button primary" },
        "Create Game"
    );

    return div2({class: "div selection"},
        h1("Create Game"),
        form(
            {},
            async (e) => {
                e.preventDefault();
                const checkedGenres = document.querySelectorAll('input[name="genres"]:checked');
                const checkedDevelopers = document.querySelectorAll('input[name="developer"]:checked');
                const selectedGenres = Array.from(checkedGenres).map(genre => genre.value);
                const selectedDevelopers = Array.from(checkedDevelopers).map(developer => developer.value);
                const gameName = document.querySelector("#game").value;
                const gameBody = {
                    name: gameName,
                    developer: selectedDevelopers.join(','),
                    genres: selectedGenres
                };


                const game = await gameCreation(gameBody);

                window.location.href = "#gamesDetails/" + game.gameId;

            },
            div2(
                { id: "upper-part" },
                div2(
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
                label({ for: "game" }, "Game : "),
                input({ type: "text", name: "game", id: "game" }),
                p(),
                CreateButton

            ),
            hr(), // Horizontal line to separate upper and lower parts



        )
    )

}
