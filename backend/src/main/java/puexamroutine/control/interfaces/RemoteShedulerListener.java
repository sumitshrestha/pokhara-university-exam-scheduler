
//                                      !! RAM !!

package puexamroutine.control.interfaces;

import java.rmi.*;
import puexamroutine.control.routinegeneration.interfaces.SystemStepState;

/**
 *
 * @author Sumit Shresth
 */
public interface RemoteShedulerListener extends Remote{

    /**
     * This method notifies listener of the step being done by the system.
     * 
     * @param state state of system
     * 
     * @throws java.rmi.RemoteException remote exception if cannot be done
     */
    void notify( SystemStepState state ) throws RemoteException;

    /**
     * This method returns if the scheduling task is being cancelled by user.
     * This functin is used by system to check if system can continue processing or not
     *
     * @return true if cancelled else false
     * 
     * @throws java.rmi.RemoteException remote exception if cannot listenen
     */
    boolean isCancelled() throws RemoteException;
    
}