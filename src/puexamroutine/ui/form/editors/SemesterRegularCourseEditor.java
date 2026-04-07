
//                          !! RAM !!

package puexamroutine.ui.form.editors;

import puexamroutine.ui.form.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.JTable;
import java.awt.Component;
import javax.swing.JOptionPane;
import java.util.HashMap;
import puexamroutine.control.domain.CourseCode;
import puexamroutine.control.domain.Semester;
import puexamroutine.control.domain.RegularCourses;

/**
 *
 * @author Sumit Shresth
 */
public class SemesterRegularCourseEditor extends AbstractCellEditor implements TableCellEditor{
    
    private boolean DEBUG = false;
    
    public SemesterRegularCourseEditor( final String prg, final String coll ){        
        this.College = coll;
        this.Program = prg;                
        this.button = new javax.swing.JButton("value being edited...");
        this.button.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onTableFocus();
            }
        });
    }
    
    public void addRegularSemesters( java.util.List<RegularCourses> sems ){
        // initialization
        this.FormList.clear();
        this.currentRow = 0;
        this.rowCounter = 0;
        
        java.util.Iterator<RegularCourses> semItr = sems.iterator();
        while( semItr.hasNext() ){
            Semester sem = semItr.next().getSemester();
            RegularCourseForm form = new RegularCourseForm(null, sem, this.grpCourses);
            this.FormList.add(form);            
        }
        this.rowCounter = sems.size() - 1;
    }

    public void onRowAddition( String sem ) {
        RegularCourseForm form = new RegularCourseForm(null, this.Program, sem, this.College, this.grpCourses);
        this.FormList.add(form);
        this.rowCounter++;
    }
    
    public void setSelectCourses( final java.util.Map<CourseCode,String> grpCourses ){
        this.grpCourses = grpCourses;
    }    
    
    public Object getCellEditorValue() {
        Object[] courses = null;
        HashMap pair = null;        
        RegularCourseForm currentForm = this.FormList.get(this.currentRow);
        while( /*currentForm.getReturnStatus() == currentForm.RET_OK &&*/ pair == null ){
            pair = currentForm.getCourses();
            if( pair == null ){
                JOptionPane.showMessageDialog( null, "The format of coures code appears to be incorrect\nThe right syntax is Prefix-Code.Credit\nPlease Try Again", "Course Code Error", JOptionPane.ERROR_MESSAGE );
                //canContinue = this.displayCurrentForm();
                this.displayCurrentForm();
            }
            else{
                courses = pair.keySet().toArray();
            }
        }
        return toString(courses);
    }
    
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        if(this.DEBUG)System.out.println("this is row "+row+" when total rows is "+table.getModel().getRowCount() );
        this.currentSem = table.getValueAt(row, 0).toString();
        if( this.rowCounter < row ){
            onRowAddition( this.currentSem );
        }
        this.currentRow = row;
        this.parentTable = table;
        return this.button;
    }

    public Object toString(Object[] courses) {

        String val = "";
        /*if( currentForm.getReturnStatus() == currentForm.RET_CANCEL )
        return "";*/
        for (int i = 0; i < courses.length; i++) {
            val += courses[i];
            if (i < courses.length - 1) {
                val += ",";
            }
        }
        return val;
    }

    private boolean displayCurrentForm() {
        RegularCourseForm form = this.FormList.get(this.currentRow);
        form.setCollege(College);
        form.setProgram(Program);                        
        form.setSemester( this.currentSem );
        form.setVisible(true);
        return form.getReturnStatus() == form.RET_OK;
    }
    
    private void onTableFocus(){
        //this.currentFormCopy = (RegularCourseForm) DeepCopy.copy( this.FormList.get(this.currentRow) );
        displayCurrentForm();
        fireEditingStopped();
        this.parentTable.requestFocusInWindow();
    }    
    
    public java.util.List<RegularCourseForm> getForms(){
        return this.FormList;
    }
    
    public void onRowDeleted( int row ){
        this.FormList.remove(row);
        this.rowCounter--;
    }
    
    public void setCollege( final String College ){
        this.College = College;        
    }
    
    public void setProgram( final String Program ){
        this.Program = Program;               
    }
    
    private String College, Program;    
    private javax.swing.JButton button;    
    private int rowCounter = -1;
    private int currentRow;
    private java.util.ArrayList<RegularCourseForm> FormList = new java.util.ArrayList<RegularCourseForm>();
    private JTable parentTable = null;    
    private java.util.Map<CourseCode,String> grpCourses = null;
    private String currentSem;
}