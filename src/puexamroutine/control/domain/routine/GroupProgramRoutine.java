
//                          !! RAM !!

package puexamroutine.control.domain.routine;

import java.util.Collection;
import java.util.List;
import puexamroutine.control.domain.CourseCode;
import puexamroutine.control.domain.Day;
import puexamroutine.control.domain.Group;

/**
 * This interface gives the calandar of the a particular group.
 * A group has program which may share same program.
 * In such cases, changing any course of a single program may effect other programs in same group.
 * To monitor that effect and take necessary actions, a higher level calandar above program routine but below overall calandar is must.
 * This interface provides that. 
 * It provides all necessary signatures for implementing a group based routines.
 *
 * @author Sumit Shresth
 */
public interface GroupProgramRoutine{

    Collection<CourseCode> getExamCourses(Day day);

    List<Day> getExamDayList();

    Group getGroup();

}