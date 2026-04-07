
//                                      !! RAM !!

package puexamroutine.control.domain.list;

import java.util.Collection;
import puexamroutine.control.domain.RegularCourses;
import puexamroutine.control.domain.interfaces.DependentCourses;

/**
 * This class is list of all set of independent regular courses for one group.
 * It encapsulates the list of independent regular courses into a class(unit).
 *
 * @author Sumit Shrestha
 */
public class IndependentRegularCourseList extends IndependentCourseList implements java.io.Serializable{
    
    public IndependentRegularCourseList( puexamroutine.control.domain.Group grp ){
        super( grp );        
    }
        
    /**
     * This method adds only non repeating regular courses in the list.
     * Non repeating means that the regular course must be either logically or physically equal to any one regular course in list.
     * (See more on addRegularCourse Docs)
     * 
     * @param reg specified regular course list to be added
     * @return true if no error while adding else false on error or exception
     */
    public boolean addRegularCourses( java.util.Collection<puexamroutine.control.domain.RegularCourses> reg ){
        try{
            java.util.Iterator<puexamroutine.control.domain.RegularCourses> regItr = reg.iterator();
            while( regItr.hasNext() ){
                this.addRegularCourse( regItr.next() );
            }
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    /**
     * This method adds specified regular course iff it is not present in the list.
     * Non repeating means that the regular course must be either logically or physically equal to any one regular course in list.
     * For more on regular course equality see Docs in regular course
     * 
     * @param reg specified regular course to be be added
     * @return true if added successfully else false
     */
    public boolean addRegularCourse( puexamroutine.control.domain.RegularCourses reg ){
        return super.addDependentCourse(reg);
    }
    
    public java.util.HashSet<puexamroutine.control.domain.RegularCourses> getRegularCourses(){
        java.util.Iterator<puexamroutine.control.domain.interfaces.DependentCourses> RegItr = super.getDependentCourses().iterator();
        java.util.HashSet<RegularCourses> Reg = new java.util.HashSet<RegularCourses>();
        while( RegItr.hasNext() ){
            Reg.add( (RegularCourses) RegItr.next() );
        }
        return Reg;
    }
    
    public puexamroutine.control.domain.RegularCourses[] getRegularCoursesArray(){
        return super.getDependentCourses().toArray( new RegularCourses[]{} );
    }
    
    /**
     * This method is overriding of super class implementation.
     * This was done since any other non regular class implementing dependent course interface can use this function
     * But, Later when regular course is used it cannot be casted back creating problem
     * So, This overriding gets rid of that problem and simply throws exception if done so
     * 
     * @param reg specified regular course to be added as dependent course
     * @return true if added else false
     * @throws java.lang.ClassCastException if non regular course class attempted to add
     */
    @Override
    public boolean addDependentCourse( DependentCourses reg) throws java.lang.ClassCastException{        
        return super.addDependentCourse(( RegularCourses ) reg );
    }
    
    /**
     * This method overrides the super class implementation of same method
     * This is done since any non regular course class implementing dependent course class can attempt to add
     * This creates problem during later casting and is to be avoided
     * 
     * @param reg specified collection of regular course to be added
     * @return true if added
     * @throws java.lang.ClassCastException if attempting to add any one regular course in collection throws exception
     */
    @Override
    public boolean addDependentCourses(Collection<DependentCourses> reg) throws java.lang.ClassCastException{        
        java.util.Iterator<DependentCourses> regItr = reg.iterator();
        while( regItr.hasNext() ){
            this.addDependentCourse( regItr.next() );
        }
        return true;        
    }
    
}