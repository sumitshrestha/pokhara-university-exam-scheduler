
//                              !! RAM !!

package puexamroutine.control.routinegeneration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution;
import puexamroutine.control.domain.list.IndependentRegularCourseList;
import puexamroutine.control.domain.list.IndependentCourseList;
import puexamroutine.control.domain.*;
import puexamroutine.control.interfaces.*;
import puexamroutine.control.routinegeneration.graph.domain.interfaces.User;

/**
 * This class is used to group the courses of a group
 * 
 * @author Sumit Shresth
 */
public class Grouper implements User{

    private static final Logger LOGGER = LoggerFactory.getLogger(Grouper.class);
    
    private boolean DEBUG = false;

    public Grouper( int min, int max, DomainListener user ) throws Exception{
        this.user = user;
        this.GraphColorer = new puexamroutine.control.routinegeneration.graph.colorcontrol.GraphColorerOptimize1( this );
        this.CoursesGroupAnalyzer = new puexamroutine.control.routinegeneration.IndependentRegularCourseListAnalyzer( );    
        if( max < min ) throw new Exception("maximum gap is less than minimum gap...");
        this.min = min;
        this.max = max;
    }
    
    /**
     * This method sets the grouplist getting list of the courses
     * 
     * @param Groups The group list to be set
     */
    public void setGroupList( puexamroutine.control.domain.list.GroupList Groups ){
        this.Groups = Groups;
    }
    
    /**
     * This method is used to group the reular courses from the specified group such that each group has none of the two courses that belong to one set regular courses.
     * 
     * @param grp
     */
    public puexamroutine.control.domain.list.IndependentRegularCourseList groupRegularCourses( puexamroutine.control.domain.Group grp ){
       try{
           java.util.HashSet< puexamroutine.control.domain.RegularCourses > RegularCoursesSet = this.Groups.getRegularCourses(grp);           
           java.util.Collection<CourseCode> BkPapers = this.Groups.getBackPapers(grp);
           if( RegularCoursesSet.size() < 1 ){ // no regular course
               return new puexamroutine.control.domain.list.IndependentRegularCourseList(grp);
           }
           puexamroutine.control.domain.list.GroupCourseList RegularCourses = this.Groups.getCourseList(grp);          
           java.util.HashSet<CourseCode> AllCourses = new java.util.HashSet<CourseCode>();
           AllCourses.addAll( RegularCourses.getRegularCourses() );
           AllCourses.addAll( BkPapers );
           java.util.Collection< puexamroutine.control.domain.interfaces.CandidateInterface > BkCandidates = this.Groups.getBackCandidates( grp );           
           java.util.Collection<puexamroutine.control.domain.interfaces.DependentCourses> Dep = java.util.Arrays.asList( BkCandidates.toArray( new puexamroutine.control.domain.interfaces.DependentCourses[]{} ) );
           Dep = new java.util.HashSet<puexamroutine.control.domain.interfaces.DependentCourses>(Dep);
           Dep.addAll( RegularCoursesSet );
           if(this.DEBUG){
               LOGGER.debug("Total dependent courses are {}", Dep.size());
               java.util.Iterator<CourseCode> CItr = AllCourses.iterator();
               StringBuilder sb = new StringBuilder();
               while( CItr.hasNext() ){
                   sb.append(CItr.next().toString()).append(", ");
               }
               LOGGER.debug("All courses: {}", sb);
           }
           puexamroutine.control.routinegeneration.graph.domain.interfaces.KeyedAdjacancyMatrix RegularCourseGraph = this.GraphFact.getGraph( AllCourses, Dep );           
           this.CoursesGroupAnalyzer.intiialize( AllCourses.toArray( new CourseCode[]{} ), RegularCoursesSet, this.min, this.max , this.user , 1 );
           this.GraphColorer.initialize( RegularCourseGraph, this.CoursesGroupAnalyzer );
           if(this.DEBUG)LOGGER.debug("{}:{}:{} started coloring", grp.getFaculty(), grp.getLevel(), grp.getDiscipline());
           this.GraphColorer.color();
           if( this.user.isCancelled() || this.CoursesGroupAnalyzer.didTimeUp() ) return null;
           //System.out.println( grp.getFaculty()+":"+grp.getDiscipline()+" finished coloring...");
            puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution OptSoln = this.GraphColorer.getOptimumSolution();
           IndependentRegularCourseList temp = new IndependentRegularCourseList( grp );
           IndependentCourseCreator.createListOfSameColoredCourseCodes(OptSoln, AllCourses.toArray( new CourseCode[]{} ), temp, this.DEBUG );
           return temp;
       } 
       catch( Exception e ){
           if(this.DEBUG)LOGGER.debug("Error while grouping regular courses", e);
           return null;
       }
    }
    
    public IndependentCourseList groupBackCourses( puexamroutine.control.domain.Group grp ){
        java.util.Collection< CourseCode> BkPapers = this.Groups.getBackPapers(grp);
        if( BkPapers.size() < 1){// i.e. no back papers
            return new puexamroutine.control.domain.list.IndependentRegularCourseList(grp);
        }
        java.util.Collection< puexamroutine.control.domain.interfaces.CandidateInterface > BkCandidates = this.Groups.getBackCandidates( grp );
        java.util.Collection<puexamroutine.control.domain.interfaces.DependentCourses> Dep = java.util.Arrays.asList( BkCandidates.toArray( new puexamroutine.control.domain.interfaces.DependentCourses[]{} ) );
        puexamroutine.control.routinegeneration.graph.domain.interfaces.KeyedAdjacancyMatrix BkGraph = this.GraphFact.getGraph(BkPapers, Dep );        
        this.GraphColorer.initialize(BkGraph, this.BackPaperGrouperAnalyzer );
        this.GraphColorer.color();
        if( this.user.isCancelled() ) return null;
        puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution OptSoln = this.GraphColorer.getOptimumSolution();
        IndependentCourseList temp = new IndependentCourseList( grp );
        IndependentCourseCreator.createListOfSameColoredCourseCodes( OptSoln, BkPapers.toArray( new CourseCode[]{} ), temp,false);
        return temp;
    }
    
    public boolean requestCancel(){
        return this.user.isCancelled();
    }
    
    public boolean didTimeUpOccured(){
        return this.CoursesGroupAnalyzer.didTimeUp();
    }
    
    private puexamroutine.control.domain.list.GroupList Groups;
    private int min, max;
    private puexamroutine.control.routinegeneration.ExamGraphFactory GraphFact = new puexamroutine.control.routinegeneration.ExamGraphFactory();
    private puexamroutine.control.routinegeneration.graph.colorcontrol.GraphColorer GraphColorer;
    private final puexamroutine.control.routinegeneration.IndependentRegularCourseListAnalyzer CoursesGroupAnalyzer;
    private final DomainListener user;
    private puexamroutine.control.routinegeneration.graph.domain.interfaces.GraphColoringSolutionAnalyzer BackPaperGrouperAnalyzer = new puexamroutine.control.routinegeneration.graph.domain.interfaces.GraphColoringSolutionAnalyzer() {

            public boolean isFeasible(GraphColoringSolution OptGraph) {
                return true;
            }

            /**
             * This method returns true indicating that the first obtained solution of back papers is best and need to halt
             * Thus, The system stops immediately after generating first solution
             */
            public boolean isOptimal(GraphColoringSolution OptGraph) {
                return true;
            }

            public boolean isMoreOptimal(GraphColoringSolution NewGraph, GraphColoringSolution OldGraph) {
                return true;
            }
        };
        
}