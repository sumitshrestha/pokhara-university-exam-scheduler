
//                              !! RAM !!

package puexamroutine.control.routinegeneration.graph.domain.interfaces;

/**
 * This interface represents the user of graph coloring.
 * The user of graph coloring must implement this interface.
 * The graph coloring algorithm will periodically inspect user for info like if it wants to contine coloring, etc
 * So, This interface plays a reverse bridge between user and grpah coloring
 *
 * @author Sumit Shresth
 */
public interface User {

    /**
     * This method is true if user requested to cancel the graph coloring process.
     * This is very important since the graph coloring is long and unpredictable task
     * So, The system will periodically check if this is true for continuing else graph coloring will immediately return true specifying forced close was made
     * 
     * @return true if user wants to cancel else false
     */
    boolean requestCancel();
    
}
