
//                              !! RAM !!

package puexamroutine.control.domain.list;

/**
 *
 * @author Sumit Shresth
 */
public class ProgramList implements java.io.Serializable{
    
    public ProgramList(  final puexamroutine.control.domain.Group g ){
        this.group = g;
    }
    
    public void addPrograms(java.util.Collection<puexamroutine.control.domain.Program> p ){
        this.Programs.addAll(p);
    }
    
    public void addProgram( puexamroutine.control.domain.Program p ){
        this.Programs.add(p);
    }
    
    public java.util.ArrayList<puexamroutine.control.domain.Program> getPrograms(){
        return this.Programs;
    }
    
    public final puexamroutine.control.domain.Group getGroup(){
        return this.group;
    }   
    
    public boolean exists( puexamroutine.control.domain.Program prg ){
        return this.Programs.contains(prg) || this.checkStrictly(prg);
    }
    
    private boolean checkStrictly( puexamroutine.control.domain.Program prg ){
        java.util.Iterator<puexamroutine.control.domain.Program> it = this.Programs.iterator();
        while( it.hasNext() ){
            if( it.next().equals(prg) )
                return true;
        }
        return false;
    }
    
    /**
     * This method gives the list of all regular courses within this group of programs.
     * The course within each such set may overlap which is the reason of keeping them together in this list.
     * 
     * @return the list of all regular courses within this group of programs.
     */
    public java.util.HashSet<puexamroutine.control.domain.RegularCourses> getRegularCourses(){
        if( this.RegularCourses == null )
            this.calculateRegularCourses();
        return this.RegularCourses;
    }
    
    private void calculateRegularCourses(){
        RegularCourses = new java.util.HashSet<puexamroutine.control.domain.RegularCourses>();
        java.util.Iterator<puexamroutine.control.domain.Program> prgItr = this.Programs.iterator();
        while( prgItr.hasNext() ){
            puexamroutine.control.domain.Program prg = prgItr.next();
            RegularCourses.addAll( prg.getRegularCourse() );
        }
    }
    
    private void calculateCentres(){
        Centres = new java.util.HashSet<puexamroutine.control.domain.CentreIdentifier>();
        java.util.Iterator<puexamroutine.control.domain.Program> PrgItr = this.Programs.iterator();
        while( PrgItr.hasNext() ){
            Centres.addAll( PrgItr.next().getCentres() );
        }        
    }
    
    public java.util.HashSet<puexamroutine.control.domain.CentreIdentifier> getCentres(){
        if( this.Centres == null )
            this.calculateCentres();
        return this.Centres;
    }
    
    private void calculateBackCandidates(){
        this.BkCandidates = new java.util.HashSet<puexamroutine.control.domain.interfaces.CandidateInterface>();
        java.util.Iterator<puexamroutine.control.domain.Program> prgItr = this.Programs.iterator();
        while( prgItr.hasNext() ){
            this.BkCandidates.addAll( prgItr.next().getBackCandidates() );
        }
    }
    
    public java.util.Collection<puexamroutine.control.domain.interfaces.CandidateInterface> getBackCandidates(){
        if( this.BkCandidates == null ) this.calculateBackCandidates();
        return this.BkCandidates;
    }
    
    public int getTotalCandidates( ){
        int sum = 0;
        java.util.Iterator<puexamroutine.control.domain.RegularCourses> RegItr = this.getRegularCourses().iterator();
        while( RegItr.hasNext() ){
            sum += RegItr.next().getMaximumAppearingCands();
        }        
        return sum;
    }
    
    public boolean belongsTo( puexamroutine.control.domain.CourseCode c ){
        java.util.Iterator<puexamroutine.control.domain.Program> prgitr= this.Programs.iterator();
        while( prgitr.hasNext() ){
            if( prgitr.next().getRegularCourses().contains(c) ){
                return true;
            }
        }
        return false;
    }
    
    private final  puexamroutine.control.domain.Group group;    
    private java.util.ArrayList<puexamroutine.control.domain.Program> Programs = new java.util.ArrayList<puexamroutine.control.domain.Program>();
    private java.util.HashSet<puexamroutine.control.domain.CentreIdentifier> Centres = null;
    private java.util.HashSet<puexamroutine.control.domain.RegularCourses> RegularCourses = null;
    private java.util.HashSet<puexamroutine.control.domain.interfaces.CandidateInterface> BkCandidates = null;    
}