-- Realistic heavy dataset compatible with legacy loader expectations
-- Uses only canonical PU group combinations and valid course-code format (e.g. ENG-101.1)
-- Safe to re-run (idempotent)

USE puroutine;
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET collation_connection = 'utf8mb4_unicode_ci';

-- --------------------------------------------------
-- Cleanup malformed / incompatible benchmark rows
-- --------------------------------------------------
DELETE FROM regularcourse
WHERE Course LIKE 'BPRG%'
   OR Course LIKE 'HPRG%'
   OR Course REGEXP '-[0-9][0-9]$';

DELETE FROM collegescentre
WHERE Course LIKE 'BPRG%'
   OR Course LIKE 'HPRG%'
   OR Course REGEXP '-[0-9][0-9]$';

DELETE FROM Program
WHERE Faculty IN ('BENCHMARK', 'BENCH_HEAVY', 'MANAGEMENT', 'SCIENCE AND TECHNOLOGY')
   OR Course REGEXP '-[0-9][0-9]$';

DELETE FROM course
WHERE Faculty IN ('BENCHMARK', 'BENCH_HEAVY', 'MANAGEMENT', 'SCIENCE AND TECHNOLOGY')
   OR CourseCode REGEXP '^[A-Z]{3,5}[0-9]{3}-[0-9]{2}$';

DELETE FROM backpapercandidate
WHERE ID LIKE 'BK-%';

-- Keep only supported canonical group combinations in examdivision
DELETE FROM examdivision
WHERE NOT (
  (Faculty='Science And Technology' AND Level='Bachelor' AND Discipline='Engineering') OR
  (Faculty='Science And Technology' AND Level='Bachelor' AND Discipline='Biomedical') OR
  (Faculty='Management' AND Level='Master' AND Discipline='NotAvailable') OR
  (Faculty='HUMANITIES' AND Level='MASTER' AND Discipline='LAW') OR
  (Faculty='HUMANITIES' AND Level='BACHELOR' AND Discipline='LAW')
);

-- --------------------------------------------------
-- Ensure canonical groups exist
-- --------------------------------------------------
INSERT INTO examdivision (Faculty, Level, Discipline)
SELECT 'Science And Technology', 'Bachelor', 'Engineering'
WHERE NOT EXISTS (
  SELECT 1 FROM examdivision WHERE Faculty='Science And Technology' AND Level='Bachelor' AND Discipline='Engineering'
);

INSERT INTO examdivision (Faculty, Level, Discipline)
SELECT 'Science And Technology', 'Bachelor', 'Biomedical'
WHERE NOT EXISTS (
  SELECT 1 FROM examdivision WHERE Faculty='Science And Technology' AND Level='Bachelor' AND Discipline='Biomedical'
);

INSERT INTO examdivision (Faculty, Level, Discipline)
SELECT 'Management', 'Master', 'NotAvailable'
WHERE NOT EXISTS (
  SELECT 1 FROM examdivision WHERE Faculty='Management' AND Level='Master' AND Discipline='NotAvailable'
);

INSERT INTO examdivision (Faculty, Level, Discipline)
SELECT 'HUMANITIES', 'MASTER', 'LAW'
WHERE NOT EXISTS (
  SELECT 1 FROM examdivision WHERE Faculty='HUMANITIES' AND Level='MASTER' AND Discipline='LAW'
);

INSERT INTO examdivision (Faculty, Level, Discipline)
SELECT 'HUMANITIES', 'BACHELOR', 'LAW'
WHERE NOT EXISTS (
  SELECT 1 FROM examdivision WHERE Faculty='HUMANITIES' AND Level='BACHELOR' AND Discipline='LAW'
);

-- --------------------------------------------------
-- Helpers
-- --------------------------------------------------
CREATE TEMPORARY TABLE tmp_idx (i INT PRIMARY KEY);
SET @i := 0;
INSERT INTO tmp_idx (i)
SELECT @i := @i + 1
FROM information_schema.columns
LIMIT 40;

CREATE TEMPORARY TABLE tmp_slot (s INT PRIMARY KEY);
INSERT INTO tmp_slot (s) VALUES (1),(2),(3),(4),(5),(6),(7),(8);

