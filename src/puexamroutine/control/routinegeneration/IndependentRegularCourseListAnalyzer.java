
//                          !! RAM !!

package puexamroutine.control.routinegeneration;

import java.util.Iterator;
import puexamroutine.control.domain.RegularCourses;
import puexamroutine.control.interfaces.DomainListener;

/**
 * This class is for anlysing the list of set of independent courses.
 * It checks how the exams for each semester of each college is arranged.
 * It checks if the set of courses are feasible or not.
 * It also checks if the group of courses are too large or too small.
 *
 * @author Sumit Shresth
 */
public class IndependentRegularCourseListAnalyzer implements puexamroutine.control.routinegeneration.graph.domain.interfaces.GraphColoringSolutionAnalyzer{
    
    private boolean DEBUG = false;
    
    /**
     * This method initialzes the state of analyzer for analyzing each group of regular courses.
     * This mehod is called whenever a new group of courses has to be checked.
     * One of important job of this method is to create the list of regular course whose placement can be checked after each color combination generation by the graph colorer.
     * Since, The regular course may repeat mainly for same program of various colleges, They must be eliminated.
     * This job cannot be done at each graph coloring step.
     * Here, it is done at the beginning of graph coloring.
     * So, The list can be used after each graph coloring for feasiblity and optimality of solution.
     * This is very essential for performance of algo.
     * 
     * @param c An array of courses for analyzing
     * @param Reg The list of group of regular coureses for each semester.
     */
    public void intiialize( final puexamroutine.control.domain.CourseCode[] c, final java.util.HashSet<puexamroutine.control.domain.RegularCourses> Reg, int min, int max ,DomainListener User ,int MaxtimeUp ){
        this.Courses = c;
        this.User = User;
        this.MAXIMUM_TIME_OUT_TIME = MaxtimeUp;
        this.OptimalColoredCoursesRecord = null;
        puexamroutine.control.domain.Group grp = Reg.iterator().next().getProgram().getGroup();
        this.ColoredCoursesList = new puexamroutine.control.domain.list.IndependentRegularCourseList(grp);
        
        // here, only non repeating regular courses are added see more on docs for it
        this.ColoredCoursesList.addRegularCourses(Reg);     
        this.CurrentColoredCoursesVariance = this.OptimalColoredCoursesVariance = java.lang.Double.MAX_VALUE;
        this.MAX_DISP = max;
        this.MIN_DISP = min;        
        //this.ColoredCoursesList.print();
    }
    
    /**
     * This method checks if each set of courses within a semester are adjacant in the graph.
     * This has been mainly done for alowing regular course of each semester does not cross maximum limit.
     * So, keeping each regular courses one after another ensures this.
     * For some semesters, the first group may have any of its coures. So, such system must have the last exam on the ith group of colors where i is the total amount of courses in that semester.
     * To generalize, if for any semester x, the first appearing exam for it is e on yth group of exam, then the last exam for x must be on (y+z)th group where z is the total courses within x.
     * 
     * @param GroupedCourses the group of exam that can be carried on same day with other necessary details of grouping.
     * @return true if all semesters are feasible with respect to serialness.
     */
    public boolean isFeasible( puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution GroupedCourses ){                
        this.Solution = GroupedCourses;
        IndependentCourseCreator.createListOfSameColoredCourseCodes(GroupedCourses, Courses, this.ColoredCoursesList , this.DEBUG );
        return this.generateCombinationAndCheckFeasibility();
    }
    
    /**
     * This method checks if the set of group of regular courses are perfectly fit for use or not.
     * The group os courese must be feasible for its uses i.e. all exams in each of semester must be serial.
     * It will use variance to find optimality.
     * For finding the best group it uses opitmal veriance value to compare with variance of supplied group.
     * 
     * @param GroupedSerialCourses
     * @return true if the set of exam can be perfectly conducted else false
     */
    public boolean isOptimal( puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution GroupedSerialCourses ){                
        //return this.CurrentColoredCoursesRecord.getTotalCourses() == 0;
        return true;        
        //return this.User.isCancelled() || this.didTimeUp();        
    }
    
