
//                      !! RAM !!

package puexamroutine.control.routinegeneration.graph.domain;

/**
 *
 * @author Sumit Shresth
 */
public class MemoryGraph implements puexamroutine.control.routinegeneration.graph.domain.interfaces.AdjacancyMatrix{
    
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
            System.out.println("error in memory graph initialize "+ e.getMessage() );
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
        System.out.println("The size of graph was " + getSize()+"*"+getSize()+ "\nthis is the degree::"+this.getDegree()+"\nThis is the graph");
        for( int i=1;i<=getSize(); i++){            
            for( int j=1;j<=getSize();j++){
                System.out.print(" " + (get(i, j)?1:0) );
            }
            System.out.print("\n");
        }
    }
    
    private boolean[][] Array;
    private int Size=-1;
}
