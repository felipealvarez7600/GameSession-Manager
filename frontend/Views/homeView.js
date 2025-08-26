import {button, div2, h1, p} from "../DSL.js";


export function homeView(){
    const signupButton = button({ type: "button", id: "signup", class: "button primary" }, "Sign Up");
    const loginButton = button({ type: "button", id: "login", class: "button primary" }, "Log In");

    signupButton.addEventListener("click", () => {
        window.location.href = "#signup";
    })
    loginButton.addEventListener("click", () => {
        window.location.href = "#login";
    })

    return div2( {class: "div centered" },
        h1("Welcome to the Game Session Manager"),
        p(),
        signupButton,
        loginButton
    )
}