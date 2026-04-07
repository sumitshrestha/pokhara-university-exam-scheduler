import { useEffect, useState } from 'react'
import axios from 'axios'

export default function Centres() {
  const [centres, setCentres] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    axios.get('/api/centres')
      .then(r => setCentres(r.data))
      .catch(() => setError('Could not load centres. Is the backend running?'))
  }, [])

  if (error) return <p className="error">{error}</p>
  if (!centres) return <p>Loading...</p>

  const rows = centres.centreList ?? centres.centreTable ?? []

  return (
    <div>
      <h1>Exam Centres</h1>
      <div className="card">
        {Array.isArray(rows) && rows.length > 0 ? (
          <table>
            <thead>
              <tr>
                <th>Centre</th>
                <th>Max Limit</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((c, idx) => (
                <tr key={idx}>
                  <td>{c.centreName ?? c.Centre ?? 'N/A'}</td>
                  <td>{c.maxLimit ?? c.MaxLimit ?? 'N/A'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <pre style={{ overflow: 'auto', maxHeight: '60vh', fontSize: '.8rem' }}>
            {JSON.stringify(centres, null, 2)}
          </pre>
        )}
      </div>
    </div>
  )
}
