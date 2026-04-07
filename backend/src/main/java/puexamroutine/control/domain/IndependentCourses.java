
//                              !! RAM !!

package puexamroutine.control.domain;

/**
 * This class is set of independent courses of same college's program.
 * Idependent courses means those class that are not dependent so that no student of that college's program has two course within same set of independent courses.
 * So, This helps to appropiately group regular courses.
 * This is obtained only after graph coloring step which groups the courses with same color.
 * This class is introduced as a means of encapsulating the independent courses which was done using array but introduced problems.
 *
 * @author Sumit Shresth
 */
public class IndependentCourses implements java.io.Serializable{
    
    public boolean addIndepentRegularCourses( java.util.Collection<puexamroutine.control.domain.CourseCode> IdpCourse ){
        return this.IndependentCourses.addAll(IdpCourse);
    }
    
    public boolean addIndepentRegularCourse( puexamroutine.control.domain.CourseCode IdpCourse ){
        return this.IndependentCourses.add(IdpCourse);
    }
    
    public puexamroutine.control.domain.CourseCode getCourseCode( int i ) throws Exception{
        return (puexamroutine.control.domain.CourseCode)this.IndependentCourses.toArray()[i];
    }
    
    public java.util.Collection<puexamroutine.control.domain.CourseCode> getIndependentCourses(){
        return this.IndependentCourses;
    }
    
    public puexamroutine.control.domain.CourseCode[] getArrayedIndependentCourses(){
        return this.IndependentCourses.toArray( new puexamroutine.control.domain.CourseCode[]{} );
    }
    
    public boolean contains( puexamroutine.control.domain.CourseCode c ){
        return this.IndependentCourses.contains(c);
    }

    private java.util.HashSet< puexamroutine.control.domain.CourseCode > IndependentCourses= new java.util.HashSet<puexamroutine.control.domain.CourseCode>();
}