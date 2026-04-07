
//                          !! RAM !!

package puexamroutine.control.domain.list;
import puexamroutine.control.domain.*;

/**
 *
 * @author Sumit Shresth
 */
public class GroupList implements java.io.Serializable{
    
    public void addCourses( puexamroutine.control.domain.Group g, puexamroutine.control.domain.list.GroupCourseList clist ){
        this.GroupsCourses.put(g, clist);
        if( ! this.GroupsPrograms.containsKey(g)){
            this.GroupsPrograms.put(g, null );
        }
    }
    
    public void addProgram( puexamroutine.control.domain.Group g, puexamroutine.control.domain.list.ProgramList prgs ){    
        this.GroupsPrograms.put(g, prgs);
        if( ! this.GroupsCourses.containsKey(g) ){
            this.GroupsCourses.put(g, null );
        }
    }
        
    public boolean addPrograms( java.util.Collection< puexamroutine.control.domain.Group > G, java.util.Collection<puexamroutine.control.domain.list.ProgramList> prgs ){
        if( G.size() != prgs.size() )
            return false;
        
        java.util.Iterator<puexamroutine.control.domain.Group> groupItr = G.iterator();
        java.util.Iterator<puexamroutine.control.domain.list.ProgramList> prgsItr= prgs.iterator();
        while( groupItr.hasNext() ){
            this.addProgram( groupItr.next(), prgsItr.next() );
        }
        
        return true;
    }
    
    public boolean addCourses( java.util.Collection< puexamroutine.control.domain.Group > G, java.util.Collection<puexamroutine.control.domain.list.GroupCourseList> prgs ){
        if( G.size() != prgs.size() )
            return false;
        
        java.util.Iterator<puexamroutine.control.domain.Group> groupItr = G.iterator();
        java.util.Iterator<puexamroutine.control.domain.list.GroupCourseList> prgsItr= prgs.iterator();
        while( groupItr.hasNext() ){
            this.addCourses( groupItr.next(), prgsItr.next() );
        }
        
        return true;
    }
    
    /**
     * This method sets the course list to be conducted.
     * 
     * @param CourseTableMap the map of course code --> course code name (eg. cmp-122.1 --> multimedia)
     * 
     * @return true if set successfully else false
     */
    public boolean setCourses( java.util.HashMap< String, String > CourseTableMap ) throws Exception{        
        try{
            java.util.Iterator<puexamroutine.control.domain.Group> grpItr = this.getGroups().iterator();
            while( grpItr.hasNext() ){
                Group grp = grpItr.next();
                java.util.Iterator<CourseCode> cItr = this.getCourses( grp ).iterator();
                while( cItr.hasNext() ){
                    CourseCode c = cItr.next();
                    Course ctemp = new Course( c, grp, CourseTableMap.get(c.toString()));
                    this.Courses.addCourse(ctemp);
                }
            }
            return true;
        }
        catch( Exception e ){
            return false;
        }
    }

    public java.util.HashMap< puexamroutine.control.domain.Group, puexamroutine.control.domain.list.ProgramList > getGroupAndPrograms(){
        return this.GroupsPrograms;
    }
    
    public java.util.Collection<puexamroutine.control.domain.Group> getGroups(){
        return this.GroupsPrograms.keySet();
    }    
    
    public int getTotalGroups(){
        return this.getGroups().size();
    }
    
    public java.util.Collection<puexamroutine.control.domain.list.ProgramList> getProgramsLists(){
        return this.GroupsPrograms.values();
    }
    
    public boolean exists(puexamroutine.control.domain.Group g ){
        return this.getGroups().contains(g) || this.checkStrictly(g);
    }
    
    public puexamroutine.control.domain.list.GroupCourseList getCourseList( puexamroutine.control.domain.Group g ){
        return this.GroupsCourses.get(g);
    }
    
    public puexamroutine.control.domain.CourseCode[] getRegularCoursesArray( puexamroutine.control.domain.Group g ){
        return this.GroupsCourses.get(g).getRegularCoursesArray();
    }
    
    public puexamroutine.control.domain.list.ProgramList getPrograms(puexamroutine.control.domain.Group g ){
        return this.GroupsPrograms.get(g);
    }
    
    private boolean checkStrictly( puexamroutine.control.domain.Group g ){
        java.util.Iterator<puexamroutine.control.domain.Group> grpItr = this.getGroups().iterator();
        while( grpItr.hasNext() ){
            if( grpItr.next().equals(g) )
                return true;
        }
        return false;
    }
    
    /**
     * This method gives the list of all set of semesters for this group.
     * Each semesrer set will be interconnected in the graph.
     * 
     * @param grp The specified group whose semesters have to be obtained
     * @return the list of all set of semesters for this group
     */
    public java.util.HashSet<puexamroutine.control.domain.Semester> getExamSemester( puexamroutine.control.domain.Group grp ){
        java.util.HashSet<puexamroutine.control.domain.Semester> Sems = new java.util.HashSet<puexamroutine.control.domain.Semester>();
        java.util.Iterator<puexamroutine.control.domain.Program> PrgsItr = this.GroupsPrograms.get( grp ).getPrograms().iterator();
        while( PrgsItr.hasNext() ){
            puexamroutine.control.domain.Program prg = PrgsItr.next();
            Sems.addAll( prg.getSemesters() );
        }
        return Sems;
    }
    
