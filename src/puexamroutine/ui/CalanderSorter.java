
//                      !! RAM !!

package puexamroutine.ui;

import javax.swing.table.*;
import java.util.Comparator;

/**
 *
 * @author Sumit Shresth
 */
public class CalanderSorter extends TableRowSorter{
    private boolean DEBUG = false;
    public CalanderSorter( TableModel model ){
        super( model );
    }
    
    public Comparator<?> getComparator(int column){
        if(this.DEBUG)System.out.println("\n\n\n\n\nThis is column value "+column );
        if( column == 2 )
            return this.ExamsComparator;
        else
            if( column == 0 )
                return this.ExamDayComparator;
            else
                return new Comparator<String>(){
                    public int compare(String s1, String s2) {
                        return 0;
                    }        
                };                
    }

    private Comparator<String> ExamsComparator = new Comparator<String>(){
        public int compare( String s1, String s2) {
            String[] ex1 = s1.split( "/" );
            String[] ex2 = s2.split( "/" );
            if( ex1.length > ex2.length )
                return 1;
            else
                if( ex1.length == ex2.length )
                    return 0;
                else
                    return -1;
        }
    };
    
    private Comparator<String> ExamDayComparator = new Comparator<String>(){
        public int compare(String s1, String s2) {
            int day1 = Integer.parseInt(s1);
            int day2 = Integer.parseInt(s2);
            if( day1 > day2 )
                return 1;
            else
                if( day1 == day2 ) 
                    return 0;
                else
                    return -1;
        }
    };
}