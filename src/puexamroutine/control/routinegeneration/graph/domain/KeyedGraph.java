
//                          !! RAM !! 

package puexamroutine.control.routinegeneration.graph.domain;

/**
 *
 * @author Sumit Shresth
 */
public class KeyedGraph extends puexamroutine.control.routinegeneration.graph.domain.MemoryGraph implements puexamroutine.control.routinegeneration.graph.domain.interfaces.KeyedAdjacancyMatrix{
    
    public KeyedGraph( int size ){
        super( size );        
        this.KeyMap = new puexamroutine.control.routinegeneration.graph.domain.util.DoubleOrderKeyMap( size );
    }
    
    public boolean setKeys( String[] Keys){
        if( Keys.length < super.getSize() ){
            return false;
        }
        else{
            for( int i=0;i<super.getSize(); i++ ){
                this.KeyMap.put(i + 1 , Keys[i] ); // index will be in order of graph i.e. it will start from 1 and not 0 
            }
            return true;
        }
    }
    
    public boolean get( String Key1, String Key2 ){
        try{
            int indx1 = this.KeyMap.get(Key1);
            int indx2 = this.KeyMap.get(Key2);
            
            return super.get(indx1, indx2);
        }
        catch( java.lang.NullPointerException e ){
            return false;
        }
    }
    
    public boolean set( String Key1, String Key2, boolean val ){
        try{
            int indx1 = this.KeyMap.get(Key1);
            int indx2 = this.KeyMap.get(Key2);
            
            super.set(indx1, indx2, val);
            
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public String getKey( int i ){
        return this.KeyMap.get(i);
    }
    
    public int getIndex( final String Key ){
       return this.KeyMap.get(Key);
    }
    
    puexamroutine.control.routinegeneration.graph.domain.util.DoubleOrderKeyMap KeyMap;
}
