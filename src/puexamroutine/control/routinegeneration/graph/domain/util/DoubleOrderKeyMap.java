
//                              !! RAM !!

package puexamroutine.control.routinegeneration.graph.domain.util;

/**
 *
 * @author Sumit Shresth
 */
public class DoubleOrderKeyMap {
    
    /**
     * Intializes the map of specified initial size
     * 
     * @param size non negative initial size of the map
     */
    public DoubleOrderKeyMap( int size ){        
        this.Array1 = new java.util.HashMap<java.lang.Integer, String >( size );
        this.Array2 = new java.util.HashMap< String, java.lang.Integer >( size );
    }
    
    /**
     * Intializes the map with zero intial size.
     */
    public DoubleOrderKeyMap(){
        this.Array1 = new java.util.HashMap<java.lang.Integer, String >( );
        this.Array2 = new java.util.HashMap< String, java.lang.Integer >( );
    }
    
    /**
     * This method is used to add udordered pair {index, key} into the map. 
     * 
     * @param Index integer indicating the no of key
     * @param Key string indicating value of the integer
     * 
     * @return true if successfully put else false
     */
    public boolean put( int Index, String Key ){
        try{
            java.lang.Integer index = Index;
            this.Array1.put(index, Key);
            this.Array2.put(Key, index);        
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    /**
     * This method is used to put unordered pair { Key, Index } into the map.
     * 
     * @param Key String indicating value of the integer
     * @param Index integer indicating the no of key
     * 
     * @return true if successfully put else false
     */
    public boolean put( String Key, int Index ){
        try{
            java.lang.Integer index = Index;
            this.Array1.put(index, Key);
            this.Array2.put(Key, index);
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    /**
     * This method gets the String value associated with specified Index
     * 
     * @param Index valid index that has been put into map
     * 
     * @return value associated with the specified Index , null if no such index is associated
     */
    public String get( int Index ){
        return this.Array1.get(Index);
    }
    
    /**
     * This method gives the integer value associated with specified Stirng key
     * 
     * @param Key valid key that has been put into map
     * 
     * @return integer value associated with the apecified key
     * 
     * @throws java.lang.NullPointerException if integer value inputted is not associated with any key in map
     */
    public int get( String Key ) throws java.lang.NullPointerException{        
            return this.Array2.get(Key);        
    }
    
    
    private java.util.HashMap<java.lang.Integer, String > Array1;
    private java.util.HashMap< String, java.lang.Integer > Array2;
}
