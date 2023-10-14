import IA.BicingBusquedaLocal.Board;
import IA.BicingBusquedaLocal.GoalTest;
import IA.BicingBusquedaLocal.HeuristicFunction;
import IA.BicingBusquedaLocal.SuccesorFunction;
import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.AStarSearch;
import aima.search.informed.IterativeDeepeningAStarSearch;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws Exception{
        /**
         *  For a problem to be solvable:
         *    count(0,prob) % 2 == count(0,sol) %2
         */
        int [] prob = new int []{1 ,0, 1, 1, 0};
        int [] sol = new int[]{1, 1, 0, 1, 0};

        ProbIA5Board board = new ProbIA5Board(prob, sol );

        // Create the Problem object
        Problem p = new  Problem(board,
                                new SuccesorFunction(),
                                new GoalTest(),
                                new HeuristicFunction());

        // Instantiate the search algorithm
	// AStarSearch(new GraphSearch()) or IterativeDeepeningAStarSearch()
        Search alg = new AStarSearch(new GraphSearch());

        // Instantiate the SearchAgent object
        SearchAgent agent = new SearchAgent(p, alg);

	// We print the results of the search
        System.out.println();
        printActions(agent.getActions());
        printInstrumentation(agent.getInstrumentation());

        // You can access also to the goal state using the
	// method getGoalState of class Search

    }

        private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }
        
    }
    
    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }
    
}
