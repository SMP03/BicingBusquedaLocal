package IA.BicingBusquedaLocal;

import aima.search.framework.GoalTest;


public class BicingGoalTest implements GoalTest {

    public boolean isGoalState(Object state){

        return((BicingBoard) state).is_goal();
    }
}
