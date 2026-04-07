
//                          !! RAM !!

package puexamroutine.control.routinegeneration.graph.domain.interfaces;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author Sumit Shresth
 */
public interface OptimumColoredGraphInterface {

    int[] getColorArray();

    int getTotalUniqueColors();

    Integer[] getUniqueColors();

    Map<Integer, Integer> getUniqueColorsOccurrence();

    double getVariance();

    void report();

    HashMap<Integer, HashSet> getColorIndexListMap();

}
