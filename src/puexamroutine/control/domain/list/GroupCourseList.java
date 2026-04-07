
//                  !! RAM !!

package puexamroutine.control.domain.list;

import puexamroutine.control.domain.CourseCode;

/**
 * This class represents the list of related courses for a known group.
 * The Related courses must be easily accessible as they are very frequently used in scheduling.
 * 
 * @author Sumit Shresth
 */
public class GroupCourseList implements java.io.Serializable{
    
    public GroupCourseList( puexamroutine.control.domain.Group group ){        
        this.group = group;
    }    
    
    /**
     * This is nm important short function.
     * It is the only generator of course ocde.
     * Others will get it from this class through getter functions.
     * 
     * @param c string course to be converted into course code.
     */
    public void addCourse( final String c ){
        //System.out.println(this.group.toString()+" "+c);
        this.groupCourses.put( c, new puexamroutine.control.domain.CourseCode(c) );
    }
    
    public void addCourses( final String[] c ){
        for( int i=0; i<c.length; i++ )
            this.addCourse( c[i] );
    }
    
    public void addRegularCourse( final String C ){
        this.RegularCourses.put( C, this.groupCourses.get(C) );
    }
    
    public void addRegularCourses( final String[] C ){
        for( int i=0; i<C.length; i++ )
            this.addRegularCourse( C[i] );
    }
    
    public puexamroutine.control.domain.CourseCode getRegularCourse( String Course ){
        return this.RegularCourses.get( Course.replaceAll( " ", "" ) );
    }
    
    public java.util.Collection<puexamroutine.control.domain.CourseCode> getBackCourses(){
        return this.BackCourses.values();
    }
    
    public puexamroutine.control.domain.CourseCode getBackCourse( final String BkCourse ){
        return this.BackCourses.get( BkCourse );
    }
    
    public void addBackCourse( final String c ){
        this.BackCourses.put( c, this.groupCourses.get(c) );
    }
    
    public void addBackCourses( final String[] c ){
        for( int i=0; i<c.length; i++ )
            this.addBackCourse( c[i] );
    }
    
    public java.util.Collection<puexamroutine.control.domain.CourseCode> getRegularCourses(){
        return this.RegularCourses.values();
    }
    
    public puexamroutine.control.domain.CourseCode[] getRegularCoursesArray(){
        return this.RegularCourses.values().toArray( new puexamroutine.control.domain.CourseCode[]{} );
    }
    
    public final puexamroutine.control.domain.Group getGroup(){
        return this.group;
    }
    
    public final boolean regularCourseExists( final String CourseCode ){
        return this.RegularCourses.containsKey( CourseCode );
    }
    
    public boolean regularCourseExists( puexamroutine.control.domain.CourseCode c ){
        return this.RegularCourses.values().contains(c) || this.checkStrictly(c);        
    }
    
    private boolean checkStrictly(CourseCode c) {
        java.util.Iterator<puexamroutine.control.domain.CourseCode> it = this.RegularCourses.values().iterator();
        while (it.hasNext()) {
            if (it.next().getCourseCode().equals(c.getCourseCode())) {
                return true;
            }
        }
        return false;
    }
    
    public java.util.Collection<puexamroutine.control.domain.CourseCode> getCourses(){
        return this.groupCourses.values();
    }
    
    public java.util.Collection<String> getCoursesAsString(){
        return this.groupCourses.keySet();
    }
    
    public boolean exists( puexamroutine.control.domain.CourseCode course  ){
        return this.regularCourseExists(course) || this.BackCourses.values().contains( course  );
    }
    
    private final puexamroutine.control.domain.Group group;
    private java.util.HashMap<String, puexamroutine.control.domain.CourseCode> RegularCourses = new java.util.HashMap<String,puexamroutine.control.domain.CourseCode>();
    private java.util.HashMap<String, puexamroutine.control.domain.CourseCode > BackCourses = new java.util.HashMap<String, puexamroutine.control.domain.CourseCode>();
    private java.util.HashMap<String, puexamroutine.control.domain.CourseCode> groupCourses = new java.util.HashMap<String, puexamroutine.control.domain.CourseCode>();
}