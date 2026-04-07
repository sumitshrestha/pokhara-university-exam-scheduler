
//                      !! RAM !!

package puexamroutine.control.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Sumit Shresth
 */
public class DatabaseReader {
    
    public static final String getDefaultCourseCodeField(){
        return "CourseCode";
    }
    
    public static final String getDefaultProgramField(){
        return "Course";
    }
    
    public static final String getDefaultCourseNameField(){
        return "CourseName";
    }
    
    public static final String getDefaultRegularCourseTableName(){
        return "regularcourse";
    }
    
    public static final String getDefaultCourseTable(){
        return "course";
    }
    
    public static final String getDefaultSemesterFieldName(){
        return "Semester";
    }
    
    public static final String getDefaultDatabaseName(){
        return "puroutine";
    }
    
    public static final String getDefaultBackTable(){
        return "backpapercandidate";
    }
    
    public static final String getDefaultCandidateIDField(){
        return "ID";
    }
    
    public static final String getDefaultOutCandidate(){
        return "out";
    }
    
    public static final String getDefaultProgramTableName(){
        return "Program";
    }
    
    public static final String getDefaultFacultyFieldName(){
        return "Faculty";
    }
    
    public static final String getDefaultLevelFieldName(){
        return "Level";
    }
    
    public static final String getDefaultDisciplineField(){
        return "Discipline";
    }
    
    public static final String getDefaultExamDivisionTable(){
        return "examdivision";
    }
    
    public static final String getDefaultExamCentreField(){
        return "Centre";
    }
    
    public static final String getDefaultExamCentreLimitField(){
        return "MaxLimit";
    }
    
    public static final String getDefaultExamCentreTable(){
        return "centre";
    }
    
    public static final String getDefaultCollegeField(){
        return "College";
    }
    
    public static final String getDefaultTotalCandidateField(){
        return "TotalCandidates";
    }
    
    public static final String getDefaultCollegeCentreTable(){
        return "collegescentre";
    }
    
    public DatabaseReader(){   
        CourseCodeField= getDefaultCourseCodeField();
        ProgramField= this.getDefaultProgramField();
        RegularCourseTableName = this.getDefaultRegularCourseTableName();
        CourseTable = this.getDefaultCourseTable();
        SemesterFieldName = this.getDefaultSemesterFieldName();
        DatabaseName = this.getDefaultDatabaseName();
        BackTable = this.getDefaultBackTable();
        CandidateIDField= this.getDefaultCandidateIDField();                
        this.ProgramTableName = this.getDefaultProgramTableName();
        this.LevelField = this.getDefaultLevelFieldName();
        this.FacultyField = this.getDefaultFacultyFieldName();
        this.DisciplineField = this.getDefaultDisciplineField();
        this.ExamDivisionTable = this.getDefaultExamDivisionTable();
        this.ExamCentreField = this.getDefaultExamCentreField();
        this.ExamCentreLimitField = this.getDefaultExamCentreLimitField();
        this.ExamCentreTable = this.getDefaultExamCentreTable();
        this.College = this.getDefaultCollegeField();
        this.TotalCandidateField = this.getDefaultTotalCandidateField();
        this.CollegeCentreTable = this.getDefaultCollegeCentreTable();
        this.CourseCodeNameField = this.getDefaultCourseNameField();
    }
    
    public DatabaseReader( final String CourseCodeName1, final String ProgramField1, final String RegularCourseTableName1, final String CourseTable1, final String SemesterFieldName1, final String DatabaseName1, final String BackTable1, final String CandidateIDField1, final String PrgTab, final String FacField, final String LevelField ,String courseName ){   
        this.CourseCodeField= CourseCodeName1;
        this.ProgramField= ProgramField1;
        this.RegularCourseTableName = RegularCourseTableName1;
        this.CourseTable = CourseTable1;
        this.SemesterFieldName = SemesterFieldName1;
        this.DatabaseName = DatabaseName1;
        this.BackTable = BackTable1;
        this.CandidateIDField= CandidateIDField1;        
        this.ProgramTableName = PrgTab;
        this.LevelField = LevelField;
        this.FacultyField = FacField;
        this.DisciplineField = this.getDefaultDisciplineField();
        this.ExamDivisionTable = this.getDefaultExamDivisionTable();
        this.ExamCentreField = this.getDefaultExamCentreField();
        this.ExamCentreLimitField = this.getDefaultExamCentreLimitField();
        this.ExamCentreTable = this.getDefaultExamCentreTable();
        this.College = this.getDefaultCollegeField();
        this.TotalCandidateField = this.getDefaultTotalCandidateField();
        this.CollegeCentreTable = this.getDefaultCollegeCentreTable();
        this.CourseCodeNameField = courseName;
    }
    
