
//                                  !! RAM !!

package puexamroutine.control.domain;

/**
 * This class represents an unique identifier for the day.
 * This class is final so that it cannot be modified neither its clone can be obtained.
 * It has only getter functions to read its value.
 * 
 * @author Sumit Shrestha
 */
public record ExamDayIdentifier(String shift, int date) implements java.io.Serializable {

    public final String getShift(){
        return this.shift;
    }

    public final int getDate(){
        return this.date;
    }

    @Override
    public final String toString(){
        return this.date + " : " + this.shift;
    }

    //private final java.util.Date StartDate;
}