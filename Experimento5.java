import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import IA.BicingBusquedaLocal.BicingBoard;


public class Experimento5 {

    public static void main(String[] args) {

        PrintStream orgStream = null;

        Scanner in = new Scanner(System.in);
        try {
            orgStream = System.out;

            System.out.println("Number of repetitions?");
            int repetitions = in.nextInt();
            System.out.println("Output base name?");
            String filename_base = in.next();

            String[] main_args_hc_bikes = new String[]{"--rformat", "-he", Integer.toString(0), "-r", Integer.toString(repetitions)};
            mainexec(orgStream, main_args_hc_bikes, filename_base, "HC", "bikes_profit");

            String[] main_args_sa_bikes = new String[]{"--rformat", "-sa", "-he", Integer.toString(0), "-r", Integer.toString(repetitions)};
            mainexec(orgStream, main_args_sa_bikes, filename_base, "SA", "bikes_profit");

            String[] main_args_hc_dynamic = new String[]{"--rformat", "-he", Integer.toString(2), "-r", Integer.toString(repetitions)};
            mainexec(orgStream, main_args_hc_dynamic, filename_base, "HC", "dynamic");

            String[] main_args_sa_dynamic = new String[]{"--rformat", "-sa", "-he", Integer.toString(2), "-r", Integer.toString(repetitions)};
            mainexec(orgStream, main_args_sa_dynamic, filename_base, "SA", "dynamic");

        }
        finally {
            in.close();
        }

    }

    public static void mainexec(PrintStream orgStream, String[] main_args, String filename_base, String algorithm, String heuristica) {
        try {
            PrintStream out;
            out = new PrintStream(new FileOutputStream(filename_base + "_Experimento5_" + algorithm + "_" + heuristica + ".txt", false));
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
