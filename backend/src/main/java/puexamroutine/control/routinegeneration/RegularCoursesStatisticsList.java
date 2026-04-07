
//                                  !! RAM !!

package puexamroutine.control.routinegeneration;

import java.util.Iterator;

/**
 * This class is the list of statistics regarding regular courses location in the graph color list.
 * It holds info for one result of graph coloring.
 * A single graph coloring solution groups the exams within one group with each group having one color.
 * For regular courses of each semester, RegularCoursesStatistics holds the info regarding placement in the color groups.
 * This class lists objects of that class for one combination of graph coloring.
 *
 * @author Sumit Shresth
 */
public class RegularCoursesStatisticsList {
    
    public void setSolution( puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution Soln ){
        this.Source = Soln;
    }
    
    public void addRegularCoursesStatistics( java.util.Collection<puexamroutine.control.routinegeneration.RegularCoursesStatistics> reg ){
        this.List.addAll(reg);
    }
    
    public void addRegularCoursesStatistic( puexamroutine.control.routinegeneration.RegularCoursesStatistics reg ){
        this.List.add(reg);
    }
    
    public java.util.HashSet<puexamroutine.control.routinegeneration.RegularCoursesStatistics> getList(){
        return this.List;
    }
    
    public puexamroutine.control.routinegeneration.RegularCoursesStatistics getRegularCoursesStatisticsFor( puexamroutine.control.domain.RegularCourses reg ){
        java.util.Iterator<puexamroutine.control.routinegeneration.RegularCoursesStatistics> RegItr = this.List.iterator();
        while( RegItr.hasNext() ){
            puexamroutine.control.routinegeneration.RegularCoursesStatistics tmp = RegItr.next();
            if( tmp.getRegularCourses().containsAll( reg.getCourses() ) ){
                return tmp;
            }
        }
        return null;
    }
    
    public final double calculateMeanDisplacement(){
        double sum = 0;
        java.util.Iterator<puexamroutine.control.routinegeneration.RegularCoursesStatistics> ListItr = this.List.iterator();
        while( ListItr.hasNext() ){
            sum += ListItr.next().getOverAllDisplacement();
        }
        this.MeanDisplacement = sum / this.List.size();
        return this.MeanDisplacement;
    }
    
    public final puexamroutine.control.routinegeneration.RegularCoursesStatistics calculateRegularCoursesWithMaxDisplacement(){
        int max = java.lang.Integer.MIN_VALUE;
        RegularCoursesWithMaxDisplacement = null;
        java.util.Iterator<puexamroutine.control.routinegeneration.RegularCoursesStatistics> ListItr = this.List.iterator();
        while( ListItr.hasNext() ){
            puexamroutine.control.routinegeneration.RegularCoursesStatistics tmp = ListItr.next();
            if( tmp.getOverAllDisplacement() > max ){
                max = tmp.getOverAllDisplacement();
                RegularCoursesWithMaxDisplacement = tmp;
            }
        }
        return RegularCoursesWithMaxDisplacement;
    }
    
    public final int getMaxExtremeCourseDisplacement(){        
        if( this.RegularCoursesWithMaxDisplacement == null ) this.calculateRegularCoursesWithMaxDisplacement();
        return this.RegularCoursesWithMaxDisplacement.getOverAllDisplacement();        
    }

    public final double calculateDisplacementVariance(){
        double first = 0;
        double sec = 0;
        java.util.Iterator<puexamroutine.control.routinegeneration.RegularCoursesStatistics> ListItr = this.List.iterator();
        while( ListItr.hasNext() ){
            double disp = ListItr.next().getOverAllDisplacement();
            first += disp;
            sec += disp * disp;
        }
        double sec1 = sec / this.List.size();
        this.DisplacementVariance = first/this.List.size() - sec1 * sec1;
        return this.DisplacementVariance;
    }
    
    public final double calculateMinIndexVariance(){
        double first = 0;
        double sec = 0;
        java.util.Iterator<puexamroutine.control.routinegeneration.RegularCoursesStatistics> ListItr = this.List.iterator();
        while( ListItr.hasNext() ){
            double disp = ListItr.next().getAverageIndex();
            sec += disp;
            first += disp * disp;
        }
        double sec1 = sec / this.List.size();
        this.MidIndexVariance = first/this.List.size() - sec1 * sec1;        
        //if( this.MidIndexVariance < 0 )
            this.print("first",first+"","sec",sec+"","sec1",sec1+"","var",this.MidIndexVariance+"");
        return this.MidIndexVariance;
    }
    
