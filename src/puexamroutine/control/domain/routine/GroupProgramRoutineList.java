
//                              !! RAM !!

package puexamroutine.control.domain.routine;

import puexamroutine.control.domain.Day;
import puexamroutine.control.domain.Exam;
import puexamroutine.control.domain.CourseCode;
import puexamroutine.control.domain.Program;

/**
 * This class represents the list of all program routine of specified group.
 *
 * @author Sumit Shresth
 */
public class GroupProgramRoutineList implements java.io.Serializable, GroupProgramRoutine{
    
    public GroupProgramRoutineList( final puexamroutine.control.domain.Group grp, final puexamroutine.control.domain.list.GroupList grplist ){
        this.Grp = grp;
        this.grplist = grplist;            
    }
    
    /**
     * This method is used to add an exam to all the programs within this group with the specified exam on specified exam day.
     * 
     * @param ExamDay the specified day when exam is due to occur
     * @param exam The specified exam to be conducted that belongs to this group
     */
    public void addExam( puexamroutine.control.domain.Day ExamDay, puexamroutine.control.domain.Exam exam ){
        java.util.Iterator<puexamroutine.control.domain.Program> prgitr = exam.getExaminationPrograms().iterator();
        java.util.Collection<puexamroutine.control.domain.Program> prgs = exam.getExaminationPrograms();
        while( prgitr.hasNext() ){
            puexamroutine.control.domain.Program prg = prgitr.next();
            if( prgs.contains(prg) ){
                addExamToProgramRoutine(exam, ExamDay, prg);
            }
        }
        addExamToGroupCalander(ExamDay, exam);
    }

    private void addExamToGroupCalander(Day ExamDay, Exam exam) {
        // add to itself
        if (!this.ExamsDayList.contains(ExamDay)) {
            this.ExamsDayList.add(ExamDay);
            ExamDayCourses courses = new ExamDayCourses();
            courses.add(exam.getExamCourse());
            this.CoursesOnExamDayMap.put(ExamDay, courses);
        } else {
            java.util.Collection<CourseCode> courses = this.CoursesOnExamDayMap.get(ExamDay).getCourses();
            courses.add(exam.getExamCourse());
        }
    }
    
    private void addExamToProgramRoutine(Exam exam, Day ExamDay, Program prg) {
        if (!this.ProgramRoutineList.containsKey(prg)) {
            puexamroutine.control.domain.routine.ProgramRoutine prgRoutine = new puexamroutine.control.domain.routine.ProgramRoutine(prg);
            prgRoutine.addExam(exam, ExamDay);
            this.ProgramRoutineList.put(prg, prgRoutine);
        } else {
            puexamroutine.control.domain.routine.ProgramRoutine prgRoutine = this.ProgramRoutineList.get(prg);
            prgRoutine.addExam(exam, ExamDay);
        }
    }
    
    public java.util.Collection<puexamroutine.control.domain.routine.ProgramRoutine> getAllProgramRoutine(){
        //return this.ProgramRoutineList.values();
        java.util.Collection<puexamroutine.control.domain.routine.ProgramRoutine> val = new java.util.ArrayList<puexamroutine.control.domain.routine.ProgramRoutine>( this.ProgramRoutineList.size() );
        java.util.Iterator<puexamroutine.control.domain.Program> prgItr = this.ProgramRoutineList.keySet().iterator();
        while( prgItr.hasNext() ){
            val.add( this.ProgramRoutineList.get( prgItr.next() ));
        }
        return val;
    }
        
    public final java.util.List<puexamroutine.control.domain.Day> getExamDayList(){
        return this.ExamsDayList;
    }
    
    public final java.util.Collection<CourseCode> getExamCourses( puexamroutine.control.domain.Day day ){
        return this.CoursesOnExamDayMap.get(day).getCourses();
    }
    
    public final puexamroutine.control.domain.Group getGroup(){
        return this.Grp;
    }
    
    /**
     * This class represents courses for a particular day.
     * 
     * @author Sumit Shresth
     */
    public final class ExamDayCourses implements java.io.Serializable{
        
        public ExamDayCourses(){            
        }
        
        public void add( CourseCode c ){
            this.cs.add(c);
        }
        
        public java.util.Collection<CourseCode> getCourses( ){
            return this.cs;
        }
        
        private java.util.HashSet<CourseCode> cs = new java.util.HashSet<CourseCode>();
    }
    
    private final puexamroutine.control.domain.Group Grp;
    private final puexamroutine.control.domain.list.GroupList grplist;
    
    private java.util.HashMap<puexamroutine.control.domain.Program, puexamroutine.control.domain.routine.ProgramRoutine > ProgramRoutineList = new java.util.HashMap<puexamroutine.control.domain.Program, puexamroutine.control.domain.routine.ProgramRoutine>();
    
    private java.util.ArrayList<puexamroutine.control.domain.Day> ExamsDayList = new java.util.ArrayList<puexamroutine.control.domain.Day>();
    private java.util.HashMap< puexamroutine.control.domain.Day, ExamDayCourses  > CoursesOnExamDayMap = new java.util.HashMap< puexamroutine.control.domain.Day, ExamDayCourses  >();
}