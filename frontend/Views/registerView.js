import {button, div, div2, form, h1, input, label, p} from "../DSL.js";


export function registerView(handle){
    const homeButton = button({ type: "button", id: "home", class: "button primary" }, "Home");
    homeButton.addEventListener("click", () => {
        window.location.href = "#home";
    })
    return div2({ class: "div centered" },
        h1("Sign Up"),
        form(
            {},
            (event) => {
                event.preventDefault();
                const formData = new FormData(event.target);
                const player = {
                    name: formData.get("regName"),
                    email: formData.get("regEmail"),
                    password: formData.get("regPassword")
                }
                handle(player);
            },
            div(
                label({for: "regName", class: "label"}, "Name:"),
                input({type: "text", name: "regName", id: "regName", required: true, class: "input"}),
                p(),
                label({for: "regEmail", class: "label"}, "Email:"),
                input({type: "email", name: "regEmail", id: "regEmail", required: true, class: "input"}),
                p(),
                label({for: "regPassword", class: "label"}, "Password:"),
                input({type: "password", name: "regPassword", id: "regPassword", required: true, class: "input"}),
                p(),
                button({type: "submit", class: "button primary"}, "Sign Up"),
                p(),
                homeButton
            )
        )
    )
}