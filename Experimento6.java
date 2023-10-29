import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;


public class Experimento6 {

    public static void main(String[] args) {

        PrintStream orgStream = null;
        ArrayList<PrintStream> output_files = new ArrayList<PrintStream>();
        Scanner in = new Scanner(System.in);
        try {

            orgStream = System.out;

            System.out.println("Number of map seeds?");
            int num_map_seeds = in.nextInt();
            System.out.println("Number of init seeds per map seed?");
            int num_init_reps = in.nextInt();
            System.out.println("Number of repetitions per init seed?");
            int repetitions = in.nextInt();
            System.out.println("Heuristic? 0: Only bikes profit, 1: bikes profit + transport cost, 2: bikes profit + dynamic transport cost");
            int h = in.nextInt();
            System.out.println("Output base name?");
            String filename_base = in.next();

            output_files.add(new PrintStream(new FileOutputStream(filename_base + "_rush_hour.txt", false)));
            output_files.add(new PrintStream(new FileOutputStream(filename_base + "_equilibrium.txt", false)));

            for (int n_map = 0; n_map<num_map_seeds; ++n_map) {
                int map_seed = (int)(Math.random()*Integer.MAX_VALUE);

                for (int n_rep = 0; n_rep<num_init_reps; ++n_rep) {
                    int init_seed = (int)(Math.random()*Integer.MAX_VALUE);
                
                    System.setOut(output_files.get(0));

                    String[] main_args_rush = {"--rformat-no-tags", "-r", Integer.toString(repetitions) , "-m", Integer.toString(map_seed), "-i", 
                                            Integer.toString(init_seed), "-rh", "-he", Integer.toString(h)};
                    if (n_map == 0 && n_rep==0) {
                        main_args_rush[0] = "--rformat";
                    }
                    try {
                        Main exec = new Main();
                        exec.execute(main_args_rush);
                    }
                    catch (Exception e) {
                        orgStream.println("Exception in Main");
                    }

                    System.setOut(output_files.get(1));
                    String[] main_args_equilibrium = {"--rformat-no-tags", "-r", Integer.toString(repetitions) , "-m", Integer.toString(map_seed), "-i", 
                                            Integer.toString(init_seed), "-he", Integer.toString(h)};
                    if (n_map == 0 && n_rep==0) {
                        main_args_equilibrium[0] = "--rformat";
                    }
                    try {
                        Main exec = new Main();
                        exec.execute(main_args_equilibrium);
                    }
                    catch (Exception e) {
                        orgStream.println("Exception in Main");
                    }
                }
            }
            


        }
        catch (FileNotFoundException fnfEx) {
            System.out.println("Error in IO redirection");
            fnfEx.printStackTrace();
        }
    }
}
