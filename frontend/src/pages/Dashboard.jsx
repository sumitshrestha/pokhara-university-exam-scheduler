export default function Dashboard() {
  return (
    <div className="page-shell">
      <section className="hero-panel compact-hero">
        <div>
          <p className="eyebrow">Overview</p>
          <h1>Pokhara University exam data now has an actual frontend workflow.</h1>
          <p className="hero-copy">
            Use the registration workspace to capture semester courses, then move to the group,
            centre, and schedule screens to inspect what the scheduler will consume.
          </p>
        </div>
      </section>

      <div className="overview-grid">
        <div className="card side-card">
          <p className="eyebrow">Step 1</p>
          <h2>Register semester data</h2>
          <p className="muted-copy">
            The new registration screen mirrors the old Swing workflow: choose group context,
            select the program and college, then add course rows with candidate totals.
          </p>
        </div>

        <div className="card side-card">
          <p className="eyebrow">Step 2</p>
          <h2>Verify operating data</h2>
          <p className="muted-copy">
            Review the groups and centres pages to confirm the scheduler inputs reflect what is in
            the database after each registration save.
          </p>
        </div>

        <div className="card side-card">
          <p className="eyebrow">Step 3</p>
          <h2>Generate schedule</h2>
          <p className="muted-copy">
            When the registration data is complete, use the schedule view to run the graph-coloring
            scheduler with the gap constraints you want.
          </p>
        </div>
      </div>
    </div>
  )
}
