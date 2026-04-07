
//                              !! RAM !!

package puexamroutine.control.domain.routine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import puexamroutine.control.domain.Day;
import puexamroutine.control.domain.ExamDayIdentifier;

/**
 * This class represents the entire calander of examination days.
 * It represents a serial list of days.
 * It encapsulates necessry info for creating, scheduling examination days.
 * 
 * @author Sumit Shresth
 */
public class ExaminationDayCalander implements java.io.Serializable{

    private static final Logger LOGGER = LoggerFactory.getLogger(ExaminationDayCalander.class);
    
    public ExaminationDayCalander( puexamroutine.control.domain.list.CentreTable centreTab ){
        this.resetCalanderLocation();        
        this.CentreTable = centreTab;
    }
    
    /**
     * This method gets the next day for the exam 
     * 
     * @return next exam day that is generated
     */
    public puexamroutine.control.domain.Day generateNextExamDay(){
        puexamroutine.control.domain.Day next = this.increment();
        if( this.isDayShift() ){ // if it is still this day only shift is changed
            next = this.increment();            
        }
        return next;
    }
    
    /**
     * This method next exam for the system.
     * 
     * @return next exam day
     */
    public puexamroutine.control.domain.Day generateNextExam(){
        return this.increment();
    }

    /**
     * This method generates the day represented by current state of the calander i.e. day and shift.
     * It then adds it to the list.
     * For same state it must not be called twice as it adds same value twice.
     * 
     * @return The current day
     */
    private Day generateCurrentDay() {
        puexamroutine.control.domain.ExamDayIdentifier exam = new puexamroutine.control.domain.ExamDayIdentifier(this.getCurrentShift(), this.getCurrentDayIndex() );
        puexamroutine.control.domain.list.CentreTableForDay centresTable = new puexamroutine.control.domain.list.CentreTableForDay(this.CentreTable.getCentres());
        puexamroutine.control.domain.Day CurrentDay = new puexamroutine.control.domain.Day(exam, centresTable);
        this.ExamCalander.put(exam, CurrentDay);
        this.ExamList.add(exam);
        return CurrentDay;
    }
    
    /**
     * This method increments the calander appropiately.
     * 
     * @return day that formed after incrementing
     */
    private puexamroutine.control.domain.Day increment(){        
        this.CurrentCounter ++;
        
        if( this.CurrentCounter == this.ExamList.size() + 1 )
            return this.generateCurrentDay();
        else
            return this.getExamDayAtCurrentLocation();
    }
    
    public boolean isMorningShift(){
        return !this.isDayShift();
    }
    
    public boolean isDayShift(){
        return this.CurrentCounter % 2 == 0;
    }
    
    public int getCurrentDayIndex(){
        return (int) java.lang.Math.floor( ( CurrentCounter + 1 ) / 2 );
    }

    public java.util.Set<ExamDayIdentifier> getExamDays(){
        return this.ExamCalander.keySet();
    }
    
    public puexamroutine.control.domain.Day getExamDay(puexamroutine.control.domain.ExamDayIdentifier ID ){
        return this.ExamCalander.get(ID);
    }
    
    /**
     * This method gets the total duration of exam.
     * 
     * @return total exam days
     */
    public int getTotalExamDays(){
        return (int) java.lang.Math.floor( this.ExamList.size() /2 );
    }
    
    public boolean setCalanderCounter( int count ){
        if( count >= 0 ){
            this.CurrentCounter = count * 2;
            return true;
        }
        else
            return false;
    }
    
    /**
     * This method gives the exam day at the current calander location.
     * This helps in processing the exams.
     * 
     * @return The exam day at the current location
     */
    public puexamroutine.control.domain.Day getExamDayAtCurrentLocation(){
        return this.ExamCalander.get( this.ExamList.get(CurrentCounter - 1 ) );
    }
    
    public final String getCurrentShift(){
        if( this.isMorningShift() ){
            return this.MORNING;
        }
        else
            return this.DAY;
    }
    
    public void print(){
        java.util.Iterator<puexamroutine.control.domain.ExamDayIdentifier> ExamDayItr = this.ExamList.iterator();
        int i=1;
        while( ExamDayItr.hasNext() ){
            puexamroutine.control.domain.Day ExamDay = this.ExamCalander.get( ExamDayItr.next() );
            LOGGER.info("{}th Day {}", i, ExamDay.getShift());
            ExamDay.print();
            if( ExamDay.getShift() == this.DAY ){
                i++;
            }
        }
    }
    
    public java.util.Iterator<puexamroutine.control.domain.ExamDayIdentifier> getCalanderIterator(){
        return this.ExamList.iterator();
    }
    
    public void resetCalanderLocation(){
        this.CurrentCounter = 0;
    }
    
    
    public static final String MORNING="10-1", DAY="2-5";
    
    private int CurrentCounter;
    private puexamroutine.control.domain.list.CentreTable CentreTable;
    java.util.ArrayList<puexamroutine.control.domain.ExamDayIdentifier> ExamList = new java.util.ArrayList<puexamroutine.control.domain.ExamDayIdentifier>();
    private java.util.HashMap<puexamroutine.control.domain.ExamDayIdentifier, puexamroutine.control.domain.Day> ExamCalander = new java.util.HashMap<puexamroutine.control.domain.ExamDayIdentifier, puexamroutine.control.domain.Day>();
}