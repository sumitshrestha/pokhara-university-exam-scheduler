
//                              !! RAM !!

package puexamroutine.control.routinegeneration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.Iterator;
import puexamroutine.control.domain.Day;
import puexamroutine.control.domain.Exam;
import puexamroutine.control.domain.Group;
import puexamroutine.control.domain.IndependentCourses;
import puexamroutine.control.domain.list.IndependentCourseList;
import puexamroutine.control.domain.routine.*;
import puexamroutine.control.domain.interfaces.CandidateInterface;

/**
 * This class schedules the exam into a calander.
 * The job of this class is to schedule the grouped set of regular courses after being done by the grouper.
 * It will assign the exam to each day of exam.
 *
 * @author Sumit Shresth
 */
public class ExamScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExamScheduler.class);
    
    private boolean DEBUG = false;
    
    public boolean initialize( puexamroutine.control.domain.list.GroupList GrpList, puexamroutine.control.domain.list.CentreTable CentreTab, int min_gap, int max_gap ){
        try{
            this.GroupList = GrpList;
            this.ExamCalander = new puexamroutine.control.domain.routine.ExaminationDayCalander( CentreTab );
            this.removeAll();    
            this.MAX_GAP = max_gap;
            this.MIN_GAP = min_gap;
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public void removeAll(){
        this.RegularCourseSetList.clear();
        
    }
    
    public void addRegularCourseSet( puexamroutine.control.domain.list.IndependentRegularCourseList set ){
        puexamroutine.control.domain.Group grp = set.getGroup();               
        if( !this.RegularCourseSetList.containsKey( grp ))
            this.RegularCourseSetList.put( grp , set );  
        else if(this.DEBUG)
            LOGGER.debug("setlist already contains {}", set.getGroup());
    }
    
    public void addBackCourseSet( puexamroutine.control.domain.list.IndependentCourseList BkCourse ){
        puexamroutine.control.domain.Group grp = BkCourse.getGroup();
        if( !this.BackCoursesSetList.containsKey(grp))
            this.BackCoursesSetList.put(grp, BkCourse );
        else
            LOGGER.warn("back course set already in list --> ExamScheduler");
    }
    
    public void schedule(){
        this.initializeGroupDependency();
        final puexamroutine.control.domain.Group[] SortGrp = this.GroupList.getSortedGroupForTotalCandidates();
        this.print();
        /*while( ! this.allCoursesAssigned() ){
            
        }*/
    }
    
    /**
     * This method is just the testing schedule of the exam
     * 
     * @return true if successfully scheduled else false
     * @throws java.lang.Exception if exam cannot be conducted
     */
    public boolean scheduleCourses() throws Exception {                        
        HashMap<Group,java.util.Iterator<puexamroutine.control.domain.IndependentCourses> > idpCoursesItrArray = new HashMap<Group,java.util.Iterator<puexamroutine.control.domain.IndependentCourses> >();        
        int max_Reg_Exam_Days = initializeIdpCourseItrAndGetMaxExamDay( idpCoursesItrArray, this.RegularCourseSetList );
        if ( max_Reg_Exam_Days < 0 ){
            return false;
        }
        for( int i=0; i<max_Reg_Exam_Days; i++ ){
            puexamroutine.control.domain.Day MornExam = this.ExamCalander.generateNextExamDay();
            puexamroutine.control.domain.Day DayExam = this.ExamCalander.generateNextExam();
            conductExamInDay(idpCoursesItrArray, DayExam, MornExam );            
            if( i < max_Reg_Exam_Days - 1 )
                for( int j=0; j<this.MIN_GAP; j++ ){
                    this.ExamCalander.generateNextExamDay();
                }
        }        
        return true;
    }

    private void addNewPrgRoutList(Exam ex, Group grp, Day day) {
        puexamroutine.control.domain.routine.GroupProgramRoutineList prgoutlist = new puexamroutine.control.domain.routine.GroupProgramRoutineList(grp, GroupList);
        prgoutlist.addExam(day, ex);
        this.GroupPrgRoutineListMap.put(grp, prgoutlist);
    }

    private void conductExamInDay(HashMap<Group,Iterator<IndependentCourses>> idpCoursesItrArray, Day DayExam, Day MornExam ) throws Exception {
        Iterator<Group> grpItr = idpCoursesItrArray.keySet().iterator();
        int j=0;
        while( grpItr.hasNext() ){
            Group grp = grpItr.next();
            if (idpCoursesItrArray.get( grp ).hasNext()) {
                puexamroutine.control.domain.IndependentCourses d = idpCoursesItrArray.get( grp ).next();
                java.util.Iterator<puexamroutine.control.domain.CourseCode> CoursesItr = d.getIndependentCourses().iterator();
                while (CoursesItr.hasNext()) {
                    puexamroutine.control.domain.CourseCode itrCourse = CoursesItr.next();
                    final String ExamType = this.getExamType( itrCourse, grp );
                    
                    puexamroutine.control.domain.Exam createEx = new puexamroutine.control.domain.Exam(itrCourse, ExamType);
                    
                    if( ExamType == Exam.REGULAR )
                        createEx.addExaminationSemesters(this.getSemesters(itrCourse));
                    else
                        createEx.addCandidates( this.getBackCandidates( itrCourse ) );
                    
                    if (j % 2 == 0) {
                        MornExam.conduct(createEx);
                    } else {
                        DayExam.conduct(createEx);
                    }
                    //j++;// distribute couses in same group in two shifts
                }
                j++;// distribute couses in same group in two shifts
            }
            //j++;// make all courses within same group in same shift
        }
    }
    
    /**
     * This method is used to get if the supplied course of the group is regular or not.
     * 
     * @param c course code to be checked
     * @param g group of course of which to be studied
     * 
     * @return Exam.REGULAR if c is regular course in g only else Exam.PURE_BACK
     */
    private final String getExamType( puexamroutine.control.domain.CourseCode c, Group g ){
        if( Arrays.asList( this.GroupList.getRegularCoursesArray(g) ).contains( c ) )
            return Exam.REGULAR;
        else
            return Exam.PURE_BACK;
    }
    
    private int initializeIdpCourseItrAndGetMaxExamDay( HashMap<Group,Iterator<IndependentCourses>> idpCoursesItrArray, java.util.HashMap<Group, IndependentCourseList> GroupColoredCoursesMap ) {
        int max_Exam_Days = java.lang.Integer.MIN_VALUE;
        java.util.Iterator<puexamroutine.control.domain.Group> GroupIdpCoursesListItr = GroupColoredCoursesMap.keySet().iterator();
        while (GroupIdpCoursesListItr.hasNext()) {
            Group grp = GroupIdpCoursesListItr.next();
            IndependentCourseList IdpRegCoursesList = GroupColoredCoursesMap.get(grp);
            if (max_Exam_Days < IdpRegCoursesList.getTotalIndependentCoursesGroup()) {
                max_Exam_Days = IdpRegCoursesList.getTotalIndependentCoursesGroup();
            }
            idpCoursesItrArray.put(grp,IdpRegCoursesList.getIndependentCoursesIterator());
        }
        return max_Exam_Days;
    }
    
    /**
     * This method gives the semesters of various semesters that teach the specified course.
     * 
     * @param course specified course teaching whose semesters to be found
     * @return semesters that can be found
     */
    private java.util.Collection<puexamroutine.control.domain.Semester> getSemesters( puexamroutine.control.domain.CourseCode course ) throws Exception {        
        java.util.Iterator<puexamroutine.control.domain.RegularCourses> GrpRegCoursesItr = this.GroupList.getRegularCourses( this.GroupList.getGroup(course) ).iterator();
        java.util.HashSet<puexamroutine.control.domain.Semester > Sems = new java.util.HashSet<puexamroutine.control.domain.Semester>();
        while( GrpRegCoursesItr.hasNext() ){
            puexamroutine.control.domain.RegularCourses regc = GrpRegCoursesItr.next();
            if( regc.getCourses().contains( course ) ){
                Sems.add( regc.getSemester() );
            }
        }
        return Sems;
    }
    
    public java.util.Collection<CandidateInterface> getBackCandidates( puexamroutine.control.domain.CourseCode course ) throws Exception{
        java.util.Collection<CandidateInterface> Candidates = new java.util.HashSet<CandidateInterface>();
        java.util.Iterator<CandidateInterface> CandItr = this.GroupList.getBackCandidates( this.GroupList.getGroup(course)).iterator();
        while( CandItr.hasNext() ){
            CandidateInterface Cand = CandItr.next();
            if( Cand.getBackCourses().contains(course) ){
                Candidates.add(Cand);
            }
        }
        return Candidates;
    }
    
    private void initializeGroupDependency(){
        this.GroupsDependencyList = new java.util.HashMap< puexamroutine.control.domain.Group, puexamroutine.control.schedule.GroupCentreDependency>();
        java.util.Iterator<puexamroutine.control.domain.Group> grpItr = this.GroupList.getGroups().iterator();
        while( grpItr.hasNext() ){
            puexamroutine.control.schedule.GroupCentreDependency grpDep = new puexamroutine.control.schedule.GroupCentreDependency(grpItr.next(), GroupList);
            this.addGroupsToDependencyList(grpDep);
            this.GroupsDependencyList.put( grpDep.getGroup(), grpDep );
        }
    }
    
    private void addGroupsToDependencyList( puexamroutine.control.schedule.GroupCentreDependency grp ){
        java.util.Iterator<puexamroutine.control.domain.Group> grpItr = this.GroupList.getGroups().iterator();
        while( grpItr.hasNext() ){
            puexamroutine.control.domain.Group temp = grpItr.next();
            if( temp != grp.getGroup() ){
                grp.add(temp, this.getOverlappingCentres( temp, grp.getGroup() ) );
            }
        }
    }
    
    private int isCalculated( puexamroutine.control.domain.Group grp1, puexamroutine.control.domain.Group grp2 ){
        if( this.GroupsDependencyList == null )return -1;
        if( this.GroupsDependencyList.containsKey(grp1)){
            int temp = onExist(grp1, grp2 );
            if( temp > -1 ){
                return temp;
            }
        }
        else
            if( this.GroupsDependencyList.containsKey(grp2)){
                int temp = onExist(grp2, grp1 );
                if( temp > -1 ){
                    return temp;
                }
            }            
        return -1;
    }
    
    private int onExist(Group grp1, Group grp2 ) {
        return this.GroupsDependencyList.get(grp1).getNetOverlappingCentres(grp2);        
    }
    
    private java.util.HashSet<puexamroutine.control.domain.CentreIdentifier> calculateOverlappingCentres( puexamroutine.control.domain.Group grp1, puexamroutine.control.domain.Group grp2 ){
        java.util.HashSet< puexamroutine.control.domain.CentreIdentifier > Cent1 = this.GroupList.getCentres(grp1);
        java.util.Iterator< puexamroutine.control.domain.CentreIdentifier > Cent2Itr = this.GroupList.getCentres(grp2).iterator();        
        java.util.HashSet< puexamroutine.control.domain.CentreIdentifier > IntersectSet = new java.util.HashSet<puexamroutine.control.domain.CentreIdentifier>();
        while( Cent2Itr.hasNext() ){
            puexamroutine.control.domain.CentreIdentifier centre = Cent2Itr.next();
            if( centre == null ){
                continue;
            }
            if( Cent1.contains( centre ) ){
                IntersectSet.add(centre);
            }
        }
        return IntersectSet;
    }
    
    private int getOverlappingCentres( puexamroutine.control.domain.Group grp1, puexamroutine.control.domain.Group grp2 ){
        int result = this.isCalculated(grp1, grp2);
        if( result > -1 )return result;
        return this.calculateOverlappingCentres(grp1, grp2).size();
    }

    private void print(){
        LOGGER.info("printing dependency");
        java.util.Iterator<puexamroutine.control.domain.Group> grpitr = this.GroupsDependencyList.keySet().iterator();
        while( grpitr.hasNext() ){
            puexamroutine.control.domain.Group grp = grpitr.next();
            LOGGER.info("{}:{};{}", grp.getFaculty(), grp.getLevel(), grp.getDiscipline());
            java.util.Iterator<puexamroutine.control.domain.Group> sortgrp = this.GroupsDependencyList.get(grp).getSortedOptimalGroup().iterator();
            while( sortgrp.hasNext() ){
                grp = sortgrp.next();
                LOGGER.info("{}:{};{}", grp.getFaculty(), grp.getLevel(), grp.getDiscipline());
            }
        }
    }
    
    public final puexamroutine.control.domain.routine.ExaminationDayCalander getCalander(){
        return this.ExamCalander;
    }
    
    public void calculateGroupPrgRoutine() throws Exception{
        this.GroupPrgRoutineListMap.clear();
        java.util.Iterator<puexamroutine.control.domain.ExamDayIdentifier> ExamDayItr = this.ExamCalander.getCalanderIterator();
        while( ExamDayItr.hasNext() ){
            puexamroutine.control.domain.Day day = this.ExamCalander.getExamDay( ExamDayItr.next() );
            java.util.Iterator<puexamroutine.control.domain.Exam> ExamItr = day.getExams().iterator();
            while( ExamItr.hasNext() ){
                puexamroutine.control.domain.Exam ex = ExamItr.next();
                puexamroutine.control.domain.Group grp = this.GroupList.getGroup( ex.getExamCourse() );
                if( this.GroupPrgRoutineListMap.containsKey( grp ) ){
                    puexamroutine.control.domain.routine.GroupProgramRoutineList prgroutlist = this.GroupPrgRoutineListMap.get( grp );
                    prgroutlist.addExam(day, ex);
                }
                else{
                    addNewPrgRoutList(ex, grp,day);
                }
            }
        }
    }
    
    public java.util.Collection<puexamroutine.control.domain.routine.ProgramRoutine> getAllProgramRoutine(){
        java.util.Iterator<puexamroutine.control.domain.Group> grpItr = this.GroupPrgRoutineListMap.keySet().iterator();
        java.util.HashSet<puexamroutine.control.domain.routine.ProgramRoutine> prgRout = new java.util.HashSet<puexamroutine.control.domain.routine.ProgramRoutine>();
        while( grpItr.hasNext() ){
            puexamroutine.control.domain.Group grp = grpItr.next();
            puexamroutine.control.domain.routine.GroupProgramRoutineList temp = this.GroupPrgRoutineListMap.get( grp );
            if( temp != null )
                prgRout.addAll( temp.getAllProgramRoutine() );
            else if(this.DEBUG)
                LOGGER.debug("program routine list for {}:{}:{} was null. So, skipping", grp.getFaculty(), grp.getLevel(), grp.getDiscipline());
        }
        return prgRout;
    }
    
    public void printAllRoutines(){
        java.util.Iterator<puexamroutine.control.domain.routine.ProgramRoutine> PrgRoutItr = this.getAllProgramRoutine().iterator();
        while( PrgRoutItr.hasNext() ){
            PrgRoutItr.next().print();
        }
    }
    
    public java.util.Collection<GroupProgramRoutine> getGroupRoutine(){
        return Arrays.asList( this.GroupPrgRoutineListMap.values().toArray(new GroupProgramRoutine[]{}) );
    }
    
    public java.util.Collection<GroupProgramRoutineList> getGroupProgramRoutine(){
        /**
         * ERROR(FOR REMEMBER AND LEARNING. SEE COMMENTED STATEMENT) :: as HashMap.values() is not serialized (viz is bug), this method created problem when used in returning Result by server to client while marshalling Result.
         * So, this method was cancelled and replaced by following uncommented statement.
         * Guys, this created a lot of problem to me a week long, (longer than any error till now ), so, stuck it in mind and avoid such deadly error. 
         * If some one can send it to SUN MICRO. INC, to report it as bug and make inner class java.util.HashMap$Values implement java.io.Serializable in addition to java.util.AbstractCollection
         */
        //return this.GroupPrgRoutineListMap.values();
        
        /**
         * Here, is the right and greatest code
         * I have simply added values (viz already a collection (but unserialized collection unfortunately (ASK JAVA DESIGHERS WHY THEY DID IT !! ))) to newly created arrayList object (viz serialized one fortuntely)
         * This is waste of time and memeory but Java is hit for such scoopes
         */        
        return new java.util.ArrayList( this.GroupPrgRoutineListMap.values() );
    }
    
    private java.util.HashMap<puexamroutine.control.domain.Group, puexamroutine.control.domain.list.IndependentCourseList> RegularCourseSetList = new java.util.HashMap<puexamroutine.control.domain.Group, puexamroutine.control.domain.list.IndependentCourseList>();
    private java.util.HashMap< puexamroutine.control.domain.Group, puexamroutine.control.domain.list.IndependentCourseList > BackCoursesSetList = new java.util.HashMap<puexamroutine.control.domain.Group,puexamroutine.control.domain.list.IndependentCourseList>();    
    private puexamroutine.control.domain.routine.ExaminationDayCalander ExamCalander = null;
    private puexamroutine.control.domain.list.GroupList GroupList = null;
    private int MIN_GAP, MAX_GAP;
    private java.util.HashMap<  puexamroutine.control.domain.Group, puexamroutine.control.schedule.GroupCentreDependency> GroupsDependencyList=null;
    private java.util.HashMap<puexamroutine.control.domain.Group, puexamroutine.control.domain.routine.GroupProgramRoutineList> GroupPrgRoutineListMap = new java.util.HashMap<puexamroutine.control.domain.Group, puexamroutine.control.domain.routine.GroupProgramRoutineList    >();
}