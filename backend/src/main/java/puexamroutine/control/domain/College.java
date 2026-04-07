
//                      !! RAM !! 

package puexamroutine.control.domain;

/**
 *
 * @author Sumit Shresth
 */
public class College implements java.io.Serializable{
    
    public College( final String n ){
        this.Name = n.trim().toUpperCase();
    }
    
    public void addPrograms_Center_Semesters_Back(puexamroutine.control.domain.Program prg, puexamroutine.control.domain.CentreIdentifier c, puexamroutine.control.domain.list.SemesterList sems,puexamroutine.control.domain.CollegeBackCandidates CollBack ){
        this.addProgramCentre(prg, c);
        this.addProgramsSemesters(prg, sems);
        this.BackCandidates.put( prg, CollBack );
    }
    
    public void removeProgram(puexamroutine.control.domain.Program prg ){
        this.ProgramsCentres.remove(prg);
        this.ProgramsSemesters.remove(prg);
        this.BackCandidates.remove(prg);
    }
    
    private void addProgramCentre( puexamroutine.control.domain.Program prg, puexamroutine.control.domain.CentreIdentifier c ){
        this.ProgramsCentres.put(prg, c);             
    }
    
    private void addProgramsSemesters( puexamroutine.control.domain.Program prg, puexamroutine.control.domain.list.SemesterList sems ){
        this.ProgramsSemesters.put(prg, sems);        
    }
        
    public final String getCollegeName(){
        return this.Name;
    }
    
    /**
     * This method gets the centre of the college for the program supplied.
     * If the college does not support the specified program then null is returned.
     * 
     * @param prg The program of this college whose examination centre is to be obtained.
     * 
     * @return the examination centre of this college's program
     */
    public puexamroutine.control.domain.CentreIdentifier getCentre( puexamroutine.control.domain.Program prg ){
        return this.ProgramsCentres.get(prg);
    }
    
    /**
     * This method gets the list of regular semesters of the college for the program supplied.
     * If the college does not support the specified program then null is returned.
     * 
     * @param prg The program of this college whose regular semesters is to be obtained.
     * 
     * @return the regular semesters of this college's program
     */
    public puexamroutine.control.domain.list.SemesterList getSemesters( puexamroutine.control.domain.Program prg ){
        return this.ProgramsSemesters.get(prg);
    }
    
    /**
     * This method gives the list of all semester for all programs for this college
     * 
     * @return the list of all semester for all programs for this college
     */
    public puexamroutine.control.domain.Semester[] getAllSemesters(){
        return this.ProgramsSemesters.values().toArray( new puexamroutine.control.domain.Semester[]{} );
    }
    
    /**
     * This method gives the list of regular courses for the specified program
     * 
     * @param prg specified whose regular courses has to be found
     * @return list of regular course for the specified program
     */
    public java.util.List<puexamroutine.control.domain.RegularCourses> getRegularCourse( puexamroutine.control.domain.Program prg ){        
        return this.getSemesters(prg).getRegularCourses();
    }
    
    public puexamroutine.control.domain.CollegeBackCandidates getBackCandidates( final puexamroutine.control.domain.Program prg ){
        return this.BackCandidates.get(prg);
    }
    
    @Override
    public final String toString(){
        return this.Name;
    }
        
    private final String Name;
    private java.util.HashMap<puexamroutine.control.domain.Program, puexamroutine.control.domain.list.SemesterList > ProgramsSemesters = new java.util.HashMap<puexamroutine.control.domain.Program, puexamroutine.control.domain.list.SemesterList >();
    private java.util.HashMap<puexamroutine.control.domain.Program, puexamroutine.control.domain.CentreIdentifier > ProgramsCentres = new java.util.HashMap<puexamroutine.control.domain.Program, puexamroutine.control.domain.CentreIdentifier >();
    private java.util.HashMap<puexamroutine.control.domain.Program, puexamroutine.control.domain.CollegeBackCandidates> BackCandidates = new java.util.HashMap<puexamroutine.control.domain.Program,puexamroutine.control.domain.CollegeBackCandidates >();
}