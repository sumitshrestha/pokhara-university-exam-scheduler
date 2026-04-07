
//                              !! RAM !!

package puexamroutine.control.routinegeneration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import puexamroutine.control.domain.CourseCode;
import puexamroutine.control.domain.interfaces.DependentCourses;
import puexamroutine.control.routinegeneration.graph.domain.KeyedGraph;

/**
 * This class is for creating an graph for coloring courses.
 * This will be used to create as many graph as many graph as needed.
 * 
 * @author Sumit Shresth
 */
public class ExamGraphFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExamGraphFactory.class);
    
    private boolean DEBUG = false;
    
    public ExamGraphFactory( ){                
    }
    
    /**
     * This method is used to obtain graph from the set of related regular courses
     * 
     * @param RegCourseList The list of all related regular coures from which graph has to be obtained
     * @param RegCourseSetList The list of all related regular courses set where each set has courses that are regular
     * @return graph from specified courses
     */
    public puexamroutine.control.routinegeneration.graph.domain.interfaces.KeyedAdjacancyMatrix getGraph( java.util.Collection<CourseCode> RegCourseList, java.util.Collection<DependentCourses> RegCourseSetList ){        
        KeyedGraph Graph = createGraph(RegCourseList);        
        connectGraph(RegCourseSetList, Graph);        
        if(this.DEBUG) Graph.report();
        /*try{System.out.println("This is test for connection...");
        for( int i=0;i<Graph.getSize(); i++ ){
            if( Graph.get(i, Graph.getIndex("MGT-302.3")) ){
                System.out.println( Graph.getKey(i) +" is connected");
            }
            else
                System.out.println( Graph.getKey(i) +" is not connected");
        } }catch(Exception e ){System.err.println(e.getMessage());};*/
        return Graph;
    }

    /**
     * This method connects the regular courses in the graph
     * 
     * @param Graph the graph to be connected
     * @param Reg The regular courses to be conencted
     */
    private void conenctGraph(KeyedGraph Graph, DependentCourses DependentCourses) {
        puexamroutine.control.domain.CourseCode[] RegCourses = DependentCourses.getDependentCourses().toArray(new puexamroutine.control.domain.CourseCode[]{});
        interconnectCourses(RegCourses, Graph);
    }

    /**
     * This method is used to connect the edges of created graph
     * 
     * @param RegCourseSetList list of set of regular courses
     * @param Graph the graph to be connected
     */
    private void connectGraph(java.util.Collection<DependentCourses> DependentCourseList, KeyedGraph Graph) {
        java.util.Iterator<DependentCourses> DependentCourseItr = DependentCourseList.iterator();
        while ( DependentCourseItr.hasNext()) {
            DependentCourses Dep = DependentCourseItr.next();
            if(this.DEBUG)print(Dep);
            conenctGraph( Graph, Dep );
        }
    }

    /**
     * This method creates a graph having non unconnected vertices
     * 
     * @param RegCourseList list of all regular courses whose graph has to be created
     * @return graph that has been created
     */
    private KeyedGraph createGraph( java.util.Collection<CourseCode> RegCourseList) {
        puexamroutine.control.routinegeneration.graph.domain.KeyedGraph Graph = new puexamroutine.control.routinegeneration.graph.domain.KeyedGraph(RegCourseList.size());
        String[] Courses = getCourseArray(RegCourseList);
        if(this.DEBUG)this.print(Courses);
        if(this.DEBUG)LOGGER.debug("The size of graph is {}", Courses.length);
        Graph.setKeys(Courses);        
        return Graph;
    }

    private String[] getCourseArray( java.util.Collection<CourseCode> RegCourseList) {
        String[] Courses = new String[RegCourseList.size()];
        java.util.Iterator<puexamroutine.control.domain.CourseCode> RegCourseItr = RegCourseList.iterator();
        int i = 0;
        while (RegCourseItr.hasNext()) {
            Courses[i++] = RegCourseItr.next().getCourseCode();
        }
        return Courses;
    }

    /**
     * This method interconnects the courses in the graph
     * 
     * @param RegCourses array of all courses to be interconnect
     * @param Graph the graph to be interconnect
     */
    private void interconnectCourses(CourseCode[] RegCourses, KeyedGraph Graph) {
        //boolean temp =false;
        for (int i = 0; i < RegCourses.length; i++) {
            for (int j = i + 1; j < RegCourses.length; j++) {
                /*if( RegCourses[i].toString().equals("MGT-302.3") || RegCourses[i].toString().equals("MGT-302.3") ){
                    temp = true;
                    System.out.println("Here The required is connecting to "+ RegCourses[i]+" and "+RegCourses[j]);
                }*/
                Graph.set(RegCourses[i].getCourseCode(), RegCourses[j].getCourseCode(), true);                    
                /*if( temp){
                    System.out.println("This is reqd index "+Graph.getIndex( "MGT-302.3" ));
                    if( !Graph.get( Graph.getIndex( RegCourses[i].getCourseCode() ), Graph.getIndex( RegCourses[j].getCourseCode()) ))
                        System.err.println(RegCourses[i].getCourseCode() +" and "+ RegCourses[j].getCourseCode() + " are not connected...");                
                else
                    System.out.println("This is the index "+Graph.getIndex( RegCourses[i].getCourseCode())+" and " +Graph.getIndex( RegCourses[j].getCourseCode()) );                
                }*/
            }
        }
    }
    
    /**
     * This method is used to create graph for pure back papers of any group.
     * It is analogy of getGraph for regular subjects.
     * Unlike for regular subjects, here only non regular subjects are taken into account
     * It creates graph having vertices of non regualar subjects only.
     * Each candidates here is analogy of regular subjects as back subjects for candidates is depended on him/her.
     * Different candidates from same college with same semester may have different set of pure back papers.
     * They may only overlap in regular subjects that are neglected here since they are scheduled as regular subjects.
     * So, Each back candidate may be unique and has to be treated differently.
     * 
     * @param BkPapers specified list of back papers to be conducted
     * @param BkCandidates specified list of back candidates with non zero pure back papers
     * 
     * @return graph representing specified back subjects
     */
    /**public puexamroutine.control.graph.domain.interfaces.KeyedAdjacancyMatrix getGraph( java.util.Collection<puexamroutine.control.domain.CourseCode> BkPapers, java.util.Collection<puexamroutine.control.domain.Candidate> BkCandidates ){
        puexamroutine.control.graph.domain.KeyedGraph BkGraph = this.createGraph(BkPapers);
        
        return null;
    }*/
    
    
    private void print( String[] c ){
        StringBuilder sb = new StringBuilder();
        for( int i=0; i<c.length; i++ ){
            sb.append(" ").append(i + 1).append("th").append(c[i]);
        }
        LOGGER.debug("{}", sb);
    }
    
    private void print( DependentCourses dep ){
        try{
        java.util.Iterator<CourseCode> c = dep.getDependentCourses().iterator();
        StringBuilder sb = new StringBuilder();
        while(c.hasNext()){
            CourseCode ct = c.next();
            if(ct == null)
                sb.append(" ct is null in printing dep...");
            else
                sb.append(ct.toString()).append(" ,");
        }
        LOGGER.debug("{}", sb);
        }
        catch( Exception e ){
            LOGGER.error("error while printing dep..", e);
        }
    }
        
}