    public final double getDisplacementVariance(){
        if( this.DisplacementVariance == java.lang.Double.MIN_VALUE ) this.calculateDisplacementVariance();
        return this.DisplacementVariance;
    }
    
    public final puexamroutine.control.routinegeneration.RegularCoursesStatistics getRegularCoursesWithMaxDisplacement(){        
        return this.RegularCoursesWithMaxDisplacement;
    }
    
    public final double getMeanDisplacement(){
        if( this.MeanDisplacement == java.lang.Double.MIN_VALUE ) this.calculateMeanDisplacement();
        return this.MeanDisplacement;
    }
    
    public final int getTotalCourses(){        
        return this.List.size();
    }
    
    public final boolean isEmpty(){
        return this.List.isEmpty();
    }
    
    public final puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution getSolution(){
        return this.Source;
    }
    
    public final double getAverageIndexVariance(){
        if( this.MidIndexVariance == java.lang.Double.MIN_VALUE ) this.calculateMinIndexVariance();
        return this.MidIndexVariance;
    }
    
    public double getOptimizeValue(){
        return this.getMaxExtremeCourseDisplacement() * this.getMeanDisplacement() * this.getTotalCourses();// * this.getAverageIndexVariance();
    }
    
    public int calculateMinIndexAmongList(){
        int listMin = java.lang.Integer.MAX_VALUE;
        java.util.Iterator<puexamroutine.control.routinegeneration.RegularCoursesStatistics> listItr = this.List.iterator();
        while( listItr.hasNext() ){
            int min = listItr.next().getMinIndex();
            if( listMin > min ){
                listMin = min;
            }
        }
        return listMin;
    }
    
    public int calculateMaxIndexAmongList(){
        int listMax = java.lang.Integer.MIN_VALUE;
        java.util.Iterator<puexamroutine.control.routinegeneration.RegularCoursesStatistics> listItr = this.List.iterator();
        while( listItr.hasNext() ){
            int max = listItr.next().getMinIndex();
            if( listMax < max ){
                listMax = max;
            }
        }
        return listMax;
    }
    
    public int getMinimumIndexOfList(){
        if( this.Min_Index == java.lang.Integer.MIN_VALUE ) this.calculateMinIndexAmongList();
        return this.Min_Index;
    }
    
    public int getMaximumIndexOfList(){
        if( this.Max_Index == java.lang.Integer.MIN_VALUE ) this.calculateMaxIndexAmongList();
        return this.Max_Index;
    }
    
    void print(String... t){
        System.out.println("\nThis is printing in variance");
        for( int i=0; i<t.length; i+=2 ){
            System.out.println(t[i]+"::"+t[i+1]);
        }
    }
    
    /**
     * This method will be used by feasibility checker of analyzer to find the feasible color combination
     * The feasibility is described in its function.
     * This method will give the maximum difference between any pair of close course in all program and its semester of this group represented by it.
     * 
     * @return maximum close course displacement among all programs in this group
     */
    public final int getMaximumCloseIndexDisplacement(){        
        java.util.Iterator<puexamroutine.control.routinegeneration.RegularCoursesStatistics> ListItr = this.List.iterator();        
        return getMaximumCloseIndexDisplacement( ListItr );
    }
    
    java.util.HashSet<puexamroutine.control.routinegeneration.RegularCoursesStatistics> List = new java.util.HashSet<puexamroutine.control.routinegeneration.RegularCoursesStatistics>();
    private double MeanDisplacement = java.lang.Double.MIN_VALUE;
    private puexamroutine.control.routinegeneration.RegularCoursesStatistics RegularCoursesWithMaxDisplacement=null;
    private double DisplacementVariance= java.lang.Double.MIN_VALUE;
    private puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution Source=null;
    private double MidIndexVariance = java.lang.Double.MIN_VALUE;
    private int Min_Index=java.lang.Integer.MIN_VALUE, Max_Index=java.lang.Integer.MIN_VALUE;

    private int getMaximumCloseIndexDisplacement(Iterator<RegularCoursesStatistics> ListItr ) {
        int max = java.lang.Integer.MIN_VALUE;
        while (ListItr.hasNext()) {
            puexamroutine.control.routinegeneration.RegularCoursesStatistics tmp = ListItr.next();
            if (tmp.getMaximumCloseCourseIndexDisplacement() > max) {
                max = tmp.getMaximumCloseCourseIndexDisplacement();
            }
        }
        return max;
    }
    
}