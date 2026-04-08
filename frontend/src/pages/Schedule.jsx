import { useState } from 'react'
import axios from 'axios'

export default function Schedule() {
  const [minGap, setMinGap] = useState(1)
  const [maxGap, setMaxGap] = useState(3)
  const [loading, setLoading] = useState(false)
  const [autoHunting, setAutoHunting] = useState(false)
  const [error, setError] = useState('')
  const [result, setResult] = useState(null)
  const [diagnoseResult, setDiagnoseResult] = useState(null)
  const [diagnosing, setDiagnosing] = useState(null) // stores "faculty/level/discipline" being diagnosed

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

  const handleAutoHunt = async () => {
    setAutoHunting(true)
    setError('')
    setResult(null)
    setDiagnoseResult(null)
    try {
      const res = await axios.post('/api/schedule/auto-hunt')
      setResult(res.data)
      if (res.data.bestMinGap != null) {
        setMinGap(res.data.bestMinGap)
        setMaxGap(res.data.bestMaxGap)
      }
    } catch (e) {
      const payload = e?.response?.data
      if (payload && typeof payload === 'object') {
        setResult(payload)
      }
      setError(payload?.error ?? 'Auto-hunt failed')
    } finally {
      setAutoHunting(false)
    }
  }

  const handleDiagnose = async (faculty, level, discipline) => {
    const key = `${faculty}/${level}/${discipline}`
    setDiagnosing(key)
    setDiagnoseResult(null)
    try {
      const res = await axios.get('/api/schedule/diagnose', {
        params: { faculty, level, discipline }
      })
      setDiagnoseResult(res.data)
    } catch (e) {
      setDiagnoseResult({ error: e?.response?.data?.error ?? 'Diagnose failed' })
    } finally {
      setDiagnosing(null)
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
        <button onClick={handleGenerate} disabled={loading || autoHunting} style={{ marginLeft: '1rem' }}>
          {loading ? 'Generating...' : 'Generate'}
        </button>
        <button
          onClick={handleAutoHunt}
          disabled={loading || autoHunting}
          style={{ marginLeft: '0.5rem' }}
          title="Tries all gap combinations (minGap 0–4, maxGap 0–8) and returns the schedule with the fewest unscheduled groups and tightest calendar."
        >
          {autoHunting ? 'Hunting...' : '🔍 Auto Hunt Best Gaps'}
        </button>

        {error && <p className="error">{error}</p>}
        {result && (
          <div style={{ marginTop: '1rem' }}>
            <p className="success">{result.message ?? 'Schedule generated successfully.'}</p>
            {result.bestMinGap != null && (
              <p>
                <strong>Best gaps found:</strong> minGap = {result.bestMinGap}, maxGap = {result.bestMaxGap}
                {result.trialsSearched != null && <span style={{ marginLeft: '0.5rem', color: '#888' }}>({result.trialsSearched} combinations tried)</span>}
              </p>
            )}
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
                <ul style={{ listStyle: 'none', padding: 0 }}>
                  {result.unscheduledGroups.map((group, idx) => {
                    const key = `${group.faculty}/${group.level}/${group.discipline}`
                    return (
                      <li key={`${key}-${idx}`} style={{ marginBottom: '0.4rem' }}>
                        {group.faculty} / {group.level} / {group.discipline}
                        <button
                          onClick={() => handleDiagnose(group.faculty, group.level, group.discipline)}
                          disabled={diagnosing === key}
                          style={{ marginLeft: '0.75rem', fontSize: '0.8rem' }}
                        >
                          {diagnosing === key ? 'Diagnosing...' : '🔬 Diagnose'}
                        </button>
                      </li>
                    )
                  })}
                </ul>

                {diagnoseResult && (
                  <div className="card" style={{ marginTop: '1rem', background: '#ffffff', color: '#111827', border: '1px solid #dbe2ea', borderLeft: '4px solid #0d6efd' }}>
                    {diagnoseResult.error ? (
                      <p className="error">{diagnoseResult.error}</p>
                    ) : (
                      <>
                        <h4>Diagnosis: {diagnoseResult.group?.faculty} / {diagnoseResult.group?.level} / {diagnoseResult.group?.discipline}</h4>

                        <div style={{ background: '#f8fafc', padding: '0.75rem', borderRadius: '6px', marginBottom: '0.75rem', border: '1px solid #e2e8f0' }}>
                          <strong>Feasibility Note:</strong>
                          <p style={{ margin: '0.3rem 0 0', color: '#b42318' }}>{diagnoseResult.summary?.feasibilityNote}</p>
                        </div>

                        <table style={{ marginBottom: '0.75rem', width: '100%', background: '#ffffff', color: '#111827' }}>
                          <tbody>
                            <tr><td><strong>Regular courses</strong></td><td>{diagnoseResult.summary?.totalRegularCourses}</td></tr>
                            <tr><td><strong>Back-paper courses</strong></td><td>{diagnoseResult.summary?.totalBackCourses}</td></tr>
                            <tr><td><strong>Total courses in conflict graph</strong></td><td>{diagnoseResult.summary?.totalCoursesInGraph}</td></tr>
                            <tr><td><strong>Semester sets (college/program combos)</strong></td><td>{diagnoseResult.summary?.totalRegularCourseSets}</td></tr>
                            <tr><td><strong>Back-paper candidates</strong></td><td>{diagnoseResult.summary?.totalBackCandidates}</td></tr>
                            <tr><td><strong>Total conflict edges</strong></td><td>{diagnoseResult.summary?.totalConflictEdges}</td></tr>
                            <tr><td><strong>Max courses in any single semester set</strong></td><td>{diagnoseResult.summary?.maxCoursesInAnySemesterSet}</td></tr>
                            <tr><td><strong>Semester sets with internal conflicts</strong></td><td style={{ color: diagnoseResult.summary?.semesterSetsWithInternalConflicts > 0 ? '#e94560' : 'inherit' }}>{diagnoseResult.summary?.semesterSetsWithInternalConflicts}</td></tr>
                            <tr><td><strong>Estimated minimum viable maxGap</strong></td><td>{diagnoseResult.summary?.estimatedMinViableMaxGap}</td></tr>
                          </tbody>
                        </table>

                        {Array.isArray(diagnoseResult.regularCourseSets) && diagnoseResult.regularCourseSets.length > 0 && (
                          <>
                            <h5>Semester Sets (sorted by internal conflicts ↓)</h5>
                            <table style={{ width: '100%', background: '#ffffff', color: '#111827' }}>
                              <thead><tr><th>College</th><th>Program</th><th>Sem</th><th>Courses</th><th>Internal Conflicts</th></tr></thead>
                              <tbody>
                                {diagnoseResult.regularCourseSets.map((s, i) => (
                                  <tr key={i} style={{ background: s.internalConflicts > 0 ? 'rgba(233,69,96,0.15)' : 'inherit' }}>
                                    <td>{s.college}</td>
                                    <td>{s.program}</td>
                                    <td>{s.semester}</td>
                                    <td style={{ fontSize: '0.75rem' }}>{(s.courses ?? []).join(', ')}</td>
                                    <td style={{ color: s.internalConflicts > 0 ? '#e94560' : 'inherit', fontWeight: s.internalConflicts > 0 ? 'bold' : 'normal' }}>{s.internalConflicts}</td>
                                  </tr>
                                ))}
                              </tbody>
                            </table>
                          </>
                        )}

                        {Array.isArray(diagnoseResult.topConflictingCandidates) && diagnoseResult.topConflictingCandidates.length > 0 && (
                          <>
                            <h5 style={{ marginTop: '0.75rem' }}>Top Conflicting Back-Paper Candidates (cross-semester edges ↓)</h5>
                            <table style={{ width: '100%', background: '#ffffff', color: '#111827' }}>
                              <thead><tr><th>Candidate ID</th><th>State</th><th>Cur. Sem</th><th>Back Papers</th><th>Regular Papers</th><th>Cross-Sem Edges</th></tr></thead>
                              <tbody>
                                {diagnoseResult.topConflictingCandidates.map((c, i) => (
                                  <tr key={i} style={{ background: c.crossSemesterEdges > 0 ? 'rgba(233,69,96,0.1)' : 'inherit' }}>
                                    <td style={{ fontSize: '0.75rem' }}>{c.candidateId}</td>
                                    <td>{c.state}</td>
                                    <td>{c.currentSemester}</td>
                                    <td style={{ fontSize: '0.75rem' }}>{(c.backPapers ?? []).join(', ')}</td>
                                    <td style={{ fontSize: '0.75rem' }}>{(c.regularPapers ?? []).join(', ')}</td>
                                    <td style={{ color: c.crossSemesterEdges > 0 ? '#e94560' : 'inherit', fontWeight: 'bold' }}>{c.crossSemesterEdges}</td>
                                  </tr>
                                ))}
                              </tbody>
                            </table>
                          </>
                        )}

                        {Array.isArray(diagnoseResult.topConflictedCourses) && diagnoseResult.topConflictedCourses.length > 0 && (
                          <>
                            <h5 style={{ marginTop: '0.75rem' }}>Most Conflicted Courses (degree ↓)</h5>
                            <table style={{ width: '100%', background: '#ffffff', color: '#111827' }}>
                              <thead><tr><th>Course</th><th>Conflicts With (degree)</th></tr></thead>
                              <tbody>
                                {diagnoseResult.topConflictedCourses.slice(0, 15).map((c, i) => (
                                  <tr key={i}>
                                    <td>{c.course}</td>
                                    <td>{c.degree}</td>
                                  </tr>
                                ))}
                              </tbody>
                            </table>
                          </>
                        )}
                      </>
                    )}
                  </div>
                )}

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
