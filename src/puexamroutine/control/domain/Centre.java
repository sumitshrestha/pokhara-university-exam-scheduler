
//                      !! RAM !!

package puexamroutine.control.domain;

/**
 * This Class represents the centre where exam is to be conducted.
 * In PU, there are many centres with each having maximum limit of seats.
 * Each Centre has unique name that will be given from database.
 * 
 * @author Sumit Shresth
 */
public class Centre implements java.io.Serializable{
    
    public Centre( puexamroutine.control.domain.CentreIdentifier CentreID ){
        this.CentreID = CentreID;
        this.Accomodated = 0;
    }
    
    public final String getCentreName(){
        return this.CentreID.getCentreName();
    }
    
    public int getLimit(){
        return this.CentreID.getMaximumCentreLimit();
    }
    
    public int getAccomodated(){
        return this.Accomodated;
    }
    
    public int getAvailable(){
        return this.CentreID.getMaximumCentreLimit() - this.Accomodated;
    }
    
    public boolean assign( int CandidateSum ){
        if( this.canAssign(CandidateSum) ){
            this.Accomodated += CandidateSum;
            return true;
        }
        else
            return false;
    }
    
    public boolean testAssign( int CandidateSum ){
        if( this.canTestAssign(CandidateSum) ){
            this.testAccomodated += CandidateSum;
            return true;
        }
        else
            return false;
    }
    
    public void startTest(){
        this.testAccomodated = this.Accomodated;
    }
    
    public boolean canAssign( int CandidateSum ){
        return this.getAvailable() >= CandidateSum;// && CandidateSum > 0;
    }
    
    public boolean canTestAssign( int CandidateSum ){
        return ( this.CentreID.getMaximumCentreLimit() - this.testAccomodated ) >= CandidateSum && CandidateSum > 0;
    }
    
    public boolean equals( puexamroutine.control.domain.Centre c ){
        return this.CentreID.getCentreName().equals( c.getCentreName() );
    }
    
    public puexamroutine.control.domain.CentreIdentifier getCentreID(){
        return this.CentreID;
    }
    
    private final puexamroutine.control.domain.CentreIdentifier CentreID;
    private int Accomodated, testAccomodated;
}