
//                              !! RAM !!

package puexamroutine.control.routinegeneration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.lang.*;
import java.util.HashSet;
import puexamroutine.control.domain.*;
import puexamroutine.control.domain.list.*;
import puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution;

/**
 * This class creates the independent course.
 * About Independent course read in docs.
 * To summarize independent course list is list of set of course each having same color
 * It is obtained after graph coloring.
 * Originally, This work was done by IndependentRegularCourseAnalyzer.
 * But, This class is for regular courses, it rendered useless for the back papers that are handled differently by different analyzer
 * Still, The creation work is same in both.
 * So, The need of this separate class grew
 *
 * @author Sumit Shresth
 */
public final class IndependentCourseCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndependentCourseCreator.class);
    
    /**
     * This method creates one independent courses object from the specified Courses
     * This represents a set of courses having same color that can be conducted within same day
     * 
     * @param Color 
     * @param ColoredCoursesIndex
     * @param Courses
     * @return independent courses 
     */
    private static final IndependentCourses createSameColoredCourseCodeList( Integer Color, HashMap<Integer, HashSet<Integer>> ColoredCoursesIndex, CourseCode[] Courses,boolean DEBUG) {
        java.util.Iterator<java.lang.Integer> SameColorCoursesItr = ColoredCoursesIndex.get(Color).iterator();
        IndependentCourses SameColorCourseCodes = new IndependentCourses();
        while (SameColorCoursesItr.hasNext()) {
            int ColorIndex = SameColorCoursesItr.next();            
            if(DEBUG)LOGGER.debug("{}", Courses[ColorIndex].toString());
            SameColorCourseCodes.addIndepentRegularCourse( Courses[ColorIndex] );
        }
        return SameColorCourseCodes;
    }
    
    public static final void createListOfSameColoredCourseCodes( GraphColoringSolution Solution, CourseCode[] Courses, IndependentCourseList ColoredCoursesList ,boolean DEBUG ){
        ColoredCoursesList.clearIndependentCourses();
        HashMap<Integer, HashSet<Integer>> ColoredCoursesIndex = Solution.getGroupedVerticesIndexes();
        Iterator<Integer> ColorItr = ColoredCoursesIndex.keySet().iterator();
        while( ColorItr.hasNext() ){            
            int c = ColorItr.next();                     
            if(DEBUG) LOGGER.debug("for color {}", c);
            ColoredCoursesList.addIndependentCourse( c,createSameColoredCourseCodeList( c, ColoredCoursesIndex, Courses ,DEBUG )  );
        }
    }
    
}