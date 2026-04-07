
//                                  !! RAM !!

package puexamroutine.control.routinegeneration.graph.colorcontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import puexamroutine.control.routinegeneration.graph.domain.interfaces.User;

/**
 * This class is more optimal implementation of super class.
 * It overrides some core functions of super class.
 * 
 * @author Sumit Shresth
 */
public class GraphColorerOptimize1 extends puexamroutine.control.routinegeneration.graph.colorcontrol.GraphColorer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphColorerOptimize1.class);
    
    public GraphColorerOptimize1( User user ){        
        super( user );
    }

    @Override
    public boolean initialize(puexamroutine.control.routinegeneration.graph.domain.interfaces.AdjacancyMatrix Graph,
                              puexamroutine.control.routinegeneration.graph.domain.interfaces.GraphColoringSolutionAnalyzer anal) {
        boolean initialized = super.initialize(Graph, anal);
        if (initialized) {
            int capacity = super.size + 2;
            this.adjacentColorStamp = new int[capacity];
            this.nonAdjacentColorStamp = new int[capacity];
            this.possibleColors = new int[capacity];
            this.stampCounter = 1;
        }
        return initialized;
    }
    
    /**
     * This method is used to color the graph specifed during creation of this object.
     * It hides the actual implementation issues from the user and neither takes or gives and values.
     * After coloring, the coloring info can be obtained.
     * This implementation is essentially similar to its super class implementation.
     * The only difference is due to the main difference between the mColoring function used by each.
     * The super implementation uses chromatic number to color the graph while this implementation finds its own chromatic number.( for more info: read the documentation of mColoring function in this class).
     * So, Here Chromatic number is not set initially whereas in super implementation, it is set.
     * 
     */
    @Override
    public void color(){
        super.reachedEnd = 0;
        boolean st = this.mColoring(1);
        if( super.DEBUG ){
            if(st)        
                LOGGER.debug("The function could not execute completly");
            else
                LOGGER.debug("The function executed completly");
            LOGGER.debug("System reached for {} times", this.reachedEnd);
        }
    }
        
    /**
     * This method is the solution of m-colorablity otimization problem using recursive backtrack method.
     * The method is used to color the graph using recursive backtrack method.
     * Unlike its super class implementation, it scans the necassary nodes only.
     * So, it is much quicker. The super implementation used to assign all possible combination of colors between the 0 and chromatic no specified.
     * But, it does not assigns all colors. The color is only a way of grouping nodes of graph. So, The colors are assigned as optimally as possible.
     * If an assigned color is possible then, new color is not assigned. This was not done in super class implementation.
     * 
     * The Main differance between this and super implementation is ::
     * Super:: It is M-COLORABILITY DECISION PROBLEM SOLUTION
     * this:: It is M-COLORABILITY OPTIMIZATION PROBLEM SOLUTION
     * So, it does not require value of m to be specified. So, it is not only faster but optimal.
     * However, just like its super implementation, it uses recursion which can still be less efficient.
     * Still, i developed it using the reference of its super implementation and am glad to get it.
     * Here may be some implementation specific problem as I took advantage of API available for Java that may not be available for lower level languages like C, etc.
     * Still, It was necessary for software development.
     * 
     * @param k index of the current vertex. it starts from 1 not 0
     */
    @Override
    protected boolean mColoring( int k ){
        int stamp = nextStamp();
        for( int i =1; i< k ; i++ ){
            int color = super.array[i - 1];
            if (color <= 0) {
                continue;
            }
            if( super.Graph.get(i, k) ){// there is (i,k) edge in graph
                this.adjacentColorStamp[color] = stamp;
            }
            else {
                this.nonAdjacentColorStamp[color] = stamp;
            }
        }

        int possibleColorCount = buildPossibleColors(k, stamp);
        if( possibleColorCount > 0  ){// if there is atleast one available color to be assigned to this vertex k
            for( int i=0;i<possibleColorCount; i++){
                super.array[k-1] = this.possibleColors[i];
                if( super.onColorAssign(k) )
                    return true;                
            }
        }
        else{
            int max = findMax(k,super.array );
            super.array[k-1] = max + 1;
            if( super.onColorAssign(k) )
                return true;
        }
        
        return false;
    }
    
    /**
     * This method returns the difference between two specified arrays.
     * The values will be elements of array1 that are not present in array 2 and not vice versa.
     * The algorithm is tried to be as optimized as possible.
     * Hashing (Hash set) is used to allow searching to a near constant order of O(1).
     * 
     * @param Array1 first operand array
     * @param HashedArray2 second operand hashed array
     * @return difference between array1 and array2
     */
    private int buildPossibleColors(int k, int stamp) {
        int count = 0;
        for (int color = 1; color < k; color++) {
            if (this.nonAdjacentColorStamp[color] == stamp && this.adjacentColorStamp[color] != stamp) {
                this.possibleColors[count++] = color;
            }
        }
        return count;
    }

    private int nextStamp() {
        this.stampCounter++;
        if (this.stampCounter == Integer.MAX_VALUE) {
            java.util.Arrays.fill(this.adjacentColorStamp, 0);
            java.util.Arrays.fill(this.nonAdjacentColorStamp, 0);
            this.stampCounter = 1;
        }
        return this.stampCounter;
    }
    
    public int findMax(int k,int[] Array) {
        int max = 0;
        for (int i = 0; i < k - 1; i++) {
            if (max < Array[i]) {
                max = Array[i];
            }
        }
        return max;
    }

    private int[] adjacentColorStamp;
    private int[] nonAdjacentColorStamp;
    private int[] possibleColors;
    private int stampCounter;
    
}// class ends