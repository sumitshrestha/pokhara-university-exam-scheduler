
//                      !! RAM !!

package puexamroutine.control.domain.list;

/**
 * This class represents the centre table for a day.
 * Each day has one such table that represents the state of the centres for that day.
 * 
 * @author Sumit Shresth
 */
public class CentreTableForDay implements java.io.Serializable{
    
    public CentreTableForDay(  java.util.Collection<puexamroutine.control.domain.CentreIdentifier> Cen ){        
        java.util.Iterator<puexamroutine.control.domain.CentreIdentifier> CenIDItr = Cen.iterator();
        while( CenIDItr.hasNext() ){
            puexamroutine.control.domain.CentreIdentifier CentID = CenIDItr.next();
            this.Centres.put(CentID, new puexamroutine.control.domain.Centre(CentID) );
        }
    }
    
    public void setExamDay( puexamroutine.control.domain.Day ExamDay ){
        this.ExamDay = ExamDay;
    }

    public final puexamroutine.control.domain.Day getExamDay(){
        return this.ExamDay;
    }
    
    public java.util.Collection<puexamroutine.control.domain.Centre> getCentresForThisDay(){
        return this.Centres.values();
    }
    
    public java.util.Collection<puexamroutine.control.domain.CentreIdentifier> getExaminationCentres(){
        return this.Centres.keySet();
    }
    
    public puexamroutine.control.domain.Centre getCentre(puexamroutine.control.domain.CentreIdentifier CentID ){
        return this.Centres.get(CentID);        
    }
    
    private puexamroutine.control.domain.Day ExamDay;
    private java.util.HashMap<puexamroutine.control.domain.CentreIdentifier, puexamroutine.control.domain.Centre> Centres = new java.util.HashMap<puexamroutine.control.domain.CentreIdentifier, puexamroutine.control.domain.Centre>();
}