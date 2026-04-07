
//                              !! RAM !!

package puexamroutine.control.domain.list;

/**
 * This bean class represents the data of university that has been read from database.
 * This must have all the necessry data for generating routine.
 * This bean class thus encapsulates the necessary data.
 * Since, This represents the data, This is serializable to allow data to be passed from one computer to other.
 *
 * @author Sumit Shresth
 */
public class UniversityDataBean implements java.io.Serializable{
    
    public UniversityDataBean( GroupList l, CentreTable t, CollegeList c ){
        this.GroupList = l;
        this.ExamCentreList = t;
        this.CollegeList = c;
    }
    
    public final puexamroutine.control.domain.list.GroupList getGroupList(){
        return this.GroupList;
    }
    
    public final puexamroutine.control.domain.list.CentreTable getCentreList(){
        return this.ExamCentreList;
    }
    
    public final puexamroutine.control.domain.list.CollegeList getCollegeList(){
        return this.CollegeList;
    }

    private final GroupList GroupList;
    private final CentreTable ExamCentreList;
    private final CollegeList CollegeList;
}