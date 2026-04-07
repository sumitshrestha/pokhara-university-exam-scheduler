
//                                      !! RAM !!

package puexamroutine.ui.form.editors;

import javax.swing.JTable;
import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import java.awt.Component;
import puexamroutine.ui.form.BackCanidateSemesterForm;

/**
 *
 * @author sumit shresth
 */
public class BackCandidateSemesterEditor extends AbstractCellEditor implements TableCellEditor{
    
    public BackCandidateSemesterEditor(){
        this.button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                onTableFocus();
            }
        });
    }
    
    public Object getCellEditorValue() {
        //if( this.form.getState() == this.form.ok )
            return this.form.getSemester();
        /*else
            return null;*/
    }
    
    public Component getTableCellEditorComponent( JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        if( this.rowCounter < row ){
            this.form.initialize( table.getValueAt( row, 0 ).toString() ,this.DEFAULT_SEM );
            this.rowCounter++;
        }
        else{
            String val = value.toString().equals("")?String.valueOf( this.DEFAULT_SEM ):value.toString();
            this.form.initialize( table.getValueAt( row, 0 ).toString(), Integer.parseInt(val) );
        }
        this.currentRow = row;   
//        this.previousValue = value;
        this.parentTable = table;
        return this.button;
    }
        
    private void onTableFocus(){
        displayCurrentForm();
        fireEditingStopped();
        this.parentTable.requestFocusInWindow();
    }
    
    private void displayCurrentForm() {        
        form.setVisible(true);
    }
    
    public void onRowDeleted( int row ){
        this.rowCounter--;
    }

    //private java.util.ArrayList<Integer> forms = new java.util.ArrayList<Integer>();
    private javax.swing.JButton button = new javax.swing.JButton("click to edit...");
    private int rowCounter=-1, currentRow;    
    private JTable parentTable = null;
    private BackCanidateSemesterForm form = new BackCanidateSemesterForm( null );
    private final int DEFAULT_SEM = 0;
}