
//                                  !! RAM !!

package puexamroutine.ui.components;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;

/**
 *
 * @author Sumit Shresth
 */
public class SearchComponent implements TreeSearcher.TreeSearchInterface{
    
    public SearchComponent( JTree Tree ){
        this.Tree = Tree;
    }
    
        public void find(){                
            do{
                this.FindForm.setVisible( true );
                int state = this.FindForm.getReturnStatus();
                
                if( state == this.FindForm.RET_CANCEL )
                    return;
                
                // else
                findword = this.FindForm.getFindWord();
                
                this.TreeSearcher.setFindCond( findword, this.FindForm.isIgnoreCase() );
                
                if( findword == null ||  findword.equals("") )
                    continue;
                // else
                this.TreeSearcher.recursive_find( (TreeNode)Tree.getModel().getRoot() );
            }
            while( this.FindForm.getReturnStatus() != this.FindForm.RET_CANCEL );
        }
            
            
        public void displayNode(final javax.swing.tree.TreeNode Node) {
            javax.swing.tree.TreePath path = new javax.swing.tree.TreePath( ( (javax.swing.tree.DefaultMutableTreeNode) Node ).getPath());
            Tree.scrollPathToVisible( path );                    
            Tree.setSelectionPath( path );
        }
        
        public boolean continueSearch(){
            int choice = javax.swing.JOptionPane.showConfirmDialog( null, "Do you want to continue finding?","Continue?" ,javax.swing.JOptionPane.YES_NO_OPTION );
            if( choice != javax.swing.JOptionPane.OK_OPTION )
                return false;
            else
                return true;
        }
        
    private String findword = null;
    private FindCmp FindForm = new FindCmp( null ) ;
    private TreeSearcher TreeSearcher = new TreeSearcher( this );
    private JTree Tree = null;
}