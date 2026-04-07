
//                              !! RAM !!

package puexamroutine.control.routinegeneration.graph.domain;

import puexamroutine.control.routinegeneration.graph.domain.interfaces.OptimumColoredGraphInterface;

/**
 *
 * @author Sumit Shresth
 */
public class OptimumColoredGraph implements OptimumColoredGraphInterface {
    
    public OptimumColoredGraph( int[] ColorArray ){        
        this.ColorArray = ColorArray.clone();
        if( this.calculateArrayUniqueColors() ){
            this.calculateVariance();
        }
    }
    
    public final double getVariance(){
        return this.Variance;
    }
    
    public final int  getTotalUniqueColors(){
        return this.UniqueColors.length;
    }
        
    public final int[] getColorArray(){
       return this.ColorArray;
    }
    
    public final java.lang.Integer[] getUniqueColors(){
        return this.UniqueColors;
    }
    
    public final java.util.Map<java.lang.Integer,java.lang.Integer> getUniqueColorsOccurrence(){
        return this.ColorsAmount;
    }
    
    /**
     * This method calculates the unique colors in the given color array. 
     * It must be called after setting array to a valid value only.
     * 
     * @return true if successfully set else false
     */
    private boolean calculateArrayUniqueColors(){
        
        if( this.ColorArray == null ){
            return false;
        }
        try{
       java.util.ArrayList<java.lang.Integer> UniqueColors = new java.util.ArrayList<java.lang.Integer>();
       UniqueColors.add( this.ColorArray[0] );
       for( int i=1;i<this.ColorArray.length; i++ ){
           if( ! this.contains(UniqueColors, this.ColorArray[i] )){
               UniqueColors.add( this.ColorArray[i] );
           }
       }
       
       this.UniqueColors = UniqueColors.toArray( new java.lang.Integer[]{} );       
       return true; 
        }
        catch( Exception e ){
            return false;
        }
    }
    
    private boolean contains( java.util.ArrayList<java.lang.Integer> UniqueColors, int Color ){
        for( int i =0;i< UniqueColors.size(); i++ ){
            if( UniqueColors.get(i).intValue() == Color ){
                return true;
            }            
        }
        return false;
    }
    
    private boolean calculateVariance(){
        if( this.calculateUniqueColorsNumber() ){
            double first = 0;
            double sec = 0;
            for( int i=0;i<this.getTotalUniqueColors(); i++ ){
                int Occur = this.ColorsAmount.get( this.UniqueColors[i] );
                first += Occur * Occur;
                sec += Occur;
            }
            double sec1 = sec/this.getTotalUniqueColors();
            this.Variance = first/this.getTotalUniqueColors() - sec1 * sec1;
            return true;
        }
        else{
            this.Variance = -1;
            return false;
        }
    }
    
    private boolean calculateUniqueColorsNumber(){
        try{
            if( this.UniqueColors.length < 1 ){
                return false;
            }
            
            this.ColorsAmount = new java.util.HashMap<java.lang.Integer,java.lang.Integer>( this.getTotalUniqueColors() );
            
            for(int i=0;i< this.UniqueColors.length; i++ ){
                int Occur = this.getTotalOccurence( this.UniqueColors[i]);
                if( Occur >= 0 )
                    this.ColorsAmount.put( this.UniqueColors[i] , Occur );
                else
                    return false;// -1 means error whick effect variance calculation
            }
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    /**
     * This method gives the total occurnce of Color in the color array. if error then -1;
     * 
     * @param Color color whose occurenc in array is to be calculated
     * 
     * @return size indicating occurence of color in array. if error then -1
     */
    private int getTotalOccurence( int Color ){
        try{
            int ColorOccurence = 0;
            for( int i=0;i<this.ColorArray.length; i++ ){
                if( this.ColorArray[i] == Color ){
                    ColorOccurence ++;
                }
            }
            return ColorOccurence;
        }
        catch( Exception e ){
            return -1;
        }
    }
    
    public java.util.HashMap< java.lang.Integer, java.util.HashSet > getColorIndexListMap(){
        java.util.HashMap< java.lang.Integer, java.util.HashSet > Array = new java.util.HashMap< java.lang.Integer, java.util.HashSet >();
        for( int i=0 ; i<this.ColorArray.length; i++ ){
            if( Array.containsKey( ColorArray[i] )){
                java.util.HashSet<java.lang.Integer> Index = Array.get( ColorArray[i] );
                Index.add(i);
            }
            else{
                java.util.HashSet<java.lang.Integer> Index = new java.util.HashSet<java.lang.Integer>();
                Index.add(i);
                Array.put( ColorArray[i], Index);
            }
        }
        return Array;
    }
    
    public void report(){
        System.out.print("\n\nThese are the color combination\n");
        for( int i=0;i<getColorArray().length;i++){
            System.out.print( " " + getColorArray()[i] );
        }
        System.out.print("\nThese are the unique colors\n");
        for( int i=0;i<getUniqueColors().length;i++){
            System.out.print(" " + getUniqueColors()[i] );
        }
        System.out.println("\nThis is unique color occurence\n");
        for( int i=0;i<this.UniqueColors.length; i++){
            System.out.print( " "+this.UniqueColors[i] +"="+this.ColorsAmount.get( this.UniqueColors[i]));
        }
        System.out.println("\nThis is total unique color::" + getTotalUniqueColors());
        System.out.println("\nThis is variance::" + getVariance() );
    }
    
    private double Variance;    
    private int[] ColorArray;
    private java.lang.Integer[] UniqueColors;
    private java.util.HashMap<java.lang.Integer,java.lang.Integer> ColorsAmount;
}