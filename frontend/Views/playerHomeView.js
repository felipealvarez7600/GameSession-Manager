import {button, div2, h1, h2, p} from "../DSL.js";
import authServices from "../Services/authServices.js";


export async function playerHomeView(){
    const logoutButton = button({ type: "button", id: "logout", class: "button primary" }, "Log Out");

    logoutButton.addEventListener("click", () => {
        authServices.logout();
        window.location.href = "#home";
    })

    return div2( {class: "div centered" },
        h1("Welcome to the GameLink™"),
        p(),
        h2("What would you like to do today?"),
        p(),
        logoutButton
    )
}