import {button, div, div2, form, h1, input, label, option, p, select} from "../DSL.js";
import SessionServices from "../Services/SessionServices.js";
import {displaySessions} from "./displayLists/displaySessions.js";
import {playersPartialNameView} from "./playersPartialNameView.js";
import PlayerServices from "../Services/PlayerServices.js";
import {gamePartialNameView} from "./GamePartialNameView.js";


export async function sessionSearchView(mainContent, url){
    const limit = 5 + 1 // Add 1 to check if there are more sessions to display
    const state = {
        skip: 0,
        sessions: []
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
        nextButton.disabled = state.sessions.length < limit;
        prevButton.disabled = state.skip <= 0;
    }

    const params = {};

    const sessionSearchForm = form(
        {},
        async (event) => {
            event.preventDefault();
            const formData = new FormData(event.target);
            const game = document.getElementById("gameName").value;
            const date = formData.get("date");
            const time = formData.get("time");
            const selectedState = formData.get("state");
            const username = document.getElementById("username").value;
            params.limit = limit;
            params.skip = state.skip;

            if(username) {
                const newParams = {
                    username: username,
                    limit: 1,
                    skip: 0
                };
                const player = await PlayerServices.getPlayersByPartialUsername(newParams, url);
                if(player && player[0]) {
                    params.pid = player[0].id;
                }
            }

            if (game) {
                params.game = game;
            } else {
                delete params.game;
            }

            if (date && time) {
                const dateTime = new Date(date + 'T' + time + 'Z');
                if (dateTime instanceof Date && !isNaN(dateTime)) {
                    params.date = dateTime.toISOString().substring(0, 22) + "Z";
                }
            }

            if (selectedState === "OPEN" || selectedState === "CLOSED") {
                params.state = selectedState;
            } else {
                delete params.state;
            }

            state.sessions = await SessionServices.fetchSessions(params, url);
            const sessionsNew = state.sessions.length < limit ? state.sessions : state.sessions.slice(0, -1);
            mainContent.appendChild(
                div2( { class: "div sessions" },
                    displaySessions(mainContent, sessionsNew)
                )
            )
            toggleButtons();
        },
        div(
            await gamePartialNameView(mainContent, "div lists C"),
            //input({ type: "text", name: "game", id: "game" }),
            p(),
            await playersPartialNameView(mainContent, url, "div lists"),
            p(),
            div2(
                { id: "date-time", class: "container space-between" },
                div2(
                    { id: "date", class: "item"},
                    label({ for: "date" }, "Date : "),
                    input({ type: "date", name: "date", id: "date" })
                ),
                div2(
                    { id: "time", class: "item" },
                    label({ for: "time" }, "Time : "),
                    input({ type: "time", name: "time", id: "time" })
                )
            ),
            p(),
            label({ for: "state", class: "item" }, "State : "),
            select({ name: "state", id: "state"},
                option({ value: "" }, "Select state"),
                option({ value: "OPEN" }, "Open"),
                option({ value: "CLOSED" }, "Closed")
            ),
            p(),
            button({ type: "submit", class: "button primary" }, "Search"),
            p()
        )
    );

    return div2( { class: "div centered-up"},
        h1("Sessions"),
        sessionSearchForm,
        prevButton,
        nextButton,
    )
}
