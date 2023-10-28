import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;


public class Experimento1 {

    public static void main(String[] args) {
        ArrayList<String[]> set_operators;
        set_operators = new ArrayList<String[]>();
        set_operators.add(new String[]{"1","1","0","0","0","0"});
        set_operators.add(new String[]{"1","1","1","1","1","0"});
        set_operators.add(new String[]{"1","1","1","1","1","1"});

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

            for (int i = 0; i < set_operators.size(); ++i) {
                output_files.add(new PrintStream(new FileOutputStream(filename_base + "_set" + (i+1) + ".txt", false)));
            }
            for (int n_map = 0; n_map<num_map_seeds; ++n_map) {
                int map_seed = (int)(Math.random()*Integer.MAX_VALUE);
                for (int n_rep = 0; n_rep<num_reps; ++n_rep) {
                    int init_seed = (int)(Math.random()*Integer.MAX_VALUE);
                    for (int i = 0; i < set_operators.size(); ++i) {
                        System.setOut(output_files.get(i));

                        String[] ops = new String[6];
                        ops = set_operators.get(i);
                        String[] main_args = {"--rformat-no-tags", "-r", "1", "-m", Integer.toString(map_seed), "-i", Integer.toString(init_seed),
                            "--operators", ops[0], ops[1], ops[2], ops[3], ops[4], ops[5]};
                        if (n_map == 0 && n_rep==0) {
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
            


        }
        catch (FileNotFoundException fnfEx) {
            System.out.println("Error in IO redirection");
            fnfEx.printStackTrace();
        }
    }
}
