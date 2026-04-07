
//                          !! RAM !!

package puexamroutine.control.domain;

/**
 * This class represents the means for grouping the courses into subgroups. 
 * It is based on Pokhara University rules only so this is domain based.
 * Since, Pokhara university uses faculy, level division they exists.
 * Also, There are one more classification that are not defined formally but does exists.
 * I call it discipline. The best current example is enginnering and bio-medical disciplines in Science and Technology faculty of Bachelor level.
 * So, The current distinguish has made this three classification necessary.
 * 
 * @author Sumit Shresth
 */
public class Group implements java.io.Serializable{
    
    public Group( final String f, final String l, final String d ){
        this.Faculty = f.trim().toUpperCase();
        this.Level = l.trim().toUpperCase();
        this.Discipline = d.trim().toUpperCase();
    }
    
    public final String getFaculty(){
        return this.Faculty;
    }
    
    public final String getLevel(){
        return this.Level;
    }
    
    public final String getDiscipline(){
        return this.Discipline;
    }
    
    public final boolean equals( puexamroutine.control.domain.Group g ){
        return g == this || this.checkStrictly(g);
    }
    
    private final boolean checkStrictly( puexamroutine.control.domain.Group g ){
        return this.Faculty.equals( g.getFaculty() ) && this.Level.equals( g.getLevel() ) && this.Discipline.equals( g.getDiscipline() );
    }
    
    public final String toString(){
        return this.Faculty+"/"+this.Level+"/"+this.Discipline;
    }
    
    private final String Faculty, Level, Discipline;
    
    public static final String NonExistentDiscipline="Not Available";
    public static final String NonExistentLevel="Not Available";
}