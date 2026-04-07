
//                      !! RAM !!

package puexamroutine.control.routinegeneration.graph.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class holds the one solution of graph coloring algrithm i.e. list of vertices with colors appropiate.
 * This will be output of graph coloring. Any system that was to analyze the solution uses object of this class.
 * This class will also hold other information about solution for better anlyzing solution.
 * 
 * @author Sumit Shresth
 */
public class GraphColoringSolution {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphColoringSolution.class);
    
    public GraphColoringSolution( int[] colorarray, puexamroutine.control.routinegeneration.graph.domain.interfaces.AdjacancyMatrix Graph ){
        this.ColorArray = colorarray.clone();
        this.Graph = Graph;
        this.setTotalColors();
        this.setGroupedVerticesIndexes();
    }

    public final int[] getColorArray(){
        return this.ColorArray;
    }
    
    public final puexamroutine.control.routinegeneration.graph.domain.interfaces.AdjacancyMatrix getGraph(){
        return this.Graph;
    }
    
    public final int getTotalColors(){
        return this.TotalColors;
    }
    
    public final int getColorOf( int vert_index ){
        return this.ColorArray[ vert_index ];
    }
    
    public final void setGroupedVerticesIndexes(){
        this.GroupedVerticesIndexes = new java.util.HashMap<java.lang.Integer, java.util.HashSet<java.lang.Integer> >();
        
        for( int i=1; i<=this.TotalColors; i++ ){
            this.GroupedVerticesIndexes.put( i, new java.util.HashSet<java.lang.Integer>() );
        }
        
        for( int i= 0; i < this.ColorArray.length; i++ ){    
            int index = this.ColorArray[i];
            java.util.HashSet<java.lang.Integer> temp = this.GroupedVerticesIndexes.get( index );
            temp.add(i);            
        }
        
        //this.print();
    }
    
    public final java.util.HashMap<java.lang.Integer, java.util.HashSet<java.lang.Integer> > getGroupedVerticesIndexes(){
        return this.GroupedVerticesIndexes;
    }
    
    private void print(){
        java.util.Iterator<java.lang.Integer> temp = this.GroupedVerticesIndexes.keySet().iterator();
        while( temp.hasNext() ){
            int c =temp.next();
            StringBuilder sb = new StringBuilder();
            sb.append("color ").append(c);
            java.util.Iterator<java.lang.Integer> temp2 = this.GroupedVerticesIndexes.get( c ).iterator();
            while( temp2.hasNext() ){
                sb.append(" ").append(temp2.next());
            }
            LOGGER.debug("{}", sb);
        }        
    }
    
    private final void setTotalColors(){
        int min = this.ColorArray[0];
        int max = min;
        for( int i=1; i<this.ColorArray.length; i++ ){
            if( this.ColorArray[i] > max ){
                max = this.ColorArray[i];
            }
            else
                if( this.ColorArray[i] < min ){
                    min = this.ColorArray[i];
                }
        }
        this.TotalColors = max - min + 1 ;
    }
    
    private int[] ColorArray;
    private puexamroutine.control.routinegeneration.graph.domain.interfaces.AdjacancyMatrix Graph;
    private int TotalColors;
    private java.util.HashMap<java.lang.Integer, java.util.HashSet<java.lang.Integer> > GroupedVerticesIndexes;
}