package IABicing.BicingBusquedaLocal;

import java.util.Random;

import IA.Bicing.Estaciones;

public class Board {
    /* Class independent from AIMA classes
       - It has to implement the state of the problem and its operators
    */

    /* State data structure
     * Matrix fx5: for each truck f
     *  [f][0] departure station id
     *  [f][1] first dropoff station id
     *  [f][2] second dropoff station id (-1 if none selected)
     *  [f][3] bikes taken
     *  [f][4] bikes dropped at first station (ignored if only one dropoff is selected)
    */
    private static final int DEPARTURE = 0;
    private static final int FIRST_DROPOFF = 1;
    private static final int SECOND_DROPOFF = 2;
    private static final int BIKES_TAKEN = 3;
    private static final int BIKES_DROPPED = 4;

    /* Constants to enable use of different initial states algorithms */
    public static final int RANDOM_NUM_FURGOS = 0;
    public static final int MAX_NUM_FURGOS = 1;

    private int [][] moves;
    private static Estaciones map;
    private static int max_furgos;


    /* Constructor */
    public Board(int num_furgos, int nest, int nbic, int dem, int map_seed, int init_strategy, int init_seed) {
        map = new Estaciones(nest, nbic, dem, map_seed);
        max_furgos = num_furgos;
        switch (init_strategy) {
            case RANDOM_NUM_FURGOS:
                init_random_num_furgos(init_seed);
                break;
            
            case MAX_NUM_FURGOS:
                init_max_num_furgos(init_seed);
                break;
            
            default:
                break;
        }
    }

    /* Initial State Algorithms */

    /* Initialize with random number of furgos with random routes (ensuring 3 different stations for each furgo) */
    private void init_random_num_furgos(int seed) {
        Random generator = new Random(seed);
        int nfurgos = generator.nextInt(max_furgos); // 0<=num_furgos<max_furgos
        int nest = map.size();
        moves = new int[nfurgos][5]; 
        for (int i = 0; i < nfurgos; ++i) {
            moves[i][DEPARTURE] = generator.nextInt(nest);

            do {
                moves[i][FIRST_DROPOFF] = generator.nextInt(nest);
            } while (moves[i][DEPARTURE] == moves[i][FIRST_DROPOFF]);

            do {
                moves[i][SECOND_DROPOFF] = generator.nextInt(nest);
            } while (moves[i][DEPARTURE] == moves[i][SECOND_DROPOFF] || moves[i][FIRST_DROPOFF] == moves[i][SECOND_DROPOFF]);

            int num_available_bikes = map.get(moves[i][DEPARTURE]).getNumBicicletasNoUsadas();
            if (num_available_bikes > 30) moves[i][BIKES_TAKEN] = generator.nextInt(30) + 1;
            else moves[i][BIKES_TAKEN] = generator.nextInt(num_available_bikes) + 1;
            
            moves[i][BIKES_DROPPED] = generator.nextInt(moves[i][BIKES_TAKEN]);
        }
    }

    private void init_max_num_furgos(int seed) {

    }

    /* Operators */
    public void add_furgo(int departure, int first_dropoff, int bikes_taken) {

    }

    public void remove_furgo(int furgo_id) {
        
    }

    public void change_departure(int furgo_id, int new_departure, int bikes_taken) {

    }

    public void change_first_dropoff(int furgo_id, int new_dropoff, int bikes_dropped) {

    }

    public void change_second_dropoff(int furgo_id, int new_dropoff, int bikes_dropped) {

    }

    /* Heuristic function */

    /* Auxiliary */
}
