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

    public static final double INIT_FACTOR_HEURISTICA = 0.6;

    private int [][] moves;
    private static Estaciones map;
    private static int max_furgos;
    private double factor_heuristica = 0.f;

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
        
        factor_heuristica = Math.min(1, original.factor_heuristica + 0.1);
    }

    /* Empty constructor */
    public BicingBoard() {
        moves = new int[0][5];
    }

    public BicingBoard(int num_furgos, int n_stations, int n_bicycles, int demand, int map_seed, int init_strategy, int init_seed) {
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
        this.moves[furgo_id][BIKES_DROPPED] = Integer.min(this.moves[furgo_id][BIKES_DROPPED], bikes_taken);
        remove_redundancy(furgo_id);
    }

    public void change_first_dropoff(int furgo_id, int new_dropoff, int bikes_dropped) {
        moves[furgo_id][FIRST_DROPOFF] = new_dropoff;
        moves[furgo_id][BIKES_DROPPED] = bikes_dropped;
        remove_redundancy(furgo_id);
    }

    public void change_second_dropoff(int furgo_id, int new_dropoff, int bikes_dropped) {
        moves[furgo_id][SECOND_DROPOFF] = new_dropoff;
        moves[furgo_id][BIKES_DROPPED] = bikes_dropped;
        remove_redundancy(furgo_id);
    }

    public void swap_dropoffs(int furgo_id) {
        int aux = moves[furgo_id][SECOND_DROPOFF];
        moves[furgo_id][SECOND_DROPOFF] = moves[furgo_id][FIRST_DROPOFF];
        moves[furgo_id][FIRST_DROPOFF] = aux;
        moves[furgo_id][BIKES_DROPPED] = moves[furgo_id][BIKES_TAKEN] - moves[furgo_id][BIKES_DROPPED];
    }

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
    public double both_criteria_heuristic() {
        int[] balance = get_balance();
        double ganancias = get_bike_income(balance);
        ganancias += get_transport_cost(balance);
        return ganancias;
    }

    public double dynamic_criterion_heuristic() {
        int[] balance = get_balance();
        double ganancias = factor_heuristica*get_bike_income(balance);
        ganancias += get_transport_cost(balance);
        return ganancias;
    }

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

    private double manhattan_dist(Estacion e1, Estacion e2) {
        return (Math.abs(e1.getCoordX() - e2.getCoordX()) + Math.abs(e1.getCoordY() - e2.getCoordY()))/1000.0;
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

    public int get_bikes_first_dropoff(int furgo_id) {
        if (moves[furgo_id][SECOND_DROPOFF] == -1) return moves[furgo_id][BIKES_TAKEN];
        return moves[furgo_id][BIKES_DROPPED];
    }

    public int get_bikes_second_dropoff(int furgo_id) {
        return moves[furgo_id][BIKES_TAKEN] - moves[furgo_id][BIKES_DROPPED];
    }

    public int get_departure(int furgo_id) {
        return moves[furgo_id][DEPARTURE];
    }

    public int get_first_dropoff(int furgo_id) {
        return moves[furgo_id][FIRST_DROPOFF];
    }

    public int get_second_dropoff(int furgo_id) {
        return moves[furgo_id][SECOND_DROPOFF];
    }
 

    public static int get_max_furgos() {
        return max_furgos;
    }

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

    //Checks if there is no furgo starting at departure
    public boolean is_free_departure(int departure) {
        for(int i = 0; i < moves.length; ++i) 
            if(moves[i][DEPARTURE] == departure) return false;
        

        return true;
    }

    /**
     * Initiates the routes of the F furgos choosing randomingly F stations as the furgos departure station,
     * assigning bikes_taken as the maximum amount avoiding demand penalizations and assigning the 
     * first_dropoff as the nearest station
     * @param seed
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
     * @param e
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
     * @param
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
     * 
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
