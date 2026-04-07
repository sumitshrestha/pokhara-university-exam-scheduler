
//                              !! RAM !!

package puexamroutine.ui.components;

/**
 *
 * @author Sumit Shresth
 */
public interface MultiLineComponent {

     /**
     * This method is use to add elements into the container.
     * The values have user defined interpretation.
     * So, its users responsiblity to define it appropiately.
     * 
     * @param values values to be added into cell
     */
    public void add( Object values );
    
    public void remove( Object values );
    
    /**
     * This method gives the component representing it.
     * The renderer uses it to determine the size to displayed.
     * 
     * @return component equivalent of this component.
     */     
    public java.awt.Component getComponent();

    void removeAll();
    
    boolean exists( Object values );
    
}