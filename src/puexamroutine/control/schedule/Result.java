
//                                  !! RAM !!

package puexamroutine.control.schedule;

import puexamroutine.control.domain.routine.*;
import java.util.Collection;
import puexamroutine.control.domain.Group;

/**
 * This class represents the result of the scheduling.
 *
 * @author Sumit Shresth
 */
public class Result implements java.io.Serializable {

    public Result( boolean success, final ExaminationDayCalander calander,final Collection<GroupProgramRoutineList> grpRout, Collection<Group> unscheduledGrp, final String error  ){               
        this.success= success;
        this.calander = calander;        
        this.unscheduledGrp = unscheduledGrp;
        this.Error = error;
        this.GroupRouts = grpRout;
    }
    
    public boolean executedSuccessfully(){
        return this.success;
    }
    
    public final String getError(){
        return this.Error;
    }
    
    public final ExaminationDayCalander getExamCalander(){
        return this.calander;
    }
        
    public final Collection<Group> getunScheduledGroups(){
        return this.unscheduledGrp;
    }
    
    public final Collection<GroupProgramRoutineList> getGroupRoutine(){
        return this.GroupRouts;
    }
    
    private final boolean success;
    private final ExaminationDayCalander calander;    
    private final Collection<Group> unscheduledGrp;
    private final String Error;
    private final Collection<GroupProgramRoutineList> GroupRouts;    
}