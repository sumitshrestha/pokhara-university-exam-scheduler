
//                              !! RAM !!

package puexamroutine.control.routinegeneration.graph.colorcontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import puexamroutine.control.routinegeneration.graph.domain.interfaces.User;

/**
 *
 * @author Sumit Shresth
 */
public class GraphColorer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphColorer.class);
    
    protected boolean DEBUG = false;
    
    public GraphColorer( User user ){        
        this.ColoringUser = user;
    }
    
    public boolean initialize( puexamroutine.control.routinegeneration.graph.domain.interfaces.AdjacancyMatrix Graph, puexamroutine.control.routinegeneration.graph.domain.interfaces.GraphColoringSolutionAnalyzer anal ){
        try{
        this.size = Graph.getSize();
        this.array = new int[size];
        this.Graph = Graph;        
        this.state = false;
        this.GraphColorAnalyzer = anal;
        this.OptimalSolution = null;
        return true;
        }
        catch( Exception e ){
            this.state = true;
            LOGGER.error("error in Graph color initialize", e);
            return false;
        }
    }
    
    public void color(){        
        if( this.state )
            return;
        
        //int Chromatic = this.Graph.getDegree();
        int Chromatic = this.getMeanDegree();// use mean of vertex degree to approx chromatic no.
        if(this.DEBUG)LOGGER.debug("initial chromatic no {}", Chromatic);
        this.OptimizeFlag = false;
        do{
            try{
            this.ChromaticNumber = Chromatic++;
            this.reachedEnd = 0;
            
            
            boolean st = this.mColoring( 1 ); 
            this.ChromaticNumber = this.Optimizer.getOptimumGraph().getTotalUniqueColors();
            
            if(this.DEBUG){
                if(st)
                    LOGGER.debug("The function could not execute completly");
                else
                    LOGGER.debug("The function executed completly");
                LOGGER.debug("System reached for {} times", this.reachedEnd);
            }
            
            }
            catch( Exception e ){                
            }            
        }while( ! this.OptimizeFlag && this.ChromaticNumber <= this.array.length );
    }
    
    /**
     * 
     * @param k index of the current vertex. it starts from 1 not 0
     * 
     * @return true if successfully colored else false
     */
    protected boolean mColoring( int k ){                
        do{
            this.NextValue(k);
            if( this.array[ k - 1 ] == 0 ){
                /*if( k == 1)
                    this.Optimizer.getOptimumGraph().report();*/
                return false;
            }
            
            if( onColorAssign(k) )
                return true;
        }
        while( true );
        
    }
    
    protected void NextValue( int k ){
        do{
            this.array[k -1] = ( this.array[k -1 ] + 1  ) % ( this.ChromaticNumber + 1 );
            if( this.array[k-1] == 0 )
                return;
            int j=1;
            for( ; j<=this.size; j++ ){
                if( this.Graph.get( k, j ) && this.array[k-1] == this.array[j-1] ){
                    break;                    
                }
            }
            if( j == this.size + 1 ){
                return;
            }
        }
        while( true );
    }
    
    public puexamroutine.control.routinegeneration.graph.domain.interfaces.OptimumColoredGraphInterface getOptimumGraph(){
        return this.Optimizer.getOptimumGraph();
    }
    
    public final puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution getOptimumSolution(){
        return this.OptimalSolution;
    }

    protected boolean onColorAssign(int k) {
        
        if( this.ColoringUser.requestCancel() )return true;
        
        if (k == this.size) {
            return onColoringGraph();
        } else {
            return mColoring(k + 1);
        }
    }

    /**
     * This method handles job of dealing the solution created by graph coloring algo.
     * Whenever graph coloring algo completes assigning colors to all vertices in graph, it is called.
     * For anaysing solution it uses graph analyser that the user must define during initializing of the object.
     * It performs the basic greedy prcedure of analysing solution.
     * The solution formed by the graph coloring is always possible solution. But, it is not enough.
     * So First, The feasiblity of solution of obtained. If it is not feasible then its simply returns false.
     * If feasible then it now checks if solution is optimal or not.
     * If the generated solution is best then it returns true thus ending further solution generation of graph coloring algorithm.
     * else then it checks if the solution is better then previous better solution.
     * Initially, the previous solution is null.
     * So, the first feasble solution is always better( better than nothing i.e. null )
     * Afterwards, it simply goes on.
     * Thus, By the end of graph coloring algorithm, there is atleast non null optimal solution if any feasible solution exists.
     * This is basic algorithm that generalizes the work. So, it is kept FINAL so it cannot be overriden.
     * 
     * @return true if best solution is obtained so that there is no need to find next solution else false.
     */
    protected final boolean onColoringGraph() {        
        this.optimized();        
        puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution Sol = new puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution(array, Graph );
        if( ! this.GraphColorAnalyzer.isFeasible(Sol) ){
            return false;
        }
        else{
            if(this.DEBUG)LOGGER.debug("feasible found...");
            if( this.GraphColorAnalyzer.isOptimal(Sol) ){                
                this.OptimalSolution = Sol;
                return true;
            }
            else
                if( this.OptimalSolution == null ){                    
                    this.OptimalSolution = Sol;
                }
            else
                if( this.GraphColorAnalyzer.isMoreOptimal(Sol, this.OptimalSolution ) ){
                    if(this.DEBUG) LOGGER.debug("more optimal found using comparing...");
                    this.OptimalSolution = Sol;
                    return false;
                }
        }
        return false;
    }
    
    private final void optimized( ){
        this.reachedEnd ++;                
    }
    
    /**
     * This method returns the mean of degree of every vertex in the this.Graph. It is used to approximate the chromatic number of the Graph.
     * @return mean of degree of every vertex in this.Graph
     */
    private final int getMeanDegree(){
        int TotalDegree = 0;
        for( int i=1;i<= this.size;i++){
            for( int j=1; j<=this.size; j++ ){
                if( this.Graph.get(i, j)){
                    TotalDegree ++;
                }
            }
        }
        return TotalDegree/this.size;
    }
    
    public final int getChromaticNumber(){
        return this.ChromaticNumber;
    }
    
    private void print(){
        StringBuilder sb = new StringBuilder();
        for( int i=0; i<this.array.length; i++ ){
            sb.append("  ").append(i).append("th :").append(this.array[i]);
        }
        LOGGER.debug("{}", sb);
    }
    
    protected int reachedEnd;
    protected int size;
    protected int[] array;
    private boolean state = false;
    protected int ChromaticNumber;
    protected puexamroutine.control.routinegeneration.graph.domain.interfaces.AdjacancyMatrix Graph;
    protected puexamroutine.control.routinegeneration.graph.colorcontrol.ColorCombinationOptimizer Optimizer = new puexamroutine.control.routinegeneration.graph.colorcontrol.ColorCombinationOptimizer( 0.05 );
    private puexamroutine.control.routinegeneration.graph.domain.interfaces.GraphColoringSolutionAnalyzer GraphColorAnalyzer;
    private puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution OptimalSolution;
    private boolean OptimizeFlag;//flag that keeps info of colored being assigned to array. if they are not it remains false. when they are completly assigned then it becaomes true.    
    private final User ColoringUser;
}