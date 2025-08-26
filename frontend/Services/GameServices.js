import {handlerError} from "../errorHandler.js";

function getFullUrlUpToHostPort() {
    return window.location.protocol + "//" + window.location.host + "/";
}

const API_BASE_URL = getFullUrlUpToHostPort();

class GameServices {

    /**
     * Function to get games from the server
     */
    async getGames(gameQuery, limit, skip, gameName) {
        const token = window.sessionStorage.getItem('token');
        try {
            const queryParams = new URLSearchParams({
                limit: limit,
                skip: skip,
                ...gameQuery, // Assuming gameQuery is an object with additional query parameters
                gameName: gameName
            });

            const url = API_BASE_URL + `games?${queryParams.toString()}`;

            const response = await fetch(url, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            if(!response.ok){
                throw await response.json();
            }
            return await response.json();
        }catch (error) {
            handlerError(error)
        }
    }

    async getPartialGamesByName(gameName, limit, skip){
        const token = window.sessionStorage.getItem('token');
        try {
            const queryParams = new URLSearchParams({
                gameName: gameName,
                limit: limit,
                skip: skip
            });

            const url = API_BASE_URL + `games/partial?${queryParams.toString()}`;

            const response = await fetch(url, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            if(!response.ok){
                throw await response.json();
            }
            return await response.json();
        }catch (error) {
            handlerError(error)
        }
    }

    /**
     * Function to create a game in the server
     */
    async createGame(game){
        const token = window.sessionStorage.getItem('token');
        try {
            const response = await fetch(API_BASE_URL + 'games', {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(game)
            });
            if(!response.ok){
                throw await response.json();
            }
            return await response.json();
        }
        catch (error) {
            handlerError(error)
        }
    }

    /**
     * Function to get genres from the server
     */
    async getGenres(){
         const token = window.sessionStorage.getItem('token');

        try {
            const response = await fetch(API_BASE_URL + 'genres', {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            if(!response.ok){
                throw await response.json();
            }
            return await response.json();
        }
        catch (error) {
            handlerError(error)
        }
    }

    /**
     * Function to get developers from the server
     */
    async getDevelopers(){
        const token = window.sessionStorage.getItem('token');
        try {
            const response = await fetch(API_BASE_URL + 'developers', {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return await response.json();
        }
        catch (error) {
            handlerError(error)
        }
    }

    /**
     * Function to get the details of a game from the server
     */
    async getGameDetails(){
        const gameId = window.location.hash.split('/')[1];
        const token = window.sessionStorage.getItem('token');
        try {
            const response = await fetch(API_BASE_URL + 'games/' + gameId, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return await response.json();
        }
        catch (error) {
            handlerError(error)
        }
    }


}

export default new GameServices();
