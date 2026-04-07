
//                          !! RAM !!

package puexamroutine.ui.components;

import puexamroutine.ui.components.*;
import javax.swing.JLabel;

/**
 *
 * @author Sumit Shresth
 */
public class SingleLine extends JLabel implements MultiLineComponent{

    public void add( Object values ){
        String val = (String) values;
        this.setText(val);
        this.updateUI();
    }
    
    public void remove( Object values ){
        // do nothing
    }
    
    public java.awt.Component getComponent(){
        return this;
    }
    
    public boolean exists( Object values ){
        return super.getText().equals(values);
    }

}