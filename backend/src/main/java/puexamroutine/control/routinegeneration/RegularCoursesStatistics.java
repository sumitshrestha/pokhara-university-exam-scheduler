
//                          !! RAM !!

package puexamroutine.control.routinegeneration;

/**
 * This class is used by the graph color analyzer to encapsulate some important information regarding regular courses.
 * It is especially for the regular courses whose placement in graph coloring have problem for scheduling.
 * The main problem is the gap. It must not cross the max limit.
 * So, The main aim of analyzer is to choose the combination having serial exams.
 * But, Working with the data has finally proved that it is impossible.
 * So, The combination that have near serial exams have to be choosen.
 * So, For this necessary data is needed by analyzer. Based on these it calculates feasibility.
 * Each regular courses must be represented adequetly for this purpose.
 * 
 * This class keeps the coloring placement of each course withing a set of regular course of a semester of program
 *
 * @author Sumit Shresth
 */
public class RegularCoursesStatistics {
    
    public RegularCoursesStatistics( java.util.Collection<puexamroutine.control.domain.CourseCode> Reg ){
        this.RegularCourses = Reg;
    }
    
    public void setMaxIndex( final int max ){
        this.MaxIndex = max;
    }
    
    public void setMinIndex( final int min ){
        this.MinIndex = min;
    }
    
    /**
     * This method sets the Maximum displacement between any two close course in this regular course.
     * First, Close course means What??
     * In the color assignment for this regular course the index of color is non reapeating i.e. 0 to course1, (1 or greater but not 0 ) to course2, and so on. 
     * So, course1 and course2 are close because they have minimum displacement between each other though there may be displacement between one course and any other course but there must exists almost two course close to each other one rightwards and other left wareds.
     * This minimum difference that caused the two courses to be close, I call as displacement between two close courses.
     * Between each pair of close courses, each will have displacement value.
     * This method will hold the maximum of all displacement value since maximum is enough here for further processing.
     * 
     * @param disp The maximum displacement between close courses in this regular courses.
     */
    public void setMaxCloseCourseIndexDisplacement( final int disp ){
        this.CloseCourseIndexDisplacement = disp;
    }
    
    public java.util.Collection<puexamroutine.control.domain.CourseCode> getRegularCourses(){
        return this.RegularCourses;
    }
    
    public final int getMaxIndex(){
        return this.MaxIndex;
    }
    
    public final int getMinIndex(){
        return this.MinIndex;
    }
    
    public final int getExtremeCoursesDifference(){
        return this.MaxIndex - this.MinIndex + 1;
    }
    
    public final boolean isSerial(){
        return this.getOverAllDisplacement() == 0;
    }
    
    public final int getOverAllDisplacement(){
        return this.getExtremeCoursesDifference() - this.RegularCourses.size();
    }
    
    public final float getAverageIndex(){
        return (float)this.getExtremeCoursesDifference() / 2;
    }
    
    public int getMaximumCloseCourseIndexDisplacement(){
        return this.CloseCourseIndexDisplacement;
    }
    
    private java.util.Collection<puexamroutine.control.domain.CourseCode> RegularCourses;
    private int MaxIndex, MinIndex, CloseCourseIndexDisplacement;
}