
//                                  !! RAM !!

package puexamroutine.control.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import puexamroutine.control.domain.interfaces.CandidateInterface;
import puexamroutine.control.*;
import puexamroutine.control.domain.*;

/**
 *
 * @author Sumit Shresth
 */
public class Candidate implements CandidateInterface,java.io.Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Candidate.class);
    
    private boolean DEBUG = false;
    
    public Candidate( final String ID, final Program prg ,College Col ,final int Sem, final String State ){        
        this.ID = ID.trim().toUpperCase();
        this.State = State;
        this.Program = prg;
        this.College = Col;
        this.Semester = Sem;
    }
    
    public void addBackPapers( java.util.Collection<puexamroutine.control.domain.CourseCode> BackPapers ){
        this.BackPapers.addAll( BackPapers );
    }
    
    public void addBackPaper( puexamroutine.control.domain.CourseCode cours ){
        this.BackPapers.add(cours);
    }
    
    public void addRegualarPapers( java.util.Collection<puexamroutine.control.domain.CourseCode> RegPapers ){        
        this.RegularPapers.addAll( RegPapers );
    }
    
    public void addRegularPaper( puexamroutine.control.domain.CourseCode cours ){
        this.RegularPapers.add(cours);
    }
    
    public final puexamroutine.control.domain.CourseCode[] getBackPapers(){
        return this.BackPapers.toArray( new puexamroutine.control.domain.CourseCode[]{} );
    }
    
    public final puexamroutine.control.domain.CourseCode[] getRegularPapers(){
        return this.RegularPapers.toArray( new puexamroutine.control.domain.CourseCode[]{} );
    }
    
    public final String getState(){
        return this.State;
    }
    
    public final String getCandidateID(){
        return this.ID;
    }
    
    public final puexamroutine.control.domain.Program getProgram(){
        return this.Program;
    }
    
    public final int getSemester(){
        return this.Semester;
    }
    
    public final java.util.Collection<puexamroutine.control.domain.CourseCode> getAllCoursesToAttend(){
        if(DEBUG)LOGGER.debug("{} is being accessed. state={} regular={} back={}", this.ID, this.State, this.RegularPapers.size(), this.BackPapers.size());
        if( this.State == this.BACK_ONLY ){
            return this.getBackCourses();
        }
        else
            if( this.State == this.REGULAR_ONLY ){
                return this.getRegularCourses();
            }
            else{// both
                java.util.HashSet<puexamroutine.control.domain.CourseCode> list = new java.util.HashSet<puexamroutine.control.domain.CourseCode>();
                list.addAll( this.BackPapers );
                list.addAll( this.RegularPapers );
                if(this.DEBUG)LOGGER.debug("Combined regular and back papers count={}", list.size());
                return list;
            }
    }
    
    public java.util.Collection<puexamroutine.control.domain.CourseCode> getRegularCourses(){
        return this.RegularPapers;
    }
    
    public java.util.Collection<puexamroutine.control.domain.CourseCode> getBackCourses(){
        return this.BackPapers;
    }
    
    public boolean hasPureBackPapers(){
        return this.BackPapers.size() > 0;
    }
    
    public java.util.Collection<puexamroutine.control.domain.CourseCode> getDependentCourses(){
        return this.getAllCoursesToAttend();
    }
    
    public final College getCollege(){
        return this.College;
    }
    
    private final String ID;
    private final Program Program;
    private final College College;
    private final String State;    
    private final int Semester;
    
    private java.util.List<puexamroutine.control.domain.CourseCode> BackPapers = new java.util.ArrayList<puexamroutine.control.domain.CourseCode>();
    private java.util.HashSet<puexamroutine.control.domain.CourseCode> RegularPapers = new java.util.HashSet<puexamroutine.control.domain.CourseCode>();    
}