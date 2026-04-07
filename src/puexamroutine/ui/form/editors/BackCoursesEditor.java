
//                                  !! RAM !!

package puexamroutine.ui.form.editors;

import puexamroutine.ui.form.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.JTable;
import java.awt.Component;
import javax.swing.JOptionPane;
import puexamroutine.control.domain.CourseCode;
import puexamroutine.control.domain.Candidate;
import puexamroutine.control.domain.interfaces.CandidateInterface;

/**
 *
 * @author Sumit Shresth
 */
public class BackCoursesEditor extends AbstractCellEditor implements TableCellEditor{
    
    public BackCoursesEditor(){
        this.button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onTableFocus();
            }
        });
    }
    
    public void addBackCandidates( java.util.List<CandidateInterface> cands ){
        this.forms.clear();        
        this.currentRow = 0;
        
        java.util.Iterator<CandidateInterface> candItr = cands.iterator();
        while( candItr.hasNext() ){
            BackCandidateForm form = new BackCandidateForm( null, (Candidate)candItr.next(), this.selectCourses );
            this.forms.add(form);
        }
        this.rowCounter = cands.size() - 1 ;
    }
    
    public void setSelectCourses( java.util.Map<CourseCode,String> selectCourses ){
        this.selectCourses = selectCourses;
    }

    public Object getCellEditorValue() {
        Object[] courses = null;
        do{
            courses = this.forms.get(this.currentRow).getCourses();        
            if( courses == null ){
                JOptionPane.showMessageDialog( null, "The format of coures code appears to be incorrect\nThe right syntax is Prefix-Code.Credit\nPlease Try Again", "Course Code Error", JOptionPane.ERROR_MESSAGE );
                this.displayCurrentForm();            
            }            
        }while( courses == null );
        return toString(courses);
    }
    
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        if( this.rowCounter < row ){
            this.onRowAddition( table.getValueAt( row, 0 ).toString() );
        }
        this.currentRow = row;
        this.parenttable = table;
        return this.button;
    }

    public Object toString(Object[] courses) {

        String val = "";
        for (int i = 0; i < courses.length; i++) {
            val += courses[i];
            if (i < courses.length - 1) {
                val += ",";
            }
        }
        return val;
    }

    private void displayCurrentForm() {
        BackCandidateForm form = this.forms.get(this.currentRow);
        form.setCandidateID(this.parenttable.getValueAt(this.currentRow, 0).toString());
        form.setVisible(true);
    }
    
    private void onTableFocus(){
        displayCurrentForm();
        fireEditingStopped();
        this.parenttable.requestFocusInWindow();
    }    
    
    public java.util.List<BackCandidateForm> getForms(){
        return this.forms;
    }
    
    public void onRowDeleted( int row ){
        if( row > this.forms.size() - 1 ) return;
        this.forms.remove(row);
        this.rowCounter--;
    }
    
    public void onRowAddition( String cand ){
        BackCandidateForm form  = new BackCandidateForm( null, cand, this.selectCourses );
        this.forms.add(form);
        this.rowCounter ++;
    }
        
    private java.util.ArrayList<BackCandidateForm> forms = new java.util.ArrayList<BackCandidateForm>();
    private javax.swing.JButton button = new javax.swing.JButton("value being edited...");
    private int rowCounter=-1, currentRow;
    private javax.swing.JTable parenttable;
    private java.util.Map<CourseCode,String> selectCourses = null;
}