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

import java.io.IOError;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    private static final int NUM_FURGOS = 5;
    private static final int NUM_STATIONS = 25;
    private static final int NUM_BICYCLES = 1250;
    private static final int SCENERY_TYPE = Estaciones.EQUILIBRIUM;

    public static void Usage() {
        System.out.println("java Main [{-m|-mapseed} <map_seed>] [{-i|-initseed} <init_seed>]");
        System.out.println("\t[{-r|-repetitions} <num_of_repetitions>] [{-q|-quiet}] [{--rformat|--rformat-no-tags}]");
        System.out.println("\t[{-s|-solutions}] [--operators <AddFurgo> <RemoveFurgo> <ChangeDpt> <ChangeDrop1> <ChangeDrop2> <SwapDrop>]");
        System.out.println("\t[--init-strategy <init_strat_id>] [{-h|--help}]");
        System.out.println("Description:");
        System.out.println(" -If options are not provided console input is used.");
        System.out.println(" -Else program is executed with option values or, if no option provided, default values");
    }

    public static void main(String[] args) throws Exception{
        int map_seed = 0;
        int init_seed = 0;
        Boolean random_init_seed = true;
        Boolean random_map_seed = true;
        int num_of_reps = 1;
        Boolean quiet = false;
        Boolean print_solutions = false;
        Boolean rformat = false;
        Boolean rtags = false;
        Boolean operators[] = {true, true, true, true, true, true};
        int init_strategy = BicingBoard.MIN_DIST;
        if (args.length >= 1) {
            for (int i = 0; i < args.length; ++i) {
                if (args[i].equals("-m") || args[i].equals("--mapseed")) {
                    if (i+1 == args.length) {Usage();return;};
                    map_seed = Integer.valueOf(args[i+1]);
                    random_map_seed = false;
                    ++i;
                }
                else if (args[i].equals("-i") || args[i].equals("--initseed")) {
                    if (i+1 == args.length) {Usage();return;};
                    init_seed = Integer.valueOf(args[i+1]);
                    random_init_seed = false;
                    ++i;
                }
                else if (args[i].equals("-r") || args[i].equals("--repetitions")) {
                    if (i+1 == args.length) {Usage();return;};
                    num_of_reps = Integer.valueOf(args[i+1]);
                    ++i;
                }
                else if (args[i].equals("-q") || args[i].equals("--quiet")) {
                    quiet = true;
                }
                else if (args[i].equals("-s") || args[i].equals("--solutions")) {
                    print_solutions = true;
                }
                else if (args[i].equals("--rformat")) {
                    rformat = true;
                    rtags = true;
                }
                else if (args[i].equals("--rformat-no-tags")) {
                    rformat = true;
                    rtags = false;
                }
                else if (args[i].equals("--operators")) {
                    for (int j = 0; j < 6; ++j) {
                        if (Integer.valueOf(args[i+1+j]) == 0) {
                            operators[j] = false;
                        }
                        else {
                            operators[j] = true;
                        } 
                    }
                    i+=6;
                }
                else if (args[i].equals("--init-strat")) {
                    init_strategy = Integer.valueOf(args[i+1]);
                    i+=1;
                }
                else if (args[i].equals("-h") || args[i].equals("--help")) {
                    Usage();
                    return;
                }
                else {
                    System.out.printf("Argument \"%s\" is not valid.%n", args[i]);
                    Usage();
                    return;
                }
            }
        }
        else { // Console input
            Scanner in = new Scanner(System.in);
            String answer;
            System.out.printf("Map Seed? (0=random)%n");
            map_seed = in.nextInt();
            random_map_seed = (map_seed == 0);
            System.out.printf("Init Strategy Seed? (0=random) (needed for random init_strategy)%n");
            init_seed = in.nextInt();
            random_init_seed = (init_seed == 0);
            num_of_reps = 1;
            if (random_map_seed || random_init_seed) {
                System.out.printf("Number of repetitions?%n");
                num_of_reps = in.nextInt();
            }
            System.out.printf("Quiet? (y/n)%n");
            answer = in.next();
            if (answer.equals("y") || answer.equals("Y")) {
                quiet = true;
            }
            else {
                System.out.printf("Print solutions? (y/n)%n");
                answer = in.next();
                if (answer.equals("y") || answer.equals("Y")) {
                    print_solutions = true;
                }
            }
            in.close();
        }



        ArrayList<Double> bike_profits = new ArrayList<Double>();
        ArrayList<Double> transport_costs = new ArrayList<Double>();
        ArrayList<Double> heuristics = new ArrayList<Double>();
        ArrayList<Integer> nodes_expanded = new ArrayList<Integer>();
        ArrayList<Double> total_distances = new ArrayList<Double>();
        ArrayList<Double> times = new ArrayList<Double>();

        if (rtags) {
            System.out.println("NumRep\tMapSeed\tInitStratSeed\tBikeProfit\tTransportCosts\tTotalProfit\tFinalHeuristic\tTotalDistance\tExecutionTime");
        }
        for (int i = 0; i < num_of_reps; ++i) {
            if (random_map_seed) map_seed = (int)(Math.random()*Integer.MAX_VALUE);
            if (random_init_seed) init_seed = (int)(Math.random()*Integer.MAX_VALUE);
            if (!rformat) System.out.printf("Rep#%d: MapSeed:%d InitStratSeed:%d%n", i, map_seed, init_seed);
            BicingBoard board = new BicingBoard(NUM_FURGOS, NUM_STATIONS, NUM_BICYCLES, SCENERY_TYPE, map_seed, init_strategy, init_seed);

            BicingHeuristicFunction heuristic = new BicingHeuristicFunction();

            // Create the Problem object
            Problem p = new  Problem(board,
            new BicingSuccesorFunction(quiet, rformat, operators),
            new BicingGoalTest(),
            heuristic);

            // Instantiate the search algorithm
            // Hill Climbing Search
            Search search = new HillClimbingSearch();

            // Instantiate the SearchAgent object
            long start = System.nanoTime();
            SearchAgent agent = new SearchAgent(p, search);
            long end = System.nanoTime();
            // We print the results of the search
            if (!quiet && !rformat) {
                System.out.println("Actions taken:");
                printActions(agent.getActions());
                printInstrumentation(agent.getInstrumentation());
            }
            
            BicingBoard goal = (BicingBoard)search.getGoalState();
            int[] balance = goal.get_balance();
            double bike_income = goal.get_bike_income(balance);
            double transport_cost = goal.get_transport_cost(balance);
            double heuristic_val = heuristic.getHeuristicValue(goal);
            double total_distance = goal.get_total_dist();
            double time = (end-start)/1000000.0;
            if (!quiet) {
                if (!rformat) {
                    if (print_solutions) {
                        System.out.println("Solution:");
                        goal.print_state();
                    }
                    System.out.printf("Final metrics:Bike profits:%15.2f | Transport costs:%15.2f | Total:%15.2f | AlgHeuristic:%15.2f | Total Distance:%15.2f | Execution Time:%15.2f%n",
                    bike_income, transport_cost, (bike_income+transport_cost), heuristic_val, total_distance, time);
                    System.out.println("===============================================================================================");
                }
                else {
                    System.out.printf("%d\t%d\t%d\t%f\t%f\t%f\t%f\t%f\t%f%n", i, map_seed, init_seed, bike_income, transport_cost, (bike_income+transport_cost), heuristic_val, total_distance, time);
                } 
            }
            if (!rformat) {
                bike_profits.add(bike_income);
                transport_costs.add(transport_cost);
                heuristics.add(heuristic_val);
                nodes_expanded.add(Integer.valueOf(agent.getInstrumentation().getProperty("nodesExpanded")));
                total_distances.add(total_distance);
                times.add(time);
            }
        }

        if (num_of_reps > 1 && !rformat) {
            System.out.printf("Experiments summary: (%d experiments)%n", num_of_reps);
            System.out.println("Bike Profits:");
            double max_bike_profits = Double.MIN_VALUE;
            double min_bike_profits = Double.MAX_VALUE;
            double mean_bike_profits = 0.0;
            for (int i = 0; i < num_of_reps; ++i) {
                Double val = bike_profits.get(i);
                if (val > max_bike_profits) max_bike_profits = val;
                if (val < min_bike_profits) min_bike_profits = val;
                mean_bike_profits += val;
            }
            mean_bike_profits /= num_of_reps;
            System.out.printf("  MAX:%15.2f%n", max_bike_profits);
            System.out.printf("  MEAN:%14.2f%n", mean_bike_profits);
            System.out.printf("  MIN:%15.2f%n", min_bike_profits);
            
            System.out.println("Transport costs:");
            double max_transport_costs = Double.MIN_VALUE;
            double min_transport_costs = Double.MAX_VALUE;
            double mean_transport_costs = 0.0;
            for (int i = 0; i < num_of_reps; ++i) {
                Double val = transport_costs.get(i);
                if (val > max_transport_costs) max_transport_costs = val;
                if (val < min_transport_costs) min_transport_costs = val;
                mean_transport_costs += val;
            }
            mean_transport_costs /= num_of_reps;
            System.out.printf("  MAX:%15.2f%n", max_transport_costs);
            System.out.printf("  MEAN:%14.2f%n", mean_transport_costs);
            System.out.printf("  MIN:%15.2f%n", min_transport_costs);

            System.out.println("Total benefits:");
            double max_benefits = Double.MIN_VALUE;
            double min_benefits = Double.MAX_VALUE;
            double mean_benefits = 0.0;
            for (int i = 0; i < num_of_reps; ++i) {
                Double val = bike_profits.get(i) + transport_costs.get(i);
                if (val > max_benefits) max_benefits = val;
                if (val < min_benefits) min_benefits = val;
                mean_benefits += val;
            }
            mean_benefits /= num_of_reps;
            System.out.printf("  MAX:%15.2f%n", max_benefits);
            System.out.printf("  MEAN:%14.2f%n", mean_benefits);
            System.out.printf("  MIN:%15.2f%n", min_benefits);

            System.out.println("Heuristic:");
            double max_heuristic = Double.MIN_VALUE;
            double min_heuristic = Double.MAX_VALUE;
            double mean_heuristic = 0.0;
            for (int i = 0; i < num_of_reps; ++i) {
                Double val = heuristics.get(i);
                if (val > max_heuristic) max_heuristic = val;
                if (val < min_heuristic) min_heuristic = val;
                mean_heuristic += val;
            }
            mean_heuristic /= num_of_reps;
            System.out.printf("  MAX:%15.2f%n", max_heuristic);
            System.out.printf("  MEAN:%14.2f%n", mean_heuristic);
            System.out.printf("  MIN:%15.2f%n", min_heuristic);

            System.out.println("Nodes expanded:");
            int max_nodes_expanded = Integer.MIN_VALUE;
            int min_nodes_expanded = Integer.MAX_VALUE;
            double mean_nodes_expanded = 0;
            for (int i = 0; i < num_of_reps; ++i) {
                int val = nodes_expanded.get(i);
                if (val > max_nodes_expanded) max_nodes_expanded = val;
                if (val < min_nodes_expanded) min_nodes_expanded = val;
                mean_nodes_expanded += val;
            }
            mean_nodes_expanded = mean_nodes_expanded / num_of_reps;
            System.out.printf("  MAX:%15d%n", max_nodes_expanded);
            System.out.printf("  MEAN:%14.2f%n", mean_nodes_expanded);
            System.out.printf("  MIN:%15d%n", min_nodes_expanded);

            System.out.println("Total Distance:");
            double max_total_distance = Double.MIN_VALUE;
            double min_total_distance = Double.MAX_VALUE;
            double mean_total_distance = 0.0;
            for (int i = 0; i < num_of_reps; ++i) {
                Double val = total_distances.get(i);
                if (val > max_total_distance) max_total_distance = val;
                if (val < min_total_distance) min_total_distance = val;
                mean_total_distance += val;
            }
            mean_total_distance /= num_of_reps;
            System.out.printf("  MAX:%15.2f%n", max_total_distance);
            System.out.printf("  MEAN:%14.2f%n", mean_total_distance);
            System.out.printf("  MIN:%15.2f%n", min_total_distance);

            System.out.println("Exec Time:");
            double max_time = Double.MIN_VALUE;
            double min_time = Double.MAX_VALUE;
            double mean_time = 0.0;
            for (int i = 0; i < num_of_reps; ++i) {
                Double val = times.get(i);
                if (val > max_time) max_time = val;
                if (val < min_time) min_time = val;
                mean_time += val;
            }
            mean_time /= num_of_reps;
            System.out.printf("  MAX:%15.2f%n", max_time);
            System.out.printf("  MEAN:%14.2f%n", mean_time);
            System.out.printf("  MIN:%15.2f%n", min_time);

        }
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
