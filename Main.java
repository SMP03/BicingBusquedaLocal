import IA.Bicing.Estaciones;
import IA.BicingBusquedaLocal.BicingBoard;
import IA.BicingBusquedaLocal.BicingGoalTest;
import IA.BicingBusquedaLocal.BicingHeuristicFunction;
import IA.BicingBusquedaLocal.BicingSuccesorFunction;
import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class Main {
    private static final int NUM_FURGOS = 5;
    private static final int NUM_STATIONS = 25;
    private static final int NUM_BICYCLES = 1250;
    private static final int SCENERY_TYPE = Estaciones.RUSH_HOUR;
    private static final int MAP_SEED = 0;
    private static final int INIT_STRATEGY = BicingBoard.EMPTY_FURGOS;
    private static final int INIT_SEED = 4;

    public static void main(String[] args) throws Exception{
        BicingBoard board = new BicingBoard(NUM_FURGOS, NUM_STATIONS, NUM_BICYCLES, SCENERY_TYPE, MAP_SEED, INIT_STRATEGY, INIT_SEED);

        // Create the Problem object
        Problem p = new  Problem(board,
                                new BicingSuccesorFunction(),
                                new BicingGoalTest(),
                                new BicingHeuristicFunction());

        // Instantiate the search algorithm
	// Hill Climbing Search
        Search seach = new HillClimbingSearch();

        // Instantiate the SearchAgent object
        SearchAgent agent = new SearchAgent(p, seach);

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
