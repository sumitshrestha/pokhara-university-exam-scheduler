import { useState } from 'react'
import axios from 'axios'

export default function Schedule() {
  const [minGap, setMinGap] = useState(1)
  const [maxGap, setMaxGap] = useState(3)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [result, setResult] = useState(null)

  const handleGenerate = async () => {
    setLoading(true)
    setError('')
    setResult(null)
    try {
      const res = await axios.post('/api/schedule/generate', { minGap, maxGap })
      setResult(res.data)
    } catch (e) {
      const payload = e?.response?.data
      if (payload && typeof payload === 'object') {
        setResult(payload)
      }

      const status = payload?.scheduleStatus
      const message = payload?.message ?? payload?.error

      if (message && status) {
        setError(`${status}: ${message}`)
      } else {
        setError(message ?? 'Failed to generate schedule')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <h1>Generate Schedule</h1>
      <div className="card">
        <label>
          Min Gap
          <input
            type="number"
            min="0"
            value={minGap}
            onChange={(e) => setMinGap(Number(e.target.value))}
          />
        </label>
        <label style={{ marginLeft: '1rem' }}>
          Max Gap
          <input
            type="number"
            min="0"
            value={maxGap}
            onChange={(e) => setMaxGap(Number(e.target.value))}
          />
        </label>
        <button onClick={handleGenerate} disabled={loading} style={{ marginLeft: '1rem' }}>
          {loading ? 'Generating...' : 'Generate'}
        </button>

        {error && <p className="error">{error}</p>}
        {result && (
          <div style={{ marginTop: '1rem' }}>
            <p className="success">{result.message ?? 'Schedule generated successfully.'}</p>
            <p>
              Scheduled groups: {result.groupRoutineCount ?? 0} | Unscheduled groups: {result.unscheduledGroupCount ?? 0}
            </p>

            {Array.isArray(result.calendarDays) && result.calendarDays.length > 0 && (
              <div style={{ marginTop: '1rem' }}>
                <h3>Exam Calendar</h3>
                <table>
                  <thead>
                    <tr>
                      <th>Day</th>
                      <th>Shift</th>
                      <th>Courses</th>
                    </tr>
                  </thead>
                  <tbody>
                    {result.calendarDays.map((day, idx) => (
                      <tr key={`${day.dateIndex}-${day.shift}-${idx}`}>
                        <td>{day.dateIndex}</td>
                        <td>{day.shift}</td>
                        <td>{(day.courses ?? []).join(', ')}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {Array.isArray(result.groupRoutines) && result.groupRoutines.length > 0 && (
              <div style={{ marginTop: '1rem' }}>
                <h3>Group-wise Schedule</h3>
                {result.groupRoutines.map((group) => (
                  <div className="card" key={`${group.faculty}-${group.level}-${group.discipline}`} style={{ marginBottom: '1rem' }}>
                    <strong>{group.faculty} / {group.level} / {group.discipline}</strong>
                    {(group.programs ?? []).map((program) => (
                      <div key={program.program} style={{ marginTop: '.6rem' }}>
                        <div><b>{program.program}</b></div>
                        <table style={{ marginTop: '.4rem' }}>
                          <thead>
                            <tr>
                              <th>Day</th>
                              <th>Shift</th>
                              <th>Courses</th>
                            </tr>
                          </thead>
                          <tbody>
                            {(program.slots ?? []).map((slot, idx) => (
                              <tr key={`${program.program}-${slot.dateIndex}-${slot.shift}-${idx}`}>
                                <td>{slot.dateIndex}</td>
                                <td>{slot.shift}</td>
                                <td>{(slot.courses ?? []).join(', ')}</td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    ))}
                  </div>
                ))}
              </div>
            )}

            {Array.isArray(result.unscheduledGroups) && result.unscheduledGroups.length > 0 && (
              <div style={{ marginTop: '1rem' }}>
                <h3>Unscheduled Groups</h3>
                <ul>
                  {result.unscheduledGroups.map((group, idx) => (
                    <li key={`${group.faculty}-${group.level}-${group.discipline}-${idx}`}>
                      {group.faculty} / {group.level} / {group.discipline}
                    </li>
                  ))}
                </ul>
              </div>
            )}

            <pre style={{ overflow: 'auto', maxHeight: '60vh', fontSize: '.8rem' }}>
              {JSON.stringify(result, null, 2)}
            </pre>
          </div>
        )}
      </div>
    </div>
  )
}