    /**
     * This method checks if the new group of courses is better than older group of courses.
     * It will check the variance value of each. If variance of new value is less than older than it is more optimal than previous.
     * 
     * @param NewGroupedSerialCourses list of new set of regular courses
     * @param OldGroupedSerialCourses list of old set of regular courses
     * @return true if new group is better than older else false
     */
    public boolean isMoreOptimal( puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution NewGroupedSerialCourses, puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution OldGroupedSerialCourses ){                
        if ( this.isBetter( CurrentColoredCoursesRecord, this.OptimalColoredCoursesRecord ) ){            
            this.OptimalColoredCoursesRecord = this.CurrentColoredCoursesRecord;
            this.OptimalColoredCoursesVariance = this.getCurrentSolutionVariance();
            return true;
        }
        else
            return false;
    }
    
    private puexamroutine.control.routinegeneration.RegularCoursesStatisticsList createRegularCourseStatisticList(Iterator<RegularCourses> RegCourseItr) {        
        puexamroutine.control.routinegeneration.RegularCoursesStatisticsList list = new puexamroutine.control.routinegeneration.RegularCoursesStatisticsList();
        while (RegCourseItr.hasNext()) {
            RegularCourses reg = RegCourseItr.next();            
            puexamroutine.control.routinegeneration.RegularCoursesStatistics st = this.getRegularCoursesStatistics( reg.getCourses());            
            //if (!st.isSerial()) {
                list.addRegularCoursesStatistic(st);
            //}
        }                
        return list;
    }

    private void debug_feasibility(int index_gap, int actual_gap) {
        if (this.DEBUG ){//&& !(actual_gap < this.MAX_DISP)) {
            System.out.println("\n\n\tNOTE FEASIBILITY \nFeasibility Debug");
            System.out.println("index_gap(d):" + index_gap);
            System.out.println("actual_gap:" + actual_gap);
        }
    }

    /**
     * This method is for calculating the maximum close index displacement for the specified regular course.
     * here there is no regular course as such but the array of index is for this course.
     * 
     * @param courses_index_array the array of index 
     * @return maximum displacement of close 
     */
    private int getMaximumCloseIndexDisplacementForRegularCourse(int[] courses_index_array) {
        int Max_Close_Index_Disp = java.lang.Integer.MIN_VALUE;
        if(this.DEBUG){System.out.println("before");for(int i=0; i<courses_index_array.length; i++ )System.out.print(courses_index_array[i]+",");}
        java.util.Arrays.sort(courses_index_array);
        if(this.DEBUG){System.out.println("before");for(int i=0; i<courses_index_array.length; i++ )System.out.print(courses_index_array[i]+",");}
        for (int j = 0; j < courses_index_array.length - 1; j++) {
            int temp_Disp = java.lang.Math.abs(courses_index_array[j+1] - courses_index_array[j]);
            if (temp_Disp > Max_Close_Index_Disp) {
                Max_Close_Index_Disp = temp_Disp;
                if(this.DEBUG)System.out.println("new assignment to max "+temp_Disp);
            }
        }
        return Max_Close_Index_Disp;
    }
    
    /////////////////////////////////////////////////////////////////////////////
    ////////////////    CODE FOR FEASIBLITY     ////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    
    private final puexamroutine.control.routinegeneration.RegularCoursesStatistics getRegularCoursesStatistics( java.util.Collection<puexamroutine.control.domain.CourseCode> Reg ){
        puexamroutine.control.routinegeneration.RegularCoursesStatistics Data = new puexamroutine.control.routinegeneration.RegularCoursesStatistics( Reg );
        java.util.Iterator<puexamroutine.control.domain.CourseCode> RegItr = Reg.iterator();
        int[] courses_index_array = new int[ Reg.size() ];
        int i=0;
        if( RegItr.hasNext() ){
            puexamroutine.control.domain.CourseCode c = RegItr.next();            
            
            int min= this.ColoredCoursesList.getIndex(c);
            int max = min;        
            if(this.DEBUG)System.out.println("index of "+c.toString()+" is "+ min);
            courses_index_array[i] = min;
        
        while( RegItr.hasNext() ){            
            puexamroutine.control.domain.CourseCode c1 = RegItr.next();            
            int index = this.ColoredCoursesList.getIndex(c1);
            courses_index_array[++i] = index;
        if(this.DEBUG)System.out.println("index of "+c1.toString()+" is "+ index);
            if( index == -1 )
                continue;
            
            if( min > index ){
                min = index;
            }
            else
                if( max < index ){
                    max = index;
                }
        }
            Data.setMaxIndex(max);
            Data.setMinIndex(min);
            
            int Max_Close_Index_Disp = getMaximumCloseIndexDisplacementForRegularCourse(courses_index_array);
            if(this.DEBUG ) System.out.println("the value of maximum being set is "+Max_Close_Index_Disp);
            Data.setMaxCloseCourseIndexDisplacement( Max_Close_Index_Disp  );
            
            return Data;
        }
        
        return null;
        
    }
    
