
//                                  !! RAM !!

package puexamroutine.ui.form.editors;

import puexamroutine.ui.form.CourseCodeInputForm;
import puexamroutine.control.domain.CourseCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.JTable;
import java.awt.Component;

/**
 *
 * @author Sumit Shresth
 */
public class CourseCodeEditor extends AbstractCellEditor implements TableCellEditor {

    public CourseCodeEditor(){        
        this.button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onTableFocus();                
            }
        });
    }
    
    public void addSelectCourses( java.util.Map<CourseCode,String> courses ) throws Exception{        
            this.tempForm.initialize( null , courses );        
    }
    
    public void addCourses( java.util.Collection<CourseCode> courses ){
        this.forms.clear();
        this.forms.addAll( courses );
        this.rowCounter = courses.size() - 1;
    }
    
    public void addCourse( CourseCode c ){
        this.forms.add(c);
        this.rowCounter ++;
    }

    public Object getCellEditorValue() {                    
            if( tempForm.getReturnStatus() == tempForm.CANCEL ){
                return this.previousValue;
            }            
            else{
                //on okay click
                CourseCode c = this.tempForm.getCourseCode();
                if( this.currentRow > this.rowCounter ){
                    this.forms.add(c);
                    this.rowCounter++;
                }
                else{
                    this.forms.remove( this.currentRow );
                    this.forms.add(currentRow, c);
                }
                return c.toString();
            }
    }
    
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        if( this.rowCounter < row ){            
            this.tempForm.initialize( null );                        
        }
        else{
            this.tempForm.initialize( this.forms.get(row) );
        }
        this.currentRow = row;   
        this.previousValue = value;
        this.table = table;
        return this.button;
    }

    private void displayCurrentForm() {
        this.tempForm.setVisible( true );
    }
    
    private void onTableFocus(){
        displayCurrentForm();
        fireEditingStopped();
        this.table.requestFocusInWindow();            
    }    
    
    public java.util.List<CourseCode> getForms(){
        return this.forms;
    }
    
    public void onRowDeleted( int row ){
        if( row > this.forms.size() - 1 ) return;
        this.forms.remove(row);
        this.rowCounter--;
    }
    
    private java.util.ArrayList<CourseCode> forms = new java.util.ArrayList<CourseCode>();
    private javax.swing.JButton button = new javax.swing.JButton("CLICK TO EDIT");
    private int rowCounter=-1, currentRow;    
    private Object previousValue;
    private JTable table=null;
    private CourseCodeInputForm tempForm = new CourseCodeInputForm(null);
}