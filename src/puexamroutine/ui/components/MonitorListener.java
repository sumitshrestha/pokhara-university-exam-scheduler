
//                          !! RAM !!

package puexamroutine.ui.components;

/**
 * This interface is used by schedule monitor to interact with the main fraem
 * The monitor may have some event that parent frame must know.
 * It includes user cancellation of job, etc
 * So, there must be some bridge for that.
 * The user of monitor i.e. parent frame must implement it.
 *
 * @author Sumit Shresth
 */
public interface MonitorListener {

    void cancel();
    
}