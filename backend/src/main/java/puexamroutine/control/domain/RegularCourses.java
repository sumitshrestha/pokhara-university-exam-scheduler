
//                  !! RAM !!

package puexamroutine.control.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sumit Shresth
 */
public class RegularCourses implements puexamroutine.control.domain.interfaces.DependentCourses, java.io.Serializable{

    private static final Logger LOGGER = LoggerFactory.getLogger(RegularCourses.class);
    
    public RegularCourses( puexamroutine.control.domain.College col, puexamroutine.control.domain.Program prg, puexamroutine.control.domain.Semester Sems, java.util.HashMap<puexamroutine.control.domain.CourseCode, java.lang.Integer> pair ){        
        this.College = col;
        this.Prg = prg;
        this.Semester = Sems;
        this.CoursesStudents = pair;
        if( this.DEBUG )
            test();
    }
    
    public final puexamroutine.control.domain.College getCollege(){
        return this.College;
    }
    
    public final puexamroutine.control.domain.Program getProgram(){
        return this.Prg;
    }
    
    public final puexamroutine.control.domain.Semester getSemester(){
        return this.Semester;
    }
    
    public final java.util.Collection<puexamroutine.control.domain.CourseCode> getCourses(){
        return this.CoursesStudents.keySet();
    }
    
    public java.util.Collection<puexamroutine.control.domain.CourseCode> getDependentCourses(){
        return this.getCourses();
    }
    
    public int getTotalStudents( puexamroutine.control.domain.CourseCode code ){
        return this.CoursesStudents.get( code );
    }
    
    /**
     * This method gives the maximum students appearing for this college's semester's regular courses.
     * This is needed in getting upper limit of total candidates.
     * 
     * @return maximum students appearing for this college's semester's regular courses
     */
    public int getMaximumAppearingCands(){
        if( this.MaximumAppearingCands == java.lang.Integer.MIN_VALUE )
            this.calculateMaximumAppearingCands();
        return this.MaximumAppearingCands;
    }
    
    private void calculateMaximumAppearingCands(){        
        java.util.Iterator<java.lang.Integer> itr = this.CoursesStudents.values().iterator();
        while( itr.hasNext() ){
            int i = itr.next();
            if( MaximumAppearingCands < i ){
                MaximumAppearingCands = i;
            }
        }        
    }
    
    public puexamroutine.control.domain.Group getGroup(){
        return this.Prg.getGroup();
    }
    
    /**
     * This method checks if this regular course is physically or logically equal to the specified regular course.
     * Physically means if they share same memory viz is easier job.
     * Logically means bit complex thing. Two regular courses are equal iff they belong to same program of same group sharing same courses within each.
     * 
     * @param Reg specified regular course to be checked with
     * @return true if equal else false
     */
    public boolean equals( puexamroutine.control.domain.RegularCourses Reg ){
        if( this == Reg ){
            return true;
        }
        return checkStrongly( this, Reg);
    }
    
    /**
     * This method is for logical equivalence of two specified regular courses.
     * This method first checks if the total courses within each of the two specified regular courses is equal.
     * This is done to check two regular course have same number of courses. If not then they are not equal
     * Then, It checks if course within one regualar course is contained in courses of other regular courses.
     * 
     * @param Reg1 specified first operand regular course to check 
     * @param Reg2 specified second operand regular course to check
     * @return true if two regular course are equal else false
     */
    public static boolean checkStrongly( RegularCourses Reg1, RegularCourses Reg2 ) {        
        if( Reg1.getCourses().size() != Reg2.getCourses().size() ){
            return false;
        }
        
        return Reg1.getCourses().containsAll( Reg2.getCourses() );
    }
    
    public void test(){
        java.util.Iterator<puexamroutine.control.domain.CourseCode> c = this.CoursesStudents.keySet().iterator();
        while( c.hasNext() ){
            puexamroutine.control.domain.CourseCode ct = c.next();
            if( ct == null ){
                LOGGER.warn("course code null for {}:{}:{}", this.College.toString(), this.Prg.getProgramName(), this.Semester.getSemester());
            }
            else
                LOGGER.debug("{}", ct.toString());
        }
    }
    
    private final puexamroutine.control.domain.College College;
    private final puexamroutine.control.domain.Program Prg;    
    private final java.util.HashMap<puexamroutine.control.domain.CourseCode, java.lang.Integer> CoursesStudents;
    private final puexamroutine.control.domain.Semester Semester;
    private int MaximumAppearingCands= java.lang.Integer.MIN_VALUE;
    
    private boolean DEBUG = false;
}