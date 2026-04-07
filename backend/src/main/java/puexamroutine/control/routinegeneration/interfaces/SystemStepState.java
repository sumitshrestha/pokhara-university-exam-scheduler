
//                              !! RAM !!

package puexamroutine.control.routinegeneration.interfaces;

/**
 * This class represents the result of the controller schedule.
 * The controller is a black box that simply does the job.
 * But, This is not preferable since the time taken to process may be long and mainly unpredictable.
 * This mainly is true for the grouper and especially to its coloring step
 * So, There are many steps through which the system undergoes where some are predictable while some unpredictable
 * So, To make user informed of actions taking place in this back box, This event class is designed.
 * This class gives the info of the state in which the system is.
 * The user of this system can then take reaction depending of it.
 * So, This class holds very important value in dealing with user interface which will be user of this black box system.
 * 
 * @author Sumit Shresth
 */
public class SystemStepState implements Cloneable{
    
    public SystemStepState(){        
    }
    
    public void setPredictable( boolean st ){
        this.Predictable = st;
        this.resetError();
    }
    
    public final void setWorkPercent( int complete ){
        this.PercentWorkCompleted = complete;
        this.resetError();
    }
    
    public final void setStep( final String step ){
        this.Step = step;
        this.resetError();
    }
    
    public final void setStepCause( final String cause ){
        this.RelatedData = cause;
        this.resetError();
    }
    
    public final void setError( final String error ){
        this.isError = true;
        this.Error = error;
    }
        
    public boolean isPredictable(){
        return this.Predictable;
    }
    
    public final int getWorkPercent(){
        return this.PercentWorkCompleted;
    }
    
    public final String getStep(){
        return this.Step;
    }
    
    public final String getStepCause(){
        return this.RelatedData;
    }
    
    public final boolean isComplete(){
        return this.getWorkPercent() == 100;
    }
    
    public final String getError(){
        return this.Error;
    }
    
    public final boolean isError(){
        return this.isError;
    }
    
    public final void resetError(){
        this.Error = null;
        this.isError = false;
    }
    
    @Override
    public SystemStepState clone(){
        try{
            return (SystemStepState) super.clone();
        }
        catch( java.lang.CloneNotSupportedException e ){
            return null;
        }
    }

    /**
     * This field gives if this step is predictable or not.
     * If not predictable then user can be more carefull to it since it may cause system resource to be used up causing runtime exception
     * In such case, user must inform user about it.
     * It is mainly for coloring step where things are really very unpredictable
     */
    private boolean Predictable;
    /**
     * This field holds the total work that is completed till now in percentage.
     */
    private int PercentWorkCompleted;
    /**
     * This method gives the name of step where the system is presently in.
     */
    private String Step;
    /**
     * This field is for giving the data that is causing this step.
     * Suppose routine of engineering is being prepared.
     * So, in that case it will hold that value
     * But, for some step there will be no particular user data so null is perfectly valid
     */
    private String RelatedData;
    private String Error;
    private boolean isError = false;
}