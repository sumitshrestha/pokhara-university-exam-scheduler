
//                                  !! RAM !!

package puexamroutine.ui.form.editors;

import puexamroutine.ui.form.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.JTable;
import java.awt.Component;
import puexamroutine.control.domain.*;

/**
 *
 * @author Sumit Shresth
 */
public class CollegeFormEditor extends AbstractCellEditor implements TableCellEditor{
    
    public CollegeFormEditor( final String prg, String[] centres ){
        this.Prg = prg;
        this.Centres = centres;
        this.button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onTableFocus();
            }
        });
    }
    
    public void addColleges( Program prg, java.util.List<College> coll ){
        this.forms.clear();
        
        java.util.ListIterator<College> collItr = coll.listIterator();
        while( collItr.hasNext() ){
            CollegeForm form  = new CollegeForm( null, prg, collItr.next(), this.Centres,this.SelectCourses );
            this.forms.add(form);
        }
        this.rowCounter = coll.size() - 1;
    }

    public Object getCellEditorValue() {
        String college = null;
        /*do{
            college = this.forms.get(this.currentRow).getCollegeName();        
            if( college.equals("") ){
                JOptionPane.showMessageDialog( null, "The College name is emty\nPlease Try Again", "Error", JOptionPane.ERROR_MESSAGE );
                this.displayCurrentForm();            
            }            
        }while( college.equals("") );
        */
        college = this.forms.get(this.currentRow).getCollegeName();        
        return college;
    }
    
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        if( this.rowCounter < row ){
            CollegeForm form  = new CollegeForm( null, this.Prg,this.Centres,this.SelectCourses );
            if( ! this.forms.isEmpty() ){
                Program prg = new Program( this.Prg );
                /*College[] colleges = new College[ this.forms.size() ];
                Iterator<CollegeForm> formItr = this.forms.iterator();
                for( int i=0; i<colleges.length; i++ ){
                    colleges[i] = formItr.next().getco
                }*/
            }
            this.forms.add(form);
            this.rowCounter ++;
        }
        this.currentRow = row;        
        this.parentTable = table;
        return this.button;
    }
    
    public void setSelectCourses( java.util.Map<CourseCode,String> SelectCourses ){
        this.SelectCourses = SelectCourses;
    }

    private void displayCurrentForm() {
        CollegeForm form = this.forms.get(this.currentRow);        
        form.setProgram( this.Prg );
        form.setVisible(true);
    }
    
    private void onTableFocus(){
        displayCurrentForm();
        fireEditingStopped();
        this.parentTable.requestFocusInWindow();
    }    
    
    public java.util.List<CollegeForm> getForms(){
        return this.forms;
    }
    
    public void onRowDeleted( int row ){
        if( row > this.forms.size() - 1 )return;
        this.forms.remove(row);
        this.rowCounter--;
    }
    
    public void setProgram( final String prg ){
        this.Prg = prg;
    }
        
    private java.util.ArrayList<CollegeForm> forms = new java.util.ArrayList<CollegeForm>();
    private javax.swing.JButton button = new javax.swing.JButton("value being edited...");
    private int rowCounter=-1, currentRow;
    private String Prg;
    private String[] Centres = null;
    private JTable parentTable = null;
    private java.util.Map<CourseCode,String> SelectCourses = null;
}