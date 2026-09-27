// Shared helper for talking to the backend.
// Use it everywhere instead of calling fetch() directly, e.g.
//   const skills = await api.get('/skills')
//   await api.post('/friends', { receiverId: 5 })
//
// Paths are relative to /api, and Vite forwards them to the backend.

async function request(path, options = {}) {
    const response = await fetch(`/api${path}`, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...options.headers,
        },
    })

    // Some responses (e.g. 204 No Content) have no body.
    const body = await response.json().catch(() => null)

    if (!response.ok) {
        const error = new Error(body?.message ?? `Request failed with status ${response.status}`)
        error.status = response.status
        error.body = body
        throw error
    }

    return body
}

export const api = {
    get: (path) => request(path),
    post: (path, data) => request(path, { method: 'POST', body: JSON.stringify(data) }),
    put: (path, data) => request(path, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (path) => request(path, { method: 'DELETE' }),
}