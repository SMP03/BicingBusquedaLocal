import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;


public class Experimento7 {

    public static void main(String[] args) {

        PrintStream orgStream = null;

        ArrayList<PrintStream> output_files = new ArrayList<PrintStream>();
        Scanner in = new Scanner(System.in);

        int steps = 50000;
        int stiter = 1;
        int k = 25;
        double lambda = 0.0001;

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
            System.out.println("0: Equilibrium, 1: RushHour");
            int demandType = in.nextInt();
            System.out.println("Number of maxFurgos to try: ");
            int nmaxfurgos = in.nextInt();
            /*System.out.println("Output base name?");
            String filename_base = in.next();*/

            String nameHeuristic = "Heu" + h;
            if (demandType == 0) {
                output_files.add(new PrintStream(new FileOutputStream("exp7_" + "HC_" + nameHeuristic + "_EQ" + "_.txt", false)));
            }
            else output_files.add(new PrintStream(new FileOutputStream("exp7_" + "HC_" + nameHeuristic + "_RH" + "_.txt", false)));

            for (int n_map = 0; n_map<num_map_seeds; ++n_map) {
                int map_seed = (int)(Math.random()*Integer.MAX_VALUE);

                for (int n_rep = 0; n_rep<num_init_reps; ++n_rep) {
                    int init_seed = (int)(Math.random()*Integer.MAX_VALUE);
                    
                    for (int nfurgos = 5; nfurgos <= nmaxfurgos; nfurgos += 5) {
                        System.setOut(output_files.get(0));
                        String[] main_args_rush = {"--rformat-no-tags","--exp7-no-tags", "-r", Integer.toString(repetitions), "-m", Integer.toString(map_seed), "-i", Integer.toString(init_seed), "-rh", "-he", Integer.toString(h), "-sa", Integer.toString(steps), Integer.toString(stiter), Integer.toString(k), Double.toString(lambda), "-num_furgos", Integer.toString(nfurgos)};
                        String[] main_args_equilibrium = {"--rformat-no-tags","--exp7-no-tags", "-r", Integer.toString(repetitions), "-m", Integer.toString(map_seed), "-i", Integer.toString(init_seed), "-he", Integer.toString(h), "-sa", Integer.toString(steps), Integer.toString(stiter), Integer.toString(k), Double.toString(lambda), "-num_furgos", Integer.toString(nfurgos)};
                        String[] main_args;

                        if (demandType == 0) main_args = main_args_equilibrium;
                        else main_args = main_args_rush;

                        if (n_map == 0 && n_rep==0 && nfurgos == 5) {
                            main_args[1] = "-exp7";
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


        }
        catch (FileNotFoundException fnfEx) {
            System.out.println("Error in IO redirection");
            fnfEx.printStackTrace();
        }
    }
}
