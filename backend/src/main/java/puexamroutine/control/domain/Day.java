
//                      !! RAM !!

package puexamroutine.control.domain;

/**
 * This class represents the particular examination day.
 * Since, PU uses two shift exams, There are two shift provided.
 * 
 * @author Sumit Shresth
 */
public class Day  implements java.io.Serializable{
    
    public Day( final puexamroutine.control.domain.ExamDayIdentifier ExamID , puexamroutine.control.domain.list.CentreTableForDay t ){
        this.ExamDayID = ExamID;
        this.CentreTable = t;
    }
    
    public boolean canConduct(  puexamroutine.control.domain.Exam exam  ){
        java.util.HashMap<puexamroutine.control.domain.CentreIdentifier, java.lang.Integer> examcentres = exam.getStudentsForExams();
        java.util.Iterator<puexamroutine.control.domain.CentreIdentifier> centreItr = examcentres.keySet().iterator();
        
        while( centreItr.hasNext() ){
            this.CentreTable.getCentre( centreItr.next() ).startTest();
        }
        
        centreItr = examcentres.keySet().iterator();
        while( centreItr.hasNext() ){
            puexamroutine.control.domain.CentreIdentifier centID = centreItr.next();
            boolean canAssign = this.CentreTable.getCentre( centID ).canTestAssign( examcentres.get( centID ) );
            if( ! canAssign )
                return false;
        }       
        
        return true;
    }
    
    public boolean conduct( puexamroutine.control.domain.Exam exam ){
        try{
            java.util.HashMap<puexamroutine.control.domain.CentreIdentifier, java.lang.Integer> examcentres = exam.getStudentsForExams();
            java.util.Iterator<puexamroutine.control.domain.CentreIdentifier> centreItr = examcentres.keySet().iterator();
        
            do{
                puexamroutine.control.domain.CentreIdentifier centID = centreItr.next();
                if( this.CentreTable.getCentre(centID).assign( examcentres.get( centID ) ) ){                    
                    this.Exams.add(exam);
                }
                else
                    System.out.println( exam.getExamCourse() + "cannot be conducted in " + centID.getCentreName() + " exam due to packness");
            }
            while( centreItr.hasNext() );
            
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    public final String getShift(){
        return this.ExamDayID.getShift();
    }
    
    public final int getExaminationDate(){
        return this.ExamDayID.getDate();
    }
    
    public final puexamroutine.control.domain.ExamDayIdentifier getExamID(){
        return this.ExamDayID;
    }
    
    public final void print(){
        java.util.Iterator<puexamroutine.control.domain.Exam> ExamsItr = this.Exams.iterator();
        while( ExamsItr.hasNext() ){
            System.out.println("\t\t"+ ExamsItr.next().getExamCourse() );
        }
    }
    
    public java.util.Collection<puexamroutine.control.domain.Exam> getExams(){
        return this.Exams;
    }
    
    public String[] getExamsAsStringArray(){
        String[] array = new String[this.Exams.size()];
        java.util.Iterator<puexamroutine.control.domain.Exam> ExamsItr = this.Exams.iterator();
        int i = 0;
        while( ExamsItr.hasNext() ){
            array[i++] = ExamsItr.next().getExamCourse().toString();            
        }
        return array;
    }
    
    public puexamroutine.control.domain.CourseCode[] getExamCourses(){
        puexamroutine.control.domain.CourseCode[] c = new puexamroutine.control.domain.CourseCode[ this.Exams.size() ];
        java.util.Iterator<puexamroutine.control.domain.Exam> ExamsItr = this.Exams.iterator();
        int i = 0;
        while( ExamsItr.hasNext() ){
            c[i++] = ExamsItr.next().getExamCourse();
        }
        return c;
    }
    
    /**
     * This method gives if this day has atleast one exam assigned.
     * 
     * @return true if this day has atleast one exam else false
     */
    public boolean isAssigned(){
        return this.Exams.size() > 0;
    }
    
    private puexamroutine.control.domain.ExamDayIdentifier ExamDayID;    
    private puexamroutine.control.domain.list.CentreTableForDay CentreTable;
    private java.util.HashSet<puexamroutine.control.domain.Exam> Exams = new java.util.HashSet<puexamroutine.control.domain.Exam>();
}