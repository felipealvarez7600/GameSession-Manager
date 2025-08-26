import {handlerError} from "../errorHandler.js";

function getFullUrlUpToHostPort() {
    return window.location.protocol + "//" + window.location.host + "/";
}

// API baser URL
const API_BASE_URL = getFullUrlUpToHostPort();

class AuthServices {

    /**
     * Function to get the player by token from the server
     */
    async getPlayerByToken(){
        const token = window.sessionStorage.getItem("token")
        try{
            const res = await fetch(API_BASE_URL + "authenticated", {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                }
            })
            if (!res.ok) {
                throw await res.json() // Throws error from server
            }
            return await res.json()
        } catch (error) {
            handlerError(error); // Handle errors
        }
    }

    /**
     * Function to register a player in the server
     */
    async register(player) {
        try {
            const response = await fetch(API_BASE_URL + "players", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(player)
            });

            if (!response.ok) {
                throw await response.json();
            }

            const data = await response.json();
            // Extract token and player ID from response
            const {token, playerId} = data;

            // Store token and player ID in sessionStorage
            window.sessionStorage.setItem('token', token);
            window.sessionStorage.setItem('id', playerId);

            // Redirect to players page
            window.location.hash = "player";
        } catch (error) {
            handlerError(error); // Handle errors
        }
    }

    /**
     * Function to login a player in the server
     */
    async login(player) {

        try {
            const queryParams = new URLSearchParams({
                name: player.name,
                password: player.password
            });
            const response = await fetch(API_BASE_URL + "authenticate" + `?${queryParams.toString()}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                }
            });


            if (!response.ok) {
                throw await response.json(); // Throws error from server
            }

            const data = await response.json();
            // Extract token and player ID from response
            const {token, playerId} = data;

            // Store token and player ID in sessionStorage
            window.sessionStorage.setItem('token', token);
            window.sessionStorage.setItem('id', playerId);

            // Redirect to players page
            window.location.hash = "player";
        } catch (error) {
            handlerError(error); // Handle errors
        }
    }

    /**
     * Function to logout a player in the server
     */
    async logout() {
        const token = window.sessionStorage.getItem('token');
        try{

            const id = await this.getPlayerByToken()
            const response = await fetch(API_BASE_URL + `players/logout?playerId=${id.id}`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                }

            });
            if (!response.ok) {
                throw await response.json(); // Throws error from server
            }

            // Clear sessionStorage
            window.sessionStorage.clear();

        } catch (error) {
            handlerError(error); // Handle errors
        }
    }
}

export default new AuthServices();
