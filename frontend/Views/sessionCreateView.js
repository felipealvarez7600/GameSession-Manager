import {button, div, div2, form, h1, input, label, p} from "../DSL.js";
import SessionServices from "../Services/SessionServices.js";
import {gamePartialNameView} from "./GamePartialNameView.js";


export async function sessionCreateView(mainContent, API_BASE_URL){
    const params = {};
    const sessionInfo = form(
        {},
        async (event) => {
            event.preventDefault();
            const formData = new FormData(event.target);
            const game = document.getElementById("gameName").value;
            const date = formData.get("date");
            const time = formData.get("time");
            const capacity = formData.get("capacity")

            params.gameName = game;
            params.capacity = capacity
            const dateTime = new Date(date + 'T' + time + 'Z');
            if (dateTime instanceof Date && !isNaN(dateTime)) {
                params.date = dateTime.toISOString().substring(0, 22) + "Z";
            }

            const data = await SessionServices.createSession(params, API_BASE_URL)
            // Redirect to created session details
            window.location.hash = "sessions/" + data.sessionId;
        },
        div(
            await gamePartialNameView(mainContent, "div lists C"),
            p(),
            div2(
                { id: "date-time", class: "container space-between" },
                div2(
                    { id: "date", class: "item"},
                    label({ for: "date" }, "Date : "),
                    input({ type: "date", name: "date", id: "date", required: true})
                ),
                div2(
                    { id: "time", class: "item" },
                    label({ for: "time" }, "Time : "),
                    input({ type: "time", name: "time", id: "time", required: true})
                )
            ),
            p(),
            label({ for: "capacity", class: "item" }, "Capacity : "),
            input({ type: "number", name: "capacity", id: "capacity", required: true, class: "input smaller", min: 1, max: 100}),
            p(),
            button({ type: "submit", class: "button primary"}, "Create")
        )
    )
    return div2({ class: "div centered-up" },
        h1("Create Session"),
        sessionInfo
    );
}