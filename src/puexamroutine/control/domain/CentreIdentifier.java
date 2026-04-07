
//                                  !! RAM !!

package puexamroutine.control.domain;

/**
 *
 * This final class is the unique identifier of the examination centre.
 * It will uniquely represent an exam centre of PU.
 * 
 * @author Sumit Shresth
 */
public final class CentreIdentifier implements java.io.Serializable{
    
    public CentreIdentifier( final String n, final int MaxLimit ){
        this.name = n.trim().toUpperCase();
        this.MaxLimit = MaxLimit;
    }
    
    public final String getCentreName(){
        return this.name;
    }
    
    public final int getMaximumCentreLimit(){
        return this.MaxLimit;
    }

    private final String name;
    private final int MaxLimit;
}