    public void setConnection( java.sql.Connection Conn ) throws Exception{        
        this.Conn = Conn;
        this.Statement = this.Conn.createStatement();        
        if( this.isValid() ){
            this.useDatabase();   
        }        
    }

    private List<String> getColumns( final String Table, final int index ) throws SQLException{
        java.sql.ResultSet rs = this.Conn.getMetaData().getColumns(this.DatabaseName, null, Table, null);
        String[] result = this.getStringArray(rs, index );
        java.util.List<String> Colls = java.util.Arrays.asList(result);
        return Colls;
    }
    
    private boolean isValid() throws Exception{
        if( ! this.doesDatabaseExists() ) throw new Exception("Database Name not appropiate...");
        if( ! this.isTableValid() ) throw new Exception("Tables are not appropiate...");
        if( ! this.isExamDivisionTableValid() ) throw new Exception( " Exam Division table is not valid...");
        if( ! this.isProgramTableValid() ) throw new Exception("Program Table is not valid..." );
        if( ! this.isCollegeCentreTableValid() ) throw new Exception( "Colleges Centre Table is in valid..." );
        if( ! this.isCentreTableValid() ) throw new Exception("Centre table is not valid..." );
        if( ! this.isRegularCourseTableValid() ) throw new Exception( "Regular Course Table is not valid..." );
        if( ! this.isBackCandidateTableValid() ) throw new Exception( "Back Candidates Table is not valid..." );
        if( ! this.isCourseTableValid() ) throw new Exception( "Course Table is not valid..." );
        
        return true;
    }
    
