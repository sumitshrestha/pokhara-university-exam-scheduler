
//                              !! RAM !!

package puexamroutine.ui.components;

/**
 * This method will generate the multiline component to be added in table.
 *
 * @author Sumit Shresth
 */
public interface MultiLineComponentFactory {
    
    public MultiLineComponent createMultiLineComponent();
    
    public MultiLineComponent createMultiLineComponent( Object values );

}