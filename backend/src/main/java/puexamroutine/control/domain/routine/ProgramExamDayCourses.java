
//                                  !! RAM !!

package puexamroutine.control.domain.routine;

/**
 * This class gives the list of courses for this program of this exam day.
 * 
 * @author Sumit Shresth
 */
public class ProgramExamDayCourses implements java.io.Serializable{
    
    public ProgramExamDayCourses( puexamroutine.control.domain.Day ExDay, puexamroutine.control.domain.Program prg ){        
        this.ExamDay = ExDay;
        this.Program = prg;        
    }
    
    public void addExamCourseOnThisDay( puexamroutine.control.domain.CourseCode courses ){
        this.Courses.add(courses);
    }    
    
    public puexamroutine.control.domain.CourseCode[] getCoursesForDay(){
        return this.Courses.toArray( new puexamroutine.control.domain.CourseCode[]{} );
    }
    
    public boolean hasCoursesWithThisProgram(){
        return this.Courses.size() > 0;
    }
    
    public String[] getCourseAsString(){
        puexamroutine.control.domain.CourseCode[] c = this.getCoursesForDay();
        String[] courses = new String[ c.length ];
        for( int i=0; i<c.length; i++ )
            courses[i] = c[i].toString();
        
        return courses;
    }
    
    private java.util.HashSet<puexamroutine.control.domain.CourseCode> Courses = new java.util.HashSet<puexamroutine.control.domain.CourseCode>();
    private final puexamroutine.control.domain.Day ExamDay;
    private final puexamroutine.control.domain.Program Program;
}