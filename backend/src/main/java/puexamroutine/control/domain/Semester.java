
//                      !! RAM !!

package puexamroutine.control.domain;

/**
 *
 * @author Sumit Shresth
 */
public class Semester implements java.io.Serializable{
    
    public Semester( final String sem, puexamroutine.control.domain.College c , puexamroutine.control.domain.RegularCourses r, puexamroutine.control.domain.Program prg ){        
        this.Semester = sem.trim().toUpperCase();
        this.college =c;
        this.reg = r;
        this.prg = prg;
    }
    
    public Semester( final String sem, puexamroutine.control.domain.College c , puexamroutine.control.domain.Program prg ){        
        this.Semester = sem.trim().toUpperCase();
        this.college =c;        
        this.prg = prg;
    }
    
    public void setRegularCourses( puexamroutine.control.domain.RegularCourses reg ){
        this.reg = reg;
    }
    
    public final String getSemester(){
        return this.Semester;
    }
    
    public puexamroutine.control.domain.College getCollege(){
        return this.college;
    }
    
    public puexamroutine.control.domain.Program getProgram(){
        return this.prg;
    }
    
    public puexamroutine.control.domain.RegularCourses getRegularCourses(){
        return this.reg;
    }
    
    public boolean equals( puexamroutine.control.domain.Semester sem ){
        if( ! this.Semester.equals(sem.getSemester() ) )
            return false;
        
        if( ! this.college.equals(sem.getCollege() ))
            return false;
        
        if( ! this.prg.equals(sem.getSemester() ))
            return false;
        
        return true;
    }
    
    private final String Semester;
    private puexamroutine.control.domain.College college;
    private puexamroutine.control.domain.RegularCourses reg;
    private puexamroutine.control.domain.Program prg;
}