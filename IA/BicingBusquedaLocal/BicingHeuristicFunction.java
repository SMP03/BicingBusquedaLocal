package IA.BicingBusquedaLocal;

/**
 * Created by bejar on 17/01/17.
 */

import aima.search.framework.HeuristicFunction;

public class BicingHeuristicFunction implements HeuristicFunction {

    public double getHeuristicValue(Object n){
        return -1 *((BicingBoard) n).first_criterion_heuristic();
        //return -1 *((BicingBoard) n).both_criteria_heuristic();
        //return ((BicingBoard) n).heuristic_equilibrium();
    }
}
