
//                          !! RAM !!

package puexamroutine.control.domain.routine;

import puexamroutine.control.domain.Day;
import puexamroutine.control.domain.Exam;

/**
 *
 * @author Sumit Shresth
 */
public class ProgramRoutine implements java.io.Serializable{
    
    public ProgramRoutine( final puexamroutine.control.domain.Program prg ){
        this.Prg = prg;
    }
    
    public void addExam( puexamroutine.control.domain.Exam Exam, puexamroutine.control.domain.Day ExamDay ){
        if( this.ExamsDayList.contains( ExamDay ) ){
            addExamOnDay( Exam, ExamDay );
        }
        else{
            this.createNewExamDay(ExamDay);
            this.addExamOnDay(Exam, ExamDay);
        }            
    }

    private void addExamOnDay(Exam Exam, Day ExamDay) {
        puexamroutine.control.domain.routine.ProgramExamDayCourses prgCourse = this.CoursesOnExamDayMap.get(ExamDay);
        prgCourse.addExamCourseOnThisDay(Exam.getExamCourse());
    }
    
    private void createNewExamDay( puexamroutine.control.domain.Day Day ){
        this.ExamsDayList.add(Day);
        this.CoursesOnExamDayMap.put( Day , new puexamroutine.control.domain.routine.ProgramExamDayCourses( Day, Prg ) );
    }
    
    public void print(){
        System.out.println("\n\tThe Exam Routine for the program " + this.Prg.getProgramName() );
        java.util.Iterator<puexamroutine.control.domain.Day> DayItr = this.ExamsDayList.iterator();
        while( DayItr.hasNext() ){
            puexamroutine.control.domain.Day day = DayItr.next();            
            puexamroutine.control.domain.routine.ProgramExamDayCourses routine = this.CoursesOnExamDayMap.get( day );
            System.out.println();
            System.out.print( day.getExamID().toString()+ " " );
            puexamroutine.control.domain.CourseCode[] courseitr = routine.getCoursesForDay();
            for( int i=0; i<courseitr.length; i++ ){
                System.out.print(courseitr[i].toString() +", ");
            }
            System.out.println();
        }
    }
    
    public final puexamroutine.control.domain.Program getProgram(){
        return this.Prg;
    }
    
    public java.util.ArrayList<puexamroutine.control.domain.Day> getExamList(){
        return this.ExamsDayList;
    }
    
    public puexamroutine.control.domain.routine.ProgramExamDayCourses getExams( puexamroutine.control.domain.Day day ){
        return this.CoursesOnExamDayMap.get( day );
    }
    
    private final puexamroutine.control.domain.Program Prg;    
    private java.util.ArrayList<puexamroutine.control.domain.Day> ExamsDayList = new java.util.ArrayList<puexamroutine.control.domain.Day>();
    private java.util.HashMap< puexamroutine.control.domain.Day, puexamroutine.control.domain.routine.ProgramExamDayCourses > CoursesOnExamDayMap = new java.util.HashMap< puexamroutine.control.domain.Day, puexamroutine.control.domain.routine.ProgramExamDayCourses        >();
}