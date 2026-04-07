
//                              !! RAM !!

package puexamroutine.control.domain.list;

/**
 * This Class represents the list of colleges in PU whose examination has to be conducted.
 * 
 * @author Sumit Shresth
 */
public class CollegeList implements java.io.Serializable{
    
    public void addColleges( java.util.Collection<puexamroutine.control.domain.College> Colleges ){
        java.util.Iterator<puexamroutine.control.domain.College> CollItr = Colleges.iterator();
        while( CollItr.hasNext() ){
            puexamroutine.control.domain.College c = CollItr.next();
            this.Colleges.put( c.getCollegeName(), c);
        }
    }
    
    public void addCollege( puexamroutine.control.domain.College c ){
        this.Colleges.put( c.getCollegeName(), c);
    }
    
    public puexamroutine.control.domain.College getColleges( String CollName ){
        return this.Colleges.get( CollName );
    }
    
    public java.util.Collection<puexamroutine.control.domain.College> getColleges(){
        return this.Colleges.values();
    }
    
    public boolean exists( puexamroutine.control.domain.College c ){
        return this.Colleges.containsValue(c)  || this.checkStrictly(c);
    }
    
    private boolean checkStrictly( puexamroutine.control.domain.College c ){
        java.util.Iterator< puexamroutine.control.domain.College> it = this.Colleges.values().iterator();
        while( it.hasNext() ){
            if( it.next().getCollegeName().equals( c.getCollegeName() ) )
                return true;
        }
        return false;
    }
    
    public void removeAll(){
        this.Colleges.clear();
    }
    
    private java.util.HashMap<String,puexamroutine.control.domain.College> Colleges = new java.util.HashMap<String,puexamroutine.control.domain.College>();
}