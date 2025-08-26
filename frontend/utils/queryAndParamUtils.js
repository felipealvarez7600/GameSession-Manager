

/**
 * Build a query string from an object
 */
export function buildQueryString(params) {
    const queryString = Object.keys(params)
        .map(key => {
            if (params[key] !== undefined && params[key] !== null) {
                return `${key}=${params[key]}`
            }
            return ''
        })
        .filter(param => param !== '')
        .join('&')
    return queryString ? `?${queryString}` : ''
}

/**
 * Parse the hash query parameters
 */
export function parseHashParams() {
    const hash = window.location.hash
    const params = {}

    if (hash) {
        const queryString = hash.split('?')[1]
        if (queryString) {
            queryString.split("&").forEach(param => {
                const [key, value] = param.split("=").map(decodeURIComponent)
                params[key] = value
            })
        }
    }

    return params
}