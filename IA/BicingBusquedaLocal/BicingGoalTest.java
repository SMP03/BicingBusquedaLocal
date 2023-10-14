package IA.BicingBusquedaLocal;

import aima.search.framework.GoalTest;

/**
 * Created by bejar on 17/01/17.
 */
public class BicingGoalTest implements GoalTest {

    public boolean isGoalState(Object state){

        return((BicingBoard) state).is_goal();
    }
}
