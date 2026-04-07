-- PU Exam Routine - Database Initialization Script
-- Generated from exported MyISAM data (MySQL 5.7)
-- Compatible with MySQL 8.x (InnoDB, utf8mb4)

CREATE DATABASE IF NOT EXISTS `puroutine`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `puroutine`;

-- -------------------------------------------
-- Table: examdivision
-- Stores faculty / level / discipline groupings
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `examdivision` (
  `Faculty`     VARCHAR(100) DEFAULT NULL,
  `Level`       VARCHAR(100) DEFAULT NULL,
  `Discipline`  VARCHAR(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `examdivision` (`Faculty`, `Level`, `Discipline`) VALUES
  ('Science And Technology', 'Bachelor', 'Engineering'),
  ('Science And Technology', 'Bachelor', 'Biomedical'),
  ('Management',              'Master',   'NotAvailable'),
  ('HUMANITIES',              'MASTER',   'LAW'),
  ('HUMANITIES',              'BACHELOR', 'LAW');

-- -------------------------------------------
-- Table: centre
-- Exam centres with location and student capacity
-- Note: MaxLimit stored as VARCHAR to match legacy schema
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `centre` (
  `Centre`   VARCHAR(100) NOT NULL,
  `Place`    VARCHAR(100) DEFAULT NULL,
  `MaxLimit` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`Centre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `centre` (`Centre`, `Place`, `MaxLimit`) VALUES
  ('PEC', 'pokhara',    '999999'),
  ('PU',  'dungepatan', '999999');

-- -------------------------------------------
-- Table: collegescentre
-- Maps college + program to an exam centre
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `collegescentre` (
  `College` VARCHAR(100) NOT NULL,
  `Course`  VARCHAR(100) NOT NULL,
  `Centre`  VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`College`, `Course`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `collegescentre` (`College`, `Course`, `Centre`) VALUES
  ('GCES',  'BESE',       'PEC'),
  ('PEC',   'BECE',       'PU'),
  ('SPBS',  'BPHARM',     'PEC'),
  ('PUMC',  'MBA',        'PU'),
  ('NEC',   'BARCH',      'PEC'),
  ('APEX',  'MBA',        'PEC'),
  ('NEC',   'BECIVILDIP', 'PEC'),
  ('NEC',   'BEEC',       'PEC'),
  ('NEC',   'BEIT',       'PEC'),
  ('NEC',   'BECIVIL',    'PEC'),
  ('CART',  'BL',         'PU'),
  ('PEC',   'MBA',        'pec'),
  ('pec1',  'MBA',        'pec'),
  ('coll2', 'MBA',        'pec'),
  ('coll1', 'MBA',        'pec'),
  ('CART',  'ML',         'PEC'),
  ('APEX',  'BAHUM',      'PEC'),
  ('COLL2', 'PP',         'PEC'),
  ('COLL1', 'PP',         'PEC'),
  ('PEC2',  'PRG2',       'PEC'),
  ('PEC',   'PRG',        'PEC'),
  ('CHUM',  'BL',         'PU');

-- -------------------------------------------
-- Table: course
-- Master list of courses (subject codes + names)
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `course` (
  `CourseCode` VARCHAR(100) NOT NULL,
  `CourseName` VARCHAR(100) DEFAULT NULL,
  `Faculty`    VARCHAR(100) DEFAULT NULL,
  `Level`      VARCHAR(100) DEFAULT NULL,
  `Discipline` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`CourseCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `course` (`CourseCode`, `CourseName`, `Faculty`, `Level`, `Discipline`) VALUES
  ('ENG-101.1', 'Engineering Mathematics I',       'Science And Technology', 'Bachelor', 'Engineering'),
  ('ENG-102.1', 'Engineering Physics',             'Science And Technology', 'Bachelor', 'Engineering'),
  ('CE-201.1',  'Surveying and Geomatics',         'Science And Technology', 'Bachelor', 'Engineering'),
  ('EE-201.1',  'Basic Electrical Engineering',    'Science And Technology', 'Bachelor', 'Engineering'),
  ('IT-201.1',  'Object Oriented Programming',     'Science And Technology', 'Bachelor', 'Engineering'),
  ('AR-101.1',  'Architectural Graphics',          'Science And Technology', 'Bachelor', 'Engineering'),
  ('CVD-101.1', 'Civil Drafting',                  'Science And Technology', 'Bachelor', 'Engineering'),
  ('BIM-101.1', 'Human Anatomy',                   'Science And Technology', 'Bachelor', 'Biomedical'),
  ('BIM-102.1', 'Pharmaceutical Chemistry',        'Science And Technology', 'Bachelor', 'Biomedical'),
  ('MGT-501.1', 'Strategic Management',            'Management',             'Master',   'NotAvailable'),
  ('MGT-502.1', 'Business Research Methods',       'Management',             'Master',   'NotAvailable'),
  ('PRG-201.1', 'Project Management',              'Management',             'Master',   'NotAvailable'),
  ('LAW-301.1', 'Constitutional Law',              'HUMANITIES',             'BACHELOR', 'LAW'),
  ('LAW-302.1', 'Contract Law',                    'HUMANITIES',             'BACHELOR', 'LAW'),
  ('LAW-401.1', 'Advanced Jurisprudence',          'HUMANITIES',             'MASTER',   'LAW'),
  ('HUM-301.1', 'Humanities and Society',          'HUMANITIES',             'BACHELOR', 'LAW'),
  ('PP-101.1',  'Public Policy Fundamentals',      'Management',             'Master',   'NotAvailable');

-- -------------------------------------------
-- Table: Program (legacy table name is case-sensitive on Linux)
-- Maps faculty / level / discipline to programs
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `Program` (
  `Faculty`    VARCHAR(100) DEFAULT NULL,
  `Level`      VARCHAR(100) DEFAULT NULL,
  `Discipline` VARCHAR(100) DEFAULT NULL,
  `Course`     VARCHAR(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `Program` (`Faculty`, `Level`, `Discipline`, `Course`) VALUES
  ('Science And Technology', 'Bachelor', 'Engineering', 'BESE'),
  ('Science And Technology', 'Bachelor', 'Engineering', 'BECE'),
  ('Science And Technology', 'Bachelor', 'Engineering', 'BARCH'),
  ('Science And Technology', 'Bachelor', 'Engineering', 'BECIVILDIP'),
  ('Science And Technology', 'Bachelor', 'Engineering', 'BEEC'),
  ('Science And Technology', 'Bachelor', 'Engineering', 'BEIT'),
  ('Science And Technology', 'Bachelor', 'Engineering', 'BECIVIL'),
  ('Science And Technology', 'Bachelor', 'Biomedical',  'BPHARM'),
  ('Management',             'Master',   'NotAvailable','MBA'),
  ('Management',             'Master',   'NotAvailable','PRG'),
  ('Management',             'Master',   'NotAvailable','PRG2'),
  ('Management',             'Master',   'NotAvailable','PP'),
  ('HUMANITIES',             'MASTER',   'LAW',         'ML'),
  ('HUMANITIES',             'BACHELOR', 'LAW',         'BL'),
  ('HUMANITIES',             'BACHELOR', 'LAW',         'BAHUM');

-- -------------------------------------------
-- Table: regularcourse
-- Regular candidates mapped to courses per college/semester
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `regularcourse` (
  `CourseCode`      VARCHAR(100) NOT NULL,
  `Semester`        VARCHAR(100) NOT NULL,
  `Course`          VARCHAR(100) NOT NULL,
  `College`         VARCHAR(100) NOT NULL,
  `TotalCandidates` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`CourseCode`, `Semester`, `Course`, `College`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `regularcourse` (`CourseCode`, `Semester`, `Course`, `College`, `TotalCandidates`) VALUES
  ('ENG-101.1', '1', 'BESE',       'GCES',  '62'),
  ('ENG-102.1', '1', 'BESE',       'GCES',  '62'),
  ('CE-201.1',  '1', 'BECE',       'PEC',   '78'),
  ('ENG-101.1', '1', 'BECE',       'PEC',   '78'),
  ('AR-101.1',  '1', 'BARCH',      'NEC',   '40'),
  ('ENG-102.1', '1', 'BARCH',      'NEC',   '40'),
  ('CVD-101.1', '2', 'BECIVILDIP', 'NEC',   '35'),
  ('CE-201.1',  '2', 'BECIVILDIP', 'NEC',   '35'),
  ('EE-201.1',  '2', 'BEEC',       'NEC',   '58'),
  ('ENG-102.1', '2', 'BEEC',       'NEC',   '58'),
  ('IT-201.1',  '2', 'BEIT',       'NEC',   '55'),
  ('ENG-101.1', '2', 'BEIT',       'NEC',   '55'),
  ('CE-201.1',  '3', 'BECIVIL',    'NEC',   '66'),
  ('ENG-102.1', '3', 'BECIVIL',    'NEC',   '66'),
  ('BIM-101.1', '1', 'BPHARM',     'SPBS',  '51'),
  ('BIM-102.1', '1', 'BPHARM',     'SPBS',  '51'),
  ('MGT-501.1', '1', 'MBA',        'PUMC',  '48'),
  ('MGT-502.1', '1', 'MBA',        'PUMC',  '48'),
  ('MGT-501.1', '1', 'MBA',        'APEX',  '44'),
  ('MGT-502.1', '1', 'MBA',        'APEX',  '44'),
  ('MGT-501.1', '1', 'MBA',        'PEC',   '60'),
  ('MGT-502.1', '1', 'MBA',        'PEC',   '60'),
  ('PRG-201.1', '1', 'PRG',        'PEC',   '25'),
  ('PRG-201.1', '1', 'PRG2',       'PEC2',  '18'),
  ('PP-101.1',  '1', 'PP',         'COLL1', '32'),
  ('PP-101.1',  '1', 'PP',         'COLL2', '29'),
  ('LAW-301.1', '1', 'BL',         'CART',  '38'),
  ('LAW-302.1', '1', 'BL',         'CART',  '38'),
  ('LAW-301.1', '1', 'BL',         'CHUM',  '27'),
  ('LAW-302.1', '1', 'BL',         'CHUM',  '27'),
  ('LAW-401.1', '1', 'ML',         'CART',  '21'),
  ('HUM-301.1', '1', 'BAHUM',      'APEX',  '24');

-- -------------------------------------------
-- Table: backpapercandidate
-- Back-paper (re-exam) candidates
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `backpapercandidate` (
  `CourseCode` VARCHAR(100) NOT NULL,
  `ID`         VARCHAR(100) NOT NULL,
  `College`    VARCHAR(100) DEFAULT NULL,
  `Course`     VARCHAR(100) DEFAULT NULL,
  `Semester`   INT          DEFAULT NULL,
  PRIMARY KEY (`ID`, `CourseCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `backpapercandidate` (`CourseCode`, `ID`, `College`, `Course`, `Semester`) VALUES
  ('ENG-101.1', 'BESE-001',  'GCES',  'BESE',   1),
  ('ENG-102.1', 'BESE-001',  'GCES',  'BESE',   1),
  ('CE-201.1',  'BECE-014',  'PEC',   'BECE',   1),
  ('MGT-501.1', 'MBA-010',   'PUMC',  'MBA',    1),
  ('LAW-301.1', 'BL-007',    'CART',  'BL',     1),
  ('BIM-102.1', 'BPH-019',   'SPBS',  'BPHARM', 1);

-- -------------------------------------------
-- Table: level  (legacy / unused)
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `level` (
  `Level` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`Level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `level` (`Level`) VALUES
  ('Bachelor'),
  ('Master');
