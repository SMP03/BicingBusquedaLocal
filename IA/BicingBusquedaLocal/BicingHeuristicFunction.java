package IA.BicingBusquedaLocal;

/**
 * Created by bejar on 17/01/17.
 */

import aima.search.framework.HeuristicFunction;

public class BicingHeuristicFunction implements HeuristicFunction {

    public double getHeuristicValue(Object n){
        if(((BicingBoard) n).heuristic == ((BicingBoard) n).FIRST_CRITERION_HEURISTIC) {
            return -1 *((BicingBoard) n).first_criterion_heuristic();
        }
        else if(((BicingBoard) n).heuristic == ((BicingBoard) n).BOTH_CRITERION_HEURISTIC) {
            return -1 *((BicingBoard) n).both_criteria_heuristic();
        }
        
        return -1 *((BicingBoard) n).dynamic_criterion_heuristic();
    }
}
