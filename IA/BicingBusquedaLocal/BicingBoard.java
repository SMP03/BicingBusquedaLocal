package IA.BicingBusquedaLocal;

import java.util.Random;
import java.util.Arrays;
import java.lang.Math;

import IA.Bicing.Estaciones;
import IA.Bicing.Estacion; 

public class BicingBoard {
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

    /* String identifier for operators */

    public static String ADD_FURGO = "AF";
    public static String REMOVE_FURGO = "RF";
    public static String CHANGE_DEPARTURE = "CD";
    public static String CHANGE_FIRST_DROPOFF = "CFD";
    public static String CHANGE_SECOND_DROPOFF = "CSD";


    /* Constants to enable use of different initial states algorithms */
    public static final int RANDOM_NUM_FURGOS = 0;
    public static final int MAX_NUM_FURGOS = 1;
    public static final int EMPTY_FURGOS = 2;

    private int [][] moves;
    private static Estaciones map;
    private static int max_furgos;

    /* Constructor */
    public BicingBoard(int[][] moves) {
        this.moves = new int[moves.length][];
        for (int i = 0; i < moves.length; ++i)
            this.moves[i] = moves[i].clone();
    }

    /* Copy constructor (returns copy of the object) */
    public BicingBoard(BicingBoard original) {
        this.moves = new int[original.moves.length][];
        for (int i = 0; i < original.moves.length; ++i)
            this.moves[i] = original.moves[i].clone();
    }

    /* Empty constructor */
    public BicingBoard() {
        moves = new int[0][5];
    }

    public BicingBoard(int num_furgos, int n_stations, int n_bicycles, int demand, int map_seed, int init_strategy, int init_seed) {
        map = new Estaciones(n_stations, n_bicycles, demand, map_seed);
        max_furgos = num_furgos;
        switch (init_strategy) {
            case RANDOM_NUM_FURGOS:
                init_random_num_furgos(init_seed);
                break;
            
            case MAX_NUM_FURGOS:
                init_max_num_furgos(init_seed);
                break;
            
            case EMPTY_FURGOS:
                empty_furgos();
                break;

            default:
                break;
        }
    }

    /* Initial State Algorithms */

    /* Initialize with random number of furgos with random routes (ensuring 3 different stations for each furgo) */
    private void init_random_num_furgos(int seed) {
        Random generator = new Random(seed);
        int nfurgos = generator.nextInt(max_furgos)+1; // 1<num_furgos<=max_furgos
        moves = new int[nfurgos][5];
        random_init(generator.nextInt());
    }

    /* Initialize with maximum number of furgos with random routes (ensuring 3 different stations for each furgo) */
    private void init_max_num_furgos(int seed) {
        moves = new int[max_furgos][5]; 
        random_init(seed);
    }

    /* Initialize with no furgos */
    private void empty_furgos() {
        moves = new int[0][5]; 
    }

    /* Operators */

    /*Returns copy of object with a new furgo in the map*/ 
    public BicingBoard add_furgo(int departure, int first_dropoff, int bikes_taken) {
        BicingBoard new_board = new BicingBoard();
        new_board.moves = Arrays.copyOf(moves, get_n_furgos()+1); // Crea una copia de l'array amb una posició extra
        new_board.moves[new_board.get_n_furgos()-1] = new int[5];
        new_board.moves[new_board.get_n_furgos()-1][DEPARTURE] = departure;
        new_board.moves[new_board.get_n_furgos()-1][FIRST_DROPOFF] = first_dropoff;
        new_board.moves[new_board.get_n_furgos()-1][SECOND_DROPOFF] = -1;
        new_board.moves[new_board.get_n_furgos()-1][BIKES_TAKEN] = bikes_taken;
        new_board.moves[new_board.get_n_furgos()-1][BIKES_DROPPED] = bikes_taken;
        return new_board;
    }

