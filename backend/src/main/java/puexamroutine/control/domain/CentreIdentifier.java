
//                                  !! RAM !!

package puexamroutine.control.domain;

/**
 *
 * This final class is the unique identifier of the examination centre.
 * It will uniquely represent an exam centre of PU.
 * 
 * @author Sumit Shresth
 */
public record CentreIdentifier(String name, int maxLimit) implements java.io.Serializable {

    public CentreIdentifier {
        name = name.trim().toUpperCase();
    }

    public final String getCentreName(){
        return this.name;
    }

    public final int getMaximumCentreLimit(){
        return this.maxLimit;
    }
}