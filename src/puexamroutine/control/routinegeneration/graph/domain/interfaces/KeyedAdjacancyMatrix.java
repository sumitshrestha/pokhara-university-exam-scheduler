
//                                  !! RAM !!

package puexamroutine.control.routinegeneration.graph.domain.interfaces;

import puexamroutine.control.routinegeneration.graph.domain.interfaces.AdjacancyMatrix;

/**
 *
 * @author Sumit Shresth
 */
public interface KeyedAdjacancyMatrix extends AdjacancyMatrix{
    
    /**
     * This method sets the keys to the AdjacancyMatrix in the order. so, ist vertex will be keyed by Keys[0], 2nd by Keys[1] and so on.
     * The length of Keys must be equal to or greater than size of matrix. if not then error is generated nad key will not be set.
     * if greater than size, the extra keys will be choped off.
     * 
     * @param Keys arrays of keys to be set.
     * 
     * @return true if successfully set else false
     */
    boolean setKeys( String[] Keys);
    
    /**
     *  This method is similar to get method of its parent interface except it uses keys to find data. 
     * 
     * @param Key1 ist key denoting the source vertex.
     * @param Key2 2nd key denoting the destination vertex
     * 
     * @return true if there is incident edge else false
     */
    boolean get( String Key1, String Key2 );
    
    /**
     * This method is similar to set method of parent class except is uses the keys to set value instead of integers.
     * 
     * @param Key1 ist key denoting the source vertex
     * @param Key2 2nd key denoting the destination vertex
     * @param val true if there is incident edge else false
     * 
     * @return true if successfully set else false
     */
    boolean set( String Key1, String Key2, boolean val );
    
    /**
     * This method will give the key at the specified index of the graph
     * 
     * @param i The specified index whose key has to be obtained
     * @return the key of the specified index
     */
    String getKey( int i );
}
