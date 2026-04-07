
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
public record Result(
        boolean success,
        ExaminationDayCalander calander,
        Collection<GroupProgramRoutineList> groupRouts,
        Collection<Group> unscheduledGrp,
        String error
) implements java.io.Serializable {

    public boolean executedSuccessfully(){
        return this.success;
    }

    public final String getError(){
        return this.error;
    }

    public final ExaminationDayCalander getExamCalander(){
        return this.calander;
    }

    public final Collection<Group> getunScheduledGroups(){
        return this.unscheduledGrp;
    }

    public final Collection<GroupProgramRoutineList> getGroupRoutine(){
        return this.groupRouts;
    }
}