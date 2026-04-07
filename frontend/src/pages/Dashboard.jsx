export default function Dashboard() {
  return (
    <div>
      <h1>Pokhara University — Exam Routine Generator</h1>
      <div className="card">
        <p>Welcome. Use the navigation above to:</p>
        <ul style={{ marginTop: '0.8rem', paddingLeft: '1.2rem', lineHeight: 2 }}>
          <li><strong>Groups</strong> — View all faculty / level / discipline groups and their programs</li>
          <li><strong>Centres</strong> — View exam centres and their student capacity</li>
          <li><strong>Generate Schedule</strong> — Run the graph-coloring scheduler to produce a conflict-free timetable</li>
        </ul>
      </div>
    </div>
  )
}
