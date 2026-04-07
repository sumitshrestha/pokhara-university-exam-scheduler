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
      setError(e?.response?.data?.error ?? 'Failed to generate schedule')
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
            <p className="success">Schedule generated successfully.</p>
            <pre style={{ overflow: 'auto', maxHeight: '60vh', fontSize: '.8rem' }}>
              {JSON.stringify(result, null, 2)}
            </pre>
          </div>
        )}
      </div>
    </div>
  )
}
