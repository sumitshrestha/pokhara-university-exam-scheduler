
//                              !! RAM !!

package puexamroutine.ui;

import puexamroutine.ui.components.MultiLineComponentFactory;
import puexamroutine.ui.components.MultiLineComponent;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

/**
 *
 * @author Sumit Shresth
 */
public class MultiLineRenderer implements TableCellRenderer{
    
    private boolean DEBUG = false;
    
  public MultiLineRenderer( MultiLineComponentFactory fact ){
      this.Factory = fact;
  }
    
    public Component getTableCellRendererComponent(
                            JTable table, Object value,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
        try{        
        if( row > this.list.size() - 1 ){
            this.list.add( this.Factory.createMultiLineComponent() );
        }
        MultiLineComponent thisPanel = this.list.get(row);
        if( !thisPanel.exists(value) ){
            if(this.DEBUG)System.out.println("removing for "+row);
            thisPanel.removeAll();//remove all previous values
            thisPanel.add(value);//fill new values
        }
            
        // This line was very important to get it working with JDK1.4
        TableColumnModel columnModel = table.getColumnModel();
        thisPanel.getComponent().setSize(columnModel.getColumn(column).getWidth(), 100000);
        int height_wanted = (int) thisPanel.getComponent().getPreferredSize().getHeight();
        addSize(table, row, column, height_wanted);
        height_wanted = findTotalMaximumRowSize(table, row);
        if (height_wanted != table.getRowHeight(row)) {
        table.setRowHeight(row, height_wanted);
        }    
        if( this.DEBUG )System.out.println("this is size "+height_wanted);
        return thisPanel.getComponent();
        }
        catch( Exception e ){
            return null;
        }
    }
    
    private void addSize(JTable table, int row, int column,
                       int height) {
    Map rows = (Map) cellSizes.get(table);
    if (rows == null) {
      cellSizes.put(table, rows = new HashMap());
    }
    Map rowheights = (Map) rows.get(new Integer(row));
    if (rowheights == null) {
      rows.put(new Integer(row), rowheights = new HashMap());
    }
    rowheights.put(new Integer(column), new Integer(height));
  }

  /**
   * Look through all columns and get the renderer.  If it is
   * also a TextAreaRenderer, we look at the maximum height in
   * its hash table for this row.
   */
  private int findTotalMaximumRowSize(JTable table, int row) {
    int maximum_height = 0;
    Enumeration columns = table.getColumnModel().getColumns();
    while (columns.hasMoreElements()) {
      TableColumn tc = (TableColumn) columns.nextElement();
      TableCellRenderer cellRenderer = tc.getCellRenderer();
      if (cellRenderer instanceof MultiLineRenderer) {
        MultiLineRenderer tar = (MultiLineRenderer) cellRenderer;
        maximum_height = Math.max(maximum_height,
            tar.findMaximumRowSize(table, row));
      }
    }
    return maximum_height;
  }

  private int findMaximumRowSize(JTable table, int row) {
    Map rows = (Map) cellSizes.get(table);
    if (rows == null) return 0;
    Map rowheights = (Map) rows.get(new Integer(row));
    if (rowheights == null) return 0;
    int maximum_height = 0;
    for (Iterator it = rowheights.entrySet().iterator();
         it.hasNext();) {
      Map.Entry entry = (Map.Entry) it.next();
      int cellHeight = ((Integer) entry.getValue()).intValue();
      maximum_height = Math.max(maximum_height, cellHeight);
    }
    return maximum_height;
  }
  
  public void registerFactory( MultiLineComponentFactory fact ){
      this.Factory = fact;
  }

    private ArrayList<MultiLineComponent> list = new ArrayList<MultiLineComponent>();
    private MultiLineComponentFactory Factory = null;
    /** map from table to map of rows to map of column heights */
    private final Map cellSizes = new HashMap();
}