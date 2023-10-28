package IA.BicingBusquedaLocal;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by bejar on 17/01/17
 */
public class BicingSuccesorFunctionSA implements SuccessorFunction{
    private boolean quiet;
    private boolean rformat;
    private boolean rtrace;
    private boolean operators[];
    PrintStream outStream;
    private static enum Op {
        AF, RF, CD, CFD, CSD, SDP
    }
    
    public BicingSuccesorFunctionSA() {
        quiet = false;
        rformat = false;
        operators = new boolean[6];
        for (int i = 0; i < operators.length; i++) {
            operators[i] = true;
        }
        outStream = System.out;
    }

    public BicingSuccesorFunctionSA(PrintStream outStream, Boolean quiet, Boolean rformat, Boolean rtrace, Boolean[] operators) {
        this.quiet = quiet;
        this.rformat = rformat;
        this.rtrace = rtrace;
        this.operators = new boolean[6];
        for (int i = 0; i < this.operators.length; i++) {
            this.operators[i] = operators[i];
        }
        this.outStream = outStream;
    }

    public List getSuccessors(Object state){
        ArrayList<Op> operations = new ArrayList<Op>();
        ArrayList<Integer[]> parameters = new ArrayList<Integer[]>();

        BicingBoard board = (BicingBoard) state;

        int max_furgo_id = board.get_n_furgos();
        int[][] moves = board.get_moves();
        int num_estacions = board.get_num_estacions();
        
        if (operators[0]) {
            //add_furgo(int departure, int first_dropoff, int bikes_taken)
            if (max_furgo_id < BicingBoard.get_max_furgos()) {
                for (int departure = 0; departure < num_estacions; ++departure) {
                    if (board.is_free_departure(departure)) {
                        int bicis_no_usades = board.get_bicis_no_usades(departure);
                        for (int first_drop = 0; first_drop < num_estacions; ++first_drop) {
                            if (departure != first_drop) {
                                for (int bicis = 1; bicis <= Math.min((30), bicis_no_usades); ++bicis) {
                                    operations.add(Op.AF);
                                    parameters.add(new Integer[]{departure, first_drop, bicis});
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (operators[1]) {
            //remove_furgo
            for(int id = 0; id < max_furgo_id; ++id) {
                operations.add(Op.RF);
                parameters.add(new Integer[]{id});
            }
        }
        if (operators[2]) {
            //change_departure
            for (int furgo_id = 0; furgo_id < max_furgo_id; ++furgo_id) {
                for (int station_id = 0; station_id < num_estacions; ++station_id) {
                    if (board.is_free_departure(station_id) && (board.get_first_dropoff(furgo_id) != station_id) && (board.get_second_dropoff(furgo_id)!=station_id)) {
                        int bicis_no_usades = board.get_bicis_no_usades(station_id);
                        for (int num_bikes = 1; num_bikes <= Math.min((30), bicis_no_usades); ++num_bikes) {
                            operations.add(Op.CD);
                            parameters.add(new Integer[]{furgo_id, station_id, num_bikes});
                        }
                    }
                }
            }
        }
        if (operators[3]) {
            //change_first_dropoff
            for (int furgo_id = 0; furgo_id < max_furgo_id; ++furgo_id) {
                int bikes_taken = board.get_bikes_taken(furgo_id);
                for (int station_id = 0; station_id < num_estacions; ++station_id) {
                    if (board.get_departure(furgo_id) != station_id && board.get_second_dropoff(furgo_id) != station_id) {
                        for (int bikes_dropped = 0; bikes_dropped <= bikes_taken; ++bikes_dropped) {
                            operations.add(Op.CFD);
                            parameters.add(new Integer[]{furgo_id, station_id, bikes_dropped});
                        }
                    }
                }
            }
        }
        if (operators[4]) {
            //change_second_dropoff
            for (int furgo_id = 0; furgo_id < max_furgo_id; ++furgo_id) {
                int bikes_taken = board.get_bikes_taken(furgo_id);
                for (int station_id = 0; station_id < num_estacions; ++station_id) {
                    if (board.get_departure(furgo_id) != station_id && board.get_first_dropoff(furgo_id) != station_id) {
                        for (int bikes_dropped = 0; bikes_dropped <= bikes_taken; ++bikes_dropped) {
                            operations.add(Op.CSD);
                            parameters.add(new Integer[]{furgo_id, station_id, bikes_dropped});
                        }
                    }
                }
            }
        }
        if (operators[5]) {
            //swap_dropoffs
            for (int furgo_id = 0; furgo_id < max_furgo_id; ++furgo_id) {
                if (board.get_second_dropoff(furgo_id) != -1) { // Has second dropoff
                    operations.add(Op.SDP);
                    parameters.add(new Integer[]{furgo_id});
                }
            }
        }

        /*
        double prev = board.first_criterion_heuristic();
        for (int i = 0; i < retval.size(); ++i) {
            Successor s = ((Successor)retval.get(i));
            String action = s.getAction();
            BicingBoard bstate = (BicingBoard)s.getState();
            outStream.println(action + "\th:" + (bstate.first_criterion_heuristic() - prev));
        }
        outStream.println("Num furgos prev:" + board.get_n_furgos());
        outStream.println("=================================");
        */
        if (!quiet && !rformat) {
            int[] balance = board.get_balance();
            double bike_income = board.get_bike_income(balance);
            double transport_costs = board.get_transport_cost(balance);
            BicingHeuristicFunction heuristic = new BicingHeuristicFunction();
            double heuristic_value = heuristic.getHeuristicValue(board);
            String debug_out = String.format("Step metrics: Bike profits:%15.2f | Transport costs:%15.2f | Total:%15.2f | AlgHeuristic:%15.2f",
                bike_income, transport_costs, (bike_income+transport_costs), heuristic_value);
            outStream.println(debug_out);
        }
        Random generator = new Random();
        ArrayList retval = new ArrayList();
        int i = generator.nextInt(operations.size());
        Op operation = operations.get(i);
        Integer[] params = parameters.get(i);
        switch (operation) {
            case AF:
            {
                BicingBoard successor = board.add_furgo(params[0], params[1], params[2]);
                String S=new String(BicingBoard.ADD_FURGO + ":\tdep:" + params[0] + "\tfd:" + params[1] + "\tbicis:" + params[2]);
                retval.add(new Successor(S,successor));
            }
                break;
            case RF:
            {
                BicingBoard successor = board.remove_furgo(params[0]);
                String S=new String(BicingBoard.REMOVE_FURGO + ":\tfid:" + params[0]);
                retval.add(new Successor(S,successor));
            }
                break;
            case CD:
            {
                BicingBoard successor = new BicingBoard(board);
                successor.change_departure(params[0], params[1], params[2]);
                String S = new String(BicingBoard.CHANGE_DEPARTURE + ":\tdep:" + params[1] + "\tbicis:" + params[2]);
                retval.add(new Successor(S,successor));
            }  
                break;
            case CFD:
            {
                BicingBoard successor = new BicingBoard(board);
                successor.change_first_dropoff(params[0], params[1], params[2]);
                String S = new String(BicingBoard.CHANGE_FIRST_DROPOFF + ":\tfd:" + params[1] + "\tbdrop:" + params[2]);
                retval.add(new Successor(S,successor));
            }
                break;
            case CSD:
            {
                BicingBoard successor = new BicingBoard(board);
                successor.change_second_dropoff(params[0], params[1], params[2]);
                String S = new String(BicingBoard.CHANGE_SECOND_DROPOFF + ":\tsd:" + params[1] + "\tbdrop:" + params[2]);
                retval.add(new Successor(S,successor));
            }
                break;
            case SDP:
            {
                BicingBoard successor = new BicingBoard(board);
                successor.swap_dropoffs(params[0]);
                String S = new String(BicingBoard.SWAP_DROPOFFS + ":\tfurgoid:" + params[0]);
                retval.add(new Successor(S,successor));

            }
                break;
        }

        if (rtrace) {
            int[] balance = board.get_balance();
            double bike_income = board.get_bike_income(balance);
            double transport_costs = board.get_transport_cost(balance);
            outStream.println((bike_income+transport_costs));
        }

        return retval;

    }

}
