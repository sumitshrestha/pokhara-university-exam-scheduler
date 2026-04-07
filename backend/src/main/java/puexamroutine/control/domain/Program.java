
//                          !! RAM !!

package puexamroutine.control.domain;

/**
 *
 * @author Sumit Shresth
 */
public class Program implements java.io.Serializable{
    
    public Program( final String ProgramName ){        
        this.ProgramName = ProgramName.trim().toUpperCase();
    }
    
    public Program( final String ProgramName, puexamroutine.control.domain.Group g ){        
        this.ProgramName = ProgramName.trim().toUpperCase();
        this.Group = g;
    }
    
    /**
     * Renames the program name
     * 
     * @param newPrgNm name of the new program name
     * 
     * @return true if successfully renamed else false
     */
    public boolean rename( final String newPrgNm ){
        try{
            if( newPrgNm == null || newPrgNm.length() <= 0 )
                return false;
            this.ProgramName = newPrgNm;
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public void setGroup( puexamroutine.control.domain.Group g ){
        this.Group = g;
    }
    
    public void addColleges( java.util.Collection<puexamroutine.control.domain.College> c ){
        this.Colleges.addAll(c);
    }
    
    public void addCollege( puexamroutine.control.domain.College c ){
        this.Colleges.add(c);
    }
    
    public final String getProgramName(){
        return this.ProgramName;
    }
    
    public final puexamroutine.control.domain.Group getGroup(){
        return this.Group;
    }
    
    /**
     * This method gives the list of all semesters for this projects
     * 
     * @return the list of all semesters for this projects
     */
    public java.util.HashSet<puexamroutine.control.domain.Semester> getSemesters(){
        java.util.HashSet<puexamroutine.control.domain.Semester> Semester = new java.util.HashSet<puexamroutine.control.domain.Semester>();
        java.util.Iterator<puexamroutine.control.domain.College> CollItr = this.Colleges.iterator();
        while( CollItr.hasNext() ){
            puexamroutine.control.domain.College Coll = CollItr.next();
            Semester.addAll( Coll.getSemesters( this ).getSemesters() );
        }
        return Semester;
    }
    
    /**
     * This method gives the list of all regular courses for this program from various supporting colleges
     * This method can be used to get overall liat of regualr courses for one group.
     * 
     * @return the list of all regular courses for this program from various supporting colleges
     */
    public java.util.HashSet<puexamroutine.control.domain.RegularCourses> getRegularCourse(){
        java.util.HashSet<puexamroutine.control.domain.RegularCourses> Reg = new java.util.HashSet<puexamroutine.control.domain.RegularCourses>();
        java.util.Iterator<puexamroutine.control.domain.College> CollItr = this.Colleges.iterator();
        while( CollItr.hasNext() ){
            puexamroutine.control.domain.College Coll = CollItr.next();
            Reg.addAll( Coll.getRegularCourse( this ) );
        }
        return Reg;
    }
    
    @Override
    public String toString(){
        return this.ProgramName;
    }
    
    public boolean equals( puexamroutine.control.domain.Program prg ){
        return prg == this || this.checkStrictly(prg);
    }
    
    public boolean supports( puexamroutine.control.domain.College c ){
        return this.Colleges.contains(c);
    }
    
    public java.util.Collection<puexamroutine.control.domain.College> getSupportingColleges(){
        return this.Colleges;
    }
    
    private boolean checkStrictly( puexamroutine.control.domain.Program prg ){
        return prg.getProgramName().equals( this.ProgramName) && this.Group.equals( prg.getGroup() );
    }
    
    public java.util.HashSet<puexamroutine.control.domain.CentreIdentifier> getCentres(){
        java.util.HashSet<puexamroutine.control.domain.CentreIdentifier>  Centres = new java.util.HashSet<puexamroutine.control.domain.CentreIdentifier>();
        java.util.Iterator<puexamroutine.control.domain.College> CollegeItr = this.Colleges.iterator();
        while( CollegeItr.hasNext() ){
            Centres.add( CollegeItr.next().getCentre( this ) );
        }
        return Centres;
    }
    
    public java.util.Collection<puexamroutine.control.domain.CourseCode> getRegularCourses(){
        java.util.HashSet<puexamroutine.control.domain.CourseCode> Courses = new java.util.HashSet<puexamroutine.control.domain.CourseCode>();
        java.util.Iterator<puexamroutine.control.domain.RegularCourses> RegItr = this.getRegularCourse().iterator();
        while( RegItr.hasNext() ){
            Courses.addAll( RegItr.next().getCourses() );
        }
        return Courses;
    }
    
    public java.util.Collection< puexamroutine.control.domain.Semester > getSemesters( puexamroutine.control.domain.CourseCode c ){
        java.util.Iterator<puexamroutine.control.domain.RegularCourses> RegItr = this.getRegularCourse().iterator();
        java.util.HashSet<puexamroutine.control.domain.Semester> Sems = new java.util.HashSet<puexamroutine.control.domain.Semester>();
        while( RegItr.hasNext() ){
            puexamroutine.control.domain.RegularCourses reg = RegItr.next();
            if( reg.getCourses().contains(c)){
                Sems.add( reg.getSemester() );
            }
        }
        return Sems;
    }
    
    public java.util.Collection<puexamroutine.control.domain.interfaces.CandidateInterface> getBackCandidates(){
        java.util.HashSet<puexamroutine.control.domain.interfaces.CandidateInterface> BkCands = new java.util.HashSet<puexamroutine.control.domain.interfaces.CandidateInterface >();
        java.util.Iterator<puexamroutine.control.domain.College> CollItr = this.Colleges.iterator();
        while( CollItr.hasNext() ){
            puexamroutine.control.domain.CollegeBackCandidates CollBkCands = CollItr.next().getBackCandidates( this );
            BkCands.addAll( CollBkCands.getBackPaperCandidates() );
        }
        return BkCands;
    }
    
    private puexamroutine.control.domain.Group Group;
    private String ProgramName;
    private java.util.HashSet<puexamroutine.control.domain.College> Colleges=new java.util.HashSet<puexamroutine.control.domain.College>();
}