
//                          !! RAM !!

package puexamroutine.control.routinegeneration.graph.domain.interfaces;

/**
 * This interface is used by the Graph colorer to check if the graph colored is feasible and optimal.
 * Since, The graph colorer can color the graph is number of ways there exists several ways of finding the subgroup that satisfies the basic constraint of the graph coloring.
 * But, To get a better answer or to choose a better one among the different values, This interface is used by graph.
 * Ths implementer of this interface must adequetly define the functions. 
 * Since for the relatively large number of graph the number of results obtained is very large, the decision function must be as efficient as possible.
 * It is sort of greedy method procedure.
 * The graph colorer obtains a possible values.
 * Only possible values are not important. There are some more levels of acceptance.
 * The first is FEASIBILITY.
 * Feasiblity means that the subset obtained must be feasible for the given problem at hand. It may be possible but not feasible. so, This function Feasublie must check for feasiblily.
 * 
 * The next is Optimality.
 * Optimality means that subset is optimal. there is only one optimal value.
 *
 * @author Sumit Shresth
 */
public interface GraphColoringSolutionAnalyzer {
    
    /**
     * The method will check feasiblity of the specified coloring of graph
     * The implemeting method will be given the coloring and related information.
     * Using the domain knowledge it must check the values to obtain the feasiblity of value.
     * Since, The combination of solution generated is too large so, it must be very fast.
     * After generating a complete set of color for the graph vertices, it immediatelty calls this function.
     * if it return false, it simply generates next solution to be checked again.
     * else if true then it means subset of solution is feasible.It now checks for optimality.
     * 
     * @param OptGraph The list of colors assigned by graph colorer to be checked for feasibility
     * @return true if specified graph coloring is feasible else false.
     */
    boolean isFeasible( puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution OptGraph );
    
    /**
     * The method will check for optimality or perfectness of the graph coloring.
     * Sometimes the graph coloring is simply perfect enough to be used directly without further generating next soution.
     * Such a solution is complete optimal.
     * The implementing function must use domain knowledge to determine opimality of values.
     * The grounds on which it determaines optimality of value must be well defined.
     * Sometimes, it is preferable to simply use optimal degree to find it. 
     * It is faster and requires simply checking the value against that.
     * But, it is better only for well defined problems.
     * 
     * @param OptGraph  The list of colors assigned by graph colorer to be checked for full optimality
     * @return true if specified graph coloring is fully optimal else false.
     */
    boolean isOptimal( puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution OptGraph );
    
    /**
     * The method will check if newly generated solution is more optimal than older method.
     * Sometimes the graph coloring is not best optimal but still better than previous solution.
     * For most problems it is more optimal to find such soultion than running for the best.
     * The main reason for this is that it is far difficult to find well defined optimal function to find best one.
     * But, comparing two values and finding better one is easier.
     * The best can be find by repeatedly generating solution and comparing each other.
     * For most problems, The grounds on which the best solution is derived is also grounds for finding better solution. 
     * If a new solution is more optimal then it saves the new values as optiaml else sticks with the older value.
     * So, by the end of graph coloring there is atleast better solution if not best solution which can be used to work.
     * 
     * @param NewGraph The list of colors newly assigned by graph colorer to be checked for full optimality
     * @param OldGraph The list of colors previously assigned by graph colorer to be checked for full optimality
     * @return true if new list of graph color is more optimal else false.
     */
    boolean isMoreOptimal( puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution NewGraph, puexamroutine.control.routinegeneration.graph.domain.GraphColoringSolution OldGraph );
    
}