    /**
     * This method will check the feasibility of the supplied list.
     * Since, Using Optimization func created some complexity, Simply maximum displacement value is used here
     * The feasiblity function is just to check that given solution has maximum displacement that crosses maximum gap allowed 
     * This is very important step that involves the maximum gap constraint too.
     * The displacement is maximum among difference between index of any two pair of close courses in regular courses of the group being considered presently
     * About close course read its documentation in regualarcoursesstatistic
     * THIS MAXIMUM DISPLACEMENT WILL REPRESENT THE MAXIMUM GAP THAT CAN OCCUR DUE TO THIS COLOR COMBINATION
     * it is difference of index but NOT ACTUAL GAP IN DAYS.
     * So, ACTUAL GAP HAS TO BE CALCULATED
     * using this actual gap value it will find feasibility using the relation
     * The derivation of relation is::
     * suppose d = maximum displacement of index i talked above,
     *         min = minimum allowed gap(supplied by user),
     *         max = maximum allowed gap(supplied by user)
     * first, this index gap has to be converted into actual gap
     * if we take the breaks due to minimum gap into account, then the gap days will be min * d i.e. no_of_days_due_min_gap = min * d
     * for eg. consider 1 min 2 where 1 n 2 are index of two close course and min is days of gap beteen them. so, here d=1 i.e. 2-1 = 1 then gap in days between them will be 1 * min = min days as is visible clearly
     * but this is not sufficient. if d > 1, then there arrives other exams not of this regular course however
     * for eg. 1 min 2 min 3 where 1 n 3 are index of two close course of regular course reg say BESE 3rd semester
     * if we use above relation then actual gap becomes (3-1)*min = 2min
     * but here, actual gap is min + 1 + min = 2min + 1 since 2 represents one day of exam
     * clearly it is not the relation gives the actual gap. so, days of exam in between two course has to be taken into account.
     * the no of indices that appear in between (index) exam of close courses will be 1 less than index difference i.e. d -1 since the last exam is that of second close course which has to be discarded from consideration since we want to find no of indices no between them
     * so we have to add (d -1) to above relation viz gap due to intermediate exams i.e. num_of_intermediate_exam_between_close_courses = d -1
     * 
     * now we get actual gap by addition of two
     * so, Actual gap = no_of_days_due_min_gap + num_of_intermediate_exam_between_close_courses = min * d + ( d -1 ) = d * min + d -1 = d( min + 1 ) -1
     * 
     * Now it must not cross the maximum allowable days of gap between two close course. if it does than this is not feasible solution
     * So, my feasible function is nothing but Actual gap <= max 
     * but for just some optimization i remove = and make it actual gap < max so actual gap will be less than maximum gap
     * 
     * Here, one exception may arise. if only one course exists in each semester of programs in present group i.e. d < 1 (d = Integer.MIN_VAL ) this does arise if there is only one course in each semester of present group.
     * So, in that case only one color is assigned viz not wrong. This satisfies the feasibilty condition n is rightly called feasible
     * So, True is being returned in such cases.
     * 
     * @param list The specified list whose optimization value is to be checked.
     * @return true if list is feasible else false.
     */
    private boolean checkFeasibility( puexamroutine.control.routinegeneration.RegularCoursesStatisticsList list ){        
        /*
         * Outdated code has bug
         * The bugs are:
         * 1. it uses getMaxDisplacement which gives the maximum displacement of extreme courses and not close courses
         * 2. it uses wrong relation the actual relation is given down
         *  d*(min + 1 ) - 1 < max
         */
        //return list.getMaxExtremeCourseDisplacement() * ( 1 + this.MIN_DISP ) < this.MAX_DISP;
        
        /*
         * 19-8-2008 addition
         */
        int index_gap = list.getMaximumCloseIndexDisplacement();        
        if( index_gap == java.lang.Integer.MIN_VALUE ){
            if(this.DEBUG)System.out.println("\n\nindex gap is minimum so returning...");
            return true;// see up in docs for exceptions
        }
        int actual_gap = index_gap * ( this.MIN_DISP + 1 ) - 1;
        debug_feasibility(index_gap, actual_gap);
        return actual_gap < this.MAX_DISP;        
    }
    
    public puexamroutine.control.routinegeneration.RegularCoursesStatisticsList getOptimalSolution(){
        return this.OptimalColoredCoursesRecord;
    }
    
