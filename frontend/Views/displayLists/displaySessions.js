import {a, div2, li, p, ul} from "../../DSL.js";


/**
 * Display the list of sessions
 */
export function displaySessions(mainContent, sessions) {
    // Remove previous session list if it exists
    const previousSessionList = mainContent.querySelector('#sessionList');
    if (previousSessionList) {
        previousSessionList.remove();
    }
    const sessionList = sessions.length === 0 ? p("No sessions found.") : ul(
        div2( { class: "container space-between"},
            ...sessions.map(session =>
                div2( { class: "item"},
                    li("Game : " + session.gameName,
                    li("Date : " + session.date.slice(0, 19).replace("T", " ")),
                    li("State : " + session.state),
                    li("Capacity : " + session.capacity),
                    li(
                        a({
                            href: "#sessions/" + session.id,
                            class: "link-primary"
                        }, "Get session Details")
                        )
                    )
                )
            )
        )
    );

    sessionList.id = 'sessionList';
    return sessionList;
}