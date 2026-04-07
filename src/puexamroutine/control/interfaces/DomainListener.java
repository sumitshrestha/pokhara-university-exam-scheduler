
//                              !! RAM !!

package puexamroutine.control.interfaces;

import puexamroutine.control.routinegeneration.interfaces.SystemStepState;

/**
 * This interface represents the user of this controller.
 * The main job of controller is to read database and schedule exams according to inputs.
 * There must be some user of this controller.
 * The user must be notified of various events that occured in this black box of controller.
 *
 * @author Sumit Shresth
 */
public interface DomainListener {

    /**
     * This method notifies listener of the step being done by the system.
     * 
     * @param state state of system
     */
    void notify( SystemStepState state );

    /**
     * This method returns if the scheduling task is being cancelled by user.
     * This functin is used by system to check if system can continue processing or not
     *
     * @return true if cancelled else false
     */
    boolean isCancelled();
    
    /**
     * This method returns if the sceduling task is being paused by the user.
     * This functin is used by system to check if system can continue processing or not
     * 
     * @return true if paused by user else false
     */
    boolean isPaused();
    
}