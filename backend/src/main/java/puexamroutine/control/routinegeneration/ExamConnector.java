
//                      !! RAM !!

package puexamroutine.control.routinegeneration;

/**
 *
 * @author Sumit Shresth
 */
public class ExamConnector {
    
    public ExamConnector( puexamroutine.control.routinegeneration.graph.domain.interfaces.KeyedAdjacancyMatrix Graph, puexamroutine.control.database.DatabaseReader DbReader ){
        this.Graph = Graph;
        this.DbReader = DbReader;        
    }
    
    public boolean connectRegularCourses(){
        try{
            /*puexamroutine.domain.CandidateProgramInterface[] Prg = this.DbReader.readRegularCandidatePrograms();
            for( int i=0;i<Prg.length;i++ ){
                String[] PrgSem = this.DbReader.readSemestersAppearingForCandidateProgram( Prg[i] );
                for( int j=0;j<PrgSem.length; j++ ){
                    puexamroutine.domain.CourseCodeInterface[] Courses = this.DbReader.readRegularCoursesForProgramSemester(Prg[i], PrgSem[j]);
                    this.connectCourses(Courses);                    
                }            
            }*/
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    /*
    private boolean connectCourses( puexamroutine.control.domain.interfaces.CourseCodeInterface[] Courses ){
        try{            
            for( int row=0;row<Courses.length; row++ ){
                for( int col = row +1; col < Courses.length; col++ ){
                    this.Graph.set( Courses[row].getCourseCode(), Courses[col].getCourseCode(), true );
                }
            }            
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    /**
     * This method connects the back courses in the graph.
     * unlike regular back is completely dependent on candidates.
     * Back candidates can be of two types. 
     * so each must be dealt accordingly.
     * 
     * @return true if back courses have been connected else false
     *//*
    public boolean connectBackCourses(){
        try{
            puexamroutine.control.domain.Candidate[] BkCandidates = this.DbReader.readBackPaperCandidates();
            for( int i=0; i< BkCandidates.length; i++ ){
                this.DbReader.setBackPapers( BkCandidates[i] );
                this.DbReader.setRegularPapers( BkCandidates[i] );
                this.connectCourses( BkCandidates[i].getAllCoursesToAttend() );
            }
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    */
    
    private puexamroutine.control.routinegeneration.graph.domain.interfaces.KeyedAdjacancyMatrix Graph;    
    private puexamroutine.control.database.DatabaseReader DbReader;    
}