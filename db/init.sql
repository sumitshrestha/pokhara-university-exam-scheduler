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

-- -------------------------------------------
-- Table: program (called "Program" in app)
-- Maps faculty / level / discipline to programs
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `program` (
  `Faculty`    VARCHAR(100) DEFAULT NULL,
  `Level`      VARCHAR(100) DEFAULT NULL,
  `Discipline` VARCHAR(100) DEFAULT NULL,
  `Course`     VARCHAR(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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

-- -------------------------------------------
-- Table: level  (legacy / unused)
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `level` (
  `Level` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`Level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
