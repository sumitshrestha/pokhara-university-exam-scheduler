
//                              !! RAM !!

package puexamroutine.control.domain.interfaces;

import puexamroutine.control.domain.College;
import puexamroutine.control.domain.Program;

/**
 * This interface is for interfacing list of courses that are interdependent on each other.
 * This is done mainly for similarities between regular subjects and back papers.
 * Both regular subject and back papers ( here for each candidate ) have set of courses to be conducted
 * But non of courses in each list can be conducted in same day.
 * This will cause same student ( regular or back ) to attend two exam in same day that is impossible
 * So, This dependency betwwen courses in list is similarity between regular and back papers
 * 
 * Thus, To represent this similarity between regualar and back papers this interface is developed
 *
 * @author Sumit Shresth
 */
public interface DependentCourses {
    
    java.util.Collection<puexamroutine.control.domain.CourseCode> getDependentCourses();

    Program getProgram();

    College getCollege();

}