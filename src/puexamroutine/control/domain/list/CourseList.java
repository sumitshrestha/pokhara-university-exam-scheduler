
//                          !! RAM !!

package puexamroutine.control.domain.list;

import puexamroutine.control.domain.*;

/**
 * This class is list of the courses and not just of a particular group.
 *
 * @author Sumit Shresth
 */
public class CourseList implements java.io.Serializable{
    
    public void addCourse( Course c ){
        this.Courses.put( c.getCourseCode() , c);
    }
    
    public java.util.Collection<Course> getCourses(){
        return this.Courses.values();
    }
    
    public java.util.Collection<CourseCode> getCourseCodes(){
        return this.Courses.keySet();
    }
    
    public java.util.Map<CourseCode,String> getCourses( Group grp ){
        java.util.Iterator<CourseCode> cItr = this.Courses.keySet().iterator();
        java.util.Map<CourseCode, String> grpCourses = new java.util.HashMap<CourseCode, String>();
        while( cItr.hasNext() ){
            CourseCode c = cItr.next();
            if( this.strongGet(c).getGroup() == grp )
                grpCourses.put(c, this.strongGet(c).getCourseName() );
        }
        return grpCourses;
    }
    
    public boolean isEmpty(){
        return this.Courses.isEmpty();
    }
    
    public final String get( CourseCode c ){
        return (this.Courses.get(c) != null) ? this.Courses.get(c).getCourseName() : this.strongGet(c).getCourseName();
    }
    
    private final Course strongGet( CourseCode c ){
        java.util.Iterator<CourseCode> cItr = this.Courses.keySet().iterator();
        while( cItr.hasNext() ){
            CourseCode tempc = cItr.next();
            if( tempc.equals( c )){
                return this.Courses.get( tempc );
            }
        }
        return null;
    }
    
    public final java.util.Collection<CourseCode> keySet(){
        return this.getCourseCodes();
    }
    
    public void clear(){
        this.Courses.clear();
    }
    
    public Group getGroup( CourseCode c ){
        return this.strongGet(c).getGroup();
    }
        
    private java.util.HashMap< CourseCode, Course > Courses = new java.util.HashMap< CourseCode, Course >();
}