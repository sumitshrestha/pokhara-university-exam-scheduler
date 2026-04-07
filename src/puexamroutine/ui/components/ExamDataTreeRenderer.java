
//                              !! RAM !!

package puexamroutine.ui.components;

import javax.swing.tree.*;
import java.awt.*;
import javax.swing.*;
import puexamroutine.ui.ExamDataTree;

/**
 *
 * @author Sumit Shresth
 */
public class ExamDataTreeRenderer extends DefaultTreeCellRenderer{
    
    public ExamDataTreeRenderer(){
        this.FacultyIcon = this.createImageIcon( "/puexamroutine/ui/img/DataTree/Faculty.PNG" );
        this.LevelIcon = this.createImageIcon( "/puexamroutine/ui/img/DataTree/level.png");
        this.ProgramIcon = this.createImageIcon( "/puexamroutine/ui/img/DataTree/program.png");
        this.DisciplineIcon = this.createImageIcon( "/puexamroutine/ui/img/DataTree/Department.PNG");
        this.CollegeIcon = this.createImageIcon( "/puexamroutine/ui/img/DataTree/college.png");        
    }

    public Component getTreeCellRendererComponent(
                            JTree tree,
                            Object value,
                            boolean sel,
                            boolean expanded,
                            boolean leaf,
                            int row,
                            boolean hasFocus) {

        super.getTreeCellRendererComponent(
                            tree, value, sel,
                            expanded, leaf, row,
                            hasFocus);        
        
        ExamDataTree thisTree = (ExamDataTree) tree;        
        
        switch ( thisTree.getLevelOfSelectedNode( (DefaultMutableTreeNode)value ) ){
            case ExamDataTree.FACULTY_NODE:
                this.setToolTipText("Faculty");
                this.setIcon( this.FacultyIcon );
                break;
            case ExamDataTree.LEVEL_NODE:
                this.setToolTipText("Level of this Faculty");
                this.setIcon( this.LevelIcon );
                break;
            case ExamDataTree.DISCIPLINE_NODE:
                this.setToolTipText( "Discipline of faculty" );
                this.setIcon( this.DisciplineIcon );
                break;
            case ExamDataTree.PROGRAM_NODE:
                this.setToolTipText( "Program " );
                this.setIcon( this.ProgramIcon );
                break;
            case ExamDataTree.COLLEGE_NODE:
                this.setToolTipText( "College under University" );
                this.setIcon( this.CollegeIcon );
                break;
            default:
                this.setIcon( null );
        }
        return this;
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = ExamDataTreeRenderer.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    private Icon FacultyIcon, CollegeIcon, ProgramIcon, LevelIcon, DisciplineIcon;
}