    private boolean isTableValid(){
        try{
            java.sql.ResultSet rs = this.Conn.getMetaData().getTables( this.DatabaseName, null, null, null);
            String[] Tables = this.getStringArray(rs, 3 );
            java.util.List<String> TablesList = java.util.Arrays.asList( Tables );
            String[] DomainTables = {this.CollegeCentreTable,this.CourseTable,this.ExamCentreTable, this.ExamDivisionTable, this.RegularCourseTableName, this.BackTable };
            for( int i=0; i<DomainTables.length; i++ ){
                if( ! ( TablesList.contains( DomainTables[i].toLowerCase() ) || TablesList.contains( DomainTables[i].toUpperCase() ) ) ){
                    return false;
                }
            }
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    protected boolean doesDatabaseExists(){
        try{
            java.sql.ResultSet rs = this.Conn.getMetaData().getCatalogs();
            String[] result = this.getStringArray(rs,1);
            for( int i=0; i<result.length; i++ ){
                if( result[i].equalsIgnoreCase( this.DatabaseName ) ){
                    return true;
                }
            }
            return false;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    protected boolean isExamDivisionTableValid(){
        try{
            List<String> Colls = getColumns( this.ExamDivisionTable, 4 );
            if( Colls.contains( this.FacultyField.toUpperCase() ) && Colls.contains( this.LevelField.toUpperCase() ) && Colls.contains( this.DisciplineField.toUpperCase() ) )
                return true;
            else
                return false;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    protected boolean isProgramTableValid(){
        try{
            List<String> Colls = getColumns( this.ProgramTableName, 4 );
            if( Colls.contains( this.ProgramField.toUpperCase()) && Colls.contains( this.FacultyField.toUpperCase() ) && Colls.contains( this.LevelField.toUpperCase() ) && Colls.contains( this.DisciplineField.toUpperCase() ) ){
                return true;
            }
            else
                return false;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    protected boolean isCollegeCentreTableValid(){
        try{
            List<String> Colls = getColumns( this.CollegeCentreTable, 4 );
            if( Colls.contains( this.College.toUpperCase() ) && Colls.contains( this.ExamCentreField.toUpperCase() ) && Colls.contains( this.ProgramField.toUpperCase() ) )
                return true;
            else
                return false;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    protected boolean isCentreTableValid(){
        try{
            List<String> Colls = getColumns( this.ExamCentreTable, 4 );
            if( Colls.contains( this.ExamCentreField.toUpperCase() ) && Colls.contains( this.ExamCentreLimitField.toUpperCase() ) )
                return true;
            else
                return false;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    protected boolean isRegularCourseTableValid(){
        try{
            List<String> Colls = getColumns( this.RegularCourseTableName, 4 );
            if( Colls.contains( this.CourseCodeField.toUpperCase() ) && Colls.contains(this.SemesterFieldName.toUpperCase() ) && Colls.contains(this.ProgramField.toUpperCase() ) && Colls.contains( this.College.toUpperCase() ) && Colls.contains(this.TotalCandidateField.toUpperCase() ))
                return true;
            else
                return false;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    protected boolean isBackCandidateTableValid(){
        try{
            List<String> Colls = getColumns( this.BackTable , 4 );
            if( Colls.contains( this.CourseCodeField.toUpperCase() ) && Colls.contains(this.CandidateIDField.toUpperCase() ) && Colls.contains(this.College.toUpperCase() ) && Colls.contains(this.ProgramField.toUpperCase() ) && Colls.contains( this.SemesterFieldName.toUpperCase() ) )
                return true;
            else
                return false;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    protected boolean isCourseTableValid(){
        try{
            List<String> Colls = getColumns( this.CourseTable , 4 );
            if( Colls.contains( this.CourseCodeField.toUpperCase() ) && Colls.contains(this.CourseCodeNameField.toUpperCase() ) && Colls.contains(this.FacultyField.toUpperCase() ) && Colls.contains(this.LevelField.toUpperCase() ) && Colls.contains(this.DisciplineField.toUpperCase() ) )
                return true;
            else
                return false;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public boolean useDatabase(){
        try{
            String quey = "use "+ this.DatabaseName;
            return this.Statement.execute(quey);            
        }
        catch( Exception e){
            System.out.println("Database "+this.DatabaseName+"cannot be selected due to " + e.getMessage());
            return false;
        }
    }
    
    /**
     * This method reads the colleges that have the following program.
     * 
     * @param Program the program whose college have to be found
     * @return list of all colleges that support the program
     */
    public String[] readCollegesOfProgram( String Program ){
        try{
            String Query = "select distinct "+this.College+" from "+this.RegularCourseTableName+" where "+this.ProgramField+"=\'"+Program+"\';";
            return executeQuery(Query);
        }
        catch( Exception e ){
            return null;
        }
    }
    
    /**
     * This method reads the college that have 
     * @param College specified college whose programs have to be read
     * @return list of programs for the specified college
     */
    public String[] readProgramsOfCollege( String College ){
        try{
            String Query = "select distinct "+this.ProgramField+" from "+this.RegularCourseTableName+" where "+this.College+"=\'"+College+"\';";
            return executeQuery(Query);
        }
        catch( Exception e ){
            return null;
        }
    }
    
    /**
     * This method reads the programs associated with faculty, level and discipline specified
     * 
     * @param Faculty specified faculty whose programs has to be read
     * @param Level specified level of a faculty whose programs has to be read
     * @param Discipline specified discipline of a faculty's level whose programs has to be read
     * @return list of programs associated with the faculty, level and discipline
     */
    public String[] readPrograms( final String Faculty, final String Level, final String Discipline ){
        try{
            String Query = "select distinct " + this.ProgramField + " from " + this.ProgramTableName + " where " + this.FacultyField + "=\'" + Faculty + "\' AND " + this.LevelField + "=\'" + Level + "\' AND "+ this.DisciplineField+"=\'"+Discipline+"\';";
            return executeQuery(Query);
        }
        catch( Exception e ){
            return null;
        }
    }    
    
    /**
     * This mehod reads the faculties supported in the PU whose exam has to be conducted.
     * 
     * @return list of all faculties whoses exam has to be conducted
     */
    public String[] readFaculties(){
        try{
            String Query = "select distinct " + this.FacultyField + " from "+ this.ExamDivisionTable+ ";";
            return executeQuery(Query);
        }
        catch( Exception e ){
            return null;
        }
    }
    
    /**
     * This method reads the list of level for specified Faculty whose exam has to be conducted.
     * 
     * @param Faculty The faculty whose level has to be found
     * @return list of level for specified Faculty whose exam has to be conducted.
     */
    public String[] readLevels( String Faculty ){
        try{
            String Query = "select distinct " + this.LevelField + " from " + this.ExamDivisionTable + " where " + this.FacultyField + "=\'" + Faculty +"\';";
            return executeQuery(Query);
        }
        catch( Exception e ){
            return null;
        }
    }
    
    /**
     * This method reads the discipline for the specified faculty and level whose exam has to be conducted.
     * 
     * @param Faculty specifed faculty whose disciplines has to be read
     * @param Level specified level of faculty whose disciplines has to be read
     * @return list of disciplines if no error else null
     */
    public String[] readDisciplines( final String Faculty, final String Level ){
        try{
            String Query = "select distinct " + this.DisciplineField + " from " + this.ExamDivisionTable + " where " + this.FacultyField +"=\'"+Faculty+"\' AND "+ this.LevelField +"=\'"+Level+"\';";
            return executeQuery(Query);
        }
        catch( Exception e ){
            return null;
        }
    }
    
    /**
     * This method reads the centres and its limit in a hash map where examination has to be conducted.
     * 
     * @return hash map of the exam centre and its corresponding limit if no error else null
     */
    public java.util.HashMap<String,java.lang.Integer> getCentres(){
        try{
            java.util.HashMap<String,java.lang.Integer> cent = new java.util.HashMap<String,java.lang.Integer>();
            String Query = "select " + this.ExamCentreField+"," + this.ExamCentreLimitField+" from " + this.ExamCentreTable + ";";
            return this.executeQuery(Query, cent);
        }
        catch( Exception e ){
            return null;
        }
    }
    
    /**
     * This method reads regular colleges of PU that are attending the exam.
     * 
     * @return list of regular colleges of PU that are attending the exam.
     */
    public String[] readRegularColleges(){
        try{            
            String Query = "select distinct "+this.College + " from " + this.RegularCourseTableName+";";
            return executeQuery(Query);
        }
        catch( Exception e ){
            return null;
        }
    }
    
    public boolean canRead(){
        try{
            return ! this.Conn.isClosed();
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public java.util.HashMap<String,java.lang.Integer> readCoursesNTotalStudent( final String Prg, final String College, final String Semester ){
        try{
            java.util.HashMap<String,java.lang.Integer> list = new java.util.HashMap<String,java.lang.Integer>();
            String Query = "select distinct "+this.CourseCodeField+","+this.TotalCandidateField+" from "+this.RegularCourseTableName+" where "+this.ProgramField+"=\'"+Prg+"\' AND "+this.College+"=\'"+College+"\' AND "+this.SemesterFieldName +"=\'"+Semester+"\';";
            return executeQuery(Query, list);
        }
        catch( Exception e ){
            return null;
        }
    }
    
    public final String[] readSemesters( final String Program, final String College ){
        try{
            String Query = "select distinct "+this.SemesterFieldName+" from "+this.RegularCourseTableName+" where "+this.ProgramField+"=\'"+Program+"\' AND "+this.College+"=\'"+College+"\';";
            return this.executeQuery(Query);
        }
        catch( Exception e ){
            return null;
        }
    }
    
    public final String readCentre( final String Program, final String College ){
       try{
           String Query = "select "+ this.ExamCentreField + " from "+ this.CollegeCentreTable+" where "+ this.ProgramField+"= '"+Program+"' AND "+this.College+"= '"+College+"';";
           return this.executeQuery(Query)[0];
       } 
       catch( Exception e ){
           return null;
       }
    }
    
    public final String[] readRegularCourses( final String Faculty, final String Level, final String Discipline ){
        try{
            String Query = "select distinct "+this.ProgramField+" from "+this.ProgramTableName+" where "+this.FacultyField+"=\'"+Faculty+"\' AND "+this.LevelField+"=\'"+Level+"\' AND "+this.DisciplineField+"=\'"+Discipline+"\' AND "+this.RegularCourseTableName+"."+this.ProgramField+"="+this.ProgramTableName+"."+this.ProgramField;
            Query = "select distinct "+this.CourseCodeField+" from "+this.RegularCourseTableName+" where EXISTS ("+Query+");";
            
            return this.executeQuery(Query);
        }
        catch( Exception e ){
            return null;
        }
    }
    
    public final String[] readGroupCourses( final String Faculty, final String Level, final String Discipline ){
        try{
            String Query = "select "+this.CourseCodeField+" from "+ this.CourseTable+" where "+this.FacultyField+"='"+Faculty+"' AND "+this.LevelField+"='"+Level+"' AND "+this.DisciplineField+"='"+Discipline+"';";            
            return this.executeQuery(Query);
        }
        catch( Exception e ){
            return null;
        }
    }
    
    public final HashMap<String, Integer> getBackPaperCandidates( final String College, final String Prg ){
        try{
            final String Query = "select distinct "+ this.CandidateIDField+", "+this.SemesterFieldName+" from "+this.BackTable+" where "+ this.College+"='"+College+"' AND "+this.ProgramField+"='"+Prg+"';";
            java.util.HashMap<String,java.lang.Integer> list = new java.util.HashMap<String,java.lang.Integer>();
            return this.executeQuery(Query,list);
        }
        catch( Exception e ){
            return null;
        }
    }
    
    public final String[] getNonRegularBackPapers( final String candidate ,final String college, final String program ){
        try{
            String Query = "select distinct "+this.CourseCodeField+" from "+this.RegularCourseTableName;
            Query = " select distinct " + this.CourseCodeField+ " from "+ this.BackTable+" where " + this.CandidateIDField + "= '"+ candidate+"' AND "+ this.College+"= '"+college+"' AND "+this.ProgramField+"= '"+program+"' AND " + this.BackTable+ "." + this.CourseCodeField+ " NOT IN ("+Query +");";
            
            return this.executeQuery(Query);
        }
        catch( Exception e ){
            return null;
        }
    }
    
    public final String[] getCandidateBackPapers( final String candidate ,final String college, final String program ){
        try{            
            final String Query = " select distinct " + this.CourseCodeField+ " from "+ this.BackTable+" where " + this.CandidateIDField + "= '"+ candidate+"' AND "+ this.College+"= '"+college+"' AND "+this.ProgramField+"= '"+program+"';";
            return this.executeQuery(Query);
        }
        catch( Exception e ){
            return null;
        }
    }
    
    public final String[] getGroupBackPapers( final String Faculty, final String Level, final String Disc ){
        try{
            String Query = "select distinct "+ this.ProgramField+" from "+ this.ProgramTableName + " where "+ this.FacultyField +"= '"+Faculty+"' AND "+ this.LevelField+"='"+Level+"' AND "+ this.DisciplineField+"='"+Disc+"'";
            Query = "select distinct "+ this.CourseCodeField+" from "+this.BackTable + " where "+this.ProgramField+" IN ("+Query +");";
            
            return this.executeQuery(Query);
        }
        catch( Exception e ){
            return null;
        }
    }

    private String[] executeQuery(String Query) throws SQLException, SQLException {        
        java.sql.ResultSet Result = this.Statement.executeQuery(Query);
        return this.getStringArray(Result,1);
    }

    private HashMap<String, Integer> executeQuery(String Query, HashMap<String, Integer> list) throws SQLException, NumberFormatException {
        java.sql.ResultSet Result = this.Statement.executeQuery(Query);
        while (Result.next()) {
            list.put( Result.getString(1).trim().toUpperCase(), java.lang.Integer.parseInt(Result.getString(2)));
        }
        return list;
    }
    
    private final String[] getStringArray(ResultSet Result,final int collIndex) throws SQLException {
        java.util.ArrayList<String> list = new java.util.ArrayList<String>();
        while (Result.next()) {
            list.add( Result.getString( collIndex ).trim().toUpperCase() );
        }
        return list.toArray(new String[]{});
    }
    
    public final java.util.HashMap<String,String> readCourses(){
        try{
            java.util.HashMap<String,String> courseMap = new java.util.HashMap<String,String>();
            String Query = "select "+this.CourseCodeField+","+this.CourseCodeNameField+" from "+this.CourseTable+";";
            java.sql.ResultSet Result = this.Statement.executeQuery(Query);
            while (Result.next()) {
                courseMap.put( Result.getString(1).trim().toUpperCase(), Result.getString(2).trim() );
            }
            return courseMap;
        }
        catch( Exception e ){
            return null;
        }
    }
    
    public final String readCourseName( String CourseCode ){
        try{
            String Query = "select "+this.CourseCodeNameField+" from "+ this.CourseTable+" where "+ this.CourseCodeField+"='"+CourseCode+"';";
            return this.executeQuery( Query )[0];
        }
        catch( Exception e ){
            return null;
        }
    }
    
    protected final String CourseCodeField;
    protected final String CourseCodeNameField;
    protected final String ProgramField;
    protected final String RegularCourseTableName;
    protected final String CourseTable;
    protected final String SemesterFieldName;
    protected final String DatabaseName;
    protected final String BackTable;
    protected final String CandidateIDField;        
    protected final String FacultyField;
    protected final String LevelField;    
    protected final String ProgramTableName;
    protected final String DisciplineField;
    protected final String ExamDivisionTable;
    protected final String ExamCentreField, ExamCentreLimitField;
    protected final String ExamCentreTable;
    protected final String College;
    protected final String TotalCandidateField;
    protected final String CollegeCentreTable;
    
    protected java.sql.Connection Conn;    
    protected java.sql.Statement Statement;
}