package IA.BicingBusquedaLocal;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bejar on 17/01/17
 */
public class BicingSuccesorFunction implements SuccessorFunction{

    public List getSuccessors(Object state){
        ArrayList retval = new ArrayList();
        BicingBoard board = (BicingBoard) state;

        int max_furgo_id = board.get_n_furgos();
        int[][] moves = board.get_moves();
        int num_estacions = board.get_num_estacions();

        //add_furgo(int departure, int first_dropoff, int bikes_taken)
        if (moves.length < max_furgo_id) {

            for (int departure = 0; departure < num_estacions; ++departure) {
                if (board.is_free_departure(departure)) {
                    int bicis_no_usades = board.get_bicis_no_usades(departure);
                    for (int first_drop = 0; first_drop < num_estacions; ++first_drop) {
                        if (departure != first_drop) {
                            for (int bicis = 1; bicis <= Math.min((30), bicis_no_usades); ++bicis) {

                                BicingBoard successor = new BicingBoard(moves);
                                successor.add_furgo(departure, first_drop, bicis);
                                String S=new String(BicingBoard.ADD_FURGO);
                                retval.add(new Successor(S,successor));
                            }
                        }
                    }
                }
            }
        }

        //remove_furgo
        for(int id = 0; id < max_furgo_id; ++id) {
            BicingBoard successor = new BicingBoard(moves);
            successor.remove_furgo(id);
            String S=new String(BicingBoard.REMOVE_FURGO);
            retval.add(new Successor(S,successor));
        }

        


        return retval;

    }

}
