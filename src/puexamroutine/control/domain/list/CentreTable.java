
//                          !! RAM !!

package puexamroutine.control.domain.list;

/**
 * This class represents the list of all centre where examination has to be conducted.
 * 
 * @author Sumit Shresth
 */
public class CentreTable implements java.io.Serializable{
    
    public void addCentres( java.util.Collection<puexamroutine.control.domain.CentreIdentifier> c ){
        java.util.Iterator<puexamroutine.control.domain.CentreIdentifier> CentreItr = c.iterator();
        while( CentreItr.hasNext() ){
            this.addCentre( CentreItr.next() );
        }
    }
    
    public void addCentre( puexamroutine.control.domain.CentreIdentifier c ){
        this.Centres.put( c.getCentreName() , c);
    }

    /**
     * This method returns the original list of the centres as in the table.
     * Since the centre object has changeable fields that can be modified by user leading to corruption, Only read only methods are allowed to use this mehod.
     * Modifier must use the getClonedCentres() mehod instead.
     * 
     * @return original copy of the centres
     */
    public java.util.Collection<puexamroutine.control.domain.CentreIdentifier> getCentres(){
        return this.Centres.values();
    }
    
    public puexamroutine.control.domain.CentreIdentifier getCentre( final String cent ){
        return this.Centres.get(cent);
    }
    
    public int getTotalCentres(){
        return this.Centres.size();
    }
    
    public void removeAll(){
        this.Centres.clear();
    }
    
    private java.util.HashMap< String, puexamroutine.control.domain.CentreIdentifier> Centres = new java.util.HashMap< String,puexamroutine.control.domain.CentreIdentifier>();
}