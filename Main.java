import IA.Bicing.Estaciones;
import IA.BicingBusquedaLocal.BicingBoard;
import IA.BicingBusquedaLocal.BicingGoalTest;
import IA.BicingBusquedaLocal.BicingHeuristicFunction;
import IA.BicingBusquedaLocal.BicingSuccesorFunction;
import IA.BicingBusquedaLocal.BicingSuccesorFunctionSA;
import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import java.io.IOError;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static final int BIKES_PROFIT_HEURISTIC = 0;
    public static final int OVERALL_PROFIT_HEURISTIC = 1;
    public static final int DYNAMIC_PROFIT_HEURISTIC = 2;

    private static int scenery_type = Estaciones.EQUILIBRIUM;

    private PrintStream outStream = System.out;

    public Main() {
    }

    public Main(PrintStream out) {
        outStream = out;
    }

    public void Usage() {
        outStream.println("Usage: java Main");

        outStream.println("Options:");
        outStream.println("\t[{-h|--help}]: This manual is printed.");

        outStream.println("\t[{-m|-mapseed} <map_seed>]: Set map generation seed manually (default:0->is random for each repetition)");
        outStream.println("\t[{-i|-initseed} <init_seed>]: Set initial solution generation strategy seed manually (default:0->is random for each repetition)");
        outStream.println("\t[{-r|-repetitions} <num_of_repetitions>]: Number of executions to be done. 1 by default.");
        outStream.println("\t[--change-stations <num_of_stations>]: Changes the number of stations, bikes=num_stations*50, furgos=num_stations/5");

        outStream.println("\t[--operators <AddFurgo> <RemoveFurgo> <ChangeDpt> <ChangeDrop1> <ChangeDrop2> <SwapDrop>]: Select operator set (0:Exclude, 1:Include). All included by default.");
        outStream.println("\t[--init-strat <init_strat_id>]: Set init strategy (0:Random number of furgos, 1:Max num of furgos, 2:No furgos, 3:Best k routes, 4:Minimum distance). 4 by default.");
        outStream.println("\t[{-sa|--simulated-annealing} {default | <steps> <stiter> <k> <lamb>}]: Use Simulated Annealing as search algorithm.");
        outStream.println("\t[{-rh|--rush-hour}]: changes from equilibrium to rush hour");
        outStream.println("\t[{-he|--heuristic} 0: Only bikes profit, 1: bikes profit + transport cost, 2: bikes profit + dynamic transport cost]");

        outStream.println("\t[{-q|-quiet}]: Reduce amount of output information (Useful for quick execution).");
        outStream.println("\t[{--rformat|--rformat-no-tags}]: Print final data in format compatible with r import from text (no-tags removes column names).");
        outStream.println("\t[{-s|-solutions}]: Print final solution in readable format (tk=Bikes taken, av=Bikes available, dp=Bikes dropped, dm=Station demand).");
        outStream.println("\t[--rtrace-cost]: Traces the cost of the solution through the execution of the search algorithm. Compatible with r.");

        outStream.println("Description:");
        outStream.println(" -If options are not provided console input is used (LIMITED FUNCTIONALITY)");
        outStream.println(" -Else program is executed with option values or, if no option provided, default values");
    }

    public static void main(String[] args) throws Exception {
        Main e = new Main();
        e.execute(args);
    }

    public void execute(String[] args) throws Exception{
        int map_seed = 0;
        int init_seed = 0;
        Boolean random_init_seed = true;
        Boolean random_map_seed = true;
        int num_furgos = 5;
        int num_stations = 25;
        int num_bicycles = 1250;
        int num_of_reps = 1;
        Boolean quiet = false;
        Boolean print_solutions = false;
        Boolean rformat = false;
        Boolean rtrace = false;
        Boolean rtags = false;
        Boolean operators[] = {true, true, true, true, true, true};
        int init_strategy = BicingBoard.MIN_DIST;
        Boolean simulated_annealing = false;
        int steps = 2000;
        int stiter = 100;
        int k = 5;
        double lamb = 0.001;
        int heuristic_criterion = 0;

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
                else if (args[i].equals("--change-stations")) {
                    num_stations = Integer.valueOf(args[i+1]);
                    num_bicycles = num_stations*50;
                    num_furgos = num_stations/5;
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
                else if (args[i].equals("--rtrace-cost")) {
                    rtrace = true;
                    quiet = true;
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
                else if (args[i].equals("-sa") || args[i].equals("--simulated-annealing")) {
                    simulated_annealing = true;
                    if (args[i+1].equals("default")) {
                        i+=1;
                    }
                    else {
                        steps = Integer.valueOf(args[i+1]);
                        stiter = Integer.valueOf(args[i+2]);
                        k = Integer.valueOf(args[i+3]);
                        lamb = Double.valueOf(args[i+4]);
                        i+=4;
                    }
                }
                else if (args[i].equals("-rh") || args[i].equals("--rush-hour")) {
                    scenery_type = Estaciones.RUSH_HOUR;
                }
                else if (args[i].equals("-he") || args[i].equals("--heuristic")) {
                    if(i+1 == args.length) Usage();
                    heuristic_criterion = Integer.valueOf(args[i+1]);
                    i += 1;
                    if(heuristic_criterion < 0 || heuristic_criterion > 2) Usage();
                }
                else if (args[i].equals("--rtrace-cost")) {
                    Usage();
                    return;
                }
                else if (args[i].equals("-num_furgos")) {
                    if (i+1 == args.length) Usage();
                    num_furgos = Integer.valueOf(args[i+1]);
                    i+=1;
                }
                else {
                    outStream.printf("Argument \"%s\" is not valid.%n", args[i]);
                    Usage();
                    return;
                }
            }
        }
        else { // Console input
            Scanner in = new Scanner(System.in);
            String answer;
            outStream.printf("Map Seed? (0=random)%n");
            map_seed = in.nextInt();
            random_map_seed = (map_seed == 0);
            outStream.printf("Init Strategy Seed? (0=random) (needed for random init_strategy)%n");
            init_seed = in.nextInt();
            random_init_seed = (init_seed == 0);
            num_of_reps = 1;
            if (random_map_seed || random_init_seed) {
                outStream.printf("Number of repetitions?%n");
                num_of_reps = in.nextInt();
            }
            outStream.printf("Quiet? (y/n)%n");
            answer = in.next();
            if (answer.equals("y") || answer.equals("Y")) {
                quiet = true;
            }
            else {
                outStream.printf("Print solutions? (y/n)%n");
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
            outStream.println("NumRep\tMapSeed\tInitStratSeed\tBikeProfit\tTransportCosts\tTotalProfit\tFinalHeuristic\tTotalDistance\tExecutionTime\tNodesExpanded");
        }
        for (int i = 0; i < num_of_reps; ++i) {
            if (random_map_seed) map_seed = (int)(Math.random()*Integer.MAX_VALUE);
            if (random_init_seed) init_seed = (int)(Math.random()*Integer.MAX_VALUE);
            if (!rformat && !rtrace) outStream.printf("Rep#%d: MapSeed:%d InitStratSeed:%d%n", i, map_seed, init_seed);
            BicingBoard board = new BicingBoard(num_furgos, num_stations, num_bicycles, scenery_type, map_seed, init_strategy, init_seed, 
                                                heuristic_criterion);

            BicingHeuristicFunction heuristic = new BicingHeuristicFunction();

            // Create the Problem object
            Problem p;
            Search search;
            SearchAgent agent;
            long start, end;
            if (!simulated_annealing) {// Hill Climbing Search
                p = new  Problem(board,
                new BicingSuccesorFunction(outStream, quiet, rformat, rtrace, operators),
                new BicingGoalTest(),
                heuristic);
                search = new HillClimbingSearch();
                // Instantiate the SearchAgent object
                start = System.nanoTime();
                agent = new SearchAgent(p, search);
                end = System.nanoTime();
            }
            else {// Simulated Annealing Search
                p = new  Problem(board,
                new BicingSuccesorFunctionSA(outStream, quiet, rformat, rtrace, operators),
                new BicingGoalTest(),
                heuristic);
                search = new SimulatedAnnealingSearch(steps, stiter, k, lamb);
                start = System.nanoTime();
                agent = new SearchAgent(p, search);
                end = System.nanoTime();
            }
 
            // We print the results of the search
            if (!quiet && !rformat) {
                if (!simulated_annealing) {//HC
                    outStream.println("Actions taken:");
                    printActions(agent.getActions());
                    printInstrumentation(agent.getInstrumentation());
                }
            }
            
            BicingBoard goal = (BicingBoard)search.getGoalState();
            int[] balance = goal.get_balance();
            double bike_income = goal.get_bike_income(balance);
            double transport_cost = goal.get_transport_cost(balance);
            double heuristic_val = heuristic.getHeuristicValue(goal);
            double total_distance = goal.get_total_dist();
            double time = (end-start)/1000000.0;
            int num_of_nodes = Integer.valueOf(agent.getInstrumentation().getProperty("nodesExpanded"));
            if (!quiet) {
                if (!rformat) {
                    if (print_solutions) {
                        outStream.println("Solution:");
                        goal.print_state();
                    }
                    outStream.printf("Final metrics:Bike profits:%15.2f | Transport costs:%15.2f | Total:%15.2f | AlgHeuristic:%15.2f | Total Distance:%15.2f | Nodes Expanded:%10d | Execution Time:%15.2f%n",
                    bike_income, transport_cost, (bike_income+transport_cost), heuristic_val, total_distance, num_of_nodes, time);
                    outStream.println("===============================================================================================");
                }
                else {
                    outStream.printf("%d\t%d\t%d\t%f\t%f\t%f\t%f\t%f\t%f\t%d%n", i, map_seed, init_seed, bike_income, transport_cost, (bike_income+transport_cost), heuristic_val, total_distance, time, num_of_nodes);
                } 
            }
            if (!rformat) {
                bike_profits.add(bike_income);
                transport_costs.add(transport_cost);
                heuristics.add(heuristic_val);
                nodes_expanded.add(num_of_nodes);
                total_distances.add(total_distance);
                times.add(time);
            }
        }

        if (num_of_reps > 1 && !rformat) {
            outStream.printf("Experiments summary: (%d experiments)%n", num_of_reps);
            outStream.println("Bike Profits:");
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
            outStream.printf("  MAX:%15.2f%n", max_bike_profits);
            outStream.printf("  MEAN:%14.2f%n", mean_bike_profits);
            outStream.printf("  MIN:%15.2f%n", min_bike_profits);
            
            outStream.println("Transport costs:");
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
            outStream.printf("  MAX:%15.2f%n", max_transport_costs);
            outStream.printf("  MEAN:%14.2f%n", mean_transport_costs);
            outStream.printf("  MIN:%15.2f%n", min_transport_costs);

            outStream.println("Total benefits:");
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
            outStream.printf("  MAX:%15.2f%n", max_benefits);
            outStream.printf("  MEAN:%14.2f%n", mean_benefits);
            outStream.printf("  MIN:%15.2f%n", min_benefits);

            outStream.println("Heuristic:");
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
            outStream.printf("  MAX:%15.2f%n", max_heuristic);
            outStream.printf("  MEAN:%14.2f%n", mean_heuristic);
            outStream.printf("  MIN:%15.2f%n", min_heuristic);

            outStream.println("Nodes expanded:");
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
            outStream.printf("  MAX:%15d%n", max_nodes_expanded);
            outStream.printf("  MEAN:%14.2f%n", mean_nodes_expanded);
            outStream.printf("  MIN:%15d%n", min_nodes_expanded);

            outStream.println("Total Distance:");
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
            outStream.printf("  MAX:%15.2f%n", max_total_distance);
            outStream.printf("  MEAN:%14.2f%n", mean_total_distance);
            outStream.printf("  MIN:%15.2f%n", min_total_distance);

            outStream.println("Exec Time:");
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
            outStream.printf("  MAX:%15.2f%n", max_time);
            outStream.printf("  MEAN:%14.2f%n", mean_time);
            outStream.printf("  MIN:%15.2f%n", min_time);

        }
    }

    private void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            outStream.println(key + " : " + property);
        }
        
    }
    
    private void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            outStream.println(action);
        }
    }
    
}