    /**
     * This method is used to obtain the list of all regualar courses for the specified group
     * Each element withing the list will have a set of regular course for certain colleges semester
     * So, Each such regualar courses must be taken without overlapping which is one constraint.
     * 
     * @param grp specified group 
     * @return the list of all regualar courses for the specified group
     */
    public java.util.HashSet<puexamroutine.control.domain.RegularCourses> getRegularCourses( puexamroutine.control.domain.Group grp ){
        return this.GroupsPrograms.get(grp).getRegularCourses();
    }
    
    /**
     * This method gives the list of all courses for specified group
     * 
     * @param grp specified group of which courses has to be obtained
     * @return collection of courses in specified group
     */
    public java.util.Collection<puexamroutine.control.domain.CourseCode> getCourses( puexamroutine.control.domain.Group grp ){
        return this.getCourseList(grp).getCourses();
    }
    
    public puexamroutine.control.domain.list.CourseList getCourses_Code_Name_Mapping(){
        return this.Courses;
    }
    
    public java.util.Map<CourseCode,String> getCoursesMap( puexamroutine.control.domain.Group grp ){
        return this.Courses.getCourses(grp);
    }
    
    /**
     * This method gives the sum of the regular candidates for the specified group.
     * 
     * @param grp specified group whose candidates sum has to be found
     * @return sum of the regular candidates for the specified group.
     */
    public int getTotalCandidates( puexamroutine.control.domain.Group grp ){
        if( ! this.GroupsCandidates.containsKey(grp) )
            this.calculateTotalCandidates(grp);
        return this.GroupsCandidates.get(grp);
    }
    
    private void calculateTotalCandidates( puexamroutine.control.domain.Group grp){        
        this.GroupsCandidates.put(grp, this.GroupsPrograms.get(grp).getTotalCandidates() );
    }
    
    public puexamroutine.control.domain.Group[] getSortedGroupForTotalCandidates(){
        if(this.SortedGroup == null ) this.sortGroupByTotalCandidates();
        return this.SortedGroup;
    }
    
    private void sortGroupByTotalCandidates(){
        SortedGroup = this.GroupsCourses.keySet().toArray( new puexamroutine.control.domain.Group[]{} );
        java.util.Comparator<puexamroutine.control.domain.Group> c = new java.util.Comparator() {
            /**
             * This method will be used to compare the groups to be sorted.
             * Since, The list is to be sorted in descending order wrt their students so it will be opposite method.
             */
            @Override
            public int compare(Object o1, Object o2) {                
                int net1 = getTotalCandidates( (puexamroutine.control.domain.Group)o1 );
                int net2 = getTotalCandidates( (puexamroutine.control.domain.Group)o2 );
                if( net1 > net2 ){
                    return -1;
                }
                else
                    if( net1 < net2 ){
                        return 1;
                    }
                    else
                        return 0;
            }
        };        
        java.util.Arrays.sort( SortedGroup, c);        
    }
    
    public java.util.HashSet< puexamroutine.control.domain.CentreIdentifier > getCentres(  puexamroutine.control.domain.Group grp ){
        return this.getPrograms(grp).getCentres();
    }
    
    public puexamroutine.control.domain.Group getGroup( puexamroutine.control.domain.CourseCode c ) throws Exception{
        java.util.Iterator<puexamroutine.control.domain.Group> grpitr = this.getGroups().iterator();
        while( grpitr.hasNext() ){
            puexamroutine.control.domain.Group grp = grpitr.next();
            if( this.GroupsCourses.get(grp).getCourses().contains( c ) ){
                return grp;
            }
        }
        throw new Exception("group not found for course " + c.toString() );
    }
    
    public puexamroutine.control.domain.Group getGroup( final String fac, final String level, final String disc ) throws Exception{
        java.util.Iterator<puexamroutine.control.domain.Group> grpitr = this.getGroups().iterator();
        while( grpitr.hasNext() ){
            puexamroutine.control.domain.Group grp = grpitr.next();
            if( grp.getFaculty().equals( fac ) && grp.getLevel().equals( level ) && grp.getDiscipline().equals(disc) ){
                return grp;
            }
        }
        throw new Exception("specified group not found" );
    }
    
    public java.util.Collection<puexamroutine.control.domain.CourseCode> getBackPapers( puexamroutine.control.domain.Group grp ){
        return this.GroupsCourses.get(grp).getBackCourses();
    }
    
    public java.util.Collection<puexamroutine.control.domain.interfaces.CandidateInterface> getBackCandidates( puexamroutine.control.domain.Group grp ){
        return this.getPrograms(grp).getBackCandidates();
    }
    
    public void removeAll(){
        this.GroupsCandidates.clear();
        this.GroupsCourses.clear();
        this.GroupsPrograms.clear();        
        this.Courses.clear();
        this.SortedGroup = null;
    }
    
    private java.util.HashMap< Group, puexamroutine.control.domain.list.ProgramList > GroupsPrograms = new java.util.HashMap< puexamroutine.control.domain.Group, puexamroutine.control.domain.list.ProgramList >();
    private java.util.HashMap< puexamroutine.control.domain.Group, puexamroutine.control.domain.list.GroupCourseList  > GroupsCourses = new java.util.HashMap< puexamroutine.control.domain.Group, puexamroutine.control.domain.list.GroupCourseList  >();
    private java.util.HashMap< puexamroutine.control.domain.Group, java.lang.Integer > GroupsCandidates = new java.util.HashMap<puexamroutine.control.domain.Group, java.lang.Integer >();
    private puexamroutine.control.domain.list.CourseList Courses = new puexamroutine.control.domain.list.CourseList();
    private puexamroutine.control.domain.Group[] SortedGroup=null;
}