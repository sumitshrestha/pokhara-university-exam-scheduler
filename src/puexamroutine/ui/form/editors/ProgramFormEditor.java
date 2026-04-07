
//                          !! RAM !!

package puexamroutine.ui.form.editors;

import puexamroutine.ui.form.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.JTable;
import java.awt.Component;
import puexamroutine.control.domain.*;
import puexamroutine.control.domain.list.ProgramList;

/**
 *
 * @author Sumit Shresth
 */
public class ProgramFormEditor extends AbstractCellEditor implements TableCellEditor{
    
    private boolean DEBUG = true;
    
    public ProgramFormEditor( final String fac, final String level, final String dis, String[] centres ){                                     
        this.setFaculty(fac);
        this.setLevel(level);
        this.setDiscipline(Discp);
        this.centres = centres;
        this.button = new javax.swing.JButton("value being edited...");
        this.button.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onTableFocus();
            }
        });
    }
    
    public void addGroupPrograms( ProgramList list ){
        this.FormList.clear();
        this.rowCounter = list.getPrograms().size() - 1;
        java.util.ListIterator<Program> prgList = list.getPrograms().listIterator();
        while( prgList.hasNext() ){
            CollegesForm form  = new CollegesForm( null, prgList.next(), this.centres,this.selectCourses );
            this.FormList.add(form);
        }
    }
    
    public void setSelectCourses( java.util.Map<CourseCode,String> c ){
        this.selectCourses = c;
    }
    
    public Object getCellEditorValue() {
        return this.FormList.get(this.currentRow).toString();
    }
    
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        if(this.DEBUG)System.out.println("this is row "+row+" when total rows is "+table.getModel().getRowCount() );
        if( this.rowCounter < row ){
            CollegesForm form  = new CollegesForm( null, table.getValueAt(row, 0).toString(), this.Faculty,this.Level,this.Discp, this.centres,this.selectCourses );
            this.FormList.add(form);
            this.rowCounter ++;
        }
        this.currentRow = row;
        this.parenttable = table;
        return this.button;
    }

    private void displayCurrentForm() {        
        CollegesForm form = this.FormList.get(this.currentRow);
        form.setProgram( this.parenttable.getValueAt( this.parenttable.getSelectedRow(), 0).toString() );
        form.setFaculty(Faculty);
        form.setLevel(Level);
        form.setDiscipline(Discp);
        this.FormList.get(this.currentRow).setVisible(true);
    }
    
    private void onTableFocus(){
        displayCurrentForm();
        fireEditingStopped();
        this.parenttable.requestFocusInWindow();
    }    
    
    public java.util.List<CollegesForm> getForms(){
        return this.FormList;
    }
    
    public void onRowDeleted( int row ){
        if( row > this.FormList.size() - 1 ) return;
        this.FormList.remove(row);
        this.rowCounter--;
    }
    
    public void setFaculty( final String fac ){
        this.Faculty = fac;
    }
    
    public void setLevel( final String level ){
        this.Level = level;
    }
    
    public void setDiscipline( final String Disp ){
        this.Discp = Disp;
    }
    
    private String Faculty,Level,Discp;
    private javax.swing.JButton button;    
    private int rowCounter = -1;
    private int currentRow;
    private java.util.ArrayList<CollegesForm> FormList = new java.util.ArrayList<CollegesForm>();
    private JTable parenttable = null;
    private String[] centres = null;
    private java.util.Map<CourseCode,String> selectCourses = null;
    
}