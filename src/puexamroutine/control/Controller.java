
//                          !! RAM !!

package puexamroutine.control;

import puexamroutine.control.routinegeneration.*;
import puexamroutine.control.interfaces.*;
import puexamroutine.control.schedule.Result;
import puexamroutine.control.domain.*;
import puexamroutine.control.domain.interfaces.*;
import puexamroutine.control.domain.list.*;
import javax.swing.JOptionPane;
import java.util.Iterator;

/**
 *
 * @author Sumit Shresth
 */
public class Controller {
    
    /*
     * This is essential state for debugging
     */
    final private boolean DEBUG = false;
    
    public Controller( final String DatabaseURL, final String DriverName, final String User, char[] passd, final String CourseCodeName, final String ProgramField, final String RegularCourseTableName, final String CourseTable, final String SemesterFieldName, final String DatabaseName, final String BackTable, final String CandidateIDField, final String PrgTab, final String FacField, final String LevelField, final int Min, final int Max ) throws Exception{
        this.Connecter = new puexamroutine.control.database.DatabaseConnector( DriverName, DatabaseURL );
        if( this.Connecter.connect(User, passd)){
            if( DEBUG ){
                System.out.println( "\n\t\tNote\nSystem is in Debugging mode \n\tSo, database is not in autocommit mode" );
                this.Connecter.getConnection().setAutoCommit( false );
            }
            read(CourseCodeName, ProgramField, RegularCourseTableName, CourseTable, SemesterFieldName, DatabaseName, BackTable, CandidateIDField, PrgTab, FacField, LevelField, Max, Min);
        }        
        else throw new Exception("Connection failed");            
    }

    private boolean conclude() {
        this.AutoCommitLock = false;
        if (!this.Writer.commit()) {
            return false;
        }
        if (!this.Writer.setAutoCommit()) {
            return false;
        }
        return true;
    }

    /**
     * This method checks and deals with datbaase errors.
     * If error has obtained then the transaction must be halted by returning true.
     * Also, if autocommit lock is reset to false.
     * The system returns to autocommit mode.
     * 
     * Note: Autocommit lock is reset by the method who has made the lock. But, This is an exception to rule.
     * This is because on error the transaction does not continues normally but returns by rollbacking previous changes made.
     * So, Any method that makes changes on database during the datnbase writing process has the right to do it.
     * 
     * @param st database process state
     * @return true if error was present else false
     */
    private boolean onError(boolean st) {
        if (!st) {
            this.Writer.rollback();
            this.AutoCommitLock = false;
            this.Writer.setAutoCommit();
            return true;
        }
        return false;
    }

    /**
     * This method is used to read data from database once connection is made.
     * 
     * @param CourseCodeName
     * @param ProgramField
     * @param RegularCourseTableName
     * @param CourseTable
     * @param SemesterFieldName
     * @param DatabaseName
     * @param BackTable
     * @param CandidateIDField
     * @param PrgTab
     * @param FacField
     * @param LevelField
     * @param Max
     * @param Min
     * 
     * @throws java.lang.Exception
     */
    private void read(final String CourseCodeName, final String ProgramField, final String RegularCourseTableName, final String CourseTable, final String SemesterFieldName, final String DatabaseName, final String BackTable, final String CandidateIDField, final String PrgTab, final String FacField, final String LevelField, final int Max, final int Min) throws Exception {
        this.Writer = new puexamroutine.control.database.DatabaseWriter(CourseCodeName, ProgramField, RegularCourseTableName, CourseTable, SemesterFieldName, DatabaseName, BackTable, CandidateIDField, PrgTab, FacField, LevelField);
        this.Writer.setConnection(this.Connecter.getConnection());
        if( this.Data == null ){
            this.Data = new puexamroutine.control.database.DataHolder();
            this.Max_Gap = Max;
            this.Min_Gap = Min;
        }
        //try{
            this.readData = this.Data.read(Writer);        
        //}
        /*catch( Exception e ){
            
        }*/
    }
    
