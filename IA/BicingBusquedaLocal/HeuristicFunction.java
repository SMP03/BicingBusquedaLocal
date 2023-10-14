package IA.BicingBusquedaLocal;

/**
 * Created by bejar on 17/01/17.
 */

import aima.search.framework.HeuristicFunction;

public class HeuristicFunction implements HeuristicFunction {

    public double getHeuristicValue(Object n){

        return ((ProbIA5Board) n).heuristic();
    }
}
