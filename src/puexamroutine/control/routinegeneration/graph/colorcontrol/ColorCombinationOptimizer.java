
//                              !! RAM !!

package puexamroutine.control.routinegeneration.graph.colorcontrol;

/**
 *
 * @author Sumit Shresth
 */
public class ColorCombinationOptimizer {
    
    public ColorCombinationOptimizer( ){        
    }
        
    public ColorCombinationOptimizer( double o ){        
        this.OptimizedValue = o;
    }
    
    public void setOptimizeValue( double o ){
        this.OptimizedValue = o;
    }
    
    public boolean isOptimized(){
        if( this.OptimumGraph.getVariance() <= this.OptimizedValue ){
                return true;
        }
        else{             
             return false;
        }
    }
    
    public puexamroutine.control.routinegeneration.graph.domain.interfaces.OptimumColoredGraphInterface getOptimumGraph(){
        return this.OptimumGraph;
    }
    
    public boolean optimize( int[] array ){
        puexamroutine.control.routinegeneration.graph.domain.interfaces.OptimumColoredGraphInterface NewOptGraph = new puexamroutine.control.routinegeneration.graph.domain.OptimumColoredGraph( array );
        if( this.OptimumGraph == null ){
            this.OptimumGraph = NewOptGraph;
            return true;
        }
        else{
            if( this.betterThan(  NewOptGraph, this.OptimumGraph) ){
                this.OptimumGraph = NewOptGraph;
                return true;
            }
        }
        return false;
    }
    
    public boolean betterThan(puexamroutine.control.routinegeneration.graph.domain.interfaces.OptimumColoredGraphInterface New, puexamroutine.control.routinegeneration.graph.domain.interfaces.OptimumColoredGraphInterface Old ){
        try{
            if( New.getTotalUniqueColors() < Old.getTotalUniqueColors()){
                return true;
            }
            else
                if( New.getTotalUniqueColors() > Old.getTotalUniqueColors()){
                    return false;
                }
                else{ // both are equal
                    if( New.getVariance() > -1D ){// check if variance of New graph is valid
                        if( New.getVariance() < Old.getVariance() ){
                            return true;
                        }
                        else
                            return false;
                    }
                    else
                        return false;//there was some error in variance calculation of New graph
                }
        }
        catch( Exception e ){
            return false;
        }
    }
    
    private puexamroutine.control.routinegeneration.graph.domain.interfaces.OptimumColoredGraphInterface OptimumGraph;
    private double OptimizedValue;
}