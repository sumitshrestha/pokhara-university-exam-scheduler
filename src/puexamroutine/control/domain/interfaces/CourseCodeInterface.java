
//                              !! RAM !!

package puexamroutine.control.domain.interfaces;

import puexamroutine.control.domain.CourseCode;

/**
 *
 * @author Sumit Shresth
 */
public interface CourseCodeInterface {
    char PrimarySeparator = '-';
    char SecondarySeparator = '.';

    String getCode();

    String getCourseCode();

    String getPrefix();

    String getCourseCredit();

    boolean equals(CourseCode c);

}
