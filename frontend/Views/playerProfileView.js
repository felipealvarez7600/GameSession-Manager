import PlayerServices from "../Services/PlayerServices.js";
import {button, div2, h1, image, li, p, ul} from "../DSL.js";
import {buildQueryString} from "../utils/queryAndParamUtils.js";
import AuthServices from "../Services/authServices.js";


export async function playerProfileView(API_BASE_URL){
    const currentPlayer = await AuthServices.getPlayerByToken();
    const playerId = window.location.hash.split("/")[1];
    const player = await PlayerServices.getPlayerProfile(playerId, API_BASE_URL);
    const playerDescription = player.details ? player.details : "This user has not inserted a description yet.";
    const profileImageSrc = player.image ? player.image : './images/default_profile_picture.png';
    const profileImage = image({ src: profileImageSrc, alt: "Profile Image", class: "profile-image" });

    const sessionsButton = button(
        { type: "button", id: "sessionsButton", class: "button primary" },
        "Player Sessions"
    );

    const updateButton = button(
        { type: "button", id: "updateButton", class: "button primary" },
        "Update Profile"
    )

    sessionsButton.addEventListener("click", () => {
        window.location.href = "#sessions" + buildQueryString({ pid: playerId });
    });

    updateButton.addEventListener("click", () => {
        window.location.href = "#player/" + playerId + "/update";
    });

    return div2(
        { class: "div centered" },
        h1("Player Details"),
        ul(
            li("Name : " + player.name),
            li("Email : " + player.email),
            li("Description : " + playerDescription),
            profileImage
        ),
        p(),
        sessionsButton,
        p(),
        currentPlayer.id === player.id.toString() ? updateButton : p()
    );
}