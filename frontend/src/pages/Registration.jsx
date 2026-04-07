import { Fragment, useEffect, useState } from 'react'
import axios from 'axios'

function createRowId() {
  if (globalThis.crypto?.randomUUID) {
    return globalThis.crypto.randomUUID()
  }

  return `${Date.now()}-${Math.random().toString(16).slice(2)}`
}

function createEmptyCourseRow() {
  return {
    id: createRowId(),
    code: '',
    name: '',
    totalCandidates: '',
  }
}

function normalize(value) {
  return value.trim().toUpperCase()
}

function uniqueSorted(values) {
  return [...new Set(values.filter(Boolean))].sort((left, right) => left.localeCompare(right))
}

function findMatchingGroup(groups, faculty, level, discipline) {
  const normalizedFaculty = normalize(faculty)
  const normalizedLevel = normalize(level)
  const normalizedDiscipline = normalize(discipline)

  return groups.find((group) => (
    group.faculty === normalizedFaculty
      && group.level === normalizedLevel
      && group.discipline === normalizedDiscipline
  ))
}

export default function Registration() {
  const [metadata, setMetadata] = useState(null)
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [submitError, setSubmitError] = useState('')
  const [submitSuccess, setSubmitSuccess] = useState('')
  const [form, setForm] = useState({
    faculty: '',
    level: '',
    discipline: '',
    program: '',
    college: '',
    centre: '',
    semester: '',
    courses: [createEmptyCourseRow()],
  })

  useEffect(() => {
    let isMounted = true

    async function loadMetadata() {
      setLoading(true)
      setLoadError('')

      try {
        const response = await axios.get('/api/data/registration-metadata')
        if (!isMounted) {
          return
        }

        const nextMetadata = response.data
        setMetadata(nextMetadata)
        setForm((current) => {
          if (current.faculty || current.level || current.discipline || current.program || current.centre) {
            return current
          }

          const firstGroup = nextMetadata.groups?.[0]
          const firstProgram = firstGroup?.programs?.[0]
          const firstCentre = nextMetadata.centres?.[0]

          return {
            ...current,
            faculty: firstGroup?.faculty ?? '',
            level: firstGroup?.level ?? '',
            discipline: firstGroup?.discipline ?? '',
            program: firstProgram?.name ?? '',
            college: firstProgram?.colleges?.[0] ?? '',
            centre: firstCentre?.name ?? '',
            semester: 'I',
          }
        })
      } catch (error) {
        if (!isMounted) {
          return
        }

        setLoadError(error?.response?.data?.error ?? 'Could not load registration metadata. Is the backend running?')
      } finally {
        if (isMounted) {
          setLoading(false)
        }
      }
    }

    loadMetadata()

    return () => {
      isMounted = false
    }
  }, [])

  const groups = metadata?.groups ?? []
  const centres = metadata?.centres ?? []
  const matchingGroup = findMatchingGroup(groups, form.faculty, form.level, form.discipline)
  const facultyOptions = uniqueSorted(groups.map((group) => group.faculty))
  const levelOptions = uniqueSorted(
    groups
      .filter((group) => !form.faculty || group.faculty === normalize(form.faculty))
      .map((group) => group.level)
  )
  const disciplineOptions = uniqueSorted(
    groups
      .filter((group) => (!form.faculty || group.faculty === normalize(form.faculty))
        && (!form.level || group.level === normalize(form.level)))
      .map((group) => group.discipline)
  )
  const programOptions = matchingGroup?.programs ?? []
  const selectedProgram = programOptions.find((program) => program.name === normalize(form.program))
  const collegeOptions = selectedProgram?.colleges ?? []
  const courseSuggestions = matchingGroup?.courses ?? []
  const centreInfo = centres.find((centre) => centre.name === normalize(form.centre))
  const totalCandidates = form.courses.reduce((sum, course) => sum + (Number(course.totalCandidates) || 0), 0)
  const knownCourseCount = form.courses.filter((course) => (
    courseSuggestions.some((suggestion) => suggestion.code === normalize(course.code))
  )).length

  const setField = (field, value) => {
    setSubmitError('')
    setSubmitSuccess('')
    setForm((current) => ({
      ...current,
      [field]: value,
    }))
  }

  const handleProgramChange = (value) => {
    setSubmitError('')
    setSubmitSuccess('')

    setForm((current) => {
      const nextProgram = value
      const nextGroup = findMatchingGroup(groups, current.faculty, current.level, current.discipline)
      const existingProgram = nextGroup?.programs?.find((program) => program.name === normalize(nextProgram))

      return {
        ...current,
        program: nextProgram,
        college: existingProgram?.colleges?.[0] ?? current.college,
      }
    })
  }

  const updateCourseRow = (rowId, field, value) => {
    setSubmitError('')
    setSubmitSuccess('')

    setForm((current) => ({
      ...current,
      courses: current.courses.map((course) => {
        if (course.id !== rowId) {
          return course
        }

        const nextCourse = {
          ...course,
          [field]: value,
        }

        if (field === 'code') {
          const matchingCourse = courseSuggestions.find((suggestion) => suggestion.code === normalize(value))
          if (matchingCourse) {
            nextCourse.name = matchingCourse.name
          }
        }

        return nextCourse
      }),
    }))
  }

  const addCourseRow = () => {
    setForm((current) => ({
      ...current,
      courses: [...current.courses, createEmptyCourseRow()],
    }))
  }

  const removeCourseRow = (rowId) => {
    setSubmitError('')
    setSubmitSuccess('')

    setForm((current) => {
      if (current.courses.length === 1) {
        return current
      }

      return {
        ...current,
        courses: current.courses.filter((course) => course.id !== rowId),
      }
    })
  }

  const loadKnownCourses = () => {
    if (!courseSuggestions.length) {
      return
    }

    setSubmitError('')
    setSubmitSuccess('')
    setForm((current) => ({
      ...current,
      courses: courseSuggestions.map((course) => ({
        id: createRowId(),
        code: course.code,
        name: course.name,
        totalCandidates: '',
      })),
    }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setSubmitError('')
    setSubmitSuccess('')

    const payload = {
      faculty: normalize(form.faculty),
      level: normalize(form.level),
      discipline: normalize(form.discipline),
      program: normalize(form.program),
      college: normalize(form.college),
      centre: normalize(form.centre),
      semester: normalize(form.semester),
      courses: form.courses.map((course) => ({
        code: normalize(course.code),
        name: course.name.trim(),
        totalCandidates: Number(course.totalCandidates),
      })),
    }

    const invalidCourse = payload.courses.find((course) => (
      !course.code || !course.name || Number.isNaN(course.totalCandidates) || course.totalCandidates < 0
    ))

    if (!payload.faculty || !payload.level || !payload.discipline || !payload.program
      || !payload.college || !payload.centre || !payload.semester) {
      setSubmitError('Complete the group, program, college, centre, and semester fields before saving.')
      return
    }

    if (!payload.courses.length) {
      setSubmitError('Add at least one course row before saving.')
      return
    }

    if (invalidCourse) {
      setSubmitError('Every course row needs a valid code, name, and non-negative total candidates value.')
      return
    }

    setSubmitting(true)
    try {
      const response = await axios.post('/api/data/registrations', payload)
      setSubmitSuccess(response.data.message ?? 'Registration saved successfully.')

      const metadataResponse = await axios.get('/api/data/registration-metadata')
      setMetadata(metadataResponse.data)
    } catch (error) {
      setSubmitError(error?.response?.data?.error ?? 'Could not save this semester registration.')
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return <p>Loading registration workspace...</p>
  }

  if (loadError) {
    return <p className="error">{loadError}</p>
  }

  return (
    <div className="page-shell registration-shell">
      <section className="hero-panel">
        <div>
          <p className="eyebrow">Course Registration Workspace</p>
          <h1>Enter semester registrations the way the Swing app used to.</h1>
          <p className="hero-copy">
            Define the exam group, pick the program and college, then register the semester&apos;s
            courses with candidate counts in one save operation.
          </p>
        </div>
        <div className="hero-metrics">
          <div className="metric-card accent-sand">
            <span className="metric-label">Exam Groups</span>
            <strong>{groups.length}</strong>
          </div>
          <div className="metric-card accent-teal">
            <span className="metric-label">Centres</span>
            <strong>{centres.length}</strong>
          </div>
          <div className="metric-card accent-ink">
            <span className="metric-label">Rows in Draft</span>
            <strong>{form.courses.length}</strong>
          </div>
        </div>
      </section>

      <div className="registration-layout">
        <form className="card registration-form" onSubmit={handleSubmit}>
          <div className="section-heading">
            <div>
              <p className="eyebrow">Context</p>
              <h2>Registration Header</h2>
            </div>
            <button type="button" className="button-ghost" onClick={loadKnownCourses} disabled={!courseSuggestions.length}>
              Load Group Course List
            </button>
          </div>

          <div className="form-grid form-grid-four">
            <label>
              Faculty
              <input
                list="faculty-options"
                value={form.faculty}
                onChange={(event) => setField('faculty', event.target.value)}
                placeholder="SCIENCE AND TECHNOLOGY"
              />
            </label>
            <label>
              Level
              <input
                list="level-options"
                value={form.level}
                onChange={(event) => setField('level', event.target.value)}
                placeholder="BACHELOR"
              />
            </label>
            <label>
              Discipline
              <input
                list="discipline-options"
                value={form.discipline}
                onChange={(event) => setField('discipline', event.target.value)}
                placeholder="ENGINEERING"
              />
            </label>
            <label>
              Semester
              <input
                value={form.semester}
                onChange={(event) => setField('semester', event.target.value)}
                placeholder="I"
              />
            </label>
            <label>
              Program
              <input
                list="program-options"
                value={form.program}
                onChange={(event) => handleProgramChange(event.target.value)}
                placeholder="BE COMPUTER"
              />
            </label>
            <label>
              College
              <input
                list="college-options"
                value={form.college}
                onChange={(event) => setField('college', event.target.value)}
                placeholder="NCIT"
              />
            </label>
            <label>
              Exam Centre
              <input
                list="centre-options"
                value={form.centre}
                onChange={(event) => setField('centre', event.target.value)}
                placeholder="POKHARA"
              />
            </label>
            <div className="info-pill">
              <span className="metric-label">Centre Capacity</span>
              <strong>{centreInfo ? centreInfo.maxLimit : 'Select a centre'}</strong>
            </div>
          </div>

          <datalist id="faculty-options">
            {facultyOptions.map((faculty) => <option key={faculty} value={faculty} />)}
          </datalist>
          <datalist id="level-options">
            {levelOptions.map((level) => <option key={level} value={level} />)}
          </datalist>
          <datalist id="discipline-options">
            {disciplineOptions.map((discipline) => <option key={discipline} value={discipline} />)}
          </datalist>
          <datalist id="program-options">
            {programOptions.map((program) => <option key={program.name} value={program.name} />)}
          </datalist>
          <datalist id="college-options">
            {collegeOptions.map((college) => <option key={college} value={college} />)}
          </datalist>
          <datalist id="centre-options">
            {centres.map((centre) => <option key={centre.name} value={centre.name} />)}
          </datalist>

          <div className="section-heading section-heading-spaced">
            <div>
              <p className="eyebrow">Semester Payload</p>
              <h2>Course Rows</h2>
            </div>
            <button type="button" onClick={addCourseRow}>Add Row</button>
          </div>

          <div className="course-grid">
            <div className="course-grid-header">Course Code</div>
            <div className="course-grid-header">Course Name</div>
            <div className="course-grid-header">Total Candidates</div>
            <div className="course-grid-header action-column">Action</div>

            {form.courses.map((course) => (
              <Fragment key={course.id}>
                <div>
                  <input
                    list="course-code-options"
                    value={course.code}
                    onChange={(event) => updateCourseRow(course.id, 'code', event.target.value)}
                    placeholder="CMP-224.3"
                  />
                </div>
                <div>
                  <input
                    value={course.name}
                    onChange={(event) => updateCourseRow(course.id, 'name', event.target.value)}
                    placeholder="OPERATING SYSTEMS"
                  />
                </div>
                <div>
                  <input
                    type="number"
                    min="0"
                    value={course.totalCandidates}
                    onChange={(event) => updateCourseRow(course.id, 'totalCandidates', event.target.value)}
                    placeholder="48"
                  />
                </div>
                <div className="action-column">
                  <button
                    type="button"
                    className="button-ghost button-danger"
                    onClick={() => removeCourseRow(course.id)}
                    disabled={form.courses.length === 1}
                  >
                    Remove
                  </button>
                </div>
              </Fragment>
            ))}
          </div>

          <datalist id="course-code-options">
            {courseSuggestions.map((course) => <option key={course.code} value={course.code} />)}
          </datalist>

          {submitError && <p className="error">{submitError}</p>}
          {submitSuccess && <p className="success">{submitSuccess}</p>}

          <div className="form-actions">
            <button type="submit" disabled={submitting}>
              {submitting ? 'Saving Registration...' : 'Save Semester Registration'}
            </button>
          </div>
        </form>

        <aside className="registration-sidebar">
          <div className="card side-card">
            <p className="eyebrow">Draft Summary</p>
            <h2>What will be written</h2>
            <div className="summary-list">
              <div>
                <span>Known course matches</span>
                <strong>{knownCourseCount}</strong>
              </div>
              <div>
                <span>Total candidate sum</span>
                <strong>{totalCandidates}</strong>
              </div>
              <div>
                <span>Existing colleges on program</span>
                <strong>{collegeOptions.length}</strong>
              </div>
            </div>
          </div>

          <div className="card side-card">
            <p className="eyebrow">Selected Group</p>
            <h2>{matchingGroup ? `${matchingGroup.faculty} / ${matchingGroup.level}` : 'New group entry'}</h2>
            <p className="muted-copy">
              {matchingGroup
                ? `${matchingGroup.discipline} currently has ${matchingGroup.programs.length} programs and ${matchingGroup.courses.length} known courses.`
                : 'Typed values are not in the current metadata yet. Saving will attempt to create the missing group or program records.'}
            </p>
          </div>

          <div className="card side-card">
            <p className="eyebrow">Reference</p>
            <h2>Legacy workflow, web form</h2>
            <ul className="bullet-list">
              <li>Editable suggestion lists mirror the old Swing combo boxes for faculty, level, discipline, and program.</li>
              <li>The course table matches the regular course form: code, name, and candidate count per row.</li>
              <li>Saving this screen upserts one college/program/semester registration in a single transaction.</li>
            </ul>
          </div>
        </aside>
      </div>
    </div>
  )
}