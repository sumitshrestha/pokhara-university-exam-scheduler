import { useEffect, useState } from 'react'
import axios from 'axios'

export default function Groups() {
  const [groups, setGroups] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    axios.get('/api/groups')
      .then(r => setGroups(r.data))
      .catch(() => setError('Could not load groups. Is the backend running?'))
  }, [])

  if (error) return <p className="error">{error}</p>
  if (!groups) return <p>Loading…</p>

  const entries = Object.entries(groups.groupsPrograms ?? {})

  return (
    <div>
      <h1>Groups &amp; Programs</h1>
      {entries.length === 0 && <p>No groups found in the database.</p>}
      {entries.map(([group, programList]) => (
        <div className="card" key={group}>
          <strong>{group}</strong>
          {programList?.programs?.length > 0 && (
            <table style={{ marginTop: '0.6rem' }}>
              <thead><tr><th>Program</th></tr></thead>
              <tbody>
                {programList.programs.map(p => (
                  <tr key={p.programName}><td>{p.programName}</td></tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      ))}
    </div>
  )
}
