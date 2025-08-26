import PlayerServices from "../Services/PlayerServices.js";
import {button, div, div2, form, h1, input, label, p} from "../DSL.js";


export async function playerUpdateView(API_BASE_URL){
    const playerId = window.location.hash.split("/")[1];
    const updatePlayerForm = form(
        {},
        async (event) => {
            event.preventDefault();
            const params = {};
            const formData = new FormData(event.target);

            const details = formData.get("details");
            const image = formData.get("image");

            // Read the image file as a base64-encoded string
            const reader = new FileReader();
            reader.onload = async function(event) {
                // Get the base64-encoded string representing the image
                const imageData = event.target.result;

                // Update params with details and the image data as text
                params.details = details;
                params.image = imageData;

                // Call the service to update the player
                await PlayerServices.updatePlayer(params, playerId, API_BASE_URL);

                // Redirect to updated player details
                window.location.hash = "player/" + playerId;
            };
            reader.readAsDataURL(image);
        },
        div(
            label({ for: "details", class: "item" }, "New player description : "),
            input({ type: "text", name: "details", id: "details"}),
            p(),
            label({ for: "image", class: "item" }, "Insert your profile image : "),
            input({ type: "file", name: "image", id: "image", class: "custom-file-upload"}),
            p(),
            button({ type: "submit", class: "button primary"}, "Update")
        )
    )
    return div2({ class: "div centered-up" },
        h1("Update Player"),
        updatePlayerForm
    );

}