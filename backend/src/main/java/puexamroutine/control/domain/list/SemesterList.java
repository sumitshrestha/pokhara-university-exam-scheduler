
//                          !! RAM !!

package puexamroutine.control.domain.list;

import puexamroutine.control.domain.Semester;

/**
 *
 * @author Sumit Shresth
 */
public class SemesterList implements java.io.Serializable{
    
    public SemesterList( final puexamroutine.control.domain.College c, final puexamroutine.control.domain.Program p ){
        this.college = c;
        this.prg = p;
    }
    
    public void addSemesters( java.util.HashSet<puexamroutine.control.domain.Semester> s ){
        this.semesters = s;
    }
    
    public void addSemesters( puexamroutine.control.domain.Semester[] s){
        this.semesters.addAll( java.util.Arrays.asList( s ) );
    }
    
    public void addSemester( puexamroutine.control.domain.Semester s ){
        this.semesters.add(s);
    }
    
    public java.util.HashSet<puexamroutine.control.domain.Semester> getSemesters(){
        return this.semesters;
    }
    
    public boolean exists( String Sem ){
        puexamroutine.control.domain.Semester[] sems = this.semesters.toArray( new puexamroutine.control.domain.Semester[]{} );
        for( int i=0; i < sems.length; i++ ){
            if( sems[i].getSemester().equals( Sem ) )
                return true;
        }
        return false;
    }
    
    public boolean exists( puexamroutine.control.domain.Semester s ){
        return this.semesters.contains(s) || this.checkStrictly(s);
    }
    
    /**
     * This method gets the list of regular courses for this semester list.
     * 
     * @return the list of regular courses for this semester list.
     */
    public java.util.List<puexamroutine.control.domain.RegularCourses> getRegularCourses( ){
        java.util.List<puexamroutine.control.domain.RegularCourses> Reg = new java.util.ArrayList<puexamroutine.control.domain.RegularCourses>();
        java.util.Iterator<puexamroutine.control.domain.Semester> SemItr = this.semesters.iterator();
        while( SemItr.hasNext() ){
            Reg.add( SemItr.next().getRegularCourses() );
        }
        return Reg;
    }
    
    private boolean checkStrictly(Semester s) {
        java.util.Iterator<puexamroutine.control.domain.Semester> it = this.semesters.iterator();
        while (it.hasNext()) {
            if (s.equals(it.next())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * This method gives the total no of maximum candidates from this college following this program appearing for regular exams only.
     * This gives the maximum students from a college program.
     * 
     * @return total no of maximum candidates from this college following this program appearing for regular exams only
     */
    public int getNetRegCandidatesForThisCollegesProgram(){
        int sum=0;
        java.util.Iterator<puexamroutine.control.domain.Semester> SemItr = this.semesters.iterator();
        while( SemItr.hasNext() ){
            sum += SemItr.next().getRegularCourses().getMaximumAppearingCands();
        }
        return sum;
    }
    
    private final puexamroutine.control.domain.College college;
    private final puexamroutine.control.domain.Program prg;
    private java.util.HashSet<puexamroutine.control.domain.Semester> semesters = new java.util.HashSet<puexamroutine.control.domain.Semester>();
        
}