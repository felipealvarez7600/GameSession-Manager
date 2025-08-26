import {handlerError} from "../errorHandler.js";

class PlayerServices{

    /**
     * Function to get players from the server by partial username
     */
    async getPlayersByPartialUsername(params, url){
        try{
            const {limit, skip, username} = params
            const res = await fetch(url + "players?limit=" + limit + "&skip=" + skip + "&username=" + username)
            if (!res.ok) {
                throw await res.json() // Throws error from server
            }
            const players = await res.json()
            return players.data
        } catch (error) {
            handlerError(error); // Handle errors
        }
    }

    /**
     * Function to get a player by ID from the server
     */
    async getPlayerProfile(playerId, url){
        try{
            const token = window.sessionStorage.getItem("token")
            const res = await fetch(url + "players/" + playerId,{
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
     * Function to get the name of a player by ID from the server
     */
    async getPlayerName(playerId, API_BASE_URL) {
        const token = window.sessionStorage.getItem("token")
        return fetch(API_BASE_URL + "players/" + playerId, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        })
            .then(res => {
                if (!res.ok) {
                    throw res.json()
                }
                return res.json()
            })
            .then(player => player.name)
            .catch(error => {
                handlerError(error)
            })
    }

    /**
     * Function to update a player in the server
     */
    async updatePlayer(params, playerId, API_BASE_URL){
        const token = window.sessionStorage.getItem("token")
        try{
            const res = await fetch(API_BASE_URL + "players/" + playerId, {
                method: "PUT",
                headers: {
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify(params)
            })
            if (!res.ok) {
                throw await res.json() // Throws error from server
            }
        } catch (error) {
            handlerError(error); // Handle errors
        }
    }

    /**
     * Function to get the player invites from the server
     */
    async getPlayerInvites(playerId, url){
        const token = window.sessionStorage.getItem("token")
        try{
            const res = await fetch(url + "players/" + playerId + "/invites", {
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
     * Function for a player to join a session in the server
     */
    async joinSession(playerId, sessionId, url){
        const token = window.sessionStorage.getItem("token")
        try{
            const res = await fetch(url + "sessions/" + sessionId + "/players/" + playerId, {
                method: "PUT",
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

}

export default new PlayerServices()