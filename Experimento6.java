import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;


public class Experimento6 {

    public static void main(String[] args) {

        PrintStream orgStream = null;
        Scanner in = new Scanner(System.in);
        try {

            orgStream = System.out;
            System.out.println("Number of repetitions?");
            int repetitions = in.nextInt();
            System.out.println("Output base name?");
            String filename_base = in.next();

            String[] main_args_rush = {"--rformat", "-sa", "default", "-r", Integer.toString(repetitions) , 
                                        "-rh", "-he", Integer.toString(2)};
            mainexec(orgStream, main_args_rush, filename_base, "rush_hour");

            String[] main_args_equilibrium = {"--rformat", "-sa", "default", "-r", Integer.toString(repetitions), 
                                                "-he", Integer.toString(2)};
            mainexec(orgStream, main_args_equilibrium, filename_base, "equilibrium");
        }
        finally {
            in.close();
        }
    }

    public static void mainexec(PrintStream orgStream, String[] main_args, String filename_base, String demand_type) {
        try {
            PrintStream out;
            out = new PrintStream(new FileOutputStream(filename_base + "_Experimento6_" + demand_type + ".txt", false));
            try {
                    Main exec = new Main(out);
                    exec.execute(main_args);
                }
                catch (Exception e) {
                    orgStream.println("Exception in Main");
                }
        }
        catch (FileNotFoundException fnfEx) {
            System.out.println("Error in IO redirection");
            fnfEx.printStackTrace();
        }
    }
}
