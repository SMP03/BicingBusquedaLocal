import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import IA.BicingBusquedaLocal.BicingBoard;


public class Experimento2 {

    public static void main(String[] args) {
        ArrayList<Integer> set_init = new ArrayList<Integer>();
        set_init.add(BicingBoard.RANDOM_NUM_FURGOS);
        set_init.add(BicingBoard.MAX_NUM_FURGOS);
        set_init.add(BicingBoard.EMPTY_FURGOS);
        set_init.add(BicingBoard.BEST_K_ROUTES);
        set_init.add(BicingBoard.MIN_DIST);

        PrintStream orgStream = null;

        ArrayList<PrintStream> output_files = new ArrayList<PrintStream>();
        Scanner in = new Scanner(System.in);
        try {
            orgStream = System.out;

            System.out.println("Number of map seeds?");
            int num_map_seeds = in.nextInt();
            System.out.println("Number of repetitions per seed?");
            int num_reps = in.nextInt();
            System.out.println("Output base name?");
            String filename_base = in.next();

            for (int i = 0; i < set_init.size(); ++i) {
                output_files.add(new PrintStream(new FileOutputStream(filename_base + "_" + BicingBoard.INIT_NAMES[set_init.get(i)] + ".txt", false)));
            }
            for (int n_map = 0; n_map<num_map_seeds; ++n_map) {
                int map_seed = (int)(Math.random()*Integer.MAX_VALUE);
                for (int i = 0; i < set_init.size(); ++i) {
                    System.setOut(output_files.get(i));

                    String[] main_args = {"--rformat-no-tags", "-r", Integer.toString(num_reps), "-m", Integer.toString(map_seed), "-he", "0",
                        "--init-strat", Integer.toString(set_init.get(i))};
                    if (n_map == 0) {
                        main_args[0] = "--rformat";
                    }
                    try {
                        Main exec = new Main();
                        exec.execute(main_args);
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
        finally {
            in.close();
        }
    }
}
