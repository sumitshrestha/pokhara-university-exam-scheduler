
//                          !! RAM !!

package puexamroutine.control.database;

import java.sql.*;

/**
 * This class writes into the database.
 * It is mainly for the purpose of storing the information into the database of PU Routine that has been inputted by the user.
 *
 * @author Sumit Shresth
 */
public class DatabaseWriter extends DatabaseReader {
    
    private boolean DEBUG = false;
    
    public DatabaseWriter( final String CourseCodeName1, final String ProgramField1, final String RegularCourseTableName1, final String CourseTable1, final String SemesterFieldName1, final String DatabaseName1, final String BackTable1, final String CandidateIDField1, final String PrgTab, final String FacField, final String LevelField ){   
        super(CourseCodeName1, ProgramField1, RegularCourseTableName1, CourseTable1, SemesterFieldName1, DatabaseName1, BackTable1, CandidateIDField1, PrgTab, FacField, LevelField ,puexamroutine.control.database.DatabaseReader.getDefaultCourseNameField() );
    }

    @Override
    public void setConnection( Connection c ){
        try{
            super.setConnection(c);
        }
        catch( Exception e ){
            this.troubleShoot();
        }
    }

    private void onDebug(final String Query) throws SQLException {
        if( ! this.DEBUG )
            return;
        System.out.println(Query);
        //temp code
        super.Statement.getConnection().rollback();
        System.exit(0);
    }

    private void removeFaculty(final String fac) throws SQLException {
        String[] levels = super.readLevels(fac);
        for (int i = 0; i < levels.length; i++) {
            this.deleteLevel(levels[i], fac);
        }
    }

    private void troubleShoot(){
        if( ! super.doesDatabaseExists() )
            this.createDatabase();        
        super.useDatabase();
        if( ! super.isExamDivisionTableValid() )
            this.createExamDivisionTable();
        if( ! super.isCourseTableValid() )
            this.createCourseTable();
        if( ! super.isProgramTableValid() )
            this.createProgramTable();        
        if( ! super.isCentreTableValid() )
            this.createCentreTable();
        if( ! super.isCollegeCentreTableValid() )
            this.createCollegeCentreTable();
        if( ! super.isRegularCourseTableValid() )
            this.createRegularCourseTable();
        if( ! super.isBackCandidateTableValid() )
            this.createBackCandidateTable();        
    }
    
    private boolean createDatabase(){
        try{            
            final String Query = "create Database " + super.DatabaseName + ";";
            return super.Statement.execute(Query);
        }
        catch( Exception e ){
            return false;
        }
    }
    
