
//                      !! RAM !!

package puexamroutine.control.domain;

import java.util.HashMap;
import java.util.HashSet;
import puexamroutine.control.domain.interfaces.CandidateInterface;

/**
 * This class represents an exam of PU.
 * An exam has to be conducted for a course.
 * Also, An exam can be coducted for multiple collges.
 * Also, An exam may be in different programs.
 * 
 * @author Sumit Shresth
 */
public class Exam  implements java.io.Serializable{
    
    public Exam( puexamroutine.control.domain.CourseCode c, final String Ex ){
        this.ExamCourse = c;                
        this.EXAM_TYPE = Ex;
    }

    public void addExaminationSemesters( java.util.Collection<puexamroutine.control.domain.Semester> sems ){
        if( this.EXAM_TYPE == this.REGULAR )
            this.ExamSemesters.addAll(sems);
    }
    
    public void addExaminationSemester( puexamroutine.control.domain.Semester sem ){
        if( this.EXAM_TYPE == this.REGULAR )
            this.ExamSemesters.add( sem );
    }
    
    public void addCandidate( CandidateInterface Cand ){
        if( this.EXAM_TYPE == this.PURE_BACK ){
            this.Candidates.add(Cand);
        }
    }
    
    public void addCandidates( java.util.Collection<CandidateInterface> Cands ){
        if( this.EXAM_TYPE == this.PURE_BACK ){
            this.Candidates.addAll(Cands);
        }
    }
    
    public java.util.HashSet<puexamroutine.control.domain.Program> getExaminationPrograms(){
        if( this.EXAM_TYPE == this.REGULAR )
            return getRegularPrograms();
        else
            if( this.EXAM_TYPE == this.PURE_BACK )
                return this.getBackPrograms();
            else
                return null;
    }
    
    public java.util.HashSet<puexamroutine.control.domain.College> getExaminationColleges(){
        if( this.EXAM_TYPE == this.REGULAR )
            return getRegularCollege();
        else
            if( this.EXAM_TYPE == this.REGULAR )
                return this.getBackColleges();
            else
                return null;
    }
    
    public final puexamroutine.control.domain.CourseCode getExamCourse(){
        return this.ExamCourse;
    }
    
    public final java.util.HashMap<puexamroutine.control.domain.CentreIdentifier, java.lang.Integer> getStudentsForExams(){
        if( this.EXAM_TYPE == this.REGULAR )
            return getRegularStudents();
        else
            if( this.EXAM_TYPE == this.PURE_BACK )
                return this.getBackStudents();
            else
                return null;
    }
    
    public final String getExamType(){
        return this.EXAM_TYPE;
    }

    private final HashSet<College> getRegularCollege() {
        java.util.HashSet<puexamroutine.control.domain.College> coll = new java.util.HashSet<puexamroutine.control.domain.College>();
        java.util.Iterator<puexamroutine.control.domain.Semester> SemItr = this.ExamSemesters.iterator();
        while (SemItr.hasNext()) {
            coll.add(SemItr.next().getCollege());
        }
        return coll;
    }
    
    private final HashSet<College> getBackColleges() {
        java.util.HashSet<puexamroutine.control.domain.College> coll = new java.util.HashSet<puexamroutine.control.domain.College>();
        java.util.Iterator< CandidateInterface > SemItr = this.Candidates.iterator();
        while (SemItr.hasNext()) {
            coll.add(SemItr.next().getCollege());
        }
        return coll;
    }

    private final HashSet<Program> getRegularPrograms() {
        java.util.HashSet<puexamroutine.control.domain.Program> Prgs = new java.util.HashSet<puexamroutine.control.domain.Program>();
        java.util.Iterator<puexamroutine.control.domain.Semester> SemItr = this.ExamSemesters.iterator();
        while (SemItr.hasNext()) {
            Prgs.add(SemItr.next().getProgram());
        }
        return Prgs;
    }
    
    private final HashSet<Program> getBackPrograms() {
        java.util.HashSet<puexamroutine.control.domain.Program> Prgs = new java.util.HashSet<puexamroutine.control.domain.Program>();
        java.util.Iterator<CandidateInterface> BackItr = this.Candidates.iterator();
        while ( BackItr.hasNext()) {
            Prgs.add( BackItr.next().getProgram());
        }
        return Prgs;
    }
    
    private final HashMap<CentreIdentifier, Integer> getRegularStudents() {
        java.util.Iterator<puexamroutine.control.domain.Semester> semItr = this.ExamSemesters.iterator();
        java.util.HashMap<puexamroutine.control.domain.CentreIdentifier, java.lang.Integer> pair = new java.util.HashMap<puexamroutine.control.domain.CentreIdentifier, java.lang.Integer>();
        while (semItr.hasNext()) {
            puexamroutine.control.domain.Semester sem = semItr.next();
            int stdt = sem.getRegularCourses().getTotalStudents(this.ExamCourse);
            puexamroutine.control.domain.CentreIdentifier c = sem.getCollege().getCentre(sem.getProgram());
            if (pair.containsKey(c)) {
                pair.put(c, stdt + pair.get(c));
            } else {
                pair.put(c, stdt);
            }
        }
        return pair;
    }
    
    private final HashMap<CentreIdentifier, Integer> getBackStudents() {
        java.util.Iterator<CandidateInterface> semItr = this.Candidates.iterator();
        java.util.HashMap<CentreIdentifier, java.lang.Integer> pair = new java.util.HashMap<puexamroutine.control.domain.CentreIdentifier, java.lang.Integer>();
        while (semItr.hasNext()) {
            CandidateInterface cand = semItr.next();
            CentreIdentifier Centre = cand.getCollege().getCentre( cand.getProgram() );
            if( pair.containsKey( Centre ) ){
                pair.put(Centre, pair.get( Centre) + 1 );
            }
            else
                pair.put(Centre, 1 ); //single candidate
        }
        return pair;
    }
    
    public static final String REGULAR = "regular exam", PURE_BACK="pure back exam";          
    private final puexamroutine.control.domain.CourseCode ExamCourse;
    private final String EXAM_TYPE;
    private java.util.HashSet<puexamroutine.control.domain.Semester> ExamSemesters = new java.util.HashSet<puexamroutine.control.domain.Semester>();    
    private java.util.Collection<CandidateInterface> Candidates = new java.util.HashSet<CandidateInterface>();
}