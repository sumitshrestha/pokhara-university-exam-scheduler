
//                                  !! RAM !!

package puexamroutine.control.domain;

/**
 * This class represents an unique identifier for the day.
 * This class is final so that it cannot be modified neither its clone can be obtained.
 * It has only getter functions to read its value.
 * 
 * @author Sumit Shrestha
 */
public final class ExamDayIdentifier implements java.io.Serializable{
    
    public ExamDayIdentifier( final String S, final int d ){
        this.Shift = S;
        this.date = d;
        //this.StartDate = stdt;
    }
    
    public final String getShift(){
        return this.Shift;
    }
    
    public final int getDate(){
        return this.date;
    }
    
    @Override
    public final String toString(){
        return this.date + " : " + this.Shift ;
    }

    private final String Shift;
    private final int date;
    //private final java.util.Date StartDate;
}