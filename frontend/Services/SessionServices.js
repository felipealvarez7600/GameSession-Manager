import {handlerError} from "../errorHandler.js";
import {buildQueryString} from "../utils/queryAndParamUtils.js";


class SessionServices {

    /**
     * Function to get sessions from the server
     */
    async fetchSessions(params, API_BASE_URL) {
        try{
            const token = window.sessionStorage.getItem("token")
            const queryString = buildQueryString(params)
            const response = await fetch(API_BASE_URL + "sessions" + queryString, {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                }
            })
            if (!response.ok) {
                throw await response.json() // Throws error from server
            }
            const sessions = await response.json()
            return sessions.data
        } catch (error) {
            handlerError(error); // Handle errors
        }
    }

    /**
     * Function to create a session in the server
     */
    async createSession(params, API_BASE_URL) {
        try {
            const token = window.sessionStorage.getItem("token")
            const response = await fetch(API_BASE_URL + "sessions", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify(params)
            });

            if (!response.ok) {
                throw await response.json(); // Throws error from server
            }

            return await response.json();
        } catch (error) {
            handlerError(error); // Handle errors
        }
    }

    /**
     * Function to get the session details from the server
     */
    async getSessionDetails(sessionId, API_BASE_URL) {
        try {
            const token = window.sessionStorage.getItem("token")
            const response = await fetch(API_BASE_URL + "sessions/" + sessionId, {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                }
            });
            if (!response.ok) {
                throw await response.json(); // Throws error from server
            }
            return await response.json();
        } catch (error) {
            handlerError(error); // Handle errors
        }

    }

    /**
     * Function to send an invitation to a session to another player in the server
     */
    async sendInvite(fromPlayerId, toPlayerId, sessionId, API_BASE_URL) {
        try {
            const token = window.sessionStorage.getItem("token")
            const response = await fetch(API_BASE_URL + "sessions/" + sessionId + "/players/" + fromPlayerId + "/invite/" + toPlayerId, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
            });
            if (!response.ok) {
                throw await response.json(); // Throws error from server
            }
        } catch (error) {
            handlerError(error); // Handle errors
        }
    }

    /**
     * Function leave a session in the server
     */
    async leaveSession(playerId, sessionId, API_BASE_URL) {
        try {
            const token = window.sessionStorage.getItem("token")
            const response = await fetch(API_BASE_URL + "sessions/" + sessionId + "/players/" + playerId, {
                method: "DELETE",
                headers: {
                    "Authorization": "Bearer " + token
                },
            });
            if (!response.ok) {
                throw await response.json(); // Throws error from server
            }

            return response;
        } catch (error) {
            handlerError(error); // Handle errors
        }
    }

    /**
     * Function to update a session in the server
     */
    async updateSession(params, sessionId, API_BASE_URL) {
        try {
            const token = window.sessionStorage.getItem("token")
            const queryString = buildQueryString(params)
            const response = await fetch(API_BASE_URL + "sessions/" + sessionId + queryString, {
                method: "PUT",
                headers: {
                    "Authorization": "Bearer " + token
                },
            });
            if (!response.ok) {
                throw await response.json(); // Throws error from server
            }
            return response;
        } catch (error) {
            handlerError(error); // Handle errors
        }
    }
}

export default new SessionServices();