    /*Removes furgo at a specific id
     * NOTE:Does not preserve the ids of the previous furgo (for faster implementation)
    */
    public BicingBoard remove_furgo(int furgo_id) {
        BicingBoard new_board = new BicingBoard();
        new_board.moves = Arrays.copyOf(moves, get_n_furgos()-1); // Crea una copia de l'array truncant l'última posició
        if (furgo_id!=this.get_n_furgos()-1) {
            new_board.moves[furgo_id] = this.moves[this.get_n_furgos()-1]; // Mou l'últim element a la posició esborrada
        }
        return new_board;
    }

    public void change_departure(int furgo_id, int new_departure, int bikes_taken) {
        this.moves[furgo_id][DEPARTURE] = new_departure;
        this.moves[furgo_id][BIKES_TAKEN] = bikes_taken;
    }

    public void change_first_dropoff(int furgo_id, int new_dropoff, int bikes_dropped) {
        moves[furgo_id][FIRST_DROPOFF] = new_dropoff;
        moves[furgo_id][BIKES_DROPPED] = bikes_dropped;
    }

    public void change_second_dropoff(int furgo_id, int new_dropoff, int bikes_dropped) {
        moves[furgo_id][SECOND_DROPOFF] = new_dropoff;
        moves[furgo_id][BIKES_DROPPED] = bikes_dropped;
    }

    /* Heuristic functions */
    /* First criterion heuristic:
     * -Optimize only the number of bikes (not taking into account the transport costs)
     * +1€ for each moved bike that counts to getting closer to the demand
     * -1€ for each moved bike that gets the station further away from the demand
     * More info: page 5
    */
    public double first_criterion_heuristic() {
        int[] balance = get_balance();

        double ganancias = 0.0;
        for (int i = 0; i < get_num_estacions(); ++i) {
            Estacion est = map.get(i);
            if (balance[i] > 0) {
                ganancias += Double.min(balance[i], est.getDemanda()-est.getNumBicicletasNext());
            }
            else if (balance[i] < 0) {
                if (est.getDemanda() >= est.getNumBicicletasNext()) {
                    ganancias += balance[i];
                }
                else {
                    ganancias += Double.min(0.0, balance[i] + est.getNumBicicletasNext()-est.getDemanda());
                }
                
            }
        }
        return ganancias;
    }

    /* Both criteria heuristic:
     * -Optimize the number of bikes and transport costs
     * +1€ for each moved bike that counts to getting closer to the demand
     * -1€ for each moved bike that gets the station further away from the demand
     * Loss of ((number_of_bikes + 9) div 10)€ for each kilometer of trasport done with a furgo
     * More info: page 5
    */
    public double both_criteria_heuristic() {
        int[] balance = get_balance();

        double ganancias = 0.0;
        for (int i = 0; i < get_num_estacions(); ++i) {
            Estacion est = map.get(i);
            if (balance[i] > 0) {
                ganancias += Double.min(balance[i], est.getDemanda()-est.getNumBicicletasNext());
            }
            else if (balance[i] < 0) {
                if (est.getDemanda() >= est.getNumBicicletasNext()) {
                    ganancias += balance[i];
                }
                else {
                    ganancias += Double.min(0.0, balance[i] + est.getNumBicicletasNext()-est.getDemanda());
                }
                
            }
        }

        for (int i = 0; i < get_n_furgos(); ++i) {
            int bikes_taken = moves[i][BIKES_TAKEN];
            int bikes_dropped = moves[i][BIKES_DROPPED];
            int departure = moves[i][DEPARTURE];
            int first_dropoff = moves[i][FIRST_DROPOFF];
            int second_dropoff = moves[i][SECOND_DROPOFF];

            Estacion departure_est = map.get(departure);
            Estacion first_dropoff_est = map.get(first_dropoff);
            ganancias -= manhattan_dist(departure_est, first_dropoff_est) * ((bikes_taken + 9) / 10);
            if (second_dropoff != -1) {
                Estacion second_dropoff_est = map.get(second_dropoff);
                ganancias -= manhattan_dist(first_dropoff_est, second_dropoff_est) * ((bikes_taken - bikes_dropped + 9) / 10);
            }
        }

        return ganancias;
    }

