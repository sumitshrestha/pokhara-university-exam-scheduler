
//                                  !! RAM !!

package puexamroutine.control.database;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import puexamroutine.control.domain.*;
import puexamroutine.control.domain.list.*;

/**
 * This class represents the data holder for processing by the system.
 * It will initialize entire domain lists and their objects by reading from the database.
 * It will also hold them for use by control classes.
 * So, its responsiblity is to encapsulate all necessary info for PU exam routine generation and provide when needed.
 * 
 * @author Sumit Shresth
 */
public class DataHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataHolder.class);
    
    public DataHolder(){                        
    }
    
    private void initialize(){
        this.CollegeList.removeAll();
        this.ExamCentreList.removeAll();
        this.GroupList.removeAll();
    }
    
    public UniversityDataBean read( puexamroutine.control.database.DatabaseReader dbrd ) throws Exception {        
        this.initialize();
        this.DbReader = dbrd;
        boolean state = this.initializeExamCentreList();
        if( ! state )
            LOGGER.warn("Center list could not be initialized");
        state = this.initializeCollegeList();
        if( ! state )
            LOGGER.warn("College list could not be initialized");
        state = this.initializeGroupList();
        if( ! state )
            LOGGER.warn("Group list could not be initialized");
        state = this.initializeCourseList();
        if( ! state )
            LOGGER.warn("Courses list could not be initialized");
        
        return new UniversityDataBean( this.GroupList, this.ExamCentreList, this.CollegeList );
    }
    
    private boolean addGroupCourses( puexamroutine.control.domain.Group grp ){
        try{
//            String[] RegCourses = this.DbReader.readRegularCourses( grp.getFaculty(), grp.getLevel(), grp.getDiscipline() );
  //          String[] BackCourses = this.DbReader.getGroupBackPapers( grp.getFaculty(), grp.getLevel(), grp.getDiscipline() );
            //GroupCourseList list = createCourseList(BackCourses, grp, RegCourses);
            GroupCourseList list = this.createGroupCourseList(grp);            
            this.GroupList.addCourses(grp, list);
            return true;
        }
        catch( Exception e ){
            LOGGER.error("Failed to add courses for group {}:{}:{}", grp.getFaculty(), grp.getLevel(), grp.getDiscipline(), e);
            return false;
        }
    }
    
    private void addColleges( Program prg ) {
        String[] Colleges = this.DbReader.readCollegesOfProgram( prg.getProgramName() );
        for (int j = 0; j < Colleges.length; j++) {
            puexamroutine.control.domain.College Coll = this.CollegeList.getColleges(Colleges[j]);            
            SemesterList SemList = createSemesterList( Coll, prg);
            String Cent = this.DbReader.readCentre( prg.getProgramName(), Coll.getCollegeName() );
            if( Cent == null ){
                continue;
            }
            CentreIdentifier CentID = this.ExamCentreList.getCentre(Cent);
            if( CentID == null )                
                continue;            
            Coll.addPrograms_Center_Semesters_Back( prg, CentID, SemList, null );// regular subjects of college are only initialized since back candidates use it later for initializing them
            CollegeBackCandidates BackCandidates = createBackCandidates( Coll, prg );
            Coll.removeProgram(prg);//remove the incomplete program initialization
            Coll.addPrograms_Center_Semesters_Back( prg, CentID, SemList, BackCandidates );//now make complete initialization with back candidates too.
            prg.addCollege(Coll);
        }
    }

    /**
     * This method creates a back candidate object of specifed candidate ID of specifeid program
     * This method puts only non regular subjects.
     * This is mainly done since regular subjects though back paper for specified candidates are already scheduled.
     * So, only those sujects that are not regular are taken into account.
     * This reduces processing necessity and simplifies code too.
     * 
     * @param prg specifeid program of specified candidate to be created
     * @param Cand specified candidate ID to be created
     * @param Semester specified semester of candidate
     * 
     * @return candidate object of specified program and specified candidate ID
     */
    private Candidate createBackCandidate( String Cand,Program prg, College college, int Semester  ) {
        Candidate BkCand = new Candidate(Cand, prg, college ,Semester , (Semester == Candidate.NON_REGULAR)?Candidate.BACK_ONLY:Candidate.REGULAR_WITH_BACK );
        String[] BkCourses = this.DbReader.getCandidateBackPapers( Cand , college.getCollegeName(), prg.getProgramName()  );
        // simply back papers of this candidate is added whether it may be regular subject of some other programs or other semester of this candidate program
        for (int j = 0; j < BkCourses.length; j++) {
            puexamroutine.control.domain.list.GroupCourseList list = this.GroupList.getCourseList( prg.getGroup() );            
            CourseCode Course = list.getBackCourse( BkCourses[j] );
            BkCand.addBackPaper(Course);            
        }
        
        //get regular subjects if any
        if( BkCand.getState() == BkCand.REGULAR_WITH_BACK ){
            Collection<RegularCourses> regs = college.getRegularCourse(prg);
            Iterator<RegularCourses> regItr = regs.iterator();
            while( regItr.hasNext() ){
                RegularCourses reg = regItr.next();
                if( Semester == Integer.parseInt( reg.getSemester().getSemester() ) ){
                    BkCand.addRegualarPapers( reg.getCourses() );
                    break;
                }
            }
        }
        return BkCand;
    }

    /**
     * This method creates back paper candidates for the specified college of specified program.
     * The list contains only those candidates that have pure back subject.
     * Pure back subjects are those that are not regular subjects of any program in exams.
     * So, such candiates are choosen and included in the list.
     * 
     * @param Coll The specified college whose back candidates is to be calculated
     * @param prg the program of college whose back candidates is to be calculated
     * @return the back candidates each having non zero pure back subjects
     */
    private CollegeBackCandidates createBackCandidates( College Coll, Program prg) {
        puexamroutine.control.domain.CollegeBackCandidates BackCandidates = new puexamroutine.control.domain.CollegeBackCandidates(Coll, prg);
        HashMap<String, Integer> Candidates = this.DbReader.getBackPaperCandidates( Coll.getCollegeName(), prg.getProgramName());
        Iterator<String> CandidateItr = Candidates.keySet().iterator();
        while( CandidateItr.hasNext() ){
            String cand = CandidateItr.next();
            int Sem = Candidates.get( cand );
            Candidate BkCand = createBackCandidate( cand, prg ,Coll, Sem );
            if( BkCand.hasPureBackPapers() )
                BackCandidates.addBackPaperCandidate( BkCand );
        }
        return BackCandidates;
    }
    
    private GroupCourseList createGroupCourseList( Group grp ) {
        puexamroutine.control.domain.list.GroupCourseList list = new puexamroutine.control.domain.list.GroupCourseList(grp);
        String[] courses = this.DbReader.readGroupCourses( grp.getFaculty(), grp.getLevel(), grp.getDiscipline() );
        list.addCourses(courses);
        
        // set regular and back courses
        String[] reg = this.DbReader.readRegularCourses( grp.getFaculty(), grp.getLevel(), grp.getDiscipline() );
        String[] back = this.DbReader.getGroupBackPapers( grp.getFaculty(), grp.getLevel(), grp.getDiscipline() );
        
        list.addRegularCourses(reg);
        list.addBackCourses(back);
        
        return list;
    }
    
    private void createNaddPrgramtoProgramList(ProgramList prglist, Group grp, String programs) {
        puexamroutine.control.domain.Program prg = new puexamroutine.control.domain.Program(programs, grp);
        addColleges( prg );
        prglist.addProgram(prg);
    }

    private RegularCourses createRegularCourse(String Sems, College Coll, Semester Sem, Program prg ) {
        java.util.HashMap<String, java.lang.Integer> RegularSubs = this.DbReader.readCoursesNTotalStudent(prg.getProgramName(), Coll.getCollegeName(), Sems);
        HashMap<CourseCode, Integer> RegCourseMap = getRegularCourseMap(prg, RegularSubs);
        puexamroutine.control.domain.RegularCourses RegularCourse = new puexamroutine.control.domain.RegularCourses(Coll, prg, Sem, RegCourseMap);
        return RegularCourse;
    }

    /**
     * This method creates one semester object for the specified semester of College's program.
     * 
     * @param Coll specified college whose semester is to be created
     * @param prg specified program of college
     * @param Sems specified semester whose domain object is to created
     * 
     * @return semester object of specified parameters
     */
    private Semester createSemester( College Coll, Program prg, String Sems ) {
        puexamroutine.control.domain.Semester Sem = new puexamroutine.control.domain.Semester(Sems, Coll, prg);
        RegularCourses RegularCourse = createRegularCourse(Sems, Coll, Sem, prg);
        Sem.setRegularCourses(RegularCourse);
        return Sem;
    }

    /**
     * This method creates the list of semeseters for the specified program of the specified college.
     * 
     * @param Coll specified college whose semester has to be read
     * @param prg specified program of college whose semester has to be read
     * @return list of semester of specified college 
     */
    private SemesterList createSemesterList( College Coll, Program prg ) {
        String[] Sems = this.DbReader.readSemesters( prg.getProgramName(), Coll.getCollegeName() );
        puexamroutine.control.domain.list.SemesterList SemList = new puexamroutine.control.domain.list.SemesterList(Coll, prg);
        for (int r = 0; r < Sems.length; r++) {
            Semester Sem = createSemester(Coll, prg, Sems[r]);
            SemList.addSemester(Sem);
        }
        return SemList;
    }
    
    private HashMap<CourseCode, Integer> getRegularCourseMap(Program prg, HashMap<String, Integer> RegularSubs) {
        java.util.HashMap<puexamroutine.control.domain.CourseCode, java.lang.Integer> RegCourseMap = new java.util.HashMap<puexamroutine.control.domain.CourseCode, java.lang.Integer>();
        java.util.Iterator<String> RegItr = RegularSubs.keySet().iterator();
        while (RegItr.hasNext()) {
            String c = RegItr.next();
            RegCourseMap.put(this.GroupList.getCourseList(prg.getGroup()).getRegularCourse(c), RegularSubs.get(c));
        }
        return RegCourseMap;
    }
    
    /**
     * This method initializes the exam centre list.
     * 
     * @return true if successfully initialized else false on any error
     */
    private boolean initializeExamCentreList(){
        try{
            java.util.HashMap<String,java.lang.Integer> cenlist = this.DbReader.getCentres();
            java.util.Iterator<String> cenlistItr = cenlist.keySet().iterator();
            while( cenlistItr.hasNext() ){
                String cen = cenlistItr.next();
                this.ExamCentreList.addCentre( new puexamroutine.control.domain.CentreIdentifier(cen, cenlist.get(cen) ));
            }
            return true;
        }
        catch( Exception e ){
            LOGGER.error("Failed to initialize exam centre list", e);
            return false;
        }
    }

    /**
     * This method initializes the college list.
     * 
     * @return true if successfully initialized else false on any error
     */
    private boolean initializeCollegeList(){
        try{
            String[] collist = this.DbReader.readRegularColleges();
            for( int i=0 ;i <collist.length; i++ ){
                this.CollegeList.addCollege( new puexamroutine.control.domain.College( collist[i] ) );
            }
            return true;
        }
        catch( Exception e ){
            LOGGER.error("Failed to initialize college list", e);
            return false;
        }
    }

    /**
     * This method initializes the specified group object
     * 
     * @param grp specified group to be initialized
     * @return true if successfull else false
     */
    private boolean initializeGroup( Group grp) {
        this.addGroupCourses(grp);
        puexamroutine.control.domain.list.ProgramList prglist = createProgramList(grp);
        if (prglist != null) {
            this.GroupList.addProgram(grp, prglist);
        } else {
            LOGGER.warn("Program list could not be created for group {}:{}:{}", grp.getFaculty(), grp.getLevel(), grp.getDiscipline());
            return false;
        }
        return true;
    }
    
    /**
     * This method initializes the group list.
     * 
     * @return true if no eror while initializing else false
     */
    private boolean initializeGroupList(){
        try{
            puexamroutine.control.domain.Group[] grps = getExamGroups();            
            if( grps == null ){
                LOGGER.error("Exam groups could not be loaded");
                return false;
            }
            for( int i=0; i<grps.length; i++ ){
                if( ! initializeGroup( grps[i] ) ) // could not be initialized
                    return false;                
            }
            return true;
        }
        catch( Exception e ){
            LOGGER.error("Failed to initialize group list", e);
            return false;
        }
    }
    
    private boolean initializeCourseList() throws Exception {        
        return this.GroupList.setCourses( this.DbReader.readCourses() );        
    }
    
    /**
     * This method returns the list of exam groups that can be scheduled separately.
     * 
     * @return list of groups which can uniquely scheduled
     */
    private puexamroutine.control.domain.Group[] getExamGroups(){
        try{
            java.util.HashSet<puexamroutine.control.domain.Group> grps = new java.util.HashSet<puexamroutine.control.domain.Group>();
            
            String[] FacList = this.DbReader.readFaculties();
            for( int i=0;i<FacList.length; i++ ){
                String[] level = this.DbReader.readLevels( FacList[i] );
                for( int j=0; j<level.length; j++ ){
                    String[] disc = this.DbReader.readDisciplines( FacList[i], level[j] );
                    for( int r = 0; r<disc.length; r++ ){
                        grps.add( new puexamroutine.control.domain.Group( FacList[i], level[j], disc[r] ) );                        
                    }
                }
            }
            
            return grps.toArray( new puexamroutine.control.domain.Group[]{} );
        }
        catch( Exception e ){
            LOGGER.error("Failed to read exam groups from database", e);
            return null;
        }
    }
    
    /**
     * This method creates a list of programs for the specified exam group.
     * 
     * @param grp the group whose program list is to be obtained
     * @return list of programs for the specified group
     */
    private puexamroutine.control.domain.list.ProgramList createProgramList(puexamroutine.control.domain.Group grp ){
        try{
            puexamroutine.control.domain.list.ProgramList prglist = new puexamroutine.control.domain.list.ProgramList(grp);
            String[] programs = this.DbReader.readPrograms( grp.getFaculty(), grp.getLevel(), grp.getDiscipline() );
            for( int i=0 ;i<programs.length; i++ ){
                createNaddPrgramtoProgramList(prglist, grp, programs[i] );
            }
            return prglist;
        }
            catch( Exception e ){
            LOGGER.error("Failed to create program list for group {}:{}:{}", grp.getFaculty(), grp.getLevel(), grp.getDiscipline(), e);
            return null;
        }
    }
    
    private puexamroutine.control.database.DatabaseReader DbReader;
    
    // domain lists
    private puexamroutine.control.domain.list.GroupList GroupList=new puexamroutine.control.domain.list.GroupList();
    private puexamroutine.control.domain.list.CentreTable ExamCentreList = new puexamroutine.control.domain.list.CentreTable();
    private puexamroutine.control.domain.list.CollegeList CollegeList = new puexamroutine.control.domain.list.CollegeList();    
}