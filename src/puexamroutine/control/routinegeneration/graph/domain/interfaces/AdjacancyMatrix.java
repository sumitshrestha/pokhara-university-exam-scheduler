
//                      !! RAM !!

package puexamroutine.control.routinegeneration.graph.domain.interfaces;

/**
 *
 * @author Sumit Shresth
 */
public interface AdjacancyMatrix {
    
    boolean get(int x, int y);
    
    void set(int x, int y, boolean val );
    
    int getSize();

    int getDegree();
    
    void report();
}
