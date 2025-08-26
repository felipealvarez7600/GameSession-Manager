/**
 * Function to handle errors
 */
export function handlerError(error) {
    console.error('Error:', error)
    const normalErrorCodes = [400, 403, 404, 409]
    const statusCode = parseInt(error.status)
    if(statusCode === 401){
        window.location.hash = "#home"
        alert(error.detail)
    } else if (normalErrorCodes.includes(statusCode)) {
        alert(error.detail)
    } else {
        alert('An unexpected error occurred, please try again later')
    }
}