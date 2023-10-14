package IA.BicingBusquedaLocal;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bejar on 17/01/17
 */
public class BicingSuccesorFunction implements SuccessorFunction{

    public List<BicingBoard> getSuccessors(Object state){
        ArrayList<BicingBoard> retval = new ArrayList<BicingBoard>();
        BicingBoard board = (BicingBoard) state;

        int max_furgo_id = board.get_n_furgos();
        int[][] moves = board.get_moves();
        int num_estacions = board.get_num_estacions();

        //add_furgo(int departure, int first_dropoff, int bikes_taken)
        for (int departure = 0; departure < num_estacions; ++departure) {
            int bicis_no_usades = board.get_bicis_no_usades(departure);
            for (int first_drop = 0; first_drop < num_estacions; ++first_drop) {
                for (int bicis = 1; bicis <= Math.min((30), bicis_no_usades); ++bicis) {
                    BicingBoard successor = new BicingBoard(moves);
                    successor.add_furgo(departure, first_drop, bicis);
                    retval.add(successor);
                }
            }
        }

        //remove_furgo
        for(int id = 0; id < max_furgo_id; ++id) {
            BicingBoard successor = new BicingBoard(moves);
            successor.remove_furgo(id);
            retval.add(successor);
        }

        


        return retval;

    }

}
