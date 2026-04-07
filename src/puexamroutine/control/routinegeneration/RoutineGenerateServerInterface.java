
//                                  !! RAM !!

package puexamroutine.control.routinegeneration;

import puexamroutine.control.domain.list.UniversityDataBean;
import puexamroutine.control.schedule.Result;
import java.rmi.*;

/**
 * This interface represents the interface of the routine generate server.
 * This interface will be the protocol for remote interfacing with the system.
 * So, it extends the remote interface.
 *
 * @author Sumit Shresth
 */
public interface RoutineGenerateServerInterface extends Remote{
    
    String SERVER_NAME = "Remote Routine Scheduler";
    public final String CLIENT_NOT_REGISTERED = "Client is not registered";

    /**
     * This method is the main protocol for scheduling using remote method.
     * This server will run in some remote computer.
     * The main job of the System will be to perform the scheduling tasks without thinking about the database.
     * Since, there can be many clients each requesting, For each request a separate thread will be created.
     * 
     * @param ClientID requesting client's ID
     * @param Data the data object that is sent to this system as a way of producing the routine
     * @param Min_Gap minimum gap between the routine
     * @param Max_Gap maximum gap between each successive regular exam
     * @param Time The Server time requested by client to server. The value is implementation dependent
     *
     * @return result object representing the overall result of scheduling
     * 
     * @throws java.rmi.RemoteException remote exception in case it could not be done
     */
    public Result generateRoutine(  String ClientID, UniversityDataBean Data, int Min_Gap, int Max_Gap,long Time ) throws RemoteException;
    
    void cancel(final String ClientID) throws RemoteException;

    public String register() throws RemoteException;
    
    /**
     * This method requests the server that it continue the previous scheduling process for extraTime time more.
     * This method is normally called after expiration of time but may be called before depending the implementation if it does not affects state.
     * 
     * @param extraTime an extra time to be added in processing
     * @param ClientID requesting client's identity for authentication purpose
     * 
     * @return true if server has increased time else false on any error including the proecessing is already cancelled or unauthorized user access, etc.
     * 
     * @see ClientRequestBean 
     */
    boolean continueScheduling( String ClientID, long extraTime ) throws RemoteException;
    
}