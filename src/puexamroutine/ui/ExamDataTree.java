
//                                  !! RAM !!

package puexamroutine.ui;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.tree.*;
import puexamroutine.control.domain.*;
import puexamroutine.control.domain.interfaces.*;
import puexamroutine.control.domain.list.*;
import puexamroutine.ui.components.ExamDataTreeRenderer;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.*;
import puexamroutine.ui.form.*;
import puexamroutine.ui.components.SearchComponent;

/**
 * This is the Tree of the exam data that is inputted by the user.
 *
 * @author Sumit Shresth
 */
public class ExamDataTree extends javax.swing.JTree{
    
    private boolean DEBUG = false;

    public ExamDataTree(){             
        this.Root = new DefaultMutableTreeNode( this.ROOT_TEXT );
        FacultyNode = new DefaultMutableTreeNode( this.FACULTY_NODE_TEXT  );
        this.Root.add( FacultyNode );
        this.Model = new ExamDataTreeModel( this.Root );
        super.setModel( Model );
        super.setCellRenderer( new ExamDataTreeRenderer() );
        super.addMouseListener( new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked( java.awt.event.MouseEvent evt ){
                onMouseClick( evt );
            }
        } );
        this.initialisePopups();
    }

    public int getLevelOfSelectedNode() {
        return this.getSelectionPath().getPathCount();
    }
    
    public int getLevelOfSelectedNode( DefaultMutableTreeNode node ) {
        return this.Model.getPathToRoot(node).length;
    }
    
    public void setDataLoader( puexamroutine.control.Controller c ){
        this.controller = c;
        this.initialize( this.controller.getGroupList() );
    }
    
    public void initialisePopups(){        
        this.RegularCourseTotalCandidatePopup = new JPopupMenu();
        JMenuItem TotalCandidateEdit = new JMenuItem("Edit total candidate");
        TotalCandidateEdit.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                changeTotalCandidateForSelectedRegularCourseNode();
            }
        });
        this.RegularCourseTotalCandidatePopup.add(TotalCandidateEdit);     
        this.RegularCoursePopup = new JPopupMenu();
        JMenuItem DelRegularCourse = new JMenuItem("remove Course");
        DelRegularCourse.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                removeRegularCourse();
            }
        });
        this.RegularCoursePopup.add(DelRegularCourse);
        JMenuItem UpdateRegularCourse = new JMenuItem("update Course");
        UpdateRegularCourse.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                updateRegularCourse();
            }
        });
        this.RegularCoursePopup.add( UpdateRegularCourse );
        this.SemesterPopup = new JPopupMenu();
        JMenuItem RegularCourseAdd = new JMenuItem("Add courses");
        RegularCourseAdd.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                addRegularCourse();
            }
        });
        this.SemesterPopup.add(RegularCourseAdd);        
        JMenuItem SemesterRemove = new JMenuItem("remove semester");
        SemesterRemove.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                removeRegularSemester();
            }
        });
        this.SemesterPopup.add(SemesterRemove);
        JMenuItem updateRegularSem = new JMenuItem("update semester");
        updateRegularSem.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                updateRegularSemester();
            }
        });
        this.SemesterPopup.add( updateRegularSem );
        this.RegularSemesterPopup = new JPopupMenu();
        JMenuItem SemesterAdd = new JMenuItem("Add Semester");
        SemesterAdd.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                addRegularSemesters();
            }
        });
        this.RegularSemesterPopup.add(SemesterAdd);        
        this.BackCandidateCoursePopup = new JPopupMenu();
        JMenuItem BackCandidateCourseDelete = new JMenuItem( "delete back course" );
        BackCandidateCourseDelete.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                removeBackCourse();
            }
        });
        this.BackCandidateCoursePopup.add( BackCandidateCourseDelete );
        JMenuItem BackCandiddateCourseUpdate = new JMenuItem( "update back course" );        
        BackCandiddateCourseUpdate.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                updateCandidateBackCourse();
            }
        });
        this.BackCandidateCoursePopup.add( BackCandiddateCourseUpdate );
        this.BackCandidatePopup = new JPopupMenu();
        JMenuItem AddbackCandCourse = new JMenuItem("add back course");
        AddbackCandCourse.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                addBackCourse();
            }
        });
        this.BackCandidatePopup.add( AddbackCandCourse  );
        JMenuItem DeletebackCand = new JMenuItem( "delete candidate" );
        DeletebackCand.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                deleteBackCandidate();
            }
        });
        this.BackCandidatePopup.add( DeletebackCand );
        JMenuItem updateCandidate = new JMenuItem( "update candidate" );
        updateCandidate.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                updateBackCandidate();
            }
        });
        this.BackCandidatePopup.add( updateCandidate );
        JMenuItem updateCandidateSem = new JMenuItem( "update candidate Semester" );
        updateCandidateSem.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                updateBackCandidateSemester();
            }
        });
        this.BackCandidatePopup.add( updateCandidateSem );
        this.BackCandidatesListPopup = new JPopupMenu();
        JMenuItem BackCandidateAdd = new JMenuItem("Add BackCandidate");
        BackCandidateAdd.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addBackCandidates();
            }
        });
        this.BackCandidatesListPopup.add( BackCandidateAdd );
        this.CollegePopup = new JPopupMenu();
        JMenuItem delCollege = new JMenuItem("delete college");
        delCollege.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CollegeDeleteConfirmForm form = new CollegeDeleteConfirmForm( null );
                form.setVisible( true );
                if( form.getReturnStatus() == form.RET_DEL_PROGRAM_COLLEGE ){
                    deleteProgramCollege();
                }
                else if( form.getReturnStatus() == form.RET_DEL_ENTIRE_COLLEGE ){
                    deleteCollege();
                }
            }
        });
        this.CollegePopup.add(delCollege);
        this.ProgramPopup = new JPopupMenu();        
        JMenuItem AddCollege = new JMenuItem("Add Colleges");
        AddCollege.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addColleges();
            }
        });
        this.ProgramPopup.add( AddCollege );
        JMenuItem removePrg = new JMenuItem("remove program");
        removePrg.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteProgram();
            }
        });
        this.ProgramPopup.add(removePrg);
        JMenuItem updatePrg = new JMenuItem( "update program" );
        updatePrg.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateProgram();
            }
        });
        this.ProgramPopup.add(updatePrg);
        this.DisciplinePopup = new JPopupMenu();
        JMenuItem AddPrograms = new JMenuItem("Add Programs");
        AddPrograms.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addPrograms();
            }
        });
        this.DisciplinePopup.add( AddPrograms );
        JMenuItem editPrograms = new JMenuItem("edit Programs");
        editPrograms.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editGroupPrograms();
            }
        });
        this.DisciplinePopup.add( editPrograms  );
        JMenuItem deleteDiscipline = new JMenuItem( "delete discipline" );
        deleteDiscipline.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteDiscipline();
            }
        });
        this.DisciplinePopup.add( deleteDiscipline );
        JMenuItem updateDiscipline = new JMenuItem( "update discipline" );
        updateDiscipline.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateDiscipline();
            }
        });
        this.DisciplinePopup.add( updateDiscipline );
        this.AddFacultyPopup = new JPopupMenu();
        JMenuItem addFaculty = new JMenuItem("Add Faculty");
        addFaculty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addFaculty();
            }
        });
        this.AddFacultyPopup.add( addFaculty );
        JMenuItem delAllFaculty = new JMenuItem("remove All Faculties");
        delAllFaculty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteAllFaculties();
            }
        });
        this.AddFacultyPopup.add( delAllFaculty );
        this.FacultyPopup = new JPopupMenu();
        JMenuItem addLevel = new JMenuItem("Add Level");
        addLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addLevel();
            }
        });
        this.FacultyPopup.add(addLevel);
        JMenuItem updateFaculty = new JMenuItem("update Faculty");
        updateFaculty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFaculty();
            }
        });
        this.FacultyPopup.add(updateFaculty);
        JMenuItem deleteFaculty = new JMenuItem("delete Faculty");
        deleteFaculty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteFaculty();
            }
        });
        this.FacultyPopup.add(deleteFaculty);       
        this.LevelPopup = new JPopupMenu();
        JMenuItem addDiscipline = new JMenuItem("Add Discipline");
        addDiscipline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDiscipline();
            }
        });
        this.LevelPopup.add(addDiscipline);
        JMenuItem deleteLevel = new JMenuItem( "delete level" );
        deleteLevel.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteLevel();
            }
        });
        this.LevelPopup.add( deleteLevel );
        JMenuItem updateLevel = new JMenuItem( "update level" );
        updateLevel.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateLevel();
            }
        });
        this.LevelPopup.add( updateLevel  );
    }
        
    public void initialize( puexamroutine.control.database.DatabaseWriter Writer ){
        this.Writer = Writer;
    }
    
    public void initialize( GroupList grps ){
        this.removeFaculties();
        this.Model.addGroups(grps);
        this.updateUI();
    }
    
    /**
     * This method is used to reload the tree from database.
     * But, use it causiously as it removes the previous state of tree i.e. the opened nodes
     */
    public void reload(){
        if( this.controller.reRead() )
            this.initialize( this.controller.getGroupList() );
        else
            JOptionPane.showMessageDialog( null, "The tree could not be reloaded", "Error Reloading...", JOptionPane.ERROR_MESSAGE );
    }

    private boolean addBackCandidateCourses(final String College, final String Program, final String BkCand,int Sem, CourseCode[] courseItr) throws HeadlessException {
        boolean state = true;
        for (int i = 0; i < courseItr.length; i++) {
            CourseCode c = courseItr[i];
            if (!this.Writer.addBackCandidate(BkCand, c.toString(), Program, College,Sem)) {
                state = false;
                JOptionPane.showMessageDialog(null, "The Back Course " + c.toString() + " cannot be added", "Course Not added", JOptionPane.ERROR_MESSAGE);
            }
        }
        return state;
    }

    private void addCollege( Program prg, College college) throws HeadlessException {
        this.Writer.addCollegeCentre(college.getCollegeName(), college.getCentre(prg).getCentreName(), prg.getProgramName());
        this.addRegularSemesters(college.getCollegeName(), prg.getProgramName(), college.getSemesters(prg));
        this.addCollegeBackCandidates(prg.getProgramName(), college.getCollegeName(), college.getBackCandidates(prg));
    }
    
    private void addProgramColleges( Program prg ){
        Iterator<College> collItr = prg.getSupportingColleges().iterator();
        while( collItr.hasNext() ){
            this.addCollege(prg, collItr.next() );
        }
    }
    
    private void addPrograms( ProgramList prglist ){
        Group grp = prglist.getGroup();
        Iterator<Program> prgItr = prglist.getPrograms().iterator();
        while( prgItr.hasNext() ){
            Program prg = prgItr.next();
            this.Writer.addProgram( prg.getProgramName(), grp.getFaculty(), grp.getLevel(), grp.getDiscipline() );
            this.addProgramColleges(prg);
        }
    }

    private void addCollegeBackCandidates(final String Program, final String College, CollegeBackCandidates BkCands) throws HeadlessException {
        Iterator<CandidateInterface> candItr = BkCands.getBackPaperCandidates().iterator();
        while (candItr.hasNext()) {
            CandidateInterface cand = candItr.next();
            final String BkCand = cand.getCandidateID();
            CourseCode[] courseItr = cand.getBackCourses().toArray( new CourseCode[]{} );
            addBackCandidateCourses(College, Program, BkCand,cand.getSemester(), courseItr);
        }
    }

    private void addProgramsToDatabase(ProgramForm form) throws HeadlessException {
        try {
            ProgramList list = form.getPrograms();
            this.addPrograms(list);
            this.Model.addGroupPrograms(list);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error in form submitted. Please try again");
        }
    }

    private void addRegularCourse(final String Program, HashMap<CourseCode, Integer> addedReg, final String Sem, Iterator<CourseCode> CourseItr, DefaultMutableTreeNode RegularCourseSemNode, final String College) throws HeadlessException {
        while (CourseItr.hasNext()) {
            final CourseCode Course = CourseItr.next();
            if (!this.Writer.addRegularCourse(Course.toString(), College, Program, Sem, addedReg.get(Course))) {
                JOptionPane.showMessageDialog(null, "Error!\n" + Course + " could not be added", "Error Adding", JOptionPane.ERROR_MESSAGE);
            } else {
                // visually add it into tree
                this.Model.addCollgeSemesterCourse(RegularCourseSemNode, Course, addedReg.get(Course));
            }
        }
    }

    private void addRegularSemesters(final String College, final String Program, SemesterList list) throws HeadlessException {
        Iterator<Semester> semItr = list.getSemesters().iterator();
        while (semItr.hasNext()) {
            RegularCourses reg = semItr.next().getRegularCourses();
            Iterator<CourseCode> codeItr = reg.getCourses().iterator();
            while (codeItr.hasNext()) {
                CourseCode course = codeItr.next();
                if (!this.Writer.addRegularCourse(course.toString(), College, Program, reg.getSemester().getSemester(), reg.getTotalStudents(course))) {
                    JOptionPane.showMessageDialog(null, course.toString() + " cannot be added", "Error in Course Addition", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private String getBackCandidateName(DefaultMutableTreeNode node) {
        return node.toString().substring(0, node.toString().lastIndexOf('[') );
    }
    
    private int getBackCandidateSemester( DefaultMutableTreeNode node ){
        final String cand = node.toString();
        final String sem = cand.substring( cand.lastIndexOf('[') + 1, cand.length()-1 );
        if( sem.equals( CandidateInterface.BACK_ONLY ) ){
            return CandidateInterface.NON_REGULAR;
        }
        else
            return Integer.parseInt(sem);
    }
    
    private TreeNode getParentNode(TreeNode temp, int ParentIndex, int childIndex) {
        for (int i = childIndex; i > ParentIndex; i--) {
            temp = temp.getParent();
        }
        return temp;
    }
    
    public void find(){
        this.Finder.find();
    }

    private void onMenuClick(MouseEvent evt) {
        switch ( getLevelOfSelectedNode() ) {
            case TOTAL_CANDIDATE_NODE:
                this.displayPopupMenu(evt, this.RegularCourseTotalCandidatePopup );
                break;
            case COURSE_NODE:
                if( this.getParentNode(this.getSelectednode(), this.REGULAR_BACK_NODE, this.COURSE_NODE).toString().equals( this.REGULAR_SEMESTER_TEXT ))
                    this.displayPopupMenu(evt, this.RegularCoursePopup );
                else
                    this.displayPopupMenu(evt, this.BackCandidateCoursePopup );// beware if another node also arises here then it need to be checked
                break;
            case SEMESTER_BACKCANDIDATE_NODE:
                if( this.getSelectednode().getParent().toString().equals( this.REGULAR_SEMESTER_TEXT ) )
                    this.displayPopupMenu(evt, this.SemesterPopup  );                
                else// beware here too
                    this.displayPopupMenu(evt, this.BackCandidatePopup );
                break;
            case REGULAR_BACK_NODE:
                if( this.getSelectednode().toString().equals( this.REGULAR_SEMESTER_TEXT ) )
                    this.displayPopupMenu(evt, this.RegularSemesterPopup  );                
                else
                    this.displayPopupMenu(evt, this.BackCandidatesListPopup  );
                break;            
            case COLLEGE_NODE:
                this.displayPopupMenu(evt, this.CollegePopup );
                break;
            case PROGRAM_NODE:
                this.displayPopupMenu(evt, this.ProgramPopup );
                break;
            case DISCIPLINE_NODE:
                this.displayPopupMenu(evt, this.DisciplinePopup );
                break;            
            case FACULTY_NODE:
                this.displayPopupMenu(evt, this.FacultyPopup );
                break;
            case LEVEL_NODE:
                this.displayPopupMenu(evt, LevelPopup );
                break;
        }
        if( this.getSelectednode() == this.FacultyNode ){
            this.displayPopupMenu(evt, this.AddFacultyPopup  );
        }
    }
    
    private void removeFaculties(){        
        this.FacultyNode.removeAllChildren();
        //this.updateUI();
    }
        
    private DefaultMutableTreeNode returnChild( DefaultMutableTreeNode Parent , String Child )
   {
       for( int i=0;i<Parent.getChildCount(); i++)
       {
           DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) Parent.getChildAt( i );
           if( node.toString().equals( Child ) )
           {
               return node;
           }
       }
       // else if Child dosent exists
       return null;
   }
    
    private void onMouseClick( java.awt.event.MouseEvent evt ){
        if( selectClickedNode( evt) )return;
        if( this.isMenuClick(evt) ){
            onMenuClick(evt);
        }        
    }
    
    private final boolean isMenuClick( java.awt.event.MouseEvent evt ){
        return evt.getButton() == evt.BUTTON3;
    }
    
    private void displayPopupMenu(final java.awt.event.MouseEvent evt, final javax.swing.JPopupMenu Menu ) {
        Menu.show(evt.getComponent(),evt.getX(), evt.getY());
    }

    private final boolean selectClickedNode(MouseEvent evt) {
        TreePath SelectedPath = this.getClosestPathForLocation(evt.getX(), evt.getY());
        if (SelectedPath == null) {
            return true;
        }
        if (this.getPathBounds(SelectedPath).contains(evt.getX(), evt.getY())) {
            //TreeNode ClickedNode = (TreeNode) SelectedPath.getLastPathComponent();
            this.setSelectionPath(SelectedPath);
        } else {
            return true;
        }
        return false;
    }
    
    public final DefaultMutableTreeNode getSelectednode(){           
        return (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
    }    
    
    private void changeTotalCandidateForSelectedRegularCourseNode(){
        DefaultMutableTreeNode TotalCandNode = this.getSelectednode();
        String RegularCourse = TotalCandNode.getParent().toString();
        String InitialSum = TotalCandNode.toString();
        RegularCourseTotalCandidateForm form = new RegularCourseTotalCandidateForm( null, RegularCourse, InitialSum );
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
            final String NewTotalCand = form.getTotalCandidates();
            final String Prg = getParentNode( TotalCandNode, this.PROGRAM_NODE, this.TOTAL_CANDIDATE_NODE ).toString();
            final String college = this.getParentNode( TotalCandNode, this.COLLEGE_NODE, this.TOTAL_CANDIDATE_NODE ).toString();
            final String sem = this.getParentNode( TotalCandNode, this.SEMESTER_BACKCANDIDATE_NODE, this.TOTAL_CANDIDATE_NODE ).toString();
            final String course = this.getParentNode( TotalCandNode, this.COURSE_NODE, this.TOTAL_CANDIDATE_NODE ).toString();
            if( this.Writer.updateRegularCourseTotalCandidates(NewTotalCand, course, sem, Prg, college ) ){                
                this.updateNode( TotalCandNode, NewTotalCand );
            }
            else
                JOptionPane.showMessageDialog( null, "Value was not updated", "Update failure", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    private void addRegularCourse(){
        try{
        DefaultMutableTreeNode RegularCourseSemNode = this.getSelectednode();
        if( ! RegularCourseSemNode.getParent().toString().equals( this.REGULAR_SEMESTER_TEXT )) return;
        final String College = this.getParentNode( RegularCourseSemNode, this.COLLEGE_NODE, this.SEMESTER_BACKCANDIDATE_NODE  ).toString();
        final String Program = this.getParentNode(RegularCourseSemNode, this.PROGRAM_NODE, this.SEMESTER_BACKCANDIDATE_NODE  ).toString();
        final String Sem = RegularCourseSemNode.toString();
        
        final String discipline = this.getParentNode( RegularCourseSemNode, this.DISCIPLINE_NODE, this.SEMESTER_BACKCANDIDATE_NODE ).toString();
        final String level = this.getParentNode( RegularCourseSemNode, this.LEVEL_NODE, this.SEMESTER_BACKCANDIDATE_NODE ).toString();
        final String fac = this.getParentNode( RegularCourseSemNode, this.FACULTY_NODE, this.SEMESTER_BACKCANDIDATE_NODE ).toString();
        
        Group grp = this.controller.getGroupList().getGroup(fac, level, discipline );
        
        RegularCourseForm form = new RegularCourseForm( null, Program, Sem, College, this.controller.getGroupList().getCoursesMap(grp) );
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
            HashMap<CourseCode, java.lang.Integer> addedReg = form.getCourses();
            Iterator<CourseCode> CourseItr = addedReg.keySet().iterator();
            addRegularCourse(Program, addedReg, Sem, CourseItr, RegularCourseSemNode, College);
        }
        }
        catch( Exception e ){
            JOptionPane.showMessageDialog( null, e.getMessage(), "failure", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    private void addBackCourse(){
        try{
        DefaultMutableTreeNode node = this.getSelectednode();
        final String College = this.getParentNode(node, this.COLLEGE_NODE, SEMESTER_BACKCANDIDATE_NODE ).toString();
        final String Program = this.getParentNode(node, this.PROGRAM_NODE, SEMESTER_BACKCANDIDATE_NODE ).toString();
        final String Cand = this.getBackCandidateName(node);
        final int sem = this.getBackCandidateSemester(node);
        
        final String discipline = this.getParentNode( node, this.DISCIPLINE_NODE, this.SEMESTER_BACKCANDIDATE_NODE ).toString();
        final String level = this.getParentNode( node, this.LEVEL_NODE, this.SEMESTER_BACKCANDIDATE_NODE ).toString();
        final String fac = this.getParentNode( node, this.FACULTY_NODE, this.SEMESTER_BACKCANDIDATE_NODE ).toString();
        
        Group grp = this.controller.getGroupList().getGroup(fac, level, discipline );
        
        BackCandidateForm form = new BackCandidateForm( null, Cand, this.controller.getGroupList().getCoursesMap(grp) );
        
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
            CourseCode[] courses = form.getCourses();
            if( addBackCandidateCourses(College, Program, Cand, sem, courses  ) )
                this.Model.addCollegeBackCandidateCourses( node, java.util.Arrays.asList( courses ) );
        }
        }
        catch( Exception e ){
            JOptionPane.showMessageDialog( null, e.getMessage(), "failure", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    private void addRegularSemesters(){
        try{
        DefaultMutableTreeNode node = this.getSelectednode();
        if( ! node.toString().equals( this.REGULAR_SEMESTER_TEXT )) return;
        final String College = this.getParentNode(node, this.COLLEGE_NODE, this.REGULAR_BACK_NODE ).toString();
        final String Program = this.getParentNode(node, this.PROGRAM_NODE, this.REGULAR_BACK_NODE ).toString();
        
        final String fac = this.getParentNode(node, this.FACULTY_NODE, this.REGULAR_BACK_NODE ).toString();
        final String level = this.getParentNode(node, this.LEVEL_NODE, this.REGULAR_BACK_NODE ).toString();
        final String discp = this.getParentNode(node, this.DISCIPLINE_NODE, this.REGULAR_BACK_NODE ).toString();
        
        Group grp = this.controller.getGroupList().getGroup(fac, level, discp);
        
        CollegeSemesterForm form = new CollegeSemesterForm( null,true, College, Program, this.controller.getGroupList().getCoursesMap(grp) );
        
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
            SemesterList list = form.getAddedSemesters();
            addRegularSemesters(College, Program, list);
            this.Model.addCollegeRegularSemesters( node, list.getSemesters() );
        }
        }
        catch( Exception e ){
            JOptionPane.showMessageDialog( null, e.getMessage(), "failure", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    private void addBackCandidates(){
        try{
        DefaultMutableTreeNode node = this.getSelectednode();        
        final String College = this.getParentNode(node, this.COLLEGE_NODE, this.REGULAR_BACK_NODE ).toString();
        final String Program = this.getParentNode(node, this.PROGRAM_NODE, this.REGULAR_BACK_NODE ).toString();
        
        final String fac = this.getParentNode(node, this.FACULTY_NODE, this.REGULAR_BACK_NODE ).toString();
        final String level = this.getParentNode(node, this.LEVEL_NODE, this.REGULAR_BACK_NODE ).toString();
        final String discp = this.getParentNode(node, this.DISCIPLINE_NODE, this.REGULAR_BACK_NODE ).toString();
        
        Group grp = this.controller.getGroupList().getGroup(fac, level, discp);
        
        BackCandidatesForm form = new BackCandidatesForm( null, Program, College, this.controller.getGroupList().getCoursesMap(grp) );
        
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
            CollegeBackCandidates BkCands = form.getBackCandidates();
            addCollegeBackCandidates(Program, College, BkCands);
            this.Model.addCollegeBackCandidates(node, BkCands);
        }
        }
        catch( Exception e ){
            JOptionPane.showMessageDialog( null, e.getMessage(), "failure", JOptionPane.ERROR_MESSAGE );
        }        
    }
    
    private void deleteBackCandidate(){
        DefaultMutableTreeNode node = this.getSelectednode();        
        final String cand = getBackCandidateName(node);
        if( this.Writer.removeBackCandidate(cand))
            this.Model.removeNodeFromParent(node);
        else
            JOptionPane.showMessageDialog( null, cand +" cannot be deleted", "Error", JOptionPane.ERROR_MESSAGE );
    }
    
    private void addColleges(){
        try{
        DefaultMutableTreeNode node = this.getSelectednode();
        final String Prg = node.toString();
        final String fac = this.getParentNode(node, this.FACULTY_NODE, this.PROGRAM_NODE ).toString();
        final String level = this.getParentNode(node, this.LEVEL_NODE, this.PROGRAM_NODE ).toString();
        final String discp = this.getParentNode(node, this.DISCIPLINE_NODE, this.PROGRAM_NODE ).toString();
        
        Group grp = this.controller.getGroupList().getGroup(fac, level, discp);
        
        CollegesForm form = new CollegesForm( null, Prg, fac, level, discp, this.Writer.getCentres().keySet().toArray( new String[]{} ), this.controller.getGroupList().getCoursesMap(grp) );
        
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){            
            Program prg = form.getProgram( grp );
            this.addProgramColleges( prg );
            this.Model.addProgramColleges( node, prg );
        }
        }
        catch( Exception e ){
            JOptionPane.showMessageDialog( null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
        }        
    }
    
    private void editGroupPrograms(){
        try{
            DefaultMutableTreeNode node = this.getSelectednode();        
        
            final String fac = this.getParentNode(node, this.FACULTY_NODE, this.DISCIPLINE_NODE ).toString();
            final String level = this.getParentNode(node, this.LEVEL_NODE, this.DISCIPLINE_NODE ).toString();
            final String discp = node.toString();
            // reading databases
            this.controller.reRead();
            Group grp = this.controller.getGroupList().getGroup(fac, level, discp);        
            ProgramList prgList = this.controller.getGroupList().getPrograms(grp);            
            ProgramForm form = new ProgramForm( null, prgList, this.Writer.getCentres().keySet().toArray( new String[]{} ), this.controller.getGroupList().getCoursesMap(grp) );            
            form.setVisible( true );
            
            if( form.getReturnStatus() == form.RET_OK ){
                java.util.Iterator<Program> prgItr = prgList.getPrograms().iterator();
                while( prgItr.hasNext() ){
                    this.Writer.removeProgram( prgItr.next().getProgramName(), fac, level, discp);
                }
                java.util.Enumeration<DefaultMutableTreeNode> prgNodes = node.children();
                while( prgNodes.hasMoreElements() ){
                    DefaultMutableTreeNode prgNode = prgNodes.nextElement();
                    this.Model.removeNodeFromParent( prgNode );
                }
                this.addProgramsToDatabase(form);
            }
        }
        catch( Exception e ){
            JOptionPane.showMessageDialog( null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    private void addPrograms(){
        try{
        DefaultMutableTreeNode node = this.getSelectednode();        
        
        final String fac = this.getParentNode(node, this.FACULTY_NODE, this.DISCIPLINE_NODE ).toString();
        final String level = this.getParentNode(node, this.LEVEL_NODE, this.DISCIPLINE_NODE ).toString();
        final String discp = node.toString();
        
        Group grp = this.controller.getGroupList().getGroup(fac, level, discp);
        
        ProgramForm form = new ProgramForm( null, fac, level, discp, this.Writer.getCentres().keySet().toArray( new String[]{} ), this.controller.getGroupList().getCoursesMap(grp) );
        
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
                addProgramsToDatabase(form);
        }
        }
        catch( Exception e ){
            JOptionPane.showMessageDialog( null,e.getMessage() );
        }
    }
    
    private void addFaculty(){
        FacultyForm form = new FacultyForm( null );
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
            Group grp = form.getGroup();
            if( this.Writer.addGroup( grp.getFaculty(), grp.getLevel(), grp.getDiscipline() ))
                this.Model.addGroup(grp);
            else
                JOptionPane.showMessageDialog( null, grp.getFaculty()+" could not be added", "Error faculty Addition", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    private void updateFaculty(){
        DefaultMutableTreeNode node = this.getSelectednode();
        final String fac = node.toString();
        updateFacultyForm form = new updateFacultyForm( null, fac );
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
            if( this.Writer.updateFaculty(fac, form.getNewFacultyName() ) ){
                this.updateNode(node, form.getNewFacultyName() );
            }
            else
                JOptionPane.showMessageDialog( null, fac+" could not be updated", "Error ", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    private void deleteFaculty(){
        DefaultMutableTreeNode node = this.getSelectednode();
        final String fac = node.toString();
        int state = JOptionPane.showConfirmDialog( null, "Are you sure you want to delete "+fac+" and all programs within it", "Confirmation", JOptionPane.YES_NO_OPTION );
        if( state == JOptionPane.YES_OPTION ){
            if( this.Writer.deleteFaculty(fac) ){
                this.Model.removeNodeFromParent(node);
            }
            else
                JOptionPane.showMessageDialog( null, fac+" could not be deleted", "Error Level Deletion", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    private void addLevel(){
        final String fac = this.getSelectednode().toString();        
        LevelForm form = new LevelForm(null,fac);
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
            Group grp = form.getGroup();
            if( this.Writer.addGroup( grp.getFaculty(), grp.getLevel(), grp.getDiscipline() ))
                this.Model.addGroup(grp);
            else
                JOptionPane.showMessageDialog( null, grp.getLevel()+" could not be added", "Error Level Addition", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    private void deleteLevel(){
        DefaultMutableTreeNode node = this.getSelectednode();
        final String level = node.toString();
        final String fac = this.getParentNode(node, this.FACULTY_NODE, this.LEVEL_NODE ).toString();
        int state = JOptionPane.showConfirmDialog( null, "Are you sure you want to delete "+fac+"/"+level+" and all programs within it", "Confirmation", JOptionPane.YES_NO_OPTION );
        if( state == JOptionPane.YES_OPTION ){
            if( this.Writer.removeLevel(fac, level)){
                this.reload();
            }
            else
                JOptionPane.showMessageDialog( null, level+" could not be deleted", "Error Level Deletion", JOptionPane.ERROR_MESSAGE );
        }        
    }
    
    private void updateLevel(){
        DefaultMutableTreeNode node = this.getSelectednode();
        final String level = node.toString();
        final String fac = this.getParentNode(node, this.FACULTY_NODE, this.LEVEL_NODE ).toString();
        levelUpdateForm form = new levelUpdateForm( null, fac, level );
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
            if( this.Writer.updateLevel(fac, level, form.getNewLevel() ) ){
                this.updateNode(node, form.getNewLevel() );
            }
            else
                JOptionPane.showMessageDialog( null, level+" could not be updated", "Error ", JOptionPane.ERROR_MESSAGE );
        }            
    }
    
    private void addDiscipline(){
        DefaultMutableTreeNode node = this.getSelectednode();
        final String level = node.toString();
        final String fac = this.getParentNode( node, this.FACULTY_NODE, this.LEVEL_NODE ).toString();
        DisciplineForm form = new DisciplineForm(null, fac,level);
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
            Group grp = form.getGroup();
            if( this.Writer.addGroup( grp.getFaculty(), grp.getLevel(), grp.getDiscipline() ))
                this.Model.addGroup(grp);
            else
                JOptionPane.showMessageDialog( null, grp.getDiscipline()+" could not be added", "Error discipline Addition", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    private void updateDiscipline(){
        DefaultMutableTreeNode node = this.getSelectednode();        
        final String fac = this.getParentNode(node, this.FACULTY_NODE, this.DISCIPLINE_NODE ).toString();
        final String level = this.getParentNode(node, this.LEVEL_NODE, this.DISCIPLINE_NODE ).toString();
        final String discp = node.toString();
        DisciplineUpdateForm form = new DisciplineUpdateForm(null, fac, level, discp );
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
            if( this.Writer.updateDiscipline(fac, level, discp, form.getNewDiscipline() ) ){
                this.updateNode(node, form.getNewDiscipline() );
            }
            else
                JOptionPane.showMessageDialog( null, discp +" could not be updated", "Error", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    private void deleteDiscipline(){
        DefaultMutableTreeNode node = this.getSelectednode();        
        final String fac = this.getParentNode(node, this.FACULTY_NODE, this.DISCIPLINE_NODE ).toString();
        final String level = this.getParentNode(node, this.LEVEL_NODE, this.DISCIPLINE_NODE ).toString();
        final String discp = node.toString();
        int state = JOptionPane.showConfirmDialog( null, "Are you sure you want to delete discipline "+discp+" and all programs within "+ fac+"/"+level+"/"+discp+" and other info", "Confirmation", JOptionPane.YES_NO_OPTION );
        if( state == JOptionPane.YES_OPTION ){
            if( this.Writer.deleteDiscipline(fac, level, discp)){
                this.reload();
            }
            else
                JOptionPane.showMessageDialog( null, discp + " could not be removed", "Error", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    private void removeBackCourse(){
        DefaultMutableTreeNode node = this.getSelectednode();
        final String course = node.toString();
        final String college = this.getParentNode(node, this.COLLEGE_NODE, this.COURSE_NODE ).toString();
        final String prg = this.getParentNode(node, this.PROGRAM_NODE, this.COURSE_NODE ).toString();
        final String BackCandID = this.getBackCandidateName( (DefaultMutableTreeNode)this.getParentNode(node, this.SEMESTER_BACKCANDIDATE_NODE, this.COURSE_NODE ));
        if( this.Writer.removeBackCourse(course, college, prg, BackCandID ) ){
            this.Model.removeNodeFromParent(node);
        }
        else
            JOptionPane.showMessageDialog( null, course + " could not be removed", "Error", JOptionPane.ERROR_MESSAGE );        
    }
    
    private void removeRegularCourse(){
        DefaultMutableTreeNode node = this.getSelectednode();
        final String course = node.toString();
        final String sem = this.getParentNode(node, this.SEMESTER_BACKCANDIDATE_NODE, this.COURSE_NODE ).toString();
        final String college = this.getParentNode(node, this.COLLEGE_NODE, this.COURSE_NODE ).toString();
        final String prg = this.getParentNode(node, this.PROGRAM_NODE, this.COURSE_NODE ).toString();
        if( this.Writer.removeRegularCourse(course, college, prg, sem) ){
            this.Model.removeNodeFromParent(node);
        }
        else
            JOptionPane.showMessageDialog( null, course + " could not be removed", "Error", JOptionPane.ERROR_MESSAGE );
    }
    
    private void updateRegularCourse(){
        DefaultMutableTreeNode node = this.getSelectednode();
        final String course = node.toString();
        final String sem = this.getParentNode(node, this.SEMESTER_BACKCANDIDATE_NODE, this.COURSE_NODE ).toString();
        final String college = this.getParentNode(node, this.COLLEGE_NODE, this.COURSE_NODE ).toString();
        final String prg = this.getParentNode(node, this.PROGRAM_NODE, this.COURSE_NODE ).toString();
        updateRegularCourseForm form = new updateRegularCourseForm(null, course );
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
            if( this.Writer.updateRegularCourse(course, college, prg, sem, form.getNewCourseVal() ) ){
                this.updateNode(node, form.getNewCourseVal() );
            }
            else
                JOptionPane.showMessageDialog( null, course + " could not be updated", "Error", JOptionPane.ERROR_MESSAGE );
        }        
    }
    
    private void updateBackCandidate(){
        DefaultMutableTreeNode node = this.getSelectednode();
        final String oldCandID = this.getBackCandidateName(node);        
        final String college = this.getParentNode(node, this.COLLEGE_NODE, this.SEMESTER_BACKCANDIDATE_NODE ).toString();
        final String prg = this.getParentNode(node, this.PROGRAM_NODE, this.SEMESTER_BACKCANDIDATE_NODE ).toString();
        ExamCandidateUpdateForm form = new ExamCandidateUpdateForm( null, college, prg, oldCandID );
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
            if( this.Writer.updateBackCandidate(oldCandID, form.getNewCandidateID() )){
                this.updateNode(node, form.getNewCandidateID()+'['+this.getBackCandidateSemester(node)+']' );
            }
            else
                JOptionPane.showMessageDialog( null, oldCandID + " could not be updated", "Error", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    private void updateBackCandidateSemester(){
        DefaultMutableTreeNode node = this.getSelectednode();
        final String cand = this.getBackCandidateName(node);        
        final int oldSem = this.getBackCandidateSemester(node);
        BackCanidateSemesterForm form = new BackCanidateSemesterForm( null );
        form.initialize( cand, oldSem );
        form.setVisible( true );
        if( form.getState() == form.ok ){
            if( this.Writer.updateBackCandidateSemester(cand, oldSem, form.getSemester() )){
                this.updateNode(node, this.Model.getBackCandidateNodeValue(cand, form.getSemester() ));
            }
            else
                JOptionPane.showMessageDialog( null, cand +"'s Semester could not be updated", "Error", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    private void updateCandidateBackCourse(){
        DefaultMutableTreeNode node = this.getSelectednode();
        final String course = node.toString();
        final String BackCandID = this.getBackCandidateName( (DefaultMutableTreeNode)this.getParentNode(node, this.SEMESTER_BACKCANDIDATE_NODE, this.COURSE_NODE ));
        final String college = this.getParentNode(node, this.COLLEGE_NODE, this.COURSE_NODE ).toString();
        final String prg = this.getParentNode(node, this.PROGRAM_NODE, this.COURSE_NODE ).toString();
        updateRegularCourseForm form = new updateRegularCourseForm(null, course );
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
            if( this.Writer.updateBackCourse(course, college, prg, BackCandID, form.getNewCourseVal() ) ){
                this.updateNode(node, form.getNewCourseVal() );
            }
            else
                JOptionPane.showMessageDialog( null, course + " could not be updated", "Error", JOptionPane.ERROR_MESSAGE );
        }        
    }
    
    private void removeRegularSemester(){
        DefaultMutableTreeNode node = this.getSelectednode();
        final String College = this.getParentNode(node, this.COLLEGE_NODE, this.SEMESTER_BACKCANDIDATE_NODE).toString();
        final String Program = this.getParentNode(node, this.PROGRAM_NODE, this.SEMESTER_BACKCANDIDATE_NODE ).toString();
        final String sem = node.toString();
        if( this.Writer.removeRegularSemester(College, Program, sem )){
            this.Model.removeNodeFromParent(node);
        }
        else
            JOptionPane.showMessageDialog( null, sem + " could not be removed", "Error", JOptionPane.ERROR_MESSAGE );
    }
    
    private void updateRegularSemester(){
        DefaultMutableTreeNode node = this.getSelectednode();
        final String College = this.getParentNode(node, this.COLLEGE_NODE, this.SEMESTER_BACKCANDIDATE_NODE).toString();
        final String Program = this.getParentNode(node, this.PROGRAM_NODE, this.SEMESTER_BACKCANDIDATE_NODE ).toString();
        final String sem = node.toString();
        SemesterUpdateForm form = new SemesterUpdateForm(null, College, Program, sem );
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
            if( this.Writer.updateRegularSemester(College, Program, sem, form.getNewSemesterValue() )){
                this.updateNode(node, form.getNewSemesterValue() );
            }
            else
                JOptionPane.showMessageDialog( null, sem + " could not be updated", "Error", JOptionPane.ERROR_MESSAGE );
        }        
    }
    
    private void deleteProgramCollege(){
        DefaultMutableTreeNode node = this.getSelectednode();
        final String College = node.toString();
        final String Program = this.getParentNode(node, this.PROGRAM_NODE, this.COLLEGE_NODE ).toString();
        final int state = JOptionPane.showConfirmDialog( null, "are you sure you want to delete "+College +" for "+Program+" and its semesters and related info", "delete college confirmation", JOptionPane.YES_NO_OPTION );
        if( state == JOptionPane.YES_OPTION ){
            if( !this.Writer.removeCollegeProgram(College, Program) )
                JOptionPane.showMessageDialog( null, "Program::"+Program+" of "+College +" could not be removed", "Error", JOptionPane.ERROR_MESSAGE );
            else                
                this.Model.removeNodeFromParent(node);            
        }
    }
    
    private void deleteCollege(){
        DefaultMutableTreeNode node = this.getSelectednode();
        final String College = node.toString();
        final int state = JOptionPane.showConfirmDialog( null, "are you sure you want to delete entire "+College +" from database along with all semeseters and related info", "delete college confirmation", JOptionPane.YES_NO_OPTION );
        if( state == JOptionPane.YES_OPTION ){
            if( !this.Writer.removeCollege(College) )
                JOptionPane.showMessageDialog( null, College +" could not be removed", "Error", JOptionPane.ERROR_MESSAGE );
            else{                
                this.reload();
            }            
        }
    }
    
    private void deleteProgram(){
        DefaultMutableTreeNode node = this.getSelectednode();        
        final String prg = node.toString();
        final String fac = this.getParentNode(node, this.FACULTY_NODE, this.PROGRAM_NODE ).toString();
        final String level = this.getParentNode(node, this.LEVEL_NODE, this.PROGRAM_NODE ).toString();
        final String discp = this.getParentNode(node, this.DISCIPLINE_NODE, this.PROGRAM_NODE ).toString();
        final int state = JOptionPane.showConfirmDialog( null, "are you sure you want to delete entire program "+ prg +" from database along with all colleges program teaching it", "delete Program confirmation", JOptionPane.YES_NO_OPTION );
        if( state == JOptionPane.YES_OPTION ){
            if( this.Writer.removeProgram(prg,fac, level, discp ) ){
                this.Model.removeNodeFromParent(node);
            }
            else
                JOptionPane.showMessageDialog( null, prg+" cannot be deleted", "delete Program error", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    private void updateProgram(){
        DefaultMutableTreeNode node = this.getSelectednode();
        final String prg = node.toString();
        final String fac = this.getParentNode(node, this.FACULTY_NODE, this.PROGRAM_NODE ).toString();
        final String level = this.getParentNode(node, this.LEVEL_NODE, this.PROGRAM_NODE ).toString();
        final String discp = this.getParentNode(node, this.DISCIPLINE_NODE, this.PROGRAM_NODE ).toString();
        updateProgramForm form = new updateProgramForm( null, fac, level, discp, prg );
        form.setVisible( true );
        if( form.getReturnStatus() == form.RET_OK ){
            if( this.Writer.updateProgram(prg, fac, level, discp, form.getText() )){
                this.updateNode(node, form.getText() );
            }
            else
                JOptionPane.showMessageDialog( null, prg+" cannot be updated", "update Program error", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    private void deleteAllFaculties(){
        final int state = JOptionPane.showConfirmDialog( null, "Are You SURE you want to delete entire faculties along with all info. This is large tasks and can lead to huge date loss that is irrevocable", "All Faculties deletion confirmation", JOptionPane.YES_NO_OPTION );
        if( state == JOptionPane.YES_OPTION ){
            if( this.Writer.removeAllFaculties() ){
                this.FacultyNode.removeAllChildren();
                this.updateUI();
            }
            else
                JOptionPane.showMessageDialog( null, "faculties cannot be deleted", "Error", JOptionPane.ERROR_MESSAGE );
        }        
    }
    
    private boolean updateNode( DefaultMutableTreeNode node, final String NewVal ){
        try{
            node.setUserObject( NewVal );
            this.updateUI();
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }
    
    private class ExamDataTreeModel extends javax.swing.tree.DefaultTreeModel{
        
        public ExamDataTreeModel( DefaultMutableTreeNode Root ){
            super( Root );
        }
        
        public void addGroups( GroupList grps ){
            java.util.Iterator<Group> grpitr = grps.getGroups().iterator();
            while( grpitr.hasNext() ){
                Group grp = grpitr.next();
                this.addGroup(grp);
                this.addGroupPrograms( grps.getPrograms(grp) );
            }
        }
    
    public void addGroupPrograms( ProgramList prglist ){
        Iterator<Program> prgItr = prglist.getPrograms().iterator();
        while( prgItr.hasNext() ){
            this.addGroupProgram( prgItr.next() );
        }
    }
    
    public boolean addGroupProgram( Program prg ){        
        DefaultMutableTreeNode grpNode = this.getGroupNode(prg.getGroup());        
        if( grpNode != null ){
            DefaultMutableTreeNode prgNode = returnChild( grpNode, prg.getProgramName());
            if( prgNode == null ){
                prgNode = new DefaultMutableTreeNode( prg.getProgramName() );
                super.insertNodeInto(prgNode, grpNode, 0 );
                this.addProgramColleges(prgNode, prg);
                return true;
            }              
        }        
        return false;
    }
    
    private void addProgramColleges( DefaultMutableTreeNode ProgramNode, Program prg ){
        Iterator<College> CollItr = prg.getSupportingColleges().iterator();
        while( CollItr.hasNext() ){
            College c = CollItr.next();
            this.addProgramCollege(ProgramNode, c, prg );
        }
    }
    
    private void addProgramCollege( DefaultMutableTreeNode ProgramNode, College college, Program prg ){
        DefaultMutableTreeNode CollegeNode = new DefaultMutableTreeNode(college.getCollegeName());
        DefaultMutableTreeNode RegularSemesterNode = new DefaultMutableTreeNode( REGULAR_SEMESTER_TEXT  );
        DefaultMutableTreeNode BackCandidatesNode = new DefaultMutableTreeNode( BACK_CANDIDATE_TEXT  );
        CollegeNode.add( RegularSemesterNode );
        CollegeNode.add( BackCandidatesNode );
        super.insertNodeInto( CollegeNode, ProgramNode, 0 );
        SemesterList list = college.getSemesters(prg);
        this.addCollegeRegularSemesters(RegularSemesterNode, list.getSemesters() );
        this.addCollegeBackCandidates( BackCandidatesNode, college.getBackCandidates(prg));
    }
    
    private void addCollegeRegularSemesters( DefaultMutableTreeNode RegularSemesterNode, Collection<Semester> Sems ){
        Iterator<Semester> SemItr = Sems.iterator();
        while( SemItr.hasNext() ){
            this.addCollegeRegularSemester(RegularSemesterNode, SemItr.next() );
        }
    }
    
    private void addCollegeRegularSemester( DefaultMutableTreeNode RegularSemesterNode, Semester Sem ){
        DefaultMutableTreeNode SemNode = new DefaultMutableTreeNode( Sem.getSemester() );
        super.insertNodeInto( SemNode, RegularSemesterNode, 0 );
        if(DEBUG)System.out.println( "semester "+Sem.getProgram()+":"+Sem.getCollege()+":"+Sem.getSemester() );
        this.addCollegesSemesterCourses(SemNode, Sem.getRegularCourses() );
    }
    
    private void addCollegesSemesterCourses( DefaultMutableTreeNode SemNode, RegularCourses reg ){
        Iterator<CourseCode> CoursesItr = reg.getCourses().iterator();
        while( CoursesItr.hasNext() ){
            CourseCode course = CoursesItr.next();
            this.addCollgeSemesterCourse( SemNode, course, reg.getTotalStudents( course ) );
        }
    }
    
    private void addCollgeSemesterCourse( DefaultMutableTreeNode SemNode, CourseCode Course, int len ){
        DefaultMutableTreeNode CourseNode = new DefaultMutableTreeNode( Course.toString() );
        DefaultMutableTreeNode TotalCandidates = new DefaultMutableTreeNode( len + "" );
        CourseNode.add( TotalCandidates );
        super.insertNodeInto( CourseNode, SemNode, 0 );
    }
    
    private void addCollegeBackCandidates( DefaultMutableTreeNode BackNode, CollegeBackCandidates BkList ){
        Iterator<puexamroutine.control.domain.interfaces.CandidateInterface> BkItr = BkList.getBackPaperCandidates().iterator();
        while( BkItr.hasNext() ){
            this.addCollegeBackCandidate(BackNode, BkItr.next() );
        }
    }
    
    private void addCollegeBackCandidate( DefaultMutableTreeNode BackNode, puexamroutine.control.domain.interfaces.CandidateInterface BackCand ){
        // here Semester is added with the Candidate ID
        DefaultMutableTreeNode Bk = new DefaultMutableTreeNode( this.getBackCandidateNodeValue( BackCand.getCandidateID(), BackCand.getSemester() ) );
        super.insertNodeInto( Bk, BackNode, 0 );
        this.addCollegeBackCandidateCourses( Bk, BackCand.getBackCourses() );
    }
    
    public final String getBackCandidateNodeValue( final String Name, final int sem ){
        return Name +"["+(( sem ==puexamroutine.control.domain.Candidate.NON_REGULAR)?puexamroutine.control.domain.Candidate.BACK_ONLY: sem)+"]";
    }
    
    private void addCollegeBackCandidateCourses( DefaultMutableTreeNode BackCandNode, Collection<CourseCode> BkPapers ){
        Iterator<CourseCode> BkPaperItr = BkPapers.iterator();
        while( BkPaperItr.hasNext() ){
            DefaultMutableTreeNode BkPaper = new DefaultMutableTreeNode( BkPaperItr.next() );
            super.insertNodeInto( BkPaper, BackCandNode, 0 );
        }
    }
    
    public DefaultMutableTreeNode addGroup( Group grp ){
        final String fac = grp.getFaculty();
        final String level = grp.getLevel();
        final String discp = grp.getDiscipline();
        
        DefaultMutableTreeNode node = returnChild( FacultyNode, fac );
        if( node == null ){
            return addFaculty( fac,level, discp );
        }
        else{
            DefaultMutableTreeNode LevelNode = returnChild( node, level );
            if( LevelNode == null ){
                return addLevel( node, level, discp );
            }
            else{
                DefaultMutableTreeNode DispNode = returnChild( LevelNode, discp );
                if( DispNode == null ){
                    DispNode = new DefaultMutableTreeNode( discp );
                    super.insertNodeInto( DispNode, LevelNode, 0 );
                    return DispNode;
                }
                else{
                    JOptionPane.showMessageDialog(null, "could not add requested discipline node since it is repeated", "Error Adding Discipline", JOptionPane.ERROR_MESSAGE );
                    return null;
                }
            }
        }        
    }

    private DefaultMutableTreeNode addFaculty(final String fac, final String level, final String discp) {
        DefaultMutableTreeNode FacNode = new DefaultMutableTreeNode(fac);
        DefaultMutableTreeNode levelNode = new DefaultMutableTreeNode(level);
        DefaultMutableTreeNode discpNode = new DefaultMutableTreeNode(discp);
        levelNode.add(discpNode);
        FacNode.add(levelNode);
        super.insertNodeInto(FacNode, FacultyNode, 0);
        return discpNode;
    }

    private DefaultMutableTreeNode addLevel(DefaultMutableTreeNode facNode, final String level, final String discp) {
        DefaultMutableTreeNode levelNode = new DefaultMutableTreeNode(level);
        DefaultMutableTreeNode discpNode = new DefaultMutableTreeNode(discp);
        levelNode.add(discpNode);
        super.insertNodeInto( levelNode, facNode, 0);
        return discpNode;
    }
    
    private DefaultMutableTreeNode getGroupNode( Group grp ){
        DefaultMutableTreeNode fac = returnChild( FacultyNode, grp.getFaculty() );
        if( fac != null){
            DefaultMutableTreeNode level = returnChild( fac, grp.getLevel());
            if( level != null ){
                DefaultMutableTreeNode disp = returnChild(level, grp.getDiscipline());
                if( disp != null )
                    return disp;                
            }
        }
        return null;
    }
    
    }// model class ends
    
    private ExamDataTreeModel Model = null;
    private DefaultMutableTreeNode Root = null;
    private DefaultMutableTreeNode FacultyNode = null;    
    public final String ROOT_TEXT = "University";
    public final String FACULTY_NODE_TEXT = "Faculty";
    public final String LEVEL_NODE_TEXT = "Level";
    public final String DISCIPLINE_NODE_TEXT = "Discipline";
    public final String REGULAR_SEMESTER_TEXT="Regular Semesters";
    public final String BACK_CANDIDATE_TEXT = "Back Candidates";
    
    public final static int ROOT_NODE = 1;
    public final static int FACULTY_NODE = 3;
    public final static int LEVEL_NODE = 4;
    public final static int DISCIPLINE_NODE = 5;
    public final static int PROGRAM_NODE = 6;
    public final static int COLLEGE_NODE = 7;
    public final static int REGULAR_BACK_NODE = 8;
    public final static int SEMESTER_BACKCANDIDATE_NODE = 9;
    public final static int COURSE_NODE = 10;
    public final static int TOTAL_CANDIDATE_NODE = 11;
    
    private puexamroutine.control.database.DatabaseWriter Writer = null;
    private puexamroutine.control.Controller controller = null;
    
    private JPopupMenu BackCandidatesListPopup, RegularCourseTotalCandidatePopup;
    private JPopupMenu SemesterPopup,RegularSemesterPopup,ProgramPopup;
    private JPopupMenu DisciplinePopup, AddFacultyPopup, RegularCoursePopup, BackCandidatePopup;
    private JPopupMenu FacultyPopup,LevelPopup, CollegePopup, BackCandidateCoursePopup;
    private SearchComponent Finder = new SearchComponent( this );
}