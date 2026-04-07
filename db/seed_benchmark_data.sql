-- Synthetic benchmark dataset for scheduler performance testing
-- Idempotent: safe to run multiple times.

USE puroutine;

CREATE TEMPORARY TABLE tmp_grp (n INT PRIMARY KEY);
INSERT INTO tmp_grp (n) VALUES
  (1),(2),(3),(4),(5),(6),(7),(8),(9),(10),
  (11),(12),(13),(14),(15),(16),(17),(18),(19),(20);

CREATE TEMPORARY TABLE tmp_prg (p INT PRIMARY KEY);
INSERT INTO tmp_prg (p) VALUES (1),(2),(3),(4);

CREATE TEMPORARY TABLE tmp_crs (c INT PRIMARY KEY);
INSERT INTO tmp_crs (c) VALUES (1),(2),(3),(4),(5),(6);

-- 20 synthetic groups
INSERT INTO examdivision (Faculty, Level, Discipline)
SELECT
  'BENCHMARK',
  CASE WHEN MOD(g.n, 2) = 0 THEN 'MASTER' ELSE 'BACHELOR' END,
  CONCAT('BENCHDISC', LPAD(g.n, 2, '0'))
FROM tmp_grp g
WHERE NOT EXISTS (
  SELECT 1
  FROM examdivision e
  WHERE e.Faculty = 'BENCHMARK'
    AND e.Level = CASE WHEN MOD(g.n, 2) = 0 THEN 'MASTER' ELSE 'BACHELOR' END
    AND e.Discipline = CONCAT('BENCHDISC', LPAD(g.n, 2, '0'))
);

-- 80 synthetic programs (4 per group)
INSERT INTO `Program` (Faculty, Level, Discipline, Course)
SELECT
  'BENCHMARK',
  CASE WHEN MOD(g.n, 2) = 0 THEN 'MASTER' ELSE 'BACHELOR' END,
  CONCAT('BENCHDISC', LPAD(g.n, 2, '0')),
  CONCAT('BPRG', LPAD(g.n, 2, '0'), '_', p.p)
FROM tmp_grp g
CROSS JOIN tmp_prg p
WHERE NOT EXISTS (
  SELECT 1
  FROM `Program` x
  WHERE x.Faculty = 'BENCHMARK'
    AND x.Level = CASE WHEN MOD(g.n, 2) = 0 THEN 'MASTER' ELSE 'BACHELOR' END
    AND x.Discipline = CONCAT('BENCHDISC', LPAD(g.n, 2, '0'))
    AND x.Course = CONCAT('BPRG', LPAD(g.n, 2, '0'), '_', p.p)
);

-- 120 synthetic courses (6 per group)
INSERT IGNORE INTO course (CourseCode, CourseName, Faculty, Level, Discipline)
SELECT
  CONCAT('BMC', LPAD(g.n, 2, '0'), LPAD(c.c, 2, '0')),
  CONCAT('Benchmark Course ', LPAD(g.n, 2, '0'), '-', c.c),
  'BENCHMARK',
  CASE WHEN MOD(g.n, 2) = 0 THEN 'MASTER' ELSE 'BACHELOR' END,
  CONCAT('BENCHDISC', LPAD(g.n, 2, '0'))
FROM tmp_grp g
CROSS JOIN tmp_crs c;

-- Program-college-centre mapping (1 college per synthetic program)
INSERT IGNORE INTO collegescentre (College, Course, Centre)
SELECT
  CONCAT('BCOL', LPAD(g.n, 2, '0'), '_', p.p),
  CONCAT('BPRG', LPAD(g.n, 2, '0'), '_', p.p),
  CASE WHEN MOD(p.p, 2) = 0 THEN 'PEC' ELSE 'PU' END
FROM tmp_grp g
CROSS JOIN tmp_prg p;

-- Regular registrations: 6 courses for each synthetic program
INSERT IGNORE INTO regularcourse (CourseCode, Semester, Course, College, TotalCandidates)
SELECT
  CONCAT('BMC', LPAD(g.n, 2, '0'), LPAD(c.c, 2, '0')),
  CAST(((c.c - 1) % 4) + 1 AS CHAR),
  CONCAT('BPRG', LPAD(g.n, 2, '0'), '_', p.p),
  CONCAT('BCOL', LPAD(g.n, 2, '0'), '_', p.p),
  CAST(40 + ((g.n * 3 + p.p * 5 + c.c * 7) % 85) AS CHAR)
FROM tmp_grp g
CROSS JOIN tmp_prg p
CROSS JOIN tmp_crs c;

DROP TEMPORARY TABLE tmp_crs;
DROP TEMPORARY TABLE tmp_prg;
DROP TEMPORARY TABLE tmp_grp;
