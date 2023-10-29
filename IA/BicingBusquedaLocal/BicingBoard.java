package IA.BicingBusquedaLocal;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.lang.Math;
import java.lang.reflect.Array;
import java.util.Comparator;

import IA.Bicing.Estaciones;
import IA.Bicing.Estacion; 
import IA.Connectat.ES;
import aima.search.framework.HeuristicFunction;

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
    public static String SWAP_DROPOFFS = "SDP";


    /* Constants to enable use of different initial states algorithms */
    public static final String[] INIT_NAMES = {"RANDOM_NUM_FURGOS", "MAX_NUM_FURGOS", "EMPTY_FURGOS", "BEST_K_ROUTES", "MIN_DIST"};
    public static final int RANDOM_NUM_FURGOS = 0;
    public static final int MAX_NUM_FURGOS = 1;
    public static final int EMPTY_FURGOS = 2;
    public static final int BEST_K_ROUTES = 3;
    public static final int MIN_DIST = 4;

    public static final int FIRST_CRITERION_HEURISTIC = 0;
    public static final int BOTH_CRITERION_HEURISTIC = 1;
    public static final int DYNAMIC_CRITERION_HEURISTIC = 2;
    public static int heuristic = 0;

    public static final double INIT_FACTOR_HEURISTICA = 0.0;
    public static final double VALOR_GRAN = 50000.f;

    private int [][] moves;
    private static Estaciones map;
    private static int max_furgos;
    private double factor_heuristica = 0.f;

    /**
     * Constructor of BicingBoard with furgo moves
     * @param moves contains the configuration of the state
     * @return BicingBoard state
     */
    public BicingBoard(int[][] moves) {
        this.moves = new int[moves.length][];
        for (int i = 0; i < moves.length; ++i)
            this.moves[i] = moves[i].clone();
    }

    /**
     * BicingBoard Copy Constructor
     * @param original contains the BicingBoard to copy
     * @return copy of the original BicingBoard state
     */
    public BicingBoard(BicingBoard original) {
        this.moves = new int[original.moves.length][];
        for (int i = 0; i < original.moves.length; ++i)
            this.moves[i] = original.moves[i].clone();
        
        factor_heuristica = Math.min(1, original.factor_heuristica + 0.1);
    }

    /**
     * BicingBoard Empty Constructor
     * @return BicingBoard with empty moves
     */
    public BicingBoard() {
        moves = new int[0][5];
    }

    /**
     * Constructor of BicingBoard with custom characteristics
     * @param num_furgos contains the number of furgos available for the scenario
     * @param n_stations contains the number of stations for the scenario
     * @param n_bicycles contains the total number of bycycles of the scenario
     * @param demand contains the type of demand (equilibrium:0, rushHour:1)
     * @param map_seed contains seed of the map of stations
     * @param init_strategy contains state initialization strategy (randomFurgos:0, maxFurgos:1, emptyFurgos:2, bestk_routes:3,min_distance routes:4)
     * @param init_seed contains initialization strategy seed
     * @param heurist contains the heuristic to be used (first_criterion:0, both_criterion:1, dynamic_criterion:2)
     * @return BicingBoard state with the specified parameters
     */
    public BicingBoard(int num_furgos, int n_stations, int n_bicycles, int demand, int map_seed, int init_strategy, int init_seed, int heurist) {
        map = new Estaciones(n_stations, n_bicycles, demand, map_seed);
        max_furgos = Math.min(n_stations, num_furgos);
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
            case BEST_K_ROUTES:
                moves = new int[max_furgos][5];
                best_k_routes_init();
                break;
            case MIN_DIST:
                moves = new int[max_furgos][5]; 
                minimum_distance_init(init_seed);
                break;
            default:
                break;
        }
        factor_heuristica = INIT_FACTOR_HEURISTICA;
        heuristic = heurist;
    }

    /**
     * Initialization Strategy with random number of furgos with random routes (ensuring 3 different stations for each furgo)
     * @param seed contains the seed of the random initialization
     */
    private void init_random_num_furgos(int seed) {
        Random generator = new Random(seed);
        int nfurgos = generator.nextInt(max_furgos)+1; // 1<num_furgos<=max_furgos
        moves = new int[nfurgos][5];
        random_init(generator.nextInt());
    }

    /**
     * Initialization strategy with maximum number of furgos with random routes (ensuring 3 different stations for each furgo) 
     * @param seed contains the seed of the random initialization of maxFurgos
     */
    private void init_max_num_furgos(int seed) {
        moves = new int[max_furgos][5]; 
        random_init(seed);
    }

    /**
     * Initialization Strategy with no furgos
     */
    private void empty_furgos() {
        moves = new int[0][5]; 
    }

    /* Operators */

    /**
     * Operator that adds a new furgo with departure station, bikes taken in departure, first dropoff station, bikes left in station is bikes taken, no second dropoff station
     * @param departure contains the departure station
     * @param first_dropoff contains the first dropoff station
     * @param bikes_taken contains the bikes taken in the departure station
     * @return a new BicingBoard state with the added furgo with its departure, first dropoff, bikes taken and all bikes taken are dropped on the first visited station 
     */
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

    /*
     * NOTE:Does not preserve the ids of the previous furgo (for faster implementation)
    */

    /**
     * Operator that removes furgo at a specific id
     * @param furgo_id contains the furgo to be removed
     * @return a new BicingBoard state with the removed furgo
     */
    public BicingBoard remove_furgo(int furgo_id) {
        BicingBoard new_board = new BicingBoard();
        new_board.moves = Arrays.copyOf(moves, get_n_furgos()-1); // Crea una copia de l'array truncant l'última posició
        if (furgo_id!=this.get_n_furgos()-1) {
            new_board.moves[furgo_id] = this.moves[this.get_n_furgos()-1]; // Mou l'últim element a la posició esborrada
        }
        return new_board;
    }

    /**
     * Operator that changes departure station for a specific furgo and the bikes taken at the new departure
     * @param furgo_id contains the furgo you want to change its departure
     * @param new_departure contains the new departure you want to assign to furgo with furgo_id
     * @param bikes_taken contains the bikes taken in the new departure station by furgo with furgo_id
     */
    public void change_departure(int furgo_id, int new_departure, int bikes_taken) {
        this.moves[furgo_id][DEPARTURE] = new_departure;
        this.moves[furgo_id][BIKES_TAKEN] = bikes_taken;
        this.moves[furgo_id][BIKES_DROPPED] = Integer.min(this.moves[furgo_id][BIKES_DROPPED], bikes_taken);
        remove_redundancy(furgo_id);
    }

    /**
     * Operator that changes first dropoff station for a specific furgo and the bikes dropped at the new first dropoff station
     * @param furgo_id contains the furgo you want to change its first dropoff station to
     * @param new_dropoff contains the new first dropoff of furgo with furgo_id
     * @param bikes_dropped contains the bikes dropped in the new first dropoff station by furgo with furgo_id
     */
    public void change_first_dropoff(int furgo_id, int new_dropoff, int bikes_dropped) {
        moves[furgo_id][FIRST_DROPOFF] = new_dropoff;
        moves[furgo_id][BIKES_DROPPED] = bikes_dropped;
        remove_redundancy(furgo_id);
    }

    /**
     * Operator that changes second dropoff station for a specific furgo and the bikes dropped at the new second dropoff station
     * @param furgo_id contains the furgo you want to change its second dropoff station to
     * @param new_dropoff contains the new second dropoff of furgo with furgo_id
     * @param bikes_dropped contains the bikes dropped in the new second dropoff station by furgo with furgo_id
     */
    public void change_second_dropoff(int furgo_id, int new_dropoff, int bikes_dropped) {
        moves[furgo_id][SECOND_DROPOFF] = new_dropoff;
        moves[furgo_id][BIKES_DROPPED] = bikes_dropped;
        remove_redundancy(furgo_id);
    }

    /**
     * Operator that swaps first and second dropoff stations of the same furgo
     * @param furgo_id contains the furgo you want to swap the first with the second dropoff station
     */
    public void swap_dropoffs(int furgo_id) {
        int aux = moves[furgo_id][SECOND_DROPOFF];
        moves[furgo_id][SECOND_DROPOFF] = moves[furgo_id][FIRST_DROPOFF];
        moves[furgo_id][FIRST_DROPOFF] = aux;
        moves[furgo_id][BIKES_DROPPED] = moves[furgo_id][BIKES_TAKEN] - moves[furgo_id][BIKES_DROPPED];
    }

    /**
     * Auxiliary function that eliminated redundancies for specific furgo. If there exists second dropoff station and no bikes are left, set second dropoff to nonexistent
     * and if there are 0 bikes left in first dropoff, swaps first with second dropoff and sets second dropoff to nonexistent
     * @param furgo_id contains the furgo for which you want to remove redundancies
     */
    private void remove_redundancy(int furgo_id) {
        if (get_second_dropoff(furgo_id) != -1) { // 2 stops
            if (get_bikes_second_dropoff(furgo_id) == 0) {
                moves[furgo_id][SECOND_DROPOFF] = -1;
            }
            else if (get_bikes_first_dropoff(furgo_id) == 0) {
                moves[furgo_id][FIRST_DROPOFF] = moves[furgo_id][SECOND_DROPOFF];
                moves[furgo_id][SECOND_DROPOFF] = -1;
            }
        }
    }

    /* Heuristic functions */
    /* First criterion heuristic:
     * -Optimize only the number of bikes (not taking into account the transport costs)
     * +1€ for each moved bike that counts to getting closer to the demand
     * -1€ for each moved bike that gets the station further away from the demand
     * More info: page 5
    */

    /**
     * First criterion heuristic, only takes into account benefit originated from bike benefits
     * @return benefits obtained only from bikes, assuming transport to be free
     */
    public double first_criterion_heuristic() {
        int[] balance = get_balance();
        return get_bike_income(balance);
    }

    /* Both criteria heuristic:
     * -Optimize the number of bikes and transport costs
     * +1€ for each moved bike that counts to getting closer to the demand
     * -1€ for each moved bike that gets the station further away from the demand
     * Loss of ((number_of_bikes + 9) div 10)€ for each kilometer of trasport done with a furgo
     * More info: page 5
    */

    /**
     * Both criterion heuristic, takes into account benefit originated from bike benefits and cost of transport
     * @return returns benefits obtained from bike profits - cost of transport
     */
    public double both_criteria_heuristic() {
        int[] balance = get_balance();
        double ganancias = get_bike_income(balance);
        ganancias += get_transport_cost(balance);
        return ganancias;
    }

    /**
     * Dynamic criterion heuristic, initially assumes transport cost to be free, then incrementally adds it to the heuristic
     * @return the value for the heuristic
     */
    public double dynamic_criterion_heuristic() {
        int[] balance = get_balance();
        double ganancias = get_bike_income(balance);
        ganancias += factor_heuristica*get_transport_cost(balance);
        ganancias += factor_heuristica*VALOR_GRAN;
        return ganancias;
    }


    /**
     * Calculates the balance of bikes in each station after the furgo moves (< 0 if more bikes are taken, > 0 if more bikes arrive, for each station)
     * @return Array of integers of size number of stations, representing the balance of bikes for each station after furgo moves
     */
    public int[] get_balance() {
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

    /**
     * Given the bike balance for each station, returns its associated benefit obtained from bikes left and picked
     * @param balance balance of bikes in each station after the furgo moves (< 0 if more bikes are taken, > 0 if more bikes arrive, for each station)
     * @return the benefit obtained from bikes left and picked
     */
    public double get_bike_income(int[] balance) {
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

    /**
     * Calculates the cost of transportation of bikes
     * @return the cost of transportation of bikes
     */
    public double get_transport_cost(int[] balance) {
        double ganancias = 0.0;
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

    
    /**
     * Calculates the total distance reached considering all furgos
     * @return the total distance covered 
     */
    public double get_total_dist() {
        double dist = 0.0;
        for (int i = 0; i < get_n_furgos(); ++i) {
            int departure = moves[i][DEPARTURE];
            int first_dropoff = moves[i][FIRST_DROPOFF];
            int second_dropoff = moves[i][SECOND_DROPOFF];

            Estacion departure_est = map.get(departure);
            Estacion first_dropoff_est = map.get(first_dropoff);
            dist += manhattan_dist(departure_est, first_dropoff_est);
            if (second_dropoff != -1) {
                Estacion second_dropoff_est = map.get(second_dropoff);
                dist += manhattan_dist(first_dropoff_est, second_dropoff_est);
            }
        }
        return dist;
    }

    /**
     * Calculates the manhattan distance between 2 stations
     * @param e1 station 1
     * @param e2 station 2
     * @return the manhattan distance between station e1 and station e2
     */
    private double manhattan_dist(Estacion e1, Estacion e2) {
        return (Math.abs(e1.getCoordX() - e2.getCoordX()) + Math.abs(e1.getCoordY() - e2.getCoordY()))/1000.0;
    }

    /* Goal test */

    public boolean is_goal(){
        // compute if board = solution
        return false;
    }

    /* Getters */

    /**
     * Returns the number of furgos used in the state
     * @return the number of furgos used in the state
     */
    public int get_n_furgos() {
        return moves.length;
    }

    /**
     * Returns the matrix of moves describing the state
     * @return the matrix of moves describing the state
     */
    public int[][] get_moves() {
        return moves;
    }

     /**
     * Returns the number of stations
     * @return the number of stations
     */
    public int get_num_estacions() {
        return map.size();
    }

     /**
     * Given a station returns the bikes not used by users in that hour
     * @param id_estacio station to get the bikes not used
     * @return the bikes not used by users in that hour for station with id_estacio
     */
    public int get_bicis_no_usades(int id_estacio) {
        return map.get(id_estacio).getNumBicicletasNoUsadas();
    }
    
    /**
     * Given a furgo returns the bikes taken from departure for that furgo
     * @param furgo_id the furgo from which to return the bikes taken from departure
     * @return the bikes taken from departure for that furgo
     */
    public int get_bikes_taken(int furgo_id) {
        return moves[furgo_id][BIKES_TAKEN];
    }

    /**
     * Given a furgo returns the bikes left in first dropoff station for that furgo
     * @param furgo_id the furgo from which to return the bikes left in first dropoff station
     * @return the bikes left in first dropoff station for furgo with furgo_id
     */
    public int get_bikes_first_dropoff(int furgo_id) {
        if (moves[furgo_id][SECOND_DROPOFF] == -1) return moves[furgo_id][BIKES_TAKEN];
        return moves[furgo_id][BIKES_DROPPED];
    }

    /**
     * Given a furgo returns the bikes left in second dropoff station for that furgo
     * @param furgo_id the furgo from which to return the bikes left in second dropoff station
     * @return the bikes left in second dropoff station for furgo with furgo_id
     */
    public int get_bikes_second_dropoff(int furgo_id) {
        return moves[furgo_id][BIKES_TAKEN] - moves[furgo_id][BIKES_DROPPED];
    }

    /**
     * Given a furgo returns the departure station for that furgo
     * @param furgo_id the furgo from which to return the departure station
     * @return the departure station for that furgo
     */
    public int get_departure(int furgo_id) {
        return moves[furgo_id][DEPARTURE];
    }

    /**
     * Given a furgo returns the first dropoff station for that furgo
     * @param furgo_id the furgo from which to return the first dropoff station
     * @return the first dropoff station for that furgo
     */
    public int get_first_dropoff(int furgo_id) {
        return moves[furgo_id][FIRST_DROPOFF];
    }

    /**
     * Given a furgo returns the second dropoff station for that furgo
     * @param furgo_id the furgo from which to return the second dropoff station
     * @return the second dropoff station for that furgo
     */
    public int get_second_dropoff(int furgo_id) {
        return moves[furgo_id][SECOND_DROPOFF];
    }
 
    /**
     * Gives the max number of furgos available for placement
     * @return the max number of furgos available for placement
     */
    public static int get_max_furgos() {
        return max_furgos;
    }

    /**
     * Prints the state configuration
     */
    public void print_state() {
        for (int i = 0; i < moves.length; i++) {
            int departure = get_departure(i);
            int first_dropoff = get_first_dropoff(i);
            int second_dropoff = get_second_dropoff(i);
            int bikes_taken = get_bikes_taken(i);
            int bikes_dropped = get_bikes_first_dropoff(i);
            if (second_dropoff != -1) {
                double dist1 = manhattan_dist(map.get(departure), map.get(first_dropoff));
                double dist2 = manhattan_dist(map.get(first_dropoff), map.get(second_dropoff));
                System.out.printf("(id:%d;tk/av:%d/%d)--[%2fkm]-->(id:%d;dp/dm:%d/%d)--[%2fkm]-->(id:%d;dp/dm:%d/%d) T:%2fkm%n",
                    departure, bikes_taken, map.get(departure).getNumBicicletasNoUsadas(), dist1,
                    first_dropoff, bikes_dropped, map.get(first_dropoff).getDemanda()-map.get(first_dropoff).getNumBicicletasNext(), dist2,
                    second_dropoff, get_bikes_second_dropoff(i), map.get(second_dropoff).getDemanda()-map.get(second_dropoff).getNumBicicletasNext(),
                    (dist1+dist2));
            }
            else {
                double dist1 = manhattan_dist(map.get(departure), map.get(first_dropoff));
                System.out.printf("(id:%d;tk/av:%d/%d)--[%2fkm]-->(id:%d;dp/dm:%d/%d) T:%2fkm%n",
                    departure, bikes_taken, map.get(departure).getNumBicicletasNoUsadas(), dist1,
                    first_dropoff, bikes_dropped, map.get(first_dropoff).getDemanda()-map.get(first_dropoff).getNumBicicletasNext(), dist1);
            }
        }
    }

    /* Auxiliary */


    /**
     * Checks if there is no furgo starting at departure
     * @param departure station id to check for furgos starting at this station id
     * @return true if there is no furgo starting at departure departure, false otherwise
     */
    public boolean is_free_departure(int departure) {
        for(int i = 0; i < moves.length; ++i) 
            if(moves[i][DEPARTURE] == departure) return false;
        

        return true;
    }

    /**
     * Initiates the routes of the F furgos choosing randomingly F stations as the furgos departure station,
     * assigning bikes_taken as the maximum amount avoiding demand penalizations and assigning the 
     * first_dropoff as the nearest station
     * @param seed seed for minimum distance initialization strategy
     */
    private void minimum_distance_init(int seed) {
        int n_stations = map.size();
        int n_furgos = moves.length;
        Random generator = new Random(seed);
        for(int furgo_id = 0; furgo_id < Math.min(n_furgos, n_stations); ++furgo_id) {
            
            int station_id;
            do {
                station_id = generator.nextInt(n_stations);
            }
            while(!is_free_departure(station_id));

            moves[furgo_id][DEPARTURE] = station_id;

            int id_first_dropoff = nearest_station(map.get(station_id));
            moves[furgo_id][FIRST_DROPOFF] = id_first_dropoff;

            int num_available_bikes = Math.min(30, map.get(moves[furgo_id][DEPARTURE]).getNumBicicletasNoUsadas());
            
            int bikes_taken = Math.max(0, num_available_bikes);
            if(num_available_bikes > 1) 
                bikes_taken = generator.nextInt(num_available_bikes) + 1;
            
            moves[furgo_id][BIKES_TAKEN] = bikes_taken;        
            moves[furgo_id][BIKES_DROPPED] = bikes_taken;
            moves[furgo_id][SECOND_DROPOFF] = -1;
        }
    }

    /**
     * Calculates nearest station from the one specified
     * @param e station to find nearest station from
     * @return The id of the station with minimum manhattan distance to e
     */
    private int nearest_station(Estacion e) {
        int station_min = -1;
        double min_dist = Double.MAX_VALUE; 
        for(int i = 0; i < map.size(); ++i) {
            Estacion est = map.get(i);
            if(est != e) {
                double dist = manhattan_dist(est, e);
                
                if(dist < min_dist) {
                    station_min = i;
                    min_dist = dist;
                }
            } 
        }
        return station_min;
    }

    /* Best k routes */
    /**
     * Initiates the routes of the F furgos choosing randomingly F stations as the furgos departure station,
     * assigning bikes_taken as the maximum amount avoiding demand penalizations and assigning the 
     * first_dropoff as the nearest station
     */
    private void best_k_routes_init() {

        int n_stations = map.size();
        int n_furgos = moves.length;

        PriorityQueue<Double[]> pq = best_k_routes(n_furgos);

        for(int furgo_id = 0; furgo_id < Math.min(n_furgos, n_stations); ++furgo_id) {

            Double[] station = pq.remove();
            
            moves[furgo_id][DEPARTURE] = station[1].intValue();
            moves[furgo_id][FIRST_DROPOFF] = station[2].intValue();
            moves[furgo_id][BIKES_TAKEN] = station[3].intValue();
            moves[furgo_id][BIKES_DROPPED] = station[3].intValue();
            moves[furgo_id][SECOND_DROPOFF] = -1;
        }
    }

    /**
     * Leaves the initial best k routes for the BicingBoard configuration in a priority queue (give most benefit)
     * @param k integer that represents the size of the priority queue (best k routes)
     * @return A priority queue with the best k routes (elements with (costRoute,station1,stationNearestFrom1,bikesTaken))
     */
    private PriorityQueue<Double[]> best_k_routes(int k) {
        PriorityQueue<Double[]> pq = new PriorityQueue<Double[]>(new DoubleArrayComparator());
        
        for(int i = 0; i < map.size(); ++i) {

            Estacion e0 = map.get(i);
            int near_stat_id = nearest_station(e0);
            Estacion near_stat = map.get(near_stat_id);

            //int bikes_taken = near_stat.getDemanda() - near_stat.getNumBicicletasNext();
            int bikes_taken = get_optimum_bikes(e0, near_stat);
            double cost_route = cost_one_dropoff(e0, near_stat, bikes_taken);

            boolean add_element = false;
            if(pq.size() < k) add_element = true;
            else {
                Double[] min_elem = pq.peek();
                if(min_elem[0] > cost_route) {
                    pq.remove();
                    add_element = true;
                }
            } 

            if(add_element) {
                Double[] elem = new Double[4];
                elem[0] = cost_route;
                elem[1] = (double)i;
                elem[2] = (double)near_stat_id;
                elem[3] = (double)bikes_taken;

                pq.add(elem);
            }

        }
        return pq;
    }

    /**
     * Calculates cost of one dropoff for departure station departure, dropoff statoin destiny and bikes taken from departures bikes_taken
     * @param departure 
     * @param destiny
     * @param bikes_taken
     * @return cost from departure to destiny taking bikes_taken bikes
     */
    private double cost_one_dropoff(Estacion departure, Estacion destiny, int bikes_taken) {
        double transport_cost = manhattan_dist(departure, destiny) * ((bikes_taken+9)/10);
        
        double loss_departure = 0;
        if(departure.getDemanda() >= departure.getNumBicicletasNext()) //arriben menys bicis de les que es necessiten
            loss_departure = -bikes_taken;
        else {
            loss_departure = Math.min(0, departure.getNumBicicletasNext() - departure.getDemanda() - bikes_taken);
        }

        double gain_dropoff = 0;
        if(destiny.getDemanda() <= destiny.getNumBicicletasNext()) //demanda ja coberta
            gain_dropoff = 0;
        else { // el que falta per arrivar a la demanda o totes les bicis que portem
            gain_dropoff = Math.min(bikes_taken, destiny.getDemanda() - destiny.getNumBicicletasNext());
        }

        
        return gain_dropoff - loss_departure - transport_cost;
    }

    /**
     * @param e station to find nearest station from
     * @param next_e
     * @return The id of the station with minimum manhattan distance to e
     */
    private int get_optimum_bikes(Estacion e, Estacion next_e) {
        int diff_demanda = next_e.getNumBicicletasNext() - next_e.getDemanda();
        if (diff_demanda > 0) {
            return Math.min(30, Math.min(diff_demanda, e.getNumBicicletasNoUsadas()));
        }
        return 0;
    }

    
    /* end */

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
                --i; // Regen
            }
        }
    }
}
