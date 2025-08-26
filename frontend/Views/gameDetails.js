import {div, h1, h2, ul, p, div2, li, hr, a, form, span, input, label, label2, select, option, button} from "../DSL.js";
import {buildQueryString} from "../utils/queryAndParamUtils.js";


export async function gameDetails(game) {
    const gameInfo = await game();
   // botão para sessions passar o name e não o ID
    const sessionsButton = button(
        {
            type: "button",
            id: "sessionsButton",
            class: "button secondary"
        },
        "Sessions"
    )

    sessionsButton.addEventListener("click", () => {
        window.location.href = "#sessions" + buildQueryString({game: gameInfo.name})
    })
    return (
        div(
            a(
                {
                    href: "#player",
                    class: "link-secondary"
                },
                "Home"
            ),
            h1('GameDetails'),
            div2(
                {class: "div centered"},
                ul(
                    h2('Name: ' + gameInfo.name),
                    h2('Developer: ' + gameInfo.developer),
                    // list of genres
                    h2('Genres:'),
                    ul(
                        gameInfo.genres.map(genres => {
                            return li(
                                p("genres : " + genres),
                            )
                        })
                    ),
                    sessionsButton
                )
            )
        )
    );
}