package IA.BicingBusquedaLocal;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bejar on 17/01/17
 */
public class BicingSuccesorFunction implements SuccessorFunction{
    private boolean quiet;
    
    public BicingSuccesorFunction() {
        quiet = false;
    }

    public BicingSuccesorFunction(Boolean quiet) {
        this.quiet = quiet;
    }

    public List getSuccessors(Object state){
        ArrayList retval = new ArrayList();
        BicingBoard board = (BicingBoard) state;

        int max_furgo_id = board.get_n_furgos();
        int[][] moves = board.get_moves();
        int num_estacions = board.get_num_estacions();
        
        //add_furgo(int departure, int first_dropoff, int bikes_taken)
        if (max_furgo_id < BicingBoard.get_max_furgos()) {
            for (int departure = 0; departure < num_estacions; ++departure) {
                if (board.is_free_departure(departure)) {
                    int bicis_no_usades = board.get_bicis_no_usades(departure);
                    for (int first_drop = 0; first_drop < num_estacions; ++first_drop) {
                        if (departure != first_drop) {
                            for (int bicis = 1; bicis <= Math.min((30), bicis_no_usades); ++bicis) {

                                BicingBoard successor = board.add_furgo(departure, first_drop, bicis);
                                String S=new String(BicingBoard.ADD_FURGO + ":\tdep:" + departure + "\tfd:" + first_drop + "\tbicis:" + bicis);
                                retval.add(new Successor(S,successor));
                            }
                        }
                    }
                }
            }
        }
        
        //remove_furgo
        for(int id = 0; id < max_furgo_id; ++id) {
            BicingBoard successor = board.remove_furgo(id);
            String S=new String(BicingBoard.REMOVE_FURGO + ":\tfid:" + id);
            retval.add(new Successor(S,successor));
        }
        
        //change_departure
        for (int furgo_id = 0; furgo_id < max_furgo_id; ++furgo_id) {
            for (int station_id = 0; station_id < num_estacions; ++station_id) {
                if (board.is_free_departure(station_id) && (board.get_first_dropoff(furgo_id) != station_id) && (board.get_second_dropoff(furgo_id)!=station_id)) {
                    int bicis_no_usades = board.get_bicis_no_usades(station_id);
                    for (int num_bikes = 1; num_bikes <= Math.min((30), bicis_no_usades); ++num_bikes) {
                        BicingBoard successor = new BicingBoard(board);
                        successor.change_departure(furgo_id, station_id, num_bikes);
                        String S = new String(BicingBoard.CHANGE_DEPARTURE + ":\tdep:" + station_id + "\tbicis:" + num_bikes);
                        retval.add(new Successor(S,successor));
                    }
                }
            }
        }
        
        //change_first_dropoff
        for (int furgo_id = 0; furgo_id < max_furgo_id; ++furgo_id) {
            int bikes_taken = board.get_bikes_taken(furgo_id);
            for (int station_id = 0; station_id < num_estacions; ++station_id) {
                if (board.get_departure(furgo_id) != station_id && board.get_second_dropoff(furgo_id) != station_id) {
                    for (int bikes_dropped = 0; bikes_dropped <= bikes_taken; ++bikes_dropped) {
                        BicingBoard successor = new BicingBoard(board);
                        successor.change_first_dropoff(furgo_id, station_id, bikes_dropped);
                        String S = new String(BicingBoard.CHANGE_FIRST_DROPOFF + ":\tfd:" + station_id + "\tbdrop:" + bikes_dropped);
                        retval.add(new Successor(S,successor));
                    }
                }
            }
        }

        //change_second_dropoff
        for (int furgo_id = 0; furgo_id < max_furgo_id; ++furgo_id) {
            int bikes_taken = board.get_bikes_taken(furgo_id);
            for (int station_id = 0; station_id < num_estacions; ++station_id) {
                if (board.get_departure(furgo_id) != station_id && board.get_first_dropoff(furgo_id) != station_id) {
                    for (int bikes_dropped = 0; bikes_dropped <= bikes_taken; ++bikes_dropped) {
                        BicingBoard successor = new BicingBoard(board);
                        successor.change_second_dropoff(furgo_id, station_id, bikes_dropped);
                        String S = new String(BicingBoard.CHANGE_SECOND_DROPOFF + ":\tsd:" + station_id + "\tbdrop:" + bikes_dropped);
                        retval.add(new Successor(S,successor));
                    }
                }
            }
        }
        /*
        double prev = board.first_criterion_heuristic();
        for (int i = 0; i < retval.size(); ++i) {
            Successor s = ((Successor)retval.get(i));
            String action = s.getAction();
            BicingBoard bstate = (BicingBoard)s.getState();
            System.out.println(action + "\th:" + (bstate.first_criterion_heuristic() - prev));
        }
        System.out.println("Num furgos prev:" + board.get_n_furgos());
        System.out.println("=================================");
        */
        if (!quiet) {
            int[] balance = board.get_balance();
            double bike_income = board.get_bike_income(balance);
            double transport_costs = board.get_transport_cost(balance);
            BicingHeuristicFunction heuristic = new BicingHeuristicFunction();
            double heuristic_value = heuristic.getHeuristicValue(board);
            String debug_out = String.format("Step metrics: Bike profits:%15.2f | Transport costs:%15.2f | Total:%15.2f | AlgHeuristic:%15.2f",
                bike_income, transport_costs, (bike_income+transport_costs), heuristic_value);
            System.out.println(debug_out);
        }

        return retval;

    }

}