CREATE TEMPORARY TABLE tmp_college (
  k INT PRIMARY KEY,
  college_name VARCHAR(100)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

INSERT INTO tmp_college (k, college_name) VALUES
  (1, 'POKHARA_ENGINEERING_COLLEGE'),
  (2, 'GANDKI_TECHNICAL_CAMPUS'),
  (3, 'WESTERN_MANAGEMENT_SCHOOL'),
  (4, 'HIMALAYAN_BUSINESS_COLLEGE'),
  (5, 'NATIONAL_LAW_COLLEGE'),
  (6, 'CITY_LAW_CAMPUS'),
  (7, 'BIOMEDICAL_SCIENCE_INSTITUTE'),
  (8, 'UNIVERSITY_HEALTH_CAMPUS');

-- --------------------------------------------------
-- Programs (realistic names with intake sections)
-- --------------------------------------------------
INSERT INTO Program (Faculty, Level, Discipline, Course)
SELECT 'Science And Technology', 'Bachelor', 'Engineering', CONCAT('BE-AI-', LPAD(i,2,'0'))
FROM tmp_idx
WHERE NOT EXISTS (
  SELECT 1 FROM Program p WHERE p.Faculty='Science And Technology' AND p.Level='Bachelor' AND p.Discipline='Engineering' AND p.Course=CONCAT('BE-AI-', LPAD(tmp_idx.i,2,'0'))
);

INSERT INTO Program (Faculty, Level, Discipline, Course)
SELECT 'Science And Technology', 'Bachelor', 'Engineering', CONCAT('BE-DATA-', LPAD(i,2,'0'))
FROM tmp_idx
WHERE NOT EXISTS (
  SELECT 1 FROM Program p WHERE p.Faculty='Science And Technology' AND p.Level='Bachelor' AND p.Discipline='Engineering' AND p.Course=CONCAT('BE-DATA-', LPAD(tmp_idx.i,2,'0'))
);

INSERT INTO Program (Faculty, Level, Discipline, Course)
SELECT 'Science And Technology', 'Bachelor', 'Biomedical', CONCAT('BPHARM-ADV-', LPAD(i,2,'0'))
FROM tmp_idx
WHERE NOT EXISTS (
  SELECT 1 FROM Program p WHERE p.Faculty='Science And Technology' AND p.Level='Bachelor' AND p.Discipline='Biomedical' AND p.Course=CONCAT('BPHARM-ADV-', LPAD(tmp_idx.i,2,'0'))
);

INSERT INTO Program (Faculty, Level, Discipline, Course)
SELECT 'Management', 'Master', 'NotAvailable', CONCAT('MBA-GEN-', LPAD(i,2,'0'))
FROM tmp_idx
WHERE NOT EXISTS (
  SELECT 1 FROM Program p WHERE p.Faculty='Management' AND p.Level='Master' AND p.Discipline='NotAvailable' AND p.Course=CONCAT('MBA-GEN-', LPAD(tmp_idx.i,2,'0'))
);

INSERT INTO Program (Faculty, Level, Discipline, Course)
SELECT 'Management', 'Master', 'NotAvailable', CONCAT('MBA-FIN-', LPAD(i,2,'0'))
FROM tmp_idx
WHERE NOT EXISTS (
  SELECT 1 FROM Program p WHERE p.Faculty='Management' AND p.Level='Master' AND p.Discipline='NotAvailable' AND p.Course=CONCAT('MBA-FIN-', LPAD(tmp_idx.i,2,'0'))
);

INSERT INTO Program (Faculty, Level, Discipline, Course)
SELECT 'HUMANITIES', 'BACHELOR', 'LAW', CONCAT('BL-HON-', LPAD(i,2,'0'))
FROM tmp_idx
WHERE NOT EXISTS (
  SELECT 1 FROM Program p WHERE p.Faculty='HUMANITIES' AND p.Level='BACHELOR' AND p.Discipline='LAW' AND p.Course=CONCAT('BL-HON-', LPAD(tmp_idx.i,2,'0'))
);

INSERT INTO Program (Faculty, Level, Discipline, Course)
SELECT 'HUMANITIES', 'MASTER', 'LAW', CONCAT('ML-RES-', LPAD(i,2,'0'))
FROM tmp_idx
WHERE NOT EXISTS (
  SELECT 1 FROM Program p WHERE p.Faculty='HUMANITIES' AND p.Level='MASTER' AND p.Discipline='LAW' AND p.Course=CONCAT('ML-RES-', LPAD(tmp_idx.i,2,'0'))
);

-- --------------------------------------------------
-- Courses with valid legacy code pattern: XXX-123.4
-- --------------------------------------------------
INSERT IGNORE INTO course (CourseCode, CourseName, Faculty, Level, Discipline)
SELECT
  CONCAT('ENG-', LPAD(100 + MOD((i.i - 1) * 8 + s.s, 900), 3, '0'), '.', MOD(i.i + s.s, 9) + 1),
  CASE s.s
    WHEN 1 THEN 'Engineering Mathematics'
    WHEN 2 THEN 'Data Structures and Algorithms'
    WHEN 3 THEN 'Computer Networks'
    WHEN 4 THEN 'Database Systems'
    WHEN 5 THEN 'Operating Systems'
    WHEN 6 THEN 'Software Engineering'
    WHEN 7 THEN 'Machine Learning'
    ELSE 'Engineering Project'
  END,
  'Science And Technology', 'Bachelor', 'Engineering'
FROM tmp_idx i
CROSS JOIN tmp_slot s;

INSERT IGNORE INTO course (CourseCode, CourseName, Faculty, Level, Discipline)
SELECT
  CONCAT('BIM-', LPAD(200 + MOD((i.i - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(i.i + s.s + 1, 9) + 1),
  CASE s.s
    WHEN 1 THEN 'Human Anatomy'
    WHEN 2 THEN 'Pharmaceutical Chemistry'
    WHEN 3 THEN 'Pharmacology'
    WHEN 4 THEN 'Clinical Pharmacy'
    WHEN 5 THEN 'Biostatistics'
    WHEN 6 THEN 'Hospital Practice'
    WHEN 7 THEN 'Community Pharmacy'
    ELSE 'Pharmacy Project'
  END,
  'Science And Technology', 'Bachelor', 'Biomedical'
FROM tmp_idx i
CROSS JOIN tmp_slot s;

INSERT IGNORE INTO course (CourseCode, CourseName, Faculty, Level, Discipline)
SELECT
  CONCAT('MGT-', LPAD(300 + MOD((i.i - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(i.i + s.s + 2, 9) + 1),
  CASE s.s
    WHEN 1 THEN 'Strategic Management'
    WHEN 2 THEN 'Managerial Economics'
    WHEN 3 THEN 'Financial Reporting'
    WHEN 4 THEN 'Business Analytics'
    WHEN 5 THEN 'Leadership and Organization'
    WHEN 6 THEN 'Research Methods'
    WHEN 7 THEN 'Operations Management'
    ELSE 'MBA Capstone'
  END,
  'Management', 'Master', 'NotAvailable'
FROM tmp_idx i
CROSS JOIN tmp_slot s;

INSERT IGNORE INTO course (CourseCode, CourseName, Faculty, Level, Discipline)
SELECT
  CONCAT('LAW-', LPAD(400 + MOD((i.i - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(i.i + s.s + 3, 9) + 1),
  CASE s.s
    WHEN 1 THEN 'Constitutional Law'
    WHEN 2 THEN 'Contract Law'
    WHEN 3 THEN 'Jurisprudence'
    WHEN 4 THEN 'Criminal Law'
    WHEN 5 THEN 'Civil Procedure'
    WHEN 6 THEN 'Administrative Law'
    WHEN 7 THEN 'Legal Drafting'
    ELSE 'Legal Research Project'
  END,
  'HUMANITIES', 'BACHELOR', 'LAW'
FROM tmp_idx i
CROSS JOIN tmp_slot s;

INSERT IGNORE INTO course (CourseCode, CourseName, Faculty, Level, Discipline)
SELECT
  CONCAT('JUR-', LPAD(500 + MOD((i.i - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(i.i + s.s + 4, 9) + 1),
  CASE s.s
    WHEN 1 THEN 'Advanced Jurisprudence'
    WHEN 2 THEN 'Comparative Constitutional Law'
    WHEN 3 THEN 'International Law'
    WHEN 4 THEN 'Human Rights Law'
    WHEN 5 THEN 'Legal Theory'
    WHEN 6 THEN 'Policy and Regulation'
    WHEN 7 THEN 'Judicial Process'
    ELSE 'LLM Dissertation'
  END,
  'HUMANITIES', 'MASTER', 'LAW'
FROM tmp_idx i
CROSS JOIN tmp_slot s;

-- --------------------------------------------------
-- College-centre mappings
-- --------------------------------------------------
INSERT IGNORE INTO collegescentre (College, Course, Centre)
SELECT c.college_name, CONCAT('BE-AI-', LPAD(i.i,2,'0')), CASE WHEN MOD(i.i,2)=0 THEN 'PEC' ELSE 'PU' END
FROM tmp_idx i
JOIN tmp_college c ON c.k = MOD(i.i,8)+1;

INSERT IGNORE INTO collegescentre (College, Course, Centre)
SELECT c.college_name, CONCAT('BE-DATA-', LPAD(i.i,2,'0')), CASE WHEN MOD(i.i+1,2)=0 THEN 'PEC' ELSE 'PU' END
FROM tmp_idx i
JOIN tmp_college c ON c.k = MOD(i.i+1,8)+1;

INSERT IGNORE INTO collegescentre (College, Course, Centre)
SELECT c.college_name, CONCAT('BPHARM-ADV-', LPAD(i.i,2,'0')), CASE WHEN MOD(i.i+2,2)=0 THEN 'PEC' ELSE 'PU' END
FROM tmp_idx i
JOIN tmp_college c ON c.k = MOD(i.i+2,8)+1;

INSERT IGNORE INTO collegescentre (College, Course, Centre)
SELECT c.college_name, CONCAT('MBA-GEN-', LPAD(i.i,2,'0')), CASE WHEN MOD(i.i+3,2)=0 THEN 'PEC' ELSE 'PU' END
FROM tmp_idx i
JOIN tmp_college c ON c.k = MOD(i.i+3,8)+1;

INSERT IGNORE INTO collegescentre (College, Course, Centre)
SELECT c.college_name, CONCAT('MBA-FIN-', LPAD(i.i,2,'0')), CASE WHEN MOD(i.i+4,2)=0 THEN 'PEC' ELSE 'PU' END
FROM tmp_idx i
JOIN tmp_college c ON c.k = MOD(i.i+4,8)+1;

INSERT IGNORE INTO collegescentre (College, Course, Centre)
SELECT c.college_name, CONCAT('BL-HON-', LPAD(i.i,2,'0')), CASE WHEN MOD(i.i+5,2)=0 THEN 'PEC' ELSE 'PU' END
FROM tmp_idx i
JOIN tmp_college c ON c.k = MOD(i.i+5,8)+1;

INSERT IGNORE INTO collegescentre (College, Course, Centre)
SELECT c.college_name, CONCAT('ML-RES-', LPAD(i.i,2,'0')), CASE WHEN MOD(i.i+6,2)=0 THEN 'PEC' ELSE 'PU' END
FROM tmp_idx i
JOIN tmp_college c ON c.k = MOD(i.i+6,8)+1;

-- --------------------------------------------------
-- Regular registrations
-- --------------------------------------------------
INSERT IGNORE INTO regularcourse (CourseCode, Semester, Course, College, TotalCandidates)
SELECT CONCAT('ENG-', LPAD(100 + MOD((i.i - 1) * 8 + s.s, 900), 3, '0'), '.', MOD(i.i + s.s, 9) + 1),
       CAST(s.s AS CHAR),
       CONCAT('BE-AI-', LPAD(i.i,2,'0')),
       c.college_name,
       CAST(45 + ((i.i * 7 + s.s * 5) % 95) AS CHAR)
FROM tmp_idx i CROSS JOIN tmp_slot s JOIN tmp_college c ON c.k = MOD(i.i,8)+1;

INSERT IGNORE INTO regularcourse (CourseCode, Semester, Course, College, TotalCandidates)
SELECT CONCAT('ENG-', LPAD(100 + MOD((i.i - 1) * 8 + s.s, 900), 3, '0'), '.', MOD(i.i + s.s, 9) + 1),
       CAST(s.s AS CHAR),
       CONCAT('BE-DATA-', LPAD(i.i,2,'0')),
       c.college_name,
       CAST(45 + ((i.i * 11 + s.s * 3) % 95) AS CHAR)
FROM tmp_idx i CROSS JOIN tmp_slot s JOIN tmp_college c ON c.k = MOD(i.i+1,8)+1;

INSERT IGNORE INTO regularcourse (CourseCode, Semester, Course, College, TotalCandidates)
SELECT CONCAT('BIM-', LPAD(200 + MOD((i.i - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(i.i + s.s + 1, 9) + 1),
       CAST(s.s AS CHAR),
       CONCAT('BPHARM-ADV-', LPAD(i.i,2,'0')),
       c.college_name,
       CAST(40 + ((i.i * 9 + s.s * 4) % 90) AS CHAR)
FROM tmp_idx i CROSS JOIN tmp_slot s JOIN tmp_college c ON c.k = MOD(i.i+2,8)+1;

INSERT IGNORE INTO regularcourse (CourseCode, Semester, Course, College, TotalCandidates)
SELECT CONCAT('MGT-', LPAD(300 + MOD((i.i - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(i.i + s.s + 2, 9) + 1),
       CAST(s.s AS CHAR),
       CONCAT('MBA-GEN-', LPAD(i.i,2,'0')),
       c.college_name,
       CAST(50 + ((i.i * 5 + s.s * 7) % 110) AS CHAR)
FROM tmp_idx i CROSS JOIN tmp_slot s JOIN tmp_college c ON c.k = MOD(i.i+3,8)+1;

INSERT IGNORE INTO regularcourse (CourseCode, Semester, Course, College, TotalCandidates)
SELECT CONCAT('MGT-', LPAD(300 + MOD((i.i - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(i.i + s.s + 2, 9) + 1),
       CAST(s.s AS CHAR),
       CONCAT('MBA-FIN-', LPAD(i.i,2,'0')),
       c.college_name,
       CAST(50 + ((i.i * 13 + s.s * 2) % 110) AS CHAR)
FROM tmp_idx i CROSS JOIN tmp_slot s JOIN tmp_college c ON c.k = MOD(i.i+4,8)+1;

INSERT IGNORE INTO regularcourse (CourseCode, Semester, Course, College, TotalCandidates)
SELECT CONCAT('LAW-', LPAD(400 + MOD((i.i - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(i.i + s.s + 3, 9) + 1),
       CAST(s.s AS CHAR),
       CONCAT('BL-HON-', LPAD(i.i,2,'0')),
       c.college_name,
       CAST(35 + ((i.i * 6 + s.s * 6) % 80) AS CHAR)
FROM tmp_idx i CROSS JOIN tmp_slot s JOIN tmp_college c ON c.k = MOD(i.i+5,8)+1;

INSERT IGNORE INTO regularcourse (CourseCode, Semester, Course, College, TotalCandidates)
SELECT CONCAT('JUR-', LPAD(500 + MOD((i.i - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(i.i + s.s + 4, 9) + 1),
       CAST(s.s AS CHAR),
       CONCAT('ML-RES-', LPAD(i.i,2,'0')),
       c.college_name,
       CAST(25 + ((i.i * 8 + s.s * 3) % 70) AS CHAR)
FROM tmp_idx i CROSS JOIN tmp_slot s JOIN tmp_college c ON c.k = MOD(i.i+6,8)+1;

-- --------------------------------------------------
-- Back paper candidates (high complexity load)
-- Pattern: each synthetic candidate carries 6-8 failed papers with
-- shared hotspot subjects so the conflict graph is much denser.
-- --------------------------------------------------

-- Engineering backlog candidates (AI and DATA tracks)
-- Core shared failures (all candidates): high overlap on even semesters.
INSERT IGNORE INTO backpapercandidate (CourseCode, ID, College, Course, Semester)
SELECT CONCAT('ENG-', LPAD(100 + MOD((i.i - 1) * 8 + s.s, 900), 3, '0'), '.', MOD(i.i + s.s, 9) + 1),
       CONCAT('BK-AI-', LPAD(i.i,2,'0')),
       c.college_name,
       CONCAT('BE-AI-', LPAD(i.i,2,'0')),
       ((s.s - 1) % 8) + 1
FROM tmp_idx i
CROSS JOIN tmp_slot s
JOIN tmp_college c ON c.k = MOD(i.i,8)+1
WHERE s.s IN (2,4,6,8);

-- Additional hotspot failures (half cohort): shared cores + odd semesters.
INSERT IGNORE INTO backpapercandidate (CourseCode, ID, College, Course, Semester)
SELECT CONCAT('ENG-', LPAD(100 + MOD((MOD(i.i,5) + 1 - 1) * 8 + s.s, 900), 3, '0'), '.', MOD(MOD(i.i,5) + s.s, 9) + 1),
       CONCAT('BK-AI-', LPAD(i.i,2,'0')),
       c.college_name,
       CONCAT('BE-AI-', LPAD(i.i,2,'0')),
       ((s.s - 1) % 8) + 1
FROM tmp_idx i
CROSS JOIN tmp_slot s
JOIN tmp_college c ON c.k = MOD(i.i,8)+1
WHERE MOD(i.i,2)=0
  AND s.s IN (1,3,5,7);

INSERT IGNORE INTO backpapercandidate (CourseCode, ID, College, Course, Semester)
SELECT CONCAT('ENG-', LPAD(100 + MOD((i.i - 1) * 8 + s.s, 900), 3, '0'), '.', MOD(i.i + s.s, 9) + 1),
       CONCAT('BK-DATA-', LPAD(i.i,2,'0')),
       c.college_name,
       CONCAT('BE-DATA-', LPAD(i.i,2,'0')),
       ((s.s - 1) % 8) + 1
FROM tmp_idx i
CROSS JOIN tmp_slot s
JOIN tmp_college c ON c.k = MOD(i.i+1,8)+1
WHERE s.s IN (1,3,5,7);

INSERT IGNORE INTO backpapercandidate (CourseCode, ID, College, Course, Semester)
SELECT CONCAT('ENG-', LPAD(100 + MOD((MOD(i.i,5) + 1 - 1) * 8 + s.s, 900), 3, '0'), '.', MOD(MOD(i.i,5) + s.s, 9) + 1),
       CONCAT('BK-DATA-', LPAD(i.i,2,'0')),
       c.college_name,
       CONCAT('BE-DATA-', LPAD(i.i,2,'0')),
       ((s.s - 1) % 8) + 1
FROM tmp_idx i
CROSS JOIN tmp_slot s
JOIN tmp_college c ON c.k = MOD(i.i+1,8)+1
WHERE MOD(i.i,2)=1
  AND s.s IN (2,4,6,8);

-- Biomedical backlog candidates
INSERT IGNORE INTO backpapercandidate (CourseCode, ID, College, Course, Semester)
SELECT CONCAT('BIM-', LPAD(200 + MOD((i.i - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(i.i + s.s + 1, 9) + 1),
       CONCAT('BK-PHARM-', LPAD(i.i,2,'0')),
       c.college_name,
       CONCAT('BPHARM-ADV-', LPAD(i.i,2,'0')),
       ((s.s - 1) % 8) + 1
FROM tmp_idx i
CROSS JOIN tmp_slot s
JOIN tmp_college c ON c.k = MOD(i.i+2,8)+1
WHERE s.s IN (3,4,7,8);

INSERT IGNORE INTO backpapercandidate (CourseCode, ID, College, Course, Semester)
SELECT CONCAT('BIM-', LPAD(200 + MOD((MOD(i.i,6) + 1 - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(MOD(i.i,6) + s.s + 1, 9) + 1),
       CONCAT('BK-PHARM-', LPAD(i.i,2,'0')),
       c.college_name,
       CONCAT('BPHARM-ADV-', LPAD(i.i,2,'0')),
       ((s.s - 1) % 8) + 1
FROM tmp_idx i
CROSS JOIN tmp_slot s
JOIN tmp_college c ON c.k = MOD(i.i+2,8)+1
WHERE MOD(i.i,3)<>0
  AND s.s IN (1,2,5,6);

-- MBA backlog candidates (general and finance)
INSERT IGNORE INTO backpapercandidate (CourseCode, ID, College, Course, Semester)
SELECT CONCAT('MGT-', LPAD(300 + MOD((i.i - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(i.i + s.s + 2, 9) + 1),
       CONCAT('BK-MBA-GEN-', LPAD(i.i,2,'0')),
       c.college_name,
       CONCAT('MBA-GEN-', LPAD(i.i,2,'0')),
       ((s.s - 1) % 8) + 1
FROM tmp_idx i
CROSS JOIN tmp_slot s
JOIN tmp_college c ON c.k = MOD(i.i+3,8)+1
WHERE s.s IN (2,3,6,8);

INSERT IGNORE INTO backpapercandidate (CourseCode, ID, College, Course, Semester)
SELECT CONCAT('MGT-', LPAD(300 + MOD((MOD(i.i,4) + 1 - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(MOD(i.i,4) + s.s + 2, 9) + 1),
       CONCAT('BK-MBA-GEN-', LPAD(i.i,2,'0')),
       c.college_name,
       CONCAT('MBA-GEN-', LPAD(i.i,2,'0')),
       ((s.s - 1) % 8) + 1
FROM tmp_idx i
CROSS JOIN tmp_slot s
JOIN tmp_college c ON c.k = MOD(i.i+3,8)+1
WHERE s.s IN (1,4,5,7);

INSERT IGNORE INTO backpapercandidate (CourseCode, ID, College, Course, Semester)
SELECT CONCAT('MGT-', LPAD(300 + MOD((i.i - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(i.i + s.s + 2, 9) + 1),
       CONCAT('BK-MBA-FIN-', LPAD(i.i,2,'0')),
       c.college_name,
       CONCAT('MBA-FIN-', LPAD(i.i,2,'0')),
       ((s.s - 1) % 8) + 1
FROM tmp_idx i
CROSS JOIN tmp_slot s
JOIN tmp_college c ON c.k = MOD(i.i+4,8)+1
WHERE s.s IN (1,4,5,7);

INSERT IGNORE INTO backpapercandidate (CourseCode, ID, College, Course, Semester)
SELECT CONCAT('MGT-', LPAD(300 + MOD((MOD(i.i,4) + 1 - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(MOD(i.i,4) + s.s + 2, 9) + 1),
       CONCAT('BK-MBA-FIN-', LPAD(i.i,2,'0')),
       c.college_name,
       CONCAT('MBA-FIN-', LPAD(i.i,2,'0')),
       ((s.s - 1) % 8) + 1
FROM tmp_idx i
CROSS JOIN tmp_slot s
JOIN tmp_college c ON c.k = MOD(i.i+4,8)+1
WHERE s.s IN (2,3,6,8);

-- Law backlog candidates (BL and ML)
INSERT IGNORE INTO backpapercandidate (CourseCode, ID, College, Course, Semester)
SELECT CONCAT('LAW-', LPAD(400 + MOD((i.i - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(i.i + s.s + 3, 9) + 1),
       CONCAT('BK-BL-', LPAD(i.i,2,'0')),
       c.college_name,
       CONCAT('BL-HON-', LPAD(i.i,2,'0')),
       ((s.s - 1) % 8) + 1
FROM tmp_idx i
CROSS JOIN tmp_slot s
JOIN tmp_college c ON c.k = MOD(i.i+5,8)+1
WHERE s.s IN (2,4,6,8);

INSERT IGNORE INTO backpapercandidate (CourseCode, ID, College, Course, Semester)
SELECT CONCAT('LAW-', LPAD(400 + MOD((MOD(i.i,4) + 1 - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(MOD(i.i,4) + s.s + 3, 9) + 1),
       CONCAT('BK-BL-', LPAD(i.i,2,'0')),
       c.college_name,
       CONCAT('BL-HON-', LPAD(i.i,2,'0')),
       ((s.s - 1) % 8) + 1
FROM tmp_idx i
CROSS JOIN tmp_slot s
JOIN tmp_college c ON c.k = MOD(i.i+5,8)+1
WHERE s.s IN (1,3,5,7);

INSERT IGNORE INTO backpapercandidate (CourseCode, ID, College, Course, Semester)
SELECT CONCAT('JUR-', LPAD(500 + MOD((i.i - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(i.i + s.s + 4, 9) + 1),
       CONCAT('BK-ML-', LPAD(i.i,2,'0')),
       c.college_name,
       CONCAT('ML-RES-', LPAD(i.i,2,'0')),
       ((s.s - 1) % 8) + 1
FROM tmp_idx i
CROSS JOIN tmp_slot s
JOIN tmp_college c ON c.k = MOD(i.i+6,8)+1
WHERE s.s IN (1,3,5,7);

INSERT IGNORE INTO backpapercandidate (CourseCode, ID, College, Course, Semester)
SELECT CONCAT('JUR-', LPAD(500 + MOD((MOD(i.i,4) + 1 - 1) * 8 + s.s, 700), 3, '0'), '.', MOD(MOD(i.i,4) + s.s + 4, 9) + 1),
       CONCAT('BK-ML-', LPAD(i.i,2,'0')),
       c.college_name,
       CONCAT('ML-RES-', LPAD(i.i,2,'0')),
       ((s.s - 1) % 8) + 1
FROM tmp_idx i
CROSS JOIN tmp_slot s
JOIN tmp_college c ON c.k = MOD(i.i+6,8)+1
WHERE s.s IN (2,4,6,8);

DROP TEMPORARY TABLE tmp_college;
DROP TEMPORARY TABLE tmp_slot;
DROP TEMPORARY TABLE tmp_idx;
