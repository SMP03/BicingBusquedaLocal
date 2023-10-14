package IA.BicingBusquedaLocal;

/**
 * Created by bejar on 17/01/17.
 */

import aima.search.framework.HeuristicFunction;

public class BicingHeuristicFunction implements HeuristicFunction {

    public double getHeuristicValue(Object n){

        return ((BicingBoard) n).heuristic();
    }
}
