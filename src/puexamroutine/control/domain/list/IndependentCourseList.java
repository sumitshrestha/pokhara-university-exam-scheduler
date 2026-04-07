
//                              !! RAM !!

package puexamroutine.control.domain.list;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import puexamroutine.control.domain.CourseCode;
import puexamroutine.control.domain.Group;
import puexamroutine.control.domain.IndependentCourses;
import puexamroutine.control.domain.interfaces.DependentCourses;

/**
 * This class is list of all independent courses for specified group
 * It encapsulates the list of independent courses into a class(unit).
 * The independent courses are obtained when graph colorer colors and groups them such that each of exam within same color can be conducted in same day
 * So, This class holds the info on it.
 *
 * @author Sumit Shresth
 */
public class IndependentCourseList implements java.io.Serializable{
    
    public IndependentCourseList( final Group grp ){
        this.Group = grp;
    }

    public void addIndependentCourse(int color, IndependentCourses Idp){
        this.List.add(Idp);
        this.IndpCoursesColor.put(Idp, color);
    }

    /**
     * This method adds specified regular course iff it is not present in the list.
     * Non repeating means that the regular course must be either logically or physically equal to any one regular course in list.
     * For more on regular course equality see Docs in regular course
     *
     * @param reg specified regular course to be be added
     * @return true if added successfully else false
     */
    public boolean addDependentCourse( DependentCourses reg){
        if( ! this.alreadyContains(reg) )
            return this.DependentCourseList.add(reg);
        else 
            return false;
    }

