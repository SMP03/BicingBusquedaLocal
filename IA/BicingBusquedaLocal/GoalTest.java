package IA.BicingBusquedaLocal;

import aima.search.framework.GoalTest;

/**
 * Created by bejar on 17/01/17.
 */
public class GoalTest implements GoalTest {

    public boolean isGoalState(Object state){

        return((ProbIA5Board) state).is_goal();
    }
}
