import { useEffect, useState } from 'react'
import { api } from './api/client.js'

function App() {
  const [health, setHealth] = useState('checking...')

  useEffect(() => {
    api
        .get('/health')
        .then((data) => setHealth(data.status))
        .catch(() => setHealth('backend unreachable'))
  }, [])

  return (
      <main>
        <h1>SkillSwap</h1>
        <p>Backend: {health}</p>
      </main>
  )
}

export default App