    private int[] get_balance() {
        int[] balance = new int[get_num_estacions()]; // Initialized to 0 (guaranteed by lang spec)
        for (int i = 0; i < get_n_furgos(); ++i) {
            int bikes_taken = moves[i][BIKES_TAKEN];
            int bikes_dropped = moves[i][BIKES_DROPPED];
            int departure = moves[i][DEPARTURE];
            int first_dropoff = moves[i][FIRST_DROPOFF];
            int second_dropoff = moves[i][SECOND_DROPOFF];

            balance[departure] -= bikes_taken;
            if (second_dropoff != -1) {
                balance[first_dropoff] += bikes_dropped;
                balance[second_dropoff] += bikes_taken - bikes_dropped;
            }
            else {
                balance[first_dropoff] += bikes_taken;
            }
        }

        return balance;
    }

    private int manhattan_dist(Estacion e1, Estacion e2) {
        return Math.abs(e1.getCoordX() - e2.getCoordX()) + Math.abs(e1.getCoordY() - e2.getCoordY());
    }

    /* Goal test */
    public boolean is_goal(){
        // compute if board = solution
        return false;
    }

    /* Getters */
    public int get_n_furgos() {
        return moves.length;
    }

    public int[][] get_moves() {
        return moves;
    }

    public int get_num_estacions() {
        return map.size();
    }

    public int get_bicis_no_usades(int id_estacio) {
        return map.get(id_estacio).getNumBicicletasNoUsadas();
    }
    
    public int get_bikes_taken(int furgo_id) {
        return moves[furgo_id][BIKES_TAKEN];
    }

    public int get_first_dropoff(int furgo_id) {
        return moves[furgo_id][FIRST_DROPOFF];
    }

    public int get_second_dropoff(int furgo_id) {
        return moves[furgo_id][SECOND_DROPOFF];
    }

    public int get_bikes_second_dropoff(int furgo_id) {
        return moves[furgo_id][BIKES_TAKEN] - moves[furgo_id][BIKES_DROPPED];
    }

    public int get_departure(int furgo_id) {
        return moves[furgo_id][DEPARTURE];
    }

    public static int get_max_furgos() {
        return max_furgos;
    }

    /* Auxiliary */

    //Checks if there is no furgo starting at departure
    public boolean is_free_departure(int departure) {
        for(int i = 0; i < moves.length; ++i) 
            if(moves[i][DEPARTURE] == departure) return false;
        

        return true;
    }

    /* Randomize routes (each path visits 3 DIFFERENT stations) */
    private void random_init(int seed) {
        Random generator = new Random(seed);
        int n_stations = map.size();
        for (int i = 0; i < moves.length; ++i) {
            moves[i][DEPARTURE] = generator.nextInt(n_stations);

            do {
                moves[i][FIRST_DROPOFF] = generator.nextInt(n_stations);
            } while (moves[i][DEPARTURE] == moves[i][FIRST_DROPOFF]);

            do {
                moves[i][SECOND_DROPOFF] = generator.nextInt(n_stations);
            } while (moves[i][DEPARTURE] == moves[i][SECOND_DROPOFF] || moves[i][FIRST_DROPOFF] == moves[i][SECOND_DROPOFF]);

            int num_available_bikes = map.get(moves[i][DEPARTURE]).getNumBicicletasNoUsadas();
            if (num_available_bikes > 30) {
                moves[i][BIKES_TAKEN] = generator.nextInt(30) + 1;
                moves[i][BIKES_DROPPED] = generator.nextInt(moves[i][BIKES_TAKEN]);
            }
            else if (num_available_bikes > 1) {
                moves[i][BIKES_TAKEN] = generator.nextInt(num_available_bikes) + 1;
                moves[i][BIKES_DROPPED] = generator.nextInt(moves[i][BIKES_TAKEN]);
            }
            else {
                moves[i][BIKES_TAKEN] = 0;
                moves[i][BIKES_DROPPED] = 0;
            }
        }
    }
}
