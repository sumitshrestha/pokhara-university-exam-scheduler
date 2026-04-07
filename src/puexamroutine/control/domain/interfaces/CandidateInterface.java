
//                          !! RAM !!

package puexamroutine.control.domain.interfaces;

import java.util.Collection;
import puexamroutine.control.domain.*;

/**
 *
 * @author Sumit Shresth
 */
public interface CandidateInterface extends DependentCourses{

    String BACK_ONLY = "BACK_ONLY";
    String REGULAR_ONLY = "REGULAR_ONLY";
    String REGULAR_WITH_BACK = "REGULAR_WITH_BACK";    
    int NON_REGULAR= -1;
    
    CourseCode[] getBackPapers();

    String getCandidateID();

    CourseCode[] getRegularPapers();

    String getState();

    puexamroutine.control.domain.Program getProgram();

    Collection<CourseCode> getAllCoursesToAttend();

    Collection<CourseCode> getBackCourses();

    Collection<CourseCode> getRegularCourses();

    boolean hasPureBackPapers();

    int getSemester();

}