    //////////////////////////////////////////////////////////////////////////////
    /////////////////   CODE FOR OPTIMIZATION   //////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    
    /**
     * This method checks compares the two specified lists.
     * It simply compares the maximum displacement and total non serial( displayed ) groups.
     * For present value to be better than older value, both the values must be lesser.
     * Initially, Optimization func was being used. But, There was serious problem with finding a proper optimization func.
     * 
     * @param NewGroupingRec The first list to be checked for betterness. Mainly, It is the newly assigned color list.
     * @param OldGroupingRec The second list to be checked for betterness. Mainly, it is previous assigned color list.
     * @return true if new list is better than older list.
     */
    private boolean isBetter( puexamroutine.control.routinegeneration.RegularCoursesStatisticsList NewGroupingRec, puexamroutine.control.routinegeneration.RegularCoursesStatisticsList OldGroupingRec ){                
        return OldGroupingRec == null || this.CurrentColoredCoursesRecord.getTotalCourses() < this.OptimalColoredCoursesRecord.getTotalCourses() && this.CurrentColoredCoursesRecord.getMaxExtremeCourseDisplacement() < this.OptimalColoredCoursesRecord.getMaxExtremeCourseDisplacement() && this.getCurrentSolutionVariance() < this.OptimalColoredCoursesVariance;
    }
    
    private double getCurrentSolutionVariance(){
        if( this.CurrentColoredCoursesVariance == java.lang.Double.MAX_VALUE )this.CurrentColoredCoursesVariance = this.calculateCurrentSolutionVariance(Solution);
        return this.CurrentColoredCoursesVariance;
    }
    
