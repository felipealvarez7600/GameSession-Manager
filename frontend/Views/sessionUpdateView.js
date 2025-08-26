import SessionServices from "../Services/SessionServices.js";
import {button, div, div2, form, input, label, p} from "../DSL.js";

export async function sessionUpdateView(url){
    const sessionId = window.location.hash.split("/")[1]
    const session = await SessionServices.getSessionDetails(sessionId, url)
    const params = {
        capacity: session.capacity,
        date: session.date.split("T")[0].replace("T", ""),
        time: session.date.split("T")[1].replace("T", "").replace("Z", "")
    };
    return div(
        div2({class: "div centered-up"},
            form(
                {},
                async (event) => {
                    event.preventDefault();
                    const formData = new FormData(event.target);
                    const date = formData.get("date");
                    const time = formData.get("time");
                    const request = {}
                    request.capacity = formData.get("capacity")
                    const dateTime = new Date(date + 'T' + time + 'Z');
                    if (dateTime instanceof Date && !isNaN(dateTime)) {
                        request.date = dateTime.toISOString().substring(0, 22) + "Z";
                    }

                    const data = await SessionServices.updateSession(request, sessionId, url)
                    // Redirect to created session details
                    window.location.hash = "sessions/" + sessionId;
                },
                div(
                    div2(
                        { id: "date-time", class: "container space-between" },
                        div2(
                            { id: "date", class: "item"},
                            label({ for: "date" }, "Date : "),
                            input({ type: "date", name: "date", id: "date", value: params.date, required: true})
                        ),
                        div2(
                            { id: "time", class: "item" },
                            label({ for: "time" }, "Time : "),
                            input({ type: "time", name: "time", id: "time", value: params.time, required: true})
                        )
                    ),
                    p(),
                    label({ for: "capacity", class: "item", value: params.capacity}, "Capacity : "),
                    input({ type: "number", name: "capacity", id: "capacity", required: true, value: params.capacity, class: "input smaller", min: 1, max: 100}),
                    p(),
                    button({ type: "submit", class: "button primary"}, "Update")
                )
            )
        ),
    )
}