    public boolean reRead(final String DatabaseURL, final String DriverName, final String User, char[] passd, final String CourseCodeName, final String ProgramField, final String RegularCourseTableName, final String CourseTable, final String SemesterFieldName, final String DatabaseName, final String BackTable, final String CandidateIDField, final String PrgTab, final String FacField, final String LevelField){
        try{
            this.Connecter = new puexamroutine.control.database.DatabaseConnector( DriverName, DatabaseURL );
            if( this.Connecter.connect(User, passd)){
                this.read(CourseCodeName, ProgramField, RegularCourseTableName, CourseTable, SemesterFieldName, DatabaseName, BackTable, CandidateIDField, PrgTab, FacField, LevelField, Max_Gap, Min_Gap);
            }
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean reRead(){
        try{
            this.readData = this.Data.read(Writer);
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public void setMinimumGap( int Min ){
        this.Min_Gap = Min;
    }
    
    public void setMaxGap( int Max ){
        this.Max_Gap = Max;
    }
    
    public void setListener( DomainListener list ){        
        this.RoutineGenerator.setListener(list);        
        this.RemoteScheduler.initialize(list);
    }
    
    public void setRemoteSchedulerIP( final String IP ){
        this.RemoteSchedulerIP = IP;
    }
    
    public void setRemoteSchedulerPort( final int port ){
        this.RemoteSchedulerPort = port;
    }
    
    public Result start( boolean remote ){                 
        if( remote ){
            return this.RemoteScheduler.scheduleRemoteRoutine(RemoteSchedulerIP, this.RemoteSchedulerPort , this.readData, Min_Gap, Max_Gap);
        }
        else{
            boolean state = this.RoutineGenerator.generate( this.readData,Min_Gap, Max_Gap);
            if (!state) {
                return new Result(false, null, null, null,"scheduling could not be performed"  );
            } else {
                return new Result(state, RoutineGenerator.getCalander(), RoutineGenerator.getGroupProgramRoutine(), RoutineGenerator.getUnScheduledGroup(),null  );
            }
        }
    }
    
    public java.sql.Connection getConnection(){
        return this.Connecter.getConnection();
    }
    
    public puexamroutine.control.domain.list.GroupList getGroupList(){
        if( this.readData != null )
            return this.readData.getGroupList();
        else
            return null;
    }
        
    public puexamroutine.control.database.DatabaseWriter getDatabaseManager(){
        return this.Writer;
    }
    
    public puexamroutine.control.domain.list.CentreTable getCentres(){
        if( this.readData != null )
            return this.readData.getCentreList();
        else
            return null;
    }
    
    // database works
    public boolean addRegularSemesters(final String College, final String Program, SemesterList list){
        boolean did_I_set_AutoCommit = false;
        if( ! this.AutoCommitLock ){
            this.AutoCommitLock = true;
            did_I_set_AutoCommit = true;
            if( ! this.Writer.removeAutoCommit() )return false;
        }        
        Iterator<Semester> semItr = list.getSemesters().iterator();
        while (semItr.hasNext()) {
            RegularCourses reg = semItr.next().getRegularCourses();
            Iterator<CourseCode> codeItr = reg.getCourses().iterator();
            while (codeItr.hasNext()) {
                CourseCode course = codeItr.next();
                if (!this.Writer.addRegularCourse(course.toString(), College, Program, reg.getSemester().getSemester(), reg.getTotalStudents(course))) {                    
                    JOptionPane.showMessageDialog(null, course.toString() + " cannot be added", "Error in Course Addition", JOptionPane.ERROR_MESSAGE);
                    if( onError( false ) )return false;                
                }
            }
        }
        return did_I_set_AutoCommit?this.conclude() : true;
    }
    
    public boolean addBackCandidateCourses(final String College, final String Program, final String BkCand,int Sem, CourseCode[] courseItr){
        boolean did_I_set_AutoCommit = false;
        if( ! this.AutoCommitLock ){
            this.AutoCommitLock = true;
            did_I_set_AutoCommit = true;
            if( ! this.Writer.removeAutoCommit() )return false;
        }        
        boolean state = true;
        for (int i = 0; i < courseItr.length; i++) {
            CourseCode c = courseItr[i];
            if (!this.Writer.addBackCandidate(BkCand, c.toString(), Program, College,Sem)) {
                state = false;
                if( onError(state) )return false;                                
            }
        }
        return state && did_I_set_AutoCommit?this.conclude() : true;
    }
    
    public boolean addCollegeBackCandidates(final String Program, final String College, CollegeBackCandidates BkCands){
        boolean did_I_set_AutoCommit = false;
        if( ! this.AutoCommitLock ){
            this.AutoCommitLock = true;
            did_I_set_AutoCommit = true;
            if( ! this.Writer.removeAutoCommit() )return false;
        }        
        Iterator<CandidateInterface> candItr = BkCands.getBackPaperCandidates().iterator();
        while (candItr.hasNext()) {
            CandidateInterface cand = candItr.next();
            final String BkCand = cand.getCandidateID();
            CourseCode[] courseItr = cand.getBackCourses().toArray( new CourseCode[]{} );
            boolean st = addBackCandidateCourses(College, Program, BkCand,cand.getSemester(), courseItr);
            if( onError(st) )return false;                
        }
        return did_I_set_AutoCommit?this.conclude() : true;
    }
    
    public boolean addCollege( Program prg, College college){
        boolean did_I_set_AutoCommit = false;
        if( ! this.AutoCommitLock ){
            this.AutoCommitLock = true;
            did_I_set_AutoCommit = true;
            if( ! this.Writer.removeAutoCommit() )return false;
        }        
        this.Writer.addCollegeCentre(college.getCollegeName(), college.getCentre(prg).getCentreName(), prg.getProgramName());
        boolean st = this.addRegularSemesters(college.getCollegeName(), prg.getProgramName(), college.getSemesters(prg));
        if( onError(st) )return false;                
        st = this.addCollegeBackCandidates(prg.getProgramName(), college.getCollegeName(), college.getBackCandidates(prg));
        if( onError(st) )return false;                
        return did_I_set_AutoCommit?this.conclude() : true;
    }
    
    public boolean addProgramColleges( Program prg ){
        boolean did_I_set_AutoCommit = false;
        if( ! this.AutoCommitLock ){
            this.AutoCommitLock = true;
            did_I_set_AutoCommit = true;
            if( ! this.Writer.removeAutoCommit() )return false;
        }        
        Iterator<College> collItr = prg.getSupportingColleges().iterator();
        while( collItr.hasNext() ){
            boolean st = this.addCollege(prg, collItr.next() );
            if( onError(st) )return false;                
        }
        return did_I_set_AutoCommit?this.conclude() : true;
    }
    
    public boolean addProgram( Program prg ){
        boolean did_I_set_AutoCommit = false;
        if( ! this.AutoCommitLock ){
            this.AutoCommitLock = true;
            did_I_set_AutoCommit = true;
            if( ! this.Writer.removeAutoCommit() )return false;
        }        
        Group grp = prg.getGroup();
        boolean st = this.Writer.addProgram( prg.getProgramName(), grp.getFaculty(), grp.getLevel(), grp.getDiscipline() );
        if( onError(st) )return false;                
        st = this.addProgramColleges(prg);
        if( onError(st) )return false;
        return did_I_set_AutoCommit?this.conclude() : true;
    }
    
    private puexamroutine.control.database.DatabaseConnector Connecter;
    private puexamroutine.control.database.DatabaseWriter Writer;    
    //data
    private puexamroutine.control.database.DataHolder Data=null;
    private int Min_Gap, Max_Gap;            
    private String RemoteSchedulerIP;
    private int RemoteSchedulerPort;
    private RoutineGenerator RoutineGenerator = new RoutineGenerator();
    private RemoteController RemoteScheduler = new RemoteController();   
    private puexamroutine.control.domain.list.UniversityDataBean readData;
    
    private boolean AutoCommitLock;
}