
//                          !! RAM !!

package puexamroutine.control.domain;

/**
 * This class holds the back students in this college.
 * This holds the courses for each back candidates.
 * Since, A college may have many program, each program may have different back candidates.
 * Also, within same program of college, there may be different candidates from different semesters.
 * Each candidates only holds the pure back papers since regular subject may be already scheduled.
 *
 * @author Sumit Shresth
 */
public class CollegeBackCandidates implements java.io.Serializable{
    
    public CollegeBackCandidates( final puexamroutine.control.domain.College college, final puexamroutine.control.domain.Program prg ){
        this.College = college;
        this.prg = prg;
    }    
    
    public final puexamroutine.control.domain.College getCollege(){
        return this.College;
    }
    
    public final puexamroutine.control.domain.Program getProgram(){
        return this.prg;
    }
    
    public void addBackPaperCandidate( puexamroutine.control.domain.interfaces.CandidateInterface cand ){
        this.BackPaperCandidates.add(cand);
    }
    
    public void addBackPaperCandidates( java.util.Collection<puexamroutine.control.domain.interfaces.CandidateInterface> cands ){
        this.BackPaperCandidates.addAll(cands);
    }
    
    public java.util.Collection<puexamroutine.control.domain.interfaces.CandidateInterface> getBackPaperCandidates(){
        return this.BackPaperCandidates;
    }
    
    public java.util.Collection<puexamroutine.control.domain.CourseCode> getBackPapers( final String BkCandidate ){
        puexamroutine.control.domain.interfaces.CandidateInterface BkCand = this.getCandidate(BkCandidate);
        return BkCand.getBackCourses();
    }
    
    public puexamroutine.control.domain.interfaces.CandidateInterface getCandidate( final String Candidate ){
        java.util.Iterator<puexamroutine.control.domain.interfaces.CandidateInterface> CandItr = this.BackPaperCandidates.iterator();
        while( CandItr.hasNext() ){
            puexamroutine.control.domain.interfaces.CandidateInterface cand = CandItr.next();
            if( cand.getCandidateID().equals( Candidate )){
                return cand;
            }
        }
        return null;
    }

    private final puexamroutine.control.domain.College College;
    private final puexamroutine.control.domain.Program prg;
    private java.util.HashSet<puexamroutine.control.domain.interfaces.CandidateInterface > BackPaperCandidates = new java.util.HashSet<puexamroutine.control.domain.interfaces.CandidateInterface>();
}