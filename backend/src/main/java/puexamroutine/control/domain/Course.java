
//                              !! RAM !!

package puexamroutine.control.domain;

/**
 * This class represents the course of particular group.
 * Before this class, course code was used but it became very difficult to remember and to use course codes.
 * So, this will be more meaniningfull to use
 *
 * @author Sumit Shresth
 */
public class Course implements java.io.Serializable{
    
    public Course( CourseCode Code, Group grp, final String CourseName ){
        this.Code = Code;
        this.grp = grp;
        this.CourseName = CourseName;
    }
    
    public void updateCode( final CourseCode c ){
        this.Code = c;
    }
    
    public void updateGroup( final Group g ){
        this.grp = g;
    }
    
    public void updateCourseName( final String n ){
        this.CourseName = n;
    }
    
    public final CourseCode getCourseCode(){
        return this.Code;
    }
    
    public final Group getGroup(){
        return this.grp;
    }
    
    public final String getCourseName(){
        return this.CourseName;
    }
    
    private CourseCode Code;
    private Group grp;
    private String CourseName;
}