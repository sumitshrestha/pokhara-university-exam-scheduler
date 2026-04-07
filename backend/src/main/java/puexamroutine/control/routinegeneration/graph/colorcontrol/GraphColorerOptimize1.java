
//                                  !! RAM !!

package puexamroutine.control.routinegeneration.graph.colorcontrol;

import puexamroutine.control.routinegeneration.graph.domain.interfaces.User;

/**
 * This class is more optimal implementation of super class.
 * It overrides some core functions of super class.
 * 
 * @author Sumit Shresth
 */
public class GraphColorerOptimize1 extends puexamroutine.control.routinegeneration.graph.colorcontrol.GraphColorer {
    
    public GraphColorerOptimize1( User user ){        
        super( user );
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
                System.out.println("The function could not execute completly");
            else
                System.out.println("The function executed completly");
            System.out.println("System reached for " + this.reachedEnd + " times");
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
        java.util.HashSet<java.lang.Integer> AdjacantEdgeColors = new java.util.HashSet<java.lang.Integer>();
        java.util.HashSet<java.lang.Integer> NonAdjacantEdgeColors = new java.util.HashSet<java.lang.Integer>();
        
        for( int i =1; i< k ; i++ ){
            if( super.Graph.get(i, k) ){// there is (i,k) edge in graph
                AdjacantEdgeColors.add( super.array[i-1] );
            }
            else
                NonAdjacantEdgeColors.add( super.array[i-1] );
        }
        
        java.lang.Integer[] PossibleColors = this.subtract( NonAdjacantEdgeColors.toArray( new java.lang.Integer[]{} ), AdjacantEdgeColors );
        
        if( PossibleColors.length > 0  ){// if there is atleast one available color to be assigned to this vertex k
            for( int i=0;i<PossibleColors.length; i++){
                super.array[k-1] = PossibleColors[i];
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
    protected java.lang.Integer[] subtract( java.lang.Integer[] Array1, java.util.HashSet<java.lang.Integer> HashedArray2 ){
        try{
            if( Array1.length == 0 || HashedArray2.size() == 0 )// if Array1 has no elements or even if Array2 has no elements to be removed from Array1 then simply return Array1 as difference
                return Array1;
            
            java.util.ArrayList<java.lang.Integer> DiffArray = new java.util.ArrayList<java.lang.Integer>();
        
            for( int i=0; i<Array1.length; i++ ){
                if( ! HashedArray2.contains( Array1[i] ) ){ // it is hoped the search in hashing used is of order O(1) as expected
                    DiffArray.add( Array1[i] );//of the order O(n)
                }
            }
        
            return DiffArray.toArray( new java.lang.Integer[]{} );
        }
        catch( Exception e ){
            System.out.println("\n\n\n\tException thrown in subtract part "+ e.getMessage() );
            return new java.lang.Integer[]{};
        }
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
    
}// class ends