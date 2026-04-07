import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom'
import Dashboard from './pages/Dashboard'
import Groups from './pages/Groups'
import Centres from './pages/Centres'
import Schedule from './pages/Schedule'
import './App.css'

export default function App() {
  return (
    <BrowserRouter>
      <nav className="navbar">
        <span className="brand">PU Exam Routine</span>
        <NavLink to="/">Dashboard</NavLink>
        <NavLink to="/groups">Groups</NavLink>
        <NavLink to="/centres">Centres</NavLink>
        <NavLink to="/schedule">Generate Schedule</NavLink>
      </nav>
      <main className="content">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/groups" element={<Groups />} />
          <Route path="/centres" element={<Centres />} />
          <Route path="/schedule" element={<Schedule />} />
        </Routes>
      </main>
    </BrowserRouter>
  )
}