    /**
     * This method adds only non repeating regular courses in the list.
     * Non repeating means that the regular course must be either logically or physically equal to any one regular course in list.
     * (See more on addRegularCourse Docs)
     *
     * @param reg specified regular course list to be added
     * @return true if no error while adding else false on error or exception
     */
    public boolean addDependentCourses(Collection<DependentCourses> reg){
        try{
            java.util.Iterator<DependentCourses> regItr = reg.iterator();
            while( regItr.hasNext() ){
                this.addDependentCourse( regItr.next() );
            }
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public HashSet<DependentCourses> getDependentCourses(){
        return this.DependentCourseList;
    }

    public DependentCourses[] getDependentCoursesArray(){
        return this.DependentCourseList.toArray( new DependentCourses[]{} );
    }

    public boolean contains(IndependentCourses Reg) {
        return this.List.contains(Reg);
    }
    
    public void clearLists() {
        this.clearIndependentCourses();
        this.clearRegularCoursesList();
    }

    /**
     * This method gives the total group of regular courses in this.
     *
     * @return total group of regular courses in this
     */
    public int getTotalIndependentCoursesGroup() {
        return this.List.size();
    }

    public void clearIndependentCourses() {
        this.List.clear();
    }

    public void clearRegularCoursesList() {
        this.DependentCourseList.clear();
    }

    public final int getColor(CourseCode Course) {
        IndependentCourses idp = this.getIndependentCourse(Course);
        if( idp == null )return -1;
        return this.IndpCoursesColor.get(idp);
    }
    
    public final int getIndex( CourseCode Course ){        
        int course_color = this.getColor(Course);        
        
        if( this.combination_array == null ){
            return course_color;
        }
        else
            return this.combination_array[ course_color - 1 ] + 1;//course color starts from 1 but array index starts from 0
    }
    
    public IndependentCourses getIndependentCourse( CourseCode c ){
        Iterator<IndependentCourses> itr = this.getIndependentCoursesIterator();
        while( itr.hasNext() ){
            IndependentCourses temp = itr.next();
            if( temp.contains(c) )
                return temp;
        }
        return null;
    }

    /**
     * This method returns the iterator of the independent courses to iterate in linear fashion.
     * This is used in creating exam time table once the graph ooloring is done and only thing required is creating table to be printed.
     * Due to involvement of the specific combination, this method is elaborated to include it.
     * Initialy, it was just a set of independent courses.
     * But, when putting it as time table they have to be linearized.
     * often one linear combination works better than other with respect to routine feasbility
     * so, the proper combination is given by combination_array. if it is null, it is not set or no proper combination is required in which case default combination is used
     * 
     * @return interator of independent courses in specific order as by combination_array else random combination
     */
    public Iterator<IndependentCourses> getIndependentCoursesIterator() {
        
        if( this.combination_array == null )
            return this.List.iterator();
        else{
            /*java.util.ArrayList<IndependentCourses> list = new java.util.ArrayList<IndependentCourses>();
            for( int i=0;i<this.List.size(); i++ ){
                list.add( this.List.get( this.combination_array[i] ));
            }*/
            IndependentCourses[] list = new IndependentCourses[ this.List.size() ];
            for( int i=0; i< list.length; i++ ){
                list[ this.combination_array[i] ] = this.List.get(i);
            }
            return java.util.Arrays.asList( list ).iterator();
        }
    }

    public Group getGroup() {
        return this.Group;
    }
/*
    public IndependentCourses[] getArrayedIndependentRegularCourses() {
        return this.List.toArray(new IndependentCourses[]{});
    }
*/
    public Collection<IndependentCourses> getRegularCoursesCollection() {
        return this.List;
    }
/*
    public IndependentCourses getIndependentRegularCourse(int i) {
        return (IndependentCourses) this.List.toArray()[i];
    }

    public CourseCode getCourse(int i, int j) throws Exception {
        return this.getIndependentRegularCourse(i).getCourseCode(j);
    }
  */  
    protected boolean checkStrongly( DependentCourses reg) {
        java.util.Iterator<DependentCourses> AvailRegItr = this.DependentCourseList.iterator();
        while (AvailRegItr.hasNext()) {
            if (AvailRegItr.next().equals(reg)) {
                return true;
            }
        }
        return false;
    }    
    
    /**
     * This method checks if the specified regular course is already in the added regular courses list.
     * This method is called before adding the specified regualar course in the list.
     * The main reason for it is that one program can be taught by multiple colleges n regular courses within it may match.
     * In such cases it is not necessary to include such repeating regualar courses in the list. 
     * This list will be used later while checking the feasibility and optimality of the graph coloring solution.
     * Thus, this method has high significance in performance optimization.
     * 
     * @param reg specified regular course to be checked with
     * @return true if already contains else false
     */
    protected boolean alreadyContains( DependentCourses reg ){
        if( this.DependentCourseList.contains(reg)){
            return true;
        }
        
        return checkStrongly(reg);
    }
    
    // 10/12/2008 added code
    /**
     * This method sets the combination of the independent courses acconrding to position set.
     * This class does not deal with appropiate combination of independent courses, it only keeps it and gives access.
     * when putting it as time table they have to be linearized.
     * often one linear combination works better than other with respect to routine feasbility
     * so, the proper combination is given by combination_array.
     * This combination is performed by some other class eg independentCourseAnalyser who finds combination and sets through this.
     * 
     * @param array the location array having location of each index in independent size so that array.length == total independent courses
     * 
     * @return true if set else false on error while setting due to some reasons like wrongly specified array 
     */
    public boolean setCombination( int[] array ){
        try{
            if( array.length != this.getTotalIndependentCoursesGroup() ){
                this.combination_array = null;
                return false;
            }
            this.combination_array = array;
            return  true;
        }
        catch( Exception e ){
            this.combination_array = array;
            return false;
        }
    }

    private final puexamroutine.control.domain.Group Group;
    private java.util.HashMap<puexamroutine.control.domain.IndependentCourses,java.lang.Integer> IndpCoursesColor = new java.util.HashMap<puexamroutine.control.domain.IndependentCourses,java.lang.Integer >();
    private java.util.ArrayList<puexamroutine.control.domain.IndependentCourses> List = new java.util.ArrayList<puexamroutine.control.domain.IndependentCourses >();
    private java.util.HashSet<puexamroutine.control.domain.interfaces.DependentCourses> DependentCourseList = new java.util.HashSet<puexamroutine.control.domain.interfaces.DependentCourses >();
    
    // 10/12/2008 added code for combination
    private int[] combination_array = null;
}