
//                              !! RAM !!

package puexamroutine.control.routinegeneration.graph.domain.util;

/**
 *
 * @author Sumit Shresth
 */
public class RandomGraph {
    public RandomGraph( final int max ){
        
        if( max < 2 ){
            this.Max = 5;
        }
        else
            this.Max = max;
        this.Rand = new java.util.Random( this.Max );
    }
    
    public puexamroutine.control.routinegeneration.graph.domain.interfaces.AdjacancyMatrix getRandomGraph(){
        double ver;
        do{
        ver = java.lang.Math.random() * this.Max;
        }
        while( ver < 2 || ver > this.Max );
        
        puexamroutine.control.routinegeneration.graph.domain.interfaces.AdjacancyMatrix Graph = new puexamroutine.control.routinegeneration.graph.domain.MemoryGraph((int)ver);
        
        this.setGraph(Graph);
        
        Graph.report();
        
        return Graph;
    }
    
    private void setGraph( puexamroutine.control.routinegeneration.graph.domain.interfaces.AdjacancyMatrix Graph ){
        
        for( int i=1;i <= Graph.getSize(); i++ ){
            for( int j= i+1 ; j <= Graph.getSize();j++){
                Graph.set( i, j, this.Rand.nextBoolean() );
                Graph.set(j, i, Graph.get( i, j));
            }
            Graph.set( i, i, false );
        }
        
    }
    
    
    java.util.Random Rand;
    private int Max;
}
