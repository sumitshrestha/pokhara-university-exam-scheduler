
//                          !! RAM !!

package puexamroutine.control.routinegeneration;

import puexamroutine.control.routinegeneration.interfaces.SystemStepState;
import puexamroutine.control.interfaces.*;
import puexamroutine.control.domain.list.UniversityDataBean;

/**
 * This class is the basic controller for the routine generation.
 * This class creates the routine for the system.
 * It simply takes the data and scheduling info and generates the schedule.
 * This has been done for two great reasons:
 * 1. To allow for more greater cohesion of design
 * 2. To make it remote process that does not bother about data origin which is done by controller.
 * This class generates routine without taking database connection into account.
 * This is very preferable since it allows the code to be more cohesive and focussed towards efficient scheduling.
 *
 * @author Sumit Shresth
 */
public class RoutineGenerator {
    
    private boolean DEBUG = false;
    
    public final static String INTIALIZE = "schedule intialization";
    public final static String REGULAR_COURSE_COLORING = "regular course coloring";
    public final static String BACK_COURSE_COLORING = "back course coloring";
    public final static String SCHEDULE= "scheduling";
    public final static String COMPLETED = "completed";

    /**
     * This method is the main operation this object i.e. gnerating routine.
     * This method takes the necessary info for routine generation and genrates the routine accordingly.
     * 
     * @param Data The main data of university using which it genrates routine
     * @param Min_Gap the minimum gap between two successive regular exam
     * @param Max_Gap the maximum gap between two successive regular exams
     * 
     * @return true if successfully generated else false
     */
    public boolean generate( UniversityDataBean Data, final int Min_Gap, final int Max_Gap  ){
        try{            
            if( listener.isCancelled() ) return false;
            setStep( INTIALIZE, 0 );
            Grouper = new puexamroutine.control.routinegeneration.Grouper( Min_Gap, Max_Gap, listener );
            puexamroutine.control.domain.list.GroupList g = Data.getGroupList();            
            java.util.Collection<puexamroutine.control.domain.Group> gr = g.getGroups();
            final int size = gr.size();
            java.util.Iterator<puexamroutine.control.domain.Group> grItr = gr.iterator();
            ExamSceduler = new puexamroutine.control.routinegeneration.ExamScheduler();
            ExamSceduler.initialize( g, Data.getCentreList(), Min_Gap,Max_Gap );
            Grouper.setGroupList( g );
            UnScheduledGroupList.clear();            
            int i=0;
            final int reg_step = 80/size;
            final int back_step = 40/size;
            while( grItr.hasNext() ){                
                if( listener.isCancelled() ) return false;
                int temp = 10 + reg_step * i;
                setStep( REGULAR_COURSE_COLORING, temp );
                puexamroutine.control.domain.Group group = grItr.next();     
                puexamroutine.control.domain.list.IndependentRegularCourseList array1 = Grouper.groupRegularCourses(group);
                if( listener.isCancelled()){
                    this.ErrorMessage = "User Cancelled the processing request\nSo, Processing could not be carried out";
                    return false;
                }
                if( this.Grouper.didTimeUpOccured() ){
                    this.ErrorMessage = "Processing could not be carried out due to Pause Time Out";
                    return false;
                }
                if( array1 != null ){
                    ExamSceduler.addRegularCourseSet( array1 );                                        
                    temp += back_step;
                    setStep( BACK_COURSE_COLORING, temp );
                //    puexamroutine.control.domain.list.IndependentCourseList grpBkPapers = Grouper.groupBackCourses(group);
                    if( listener.isCancelled() || Grouper.didTimeUpOccured() ) return false;
              //      ExamSceduler.addBackCourseSet( grpBkPapers );
                }
                else{
                    if(this.DEBUG)System.out.println("This is for group"+group.getFaculty()+":"+group.getDiscipline());
                    step.setError( "Group "+group.getFaculty()+":"+group.getLevel()+":"+group.getDiscipline()+" cannot be grouped" );
                    UnScheduledGroupList.add(group);
                }
                i++;
            }            
            if( listener.isCancelled()){
                this.ErrorMessage = "User Cancelled the processing request\nSo, Processing could not be carried out";
                return false;
            }
            setStep( SCHEDULE, 90 );
            ExamSceduler.scheduleCourses();
            //ExamSceduler.scheduleBackPapers();
            ExamSceduler.calculateGroupPrgRoutine();
            //ExamSceduler.getCalander().print();
            //ExamSceduler.printAllRoutines();
            setStep( COMPLETED,100 );
            return true;
        }
        catch( Exception e ){            
            step.setError( e.getMessage() );
            setStep( COMPLETED, 100 );      
            this.ErrorMessage = "Exception occured during processing viz\n"+e.getMessage();
            return false;
        }
    }    
    
    public void setListener( puexamroutine.control.interfaces.DomainListener list ){
        listener = list;
    }
    
    public puexamroutine.control.domain.routine.ExaminationDayCalander getCalander(){
        return ExamSceduler.getCalander();        
    }
    
    public java.util.Collection<puexamroutine.control.domain.routine.ProgramRoutine> getProgramRoutine(){
        return ExamSceduler.getAllProgramRoutine();
    }
    
    public java.util.Collection<puexamroutine.control.domain.routine.GroupProgramRoutine> getGroupRoutine(){
        return ExamSceduler.getGroupRoutine();
    }        
    
    public java.util.Collection<puexamroutine.control.domain.routine.GroupProgramRoutineList> getGroupProgramRoutine(){
        return ExamSceduler.getGroupProgramRoutine();
    }        
    
    private void notifyListener(){
        if( listener == null )return;          
        SystemStepState temp = step.clone();
        if( temp != null )
            listener.notify( temp );
    }
    
    private void setStep( final String step, final int percent ){
        this.step.setWorkPercent(percent);
        setStep(step);
    }
    
    private void setStep( final String step ){
        this.step.setStep(step);
        if( step == BACK_COURSE_COLORING || step == REGULAR_COURSE_COLORING ){
            this.step.setPredictable( false );
        }
        else
            this.step.setPredictable( true );
        notifyListener();
    }
    
    public java.util.Collection<puexamroutine.control.domain.Group> getUnScheduledGroup(){
        return UnScheduledGroupList;
    }
    
    public final String getErrorMessage(){
        return this.ErrorMessage;
    }
    
    //helpers controllers in routine generation tasks to whom this delegates
    private puexamroutine.control.routinegeneration.Grouper Grouper;
    private puexamroutine.control.routinegeneration.ExamScheduler ExamSceduler = null;
    
    //system state during routine generation
    private SystemStepState step = new SystemStepState();
    private DomainListener listener = null;
    private java.util.ArrayList<puexamroutine.control.domain.Group> UnScheduledGroupList = new java.util.ArrayList<puexamroutine.control.domain.Group>();
    private String ErrorMessage=null;
}