    private boolean createExamDivisionTable(){
        try{
            final String Query = "create table "+ super.ExamDivisionTable + " ( "+ super.FacultyField+" varchar(100), "+ super.LevelField+" varchar(100), "+ super.DisciplineField + " varchar(100), primary key( "+super.FacultyField+" , "+super.LevelField+" , "+super.DisciplineField + ") );";
            onDebug( Query );
            super.Statement.executeUpdate(Query);
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    private boolean createProgramTable(){
        try{
            final String Query = "create table "+super.ProgramTableName+ " ( "+ super.FacultyField+" varchar(100), "+ super.LevelField+" varchar(100), "+ super.DisciplineField + " varchar(100), " + super.ProgramField+" varchar(100) );";
            return super.Statement.execute(Query);
        }
        catch( Exception e ){
            return false;
        }
    }
    
    private boolean createCentreTable(){
        try{
            final String Query = "create table "+ super.ExamCentreTable+" ( "+super.ExamCentreField+" varchar(100), "+ super.ExamCentreLimitField+" integer(100), primary key( "+ super.ExamCentreField + ") );";
            return super.Statement.execute(Query);
        }
        catch( Exception e ){
            return false;
        }
    }
    
    private boolean createCollegeCentreTable(){
        try{
            final String Query = "create table "+ super.CollegeCentreTable+" ( "+super.College+" varchar(100), "+super.ProgramField+" varchar(100), "+super.ExamCentreField+" varchar(100) );";
            return super.Statement.execute(Query);
        }
        catch( Exception e ){
            return false;
        }
    }
    
    private boolean createRegularCourseTable(){
        try{
            final String Query = "create table "+ super.RegularCourseTableName+" ( "+super.CourseCodeField+" varchar(100), "+super.SemesterFieldName+" varchar(100), "+ super.ProgramField+" varchar(100), "+ super.College+" varchar(100), "+ super.TotalCandidateField+" varchar(100), primary key ( "+super.CourseCodeField+ ", " + super.SemesterFieldName+", "+super.ProgramField+", "+super.College+" ) );";            
            this.onDebug(Query);
            return super.Statement.execute(Query);
        }
        catch( Exception e ){
            return false;
        }
    }
    
    private boolean createBackCandidateTable(){
        try{            
            final String Query = "create table "+super.BackTable+" ( "+super.CourseCodeField+" varchar(100), "+super.CandidateIDField+" varchar(100), "+super.College+" varchar(100), "+super.ProgramField+" varchar(100), "+super.SemesterFieldName+" integer(100), primary key( "+ super.CandidateIDField+", "+super.CourseCodeField + ") );";
            return super.Statement.execute(Query);
        }
        catch( Exception e ){
            return false;
        }
    }
    
    private boolean createCourseTable(){
        try{            
            final String Query = "create table "+super.CourseTable+" ( "+super.CourseCodeField+" varchar(100), "+super.CourseCodeNameField+" varchar(100), "+super.FacultyField + " varchar(100), "+super.LevelField+" varchar(100), "+super.DisciplineField +" varchar(100), primary key( "+ super.CourseCodeField + ") );";            
            this.onDebug(Query);
            return super.Statement.execute(Query);
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean updateRegularCourseTotalCandidates( final String NewSumCand, final String Course, final String Sem, final String Program, final String college ){
        try{
            final String Query = "update "+super.RegularCourseTableName+" set "+ super.TotalCandidateField+"= '"+ NewSumCand + "' where "+super.CourseCodeField+"= '"+Course+"' AND "+ super.SemesterFieldName+"= '"+Sem+"' AND "+super.ProgramField+"= '"+Program+"' AND "+super.College+"= '"+ college+"';";
            super.Statement.executeUpdate( Query );            
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean addRegularCourse( final String Course, final String College, final String Program, final String Sem, final java.lang.Integer TotalCandidates ){
        try{
            final String Query = "insert into "+super.RegularCourseTableName + "("+super.ProgramField+","+super.College+","+super.SemesterFieldName+","+super.CourseCodeField+","+super.TotalCandidateField+") values ('"+Program+"', '"+College+"', '"+Sem+"', '"+Course+"','"+TotalCandidates.toString()+"');";
            super.Statement.executeUpdate( Query );
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean removeBackCourse( final String Course, final String College, final String Program, final String CandID ){
        try{
            final String Query = "delete from "+super.BackTable+" where "+super.CourseCodeField+"='"+Course+"' AND "+super.College+"='"+College+"' AND "+super.ProgramField+"='"+Program+"' AND "+super.CandidateIDField+"='"+CandID+"';";
            super.Statement.executeUpdate( Query );
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean removeRegularCourse( final String Course, final String College, final String Program, final String Sem ){
        try{
            final String Query = "delete from "+super.RegularCourseTableName+" where "+super.CourseCodeField+"='"+Course+"' AND "+super.College+"='"+College+"' AND "+super.ProgramField+"='"+Program+"' AND "+super.SemesterFieldName+"='"+Sem+"';";
            super.Statement.executeUpdate( Query );
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean updateRegularCourse( final String Course, final String College, final String Program, final String Sem, final String NewCourse ){
        try{
            final String Query = "update "+super.RegularCourseTableName+" set "+super.CourseCodeField+"='"+NewCourse+"' where "+super.CourseCodeField+"='"+Course+"' AND "+super.College+"='"+College+"' AND "+super.ProgramField+"='"+Program+"' AND "+super.SemesterFieldName+"='"+Sem+"';";
            super.Statement.executeUpdate( Query );
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean removeRegularSemester( final String College, final String Program, final String Sem ){
        try{
            final String Query = "delete from "+super.RegularCourseTableName+" where "+super.College+"='"+College+"' AND "+super.ProgramField+"='"+Program+"' AND "+super.SemesterFieldName+"='"+Sem+"';";
            super.Statement.executeUpdate( Query );
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean updateRegularSemester( final String College, final String Program, final String Sem, final String NewSem ){
        try{
            final String Query = "update "+super.RegularCourseTableName+" set "+super.SemesterFieldName+"='"+NewSem+"' where "+super.College+"='"+College+"' AND "+super.ProgramField+"='"+Program+"' AND "+super.SemesterFieldName+"='"+Sem+"';";
            super.Statement.executeUpdate( Query );
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
        
    public boolean addBackCandidate( final String CandID, final String Course, final String Prg, final String Coll ,int Sem ){
        try{
            final String Query = "insert into "+super.BackTable+"("+super.CandidateIDField+","+super.CourseCodeField+","+super.ProgramField+","+super.College+","+super.SemesterFieldName+") values ('"+CandID+"','"+Course+"','"+Prg+"','"+Coll+"',"+Sem+");";
            super.Statement.executeUpdate( Query );
            return true;
        }
        catch( Exception e ){            
            return false;
        }
    }
    
    public boolean addCollegeCentre( final String College, final String Centre, final String Prg ){
        try{
            final String Query = "insert into "+super.CollegeCentreTable +"("+super.College+","+super.ProgramField+","+super.ExamCentreField+") values ('"+College+"','"+Prg+"','"+Centre+"');";
            super.Statement.executeUpdate( Query );
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean addProgram( final String Program, final String Faculty, final String level, final String discp ){
        try{
            final String Query = "insert into "+super.ProgramTableName+"("+super.ProgramField+","+super.FacultyField+","+super.LevelField+","+super.DisciplineField+") values ('"+Program+"','"+Faculty+"','"+level+"','"+discp+"');";
            super.Statement.executeUpdate( Query );
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean addGroup( final String fac, final String level, final String discp ){
        try{
            final String Query = "insert into "+super.ExamDivisionTable+"("+super.FacultyField+","+super.LevelField+","+super.DisciplineField+") values ('"+fac+"','"+level+"','"+discp+"');";
            super.Statement.executeUpdate( Query );
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean updateBackCourse( final String Course, final String College, final String Program, final String BackCandID, final String NewCourse ){
        try{
            final String Query = "update "+super.BackTable+" set "+super.CourseCodeField+"='"+NewCourse+"' where "+super.CourseCodeField+"='"+Course+"' AND "+super.College+"='"+College+"' AND "+super.ProgramField+"='"+Program+"' AND "+super.CandidateIDField+"='"+BackCandID+"';";
            super.Statement.executeUpdate( Query );
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
        
    public boolean removeBackCandidate( final String cand ){
        try{
            final String Query = "delete from "+super.BackTable+" where "+super.CandidateIDField+"='"+cand+"';";
            super.Statement.executeUpdate( Query );
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean updateBackCandidate( final String oldCand, final String newCand ){
        try{
            final String Query = "update "+super.BackTable+" set "+ super.CandidateIDField+"='"+newCand+"' where "+super.CandidateIDField+"='"+oldCand+"';";
            super.Statement.executeUpdate( Query );
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean updateBackCandidateSemester( final String Cand, final int oldSem, final int newSem ){
        try{
            final String Query = "update "+super.BackTable+" set "+ super.SemesterFieldName+"="+newSem+" where "+super.CandidateIDField+"='"+Cand+"';";
            super.Statement.executeUpdate( Query );
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
       
    public boolean removeCollegeProgram( final String College, final String prg ){
        boolean state = false;
        try{
            super.Conn.setAutoCommit( false );
            String Query = "delete from "+ super.CollegeCentreTable+" where "+super.College+"='"+College+"' AND "+super.ProgramField+"='"+prg+"';";
            super.Statement.executeUpdate( Query );
            Query = "delete from "+ super.RegularCourseTableName+" where "+super.College+"='"+College+"' AND "+super.ProgramField+"='"+prg+"';";
            super.Statement.executeUpdate( Query );
            Query = "delete from "+ super.BackTable+" where "+super.College+"='"+College+"' AND "+super.ProgramField+"='"+prg+"';";
            super.Statement.executeUpdate( Query );
            super.Conn.commit();
            state = true;
        }
        catch( Exception e ){
            super.Conn.rollback();
            state = false;
        }
        finally{
            try{
                super.Conn.setAutoCommit( true );
                return state;
            }
            catch( Exception e ){
                return false;
            }
        }
    }
    
    public boolean removeCollege( final String College ){
        boolean state = false;
        try{
            super.Conn.setAutoCommit( false );
            String Query = "delete from "+ super.CollegeCentreTable+" where "+super.College+"='"+College+"';";
            super.Statement.executeUpdate( Query );
            Query = "delete from "+ super.RegularCourseTableName+" where "+super.College+"='"+College+"';";
            super.Statement.executeUpdate( Query );
            Query = "delete from "+ super.BackTable+" where "+super.College+"='"+College+"';";
            super.Statement.executeUpdate( Query );
            super.Conn.commit();
            state = true;
        }
        catch( Exception e ){
            super.Conn.rollback();
            state = false;
        }
        finally{
            try{
                super.Conn.setAutoCommit( true );
                return state;
            }
            catch( Exception e ){
                return false;
            }
        }
    }
    
    public boolean removeProgram( final String prg ,final String fac, final String level, final String discp ){
        boolean state = false;
        try{
            super.Conn.setAutoCommit( false );
            deleteProgram( prg, fac, level, discp );            
            super.Conn.commit();
            state = true;
        }
        catch( Exception e ){
            super.Conn.rollback();
            state = false;
        }
        finally{
            try{
                super.Conn.setAutoCommit( true );
                return state;
            }
            catch( Exception e ){
                return false;
            }
        }
    }
    
    private void deleteProgram(final String prg ,final String fac, final String level, final String discp ) throws SQLException {
        String Query = "delete from " + super.CollegeCentreTable + " where " + super.ProgramField + "='" + prg + "';";
        super.Statement.executeUpdate(Query);
        Query = "delete from " + super.RegularCourseTableName + " where " + super.ProgramField + "='" + prg + "';";
        super.Statement.executeUpdate(Query);
        Query = "delete from " + super.BackTable + " where " + super.ProgramField + "='" + prg + "';";
        super.Statement.executeUpdate(Query);
        Query = "delete from " + super.ProgramTableName + " where " + super.ProgramField + "='" + prg +"' AND "+super.FacultyField+"='"+fac+"' AND "+super.LevelField+"='"+level+"' AND "+super.DisciplineField+"='"+discp + "';";
        super.Statement.executeUpdate(Query);
    }
    
    public boolean updateProgram( final String oldProgram, final String Faculty, final String level, final String discp, final String newProgram ){
        boolean state = false;
        try{
            super.Conn.setAutoCommit( false );
            String Query = "update "+ super.CollegeCentreTable+" set "+super.ProgramField+"='"+newProgram+"' where "+super.ProgramField+"='"+oldProgram+"';";
            super.Statement.executeUpdate( Query );
            Query = "update "+ super.RegularCourseTableName+" set "+super.ProgramField+"='"+newProgram+"' where "+super.ProgramField+"='"+oldProgram+"';";
            super.Statement.executeUpdate( Query );
            Query = "update "+ super.BackTable+" set "+super.ProgramField+"='"+newProgram+"' where "+super.ProgramField+"='"+oldProgram+"';";
            super.Statement.executeUpdate( Query );
            Query = "update "+ super.ProgramTableName+" set "+super.ProgramField+"='"+newProgram+"' where "+super.ProgramField+"='"+oldProgram+"' AND "+super.FacultyField+"='"+Faculty+"' AND "+super.LevelField+"='"+level+"' AND "+super.DisciplineField+"='"+discp+"';";
            super.Statement.executeUpdate( Query );            
            super.Conn.commit();
            state = true;
        }
        catch( Exception e ){
            super.Conn.rollback();
            state = false;
        }
        finally{
            try{
                super.Conn.setAutoCommit( true );
                return state;
            }
            catch( Exception e ){
                return false;
            }
        }
    }
    
    public boolean updateProgramGroup( final String Program, final String Faculty, final String level, final String discp ){
        try{
            String Query = "update "+super.ProgramTableName+" set "+super.FacultyField+"='"+Faculty+"' , "+super.LevelField+"='"+level+"' , "+super.DisciplineField+"='"+discp+"' where "+super.ProgramField+"='"+Program+"';";
            super.Statement.executeUpdate( Query );
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean updateDiscipline( final String Faculty, final String level, final String olddiscp, final String newdiscp ){
        boolean state = false;
        try{
            super.Conn.setAutoCommit( false );
            String Query = "update "+ super.ProgramTableName+" set "+super.DisciplineField+"='"+newdiscp+"' where "+super.FacultyField+"='"+Faculty+"' AND "+super.LevelField+"='"+level+"' AND "+super.DisciplineField+"='"+olddiscp+"';";
            super.Statement.executeUpdate( Query );            
            Query = "update "+ super.ExamDivisionTable+" set "+super.DisciplineField+"='"+newdiscp+"' where "+super.FacultyField+"='"+Faculty+"' AND "+super.LevelField+"='"+level+"' AND "+super.DisciplineField+"='"+olddiscp+"';";
            super.Statement.executeUpdate( Query );            
            super.Conn.commit();
            state = true;
        }
        catch( Exception e ){
            super.Conn.rollback();
            state = false;
        }
        finally{
            try{
                super.Conn.setAutoCommit( true );
                return state;
            }
            catch( Exception e ){
                return false;
            }
        }
    }
    
    public boolean updateLevel( final String Faculty, final String oldlevel, final String newlevel ){
        boolean state = false;
        try{
            super.Conn.setAutoCommit( false );
            String Query = "update "+ super.ProgramTableName+" set "+super.LevelField+"='"+newlevel+"' where "+super.FacultyField+"='"+Faculty+"' AND "+super.LevelField+"='"+oldlevel+"';";
            super.Statement.executeUpdate( Query );            
            Query = "update "+ super.ExamDivisionTable+" set "+super.LevelField+"='"+newlevel+"' where "+super.FacultyField+"='"+Faculty+"' AND "+super.LevelField+"='"+oldlevel+"';";
            super.Statement.executeUpdate( Query );            
            super.Conn.commit();
            state = true;
        }
        catch( Exception e ){
            super.Conn.rollback();
            state = false;
        }
        finally{
            try{
                super.Conn.setAutoCommit( true );
                return state;
            }
            catch( Exception e ){
                return false;
            }
        }
    }
    
    public boolean updateFaculty( final String oldFaculty, final String newFac ){
        boolean state = false;
        try{
            super.Conn.setAutoCommit( false );
            String Query = "update "+ super.ProgramTableName+" set "+super.FacultyField+"='"+newFac+"' where "+super.FacultyField+"='"+oldFaculty+"';";
            super.Statement.executeUpdate( Query );            
            Query = "update "+ super.ExamDivisionTable+" set "+super.FacultyField+"='"+newFac+"' where "+super.FacultyField+"='"+oldFaculty+"';";
            super.Statement.executeUpdate( Query );            
            super.Conn.commit();
            state = true;
        }
        catch( Exception e ){
            super.Conn.rollback();
            state = false;
        }
        finally{
            try{
                super.Conn.setAutoCommit( true );
                return state;
            }
            catch( Exception e ){
                return false;
            }
        }
    }
    
    public boolean deleteDiscipline( final String fac, final String level, final String discipline ){
        boolean state = false;
        try{
            super.Conn.setAutoCommit( false );
            removeDiscipline(discipline, fac, level);            
            super.Conn.commit();
            state = true;
        }
        catch( Exception e ){
            super.Conn.rollback();
            state = false;
        }
        finally{
            try{
                super.Conn.setAutoCommit( true );
                return state;
            }
            catch( Exception e ){
                return false;
            }
        }
    }
    
    private void removeDiscipline(final String discipline, final String fac, final String level) throws SQLException{

        String[] prgs = super.readPrograms(fac, level, discipline);
        for (int i = 0; i < prgs.length; i++) {
            this.deleteProgram(prgs[i], fac, level, discipline);
        }
        
        String Query = "delete from " + super.ProgramTableName + " where " + super.FacultyField + "='" + fac + "' AND " + super.LevelField + "='" + level + "' AND " + super.DisciplineField + "='" + discipline + "';";
        super.Statement.executeUpdate(Query);
        Query = "delete from " + super.ExamDivisionTable + " where " + super.FacultyField + "='" + fac + "' AND " + super.LevelField + "='" + level + "' AND " + super.DisciplineField + "='" + discipline + "';";
        super.Statement.executeUpdate(Query);
        
    }
    
    public boolean removeLevel( final String fac, final String level ){
        boolean state = false;
        try{
            super.Conn.setAutoCommit( false );
            deleteLevel(level, fac);
            super.Conn.commit();
            state = true;
        }
        catch( Exception e ){
            super.Conn.rollback();
            state = false;
        }
        finally{
            try{
                super.Conn.setAutoCommit( true );
                return state;
            }
            catch( Exception e ){
                return false;
            }
        }
    }
    
    private void deleteLevel(final String level, final String fac) throws SQLException {
        String[] discps = super.readDisciplines(fac, level);
        for (int i = 0; i < discps.length; i++) {
            this.removeDiscipline(discps[i], fac, level);
        }
    }
    
    public boolean deleteFaculty( final String fac ){
        boolean state = false;
        try{
            super.Conn.setAutoCommit( false );
            removeFaculty(fac);
            super.Conn.commit();
            state = true;
        }
        catch( Exception e ){
            super.Conn.rollback();
            state = false;
        }
        finally{
            try{
                super.Conn.setAutoCommit( true );
                return state;
            }
            catch( Exception e ){
                return false;
            }
        }
    }
    
    public boolean removeAllFaculties(){
        boolean state = false;
        try{
            super.Conn.setAutoCommit( false );
            String[] facs = super.readFaculties();
            for( int i=0; i<facs.length; i++ )
                this.removeFaculty( facs[i] );
            super.Conn.commit();
            state = true;
        }
        catch( Exception e ){
            super.Conn.rollback();
            state = false;
        }
        finally{
            try{
                super.Conn.setAutoCommit( true );
                return state;
            }
            catch( Exception e ){
                return false;
            }
        }
    }
    
    public boolean addCentre( final String Centre, final String Limit ){
        try{
            String Query = "insert into "+super.ExamCentreTable+"("+super.ExamCentreField+","+this.ExamCentreLimitField+") values ('"+Centre+"','"+Limit+"');";
            super.Statement.executeUpdate(Query);
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean updateCentreName( final String oldval, final String newval ){
        boolean state = false;
        try{
            super.Conn.setAutoCommit( false );
            String Query = "update "+super.ExamCentreTable+" set "+super.ExamCentreField+"='"+newval+"' where "+super.ExamCentreField+"='"+oldval+"';";
            super.Statement.executeUpdate( Query );
            Query = "update "+super.CollegeCentreTable+" set "+super.ExamCentreField+"='"+newval+"' where "+super.ExamCentreField+"='"+oldval+"';";
            super.Statement.executeUpdate( Query );
            super.Conn.commit();
            state = true;
        }
        catch( Exception e ){
            super.Conn.rollback();
            state = false;
        }
        finally{
            try{
                super.Conn.setAutoCommit( true );
                return state;
            }
            catch( Exception e ){
                return false;
            }
        }
    }
    
    public boolean updateCentreLimit( final String centre, final String newval ){
        boolean state = false;
        try{
            super.Conn.setAutoCommit( false );
            String Query = "update "+super.ExamCentreTable+" set "+super.ExamCentreLimitField+"='"+newval+"' where "+super.ExamCentreField+"='"+centre+"';";
            super.Statement.executeUpdate( Query );
            super.Conn.commit();
            state = true;
        }
        catch( Exception e ){
            super.Conn.rollback();
            state = false;
        }
        finally{
            try{
                super.Conn.setAutoCommit( true );
                return state;
            }
            catch( Exception e ){
                return false;
            }
        }
    }
    
    public boolean addCourse( String courseCode, String courseName, final String fac, final String level, final String discipline ){
        try{
            String Query = "insert into "+super.CourseTable+"("+super.CourseCodeField+","+super.CourseCodeNameField+","+super.FacultyField+","+super.LevelField+","+super.DisciplineField+") values ('"+courseCode+"','"+courseName+"','"+fac+"','"+level+"','"+discipline+"');";
            super.Statement.executeUpdate(Query);
            return true;
        }
        catch( Exception e ){
            return false;
        }        
    }
    
    public boolean updateCourseName( String courseCode, String newCourseName){
        try{
            String Query = "update "+super.CourseTable+" set "+super.CourseCodeNameField+"='"+newCourseName+"' where "+ super.CourseCodeField+"='"+courseCode+"';";
            super.Statement.executeUpdate(Query);
            return true;
        }
        catch( Exception e ){
            return false;
        }        
    }
    
    public boolean deleteCourse( String CourseCode ){
        try{
            String Query = "delete from "+super.CourseTable+" where "+ super.CourseCodeField+"='"+CourseCode+"';";
            super.Statement.executeUpdate(Query);
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean setAutoCommit(){
        try{
            if( ! super.Conn.getAutoCommit() )
                super.Conn.setAutoCommit( true );
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean removeAutoCommit(){
        try{
            if( super.Conn.getAutoCommit() )
                super.Conn.setAutoCommit( false );
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean commit(){
        try{
            super.Conn.commit();
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean rollback(){
        try{
            super.Conn.rollback();
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
        
}