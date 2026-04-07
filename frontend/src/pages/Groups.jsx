import { useEffect, useState } from 'react'
import axios from 'axios'

export default function Groups() {
  const [groups, setGroups] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    const loadGroups = async () => {
      try {
        const metadataResponse = await axios.get('/api/data/registration-metadata')
        const metadataGroups = metadataResponse.data?.groups
        if (Array.isArray(metadataGroups) && metadataGroups.length > 0) {
          setGroups(metadataGroups)
          return
        }

        const groupsResponse = await axios.get('/api/groups')
        const fallbackGroups = Array.isArray(groupsResponse.data?.groups)
          ? groupsResponse.data.groups
          : Array.isArray(groupsResponse.data)
            ? groupsResponse.data
            : []
        setGroups(fallbackGroups)
      } catch (e) {
        const backendError = e?.response?.data?.error
        setError(backendError ?? 'Could not load groups. Is the backend running?')
      }
    }

    loadGroups()
  }, [])

  if (error) return <p className="error">{error}</p>
  if (!groups) return <p>Loading…</p>

  return (
    <div>
      <h1>Groups &amp; Programs</h1>
      {groups.length === 0 && <p>No groups found in the database.</p>}
      {groups.map(group => (
        <div className="card" key={`${group.faculty}-${group.level}-${group.discipline}`}>
          <strong>{`${group.faculty} / ${group.level} / ${group.discipline}`}</strong>
          {group.programs?.length > 0 && (
            <table style={{ marginTop: '0.6rem' }}>
              <thead><tr><th>Program</th><th>Colleges</th></tr></thead>
              <tbody>
                {group.programs.map(program => (
                  <tr key={typeof program === 'string' ? program : program.name}>
                    <td>{typeof program === 'string' ? program : program.name}</td>
                    <td>{typeof program === 'string' ? '-' : (program.colleges ?? []).join(', ')}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      ))}
    </div>
  )
}
