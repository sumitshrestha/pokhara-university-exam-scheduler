
//                      !! RAM !!

package puexamroutine.control.routinegeneration.graph.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sumit Shresth
 */
public class MemoryGraph implements puexamroutine.control.routinegeneration.graph.domain.interfaces.AdjacancyMatrix{

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryGraph.class);
    
    public MemoryGraph( int size ){
        try{
        this.Size = size;
        this.Array = new boolean [size][size];
        for( int i=0; i<size; i++){
            for( int j=0; j<size;j++ ){
                this.Array[i][j] = false;
            }
        }
        }
        catch( Exception e){
            LOGGER.error("error in memory graph initialize", e);
        }
    }
            
    public boolean get(int x, int y){
        return this.Array[x-1][y-1];
    }
    
    public void set(int x, int y, boolean val ){
        this.Array[x-1][y-1] = val;
        this.Array[y-1][x-1] = val;
    }
    
    public int getSize(){
        return this.Size;
    }
    
    public int getDegree(){
        
        int oldDegree=0;       
        
        for( int i=0;i<this.getSize();i++){
            int newDegree=0;
            for( int j=i; j<this.getSize(); j++ ){
                if( this.Array[i][j] ){
                    newDegree ++;
                }
            }
            
            if( oldDegree < newDegree ){
            oldDegree = newDegree;
            }
        }
        
        
        return oldDegree;
    }
    
    public void report(){
        LOGGER.info("The size of graph was {}*{} this is the degree::{} This is the graph", getSize(), getSize(), this.getDegree());
        for( int i=1;i<=getSize(); i++){            
            StringBuilder row = new StringBuilder();
            for( int j=1;j<=getSize();j++){
                row.append(" ").append(get(i, j)?1:0);
            }
            LOGGER.info("{}", row);
        }
    }
    
    private boolean[][] Array;
    private int Size=-1;
}