    private double calculateCurrentSolutionVariance( puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution GroupedCourses ){
        java.util.HashMap<java.lang.Integer, java.util.HashSet<java.lang.Integer> > GroupedCoursesIndex = GroupedCourses.getGroupedVerticesIndexes();
        java.util.Iterator<java.lang.Integer> GroupedCoursesIndexItr = GroupedCoursesIndex.keySet().iterator();
        double sec = 0, first = 0;
        while( GroupedCoursesIndexItr.hasNext() ){
            double disp = GroupedCoursesIndex.get( GroupedCoursesIndexItr.next() ).size();
            sec += disp;
            first += disp * disp;
        }
        double sec1 = sec / GroupedCoursesIndex.size();
        return first/GroupedCoursesIndex.size() - sec1 * sec1;        
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////     INTERFACES      //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    
    public puexamroutine.control.domain.list.IndependentRegularCourseList getIndependentListOfCourses(){
        return this.ColoredCoursesList;
    }
    
    // initial fields    
    private int MAX_DISP, MIN_DISP;
    private puexamroutine.control.domain.CourseCode[] Courses;
    private puexamroutine.control.domain.list.IndependentRegularCourseList ColoredCoursesList = null;
 //ColoredCoursesList = null;
    private puexamroutine.control.routinegeneration.RegularCoursesStatisticsList CurrentColoredCoursesRecord, OptimalColoredCoursesRecord;
// OptimalColoredCoursesRecord;
    private double CurrentColoredCoursesVariance, OptimalColoredCoursesVariance;
    private puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution Solution=null;
    
    // concurrency handling listener
    private DomainListener User = null;
    
    private boolean is_list_feasible( ) {
        
        if( ! this.seeIfUserPauses() )
            return true;//user cancelled processing so end up work 
        
        java.util.Iterator<puexamroutine.control.domain.RegularCourses> RegCourseItr = this.ColoredCoursesList.getRegularCourses().iterator();
        this.CurrentColoredCoursesRecord = createRegularCourseStatisticList(RegCourseItr);

        if (this.CurrentColoredCoursesRecord.isEmpty()) {
            return false;
        }
        this.CurrentColoredCoursesVariance = java.lang.Double.MAX_VALUE;

        /**
         * This is very important technical step.
         * The garbage collector is forcefully called to allow garbage collection of the useless instances of GraphColoringSolution and RegularCourseStatistics that are created for each call to isFeasible
         * The main reason of this is that isFeasible is called very often i.e. it is the bottle neck of system
         * For each group this func may be called for too much time that is equal to total amount of color combination at the worst case
         * Each call to feasible creates many objects, This eats up too much memory in a very short period.
         * Practical running of system has finally shown that this rate is much higher than what automatic garbage collector can do.
         * So, This causes system memory error very quickly causing system to go into undesirable state.
         * So, This forces the useless objects to be garbaged to be reused in future and thus helps in overall controll of memory usage by system
         */
        System.gc();

        return this.checkFeasibility(this.CurrentColoredCoursesRecord);
    }
    
    //  10/12/2008
    private boolean generateCombinationAndCheckFeasibility(){
        int n = this.ColoredCoursesList.getTotalIndependentCoursesGroup();        
        return generateCombination( new int[n], 0 );        
    }
    
    private boolean generateCombination( int[] array, int index ){        
        if( index == array.length ){
            if( this.DEBUG ){
                System.out.println( "one combination" );
                for( int k = 0; k < array.length; k++){
                    System.out.print( array[k]+"-" );
                }
            }
            if( this.ColoredCoursesList.setCombination(array) ){
                return this.is_list_feasible();
            }
            else{
                if(this.DEBUG)System.out.println("combination could not be set...");
                return false;
            }
        }
        int i, j;
        for( i =0; i< array.length; i++ ){            
            
                for( j=0; j<index; j++ ){
                    if( array[j] == i ){                        
                        break;
                    }
                }
                if( j == index ){
                    array[ index ] = i;
                    if( generateCombination( array, index + 1 ) )
                        return true;
                    else
                        continue;
                }                
            
        }
        return false;
    }
    
    /**
     * This method deals with the job of pausing.
     * The pause functionality allows the long processing to be paused for some reason.
     * To implement it, this mehod is added.
     * It is called every time a new combination is generated and gonna be checked for feasibility.
     * 
     * It simply checks if user has paused processing through User.isPaused() method provided in its signature
     * It then simply pauses processing using spin lock 
     * If it simply spin locks itself it may be paused forever if user is not responding or being explicitly killed.
     * For such case, <strong>I have put limited time pausing functionality</strong>
     * Even after the time span, user does not unpauses, it then <strong>system itself cancels proecssing</strong> withot further continuing it.
     * To implement this, I used a timer which ticks after some time.
     * If It still finds spin lock there, then it explicitly breaks it and cancels further processing.
     * In such case, Pause Time out occurs which users can get through didTimeUp() method
     * The maximum time out time is 5 minutes but may change according to situation.
     * 
     * @see didTimeUp()
     * @see spinLock()
     * 
     * @return true if system should continue else false
     */
    private final boolean seeIfUserPauses(){
        try{            
            if(this.DEBUG)System.out.println( "i m seeing if user has paused in graph coloring" );                                    
            if( this.User.isPaused() ){
                //this.PauseTimeUp = false;//pausing is just starting up
                //wait for 5 minutes
                new javax.swing.Timer( this.MAXIMUM_TIME_OUT_TIME*60*100, new java.awt.event.ActionListener() {        
                    public void actionPerformed( java.awt.event.ActionEvent e ) {
                        if( User.isPaused() && ! User.isCancelled() ){ // see if still locked in spin lock
                            PauseTimeUp = true; //if still in spin lock then end as time out has happened and no one has yet un paused it otherwise isPaused wouldnt still remain true
                        }                        
                        ((javax.swing.Timer)e.getSource()).stop();                        
                    }        
                }).start();
                if(this.DEBUG)System.out.println("getting to sleep");                    
                spinLock();
                if(this.DEBUG)System.out.println( "waking after being paused" );
            }            
            return ! ( this.User.isCancelled() || this.PauseTimeUp );
        }
        catch( Exception e ){
            if(this.DEBUG)System.err.println( "Cannot pause graph coloring due to "+e.getMessage() );
            return false;
        }
    }

    /**
     * This method spins locks the system till any one of the condition is not true
     * 1. Pause Time up occured
     * 2. user has explicitly un paused the paused system
     * 3. user has explicitly cancelled the processing
     * 
     * Once, any of the above condition is false, it breaks the lock.
     * 
     * @see didTimeUp()
     */
    private final void spinLock() {
        while ( ! this.didTimeUp() && this.User.isPaused() && !this.User.isCancelled()) {
            ;
        }        
    }
    
    /**
     * This method gives whether time up occured or not during procesing of the system.
     * Time up occurs if user did not unPauses the once paused system within certain time out time.
     * In such case, System implicitly cancels the further processing and returns.
     * So, This method can be used to know of the state.
     * 
     * @return true if time up occured else false
     */
    public final boolean didTimeUp(){ 
        return this.PauseTimeUp;
    }
    
    private boolean PauseTimeUp = false;
    private int MAXIMUM_TIME_OUT_TIME;//minutes
}