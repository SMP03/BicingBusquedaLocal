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

        //add_furgo(int departure, int first_dropoff, int bikes_taken)
        

        //remove_furgo
        for(int id = 0; id < max_furgo_id; ++id) {
            BicingBoard successor = new BicingBoard(moves);
            successor.remove_furgo(id);
            retval.add(successor);
        }

        


        return retval;

    }

}
